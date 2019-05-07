import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


class MyThread extends Thread {
    public boolean incr;
    public boolean decr;
    public boolean pause;
    public JLabel timer;

    MyThread(JLabel _label) { this.timer = _label; }

    @Override
    public void run() {
        int i = 0;
        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(pause) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pause = false;
                }
            }
            if(incr) {
                if(i < 20) {
                    i++;
                }
                else
                    incr = false;
            }
            if(decr) {
                if (i > 0) {
                    i--;
                } else
                    decr = false;
            }
            if(timer != null) {
                timer.setText(String.valueOf(i));
            }
        }
    }
}

public class Gui_timer {
    private JPanel rootPanel;
    private JButton forwardButton;
    private JButton backButton;
    private JButton pauseButton;
    private JButton resumeButton;
    private JLabel timer;
    private MyThread thread;


    public Gui_timer() {
        forwardButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (thread == null) {
                    thread = new MyThread(timer);
                    thread.start();
                }
                thread.decr = false;
                thread.incr = true;
            }
        });
            backButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (thread == null) {
                        thread = new MyThread(timer);
                        thread.start();
                    }
                    thread.incr = false;
                    thread.decr = true;
                }
            });

        pauseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (thread != null) {
                    thread.pause = true;
                }
            }
        });
        resumeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (thread != null) {
                    synchronized (thread) {
                        thread.notify();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setContentPane(new Gui_timer().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setSize(400,400);
    }
}


