package models;

public class Ingreso extends CuentaCompartida{

    // Coloreamos parte del texto para hacerlo más visible
    private static final String VERDE = "\u001B[32m";
    private static final String RESET = "\u001B[0m";

    private float cantidad;
    private int id;

    public Ingreso(int tipo, int id, float cantidad){
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

    public void ingresar(){
        saldo = saldo + cantidad;
        System.out.printf(VERDE + "----------   [FINALIZADO INGRESO]" + RESET +
                " Operacion num: %d - Acaba de ingresar" +
                " %.2f€ en su cuenta. Nuevo Saldo: %.2f€   ----------%n", id, cantidad, saldo);
    }
}
