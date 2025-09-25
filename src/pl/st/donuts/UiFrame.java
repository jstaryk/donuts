package pl.st.donuts;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class UiFrame extends JFrame {

    private Runnable startAction = () -> {};
    private Runnable stopAction = () -> {};
    private Runnable resetAction = () -> {};

    public UiFrame(Runnable start, Runnable stop, Runnable reset) {
        this.startAction = start;
        this.stopAction = stop;
        this.resetAction = reset;

        setResizable(false);
        setTitle("Ustawienia");
        add(new UiPanel());
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
        setVisible(true);
    }

    private class UiPanel extends JPanel {
        JButton start;
        JButton stop;
        JButton reset;

        public UiPanel() {
            start = new JButton("Start");
            start.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    startAction.run();
                }
            });
            add(start);

            stop = new JButton("Stop");
            stop.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    stopAction.run();
                }
            });
            add(stop);

            reset = new JButton("Reset");
            reset.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    resetAction.run();
                }
            });
            add(reset);
        }
    }
}
