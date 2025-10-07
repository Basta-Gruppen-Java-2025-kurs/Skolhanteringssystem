import java.util.ArrayList;
import java.util.List;

public abstract class Person {
    private String name;
    private String securityNumber;
    private String email;
    private List<Course> courses;

    public Person(String name, String securityNumber, String email) {
        this.name = name;
        this.securityNumber = securityNumber;
        this.email = email;
        this.courses = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecurityNumber() {
        return securityNumber;
    }

    public void setSecurityNumber(String securityNumber) {
        this.securityNumber = securityNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
