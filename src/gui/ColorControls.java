package gui;

import image.ColorFunction;
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

public class ColorControls extends JDialog {

    private MandelbrotImage image;

    private final Selector<ColorFunctionType> colorFunctionSelector = new Selector<>(ColorFunctionType.values());
    private final Selector<Gradient> gradientSelector = new Selector<>(Gradient.ALL);
    private final ValueField<Double> fluxField = ValueField.doubleField(0.4, 7);
    private final JRadioButton renderDistEstButton = new JRadioButton("Dist Est");
    private final ValueField<Double> maxDistEstField = ValueField.doubleField(0.01, 10);

    private final Runnable repainter;

    public ColorControls(Runnable repainter, MandelbrotImage image) {
        this.repainter = repainter;
        setTitle("Colour controls");
        setMinimumSize(new Dimension(200, 350));
        add(controlsPanel(), BorderLayout.CENTER);
        setImage(image);
        setupInputFields();
    }

    public void setImage(MandelbrotImage image) {
        this.image = image;
        if (image == null) {
            setDefaultValues();
            return;
        }
        colorFunctionSelector.getModel().setSelectedItem(image.colorFunctionType());
        gradientSelector.getModel().setSelectedItem(image.colorFuncParams().gradient());
        fluxField.setText(""+image.colorFuncParams().flux());
        renderDistEstButton.setSelected(image.colorFuncParams().renderDistEst());
        maxDistEstField.setText(""+image.colorFuncParams().maxDistRendered());
    }


    // Parameter getters

    public ColorFunctionType getColorFunction() {
        return colorFunctionSelector.get();
    }

    public ColorFunctionParameters getColorFuncParams() {
        return new ColorFunctionParameters(
                gradientSelector.get(), fluxField.get(),
                renderDistEstButton.isSelected(), maxDistEstField.get()
        );
    }


    private void setDefaultValues() {
        colorFunctionSelector.getModel().setSelectedItem(ColorFunctionType.BLACK_AND_WHITE);
        gradientSelector.getModel().setSelectedItem(Gradient.HUE);
        fluxField.setText(""+0.4);
        renderDistEstButton.setSelected(false);
        maxDistEstField.setText(""+0.01);
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
        ActionListener al = e -> repainter.run();
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
