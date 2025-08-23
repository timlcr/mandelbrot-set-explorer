package image;

import algorithm.Algorithm;
import algorithm.RepresentationValue;
import util.Complex;

import java.awt.image.BufferedImage;

/**
 * A visual representation of the Mandelbrot Set stored in a BufferedImage object.
 * Each pixel corresponds to a point in the complex plane. The colour of each pixel is determined by
 * information gathered after running an algorithm on the corresponding complex point.
 */
public class MandelbrotImage extends BufferedImage {

    private final RepresentationValue[][] array;

    private final Complex center;
    private final double zoom;
    private final int maxN;

    private ColorFunction colorFunction;

    /**
     * Constructor
     * @param width the width of the image in pixels
     * @param height the height of the image in pixels
     * @param center the complex point the image is centered on
     * @param zoom the level of zoom. This is equal to the imaginary range of the area
     *             of the complex plane covered by the image.
     * @param maxN the maximum number of iterations allowed when running the algorithm on each point
     */
    public MandelbrotImage(int width, int height, Complex center, double zoom, int maxN) {
        super(width, height, BufferedImage.TYPE_INT_RGB);
        array = new RepresentationValue[width][height];
        this.center = center;
        this.zoom = zoom;
        this.maxN = maxN;
    }

    /**
     * Populates the RepresentationValue array by running the algorithm on each complex point
     * <code>c</code> which corresponds to a pixel in the image.
     */
    public void computeRepresentationValues() {
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                Complex c = numAt(x, y);
                array[x][y] = Algorithm.run(c, c, maxN);
            }
        }
    }

    /**
     * Computes the value of the complex point corresponding to the pixel at
     * <code>(x, y)</code>
     * @param x the x coordinate of the chosen pixel
     * @param y the y coordinate of the chosen pixel
     * @return the value of the complex point corresponding to the pixel at
     * <code>(x, y)</code>
     */
    public Complex numAt(int x, int y) {
        double realRange = zoom / getHeight() * getWidth();
        double minReal = center.real() - realRange / 2d;
        double minImag = center.imaginary() - zoom / 2d;
        double real = minReal + (realRange / getWidth() * x);
        double imag = minImag + (zoom / getHeight() * (getHeight() - y));
        return new Complex(real, imag);
    }

}
