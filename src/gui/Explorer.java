package gui;

import image.MandelbrotImage;
import util.Complex;

import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;

/**
 * A gui component which allows the user to explore the Mandelbrot Set by selecting the area
 * of the next zoom with the mouse.
 */
public class Explorer extends JPanel {

    private final ExplorerDisplay display = new ExplorerDisplay();
    private final ColorControls colorControls = new ColorControls(this::handleRecolor, null);

    /**
     * Constructs an Explorer gui
     */
    public Explorer() {
        super(new BorderLayout());

        display.setBorder(GUI.imageDisplayBorder());
        add(display, BorderLayout.CENTER);

        add(buttonPanel(), BorderLayout.SOUTH);

        reset();
    }
    
    public MandelbrotImage image() {
        return display.image();
    }

    private void reset() {
        double defaultZoom = 3;
        MandelbrotImage base = MandelbrotImage.of(500, 500, Complex.DEFAULT_CENTER, defaultZoom, 1000,
                colorControls.getColorFunction(), colorControls.getColorFuncParams(defaultZoom));
        display.setImage(base);
        display.repaint();
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
        JButton colorButton = new JButton("Color");
        resetButton.addActionListener(e -> reset());
        colorButton.addActionListener(e -> colorControls.setVisible(true));
        buttonPanel.add(resetButton);
        buttonPanel.add(colorButton);
        return buttonPanel;
    }

}
