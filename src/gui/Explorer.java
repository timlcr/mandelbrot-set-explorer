package gui;

import image.MandelbrotImage;
import util.Complex;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Stack;

/**
 * A gui component which allows the user to explore the Mandelbrot Set by selecting the area
 * of the next zoom with the mouse.
 */
public class Explorer extends JPanel {

    private final Stack<MandelbrotImage> imageCache = new Stack<>();

    private final ExplorerDisplay display = new ExplorerDisplay(imageCache);
    private final ColorControls colorControls = new ColorControls(this::handleRecolor, null);

    private final Runnable sendCoords;

    /**
     * Constructs an Explorer gui
     */
    public Explorer(Runnable sendCoords) {
        super(new BorderLayout());
        this.sendCoords = sendCoords;

        display.setBorder(GUI.imageDisplayBorder());
        add(display, BorderLayout.CENTER);

        add(buttonPanel(), BorderLayout.SOUTH);

        reset();
    }
    
    public MandelbrotImage image() {
        return display.image();
    }

    public void colorControlsVisible(boolean visible) {
        colorControls.setVisible(visible);
    }

    private void reset() {
        imageCache.clear();
        double defaultZoom = 3;
        MandelbrotImage base = new MandelbrotImage(500, 500, Complex.DEFAULT_CENTER, defaultZoom, 1000,
                colorControls.getColorFunction(), colorControls.getColorFuncParams(defaultZoom));
        display.setImage(base);
        display.repaint();
    }

    private void showPreviousImage() {
        if (imageCache.isEmpty()) return;
        MandelbrotImage image = imageCache.pop();
        image.setColorFunction(colorControls.getColorFunction());
        image.setColorFuncParams(colorControls.getColorFuncParams(image.zoom));
        image.colorImage();
        display.setImage(image);
    }

    private void handleRecolor() {
        MandelbrotImage image = image();
        image.setColorFunction(colorControls.getColorFunction());
        image.setColorFuncParams(colorControls.getColorFuncParams(image.zoom));
        image.colorImage();
        display.repaint();
    }



    private JPanel buttonPanel() {
        JButton resetButton = new JButton("Reset");
        JButton previousButton = new JButton("Previous zoom");
        JButton colorButton = new JButton("Colour controls");
        JButton sendCoordsButton = new JButton("Send coordinates to Studio");
        resetButton.addActionListener(e -> reset());
        previousButton.addActionListener(e -> showPreviousImage());
        colorButton.addActionListener(e -> colorControls.setVisible(true));
        sendCoordsButton.addActionListener(e -> sendCoords.run());

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = previousButton.getPreferredSize().width - resetButton.getPreferredSize().width;
        buttonPanel.add(resetButton, c);

        c.ipadx = 0;
        c.gridy = 1;
        buttonPanel.add(previousButton, c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 2;
        buttonPanel.add(Box.createHorizontalStrut(25), c);

        c.gridx = 2;
        buttonPanel.add(colorButton, c);

        c.gridx = 3;
        buttonPanel.add(Box.createHorizontalStrut(25), c);

        c.gridx = 4;
        buttonPanel.add(sendCoordsButton, c);

        return buttonPanel;
    }

}
