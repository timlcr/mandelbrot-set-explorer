package image;

import algorithm.RepresentationValue;

import java.awt.Color;
import java.util.function.BiFunction;

/**
 * Used to determine the colour of each pixel in a <code>MandelbrotImage</code> by being
 * evaluated on each RepresentationValue in the array stored in a MandelbrotImage.
 */
public interface ColorFunction extends BiFunction<RepresentationValue, ColorFunctionParameters, Color> {

    /**
     * Defines the ColorFunctions used by gui.
     * Returns a ColorFunction specified by the type.
     * @param type the type of ColorFunction being retrieved
     * @return a ColorFunction of the type <code>type</code>
     */
    static ColorFunction of(ColorFunctionType type) {
        return switch (type) {
            case BLACK_AND_WHITE -> (val, params) ->
                    val.escapeIter() < Integer.MAX_VALUE ? Color.WHITE : Color.BLACK;
        };
    }

}
