import Helpers.IMenu;
import Helpers.TextMenu;

import java.util.ArrayList;
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
                new String[] {"Exit", "Show all teachers"},
                new Runnable[] {this::showAllTeachers},
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

    public void showAllTeachers() {
        System.out.println("=== List of Teachers ===");

        if (teachers.isEmpty()) {
            System.out.println("No teachers found.");
            return;
        }

        for (Teacher teacher : teachers) {
            System.out.println("Name       : " + teacher.getName());
            System.out.println("Email      : " + teacher.getEmail());
            System.out.println("Experience : " + teacher.getExperienceYear() + " years");
            System.out.println("-----------------------------");
        }
        System.out.println();
    }
}
