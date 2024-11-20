//home/bongi/Descargas/US_Accidents_March23.csv
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class CsvReader {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("\u001B[33mWelcome!\u001B[0m");
            System.out.println("\u001B[33mEnter the path where the CSV file is stored:\u001B[0m");
            
            String filePath = scanner.nextLine();
            File file = new File(filePath);
            
            if (file.exists() && file.isFile() && filePath.endsWith(".csv")) {
                System.out.println("\u001B[32mCSV file found: " + file.getAbsolutePath() + "\u001B[0m");
                System.out.println("\u001B[34m\nReading the available columns in the file...\n\u001B[0m");
                printCsvColumns(file);
            } else {
                System.out.println("\u001B[31mError: The file does not exist, is not a valid file, or does not have a .csv extension.\u001B[0m");
                CsvReader.main(args);
            }

            System.out.println("\u001B[33m\nEnter the columns to be analyzed separated by commas (e.g., column1, column2, column3):\u001B[0m");
            System.out.println("\u001B[33mPress Enter to use the default columns.\u001B[0m");
            
            String[] defaultColumns = {
                "ID", "Source", "Severity", "Start_Time", "End_time", "Start_Lng", "Start_Lat", 
                "Weather_Timestamp", "Temperature", "Wind_Chill", "Visibility", "Wind_Direction", 
                "Wind_Speed", "Precipitation", "Crossing", "Junction", "Traffic_Signal", 
                "Sunrise_Sunset", "Cicil_Twilight", "Nautical_Twilight", "Astronomical_Twilight"
            };

            System.out.println("\u001B[34mDefault columns are:\u001B[0m");

            for (String column : defaultColumns) {
                System.out.println(column.trim());
            }

            String selectedColumns = scanner.nextLine();

            if (selectedColumns.trim().isEmpty()) {
                selectedColumns = "ID, Source, Severity, Start_Time, End_time, Start_Lng, Start_Lat, Weather_Timestamp, Temperature, Wind_Chill, Visibility, Wind_Direction, Wind_Speed, Precipitation, Crossing, Junction, Traffic_Signal, Sunrise Sunset, Cicil_Twilight, Nautical_Twilight, Astronomical_Twilight";
            }
            String[] columns = selectedColumns.split(",");

            System.out.println("\u001B[32mYou have selected the next columns:\u001B[0m");

            for (String column : columns) {
                System.out.println(column.trim());
            }
            

        } catch (Exception exc) {
            System.err.println("\u001B[31mError: " + exc.getMessage() + "\u001B[0m");
        }
    }

    private static void printCsvColumns(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String headerFile = reader.readLine();

            if (headerFile != null) {
                String[] columns = headerFile.split(",");

                System.out.println("\u001B[34mAvailable columns:\u001B[0m");
                System.out.println("+----------------------+");
                for (String column : columns) {
                    System.out.printf("| %-25s |\n", column.trim());
                }
                System.out.println("+----------------------+");
            } else {
                System.out.println("\u001B[31mThe CSV file is empty.\u001B[0m");
            }
        } catch (IOException exc) {
            System.err.println("\u001B[31mError reading the file: " + exc.getMessage() + "\u001B[0m");
        }
    }

}
