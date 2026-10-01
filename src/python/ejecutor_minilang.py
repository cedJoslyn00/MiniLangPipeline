from functools import reduce
from pathlib import Path
import operator
import sys

IR_FILE = Path("programa.ir")
OUT_FILE = Path("../mips/resultado.txt")

COMPARADORES = {
    ">": operator.gt,
    "<": operator.lt,
    ">=": operator.ge,
    "<=": operator.le,
    "==": operator.eq,
}

OPERACIONES_MAP = {
    "+": lambda x, n: x + n,
    "-": lambda x, n: x - n,
    "*": lambda x, n: x * n,
}


def leer_ir():
    if not IR_FILE.exists():
        raise FileNotFoundError(f"No existe {IR_FILE}")

    return [
        linea.strip()
        for linea in IR_FILE.read_text(encoding="utf-8").splitlines()
        if linea.strip()
    ]


def ejecutar():
    lineas = leer_ir()

    if not lineas or not lineas[0].startswith("DATA|"):
        raise ValueError("IR inválido: falta DATA al inicio")

    if lineas[-1] != "PRINT":
        raise ValueError("IR inválido: falta PRINT al final")

    datos_txt = lineas[0].split("|", 1)[1]
    estado = list(map(int, filter(None, datos_txt.split(","))))

    traza = []
    operaciones = 0
    reducido = False
    resultado = None

    for linea in lineas[1:-1]:
        partes = linea.split("|")
        tipo = partes[0]

        if reducido:
            raise ValueError(
                "IR inválido: REDUCE debe ser la última transformación antes de PRINT"
            )

        if tipo == "FILTER":
            if len(partes) != 3:
                raise ValueError(f"FILTER inválido: {linea}")

            _, comparador, valor_txt = partes
            if comparador not in COMPARADORES:
                raise ValueError(f"Comparador no soportado: {comparador}")

            valor = int(valor_txt)
            fn = COMPARADORES[comparador]

            estado = list(
                filter(lambda x, fn=fn, valor=valor: fn(x, valor), estado)
            )

            traza.append(f"FILTER {comparador} {valor} => {estado}")
            operaciones += 1

        elif tipo == "MAP":
            if len(partes) != 3:
                raise ValueError(f"MAP inválido: {linea}")

            _, operador, valor_txt = partes
            if operador not in OPERACIONES_MAP:
                raise ValueError(f"Operador MAP no soportado: {operador}")

            valor = int(valor_txt)
            fn = OPERACIONES_MAP[operador]

            estado = list(
                map(lambda x, fn=fn, valor=valor: fn(x, valor), estado)
            )

            traza.append(f"MAP {operador} {valor} => {estado}")
            operaciones += 1

        elif tipo == "REDUCE":
            if len(partes) != 2:
                raise ValueError(f"REDUCE inválido: {linea}")

            _, modo = partes
            operaciones += 1

            if modo == "SUM":
                resultado = reduce(lambda a, b: a + b, estado, 0)

            elif modo == "MAX":
                if not estado:
                    raise ValueError(
                        "REDUCE MAX no está definido para una lista vacía"
                    )
                resultado = reduce(max, estado)

            elif modo == "MIN":
                if not estado:
                    raise ValueError(
                        "REDUCE MIN no está definido para una lista vacía"
                    )
                resultado = reduce(min, estado)

            else:
                raise ValueError(f"REDUCE no soportado: {modo}")

            traza.append(f"REDUCE {modo} => {resultado}")
            reducido = True

        else:
            raise ValueError(f"Instrucción IR no soportada: {tipo}")

    if not reducido:
        raise ValueError(
            "Se requiere REDUCE para producir un resultado numérico para MIPS"
        )

    salida = "\n".join(
        traza
        + [
            f"RESULT={resultado}",
            f"OPS={operaciones}",
            f"MIPS|{resultado}|{operaciones}",
        ]
    ) + "\n"

    OUT_FILE.write_text(salida, encoding="utf-8")

    print(salida, end="")
    print(f"Generado: {OUT_FILE}")


if __name__ == "__main__":
    try:
        ejecutar()
    except Exception as e:
        print(f"ERROR: {e}", file=sys.stderr)
        sys.exit(1)
