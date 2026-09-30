public static void main(String[] args) {
    String archivoEntrada = args.length > 0 ? args[0] : "programa.mini";
    String archivoIR = "programa.ir";

    List<Instruccion> programaAST = new ArrayList<>();

    try (BufferedReader br = new BufferedReader(new FileReader(archivoEntrada))) {
        String linea;
        int numLinea = 0;
        boolean tieneData = false;
        boolean tienePrint = false;

        while ((linea = br.readLine()) != null) {
            numLinea++;
            linea = linea.trim();
            if (linea.isEmpty()) continue;

            String[] tokens = linea.split("\\s+");
            String comando = tokens[0].toUpperCase();

            if (numLinea == 1 && !comando.equals("DATA")) {
                throw new IllegalArgumentException("Error Sintáctico en Línea " + numLinea + 
                    ": El programa debe iniciar con la instrucción 'DATA'.");
            }

            switch (comando) {
                case "DATA":
                    if (tokens.length < 2) {
                        throw new IllegalArgumentException("Error Sintáctico en Línea " + numLinea + 
                            ": DATA requiere al menos un número.");
                    }
                    List<Integer> nums = new ArrayList<>();
                    for (int i = 1; i < tokens.length; i++) {
                        try {
                            int val = Integer.parseInt(tokens[i]);
                            if (val < 0) throw new NumberFormatException();
                            nums.add(val);
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Error Léxico/Sintáctico en Línea " + numLinea + 
                                ": Valor numérico no válido u entero negativo '" + tokens[i] + "'.");
                        }
                    }
                    programaAST.add(new DataInstr(nums));
                    tieneData = true;
                    break;

                case "FILTER":
                    if (tokens.length != 3) {
                        throw new IllegalArgumentException("Error Sintáctico en Línea " + numLinea + 
                            ": FILTER requiere un comparador y un número.");
                    }
                    String comp = tokens[1];
                    if (!Arrays.asList(">", "<", ">=", "<=", "==").contains(comp)) {
                        throw new IllegalArgumentException("Error Léxico en Línea " + numLinea + 
                            ": Comparador inválido '" + comp + "'.");
                    }
                    int valFilter = Integer.parseInt(tokens[2]);
                    programaAST.add(new FilterInstr(comp, valFilter));
                    break;

                case "MAP":
                    if (tokens.length != 3) {
                        throw new IllegalArgumentException("Error Sintáctico en Línea " + numLinea + 
                            ": MAP requiere un operador aritmético y un número.");
                    }
                    String op = tokens[1];
                    if (!Arrays.asList("+", "-", "*").contains(op)) {
                        throw new IllegalArgumentException("Error Léxico en Línea " + numLinea + 
                            ": Operador aritmético inválido '" + op + "'.");
                    }
                    int valMap = Integer.parseInt(tokens[2]);
                    programaAST.add(new MapInstr(op, valMap));
                    break;

                case "REDUCE":
                    if (tokens.length != 2) {
                        throw new IllegalArgumentException("Error Sintáctico en Línea " + numLinea + 
                            ": REDUCE requiere una operación (SUM, MAX, MIN).");
                    }
                    String redOp = tokens[1].toUpperCase();
                    if (!Arrays.asList("SUM", "MAX", "MIN").contains(redOp)) {
                        throw new IllegalArgumentException("Error Léxico en Línea " + numLinea + 
                            ": Operación REDUCE inválida '" + redOp + "'.");
                    }
                    programaAST.add(new ReduceInstr(redOp));
                    break;

                case "PRINT":
                    programaAST.add(new PrintInstr());
                    tienePrint = true;
                    break;

                default:
                    throw new IllegalArgumentException("Error Léxico en Línea " + numLinea + 
                        ": Instrucción desconocida '" + comando + "'.");
            }
        }

        if (!tieneData) {
            throw new IllegalArgumentException("Error Sintáctico: Falta la instrucción DATA.");
        }
        if (!tienePrint) {
            throw new IllegalArgumentException("Error Sintáctico: El programa debe finalizar con PRINT.");
        }

        // Generación de programa.ir aplicando POLIMORFISMO
        try (PrintWriter pw = new PrintWriter(new FileWriter(archivoIR))) {
            for (Instruccion inst : programaAST) {
                pw.println(inst.toIR());
            }
        }

        System.out.println("[ETAPA 1 - JAVA] 'programa.ir' generado exitosamente.");

    } catch (Exception e) {
        System.err.println("[ETAPA 1 - ERROR] " + e.getMessage());
        System.exit(1);
    }
}