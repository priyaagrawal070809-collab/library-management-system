package service;

import datastructure.CustomLinkedList;
import datastructure.CustomQueue;
import datastructure.CustomStack;
import exception.BookNotAvailableException;
import exception.BookNotFoundException;
import exception.BorrowLimitExceededException;
import exception.MemberNotFoundException;
import model.Book;
import model.BookStatus;
import model.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Central service class tying everything together.
 *
 * Data structures used on purpose, to show a range of techniques:
 *  - HashMap<String, Book>        : O(1) lookup of a book by id (Java Collections)
 *  - CustomLinkedList<Member>     : hand-built list of all members (custom DS)
 *  - Map<String, CustomQueue<Member>> : one hand-built queue per book for reservations
 *  - CustomStack<Transaction>     : most-recent-first transaction history (custom DS)
 *  - List<Transaction>            : a flat, iterable log (backs the reports/graphs below)
 */
public class Library implements Searchable<Book>, Reservable {

    private final Map<String, Book> bookCatalog = new HashMap<>();
    private final CustomLinkedList<Member> members = new CustomLinkedList<>();
    private final Map<String, CustomQueue<Member>> reservationQueues = new HashMap<>();
    private final CustomStack<Transaction> transactionHistory = new CustomStack<>();
    private final List<Transaction> transactionLog = new ArrayList<>(); // for reporting/graphs

    // ---------- Fine tracking ----------
    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 5.0; // Rs. 5/day late

    private final Map<String, LocalDate> bookDueDates = new HashMap<>();   // bookId -> due date
    private final Map<String, Double> memberFines = new HashMap<>();      // membershipId -> total fine

    private int transactionCounter = 1;

    // ---------- Book management ----------

    public void addBook(Book book) {
        bookCatalog.put(book.getBookId(), book);
    }

    public Book getBook(String bookId) throws BookNotFoundException {
        Book book = bookCatalog.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("No book found with id: " + bookId);
        }
        return book;
    }

    public List<Book> getAllBooksSorted() {
        List<Book> all = new ArrayList<>(bookCatalog.values());
        Collections.sort(all); // uses Book.compareTo -> sorted by title
        return all;
    }

    // ---------- Member management ----------

    public void addMember(Member member) {
        members.addLast(member);
    }

    public Member findMemberById(String membershipId) throws MemberNotFoundException {
        for (Member m : members) {
            if (m.getMembershipId().equalsIgnoreCase(membershipId)) {
                return m;
            }
        }
        throw new MemberNotFoundException("No member found with membership id: " + membershipId);
    }

    public List<Member> getAllMembersSorted() {
        List<Member> all = new ArrayList<>();
        for (Member m : members) {
            all.add(m);
        }
        Collections.sort(all); // uses Member.compareTo -> sorted by name
        return all;
    }

    // ---------- Core operations ----------

    public void issueBook(String bookId, String membershipId)
            throws BookNotFoundException, MemberNotFoundException,
            BookNotAvailableException, BorrowLimitExceededException {

        Book book = getBook(bookId);
        Member member = findMemberById(membershipId);

        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new BookNotAvailableException(
                    "Book '" + book.getTitle() + "' is not available (status: " + book.getStatus() + ")");
        }
        if (!member.canBorrowMore()) {
            throw new BorrowLimitExceededException(
                    member.getName() + " has reached the borrowing limit of " + member.getMaxBooksAllowed());
        }

        book.setStatus(BookStatus.ISSUED);
        member.borrowBook(book);
        bookDueDates.put(bookId, LocalDate.now().plusDays(LOAN_PERIOD_DAYS));

        logTransaction(new Transaction(nextTransactionId(), bookId, membershipId, Transaction.Type.ISSUE));
    }

    /**
     * Returns a book. If it comes back after its due date, a fine is calculated
     * (FINE_PER_DAY per day late) and added to the member's running total.
     * Returns the fine charged for THIS return (0.0 if on time / not tracked).
     */
    public double returnBook(String bookId, String membershipId)
            throws BookNotFoundException, MemberNotFoundException {

        Book book = getBook(bookId);
        Member member = findMemberById(membershipId);

        member.returnBook(book);

        double fineCharged = 0.0;
        LocalDate dueDate = bookDueDates.remove(bookId);
        if (dueDate != null) {
            long lateDays = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
            if (lateDays > 0) {
                fineCharged = lateDays * FINE_PER_DAY;
                memberFines.merge(membershipId, fineCharged, Double::sum);
                System.out.printf(">> Book returned %d day(s) late. Fine charged to %s: Rs. %.2f%n",
                        lateDays, member.getName(), fineCharged);
            }
        }

        // If someone is waiting in the reservation queue, hand it to them
        CustomQueue<Member> queue = reservationQueues.get(bookId);
        if (queue != null && !queue.isEmpty()) {
            Member next = queue.dequeue();
            book.setStatus(BookStatus.RESERVED);
            System.out.println(">> Book reserved for next member in queue: " + next.getName());
        } else {
            book.setStatus(BookStatus.AVAILABLE);
        }

        logTransaction(new Transaction(nextTransactionId(), bookId, membershipId, Transaction.Type.RETURN));
        return fineCharged;
    }

    @Override
    public void reserveBook(String bookId, Member member) throws BookNotFoundException {
        getBook(bookId); // validates existence, throws if not found
        reservationQueues.computeIfAbsent(bookId, k -> new CustomQueue<>()).enqueue(member);

        logTransaction(new Transaction(nextTransactionId(), bookId, member.getMembershipId(), Transaction.Type.RESERVE));
    }

    private void logTransaction(Transaction t) {
        transactionHistory.push(t);
        transactionLog.add(t);
    }

    // ---------- Searching (Searchable<Book> implementation) ----------

    @Override
    public List<Book> searchByTitle(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book b : bookCatalog.values()) {
            if (b.getTitle().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(b);
            }
        }
        Collections.sort(results);
        return results;
    }

    @Override
    public List<Book> searchByAuthor(String keyword) {
        List<Book> results = new ArrayList<>();
        for (Book b : bookCatalog.values()) {
            if (b.getAuthor().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(b);
            }
        }
        Collections.sort(results);
        return results;
    }

    // ---------- Transaction history ----------

    /** Prints the most recent transactions first (LIFO, via CustomStack). */
    public void printRecentTransactions(int count) {
        CustomStack<Transaction> temp = new CustomStack<>();
        int printed = 0;
        while (!transactionHistory.isEmpty() && printed < count) {
            Transaction t = transactionHistory.pop();
            System.out.println(t);
            temp.push(t);
            printed++;
        }
        // push everything back so history isn't destroyed by viewing it
        while (!temp.isEmpty()) {
            transactionHistory.push(temp.pop());
        }
        if (printed == 0) {
            System.out.println("No transactions yet.");
        }
    }

    // ---------- Reports used for data visualization ----------

    /**
     * Counts how many books each member has ISSUED in the last 7 days.
     * Key = member's display name ("Name (membershipId)"), Value = count.
     * A LinkedHashMap preserves descending-count order for a clean chart.
     */
    public Map<String, Integer> getWeeklyIssuesByMember() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        Map<String, Integer> counts = new HashMap<>();

        for (Transaction t : transactionLog) {
            if (t.getType() == Transaction.Type.ISSUE && t.getTimestamp().isAfter(weekAgo)) {
                String label = labelFor(t.getMembershipId());
                counts.merge(label, 1, Integer::sum);
            }
        }

        // Sort descending by count for a readable chart
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> b.getValue() - a.getValue());

        Map<String, Integer> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : entries) {
            sorted.put(e.getKey(), e.getValue());
        }
        return sorted;
    }

    /**
     * All members currently carrying a fine balance, sorted highest fine first.
     */
    public List<FineRecord> getFineReport() {
        List<FineRecord> records = new ArrayList<>();
        for (Map.Entry<String, Double> e : memberFines.entrySet()) {
            String name;
            try {
                name = findMemberById(e.getKey()).getName();
            } catch (MemberNotFoundException ex) {
                name = "Unknown";
            }
            records.add(new FineRecord(e.getKey(), name, e.getValue()));
        }
        records.sort(Comparator.comparingDouble(FineRecord::getAmount).reversed());
        return records;
    }

    private String labelFor(String membershipId) {
        try {
            return findMemberById(membershipId).getName() + " (" + membershipId + ")";
        } catch (MemberNotFoundException e) {
            return membershipId;
        }
    }

    private String nextTransactionId() {
        return "TXN" + String.format("%04d", transactionCounter++);
    }
}
