import Helpers.IMenu;
import Helpers.TextMenu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.regex.Pattern;

public class SchoolSystem implements IMenu {
    private static SchoolSystem instance;
    private final HashSet<Student> students;
    private final HashSet<Teacher> teachers;
    private final HashSet<Course> courses;
    private final ArrayList<JournalEntry> journal;


    private SchoolSystem()  {
        students = new HashSet<>();
        teachers = new HashSet<>();
        courses = new HashSet<>();
        journal = new ArrayList<>();
    }

    public static SchoolSystem getInstance() {
        if (instance == null) {
            instance = new SchoolSystem();
        }
        return instance;
    }


    @Override
    public void menu() {
        TextMenu.menuLoop(
                "Welcome to School System!",
                new String[] {"Exit", "Show all teachers"},
                new Runnable[] {this::displayAllTeachers},
                false);
        System.out.println("Good bye.");
    }

    public HashSet<Student> getStudents() {
        return students;
    }

    public HashSet<Teacher> getTeachers() {
        return teachers;
    }

    public HashSet<Course> getCourses() {
        return courses;
    }

    public ArrayList<JournalEntry> getJournal() {
        return journal;
    }

    public void displayAllTeachers() {
        System.out.println("\n=== List of Teachers ===");

        if (teachers.isEmpty()) {
            System.out.println("No teachers found.");
            return;
        }

        String format = "| %-20s | %-15s | %-30s | %-18s |%n";

        System.out.printf(format, "Name", "Security No", "Email", "Experience (Years)");
        System.out.println("|----------------------|-----------------|--------------------------------|--------------------|");

        getTeachers().stream()
                .sorted(Comparator.comparing(Teacher::getName))
                .forEach(t -> System.out.printf(
                        format,
                        t.getName(),
                        t.getSecurityNumber(),
                        t.getEmail(),
                        t.getExperienceYear()
                ));

        System.out.println();
    }

    private String validatePersonalData(String name, String securityNumber, String email, int year) {
        ArrayList<String> errors = new ArrayList<>();
        // validate name
        if (name.isBlank()) {
            errors.add("blank name");
        }
        // validate security number
        if (securityNumber.isBlank()) {
            errors.add("empty security number");
        } else if (securityNumber.length() != 10 && securityNumber.length() != 12) {
            errors.add("security number length must be 10 or 12 digits");
        } else if (!Pattern.compile("d+").matcher(securityNumber).matches()) {
            errors.add("security number must have decimal digits only");
        }
        // validate email
        if (email.isBlank()) {
            errors.add("empty email");
        } else if(!email.contains("@") || !email.contains(".")) {
            errors.add("email is not in the format <address>@<domain>.<ext>");
        }
        // validate year
        if (year <= 0) {
            errors.add("year must be positive");
        }
        String errorString = String.join(", ", errors);
        errorString = errorString.isBlank() ? errorString : errorString.substring(0,1).toUpperCase() + errorString.substring(1);
        return errorString;
    }

    public boolean addTeacher(String name, String securityNumber, String email, int experienceYears) throws InvalidPersonalData {
        String validation = validatePersonalData(name, securityNumber, email, experienceYears);
        if (!validation.isEmpty()) {
            throw new InvalidPersonalData("Error adding new teacher: " + validation);
        }
        return teachers.add(new Teacher(name, securityNumber, email, experienceYears));
    }

    public boolean addStudent(String name, String securityNumber, String email, int classYear) throws InvalidPersonalData {
        String validation = validatePersonalData(name, securityNumber, email, classYear);
        if (!validation.isEmpty()) {
            throw new InvalidPersonalData("Error adding new student: " + validation);
        }
        return students.add(new Student(name, securityNumber, email, classYear));
    }

    public boolean addCourse(String subject) throws InvalidCourseData {
        if (subject.isBlank()) {
            throw new InvalidCourseData("Empty course name");
        }
        return courses.add(new Course(subject));
    }
}
