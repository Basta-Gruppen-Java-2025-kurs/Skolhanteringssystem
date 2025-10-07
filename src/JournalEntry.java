import java.time.LocalDate;

public class JournalEntry {
    private final Course course;
    private final Grade grade;
    private final String gradeComment;
    private final LocalDate date;
    private final Teacher teacher;
    private final Student student;

    JournalEntry(Course c, Teacher t, Student s, Grade g, String gc, LocalDate d) {
        course = c;
        teacher = t;
        student = s;
        grade = g;
        gradeComment = gc;
        date = d;
    }

    public Course getCourse() {
        return course;
    }

    public Grade getGrade() {
        return grade;
    }

    public String getGradeComment() {
        return gradeComment;
    }

    public LocalDate getDate() {
        return date;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public Student getStudent() {
        return student;
    }
}
