import Helpers.IMenu;
import Helpers.SafeInput;
import Helpers.TextMenu;
import Helpers.MenuBuilder;

import java.time.LocalDate;
import java.util.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
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

        Teacher anna = new Teacher("Anna Andersson", "198001011234", "anna@school.com", 10);
        Teacher bjorn = new Teacher("Björn Berg", "197512125678", "bjorn@school.com", 15);
        Teacher carina = new Teacher("Carina Carlsson", "199003039999", "carina@school.com", 8);

        teachers.add(anna);
        teachers.add(bjorn);
        teachers.add(carina);

        // Kurser
        Course math = new Course("Mathematics");
        Course physics = new Course("Physics");
        Course programming = new Course("Programming");
        Course english = new Course("English");

        courses.add(math);
        courses.add(physics);
        courses.add(programming);
        courses.add(english);

        // Elever
        Student david = new Student("David Dahl", "200601011234", "david@student.com", 1);
        Student emma = new Student("Emma Ek", "200505052222", "emma@student.com", 2);
        Student fredrik = new Student("Fredrik Fors", "200404043333", "fredrik@student.com", 3);

        students.add(david);
        students.add(emma);
        students.add(fredrik);

        // Koppla kurser till lärare
        anna.assignCourse(math);
        anna.assignCourse(english);
        bjorn.assignCourse(physics);
        bjorn.assignCourse(math);
        carina.assignCourse(programming);

        // Koppla kurser till elever
        david.assignCourse(math);
        david.assignCourse(physics);

        emma.assignCourse(english);
        emma.assignCourse(programming);

        fredrik.assignCourse(math);
        fredrik.assignCourse(programming);
        fredrik.assignCourse(english);


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
                .addItem("Show all courses", this::displayAllCourses)
                .addItem("View a course", this::viewCourse)
                .addItem("Add students", this::addStudentsMenu)
                .addItem("Add teachers", this::addTeachersMenu)
                .addItem("Add courses", this::addCoursesMenu)
                .addItem("Assign to courses", this::assignToCoursesMenu)
                .addItem("Remove course from Teacher or Student", this::removeCourseMenu)
                .addItem("Set grade", this::addJournalEntryMenu)
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


    public void displayAllCourses() {
        System.out.println("\n=== List of Courses ===");

        if(courses.isEmpty()) {
          System.out.println("No courses found. ");
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
                            .toList();

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

    public void viewCourse(){
        if (courses.isEmpty()){
            System.out.println("No courses found.");
            return;
        }

        listMenuLoop(
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
    public void removeCourseMenu(){
        menuLoop(
                "Remove a course from:",
                new String[] {"Return to main menu", "Teacher", "Student"},
                new Runnable[]{this::removeCourseFromTeacher, this::removeCourseFromStudent},
                true
        );
    }

    public void removeCourseFromTeacher(){
        removeCourseFromList(
                "Select a teacher to remove them from a course",
                "There are no teachers",
                new ArrayList<>(teachers)
        );
    }

    public void removeCourseFromStudent(){
        removeCourseFromList(
                "Select a student to remove them from a course",
                "There are no students",
                new ArrayList<>(students)
        );
    }

    public void selectCourseToRemove(Person person){
        if (person.getCourses().isEmpty()){
            System.out.println("This person has no courses");
            return;
        }

        listMenuLoop(
                "Select the course to remove from "+person.getName(),
                "Back to main menu",
                "No courses",
                new ArrayList<>(person.getCourses()),
                course -> {
                    person.removeCourse(course);
                    System.out.println("Removed successfully!");
                },
                true
        );

    }

    private void removeCourseFromList(String header, String emptyMessage, List<? extends Person> list){
        if (list.isEmpty()){
            System.out.println(emptyMessage);
            return;
        }

        listMenuLoop(
                header,
                "Back to main menu",
                "No entries",
                new ArrayList<>(list),
                this::selectCourseToRemove,
                true
        );
    }

    public void addJournalEntryMenu() {
        SafeInput si = new SafeInput(new Scanner(System.in));

        Teacher teacher = null;
        while (teacher == null) {
            System.out.println("\nSelect a teacher:");
            int i = 1;
            for (Teacher t : teachers) {
                System.out.println(i + ". " + t.getName());
                i++;
            }
            int choice = si.nextInt("Enter number (0 to cancel): ");
            if (choice == 0) return;
            teacher = new ArrayList<>(teachers).get(choice - 1);
        }

        Course course = null;
        while (course == null) {
            System.out.println("\nSelec a course from " + teacher.getName() + "´s courses: ");
            List<Course> teacherCourses = teacher.getCourses();

            int i = 1;
            for (Course c : teacherCourses) {
                System.out.println(i + ". " + c.getSubject());
                i++;
            }
            int choice = si.nextInt("Enter number (0 to cancel): ");
            if (choice <= 0 || choice > teacherCourses.size()) return;
            course = teacherCourses.get(choice - 1);
        }
        final Course selectedCourse = course;

        List<Student> courseStudents = students.stream()
                .filter(s -> s.getCourses().contains(selectedCourse))
                .toList();
        if (courseStudents.isEmpty()) {
            System.out.println("No students in this course.");
            return;
        }

        Student student = null;
        while (student == null) {
            System.out.println("\nSelect student to give grade in course " + course.getSubject() + ": ");

            int i = 1;
            for (Student s : students) {
                System.out.println(i + ". " + s.getName());
                i++;
            }
            int choice = si.nextInt("Enter number (0 to cancel): ");
            if (choice == 0) return;
            student = courseStudents.get(choice - 1);
        }

        Grade grade = null;
        while (grade == null) {
            String gradeStr = si.nextLine("Enter grade (A-F, NA, ABSENT, SPECIAL): ").toUpperCase();
            try {
                grade = Grade.valueOf(gradeStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid grade. Try again.");
            }
        }

        String comment = si.nextLine("Enter comment (optional): ");

        JournalEntry entry = new JournalEntry(course, teacher, student, grade, comment, LocalDate.now());
        journal.add(entry);
    }
}
