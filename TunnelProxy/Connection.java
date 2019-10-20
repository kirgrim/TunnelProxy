package Task2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;

public class Connection implements Runnable{
    private DatagramSocket udpServerSocket;
    private int port;
    private HashMap<String,ArrayList<Integer>> usersKnocksMap;
    private ArrayList<Integer> portSequence;
    Connection(int port, HashMap<String,ArrayList<Integer>> usersKnocksMap, ArrayList<Integer> portSequence) throws SocketException {
        this.port = port;
        this.usersKnocksMap=usersKnocksMap;
        this.portSequence=portSequence;
        udpServerSocket = new DatagramSocket(this.port);
        System.out.println("created socket:"+port);
    }
    private synchronized boolean knock(String key) { //method which updates value of given key with adding a consequent port value,made synchronised to prevent overlapping
        usersKnocksMap.putIfAbsent(key, new ArrayList<Integer>());
        usersKnocksMap.get(key).add(port);
        System.out.println("key="+key+" value="+usersKnocksMap.get(key));
        return usersKnocksMap.get(key).equals(portSequence);//returns boolean value indicating if value of this key is appropriate to establish the connection
    }
    private String key (String ip,int port){
        return ip+":"+port;
    } //simple method which returns merged ip and port
    @Override
    public void run() {
        byte[] buffIn = new byte[256];
        DatagramPacket udpInput=new DatagramPacket(buffIn, buffIn.length); //packet which includes clients message
        while (true) {
            try {
                udpServerSocket.receive(udpInput);
                System.out.println(new String(udpInput.getData(), 0, udpInput.getLength())+" "+key(udpInput.getAddress().getHostAddress(), udpInput.getPort()));
                DatagramPacket udpOutput;
                byte[] buffOut;
                if ((new String(udpInput.getData(), 0, udpInput.getLength())).equals("knock")) { //if client sends knock message to server
                    if (knock(key(udpInput.getAddress().getHostAddress(), udpInput.getPort()))) { //if final goal achieved open tcp connection
                            usersKnocksMap.remove(key(udpInput.getAddress().getHostAddress(), udpInput.getPort()));//we no longer need info about this client in our Map theoretically it also means that client can after
                            // that connect  again using the same ip and port
                            TCPServer tcpserver=new TCPServer();
                            System.out.println("waiting on connection on port:"+tcpserver.getPort());
                            buffOut = String.valueOf(tcpserver.getPort()).getBytes();
                            udpOutput = new DatagramPacket(buffOut, buffOut.length, udpInput.getAddress(), udpInput.getPort());
                            udpServerSocket.send(udpOutput);//sending port of tcp server to client
                            tcpserver.start();
                    }
                    else if(usersKnocksMap.get(key(udpInput.getAddress().getHostAddress(),udpInput.getPort())).size()==portSequence.size()){
                        usersKnocksMap.remove(key(udpInput.getAddress().getHostAddress(), udpInput.getPort())); //if key in map has appropriate value length but incorrect data we also remove it from our map
                    }
                }
            } catch(IOException e){
                e.printStackTrace();
                udpServerSocket.close();
            }
        }
    }
}
