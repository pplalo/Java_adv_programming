import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
        
public class Hilos {
    public static void main(String[] args) {
        // Creamos una variable que nos permita crear el número de hilos que se ejecutarán
        int CPUs = Runtime.getRuntime().availableProcessors();
        int nThreads = CPUs * 4;

        ExecutorService pool = Executors.newFixedThreadPool(nThreads);

        // Lista de los subarhivos generados por el método dividirArchivo
        String subfilesDirPath = System.getProperty("user.dir") + File.separator + "subarchivos";
        List<File> csvFiles = getCsvFiles(subfilesDirPath);

        // Finalmente el pool de hilos procesa cada archivo CSV
        for (File csvFile : csvFiles) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    // try {
                        //TO DO
                        System.out.println("Procesando archivo: " + csvFile.getName());
                        System.out.println("Métodos de procesamiento de archivos CSV");
                    // } catch (IOException e) {
                        // System.err.println("Error" + e.getMessage());
                    // }
                }
            });
        }

            pool.shutdown();
        }

    // Obtener los archivos CSV de la ruta subarchivos
    private static List<File> getCsvFiles(String filesPath) {
        File path = new File(filesPath);

        File[] files = path.listFiles((dir, name) -> name.endsWith(".csv"));
        List<File> csvFiles = new ArrayList<>();

        try {
            if (files != null) {
                csvFiles.addAll(Arrays.asList(files));
            }
            return csvFiles;
        } catch (Exception e) {
            System.err.println("Error obteniendo los archivos de la ruta: " + filesPath);
            System.err.println(e.getMessage());
            return null;
        }
    }
}