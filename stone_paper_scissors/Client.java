import com.sun.org.apache.regexp.internal.RE;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int my_num;
    public Client() {
        try {
            socket = new Socket("localhost",8382);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            SendRevc sr = new SendRevc(socket);
            sr.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class SendRevc extends Thread {
        private Socket socket_;
        private BufferedReader in;
        private PrintWriter out;
        private BufferedReader console;

        SendRevc(Socket socket) {
            this.socket_ = socket;
            try {
                console = new BufferedReader(new InputStreamReader(System.in));
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                String id = in.readLine();
                my_num = Integer.parseInt(id);
                System.out.println("My number is " + id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String str = console.readLine();
                    System.out.println("Client enter the string");
                    out.println(Integer.toString(my_num) + " " + str);
                    System.out.println(Integer.toString(my_num) + " " + str);
                    System.out.println(in.readLine());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public static void main(String[] args) {
        new Client();
    }
}