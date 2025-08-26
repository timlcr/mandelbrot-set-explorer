package main;

import gui.ImageDisplay;
import gui.Workspace;

import javax.swing.JFrame;
import java.awt.Dimension;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setMinimumSize(new Dimension(500, 500));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Workspace());
        frame.pack();
        frame.setVisible(true);
    }
}
