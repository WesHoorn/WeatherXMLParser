import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static Server server;
    private static Closer closer;
    private static LocalDateTime starttime;
    public static ArrayList<Integer> stationlist;

    // Start the server and run for x seconds
    public static void main (String[] args){

        try{
        makeStationList();}
        catch(IOException e){
            System.out.println("Could not find stations.txt\nExiting...");
            System.exit(0);
        }

        starttime = LocalDateTime.now();
        server = new Server(7789);
        Thread serverthread = new Thread(server);
        serverthread.start();
        closer = new Closer();
        Thread closerthread = new Thread(closer);
        closerthread.start();
        new Thread(new DataRemover()).start();


        while (closer.getBool()){
            try {Thread.sleep(1000);}
            catch(InterruptedException e){e.printStackTrace();}
            //todo: fetch and update errors for closer

        }
        //tell server to stop after button press
        System.out.println("Stopping...");
        server.stop();
        System.out.println("Start time: " + starttime+"\nEnd time: "+LocalDateTime.now());
        System.exit(0);
    }

    private static void makeStationList () throws IOException {
        ArrayList<Integer> list = new ArrayList<>();
        File file = new File("stations.txt");
        Scanner sc = new Scanner(file, StandardCharsets.UTF_8);
        while (sc.hasNext()){
            String line = sc.nextLine();

            line = line.split(",")[0];
            line = line.replace("\"","");
            list.add(Integer.parseInt(line));
        }
        stationlist = list;
    }
}
