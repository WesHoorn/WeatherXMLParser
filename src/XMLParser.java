import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Scanner;

public class XMLParser implements Runnable{
    //Instance variables
    private InputStream stream;

    private int name; //=hour
    private int errorcount;
    private volatile BufferedWriter writer;
    private String currentStn = "";
    private Deque<String> queuedLines;

    //Booleans for line handling and file creation logic
    private Boolean end = false;
    private Boolean foundStn = false;
    private Boolean fileExists = false;


    public XMLParser(InputStream stream){
        this.stream = stream;
        this.queuedLines = new ArrayDeque<>();
    }

    @Override
    //Iterate XML inputstream by every UTF8 line
    public void run(){
        Scanner sc = new Scanner(this.stream, StandardCharsets.UTF_8);
        while (sc.hasNext()){
            //close writer when at end of measurement
            if (this.end){
                if(this.writer != null){
                    try{this.writer.close();}
                    catch(Exception e){
                        System.out.println("Earlier: Error while closing writer");}
                    }
                this.end = false;
                this.fileExists = false; //reset to 'unchekced'
            }
            handleLine(sc.nextLine());
        }
        //close writer if/when stream stops
        try{this.writer.close();}
        catch(IOException e){System.out.println("Later: Error while closing writer");
        e.printStackTrace();}

    }

    //Called when station is found in xml data
    private void fileHandle() {
        LocalDateTime date = LocalDateTime.now();
        if (!(name == date.getSecond())) {
            int year = date.getYear();
            int month = date.getMonth().getValue();
            int day = date.getDayOfMonth();
            this.name = date.getHour();

            String pathname = "weatherdata/" + this.currentStn + "/" + year + "/" + month + "/" + day;
            Boolean success = new File(pathname).mkdirs();

            //open output stream
            try{
                File f = new File(pathname+"/"+name+".xml");
                FileOutputStream fo = new FileOutputStream(f, true);
                this.writer = new BufferedWriter(new OutputStreamWriter(fo));
            } catch(IOException e){e.printStackTrace();}

            //Write all lines from before station is found
            while (this.queuedLines.peekFirst()!= null){
                out(this.queuedLines.removeFirst());
            }
        }
    }


    private void handleLine(String line){
        //do not save semi-unnecessary overhead
        if (line.contains("WEATHERDATA") || line.contains("?")){
            return;
        }

        //station number is used to create filepath
        if (line.contains("STN")){
            String[] splitline = line.split(">");
            splitline = splitline[1].split("<");
            this.currentStn = splitline[0];
            this.foundStn = true;
        }
        //data contains all global stations, must filter for relevant ones
        if(!this.currentStn.isEmpty()){
            if (Main.stationlist.contains(Integer.parseInt(this.currentStn))){
                // fetch/create file and write to it
                if (this.foundStn){
                    if (!this.fileExists){
                        fileHandle();
                        this.fileExists = true;}

                    out(line);
                }
                // if not found station number: add to queue
                else{
                    this.queuedLines.add(line);
                }
            }
        }

        // end of file
        if (line.contains("</MEASUREMENT>")){
            this.end = true;
            this.foundStn = false;
        }

    }

    //write 1 line to currently opened file
    private void out(String line){
        try{
            this.writer.write(line);
            this.writer.newLine();
            this.writer.flush();
        }
        catch(IOException e){
        this.errorcount += 1;
        System.out.println("\nIO Error while trying to write to file\nErrors for this instance: "+this.errorcount);
        }

    }

}