import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class CsvReader {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome!");
            System.out.println("Enter the path where the CSV file is stored:");
            
            String filePath = scanner.nextLine();
            File file = new File(filePath);
            
            if (file.exists() && file.isFile() && filePath.endsWith(".csv")) {
                System.out.println("CSV file found: " + file.getAbsolutePath());
                System.out.println("\nReading the available columns in the file...\n");
                printCsvColumns(file);
            } else {
                System.out.println("Error: The file does not exist, is not a valid file, or does not have a .csv extension.");
            }

            System.out.println("/nEnter the columns to be analyzed separated by commas (e.g., column1, column2, column3):");

            String selectedColumns = scanner.nextLine();
            String[] columns = selectedColumns.split(",");

            System.out.println("You have selected the next columns:");
            for (String column : columns) {
                System.out.println(column.trim());
            }

        } catch (Exception exc) {
            System.err.println("Error: " + exc.getMessage());
        }
    }

    private static void printCsvColumns(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerFile = reader.readLine();

            if (headerFile != null) {
                String[] columns = headerFile.split(",");

                System.out.println("Available columns:");
                System.out.println("+----------------------+");
                for (String column : columns) {
                    System.out.printf("| %-25s |\n", column.trim().toUpperCase());
                }
                System.out.println("+----------------------+");
            } else {
                System.out.println("The CSV file is empty.");
            }
        } catch (IOException exc) {
            System.err.println("Error reading the file: " + exc.getMessage());
        }
    }
}
