package model;

import java.io.Serializable;

/**
 * Book entity. Demonstrates encapsulation and implements Comparable
 * so books can be sorted (e.g. alphabetically by title).
 */
public class Book implements Comparable<Book>, Serializable {
    private static final long serialVersionUID = 1L;

    private String bookId;
    private String title;
    private String author;
    private String isbn;
    private BookStatus status;

    public Book(String bookId, String title, String author, String isbn) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.status = BookStatus.AVAILABLE;
    }

    public String getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    @Override
    public int compareTo(Book other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return bookId.equals(book.bookId);
    }

    @Override
    public int hashCode() {
        return bookId.hashCode();
    }

    @Override
    public String toString() {
        return String.format("[%s] %-30s by %-20s | ISBN:%-15s | Status: %s",
                bookId, title, author, isbn, status);
    }
}
