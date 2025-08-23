package image;

import algorithm.RepresentationValue;
import util.Complex;

import java.awt.image.BufferedImage;

public class MandelbrotImage extends BufferedImage {

    private final RepresentationValue[][] array;

    private final Complex center;
    private final double zoom;
    private final int maxN;

    private ColorFunction colorFunction;


    public MandelbrotImage(int width, int height, Complex center, double zoom, int maxN) {
        super(width, height, BufferedImage.TYPE_INT_RGB);
        array = new RepresentationValue[width][height];
        this.center = center;
        this.zoom = zoom;
        this.maxN = maxN;
    }


}
