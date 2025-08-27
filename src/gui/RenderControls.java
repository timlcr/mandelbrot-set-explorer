package gui;

import image.MandelbrotImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

public class RenderControls extends JDialog {

    private final ValueField<Integer> widthField = ValueField.intField(500, 5);
    private final ValueField<Integer> heightField = ValueField.intField(500, 5);
    private final ValueField<Double> centerRealField = ValueField.doubleField(-0.5, 5);
    private final ValueField<Double> centerImagField = ValueField.doubleField(0, 5);
    private final ValueField<Double> zoomField = ValueField.doubleField(2.5, 5);
    private final ValueField<Integer> maxNField = ValueField.intField(100, 5);

    private final Runnable imageRenderer;

    public RenderControls(Runnable imageRenderer, MandelbrotImage image) {
        this.imageRenderer = imageRenderer;
        setTitle("Render Controls");
        setMinimumSize(new Dimension(200, 350));
        add(controlsPanel(), BorderLayout.CENTER);
        setImage(image);
        setLabels();
    }

    public void setImage(MandelbrotImage image) {
        if (image == null) {
            setDefaultValues();
            return;
        }
        widthField.setText("" + image.getWidth());
        heightField.setText("" + image.getHeight());
        centerRealField.setText("" + image.center.real());
        centerImagField.setText("" + image.center.imaginary());
        zoomField.setText("" + image.zoom);
        maxNField.setText("" + image.maxN);
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

    private JPanel controlsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));
        panel.add(widthField);
        panel.add(heightField);
        panel.add(centerRealField);
        panel.add(centerImagField);
        panel.add(zoomField);
        panel.add(maxNField);
        panel.add(renderButton());
        panel.add(hideButton());
        return panel;
    }

    private void setLabels() {
        Border empty = BorderFactory.createEmptyBorder();
        widthField.setBorder(BorderFactory.createTitledBorder(empty, "Image width"));
        heightField.setBorder(BorderFactory.createTitledBorder(empty, "Image height"));
        centerRealField.setBorder(BorderFactory.createTitledBorder(empty, "Center real"));
        centerImagField.setBorder(BorderFactory.createTitledBorder(empty, "Center imag"));
        zoomField.setBorder(BorderFactory.createTitledBorder(empty, "Zoom level"));
        maxNField.setBorder(BorderFactory.createTitledBorder(empty, "Max N"));
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
