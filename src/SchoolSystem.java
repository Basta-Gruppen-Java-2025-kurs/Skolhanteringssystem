import Helpers.IMenu;
import Helpers.TextMenu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

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
                new String[] {"Exit", "Show all students", "Show all teachers", "View a course"},
                new Runnable[] {this::listAllStudents, this::displayAllTeachers, this::viewCourse},
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

    public void viewCourse(){
        if (courses.isEmpty()){
            System.out.println("No courses found.");
            return;
        }

        TextMenu.listMenuLoop(
                "Select a course to view details:",
                "Back to main menu",
                "No active courses",
                new ArrayList<>(courses),
                this::showCourseDetails,
                true

        );
    }

    public void showCourseDetails(Course course){
        System.out.println("\n=== Course Details ===");
        System.out.println("Course: "+ course.getName());
        System.out.println("------------------------");

        System.out.println("\nTeachers:");
        var courseTeachers = getTeachers().stream()
                .filter(t -> t.getCourses().contains(course))
                .sorted(Comparator.comparing(Teacher::getName))
                .toList();

        if (courseTeachers.isEmpty()){
            System.out.println(" None assigned.");

        }else {
            courseTeachers.forEach(t -> System.out.println("  - " + t.getName()));
        }

        System.out.println("\nStudents:");
        var courseStudents = getStudents().stream()
                .filter(s -> s.getCourses().contains(course))
                .sorted(Comparator.comparing(Student::getName))
                .toList();

        if (courseStudents.isEmpty()){
            System.out.println(" None enrolled.");
        }else {
            courseStudents.forEach(s -> {
                var entry = journal.stream()
                        .filter(j -> j.getCourse().equals(course)&& j.getStudent().equals(s))
                        .reduce((first, second) -> second)
                        .orElse(null);

                String gradeText;
                if (entry == null){
                    gradeText = "No grade recorded";
                }else {
                    gradeText = entry.getGrade().getName();

                    if (entry.getGradeComment() != null){
                        gradeText += " - " + entry.getGradeComment();
                    }
                }

                System.out.println("  - " + s.getName() + " (" + gradeText + ")");
            });

        }
        System.out.println();
    }
}
