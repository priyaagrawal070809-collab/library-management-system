package service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type { ISSUE, RETURN, RESERVE }

    private String transactionId;
    private String bookId;
    private String membershipId;
    private Type type;
    private LocalDateTime timestamp;

    public Transaction(String transactionId, String bookId, String membershipId, Type type) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.membershipId = membershipId;
        this.type = type;
        this.timestamp = LocalDateTime.now();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public Type getType() {
        return type;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("[%s] %-6s | Book: %-8s | Member: %-8s | %s",
                transactionId, type, bookId, membershipId, timestamp.format(fmt));
    }
}
