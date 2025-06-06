package models;

public class Retirada extends CuentaCompartida{

    // Coloreamos parte del texto para hacerlo más visible
    private static final String ROJO = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    private float cantidad;
    private int id;

    public Retirada(int tipo, int id, float cantidad)
    {
        super(tipo);
        this.id = id;
        this.cantidad = cantidad;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    public void retirar(){
        saldo = saldo - cantidad;
        if (saldo <= 0){
            saldo = 0;
        }
        System.out.printf(ROJO + "----------   [FINALIZADO RETIRADA]" + RESET +
                " Operacion num: %d - Acaba de retirar" +
                " %.2f € de su cuenta. Nuevo Saldo: %.2f€   ----------%n", id, cantidad, saldo);
    }
}
