package Classes;
import java.util.Date;

public class Log {
    public Date time;           //DateTime of Log
    public String object;       //Log object; Request,Response or Testimonial
    public String N;            //Unique N value
    public String content;      //Signed Log
    public Log(Date time, String object, String N, String content){
        this.time=time;
        this.object = object;
        this.N=N;
        this.content=content;
    }
}
