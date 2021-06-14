package Classes;

public class PhaseIII implements java.io.Serializable{
    public String N;                                //Unique N value
    public String R_ksb;                            //Random value shared between BulletinBoard, User and Provider
    public String R_ks;                             //Random value shared between User and Provider
    public  PhaseIII(String N,String R_ks,String R_ksb){
        this.N=N;
        this.R_ks=R_ks;
        this.R_ksb=R_ksb;
    }
}
