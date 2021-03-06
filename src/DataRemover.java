import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DataRemover implements Runnable {

    private boolean runbool = true;

    public DataRemover(){
        InputStream stream = getClass().getResourceAsStream("stations.txt");
        try{Main.prepareStationList(stream);}
        catch(Exception e){
            System.out.println("Could not find stations.txt in classpath, perhaps it was found in folder?");
        }
    }

    public void run(){
        System.out.println("Dataremover running");
        while(runbool){
            try{
                Thread.sleep(TimeUnit.DAYS.toMillis(1));
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            int removed = check();
            System.out.println("Performed daily data removal\nItems removed: "+removed);
        }
    }

    private int check(){
        Date date = Calendar.getInstance().getTime();
        long time = date.getTime();//in seconds since epoch for easy compare
        time = time - (90*24*60*60);//minus 90 days
        final long timetarget = time;
        int itemcount = 0;
        for(Integer station:Main.stationlist){
            //File path = new File("parsedweatherdata/"+station+"/");
            File path = new File("/mnt/weatherdata/"+station);
            String[] directories = path.list(new FilenameFilter() {
                @Override
                public boolean accept(File current, String name) {
                    return new File(current, name).lastModified() < timetarget; //add to list if older than 90 days;

                }
            });
            if (directories != null){
                for (String dir:directories){
                    boolean removed = new File(dir).delete();
                    if (removed){itemcount++;}
                }
            }
        }
        return itemcount;
    }
}
