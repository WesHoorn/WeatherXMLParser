import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    public boolean success = false;

    // Start the server and run for x seconds
    public static void main (String[] args){

        try{
        prepareStationList();}
        catch(Exception e){
            System.out.println("Could not find stations.txt in folder, looking in classpath...");
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

    public static void prepareStationList() throws IOException{
        File file = new File("stations.txt");
        Scanner sc = new Scanner(file, StandardCharsets.UTF_8);
        makeStationList(sc);
    }

    public static void prepareStationList(InputStream stream)throws IOException{
        Scanner sc = new Scanner(stream, StandardCharsets.UTF_8);
        makeStationList(sc);
    }

    private static void makeStationList (Scanner sc) throws IOException {
        ArrayList<Integer> list = new ArrayList<>();
        while (sc.hasNext()){
            String line = sc.nextLine();

            line = line.split(",")[0];
            line = line.replace("\"","");
            list.add(Integer.parseInt(line));
        }
        stationlist = list;
    }
}
