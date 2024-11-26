import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Hilos{
    public static void main(String[] args) {
        int CPUs = Runtime.getRuntime().availableProcessors();
        int nThreads = CPUs * 4;
        
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);

        for (int i = 0; i < 10; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Hilo en ejecución: " + Thread.currentThread().getName());
                }
            });
        }

        pool.shutdown();
    }
}