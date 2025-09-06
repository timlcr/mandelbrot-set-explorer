package gui;

import image.MandelbrotImage;
import util.Complex;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// TODO: think about putting ImagePanel in its own file

/**
 * Displays a MandelbrotImage, and information about the location of the zoom.
 */
public class ImageDisplay extends JPanel {

    protected MandelbrotImage image;

    protected final JPanel imageArea = new JPanel(new GridBagLayout());
    protected final ImagePanel imagePanel = imagePanel();
    protected final ZoomLabel zoomLabel;

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

    public ImagePanel getImagePanel() { return imagePanel; }

    /**
     * Creates the ImagePanel to be used by this ImageDisplay.
     * @return the ImagePanel to be used by this ImageDisplay
     */
    protected ImagePanel imagePanel() { return new ImagePanel(); }

    /**
     * Class for the panel the image is drawn onto. Its preferred size is always the maximum
     * size while remaining inside the imageArea panel, and maintaining the aspect ratio of the image
     * being displayed.
     */
    public class ImagePanel extends JPanel {

        private int selectionRectX;
        private int selectionRectY;
        private int selectionRectWidth;
        private int selectionRectHeight;

        /**
         * Computes the value of the complex point corresponding to the pixel at
         * <code>(x, y)</code>
         * @param x the x coordinate of the chosen pixel
         * @param y the y coordinate of the chosen pixel
         * @return the value of the complex point corresponding to the pixel at
         * <code>(x, y)</code>
         */ // TODO: rewrite this method to find the indices to call image.numAt
        public Complex numAt(int x, int y) {
            double realRange = image.zoom / getHeight() * getWidth();
            double minReal = image.center.real() - realRange / 2d;
            double minImag = image.center.imaginary() - image.zoom / 2d;
            double real = minReal + (realRange / getWidth() * x);
            double imag = minImag + (image.zoom / getHeight() * (getHeight() - y));
            return new Complex(real, imag);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image == null) return;
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            drawSelectionRect(g);
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

        /**
         * Sets the size and position of the selection rectangle which gets drawn over the image
         * @param x rectangle x coord
         * @param y rectangle y coord
         * @param width rectangle width
         * @param height rectangle height
         */
        public void setSelectionRect(int x, int y, int width, int height) {
            selectionRectX = x;
            selectionRectY = y;
            selectionRectWidth = width;
            selectionRectHeight = height;
        }

        private void drawSelectionRect(Graphics g) {
            if (selectionRectWidth == 0 || selectionRectHeight == 0) return;
            g.setColor(Color.RED);
            g.drawRect(selectionRectX, selectionRectY, selectionRectWidth, selectionRectHeight);
        }

    }

}
