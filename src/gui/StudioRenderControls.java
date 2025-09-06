package gui;

import image.MandelbrotImage;
import util.Complex;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

/**
 * A dialog containing input fields which control parameters used to render new
 * MandelbrotImages.
 */
public class StudioRenderControls extends JDialog {

    private final Color color;

    private final ValueField<Integer> widthField = ValueField.intField(500, 5);
    private final ValueField<Integer> heightField = ValueField.intField(500, 5);
    private final ValueField<Double> centerRealField = ValueField.doubleField(-0.5, 5);
    private final ValueField<Double> centerImagField = ValueField.doubleField(0, 5);
    private final ValueField<Double> zoomField = ValueField.doubleField(2.5, 5);
    private final ValueField<Integer> maxNField = ValueField.intField(100, 5);

    private final Runnable imageRenderer;

    /**
     * Constructs a RenderControls dialog. The input fields can be initialised with parameters
     * taken from a given image.
     * @param imageRenderer generates an image using the parameters selected by the input fields
     *                      and then displays it
     * @param image The input fields will display parameters taken from this image. If null
     *              they wil display the default values.
     */
    public StudioRenderControls(
            Runnable imageRenderer, String title, Color color, JButton selectViewButton,
            MandelbrotImage image
    ) {
        this.imageRenderer = imageRenderer;
        this.color = color;
        setTitle(title);
        setMinimumSize(new Dimension(250, 400));
        add(controlsPanel(selectViewButton), BorderLayout.CENTER);
        setImage(image);
    }

    /**
     * Sets the input fields to display image generation parameters taken from
     * a given image.
     * @param image the image the parameters are from
     */
    public void setImage(MandelbrotImage image) {
        if (image == null) {
            setDefaultValues();
            return;
        }
        widthField.setText("" + image.getWidth());
        heightField.setText("" + image.getHeight());
        setZoomCoords(image.center, image.zoom);
        maxNField.setText("" + image.maxN);
    }

    public void setZoomCoords(Complex center, double zoom) {
        setCenter(center);
        setZoomLevel(zoom);
    }

    public void setCenter(Complex center) {
        centerRealField.setText("" + center.real());
        centerImagField.setText("" + center.imaginary());
    }

    public void setZoomLevel(double zoom) {
        zoomField.setText("" + zoom);
    }


    // Parameter getters

    public int getImageWidth() { return widthField.get(); }
    public int getImageHeight() { return heightField.get(); }
    public double getCenterReal() { return centerRealField.get(); }
    public double getCenterImaginary() { return centerImagField.get(); }
    public double getZoom() { return zoomField.get(); }
    public int getMaxN() { return maxNField.get(); }


    private void setDefaultValues() {
        widthField.setText("" + 500);
        heightField.setText("" + 500);
        centerRealField.setText("" + -0.5);
        centerImagField.setText("" + 0);
        zoomField.setText("" + 2.5);
        maxNField.setText("" + 100);
    }

    private JPanel controlsPanel(JButton selectViewButton) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.add(GUI.titledComponent(widthField, "Width"));
        panel.add(GUI.titledComponent(heightField, "Height"));
        panel.add(GUI.titledComponent(centerRealField, "Center real part"));
        panel.add(GUI.titledComponent(centerImagField, "Center imaginary part"));
        panel.add(GUI.titledComponent(zoomField, "Zoom"));
        panel.add(GUI.titledComponent(maxNField, "Max N"));
        panel.add(selectViewButton);
        panel.add(renderButton());
        panel.add(hideButton());
        JPanel border = new JPanel(new GridLayout());
        Border b = BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10), BorderFactory.createLoweredBevelBorder()
        );
        border.setBorder(b);
        border.setBackground(color);
        border.add(panel);
        return border;
    }

    private JButton renderButton() {
        JButton button = new JButton("Render image");
        button.addActionListener(e -> imageRenderer.run());
        return button;
    }

    private JButton hideButton() {
        JButton button = new JButton("Hide");
        button.addActionListener(e -> setVisible(false));
        return button;
    }

}
