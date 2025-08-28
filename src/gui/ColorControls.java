package gui;

import image.ColorFunctionParameters;
import image.ColorFunctionType;
import image.Gradient;
import image.MandelbrotImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

/**
 * A dialog box containing input fields to control the colouring of a displayed MandelbrotImage.
 */
public class ColorControls extends JDialog {

    private final Selector<ColorFunctionType> colorFunctionSelector = new Selector<>(ColorFunctionType.values());
    private final Selector<Gradient> gradientSelector = new Selector<>(Gradient.ALL);
    private final ValueField<Double> fluxField = ValueField.doubleField(5, 7);
    private final JRadioButton renderDistEstButton = new JRadioButton("Dist Est");
    private final ValueField<Double> maxDistEstField = ValueField.doubleField(5e-3, 10);

    private final Runnable repainter;

    private boolean disableActionListener = false;

    /**
     * Creates a ColorControls dialog. The input fields can be initialised with colouring
     * parameters from a given image.
     * @param repainter repaints the panel the image is displayed on
     * @param image The input fields will display parameters taken from this image. If null
     *              they wil display the default values.
     */
    public ColorControls(Runnable repainter, MandelbrotImage image) {
        this.repainter = repainter;
        setTitle("Colour controls");
        setMinimumSize(new Dimension(200, 350));
        add(controlsPanel(), BorderLayout.CENTER);
        setImage(image);
        setupInputFields();
    }

    /**
     * Sets the input fields to display the colouring values of this image. If image is
     * null the default values will be displayed.
     * @param image the image the colouring values are from
     */
    public void setImage(MandelbrotImage image) {
        disableActionListener = true;
        if (image == null) {
            setDefaultValues();
        } else {
            colorFunctionSelector.getModel().setSelectedItem(image.colorFunctionType());
            gradientSelector.getModel().setSelectedItem(image.colorFuncParams().gradient());
            fluxField.setText("" + image.colorFuncParams().flux());
            renderDistEstButton.setSelected(image.colorFuncParams().renderDistEst());
            maxDistEstField.setText("" + image.colorFuncParams().maxDistRendered());
        }
        disableActionListener = false;
    }


    // Parameter getters

    /**
     * Returns the ColorFunctionType selected in the colour function selector.
     */
    public ColorFunctionType getColorFunction() {
        return colorFunctionSelector.get();
    }

    /**
     * Returns a ColorFunctionParameters object containing the colour function parameters
     * selected on this dialog.
     */
    public ColorFunctionParameters getColorFuncParams() {
        return new ColorFunctionParameters(
                gradientSelector.get(), fluxField.get(),
                renderDistEstButton.isSelected(), maxDistEstField.get()
        );
    }


    private void setDefaultValues() {
        colorFunctionSelector.getModel().setSelectedItem(ColorFunctionType.CONTINUOUS);
        gradientSelector.getModel().setSelectedItem(Gradient.HUE);
        fluxField.setText(""+5);
        renderDistEstButton.setSelected(false);
        maxDistEstField.setText(""+5e-3);
    }

    private JPanel controlsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(colorFunctionSelector);
        panel.add(gradientSelector);
        panel.add(fluxField);
        panel.add(renderDistEstButton);
        panel.add(maxDistEstField);
        panel.add(recolorButton());
        panel.add(hideButton());
        return panel;
    }

    private void setupInputFields() {
        setLabels();
        ActionListener al = e -> {
            if (!disableActionListener) repainter.run();
        };
        colorFunctionSelector.addActionListener(al);
        gradientSelector.addActionListener(al);
        fluxField.addActionListener(al);
        renderDistEstButton.addActionListener(al);
        maxDistEstField.addActionListener(al);
    }

    private void setLabels() {
        Border empty = BorderFactory.createEmptyBorder();
        colorFunctionSelector.setBorder(BorderFactory.createTitledBorder(empty, "Colour function"));
        gradientSelector.setBorder(BorderFactory.createTitledBorder(empty, "Gradient"));
        fluxField.setBorder(BorderFactory.createTitledBorder(empty, "Flux"));
        maxDistEstField.setBorder(BorderFactory.createTitledBorder(empty, "Max distance estimate rendered"));
    }

    private JButton recolorButton() {
        JButton button = new JButton("Recolor");
        button.addActionListener(e -> repainter.run());
        return button;
    }

    private JButton hideButton() {
        JButton button = new JButton("Hide");
        button.addActionListener(e -> setVisible(false));
        return button;
    }
}
