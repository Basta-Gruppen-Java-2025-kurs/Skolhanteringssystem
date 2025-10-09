import Helpers.IMenu;
import Helpers.MenuBuilder;
import Helpers.SafeInput;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static Helpers.TextMenu.*;

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
        new MenuBuilder()
                .setHeader("Welcome to School System!")
                .addItem("Show all students", this::listAllStudents)
                .addItem("Show all teachers", this::displayAllTeachers)
                .addItem("Add students", this::addStudentsMenu)
                .addItem("Add teachers", this::addTeachersMenu)
                .addItem("Add courses", this::addCoursesMenu)
                .addItem("Assign to courses", this::assignToCoursesMenu)
                .runMenu();
        System.out.println("Good bye.");
    }

    private void assignToCoursesMenu() {
        listMenuLoop("Select course:", "Back", "No courses found.", courses.stream().toList(),
                course -> listMenuLoop("Assign teachers or students?", "Cancel", "No roles found.", Arrays.asList(Roles.values()),
                        r -> {
            ArrayList<Person> personList = new ArrayList<>(switch (r) {
                case STUDENT -> students.stream().filter(s -> !s.getCourses().contains(course)).toList();
                case TEACHER -> teachers.stream().filter(t -> !t.getCourses().contains(course)).toList();
            });
            String role = r.toString().toLowerCase();
            listMenuLoop("Add next " + role + ": ", "Stop", "No " + role + "s found.", () -> personList, person -> {
                if (person.assignCourse(course)) {
                    personList.remove(person);
                    System.out.println(r.getName() + " added.");
                } else {
                    System.out.println("Failed to add " + r.getName().toLowerCase() + ".");
                }
            }, false);
        }, true), true);
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
            String emailError = Validator.validateEmail(email);
            if (!emailError.isBlank()) {
                System.out.print(Validator.Capitalize(emailError));
                return false;
            }
            si.nameInputLoop("Please enter security number (empty to stop)", "", ". Please try again", securityNumber -> {
                if (securityNumber.isBlank()) {
                    return true;
                }
                String validationError = Validator.validateSecurityNumber(securityNumber);
                if (!validationError.isBlank()) {
                    System.out.print(Validator.Capitalize(validationError));
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
        SafeInput si = new SafeInput(new Scanner(System.in));
        while(true) {
            String courseName = si.nextLine("Please enter course name (empty to stop):");
            if (courseName.isBlank()) {
                return;
            }
            try {
                System.out.println(addCourse(courseName) ? "Course added." : "Failed to add course.");
            } catch (InvalidCourseData e) {
                System.out.println("Error adding a course: " + e);
            }
        }
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
        System.out.println("|----------------------|-----------------|--------------------------------|------------|");

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


    public boolean addTeacher(String name, String securityNumber, String email, int experienceYears) {
        String validation = Validator.validatePersonalData(name, securityNumber, email, experienceYears);
        if (!validation.isEmpty()) {
            throw new InvalidPersonalData("Error adding new teacher: " + validation);
        }
        return teachers.add(new Teacher(name, securityNumber, email, experienceYears));
    }

    public boolean addStudent(String name, String securityNumber, String email, int classYear) {
        String validation = Validator.validatePersonalData(name, securityNumber, email, classYear);
        if (!validation.isEmpty()) {
            throw new InvalidPersonalData("Error adding new student: " + validation);
        }
        return students.add(new Student(name, securityNumber, email, classYear));
    }

    public boolean addCourse(String subject) {
        if (subject.isBlank()) {
            throw new InvalidCourseData("Empty course name");
        }
        return courses.add(new Course(subject));
    }
}
