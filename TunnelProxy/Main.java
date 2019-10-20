package Task2;

import java.util.ArrayList;

public class Main{
    public static ArrayList<Integer> ports=new ArrayList<>();
    public static void main(String[] args){
        if(args.length==0){
            System.out.println("u should specify sequence of ports in params");
            System.exit(0);
        }
        try {
            for (String s : args) {
                ports.add(Integer.parseInt(s));
            }
        }catch (NumberFormatException e){
            System.out.println("ports should be numbers");
            System.exit(0);
        }
        new Server(ports); //running new server with sequence of ports as param
    }
}
