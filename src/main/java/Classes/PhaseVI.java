package Classes;

public class PhaseVI implements java.io.Serializable{
    public String N;                                //Unique N value
    public byte[] M;                                //Mask binary number
    public PhaseVI(String N, byte[] M){
        this.N=N;
        this.M=M;
    }
}
