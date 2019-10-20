package Task2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer{
    private ServerSocket serverSocket;
    public TCPServer() throws IOException {
        serverSocket=new ServerSocket(0); //creating new server socket on any available port
    }
    public  int getPort() {
        return serverSocket.getLocalPort();
    }
    public void start() { //method in which server side of tcp connection is implemented
        try {
            Socket connectionSocket=serverSocket.accept();
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            PrintWriter outToClient = new PrintWriter(connectionSocket.getOutputStream(),true);
            System.out.println("waiting for user message");
            String fromClient=inFromClient.readLine(); //server accepts clients message
            System.out.println("Received: " + fromClient);
            outToClient.println(fromClient); //server sends back the same message (in fact here could be any string)
            outToClient.close();
            inFromClient.close();
            connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
