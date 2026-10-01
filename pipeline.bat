@echo off
setlocal
title MiniLang Pipeline - Java + Python + MIPS

set "ROOT=%~dp0"
set "JAVA_DIR=%ROOT%src\java"
set "PYTHON_DIR=%ROOT%src\python"
set "MIPS_DIR=%ROOT%src\mips"
set "MARS_JAR=%ROOT%Mars4_5.jar"

if "%~1"=="" (
    set "TEST_FILE=%ROOT%tests\caso6_operaciones_dobles.mini"
) else (
    set "TEST_FILE=%~f1"
)

echo.
echo ============================================================
echo             MINILANG PIPELINE
echo ============================================================
echo Entrada: %TEST_FILE%
echo.

echo [1/3] Compilando y ejecutando JAVA...
pushd "%JAVA_DIR%"

javac Instruccion.java Main.java
if errorlevel 1 (
    echo.
    echo [ERROR] Fallo la compilacion de Java.
    popd
    goto :error
)

java Main "%TEST_FILE%"
if errorlevel 1 (
    echo.
    echo [ERROR] Fallo la etapa Java.
    popd
    goto :error
)

popd

if not exist "%PYTHON_DIR%\programa.ir" (
    echo.
    echo [ERROR] Java no genero src\python\programa.ir
    goto :error
)

echo [OK] Java termino correctamente.
echo.

echo [2/3] Ejecutando PYTHON...
pushd "%PYTHON_DIR%"

where python >nul 2>&1
if not errorlevel 1 (
    python ejecutor_minilang.py
) else (
    py ejecutor_minilang.py
)

if errorlevel 1 (
    echo.
    echo [ERROR] Fallo la etapa Python.
    popd
    goto :error
)

popd

if not exist "%MIPS_DIR%\resultado.txt" (
    echo.
    echo [ERROR] Python no genero src\mips\resultado.txt
    goto :error
)

echo [OK] Python termino correctamente.
echo.

echo [3/3] Ejecutando MIPS...

if not exist "%MARS_JAR%" (
    echo.
    echo [ERROR] No se encontro:
    echo %MARS_JAR%
    echo.
    echo Copie Mars4_5.jar en la raiz de MiniLangPipeline
    echo o cambie la variable MARS_JAR dentro de este .bat.
    goto :error
)

pushd "%MIPS_DIR%"

java -jar "%MARS_JAR%" nc firma_minilang.asm

if errorlevel 1 (
    echo.
    echo [ERROR] Fallo la etapa MIPS.
    popd
    goto :error
)

popd

if not exist "%MIPS_DIR%\firma.txt" (
    echo.
    echo [ERROR] MIPS no genero src\mips\firma.txt
    goto :error
)

echo [OK] MIPS termino correctamente.
echo.
echo ============================================================
echo              PIPELINE COMPLETADO
echo ============================================================
echo.
echo Archivos generados:
echo   IR:        %PYTHON_DIR%\programa.ir
echo   Resultado: %MIPS_DIR%\resultado.txt
echo   Firma:     %MIPS_DIR%\firma.txt
echo.
echo Firma generada:
type "%MIPS_DIR%\firma.txt"
echo.
echo ============================================================
pause
exit /b 0

:error
echo.
echo ============================================================
echo PIPELINE DETENIDO POR ERROR
echo ============================================================
pause
exit /b 1
