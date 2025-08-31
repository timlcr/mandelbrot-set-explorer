package gui;

import image.MandelbrotImage;
import util.Complex;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
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
        JPanel buttonPanel = new JPanel();
        JButton resetButton = new JButton("Reset");
        JButton previousButton = new JButton("Show previous zoom");
        JButton colorButton = new JButton("Color");
        JButton sendCoordsButton = new JButton("Send coordinates to Studio");
        resetButton.addActionListener(e -> reset());
        previousButton.addActionListener(e -> showPreviousImage());
        colorButton.addActionListener(e -> colorControls.setVisible(true));
        sendCoordsButton.addActionListener(e -> sendCoords.run());
        buttonPanel.add(resetButton);
        buttonPanel.add(previousButton);
        buttonPanel.add(colorButton);
        buttonPanel.add(sendCoordsButton);
        return buttonPanel;
    }

}
