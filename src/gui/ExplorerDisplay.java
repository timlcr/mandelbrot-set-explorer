package gui;

import image.SuccessiveMandelbrotImage;
import util.Complex;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Image display area for the explorer gui.
 */
public class ExplorerDisplay extends ImageDisplay {

    @Override
    protected ImagePanel imagePanel() {
        return new ExplorerImagePanel();
    }

    /**
     * The panel which the Mandelbrot Image is drawn to.
     * Allows the user to select the next zoom area with the mouse.
     */
    class ExplorerImagePanel extends ImagePanel {

        private int pressX = 0;
        private int pressY = 0;
        private int dragX = 0;
        private int dragY = 0;
        private boolean selecting = false;

        public ExplorerImagePanel() {
            super();
            MouseAdapter mouseAdapter = mouseAdapter();
            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (selecting) {
                g.setColor(Color.RED);
                g.drawRect(pressX, pressY, dragX - pressX, dragY - pressY);
            }
        }

        /**
         * Creates the next image and displays it. The image used is a SuccessiveMandelbrotImage,
         * which uses successive refinement to boost responsiveness and efficiency.
         * @param center the complex point the next image is centered on
         * @param zoom the zoom level of the next image
         */
        private void nextImage(Complex center, double zoom) {
            SuccessiveMandelbrotImage nextImage = new SuccessiveMandelbrotImage(
                    center, zoom, 1000, image.colorFunctionType(), image.colorFuncParams()
            );
            nextImage.setFilamentSize(image.getFilamentSize());
            setImage(nextImage);
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

        private MouseAdapter mouseAdapter() {
            return new MouseAdapter() {

                @Override
                public void mousePressed(MouseEvent e) {
                    selecting = true;
                    pressX = e.getX();
                    pressY = e.getY();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    selecting = false;
                    if (Math.abs(pressY - e.getY()) < 20) return;
                    Complex press = numAt(pressX, pressY);
                    Complex release = numAt(e.getX(), e.getY());
                    Complex center = press.plus(release).times(0.5);
                    double zoom = Math.abs(press.imaginary() - release.imaginary());
                    nextImage(center, zoom);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    dragX = e.getX();
                    dragY = e.getY();
                    repaint();
                }

            };
        }

    }

}
