package image;

import algorithm.Algorithm;
import algorithm.RepresentationValue;
import util.Complex;

import java.awt.image.BufferedImage;
import java.util.Scanner;

/**
 * A visual representation of the Mandelbrot Set stored in a BufferedImage object.
 * Each pixel corresponds to a point in the complex plane. The colour of each pixel is determined by
 * information gathered after running an algorithm on the corresponding complex point.
 */
public class MandelbrotImage extends BufferedImage {

    private final RepresentationValue[][] array;

    public final Complex center;
    public final double zoom;
    public final int maxN;

    private ColorFunction colorFunction = ColorFunction.BLACK_AND_WHITE;
    private ColorFunctionParameters colorFuncParams = ColorFunctionParameters.defaultParameters();

    /**
     * Creates a MandelbrotImage with the specified dimensions, centred on the specified
     * window of the complex plane, generated using the specified maximum number of iterations, and
     * coloured with the specified function and parameters.
     * @param width the width of the image in pixels
     * @param height the height of the image in pixels
     * @param center the complex point the image is centered on
     * @param zoom the level of zoom. This is equal to the imaginary range of the area
     *             of the complex plane covered by the image.
     * @param maxN the maximum number of iterations allowed when running the algorithm on each point
     * @param colorFunction the ColorFunction used to determine the colour of each pixel
     * @param colorFuncParams the parameters used by <code>colorFunction</code>
     * @return An image of the Mandelbrot Set
     */
    public static MandelbrotImage of(
            int width, int height, Complex center, double zoom, int maxN,
            ColorFunction colorFunction, ColorFunctionParameters colorFuncParams
    ) {
        MandelbrotImage img = new MandelbrotImage(width, height, center, zoom, maxN);
        img.setColorFunction(colorFunction);
        img.setColorFuncParams(colorFuncParams);
        img.computeRepresentationValues();
        img.colorImage();
        return img;
    }

    /**
     * Constructor
     * @param width the width of the image in pixels
     * @param height the height of the image in pixels
     * @param center the complex point the image is centered on
     * @param zoom the level of zoom. This is equal to the imaginary range of the area
     *             of the complex plane covered by the image.
     * @param maxN the maximum number of iterations allowed when running the algorithm on each point
     */
    protected MandelbrotImage(int width, int height, Complex center, double zoom, int maxN) {
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
    protected void computeRepresentationValues() {
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                Complex c = numAt(x, y);
                array[x][y] = Algorithm.run(c, c, maxN);
            }
        }
    }

    /**
     * Colours the image by using the colorFunction with inputs from the RepresentationValue array
     * to determine the colour of each pixel.
     */
    public void colorImage() {
        if(colorFunction == null) throw new RuntimeException("Color function not set");
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                RepresentationValue val = array[x][y];
                if(val == null) throw new NullPointerException(
                        "Null found in RepresentationValue array at " + x + ", " + y
                );
                setRGB(x, y, colorFunction.apply(val, colorFuncParams).getRGB());
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


    // ColorFunction controls

    public void setColorFunction(ColorFunction colorFunc) {
        this.colorFunction = colorFunc;
    }

    public void setColorFuncParams(ColorFunctionParameters colorFuncParams) {
        this.colorFuncParams = colorFuncParams;
    }

    public void setGradient(Gradient gradient) {
        colorFuncParams.setGradient(gradient);
    }

    public void setFlux(double flux) {
        colorFuncParams.setFlux(flux);
    }

    public void setRenderDistEst(boolean renderDistEst) {
        colorFuncParams.setRenderDistEst(renderDistEst);
    }

    public void setMaxDistRendered(double maxDistRendered) {
        colorFuncParams.setMaxDistRendered(maxDistRendered);
    }

}
