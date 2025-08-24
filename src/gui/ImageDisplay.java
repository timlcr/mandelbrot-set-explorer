package gui;

import image.ColorFunction;
import image.ColorFunctionParameters;
import image.MandelbrotImage;
import util.Complex;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class ImageDisplay extends JPanel {

    private MandelbrotImage image;

    private final JPanel imageArea = new JPanel(new GridBagLayout());
    private final JPanel imagePanel = imagePanel();

    public ImageDisplay() {
        super(new BorderLayout());
        add(imageArea, BorderLayout.CENTER);
        imageArea.add(imagePanel, new GridBagConstraints());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(image1button());
        buttonPanel.add(image2button());
        buttonPanel.add(image3button());
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setImage(MandelbrotImage image) {
        this.image = image;
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    private JPanel imagePanel() {
        JPanel panel =  new JPanel() {

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image == null) return;
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }

            @Override
            public Dimension getPreferredSize() {
                if (image == null) return new Dimension(200, 200);
                double aspect = image.getWidth() / (double) image.getHeight();
                int w = imageArea.getWidth();
                int h = imageArea.getHeight();
                if (w == 0 || h == 0) return super.getPreferredSize();
                if ((double) w / h > aspect) {
                    return new Dimension((int) (h * aspect), h);
                } else {
                    return new Dimension(w, (int) (w / aspect));
                }
            }
        };
        panel.setBorder(new LineBorder(Color.BLACK));
        return panel;
    }

    MandelbrotImage image1 = MandelbrotImage.of(500, 500, Complex.DEFAULT_CENTER, 2.5, 100,
            ColorFunction.BLACK_AND_WHITE, ColorFunctionParameters.defaultParameters());

    MandelbrotImage image2 = MandelbrotImage.of(750, 500, Complex.DEFAULT_CENTER, 2.5, 100,
            ColorFunction.BLACK_AND_WHITE, ColorFunctionParameters.defaultParameters());

    MandelbrotImage image3 = MandelbrotImage.of(500, 1000, Complex.DEFAULT_CENTER, 2.5, 100,
            ColorFunction.BLACK_AND_WHITE, ColorFunctionParameters.defaultParameters());

    private JButton image1button() {
        JButton button = new JButton("Image 1");
        button.addActionListener(e -> setImage(image1));
        return button;
    }

    private JButton image2button() {
        JButton button = new JButton("Image 2");
        button.addActionListener(e -> setImage(image2));
        return button;
    }

    private JButton image3button() {
        JButton button = new JButton("Image 3");
        button.addActionListener(e -> setImage(image3));
        return button;
    }

}
