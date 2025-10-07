public class Student extends Person{
    private int classYear;
    
    public Student(String name, String securityNumber, String email, int classYear) {
        super(name, securityNumber, email);
        this.classYear = classYear;
    }

    public int getClassYear() {
        return classYear;
    }

    public void setClassYear(int classYear) {
        this.classYear = classYear;
    }
}
