import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Server {
    private ServerSocket ss;
    private List<Resender> connections = Collections.synchronizedList(new ArrayList<>());
    List<Integer> winners = Collections.synchronizedList(new ArrayList<>());
    int num_player = 0;
    int send_num = 0;
    int winner = -1;
    CyclicBarrier barrier;

    List<String> results = Collections.synchronizedList(new ArrayList<>());;

    private Server() {
        try {
            //barrier = new CyclicBarrier(2);
            ss = new ServerSocket(8283);
            while (true) {
                Socket socket = ss.accept();
                Resender con = new Resender(socket);
                con.start();
                connections.add(con);
                results.add("-1");
                winners.add(-1);
                barrier = new CyclicBarrier(num_player);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Resender extends Thread {
        Socket socket_;
        PrintWriter out;
        BufferedReader in;

        Resender(Socket socket) {
            this.socket_ = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                num_player++;
                System.out.println("Num of players is " + Integer.toString(num_player));
                out.println(Integer.toString(num_player));
                System.out.println("New Connection. Server send number for each person");
            } catch (IOException e) {
                e.printStackTrace();
            }
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

        public String get_str(String str) {
            int i;
            for (i = 0; i < str.length(); ++i) {
                if (str.charAt(i) == ' ')
                    break;
            }
            str = str.substring(i + 1, str.length());
            return str;
        }

        @Override
        public void run() {
            String str = "";
            while (true) {
                try {
                    str = in.readLine();
                    //barrier = new CyclicBarrier(num_player);
                    winner = -1;
                    int num1 = parse(str);
                    str = get_str(str);
                    //results.add(num1, str);
                    results.set(num1-1, str);
                    System.out.println(num1 + " " + str);
                    send_num++;
                    if (send_num == num_player) {
                        System.out.println("Who is won?");
                        if (results.contains("камень") && results.contains("бумага") &&
                        results.contains("ножницы")) {
                            System.out.println("Все 3 варианта");
                            winner = 0;
                        } else if (results.contains("камень") && results.contains("бумага")) {
                            System.out.println("Камни и бумага");
                            for (int i = 0; i < results.size(); ++i) {
                                if (results.get(i).equals("бумага")) {
                                    winners.set(i, 1);
                                } else {
                                    winners.set(i, 0);
                                }
                            }
                        } else if (results.contains("камень") && results.contains("ножницы")) {
                            //System.out.println("Im here");
                            System.out.println("камни и ножницы");
                            for (int i = 0; i < results.size(); ++i) {
                                if (results.get(i).equals("камень")) {
                                    winners.set(i, 1);
                                } else {
                                    winners.set(i, 0);
                                }
                            }
                        } else if (results.contains("ножницы") && results.contains("бумага")) {
                            System.out.println("Ножницы и бумага");
                            for (int i = 0; i < results.size(); ++i) {
                                if (results.get(i).equals("ножницы")) {
                                    winners.set(i, 1);
                                } else {
                                    winners.set(i, 0);
                                }
                            }
                        } else {
                            System.out.println("Только 1 вариант");
                            winner = 0;
                        }
                        //System.out.println(winner);
                    }
                    try {
                        barrier.await();
                        //System.out.println("Finished");
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }


                    if (winner == 0) {
                        out.println("Ничья");
                    } else if (winners.get(num1 - 1) == 1) {
                        //System.out.println("Now here");
                        out.println("Победа");
                    } else out.println("Поражение");
                    //out.println("Winner player is " + winner);
                    //barrier.reset();
                    try {
                        barrier.await();
                        //System.out.println("Finished");
                    } catch (BrokenBarrierException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    winner = -1;
                    barrier.reset();
                    send_num = 0;
                    if (num1 == 0) {
                        for (int i = 0; i < results.size(); ++i) {
                            winners.set(i, -1);
                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args){
        new Server();
    }
}