import exception.BookNotAvailableException;
import exception.BookNotFoundException;
import exception.BorrowLimitExceededException;
import exception.MemberNotFoundException;
import model.Book;
import model.Faculty;
import model.Member;
import model.Student;
import service.FineRecord;
import service.Library;
import util.ConsoleChart;
import util.FileManager;
import util.SwingChart;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Entry point. Console menu-driven demo of the Library Management System.
 * Run this class (it has main) from VS Code / javac+java.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Library library = new Library();

    public static void main(String[] args) {
        seedSampleData();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        listAllBooks();
                        break;
                    case "2":
                        listAllMembers();
                        break;
                    case "3":
                        addNewBook();
                        break;
                    case "4":
                        addNewMember();
                        break;
                    case "5":
                        issueBook();
                        break;
                    case "6":
                        returnBook();
                        break;
                    case "7":
                        reserveBook();
                        break;
                    case "8":
                        searchBooks();
                        break;
                    case "9":
                        library.printRecentTransactions(10);
                        break;
                    case "10":
                        exportReport();
                        break;
                    case "11":
                        showWeeklyIssuesChart();
                        break;
                    case "12":
                        showFineReportChart();
                        break;
                    case "0":
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid option. Try again.");
                }
            } catch (BookNotFoundException | MemberNotFoundException
                    | BookNotAvailableException | BorrowLimitExceededException e) {
                // Custom checked exceptions handled gracefully -> Exception Handling
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
            System.out.println();
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("========== LIBRARY MANAGEMENT SYSTEM ==========");
        System.out.println("1.  View all books (sorted by title)");
        System.out.println("2.  View all members (sorted by name)");
        System.out.println("3.  Add new book");
        System.out.println("4.  Add new member");
        System.out.println("5.  Issue a book");
        System.out.println("6.  Return a book");
        System.out.println("7.  Reserve a book (when unavailable)");
        System.out.println("8.  Search books (title / author)");
        System.out.println("9.  View recent transactions");
        System.out.println("10. Export book report to file");
        System.out.println("11. Weekly issue chart (who issued books this week)");
        System.out.println("12. Fine charges report + chart");
        System.out.println("0.  Exit");
        System.out.print("Choose an option: ");
    }

    private static void listAllBooks() {
        List<Book> books = library.getAllBooksSorted();
        if (books.isEmpty()) {
            System.out.println("No books in catalog.");
        }
        for (Book b : books) {
            System.out.println(b);
        }
    }

    private static void listAllMembers() {
        List<Member> memberList = library.getAllMembersSorted();
        if (memberList.isEmpty()) {
            System.out.println("No members registered.");
        }
        // Polymorphism in action: each Member (Student/Faculty) prints its own details
        for (Member m : memberList) {
            System.out.println(m.displayDetails());
        }
    }

    private static void addNewBook() {
        System.out.print("Book ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();

        library.addBook(new Book(id, title, author, isbn));
        System.out.println("Book added successfully.");
    }

    private static void addNewMember() {
        System.out.print("Member type (1=Student, 2=Faculty): ");
        String type = scanner.nextLine().trim();
        System.out.print("Person ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Membership ID: ");
        String membershipId = scanner.nextLine().trim();

        if (type.equals("2")) {
            System.out.print("Department: ");
            String dept = scanner.nextLine().trim();
            library.addMember(new Faculty(id, name, email, membershipId, dept));
        } else {
            System.out.print("Course: ");
            String course = scanner.nextLine().trim();
            library.addMember(new Student(id, name, email, membershipId, course));
        }
        System.out.println("Member registered successfully.");
    }

    private static void issueBook()
            throws BookNotFoundException, MemberNotFoundException,
            BookNotAvailableException, BorrowLimitExceededException {
        System.out.print("Book ID to issue: ");
        String bookId = scanner.nextLine().trim();
        System.out.print("Membership ID: ");
        String memberId = scanner.nextLine().trim();

        library.issueBook(bookId, memberId);
        System.out.println("Book issued successfully.");
    }

    private static void returnBook() throws BookNotFoundException, MemberNotFoundException {
        System.out.print("Book ID to return: ");
        String bookId = scanner.nextLine().trim();
        System.out.print("Membership ID: ");
        String memberId = scanner.nextLine().trim();

        double fine = library.returnBook(bookId, memberId);
        if (fine == 0.0) {
            System.out.println("Book returned successfully (on time, no fine).");
        } else {
            System.out.println("Book returned successfully.");
        }
    }

    private static void reserveBook() throws BookNotFoundException, MemberNotFoundException {
        System.out.print("Book ID to reserve: ");
        String bookId = scanner.nextLine().trim();
        System.out.print("Membership ID: ");
        String memberId = scanner.nextLine().trim();

        Member member = library.findMemberById(memberId);
        library.reserveBook(bookId, member);
        System.out.println("Reservation placed in queue for " + member.getName());
    }

    private static void searchBooks() {
        System.out.print("Search by (1=Title, 2=Author): ");
        String mode = scanner.nextLine().trim();
        System.out.print("Keyword: ");
        String keyword = scanner.nextLine().trim();

        List<Book> results = mode.equals("2")
                ? library.searchByAuthor(keyword)
                : library.searchByTitle(keyword);

        if (results.isEmpty()) {
            System.out.println("No matching books found.");
        } else {
            for (Book b : results) {
                System.out.println(b);
            }
        }
    }

    private static void exportReport() {
        FileManager.exportBooksReport(library.getAllBooksSorted());
    }

    /** Shows who issued books in the last 7 days, as both an ASCII chart and a Swing graph. */
    private static void showWeeklyIssuesChart() {
        Map<String, Integer> weeklyIssues = library.getWeeklyIssuesByMember();
        if (weeklyIssues.isEmpty()) {
            System.out.println("No books have been issued in the last 7 days.");
            return;
        }
        System.out.println("Members who issued books this week:");
        for (Map.Entry<String, Integer> e : weeklyIssues.entrySet()) {
            System.out.println("  " + e.getKey() + " -> " + e.getValue() + " book(s)");
        }
        ConsoleChart.printBarChart("Books Issued This Week (by member)", weeklyIssues, "book(s)");
        SwingChart.showBarChart("Books Issued This Week", weeklyIssues, "Books issued");
    }

    /** Lists members with outstanding fines and charts the amounts. */
    private static void showFineReportChart() {
        List<FineRecord> fines = library.getFineReport();
        if (fines.isEmpty()) {
            System.out.println("No fines have been charged yet.");
            return;
        }
        System.out.println("Members with fine charges:");
        Map<String, Double> chartData = new java.util.LinkedHashMap<>();
        for (FineRecord fr : fines) {
            System.out.println("  " + fr);
            chartData.put(fr.getMemberName() + " (" + fr.getMembershipId() + ")", fr.getAmount());
        }
        ConsoleChart.printBarChart("Fine Charges by Member", chartData, "Rs.");
        SwingChart.showBarChart("Fine Charges by Member", chartData, "Fine (Rs.)");
    }

    /** Pre-loads some sample data so the menu is useful immediately. */
    private static void seedSampleData() {
        library.addBook(new Book("B001", "Effective Java", "Joshua Bloch", "978-0134685991"));
        library.addBook(new Book("B002", "Clean Code", "Robert C. Martin", "978-0132350884"));
        library.addBook(new Book("B003", "Introduction to Algorithms", "Cormen et al.", "978-0262033848"));
        library.addBook(new Book("B004", "Data Structures in Java", "Peter Drake", "978-0136122905"));

        library.addMember(new Student("P001", "Aditi Sharma", "aditi@example.com", "M001", "B.Tech CSE"));
        library.addMember(new Faculty("P002", "Dr. Rakesh Verma", "rakesh@example.com", "M002", "Computer Science"));

        System.out.println("Sample data loaded: 4 books, 2 members.\n");
    }
}
