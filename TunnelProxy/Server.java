package Task2;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Server {
    private HashMap<String,ArrayList<Integer>> usersKnocks = new HashMap<String,ArrayList<Integer>>(); //map indicating user information(format "address:port") as key and his sequence of knocked ports as value,keys are unique which is granted by HashMap class
    private HashSet<Integer> ports=new HashSet<Integer>();//list of ports without repetitions which is granted by HashSet
    public Server(ArrayList<Integer> portSequence){
        ports.addAll(portSequence);
        for (Integer i:ports){ //making thread for each port
            try {
                Thread connection=new Thread(new Connection(i, usersKnocks,portSequence));
                connection.start();
            } catch (SocketException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
}
