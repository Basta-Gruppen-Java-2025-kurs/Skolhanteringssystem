import Helpers.Named;

import java.util.HashSet;

public abstract class Person implements Named {
    private String name;
    private String securityNumber;
    private String email;
    private final HashSet<Course> courses;

    public Person(String name, String securityNumber, String email) {
        this.name = name;
        this.securityNumber = securityNumber;
        this.email = email;
        this.courses = new HashSet<>();
    }

    @Override
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

    public HashSet<Course> getCourses() {
        return courses;
    }

    public boolean assignCourse(Course course) {
        return courses.add(course);
    }

    public void removeCourse(Course course){
        courses.remove(course);
    }

    public boolean unassignCourse(Course course) {
        return courses.remove(course);
    }

}
