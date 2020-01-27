import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

public class XMLParser extends Thread{
    private InputStream stream;

    private int name; //=second
    private BufferedWriter writer;
    private String currentStn;
    private LocalDateTime date;
    private Deque<String> queuedLines;

    private Boolean end = false;
    private Boolean foundStn = false;
    private Boolean created = false;


    public XMLParser(InputStream stream){
        this.stream = stream;
        this.queuedLines = new ArrayDeque<>();
        setDate();
    }

    @Override
    //Iterate XML inputstream by every UTF8 line
    public void run(){
        Scanner sc = new Scanner(this.stream, StandardCharsets.UTF_8);
        while (sc.hasNext()){
            if (this.end){
                try{this.writer.close();}
                catch(IOException e){e.printStackTrace();
                    System.out.println("Earlier: Error while closing writer");}
                this.end = false;
                this.created = false;
            }
            handleLine(sc.nextLine());
        }
        try{this.writer.close();}
        catch(IOException e){System.out.println("Later: Error while closing writer");
        e.printStackTrace();}

    }

    private void setDate(){
        this.date = LocalDateTime.now();
    }

    private void fileCreate() {
        setDate();
        if (!(name == date.getSecond())) {
            int year = date.getYear();
            int month = date.getMonth().getValue();
            int day = date.getDayOfMonth();
            int hour = date.getHour();
            int minute = date.getMinute();
            this.name = date.getSecond();

            String pathname = "weatherdata/" + this.currentStn + "/" + year + "/" + month + "/" + day +
                    "/" + hour + "/" + minute;
            Boolean success = new File(pathname).mkdirs();

            try{
                File f = new File(pathname+"/"+name+".xml");
                FileOutputStream fo = new FileOutputStream(f);
                this.writer = new BufferedWriter(new OutputStreamWriter(fo));
            } catch(IOException e){e.printStackTrace();}


            while (this.queuedLines.peekFirst()!= null){
                out(this.queuedLines.removeFirst());
            }
        }
    }


    private void handleLine(String line){
        if (line.contains("WEATHERDATA") || line.contains("?")){
            return;
        }

        if (line.contains("STN")){
            String[] splitline = line.split(">");
            splitline = splitline[1].split("<");
            this.currentStn = splitline[0];
            this.foundStn = true;
        }

        if (this.foundStn){
            if (!this.created){
                this.fileCreate();
                this.created = true;}

            out(line);
        }
        else{
            this.queuedLines.add(line);
        }

        if (line.contains("</MEASUREMENT>")){
            this.end = true;
            this.foundStn = false;
        }

    }

    private void out(String line){
        try{
            this.writer.write(line);
            this.writer.newLine();
            this.writer.flush();
        }
        catch(IOException e){e.printStackTrace();
        System.out.println("Error while trying to write");}
    }

}