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
            System.out.println("\u001B[33mWelcome!\u001B[0m");
            System.out.println("\u001B[33mEnter the path where the CSV file is located:\u001B[0m");
            
            String filePath = scanner.nextLine();
            File file = new File(filePath);

            if (file.exists() && file.isFile() && filePath.endsWith(".csv")) {
                System.out.println("\u001B[32m CSV file found: " + file.getAbsolutePath() + "\u001B[0m");
                
                printCsvColumns(file);
                
                List<String[]> data = readCSV(filePath);
                
                if (data.isEmpty()) {
                    System.out.println("Error reading CSV file");
                    return;
                }
                
                System.out.println("Available columns:");
                String[] headers = data.get(0);
                for (int i = 0; i < headers.length; i++) {
                    System.out.println(i + ": " + headers[i]);
                }
                
                System.out.print("Select index to be analyzed: ");
                int columnIndex = scanner.nextInt();
                
                scanner.nextLine();

        
                if (columnIndex < 0 || columnIndex >= headers.length) {
                    System.out.println("Invalid column index");
                    return;
                }
                List<Double> columnData = new ArrayList<>();

                for (int i = 1; i < data.size(); i++) {
                    try {
                        columnData.add(Double.valueOf(data.get(i)[columnIndex]));
                    } catch (NumberFormatException e) {
                        System.out.println("No numeric value was found in the row" + (i + 1) + ", omitiendo.");
                    }
                }

                int numCpus = Runtime.getRuntime().availableProcessors();
                int numSubfiles = numCpus * 4;

                dividirArchivo(file, numSubfiles);

                File subfilesDir = new File("subarchivos");
                File[] subfiles = subfilesDir.listFiles();

                if (subfiles != null) {
                    Hilos.process(columnData, numSubfiles);

                    // File analysisFile = new File("resultados/analisis_estadistico.txt");
                    // generarAnalisisEstadistico(subfiles, analysisFile);
                } else {
                    System.out.println("\u001B[31mNo subfiles were found to process.\u001B[0m");
                }

            } else {
                System.out.println("\u001B[31mError: File not found or not a valid CSV file.\u001B[0m");
            }
        } catch (Exception exc) {
            System.err.println("\u001B[31mError: " + exc.getMessage() + "\u001B[0m");
        }
    }

    // Display available columns in CSV
    private static void printCsvColumns(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            if (header != null) {
                String[] columns = header.split(",");
                System.out.println("\u001B[34mAvailable columns:\u001B[0m");
                for (int i = 0; i < columns.length; i++) {
                    System.out.println((i + 1) + ". " + columns[i].trim());
                }
            
            } else {
                System.out.println("\u001B[31mCSV file.\u001B[0m");
            }
        } catch (IOException e) {
            System.err.println("\u001B[31mError reading file: " + e.getMessage() + "\u001B[0m");
        }
    }

    private static List<String[]> readCSV(String fileName) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                data.add(values);
            }
        } catch (IOException e) {
            System.out.println("Error reading CSV file: " + e.getMessage());
        }
        return data;
    }

    // Dividir archivo en subarchivos
    public static void dividirArchivo(File file, int numSubfiles) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            if (header == null) {
                System.out.println("\u001B[31mCSV file is empty.\u001B[0m");
                return;
            }

            // Crear directorio para subarchivos en el directorio actual
            String subfilesDirPath = System.getProperty("user.dir") + File.separator + "subarchivos";
            File subfilesDir = new File(subfilesDirPath);
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

}
