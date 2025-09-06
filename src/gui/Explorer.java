package gui;

import image.MandelbrotImage;
import image.SuccessiveMandelbrotImage;
import util.Complex;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Stack;

/**
 * A gui component which allows the user to explore the Mandelbrot Set by selecting the area
 * of the next zoom with the mouse.
 */
public class Explorer extends JPanel {

    public static final Color COLOR = new Color(250, 165, 45);

    private final Stack<MandelbrotImage> imageCache = new Stack<>();

    private final ImageDisplay display = new ImageDisplay();
    private final ColorControls colorControls = new ColorControls(
            this::handleRecolor, "Explorer colour controls", COLOR, null
    );

    private final Runnable sendCoords;

    /**
     * Constructs an Explorer gui
     */
    public Explorer(Runnable sendCoords) {
        super(new BorderLayout());
        this.sendCoords = sendCoords;
        new SubzoomSelector(display.imagePanel, this::nextImage);

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

    /**
     * Creates the next image and displays it. The image used is a SuccessiveMandelbrotImage,
     * which uses successive refinement to boost responsiveness and efficiency.
     * @param center the complex point the next image is centered on
     * @param zoom the zoom level of the next image
     */
    private void nextImage(Complex center, double zoom) {
        MandelbrotImage image = display.image;
        imageCache.push(image);
        int maxN = 1000;
        if (zoom < 2e-4) maxN = 2000;
        SuccessiveMandelbrotImage nextImage = new SuccessiveMandelbrotImage(
                center, zoom, maxN, image.colorFunctionType(), image.colorFuncParams()
        );
        nextImage.setFilamentSize(image.getFilamentSize());
        display.setImage(nextImage);
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                for (int i = 0; i < 5; i++) {
                    nextImage.refine();
                    SwingUtilities.invokeLater(() -> repaint());
                }
                return null;
            }
        };
        worker.execute();
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
        buttonPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        buttonPanel.setBackground(COLOR);
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
