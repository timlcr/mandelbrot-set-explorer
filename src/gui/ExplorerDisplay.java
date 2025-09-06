package gui;

import image.MandelbrotImage;
import image.SuccessiveMandelbrotImage;
import util.Complex;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Stack;

/**
 * Image display area for the explorer gui.
 */
public class ExplorerDisplay extends ImageDisplay {

    private final Stack<MandelbrotImage> imageCache;

    public ExplorerDisplay(Stack<MandelbrotImage> imageCache) {
        this.imageCache = imageCache;
        new SubzoomSelector(imagePanel, this::nextImage);
    }

    /**
     * Creates the next image and displays it. The image used is a SuccessiveMandelbrotImage,
     * which uses successive refinement to boost responsiveness and efficiency.
     * @param center the complex point the next image is centered on
     * @param zoom the zoom level of the next image
     */
    private void nextImage(Complex center, double zoom) {
        imageCache.push(image);
        int maxN = 1000;
        if (zoom < 2e-4) maxN = 2000;
        SuccessiveMandelbrotImage nextImage = new SuccessiveMandelbrotImage(
                center, zoom, maxN, image.colorFunctionType(), image.colorFuncParams()
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

}
