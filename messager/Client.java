import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;



public class Client extends JFrame {

    private JPanel root;
    private JTextField inText;
    private JButton sendButton;
    private JLabel idLabel;
    private JTextArea Message;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Client() {

        this.getContentPane().add(root);
        try {
            socket = new Socket("localhost", 8283);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            SendRecv sr = new SendRecv(socket);
            sr.start();
        }
        catch (Exception ex) {
            System.out.println("Client error");
        }

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                out.println(inText.getText());
            }
        });
    }

    private class SendRecv extends Thread {
        private Socket socket_;
        private BufferedReader in;
        private PrintWriter out;

        SendRecv(Socket socket) {
            this.socket_ = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                String id = in.readLine();
                System.out.println(id);
                idLabel.setText(id);
            }
            catch (Exception ex) {
                System.out.println("SendRecv Error");
            }
        }


        @Override
        public void run() {
            try {
                while (true) {
                    String str = in.readLine();
                    System.out.println(str);
                    String old_message = Message.getText();
                    Message.setText(old_message + '\n' + str);
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }

        }
    }

    public static void main(String[] args) {
        Client form = new Client();
        form.pack();
        form.setSize(new Dimension(300, 300));
        form.setVisible(true);
    }
}


