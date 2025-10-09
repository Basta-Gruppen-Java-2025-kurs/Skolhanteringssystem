import Helpers.Named;

public enum Roles implements Named {
    STUDENT("Student"),
    TEACHER("Teacher");

    private final String label;

    Roles(String label) {
        this.label = label;
    }

    @Override
    public String getName() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
