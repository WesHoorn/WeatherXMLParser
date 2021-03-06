import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;

public class XMLParser implements Runnable{
    //Instance variables
    private InputStream stream;

    private int name; //=hour
    private volatile BufferedWriter writer;
    private String currentStn = "";
    private String prevline = "";

    //Booleans for line handling and file creation logic
    private Boolean end = false;
    private Boolean foundStn = false;
    private Boolean fileExists = false;


    public XMLParser(InputStream stream){
        this.stream = stream;
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
                        System.out.println("Error while closing writer from main loop");}
                    }
                this.end = false;
                this.fileExists = false; //reset to 'unchekced'
            }
            handleLine(sc.nextLine());
        }
        //close writer if/when stream stops
        try{this.writer.close();}
        catch(Exception e){System.out.println("Could not close writer, may have been closed earlier: "+e.getClass());}

    }

    //Called when station is found in xml data
    private void fileHandle() {
        LocalDateTime date = LocalDateTime.now();
        if (!(name == date.getSecond())) {
            int year = date.getYear();
            int month = date.getMonth().getValue();
            int day = date.getDayOfMonth();
            this.name = date.getHour();

            String pathname;
            boolean success;
            boolean windows;
            if (Main.os.contains("Windows")){
                windows = true;
                pathname = "parsedweatherdata\\" + this.currentStn + "\\" + year+"-"+ month + "\\" + day;
                success = new File(pathname).mkdirs();
            } else {
                windows = false;
                pathname = "/home/pi/mnt/weatherdata/"+this.currentStn+"/"+ year+"-"+month + "/" + day;
                success = new File(pathname).mkdirs();
            }
            //System.out.println("Directory creation success: "+success+"\nAble to write to new dir: "+new File(pathname).canWrite());

            //open output stream
            File f;
            boolean created =false;
            try{
                if (!windows){
                    f = new File(pathname+"/"+name+".xml");
                    created = f.createNewFile();
                    Files.setPosixFilePermissions(f.toPath(), PosixFilePermissions.fromString("rw-rw-rw-"));
                }else{
                    f = new File(pathname+"\\"+name+".xml");
                    created = f.createNewFile();
                    f.setWritable(true);
                    f.setReadable(true);
                }
                FileOutputStream fo = new FileOutputStream(f, true);
                this.writer = new BufferedWriter(new OutputStreamWriter(fo));
            } catch (IOException e) {//System.out.println("Error while opening file"+"\nFile creation success: "+ created);
                e.printStackTrace();
            }
        }
    }

    //called for every line in the inputstream as some essential logic is dependant on its contents
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
        //data stream contains all global stations, must filter for relevant ones
        if(!this.currentStn.isEmpty()){
            if (Main.stationlist.contains(Integer.parseInt(this.currentStn))){
                // fetch/create file and write to it
                if (this.foundStn){
                    if (!this.fileExists){
                        fileHandle();
                        out("\t<MEASUREMENT>");
                        this.fileExists = true;}

                    out(line);
                }
            }
        }

        // handling of end of measurement, line will have been wrtitten
        if (line.contains("</MEASUREMENT>")){
            this.end = true;
            this.foundStn = false;
        }

    }

    //write 1 line to currently opened file
    private void out(String line){
        try{
            if (!((this.prevline.contains("<MEASUREMENT>") && (!this.prevline.contains("/"))) && line.contains("<MEASUREMENT>"))){
                this.writer.write(line);
                this.writer.newLine();
                this.writer.flush();
            }
        }
        catch(IOException e){
            String name = Thread.currentThread().getName();
            System.out.println("\nIO Error while trying to write to file in " + name +
                "\nthis happened while writing to station "+this.currentStn+" around "+LocalDateTime.now()
            +".\n Tried to write: "+line +".\nThe previous line was: "+this.prevline);
            this.prevline = "<MEASUREMENT>";
        }
        catch(NullPointerException e2){
            System.out.println("Failed to write an empty line");
        }
        this.prevline = line;
    }

}