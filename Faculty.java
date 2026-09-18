package model;

public class Faculty extends Member {
    private static final long serialVersionUID = 1L;
    private static final int MAX_BOOKS = 5;

    private String department;

    public Faculty(String id, String name, String email, String membershipId, String department) {
        super(id, name, email, membershipId);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    @Override
    public int getMaxBooksAllowed() {
        return MAX_BOOKS;
    }

    @Override
    public MembershipType getMembershipType() {
        return MembershipType.FACULTY;
    }

    @Override
    public String displayDetails() {
        return super.displayDetails() + " | Dept: " + department;
    }
}
