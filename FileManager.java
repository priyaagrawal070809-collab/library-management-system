package util;

import model.Book;

import java.io.*;
import java.util.List;

/**
 * Demonstrates Core Java File I/O.
 * Saves/loads the book catalog to a plain text file (human-readable),
 * using try-with-resources and proper exception handling.
 */
public class FileManager {

    private static final String DEFAULT_FILE = "books_report.txt";

    public static void exportBooksReport(List<Book> books) {
        exportBooksReport(books, DEFAULT_FILE);
    }

    public static void exportBooksReport(List<Book> books, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("===== LIBRARY BOOK CATALOG REPORT =====");
            writer.newLine();
            for (Book b : books) {
                writer.write(b.toString());
                writer.newLine();
            }
            writer.write("======= End of report (" + books.size() + " books) =======");
            System.out.println("Report exported to " + fileName);
        } catch (IOException e) {
            System.out.println("Failed to write report: " + e.getMessage());
        }
    }

    public static void readAndPrintFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}
