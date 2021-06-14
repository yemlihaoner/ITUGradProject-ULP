package Classes.Testimonial;
import Classes.EncryptData;

public class Testimonial implements java.io.Serializable {
    public EncryptData first;               //Provider related part of the testimonial
    public EncryptData second;              //BulletinBoard related part of the testimonial
    public Testimonial(EncryptData partI, EncryptData partII) {
        this.first=partI;
        this.second=partII;
    }
}
