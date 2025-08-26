package persistence;

import algorithm.RepresentationValue;
import image.ColorFunctionParameters;
import image.ColorFunctionType;
import util.Complex;

/**
 * Contains all necessary information to save a MandelbrotImage to disc so that it can be loaded
 * into the program later and altered further.
 */
public record MandelbrotImageData(
        int width, int height, Complex center, double zoom, int maxN,
        RepresentationValue[][] array, ColorFunctionType colorFunction,
        ColorFunctionParameters colorFuncParams
) {}
