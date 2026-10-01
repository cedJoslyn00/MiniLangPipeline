.data
inputFile:   .asciiz "resultado.txt"
outputFile:  .asciiz "firma.txt"

prefixOut:   .asciiz "CHECKSUM="
newline:     .asciiz "\n"

msgOpen:     .asciiz "Error: no se pudo abrir resultado.txt\n"
msgRead:     .asciiz "Error: no se pudo leer resultado.txt\n"
msgFormat:   .asciiz "Error: no se encontro una linea MIPS|resultado|operaciones valida\n"
msgWrite:    .asciiz "Error: no se pudo crear firma.txt\n"

buffer:      .space 2056
numBuffer:   .space 16

.text
.globl main

main:
    li   $v0, 13
    la   $a0, inputFile
    li   $a1, 0
    li   $a2, 0
    syscall

    bltz $v0, error_open
    move $s0, $v0

    li   $v0, 14
    move $a0, $s0
    la   $a1, buffer
    li   $a2, 2047
    syscall

    bltz $v0, error_read
    move $s7, $v0

    la   $t0, buffer
    addu $t0, $t0, $s7
    sb   $zero, 0($t0)

    li   $v0, 16
    move $a0, $s0
    syscall

    la   $t0, buffer

find_marker:
    lb   $t1, 0($t0)
    beqz $t1, error_format

    li   $t2, 77             # 'M'
    bne  $t1, $t2, next_char

    lb   $t1, 1($t0)
    li   $t2, 73             # 'I'
    bne  $t1, $t2, next_char

    lb   $t1, 2($t0)
    li   $t2, 80             # 'P'
    bne  $t1, $t2, next_char

    lb   $t1, 3($t0)
    li   $t2, 83             # 'S'
    bne  $t1, $t2, next_char

    lb   $t1, 4($t0)
    li   $t2, 124            # '|'
    bne  $t1, $t2, next_char

    addiu $t0, $t0, 5
    j parse_result_sign

next_char:
    addiu $t0, $t0, 1
    j find_marker

parse_result_sign:
    li   $s1, 0
    li   $t5, 1
    li   $t6, 0

    lb   $t1, 0($t0)
    li   $t2, 45
    bne  $t1, $t2, parse_result_digits

    li   $t5, -1
    addiu $t0, $t0, 1

parse_result_digits:
    lb   $t1, 0($t0)

    li   $t2, 124
    beq  $t1, $t2, result_done

    li   $t2, 48
    slt  $t3, $t1, $t2
    bne  $t3, $zero, error_format

    li   $t2, 57
    slt  $t3, $t2, $t1
    bne  $t3, $zero, error_format

    addiu $t1, $t1, -48
    li   $t2, 10
    mul  $s1, $s1, $t2
    addu $s1, $s1, $t1

    addiu $t6, $t6, 1
    addiu $t0, $t0, 1
    j parse_result_digits

result_done:
    beqz $t6, error_format

    li   $t2, -1
    bne  $t5, $t2, result_positive
    subu $s1, $zero, $s1

result_positive:
    addiu $t0, $t0, 1

    li   $s2, 0
    li   $t6, 0

parse_ops:
    lb   $t1, 0($t0)

    beqz $t1, ops_done

    li   $t2, 10
    beq  $t1, $t2, ops_done

    li   $t2, 13
    beq  $t1, $t2, ops_done

    li   $t2, 48
    slt  $t3, $t1, $t2
    bne  $t3, $zero, error_format

    li   $t2, 57
    slt  $t3, $t2, $t1
    bne  $t3, $zero, error_format

    addiu $t1, $t1, -48
    li   $t2, 10
    mul  $s2, $s2, $t2
    addu $s2, $s2, $t1

    addiu $t6, $t6, 1
    addiu $t0, $t0, 1
    j parse_ops

ops_done:
    beqz $t6, error_format

    move $s3, $s1
    xor  $s3, $s3, $s2
    addiu $s3, $s3, 17

    li   $v0, 13
    la   $a0, outputFile
    li   $a1, 1
    li   $a2, 0
    syscall

    bltz $v0, error_write
    move $s4, $v0
    
    li   $v0, 15
    move $a0, $s4
    la   $a1, prefixOut
    li   $a2, 9
    syscall

    move $a0, $s3
    jal  int_to_ascii
    move $s5, $v0
    move $s6, $v1

    li   $v0, 15
    move $a0, $s4
    move $a1, $s5
    move $a2, $s6
    syscall

    li   $v0, 15
    move $a0, $s4
    la   $a1, newline
    li   $a2, 1
    syscall

    li   $v0, 16
    move $a0, $s4
    syscall

    li   $v0, 4
    la   $a0, prefixOut
    syscall

    li   $v0, 1
    move $a0, $s3
    syscall

    li   $v0, 4
    la   $a0, newline
    syscall

    li   $v0, 10
    syscall

int_to_ascii:
    la   $t0, numBuffer
    addiu $t0, $t0, 15

    move $t1, $a0
    li   $t2, 0
    li   $t7, 0

    bgez $t1, itoa_abs_ready
    li   $t7, 1
    subu $t1, $zero, $t1

itoa_abs_ready:
    bnez $t1, itoa_loop

    addiu $t0, $t0, -1
    li   $t3, 48
    sb   $t3, 0($t0)
    addiu $t2, $t2, 1
    j itoa_sign

itoa_loop:
    li   $t4, 10
    divu $t1, $t4
    mfhi $t3
    mflo $t1

    addiu $t3, $t3, 48
    addiu $t0, $t0, -1
    sb   $t3, 0($t0)
    addiu $t2, $t2, 1

    bnez $t1, itoa_loop

itoa_sign:
    beqz $t7, itoa_done

    addiu $t0, $t0, -1
    li   $t3, 45
    sb   $t3, 0($t0)
    addiu $t2, $t2, 1

itoa_done:
    move $v0, $t0
    move $v1, $t2
    jr   $ra
    
error_open:
    li   $v0, 4
    la   $a0, msgOpen
    syscall
    j exit_error

error_read:
    li   $v0, 4
    la   $a0, msgRead
    syscall
    j exit_error

error_format:
    li   $v0, 4
    la   $a0, msgFormat
    syscall
    j exit_error

error_write:
    li   $v0, 4
    la   $a0, msgWrite
    syscall

exit_error:
    li   $v0, 10
    syscall
