import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.io.File;

import org.newsclub.net.unix.AFUNIXServerSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

public class LocalSocket implements Runnable {
    private InputStream instream;
    private Socket socket;
    private AFUNIXServerSocket server;
    private boolean runbool = true;

    //Open socket
    public LocalSocket(int port) {
        final File socketFile = new File(new File(System.getProperty("java.io.tmpdir")),
                     "junixsocket-parser.sock");
        System.out.println(socketFile);
         try{
             this.server = AFUNIXServerSocket.newInstance();
             server.bind(new AFUNIXSocketAddress(socketFile, port));
             System.out.println("server: " + server);
         }catch(IOException e){
            System.out.println("Error while setting up the server");
         }
    }



    // For every new connection: new threaded parser instance
    public void run() {
        System.out.println("Server running");
        int parsed = 0;

        while (this.runbool) {
            while (!Thread.interrupted()) {
                System.out.println("Waiting for connection...");
                try {
                    socket = server.accept();
                    System.out.println("Connected: " + socket);
                    try {
                        instream = socket.getInputStream();
                    } catch (IOException e) {
                        System.out.println("Failed to get stream from accepted connection");
                    }
                } catch (IOException e) {
                    System.out.println("Failed to set up connection");
                }

                if (this.instream != null) {
                    parsed += 1;
                    XMLParser parser = new XMLParser(instream);
                    new Thread(parser).start();
                    System.out.println("Parsers opened:" + parsed);
                    this.instream = null;
                }
            }
            //todo: threadpool/semaphore
        }
    }

    public void stop(){
        this.runbool = false;
    }
}
