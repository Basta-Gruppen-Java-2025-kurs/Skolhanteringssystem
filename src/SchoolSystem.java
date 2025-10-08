import Helpers.IMenu;
import Helpers.TextMenu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

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
                new String[] {
                        "Exit",
                        "Show all students",
                        "Show all teachers",
                        "Show all courses"},
                new Runnable[] {
                        this::listAllStudents,
                        this::displayAllTeachers,
                        this::displayAllCourses},
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

    public void listAllStudents(){
        if (students.isEmpty()){
            System.out.println("No students found.");
            return;
        }
        String format = "| %-20s | %-15s | %-30s | %-10s |%n";

        System.out.printf(format, "Name", "Security No", "Email", "Class Year");
        System.out.println("|----------------------|-----------------|-------------------------------|------------|");

        getStudents().stream()
                .sorted(Comparator.comparing(Student::getName))
                .forEach(s -> System.out.printf(
                        format,
                        s.getName(),
                        s.getSecurityNumber(),
                        s.getEmail(),
                        s.getClassYear()
                ));
      
        System.out.println();
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

    public void displayAllCourses() {
        System.out.println("\n=== List of Courses ===");

        if(courses.isEmpty()) {
            System.out.println("No courses found.");
            return;
        }

        String format = "| %-21s | %-23s | %10s |%n";
        String separator = "|-----------------------|-------------------------|------------|";

        System.out.printf(format, "Course Name", "Teachers", "Students");
        System.out.println(separator);

        courses.stream()
                .sorted(Comparator.comparing(Course::getSubject))
                .forEach(course -> {
                    List<String> teacherNames = teachers.stream()
                            .filter(t -> t.getCourses().contains(course))
                            .map(Teacher::getName)
                            .sorted()
                            .collect(Collectors.toList());

                    long studentCount = students.stream()
                            .filter(s -> s.getCourses().contains(course))
                            .count();

                    if(teacherNames.isEmpty()) {
                        System.out.printf(format, course.getSubject(), "-", studentCount);
                        System.out.println(separator);
                        return;
                    }

                    boolean first = true;
                    for (String teacherName : teacherNames) {
                        if (first) {
                            System.out.printf(format, course.getSubject(), teacherName, studentCount);
                            first = false;
                        } else {
                            System.out.printf(format, "", teacherName, "");
                        }
                    }

                    System.out.println(separator);
                });
        System.out.println();
    }
}
