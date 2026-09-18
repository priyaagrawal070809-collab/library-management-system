package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class demonstrating INHERITANCE (extends Person) and
 * setting up POLYMORPHISM via getMaxBooksAllowed() / getMembershipType(),
 * which each concrete subclass (Student, Faculty) implements differently.
 */
public abstract class Member extends Person implements Comparable<Member> {
    private static final long serialVersionUID = 1L;

    private String membershipId;
    private List<Book> borrowedBooks; // Java Collections Framework (ArrayList)

    public Member(String id, String name, String email, String membershipId) {
        super(id, name, email); // calls Person constructor
        this.membershipId = membershipId;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getMembershipId() {
        return membershipId;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void borrowBook(Book book) {
        borrowedBooks.add(book);
    }

    public void returnBook(Book book) {
        borrowedBooks.remove(book);
    }

    public boolean canBorrowMore() {
        return borrowedBooks.size() < getMaxBooksAllowed();
    }

    // Each subclass defines its own borrowing limit -> runtime polymorphism
    public abstract int getMaxBooksAllowed();

    public abstract MembershipType getMembershipType();

    @Override
    public String displayDetails() {
        return String.format("[%s] %-10s | %-15s | %-20s | Borrowed: %d/%d",
                getMembershipType(), membershipId, getName(), getEmail(),
                borrowedBooks.size(), getMaxBooksAllowed());
    }

    // Natural ordering by name -> lets Collections.sort() work on members
    @Override
    public int compareTo(Member other) {
        return this.getName().compareToIgnoreCase(other.getName());
    }
}
