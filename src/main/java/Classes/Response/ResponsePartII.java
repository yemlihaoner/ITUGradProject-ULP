package Classes.Response;

import java.util.Date;

public class ResponsePartII implements java.io.Serializable {
    public byte[] m;      //Mask
    public String n;      //N number to identification
    public String R_ksb;     //Random value to define log between User and Provider
    public Date date;       //Timestamp of request

    public ResponsePartII(Date date_now, String R_ksb, byte[] m,String n) {
        this.m=m;
        this.n=n;
        this.R_ksb=R_ksb;
        this.date=date_now;
    }
}
