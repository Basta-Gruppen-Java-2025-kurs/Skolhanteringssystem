import Helpers.Named;

public class Course implements Named {
    private String subject;
    
    Course(String subject)
    {
        this.subject = subject;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    @Override
    public String getName() {
        return subject;
    }
}
