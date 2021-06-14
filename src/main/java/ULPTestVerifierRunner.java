import Utils.Constants;

//Verifier role runner is defined as below. It creates as much as needed number of
//verifiers to test protocol.
public class ULPTestVerifierRunner {
    public static void main(String[] args){
        try{
            for (int i = 0;i<1;i++) {
                System.out.println("Running a new verifier");
                Thread.sleep(Constants.delay/5);
                new ULPVerifier().start();
            }
        }catch (InterruptedException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
