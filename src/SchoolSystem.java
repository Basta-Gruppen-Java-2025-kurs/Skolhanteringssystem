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
        TextMenu.menuLoop("Welcome to School System!", new String[] {"Exit"}, new Runnable[] {}, false);
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
}
