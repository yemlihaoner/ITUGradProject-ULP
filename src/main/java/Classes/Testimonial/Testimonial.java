package Classes.Testimonial;

import Classes.EncryptData;

public class Testimonial implements java.io.Serializable {
    public EncryptData first;
    public EncryptData second;
    public Testimonial(EncryptData partI, EncryptData partII) {
        this.first=partI;
        this.second=partII;
    }
}
