package models;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;

public class CuentaCompartida  implements Callable<Boolean> {
    protected static float saldo = 0;

    protected float cantidad;
    protected int tipo;
    protected boolean operacionCorrecta = false;


    private static Random r = new Random();
    private static int[] contadores = {0, 0, 0};

    // Semáforos
    private static Semaphore semaphore_mutex = new Semaphore(1);
    private static Semaphore semaphore_asignar_id = new Semaphore(1);
    private static Semaphore semaphore_ingresar = new Semaphore(1);
    private static Semaphore semaphore_retirar = new Semaphore(1);
    private static Semaphore semaphore_consultar = new Semaphore(1);

    // variables que gestionan los semáforos
    private static int en_Espera_Ingresar=0, en_Espera_Retirar=0, en_Espera_Consultar=0;
    private static int ingresando=0, retirando=0, consultando=0;

    // Coloreamos parte del texto para hacerlo más visible
    private static final String VERDE = "\u001B[32m";
    private static final String AZUL = "\u001B[34m";
    private static final String ROJO = "\u001B[31m";
    private static final String RESET = "\u001B[0m";

    public CuentaCompartida(int tipo){
        this.tipo = tipo;
        cantidad = 500 + r.nextFloat(500);
    }

    @Override
    public Boolean call() throws Exception {
        try {
            // Asignación de ID´s y Mensaje de llegada al banco
            semaphore_asignar_id.acquire(1);
            contadores[tipo]++;
            int id = contadores[tipo];
            if (tipo == 0){
                // Los ingresos tienen prioridad absoluta y bloquean los demás
                semaphore_ingresar.acquire(1);
                semaphore_retirar.acquire(1);
                semaphore_consultar.acquire(1);
                System.out.println(VERDE + "-----------------------------------------------------------------------------------" + RESET);
                System.out.printf(VERDE + "----------   [INICIO] Operación ID %d [INGRESO]   ----------%n" + RESET, id);
            }
            else if (tipo == 1){
                // Las retiradas tienen prioridad absoluta y bloquean los demás
                semaphore_ingresar.acquire(1);
                semaphore_retirar.acquire(1);
                semaphore_consultar.acquire(1);
                System.out.println(ROJO + "-----------------------------------------------------------------------------------" + RESET);
                System.out.printf(ROJO + "----------   [INICIO] Operacion ID %d [RETIRADA]   ----------%n" + RESET, id);
            }
            else if (tipo == 2){
                semaphore_ingresar.acquire(1);
                semaphore_retirar.acquire(1);
                System.out.println(AZUL + "-----------------------------------------------------------------------------------" + RESET);
                System.out.printf(AZUL + "----------   [INICIO] Operacion ID %d [CONSULTA]   ----------%n" + RESET, id);
            }
            else {
                System.out.printf("Operación no aceptada.%n");
            }
            semaphore_asignar_id.release(1);

            switch (tipo) {
                case 0:
                    // Bloqueamos todas las operaciones
                    semaphore_mutex.acquire(1);
                    while(retirando>0 && consultando>0){
                        en_Espera_Ingresar++;
                        System.out.printf("[INGRESO] Operación ID %d: esperando acceso...%n", id);
                        semaphore_mutex.release(1);
                        semaphore_ingresar.acquire(1);
                        semaphore_mutex.acquire(1);
                        en_Espera_Ingresar--;
                    }
                    // Bloqueamos las consultas y las retiradas
                    ingresando++;
                    System.out.printf(VERDE + "----------   [PROCESANDO INGRESO]" + RESET +
                            " Operacion num: %d - Ingresando dinero ...%n", id);
                    Thread.sleep(500); // Tiempo simulado: 0.5 segundos.
                    Ingreso ingreso = new Ingreso(tipo, id, cantidad);
                    ingreso.ingresar();
                    ingresando--;
                    operacionCorrecta = true;
                    System.out.println(VERDE + "-----------------------------------------------------------------------------------" + RESET);
                    semaphore_mutex.release(1);
                    semaphore_ingresar.release(1);
                    semaphore_retirar.release(1);
                    semaphore_consultar.release(1);
                    break;
                case 1:
                    // Bloqueamos todas las operaciones
                    if (saldo > 0) {
                        semaphore_mutex.acquire(1);
                        while(ingresando>0 && consultando>0){
                            en_Espera_Retirar++;
                            System.out.printf("[INGRESO] Operación ID %d: esperando acceso...%n", id);
                            semaphore_mutex.release(1);
                            semaphore_retirar.acquire(1);
                            semaphore_mutex.acquire(1);
                            en_Espera_Retirar--;
                        }
                        // Hacemos la retirada y enviamos el segundo mensaje
                        retirando++;
                        System.out.printf(ROJO + "----------   [PROCESANDO RETIRADA]" + RESET +
                                " Operacion num: %d - Retirando dinero ...%n", id);
                        Thread.sleep(500);  // Dormimos el hilo 0.5 segundos.
                        Retirada retirada = new Retirada(tipo, id, cantidad);
                        if (saldo >= cantidad){
                            retirada.retirar();
                        }
                        else{
                            System.out.println(ROJO + "-----------------------------------------------------------------------------------" + RESET);
                            System.out.println(ROJO + "----------   [RETIRADA] Su saldo es menor a la cantidad que quiere retirar   ----------" + RESET);
                            System.out.println(ROJO + "-----------------------------------------------------------------------------------" + RESET);
                            operacionCorrecta = false;
                        }
                        retirando--;
                        operacionCorrecta = true;
                        System.out.println(ROJO + "-----------------------------------------------------------------------------------" + RESET);
                        semaphore_mutex.release(1);
                    }
                    else {
                        System.out.println(ROJO + "-----------------------------------------------------------------------------------" + RESET);
                        System.out.println(ROJO + "----------   [RETIRADA] El saldo es 0.0€ y no dispone de fondos   ----------" + RESET);
                        System.out.println(ROJO + "-----------------------------------------------------------------------------------" + RESET);
                    }
                    // Damos paso a la siguiente operación
                    semaphore_ingresar.release(1);
                    semaphore_retirar.release(1);
                    semaphore_consultar.release(1);
                    break;
                case 2:
                    semaphore_mutex.acquire(1);
                    while(ingresando>0 && retirando>0){
                        en_Espera_Consultar++;
                        System.out.printf("[RETIRADA] Operación ID %d: esperando acceso...%n", id);
                        semaphore_mutex.release(1);
                        semaphore_consultar.acquire(1);
                        semaphore_mutex.acquire(1);
                        en_Espera_Consultar--;
                    }
                    // Hacemos la consulta y enviamos el segundo mensaje
                    consultando++;
                    System.out.printf(AZUL + "----------   [PROCESANDO CONSULTA]" + RESET +
                            " Operacion num: %d - Consultando saldo ...%n", id);
                    Thread.sleep(2000);  // Dormimos el hilo 2 segundos.
                    Consulta consulta = new Consulta(tipo, id);
                    consulta.consultar();
                    consultando--;
                    if(en_Espera_Consultar>0){
                        semaphore_consultar.release(1);
                    }
                    operacionCorrecta = true;
                    System.out.println(AZUL + "-----------------------------------------------------------------------------------" + RESET);
                    semaphore_mutex.release(1);
                    // Damos paso a la siguiente operación
                    semaphore_ingresar.release(1);
                    semaphore_retirar.release(1);
                    break;
                default:
                    System.out.println("[ERROR] Operación no válida.%n");
                    operacionCorrecta = false;
                    break;
            }

        }catch (InterruptedException e) {
            e.printStackTrace();
            operacionCorrecta = false;
        }
        return operacionCorrecta;

    }
}
