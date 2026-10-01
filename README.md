# MiniLang Pipeline

Proyecto práctico del curso **EIF400 – Paradigmas de Programación**.

Este proyecto implementa un pipeline poliglota usando **Java, Python y MIPS**.

## Flujo general

```text
programa.mini
      ↓
     JAVA
      ↓
 programa.ir
      ↓
   PYTHON
      ↓
resultado.txt
      ↓
     MIPS
      ↓
  firma.txt
```

## Tecnologías utilizadas

- Java
- Python 3
- MIPS
- MARS 4.5
- Windows Batch (`pipeline.bat`)

## Estructura del proyecto

```text
MiniLangPipeline/
│
├── pipeline.bat
├── Mars4_5.jar
├── README.md
│
├── src/
│   ├── java/
│   │   ├── Main.java
│   │   └── Instruccion.java
│   │
│   ├── python/
│   │   ├── ejecutor_minilang.py
│   │   └── programa.ir
│   │
│   └── mips/
│       ├── firma_minilang.asm
│       ├── resultado.txt
│       └── firma.txt
│
└── tests/
    ├── caso1_valido.mini
    ├── caso2_operador_invalido.mini
    ├── caso3_sin_data.mini
    ├── caso4_reduce_max.mini
    ├── caso5_lista_vacia.mini
    └── caso6_operaciones_dobles.mini
```

## Etapa 1 – Java

Java se encarga de:

- Leer el archivo `.mini`.
- Validar errores léxicos y sintácticos.
- Reportar el número de línea cuando ocurre un error.
- Representar las instrucciones mediante una jerarquía de clases.
- Utilizar herencia, sobrescritura y polimorfismo.
- Generar `programa.ir` solamente si el programa es válido.

Ejemplo de entrada:

```text
DATA 3 8 5 10 12
FILTER > 5
MAP * 2
REDUCE SUM
PRINT
```

Ejemplo de salida:

```text
DATA|3,8,5,10,12
FILTER|>|5
MAP|*|2
REDUCE|SUM
PRINT
```

## Etapa 2 – Python

Python se encarga de:

- Leer `programa.ir`.
- Ejecutar `FILTER` usando `filter()`.
- Ejecutar `MAP` usando `map()`.
- Ejecutar `REDUCE` usando `reduce()`.
- Generar una traza de las operaciones.
- Generar `resultado.txt`.

Ejemplo:

```text
FILTER > 5 => [8, 10, 12]
MAP * 2 => [16, 20, 24]
REDUCE SUM => 60
RESULT=60
OPS=3
MIPS|60|3
```

## Etapa 3 – MIPS

MIPS se encarga de:

- Leer `resultado.txt`.
- Extraer el resultado final.
- Extraer la cantidad de operaciones ejecutadas.
- Calcular una firma de verificación.
- Generar `firma.txt`.

La firma se calcula así:

```text
checksum = resultado
checksum = checksum XOR operaciones
checksum = checksum + 17
```

Ejemplo:

```text
resultado = 60
operaciones = 3

60 XOR 3 = 63
63 + 17 = 80
```

Salida:

```text
CHECKSUM=80
```

## Requisitos

Para ejecutar el proyecto se necesita:

- Java JDK instalado.
- Python 3 instalado.
- MARS 4.5.
- Windows.

## Ejecución automática

Desde la carpeta principal del proyecto:

```bat
pipeline.bat
```

Por defecto se ejecuta:

```text
tests\caso1_valido.mini
```

También se puede indicar un caso específico:

```bat
pipeline.bat tests\caso4_reduce_max.mini
```

## Casos de prueba

### Caso 1 – Programa válido

```text
RESULT=60
CHECKSUM=80
```

### Caso 2 – Operador inválido

El programa detecta un comparador inválido y detiene el pipeline.

```text
FILTER ? 5
```

### Caso 3 – Programa sin DATA

Java detecta que el programa no inicia con `DATA` y detiene el pipeline.

### Caso 4 – REDUCE MAX

```text
RESULT=99
CHECKSUM=114
```

### Caso 5 – Lista vacía

```text
FILTER > 100 => []
REDUCE SUM => 0
RESULT=0
CHECKSUM=19
```

### Caso 6 – Operaciones consecutivas

Se ejecutan varias operaciones `FILTER` y `MAP` consecutivas.

```text
RESULT=60
OPS=5
CHECKSUM=74
```

## Manejo de errores

Si una etapa falla, el pipeline se detiene y las siguientes etapas no se ejecutan.

Ejemplo:

```text
[ETAPA 1 - ERROR] Error Léxico en Línea 2: Comparador inválido '?'.
[ERROR] Fallo la etapa Java.

PIPELINE DETENIDO POR ERROR
```

## Archivos generados

Java genera:

```text
src/python/programa.ir
```

Python genera:

```text
src/mips/resultado.txt
```

MIPS genera:

```text
src/mips/firma.txt
```

## Objetivo académico

El proyecto demuestra la integración de distintos paradigmas de programación:

- **Java:** programación orientada a objetos.
- **Python:** programación funcional.
- **MIPS:** programación de bajo nivel.

Además, muestra cómo distintos lenguajes pueden colaborar mediante contratos de datos definidos entre etapas.

## Preguntas de reflexion tecnica

1. ¿Por qué Java resulta adecuado para la etapa de análisis y modelado de instrucciones?

Porque con Java se puede trabajar ordenado gracias a su modelo orientado a objetos.
Es ideal para revisar que las reglas de sintaxis se cumplan, detectar errores indicando
el numero de linea exacto y organizar las instrucciones en estructuras claras antes de continuar.

2. ¿Qué cambia conceptualmente entre describir una transformación con estilo imperativo y funcional?

Imperativo: Le indicas a la computadora paso a paso como modificar los datos usando variables
mutables, ciclos for o while e indices manuales.

Funcional: Le indicas a la computadora que resultado quieres aplicando funciones de
transformacion directa (filter, map, reduce), sin modificar la lista original y evitando efectos secundarios.

3. ¿Qué información se pierde o se conserva al convertir programa.mini a programa.ir?

Lo que se conserva: El contenido esencial para la ejecucion lo que seeria la lista
inicial de datos, la secuencia de operaciones con sus operadores.

Lo que se pierde: Los detalles visuales y contextuales que no afectan el resultado
espacios en blanco adicionales, saltos de linea y referencias a numeros de linea del codigo original.

4. ¿Por qué la representación intermedia puede compararse con una fase de un compilador?

Porque actua como un puente intermedio, separa la fase de validar sintaxis y detectar errores
de la fase de ejecucion de transformaciones. Esto permite cambiar de lenguaje en el futuro sin
reescribir todo el sistema.

5. ¿Qué ventajas y costos aparecen al integrar tres lenguajes en lugar de resolver todo con uno?

Entre las ventajas esta el que se puede provechar lo mejor de cada paradigma Java para validacion
y modelado estructurado, Python para procesamiento funcional conciso de listas, y MIPS para control
directo de registros y calculo numerico a bajo nivel.

Entre los costos esta que hay mayor complejidad al coordinar diferentes entornos de ejecucion
y una penalizacion en el rendimiento debido a la lectura y escritura constante de archivos en
disco para intercambiar informacion.