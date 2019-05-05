import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Server {
    private ServerSocket server;
    private List<Connection> connections = Collections.synchronizedList(new ArrayList<>());

    private Server() {
        try {
            server = new ServerSocket(8283);
            while (true) {
                Socket client = server.accept();
                Connection con = new Connection(client);
                con.start();
                connections.add(con);
            }
        } catch (Exception ex) {
            System.out.println("Server error");
        }
    }

    private class Connection extends Thread {
        Socket client;
        BufferedReader in;
        PrintWriter out;
        Connection(Socket cl) throws IOException {
            this.client = cl;
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
            System.out.println(Integer.toString(connections.size()));
            out.println(Integer.toString(connections.size()));
        }

        public int parse(String str) {
            String num;
            int i;
            for (i = 0; i < str.length(); ++i) {
                if (str.charAt(i) == ' ')
                    break;
            }
            num = str.substring(0, i);
            return Integer.parseInt(num);
        }

        @Override
        public void run() {
            String str = "";
            while(true) {
                try {
                    str = in.readLine();
                    int num = parse(str);
                    connections.get(num).out.println(str);
                }
                catch (Exception ex) {
                    System.out.println("Connection class error");
                }
            }
        }
    }
    public static void main(String[] args){
        new Server();
    }
}

