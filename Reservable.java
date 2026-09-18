package service;

import exception.BookNotFoundException;
import model.Member;

public interface Reservable {
    void reserveBook(String bookId, Member member) throws BookNotFoundException;
}
