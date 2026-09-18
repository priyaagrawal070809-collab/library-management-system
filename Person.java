package model;

import java.io.Serializable;

/**
 * Abstract base class demonstrating ABSTRACTION and ENCAPSULATION.
 * All people in the system (members, staff, etc.) share this identity contract.
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    // private fields -> encapsulation
    private String id;
    private String name;
    private String email;

    public Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getters / setters -> controlled access to private state
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Abstract method -> forces every subclass to define its own details view.
    public abstract String displayDetails();

    @Override
    public String toString() {
        return displayDetails();
    }
}
