public class Teacher extends Person {
    private int experienceYear;

    public Teacher(String name, String securityNumber, String email, int experienceYear) {
        super(name, securityNumber, email);
        this.experienceYear = experienceYear;
    }

    public int getExperienceYear() {
        return experienceYear;
    }

    public void setExperienceYear(int experienceYear) {
        this.experienceYear = experienceYear;
    }
}
