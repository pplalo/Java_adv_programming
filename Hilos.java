import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Hilos {
    public static void process(List<Double> columnData, int nThreads) {
        // Creamos una variable que nos permita crear el número de hilos que se ejecutarán

        ExecutorService pool = Executors.newFixedThreadPool(nThreads);

        // Lista de los subarhivos generados por el método dividirArchivo
        String subfilesDirPath = System.getProperty("user.dir") + File.separator + "subarchivos";
        List<File> csvFiles = getCsvFiles(subfilesDirPath);

        // Finalmente el pool de hilos procesa cada archivo CSV
        for (File csvFile : csvFiles) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // List<Double> columnData = readColumnData(csvFile);
                        double min = findMin(columnData);
                        double max = findMax(columnData);
                        analyzeData(columnData, min, max);

                        System.out.println("Procesando archivo: " + csvFile.getName());
                        System.out.println("Métodos de procesamiento de archivos CSV");

                        // Escribir el resultado en un archivo CSV
                        String outputFileName = "output_" + csvFile.getName();
                        File outputFile = new File(subfilesDirPath, outputFileName);
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                            writer.write("Valor máximo,Valor mínimo,Rango de valores\n");
                            writer.write(max + "," + min + "," + (max - min) + "\n");
                        } catch (IOException e) {
                            System.err.println("Error escribiendo el archivo de salida: " + outputFileName);
                            System.err.println(e.getMessage());
                        }
                    } catch (Exception e) {
                        System.err.println("Error procesando archivo: " + csvFile.getName());
                        System.err.println(e.getMessage());
                    }
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

    private static double findMin(List<Double> columnData) {
        double max =  columnData.stream().min(Double::compare).orElse(Double.MIN_VALUE);
        return max;
    }

    private static double findMax(List<Double> columnData) {
        double min = columnData.stream().max(Double::compare).orElse(Double.MAX_VALUE);
        return min;
    }

    private static void analyzeData(List<Double> columnData, double min, double max) {
        try (Scanner scanner = new Scanner(System.in)) {
            double range = max - min;

            System.out.println("Valor máximo: " + max);
            System.out.println("Valor mínimo: " + min);
            System.out.println("Rango de valores: " + range);
            System.out.print("Ingrese el número de intervalos: ");

            int numIntervals = scanner.nextInt();

            double intervalLength = getIntervals(range, numIntervals);

            System.out.println("Intervalos:");

            double start = min;

            List<int[]> intervals = setIntervals(start, numIntervals, intervalLength);

            System.out.println("Frecuencia en cada intervalo:");
            
            //     int[] frequencies = new int[numIntervals];
            //     for (double value : columnData) {
            //         int intervalIndex = (int) ((value - min) / intervalLength);
            //         if (intervalIndex == numIntervals) {
            //             intervalIndex--; // Ajuste para el valor máximo
            //         }
            //         frequencies[intervalIndex]++;
            //     }
            //     for (int i = 0; i < numIntervals; i++) {
            //         System.out.println("Intervalo " + (i + 1) + ": " + frequencies[i] + " valores");
            //     }
            //     int mayor = frequencies[0];
            //     int indicemax=0;
            //     for (int i=1;i<frequencies.length;i++){
            //         if (frequencies[i]>mayor){
            //             mayor= frequencies[i];
            //             indicemax=i;
            //         }
            //     }
            //         System.out.println("El intervalo "+(indicemax+1)+" tiene la frecuencia mas grande");
        }
    }
    
    private static double getIntervals(double range, int numIntervals){
        double intervalLength = range / numIntervals;
        return intervalLength;
    }
    
    private static List<int[]> setIntervals(double start, int numIntervals, double intervalLength){
        List<int[]> intervals = new ArrayList<>();
        double end;
        for (int i = 0; i < numIntervals; i++) {
            end = start + intervalLength;
            System.out.println("Intervalo " + (i + 1) + ": [" + start + ", " + end + ")");
            intervals.add(new int[]{(int) start, (int) end});
            start = end;
        }
        return intervals;
    }

    private static List<Double> readColumnData(File csvFile) throws IOException {
        List<Double> columnData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; 
                }
                String[] values = line.split(",");
                for (String value : values) {
                    columnData.add(Double.parseDouble(value));
                }
            }
        }
        return columnData;
    }
}