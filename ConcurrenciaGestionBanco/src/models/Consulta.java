package models;

public class Consulta extends CuentaCompartida{

    // Coloreamos parte del texto para hacerlo más visible
    private static final String AZUL = "\u001B[34m";
    private static final String RESET = "\u001B[0m";

    private int id;

    public Consulta(int tipo, int id){
        super(tipo);
        this.id = id;
    }

    public void consultar(){
        System.out.printf(AZUL + "----------   [FINALIZADO CONSULTA]" + RESET +
                " Operacion num: %d - Dispone de" +
                " %.2f € en la cuenta corriente.   ----------%n", id, saldo);
    }
}
