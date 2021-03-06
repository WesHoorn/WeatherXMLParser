import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static String os;
    private static Closer closer;
    private static LocalDateTime starttime;
    public static ArrayList<Integer> stationlist;
    public boolean success = false;
    private static boolean runbool = true;
    private static Server server;

    public static void main (String[] args){
        try{
        prepareStationList();}
        catch(Exception e){
            System.out.println("Could not find stations.txt in folder, looking in classpath...");
        }

        os = System.getProperties().getProperty("os.name");
        System.out.println(os);
        starttime = LocalDateTime.now();

        server = new Server(7789);
        Thread serverthread = new Thread(server);
        serverthread.start();
        //server = new LocalSocket(60000);
        //new Thread(server).start();

        new Thread(new DataRemover()).start();
        if (!GraphicsEnvironment.isHeadless()){
            closer = new Closer();
            Thread closerthread = new Thread(closer);
            closerthread.start();

            while (closer.getBool()){
                try {Thread.sleep(1000);}
                catch(InterruptedException e){e.printStackTrace();}
                //todo: fetch and update errors for closer

            }
        }else{
            while (runbool){
                try {Thread.sleep(5000);}
                catch(InterruptedException e){e.printStackTrace();}
                //todo: implement a way to quit
            }
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

    private static void makeStationList (Scanner sc) {
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
