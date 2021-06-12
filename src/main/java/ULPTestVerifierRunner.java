import Utils.Constants;

public class ULPTestVerifierRunner {
    public static void main(String[] args){
        try{
            System.out.println("Running a new user");

            for (int i = 0;i<1;i++) {
                Thread.sleep(Constants.delay/5);

                new ULPVerifier().start();
            }
        }catch (InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
