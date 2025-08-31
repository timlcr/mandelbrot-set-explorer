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
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

/**
 * A dialog box containing input fields to control the colouring of a displayed MandelbrotImage.
 */
public class ColorControls extends JDialog {

    private final Color color;

    private final Selector<ColorFunctionType> colorFunctionSelector = new Selector<>(ColorFunctionType.values());
    private final Selector<Gradient> gradientSelector = new Selector<>(Gradient.ALL);
    private final ValueField<Double> fluxField = ValueField.doubleField(5, 7);
    private final JRadioButton showFilamentButton = new JRadioButton("Show filament");
    private final ValueField<Double> filamentSizeField = ValueField.doubleField(0.002, 10);

    private final Runnable repainter;

    private boolean disableActionListener = false;

    /**
     * Creates a ColorControls dialog. The input fields can be initialised with colouring
     * parameters from a given image.
     * @param repainter repaints the panel the image is displayed on
     * @param image The input fields will display parameters taken from this image. If null
     *              they wil display the default values.
     */
    public ColorControls(Runnable repainter, String title, Color color, MandelbrotImage image) {
        this.repainter = repainter;
        this.color = color;
        setTitle(title);
        setMinimumSize(new Dimension(250, 350));
        add(controlsPanel(), BorderLayout.CENTER);
        setImage(image);
        setActionListener();
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
            showFilamentButton.setSelected(image.colorFuncParams().renderDistEst());
            filamentSizeField.setText("" + image.getFilamentSize());
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
    public ColorFunctionParameters getColorFuncParams(double zoom) {
        return new ColorFunctionParameters(
                gradientSelector.get(), fluxField.get(),
                showFilamentButton.isSelected(), filamentSizeField.get() * zoom
        );
    }


    private void setDefaultValues() {
        colorFunctionSelector.getModel().setSelectedItem(ColorFunctionType.CONTINUOUS);
        gradientSelector.getModel().setSelectedItem(Gradient.HUE);
        fluxField.setText(""+5);
        showFilamentButton.setSelected(false);
        filamentSizeField.setText(""+0.002);
    }

    private JPanel controlsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(GUI.titledComponent(colorFunctionSelector, "Color function"));
        panel.add(GUI.titledComponent(gradientSelector, "Gradient"));
        panel.add(GUI.titledComponent(fluxField, "Flux"));
        panel.add(showFilamentButton);
        panel.add(GUI.titledComponent(filamentSizeField, "Filament size"));
        panel.add(recolorButton());
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

    private void setActionListener() {
        ActionListener al = e -> {
            if (!disableActionListener) repainter.run();
        };
        colorFunctionSelector.addActionListener(al);
        gradientSelector.addActionListener(al);
        fluxField.addActionListener(al);
        showFilamentButton.addActionListener(al);
        filamentSizeField.addActionListener(al);
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
