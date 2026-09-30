import java.util.List;

public abstract class Instruccion {
    public abstract String toIR();
}

class DataInstr extends Instruccion {
    private List<Integer> numeros;

    public DataInstr(List<Integer> numeros) {
        this.numeros = numeros;
    }

    @Override
    public String toIR() {
        StringBuilder sb = new StringBuilder("DATA ");
        for (int i = 0; i < numeros.size(); i++) {
            sb.append(numeros.get(i));
            if (i < numeros.size() - 1) sb.append(",");
        }
        return sb.toString();
    }
}

class FilterInstr extends Instruccion {
    private String comparador;
    private int valor;

    public FilterInstr(String comparador, int valor) {
        this.comparador = comparador;
        this.valor = valor;
    }

    @Override
    public String toIR() {
        return "FILTER " + comparador + " " + valor;
    }
}

class MapInstr extends Instruccion {
    private String operador;
    private int valor;

    public MapInstr(String operador, int valor) {
        this.operador = operador;
        this.valor = valor;
    }

    @Override
    public String toIR() {
        return "MAP " + operador + " " + valor;
    }
}

class ReduceInstr extends Instruccion {
    private String operacion;

    public ReduceInstr(String operacion) {
        this.operacion = operacion;
    }

    @Override
    public String toIR() {
        return "REDUCE " + operacion;
    }
}

class PrintInstr extends Instruccion {
    @Override
    public String toIR() {
        return "PRINT";
    }
}