import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class client_gui extends JFrame {
    private JPanel root;
    private JRadioButton stoneButton;
    private JRadioButton paperButton;
    private JRadioButton scissorsButton;
    private JButton Send;
    private JLabel WhoWinner;
    private JPanel panelRadio;

    private int my_num;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public client_gui() {

        this.getContentPane().add(root);
        try {
            socket = new Socket("localhost", 8283);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            SendRecv sr = new SendRecv(socket);
            sr.start();

            panelRadio = new JPanel(new GridLayout(0, 1, 0, 5));
            panelRadio.setBorder(BorderFactory.createTitledBorder("Выберите один из вариантов"));
            String[] names1 = { "камень", "бумага", "ножницы" };
            ButtonGroup bg1 = new ButtonGroup();
            for (int i = 0; i < names1.length; i++) {
                JRadioButton radio = new JRadioButton(names1[i]);
                panelRadio.add(radio);
                bg1.add(radio);
            }
            root.add(panelRadio);




        }
        catch (Exception ex) {
            System.out.println("Client error");
        }

        Send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (stoneButton.isSelected()) {
                     out.println(my_num + " " + "камень");
                } else if (paperButton.isSelected()) {
                    out.println(my_num + " " + "бумага");
                } else {
                    out.println(my_num + " " + "ножницы");
                }
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
                //System.out.println(id);
                my_num = Integer.parseInt(id);
                //idLabel.setText(id);
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
                    //String old_message = Message.getText();
                    WhoWinner.setText("Результат : " + str);
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }

        }
    }

    public static void main(String[] args) {
        client_gui form = new client_gui();
        form.pack();
        form.setSize(new Dimension(300, 300));
        form.setVisible(true);
    }
}
