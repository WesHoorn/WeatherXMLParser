import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import com.jcraft.jsch.*;

//Must have its own thread to not make application hang while awaiting connection
public class Server extends Thread{

    private InputStream instream;
    private ServerSocket serverSocket;
    private Socket socket;
    public volatile boolean runbool = true;

    //Open socket
    public Server(int port) {
        try{
            System.out.println("Opening socket");
            this.serverSocket = new ServerSocket(port);
            System.out.println("Success");
        } catch (IOException e){
            System.out.println("Could not open socket");
        }
    }

    public InputStream getIn(){
        return this.instream;
    }

    // For every new connection: new threaded parser instance
    public void run(){
        boolean run = true;
        int parsed = 0;

        while (run){
            try{
                System.out.println("Listening...");
                this.socket = serverSocket.accept();
            }

            catch(IOException ex){
                System.out.println("Could not accept any client connection");
            }

            try{
                instream = socket.getInputStream();
            }

            catch(IOException ex){
                System.out.println("Failed to get inputstream from socket");
            }

            parsed += 1;
            XMLParser parser = new XMLParser(instream);
            parser.start();
            System.out.println("Parsers opened:" + parsed);
        }

    }
}
