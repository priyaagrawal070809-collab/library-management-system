package datastructure;

import java.util.Arrays;

/**
 * A hand-built generic stack backed by a resizable array (LIFO).
 * Used to keep the transaction history, so the most recent
 * issue/return can be viewed or "undone" first.
 */
@SuppressWarnings("unchecked")
public class CustomStack<T> {

    private Object[] elements;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public CustomStack() {
        elements = new Object[DEFAULT_CAPACITY];
    }

    public void push(T value) {
        ensureCapacity();
        elements[size++] = value;
    }

    public T pop() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        T value = (T) elements[--size];
        elements[size] = null; // avoid memory leak
        return value;
    }

    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return (T) elements[size - 1];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void ensureCapacity() {
        if (size == elements.length) {
            elements = Arrays.copyOf(elements, elements.length * 2);
        }
    }
}
