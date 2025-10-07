import Helpers.IMenu;
import Helpers.SafeInput;
import Helpers.TextMenu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
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
                new String[] {"Exit", "Show all students", "Show all teachers", "Add students", "Add teachers", "Add courses"},
                new Runnable[] {this::listAllStudents, this::displayAllTeachers, this::addStudentsMenu, this::addTeachersMenu, this::addCoursesMenu},
                false);
        System.out.println("Good bye.");
    }

    @FunctionalInterface
    interface PersonProcessor {
        void process(String name, String personalNumber, String email, int year);
    }

    private boolean enterNextPerson(String yearPrompt, PersonProcessor callback) {
        AtomicBoolean fullDataEntered = new AtomicBoolean(false);
        SafeInput si = new SafeInput(new Scanner(System.in));
        String name = si.nextLine("Please enter full name (empty to stop):");
        if (name.isBlank()) {
            return false;
        }
        si.nameInputLoop("Please enter email (empty to stop):", "", ". Please try again", email -> {
            if (email.isBlank()) {
                return false;
            }
            String emailError = validateEmail(email);
            if (!emailError.isBlank()) {
                System.out.print(Capitalize(emailError));
                return false;
            }
            si.nameInputLoop("Please enter security number (empty to stop)", "", ". Please try again", securityNumber -> {
                if (securityNumber.isBlank()) {
                    return true;
                }
                String validationError = validateSecurityNumber(securityNumber);
                if (!validationError.isBlank()) {
                    System.out.print(Capitalize(validationError));
                    return false;
                }
                int years = si.nextInt(yearPrompt, "Wrong number. Please try again.", 0, 1000);
                callback.process(name, securityNumber, email, years);
                fullDataEntered.set(true);
                return true;
            });
            return true;
        });
        return fullDataEntered.get();
    }

    private void addTeachersMenu() {
        while(enterNextPerson("Please enter years of experience:", (name,securityNumber,email,experienceYears) -> {
            try {
                System.out.println(addTeacher(name, securityNumber, email, experienceYears) ? "Teacher added." : "Failed to add teacher.");
            } catch (InvalidPersonalData e) {
                System.out.println("Error adding a new teacher: " + e);
            }
        })) {
            System.out.println("Add next teacher");
        }
    }

    private void addStudentsMenu() {
        while(enterNextPerson("Please enter class year:", (name,securityNumber,email,classYear) -> {
            try {
                System.out.println(addStudent(name, securityNumber, email, classYear) ? "Student added." : "Failed to add student.");
            } catch (InvalidPersonalData e) {
                System.out.println("Error adding a new student: " + e);
            }
        })) {
            System.out.println("Add next student");
        }
    }

    private void addCoursesMenu() {

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
        System.out.println("|----------------------|-----------------|---------------------------------|--------------------|");

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
        ArrayList<String> errors = new ArrayList<>() {
            @Override
            public boolean add(String s) {
                return !s.isBlank() && super.add(s);
            }
        };
        // validate name
        if (name.isBlank()) {
            errors.add("blank name");
        }
        // validate security number
        errors.add(validateSecurityNumber(securityNumber));
        // validate email
        errors.add(validateEmail(email));
        // validate year
        if (year <= 0) {
            errors.add("year must be positive");
        }
        return Capitalize(String.join(", ", errors));
    }

    private static String validateEmail(String email) {
        if (email.isBlank()) {
            return "empty email";
        } else if(!email.contains("@") || !email.contains(".")) {
            return "email is not in the format <address>@<domain>.<ext>";
        }
        return "";
    }

    private static String Capitalize(String string) {
        return string.isBlank() ? string : string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    private static String validateSecurityNumber(String securityNumber) {
        if (securityNumber.isBlank()) {
            return "empty security number";
        } else if (securityNumber.length() != 10 && securityNumber.length() != 12) {
            return "security number length must be 10 or 12 digits";
        } else if (!Pattern.compile("\\d+").matcher(securityNumber).matches()) {
            return "security number must have decimal digits only";
        }
        return "";
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
