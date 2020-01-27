public class Main {
    private static Server server;
    private static boolean run = true;
    private static int runtime = 0; //cycles that have passed
    private static int x = 10; //max cycles

    // Start the server and run for x seconds
    public static void main (String[] args){
        server = new Server(7789);
        server.start();

        while (run){
            try {Thread.sleep(1000);
            runtime += 1;}
            catch(InterruptedException e){e.printStackTrace();}
            finally{if (runtime > x){run = false;}} //run for ~10 seconds
        }
        //tell server to stop after timer has passed
        server.runbool = false;
    }
}
