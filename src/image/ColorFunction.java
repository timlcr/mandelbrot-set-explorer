package image;

import algorithm.RepresentationValue;

import java.awt.Color;
import java.util.function.BiFunction;

public interface ColorFunction extends BiFunction<RepresentationValue, ColorFunctionParameters, Color> {

    ColorFunction BLACK_AND_WHITE = (val, params) ->
            val.escapeIter() < Integer.MAX_VALUE ? Color.WHITE : Color.BLACK;
}
