package service;

import java.util.List;

/**
 * Generic interface -> demonstrates Generics + Interfaces (abstraction).
 * Any class that can be searched by a keyword implements this.
 */
public interface Searchable<T> {
    List<T> searchByTitle(String keyword);
    List<T> searchByAuthor(String keyword);
}
