package persistence;

import algorithm.RepresentationValue;
import image.ColorFunctionParameters;
import image.ColorFunctionType;
import util.Complex;

import java.io.Serial;
import java.io.Serializable;

public record MandelbrotImageData(
        int width, int height, Complex center, double zoom, int maxN,
        RepresentationValue[][] array, ColorFunctionType colorFunction,
        ColorFunctionParameters colorFuncParams
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
