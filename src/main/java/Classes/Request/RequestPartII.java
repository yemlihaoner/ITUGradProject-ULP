package Classes.Request;

import java.util.Date;

public class RequestPartII implements java.io.Serializable {
    public String R_ksb;        //Random value to define log between User, Provider and Board
    public Date date;       //Timestamp of request

    public RequestPartII(Date date_now, String R_ksb) {
        this.R_ksb=R_ksb;
        this.date=date_now;
    }
}
