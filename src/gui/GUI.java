package gui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.Dimension;

/**
 * The root component of the GUI. Uses a tabbed pane to switch between an <code>Explorer</code>
 * and a <code>ImageStudio</code>.
 */
public class GUI extends JFrame {

    public GUI() {
        setMinimumSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Explorer", new Explorer());
        tabbedPane.addTab("Studio", new ImageStudio());
        add(tabbedPane);

        pack();
        setVisible(true);
    }

}
