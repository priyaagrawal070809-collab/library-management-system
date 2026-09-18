package model;

public class Student extends Member {
    private static final long serialVersionUID = 1L;
    private static final int MAX_BOOKS = 3;

    private String course;

    public Student(String id, String name, String email, String membershipId, String course) {
        super(id, name, email, membershipId);
        this.course = course;
    }

    public String getCourse() {
        return course;
    }

    @Override
    public int getMaxBooksAllowed() {
        return MAX_BOOKS;
    }

    @Override
    public MembershipType getMembershipType() {
        return MembershipType.STUDENT;
    }

    @Override
    public String displayDetails() {
        return super.displayDetails() + " | Course: " + course;
    }
}
