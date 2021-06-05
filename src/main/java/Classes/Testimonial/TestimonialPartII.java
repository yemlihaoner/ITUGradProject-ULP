package Classes.Testimonial;

import java.util.Date;

public class TestimonialPartII implements java.io.Serializable {
    public String n;      //N number to identification
    public String R_ksb;     //Random value to define log between User and Provider
    public Date date;       //Timestamp of request

    public TestimonialPartII(Date date_now, String R_ksb, String n) {
        this.n=n;
        this.R_ksb=R_ksb;
        this.date=date_now;
    }
}
