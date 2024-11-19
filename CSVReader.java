import java.io.File;
import java.util.Scanner;

public class CSVReader {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome!");
        System.out.println("Enter route path where it is stored CSV file:");

        String filePath = scanner.nextLine();
        File file = new File(filePath);

        if (file.exists() && file.isFile() && filePath.endsWith(".csv")) {
            System.out.println("Archivo CSV encontrado: " + file.getAbsolutePath());
            
        } else {
            System.out.println("Error: El archivo no existe, no es un archivo válido o no tiene extensión .csv.");
        }

        scanner.close();
    }
}
