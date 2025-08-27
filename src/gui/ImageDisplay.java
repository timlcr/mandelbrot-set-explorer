package gui;

import image.MandelbrotImage;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * Displays a MandelbrotImage, and information about the location of the zoom.
 */
public class ImageDisplay extends JPanel {

    private MandelbrotImage image;

    private final JPanel imageArea = new JPanel(new GridBagLayout());
    private final JPanel imagePanel = imagePanel();
    private final ZoomLabel zoomLabel;

    private final Border imageBorder = new LineBorder(Color.BLACK);

    /**
     * Constructor
     * @param image the image the ImageDisplay is initialised with
     */
    public ImageDisplay(MandelbrotImage image) {
        super(new BorderLayout());
        this.image = image;

        add(imageArea, BorderLayout.CENTER);
        imageArea.add(imagePanel, new GridBagConstraints());

        zoomLabel = new ZoomLabel(image);
        add(zoomLabel, BorderLayout.SOUTH);
    }

    /**
     * Constructor with no image to be initialised with
     */
    public ImageDisplay() { this(null); }

    /**
     * Starts displaying the specified image
     * @param image the image to be displayed
     */
    public void setImage(MandelbrotImage image) {
        this.image = image;
        if (image != null) imagePanel.setBorder(imageBorder);
        else imagePanel.setBorder(BorderFactory.createEmptyBorder());
        zoomLabel.update(image);
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    /**
     * Returns the image being displayed
     * @return the image being displayed
     */
    public MandelbrotImage image() { return image; }

    private JPanel imagePanel() {
        return  new JPanel() {

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (image == null) return;
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }

            /**
             * Lets the image panel be the largest possible size while remaining inside the
             * imageArea panel and maintaining the same aspect ratio of the image being displayed.
             * @return the preferred size of this component as described above
             */
            @Override
            public Dimension getPreferredSize() {
                int w = imageArea.getWidth();
                int h = imageArea.getHeight();
                if (image == null || w == 0 || h == 0) return super.getSize();
                double aspect = image.getWidth() / (double) image.getHeight();
                if ((double) w / h > aspect) {
                    return new Dimension((int) (h * aspect), h);
                } else {
                    return new Dimension(w, (int) (w / aspect));
                }
            }
        };
    }

}
