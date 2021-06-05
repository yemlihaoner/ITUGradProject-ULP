package Classes.Testimonial;

public class Testimonial implements java.io.Serializable {
    public TestimonialPartI first;
    public TestimonialPartII second;
    public Testimonial(TestimonialPartI partI, TestimonialPartII partII) {
        this.first=partI;
        this.second=partII;
    }
}
