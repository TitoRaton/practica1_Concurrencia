package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GestionBanco {
    public void lanzar() throws InterruptedException {
        //  ExecutorService de 200 hilos
        int NUMERO_OPERACIONES = 200;
        ExecutorService executor = Executors.newFixedThreadPool(NUMERO_OPERACIONES);

        List<Future<Boolean>> futures = new ArrayList<>();

        // Enviamos 200 tareas al executor para que se ejecuten en paralelo
        List<Integer> tipos = calcularTipos();
        for (int i = 0; i < NUMERO_OPERACIONES; i++) {
            Thread.sleep(400);  // Dormimos el hilo 0.4 segundos.
            int tipo = tipos.get(i);
            CuentaCompartida tarea = new CuentaCompartida(tipo); // Creamos una nueva tarea con un ID único
            Future<Boolean> future = executor.submit(tarea); //Enviamos la tarea al ExecutorService
            futures.add(future); // Guardamos el Future en la lista
        }

        /* Obtenemos los resultados de las tareas, los metemos en una lista y
            contamos los errores que tiene.
        */
        int contador = 0;
        List<Boolean> errores = new ArrayList<>();

        for (Future<Boolean> future : futures) {
            boolean resultado;
            try {
                // Esto bloqueará hasta que cada tarea haya terminado
                resultado = future.get();
                if (!resultado){
                    errores.add(false);
                    contador++;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        if (!errores.isEmpty()){
            System.out.printf("Algunas operaciones no se han podido completar por error o falta de fondos." +
                    " --> Numero de operaciones fallidas: %d%n", contador);
        }else{
            System.out.println();
            System.out.println("Todas las operaciones realizadas con éxito.");
        }
        // Apagamos el ExecutorService.
        executor.shutdown();
    }

    // Función que crea la lista de tipos de hilo y los "baraja" para mezclarlos.
    public List<Integer> calcularTipos(){

        List<Integer> listaTipos = new ArrayList<>();

        for (int i=0; i < 20; i++){
            listaTipos.add(0);
        }
        for (int i=0; i < 20; i++){
            listaTipos.add(1);
        }
        for (int i=0; i < 160; i++){
            listaTipos.add(2);
        }

        Collections.shuffle(listaTipos);

        return listaTipos;
    }
}

