package gui;

import image.MandelbrotImage;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Component;
import java.awt.Dimension;

/**
 * The root component of the GUI. Uses a tabbed pane to switch between an <code>Explorer</code>
 * and a <code>ImageStudio</code>.
 */
public class GUI extends JFrame {

    JTabbedPane tabbedPane = new JTabbedPane();

    private final Explorer explorer = new Explorer(this::sendExplorerCoordsToStudio);
    private final Studio studio = new Studio();

    public GUI() {
        setMinimumSize(new Dimension(500, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPane.addTab("Explorer", explorer);
        tabbedPane.addTab("Studio", studio);
        tabbedPane.addChangeListener(cl());
        add(tabbedPane);

        pack();
        setVisible(true);
    }

    static Border imageDisplayBorder() {
        Border empty = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border bevel = BorderFactory.createLoweredBevelBorder();
        return BorderFactory.createCompoundBorder(empty, bevel);
    }

    private void sendExplorerCoordsToStudio() {
        tabbedPane.setSelectedComponent(studio);
        explorer.colorControlsVisible(false);
        MandelbrotImage explorerImage = explorer.image();
        studio.setZoomCoords(explorerImage.center, explorerImage.zoom);
        studio.renderControlsVisible(true);
    }

    private ChangeListener cl() {
        return e -> {
        Component selected = ((JTabbedPane) e.getSource()).getSelectedComponent();
        if (selected == explorer) {
            studio.colorControlsVisible(false);
            studio.renderControlsVisible(false);
        } else  if (selected == studio) {
            explorer.colorControlsVisible(false);
        }};
    }

}
