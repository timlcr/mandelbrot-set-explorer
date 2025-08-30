package image;

import algorithm.Algorithm;
import algorithm.RepresentationValue;
import persistence.MandelbrotImageData;
import util.Complex;

import javax.swing.SwingUtilities;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

/**
 * A visual representation of the Mandelbrot Set stored in a BufferedImage object.
 * Each pixel corresponds to a point in the complex plane. The colour of each pixel is determined by
 * information gathered after running an algorithm on the corresponding complex point.
 */
public class MandelbrotImage extends BufferedImage {

    protected final RepresentationValue[][] array;

    public final Complex center;
    public final double zoom;
    public final int maxN;

    protected ColorFunctionType colorFuncType;
    protected ColorFunction colorFunction;
    protected ColorFunctionParameters colorFuncParams;

    private Consumer<Double> progressObserver;

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
     * @param colorFunc the ColorFunction used to determine the colour of each pixel
     * @param colorFuncParams the parameters used by <code>colorFunction</code>
     */
    public MandelbrotImage(
            int width, int height, Complex center, double zoom, int maxN,
            ColorFunctionType colorFunc, ColorFunctionParameters colorFuncParams
    ) {
        this(width, height, center, zoom, maxN);
        assertArgumentsLegal(width, height, center, zoom, maxN, colorFunc, colorFuncParams);
        setColorFunction(colorFunc);
        setColorFuncParams(colorFuncParams);
        computeRepresentationValues();
        colorImage();
    }

    /**
     * Creates a mandelbrot image using the specified parameters, with a progress observer
     * reporting image generation progress as a double value from 0 to 1.
     */
    public MandelbrotImage(
            int width, int height, Complex center, double zoom, int maxN,
            ColorFunctionType colorFunc, ColorFunctionParameters colorFuncParams,
            Consumer<Double> progressObserver
    ) {
        this(width, height, center, zoom, maxN);
        assertArgumentsLegal(width, height, center, zoom, maxN, colorFunc, colorFuncParams);
        setColorFunction(colorFunc);
        setColorFuncParams(colorFuncParams);
        this.progressObserver = progressObserver;
        computeRepresentationValues();
        colorImage();
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
        this(new RepresentationValue[width][height], width, height, center, zoom, maxN);
    }

    /**
     * Private constructor for creating a MandelbrotImage with
     * its RepresentationValue array already initialised.
     */
    protected MandelbrotImage(
            RepresentationValue[][] array, int width, int height, Complex center, double zoom, int maxN
    ) {
        super(width, height, BufferedImage.TYPE_INT_RGB);
        this.array = array;
        this.center = center;
        this.zoom = zoom;
        this.maxN = maxN;
    }

    /**
     * Populates the RepresentationValue array by running the algorithm on each complex point
     * <code>c</code> which corresponds to a pixel in the image.
     */
    protected void computeRepresentationValues() {
        boolean updateProgress = progressObserver != null;
        for(int x = 0; x < getWidth(); x++) {
            for(int y = 0; y < getHeight(); y++) {
                evaluate(x, y);
            }
            if(updateProgress && x % 5 == 0) {
                progressObserver.accept((double) x / getWidth());
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

    /**
     * Sets the filament size of this image. Following this operation the maxDistRendered colour
     * property will be <code>filamentSize * zoom</code>.
     * @param filamentSize the new filament size
     */
    public void setFilamentSize(double filamentSize) {
        if (filamentSize >= 1) throw new IllegalArgumentException("Filament size must be less than 1");
        colorFuncParams.setMaxDistRendered(filamentSize * zoom);
    }

    /**
     * Returns the filament size of this MandelbrotImage
     * @return the filament size of this MandelbrotImage
     */
    public double getFilamentSize() {
        return colorFuncParams.maxDistRendered() / zoom;
    }


    // ColorFunction controls

    public void setColorFunction(ColorFunctionType colorFunc) {
        this.colorFuncType = colorFunc;
        this.colorFunction = ColorFunction.of(colorFunc);
    }

    public void setColorFuncParams(ColorFunctionParameters colorFuncParams) {
        this.colorFuncParams = colorFuncParams;
    }


    public ColorFunctionType colorFunctionType() { return colorFuncType; }
    public ColorFunctionParameters colorFuncParams() { return colorFuncParams; }

    /**
     * For serialisation.
     * @return a MandelbrotImageData object
     */
    public MandelbrotImageData data() {
        return new MandelbrotImageData(getWidth(), getHeight(), center, zoom, maxN,
                array, colorFuncType, colorFuncParams);
    }

    /**
     * Constructs a MandelbrotImage from a MandelbrotImageData object.
     * @param data the data object from which to construct the image from
     * @return a MandelbrotImage initialised with respect to the MandelbrotImageData object parameter
     */
    public static MandelbrotImage fromData(MandelbrotImageData data) {
        MandelbrotImage image = new MandelbrotImage(data.array(), data.width(), data.height(),
                data.center(), data.zoom(), data.maxN());
        image.setColorFunction(data.colorFunction());
        image.setColorFuncParams(data.colorFuncParams());
        image.colorImage();
        return image;
    }

    protected void evaluate(int x, int y) {
        Complex c = numAt(x, y);
        array[x][y] = Algorithm.run(c, c, maxN);
    }

    private void assertArgumentsLegal(
            int width, int height, Complex center, double zoom, int maxN,
            ColorFunctionType colorFunc, ColorFunctionParameters colorFuncParams
    ) {
        boolean legal = width > 0 && height > 0 && center != null && zoom > 0 && maxN > 0
                && colorFunc != null && colorFuncParams != null;
        if (!legal) throw new IllegalArgumentException();
    }

}
