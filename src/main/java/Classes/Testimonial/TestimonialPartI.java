package Classes.Testimonial;

import java.util.Date;

public class TestimonialPartI implements java.io.Serializable {
    public String comment;      //Comment
    public String R_ks;     //Random value to define log between User and Provider
    public Date date;       //Timestamp of request

    public TestimonialPartI(Date date_now, String R_ks, String comment) {
        this.comment=comment;
        this.R_ks=R_ks;
        this.date=date_now;
    }
}
