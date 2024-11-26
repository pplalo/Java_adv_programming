import java.io.*;
import java.util.*;

public class CsvReader {

    public static void main(String[] args) {
        long start = System.currentTimeMillis(); // Medir el tiempo de ejecución
        initiateCsvCli();
        long end = System.currentTimeMillis();
        System.out.println("\u001B[32mProceso completado en " + (end - start) + " ms.\u001B[0m");
    }

    // Iniciar la CLI para procesar el archivo CSV
    public static void initiateCsvCli() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("\u001B[33m¡Bienvenido al Procesador de CSV!\u001B[0m");
            System.out.println("\u001B[33mIngresa la ruta donde se encuentra el archivo CSV:\u001B[0m");
            
            String filePath = scanner.nextLine();
            File file = new File(filePath);

            if (file.exists() && file.isFile() && filePath.endsWith(".csv")) {
                System.out.println("\u001B[32mArchivo CSV encontrado: " + file.getAbsolutePath() + "\u001B[0m");
                
                printCsvColumns(file);

                System.out.println("\u001B[33mIngresa las columnas que deseas analizar, separadas por comas (o presiona Enter para usar todas):\u001B[0m");
                String[] selectedColumns = selectColumns(scanner);

                int numCpus = Runtime.getRuntime().availableProcessors();
                int numSubfiles = numCpus * 4;

                dividirArchivo(file, numSubfiles);

                File subfilesDir = new File("subarchivos");
                File[] subfiles = subfilesDir.listFiles();

                if (subfiles != null) {
                    procesarConcurrentemente(subfiles, selectedColumns);

                    File analysisFile = new File("resultados/analisis_estadistico.txt");
                    generarAnalisisEstadistico(subfiles, analysisFile);
                } else {
                    System.out.println("\u001B[31mNo se encontraron subarchivos para procesar.\u001B[0m");
                }

            } else {
                System.out.println("\u001B[31mError: Archivo no encontrado o no es un archivo CSV válido.\u001B[0m");
            }
        } catch (Exception exc) {
            System.err.println("\u001B[31mError: " + exc.getMessage() + "\u001B[0m");
        }
    }

    // Mostrar las columnas disponibles en el CSV
    private static void printCsvColumns(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            if (header != null) {
                String[] columns = header.split(",");
                System.out.println("\u001B[34mColumnas disponibles:\u001B[0m");
                for (String column : columns) {
                    System.out.println("- " + column.trim());
                }
            } else {
                System.out.println("\u001B[31mEl archivo CSV está vacío.\u001B[0m");
            }
        } catch (IOException e) {
            System.err.println("\u001B[31mError al leer el archivo: " + e.getMessage() + "\u001B[0m");
        }
    }

    // Seleccionar columnas
    private static String[] selectColumns(Scanner scanner) {
        String selectedColumns = scanner.nextLine();
        if (selectedColumns.trim().isEmpty()) {
            System.out.println("\u001B[34mSe usarán todas las columnas disponibles.\u001B[0m");
            return new String[0]; // Usar todas las columnas
        }
        return selectedColumns.split(",");
    }

    // Dividir archivo en subarchivos
    private static void dividirArchivo(File file, int numSubfiles) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            if (header == null) {
                System.out.println("\u001B[31mEl archivo CSV está vacío.\u001B[0m");
                return;
            }

            // Crear directorio para subarchivos en el directorio actual
            File subfilesDir = new File(System.getProperty("user.dir"), "subarchivos");
            if (!subfilesDir.exists()) {
                subfilesDir.mkdir();
            }

            BufferedWriter[] writers = new BufferedWriter[numSubfiles];
            for (int i = 0; i < numSubfiles; i++) {
                File subfile = new File(subfilesDir, "subarchivo_" + (i + 1) + ".csv");
                writers[i] = new BufferedWriter(new FileWriter(subfile));
                writers[i].write(header + "\n");
            }

            String line;
            int index = 0;
            while ((line = reader.readLine()) != null) {
                writers[index].write(line + "\n");
                index = (index + 1) % numSubfiles;
            }

            for (BufferedWriter writer : writers) {
                writer.close();
            }
            System.out.println("\u001B[32mArchivo dividido en " + numSubfiles + " subarchivos.\u001B[0m");
        } catch (IOException e) {
            System.err.println("\u001B[31mError al dividir el archivo: " + e.getMessage() + "\u001B[0m");
        }
    }

    // Procesar subarchivos concurrentemente
    private static void procesarConcurrentemente(File[] subfiles, String[] selectedColumns) {
        List<Thread> workers = new ArrayList<>();
        File resultDir = new File("resultados");
        if (!resultDir.exists()) {
            resultDir.mkdir();
        }

        for (File subfile : subfiles) {
            Thread worker = new Thread(() -> procesarSubarchivo(subfile, resultDir, selectedColumns));
            workers.add(worker);
            worker.start();
        }

        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                System.err.println("\u001B[31mError esperando hilo: " + e.getMessage() + "\u001B[0m");
            }
        }
    }

    // Procesar un subarchivo
    private static void procesarSubarchivo(File subfile, File resultDir, String[] selectedColumns) {
        try (BufferedReader reader = new BufferedReader(new FileReader(subfile))) {
            String header = reader.readLine();
            File resultFile = new File(resultDir, subfile.getName() + "_procesado.csv");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile))) {
                writer.write(header + "\n");
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line + "\n");
                }
            }
            System.out.println("\u001B[32m" + subfile.getName() + " procesado correctamente.\u001B[0m");
        } catch (IOException e) {
            System.err.println("\u001B[31mError procesando subarchivo: " + e.getMessage() + "\u001B[0m");
        }
    }

    // Generar análisis estadístico
    private static void generarAnalisisEstadistico(File[] subfiles, File resultFile) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(resultFile))) {
            writer.write("Análisis Estadístico\n====================\n");
            for (File subfile : subfiles) {
                long lines = contarLineas(subfile);
                writer.write("Archivo: " + subfile.getName() + " - Registros: " + lines + "\n");
            }
            System.out.println("\u001B[32mAnálisis estadístico generado en " + resultFile.getAbsolutePath() + "\u001B[0m");
        } catch (IOException e) {
            System.err.println("\u001B[31mError generando análisis estadístico: " + e.getMessage() + "\u001B[0m");
        }
    }

    // Contar líneas en un archivo
    private static long contarLineas(File file) {
        long lines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.readLine() != null) {
                lines++;
            }
        } catch (IOException e) {
            System.err.println("\u001B[31mError contando líneas: " + e.getMessage() + "\u001B[0m");
        }
        return lines - 1; // Excluir encabezado
    }
}
