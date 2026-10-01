import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {

        String archivoEntrada = args.length > 0 ? args[0] : "programa.mini";
        String archivoIR = "../python/programa.ir";

        List<Instruccion> programaAST = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivoEntrada))) {

            String linea;
            int numLinea = 0;

            boolean tieneData = false;
            boolean tienePrint = false;
            boolean despuesDePrint = false;

            while ((linea = br.readLine()) != null) {

                numLinea++;
                linea = linea.trim();

                if (linea.isEmpty()) {
                    continue;
                }

                if (despuesDePrint) {
                    throw new IllegalArgumentException(
                            "Error Sintáctico en Línea " + numLinea +
                                    ": No puede haber instrucciones después de PRINT.");
                }

                String[] tokens = linea.split("\\s+");
                String comando = tokens[0].toUpperCase();
                if (!tieneData && !comando.equals("DATA")) {
                    throw new IllegalArgumentException(
                            "Error Sintáctico en Línea " + numLinea +
                                    ": El programa debe iniciar con la instrucción DATA.");
                }

                switch (comando) {

                    case "DATA":

                        if (tieneData) {
                            throw new IllegalArgumentException(
                                    "Error Sintáctico en Línea " + numLinea +
                                            ": Solo se permite una instrucción DATA.");
                        }

                        if (tokens.length < 2) {
                            throw new IllegalArgumentException(
                                    "Error Sintáctico en Línea " + numLinea +
                                            ": DATA requiere al menos un número.");
                        }

                        List<Integer> nums = new ArrayList<>();

                        for (int i = 1; i < tokens.length; i++) {

                            try {

                                int val = Integer.parseInt(tokens[i]);

                                if (val < 0) {
                                    throw new NumberFormatException();
                                }

                                nums.add(val);

                            } catch (NumberFormatException e) {

                                throw new IllegalArgumentException(
                                        "Error Léxico/Sintáctico en Línea " +
                                                numLinea +
                                                ": Valor numérico no válido o entero negativo '" +
                                                tokens[i] + "'.");
                            }
                        }

                        programaAST.add(new DataInstr(nums));
                        tieneData = true;

                        break;

                    case "FILTER":

                        if (tokens.length != 3) {

                            throw new IllegalArgumentException(
                                    "Error Sintáctico en Línea " + numLinea +
                                            ": FILTER requiere un comparador y un número.");
                        }

                        String comp = tokens[1];

                        if (!Arrays.asList(
                                ">",
                                "<",
                                ">=",
                                "<=",
                                "==").contains(comp)) {

                            throw new IllegalArgumentException(
                                    "Error Léxico en Línea " + numLinea +
                                            ": Comparador inválido '" + comp + "'.");
                        }

                        int valFilter;

                        try {

                            valFilter = Integer.parseInt(tokens[2]);

                            if (valFilter < 0) {
                                throw new NumberFormatException();
                            }

                        } catch (NumberFormatException e) {

                            throw new IllegalArgumentException(
                                    "Error Léxico/Sintáctico en Línea " +
                                            numLinea +
                                            ": Número inválido en FILTER '" +
                                            tokens[2] + "'.");
                        }

                        programaAST.add(
                                new FilterInstr(comp, valFilter));

                        break;

                    case "MAP":

                        if (tokens.length != 3) {

                            throw new IllegalArgumentException(
                                    "Error Sintáctico en Línea " + numLinea +
                                            ": MAP requiere un operador aritmético y un número.");
                        }

                        String op = tokens[1];

                        if (!Arrays.asList(
                                "+",
                                "-",
                                "*").contains(op)) {

                            throw new IllegalArgumentException(
                                    "Error Léxico en Línea " + numLinea +
                                            ": Operador aritmético inválido '" +
                                            op + "'.");
                        }

                        int valMap;

                        try {

                            valMap = Integer.parseInt(tokens[2]);

                            if (valMap < 0) {
                                throw new NumberFormatException();
                            }

                        } catch (NumberFormatException e) {

                            throw new IllegalArgumentException(
                                    "Error Léxico/Sintáctico en Línea " +
                                            numLinea +
                                            ": Número inválido en MAP '" +
                                            tokens[2] + "'.");
                        }

                        programaAST.add(
                                new MapInstr(op, valMap));

                        break;

                    case "REDUCE":

                        if (tokens.length != 2) {

                            throw new IllegalArgumentException(
                                    "Error Sintáctico en Línea " + numLinea +
                                            ": REDUCE requiere SUM, MAX o MIN.");
                        }

                        String redOp = tokens[1].toUpperCase();

                        if (!Arrays.asList(
                                "SUM",
                                "MAX",
                                "MIN").contains(redOp)) {

                            throw new IllegalArgumentException(
                                    "Error Léxico en Línea " + numLinea +
                                            ": Operación REDUCE inválida '" +
                                            redOp + "'.");
                        }

                        programaAST.add(
                                new ReduceInstr(redOp));

                        break;
                    case "PRINT":

                        if (tokens.length != 1) {

                            throw new IllegalArgumentException(
                                    "Error Sintáctico en Línea " +
                                            numLinea +
                                            ": PRINT no recibe parámetros.");
                        }

                        programaAST.add(
                                new PrintInstr());

                        tienePrint = true;
                        despuesDePrint = true;

                        break;
                    default:

                        throw new IllegalArgumentException(
                                "Error Léxico en Línea " +
                                        numLinea +
                                        ": Instrucción desconocida '" +
                                        comando + "'.");
                }
            }

            if (!tieneData) {

                throw new IllegalArgumentException(
                        "Error Sintáctico: Falta la instrucción DATA.");
            }

            if (!tienePrint) {

                throw new IllegalArgumentException(
                        "Error Sintáctico: El programa debe finalizar con PRINT.");
            }

            try (
                    PrintWriter pw = new PrintWriter(
                            new FileWriter(archivoIR))) {

                for (Instruccion inst : programaAST) {

                    pw.println(
                            inst.toIR());
                }
            }

            System.out.println(
                    "[ETAPA 1 - JAVA] programa.ir generado exitosamente.");

            System.out.println(
                    "[ETAPA 1 - JAVA] Archivo generado en: " +
                            archivoIR);

        } catch (Exception e) {

            System.err.println(
                    "[ETAPA 1 - ERROR] " +
                            e.getMessage());

            System.exit(1);
        }
    }
}