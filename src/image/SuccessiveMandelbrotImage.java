package image;

import algorithm.RepresentationValue;
import util.Complex;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * A MandelbrotImage which is rendered using successive refinement for efficiency and gui responsiveness.
 */
public class SuccessiveMandelbrotImage extends MandelbrotImage {

    private int chunkSize = 16;
    private boolean done = false;

    public void refine() {
        if (done) return;
        if (chunkSize == 1) done = true;
        scan();
        colorImage();
        chunkSize /= 2;
    }

    /**
     * Constructor
     * @param center the complex point the image is centered on
     * @param zoom the level of zoom. This is equal to the imaginary range of the area
     *             of the complex plane covered by the image.
     * @param maxN the maximum number of iterations allowed when running the algorithm on each point
     */
    public SuccessiveMandelbrotImage(
            Complex center, double zoom, int maxN,
            ColorFunctionType colorFunction, ColorFunctionParameters colorFuncParams
    ) {
        super(512, 512, center, zoom, maxN);
        if(colorFunction == null || colorFuncParams == null) // TODO: create public MandelbrotImage constructor with color parameters with this null check in it.
            throw new IllegalArgumentException("colorFunction and colorFuncParams cannot be null");
        setColorFunction(colorFunction);
        setColorFuncParams(colorFuncParams);
    }

    /**
     * Evaluates only the necessary points specified by <code>chunkSize</code>.
     * Points which most likely are part of a large block of colour are identified
     * using <code>alNeighboursSame(parentX, parentY)</code>, and their iteration is skipped.
     */
    protected void scan() {
        for (int x = 0; x <= getWidth() - chunkSize; x += chunkSize) {
            for (int y = 0; y <= getHeight() - chunkSize; y += chunkSize) {
                if(chunkSize == 16) {
                    evaluate(x, y);
                    continue;
                }
                int parentX = x % (2 * chunkSize) == 0 ? x : x - chunkSize;
                int parentY = y % (2 * chunkSize) == 0 ? y : y - chunkSize;
                if ((x == parentX && y == parentY)) continue;
                if (allNeighboursSame(parentX, parentY)) {
                    array[x][y] = array[parentX][parentY];
                    continue;
                }
                evaluate(x, y);
            }
        }
    }

    /**
     * Colours the image using the current <code>chunkSize</code>.
     */
    public void colorImage() {
        chunkSize = Math.max(chunkSize, 1);
        Graphics2D g = this.createGraphics();
        for (int x = 0; x <= getWidth() - chunkSize; x += chunkSize) {
            for (int y = 0; y <= getHeight() - chunkSize; y += chunkSize) {
                RepresentationValue val = array[x][y];
                if (val == null) throw new NullPointerException(
                        "Null found in RepresentationValue array at " + x + ", " + y
                );
                Color color = colorFunction.apply(val, colorFuncParams);
                g.setColor(color);
                g.fillRect(x, y, chunkSize, chunkSize);
            }
        }
    }

    /**
     * Determines whether a point likely lies in a large block of the same colour for the want
     * of efficient image generation. The coordinates of the 'parent' of the point are used.
     * The parent has the coordinates of the top left corner of the chunk from the previous scan
     * which contains the point.
     * @param parentX the x coordinate of the 'parent' of the point being checked
     * @param parentY the y coordinate of the 'parent' of the point being checked
     * @return true if all 8 neighbours of (parentX, parentY) have the same escapeIter as (parentX, parentY)
     */
    private boolean allNeighboursSame(int parentX, int parentY) {
        int current = array[parentX][parentY].escapeIter();
        for (int x = parentX - 2 * chunkSize; x <= parentX + 2 * chunkSize; x += 2 * chunkSize) {
            for (int y = parentY - chunkSize * 2; y <= parentY + 2 * chunkSize; y += 2 * chunkSize) {
                if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) return false;
                if (array[x][y].escapeIter() != current) return false;
            }
        }
        return true;
    }

}
