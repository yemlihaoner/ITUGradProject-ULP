package Classes;

public class LostPacket implements java.io.Serializable  {
    public String content;                                          //Lost Package (ex. PhaseVI )
    public LostPacket(String content){
        this.content=content;
    }
}
