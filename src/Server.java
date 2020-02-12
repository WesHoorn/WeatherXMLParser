import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


//Must have its own thread to not make application hang while awaiting connection
public class Server implements Runnable{

    private InputStream instream;
    private ServerSocket serverSocket;
    private Socket socket;
    private boolean runbool = true;

    //Open socket
    public Server(int port) {
        try{
            System.out.println("Opening socket...");
            this.serverSocket = new ServerSocket(port, 800, InetAddress.getLoopbackAddress());
            System.out.println("Socket opened on "+this.serverSocket.getLocalSocketAddress());
        } catch (IOException e){
            System.out.println("Could not open socket");
        }
    }


    // For every new connection: new threaded parser instance
    public void run(){
        System.out.println("Server running");
        int parsed = 0;

        while (runbool){
            try{
                System.out.println("Server listening...");
                this.socket = serverSocket.accept();
                System.out.println("Incoming data, connection accepted!");
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
            new Thread(parser).start();
            System.out.println("Parsers opened:" + parsed);
            //todo: threadpool/semaphore
        }
    }
    public void stop(){
        this.runbool = false;
    }
}
