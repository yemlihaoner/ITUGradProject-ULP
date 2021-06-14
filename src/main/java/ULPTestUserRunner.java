import Utils.Constants;

//User role runner is defined as below. It creates as much as needed number of
//users to test protocol.
public class ULPTestUserRunner {
    public static void main(String[] args){
        try{
            for (int i = 0;i<1;i++) {
                System.out.println("Running a new user");
                new ULPUser().start();
                Thread.sleep(Constants.delay/5);
            }
        }catch (InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
