package Task2;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client {
    private static ArrayList<Integer> ports = new ArrayList<>();
    private final static String MESSAGE = "knock";
    private static byte[] buffer = MESSAGE.getBytes();
    private static DatagramSocket d;
    private static InetAddress inetAddress;

    static {
        try {
            d = new DatagramSocket(0); //client datagram can be created on any non occupied port
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        if (args.length > 1) {
            inetAddress = InetAddress.getByName(args[0]);
            for (int i = 1; i < args.length; i++) {
                ports.add(Integer.parseInt(args[i]));
            }
            for (Integer i : ports) {
                try {
                    DatagramPacket packetToSend = new DatagramPacket(buffer, buffer.length, inetAddress, i);//forming packet which will be sent to server
                    d.send(packetToSend);
                    Thread.sleep(100); //set some timeout between sending packets in order to minimise incorrect order risk
                } catch (Exception e) {
                    System.out.println("incorrect data");
                    e.printStackTrace();
                }
            }
            byte[] receiveBuffer = new byte[1024];
            DatagramPacket packetToReceive = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                d.setSoTimeout(100); //terminating of client process when there is no data received in given timeout
                try {
                    d.receive(packetToReceive);
                } catch (SocketTimeoutException e) {
                    System.out.println("didn't receive any data");
                    d.close();
                    System.exit(0);
                }
                String portToConnect = new String(packetToReceive.getData(), 0, packetToReceive.getLength());
                int port = Integer.parseInt(portToConnect); //creating port basing on data from received data
                System.out.println("port " + port + " to connect received");
                Socket tcpSocket = new Socket(inetAddress, port); //creating client tcp socket
                System.out.println("tcp client socket created");
                PrintWriter outToServer = new PrintWriter(tcpSocket.getOutputStream(), true);
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
                outToServer.println("hello"); //any message could be here to send to server
                System.out.println("message has been send");
                System.out.println("waiting");
                System.out.println("FROM SERVER:" + inFromServer.readLine()); //printing data received from server
                inFromServer.close();
                outToServer.close();
                tcpSocket.close();
            } catch (NumberFormatException | IOException e) {
                System.out.println("failed to connect to port");
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}

