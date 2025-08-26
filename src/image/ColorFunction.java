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
        ColorFunction cf = switch (type) {
            case BLACK_AND_WHITE -> (val, params) ->
                    val.escapeIter() < Integer.MAX_VALUE ? Color.WHITE : Color.BLACK;
        };

        return (val, params) -> {
            Color c = cf.apply(val, params);
            if (!params.renderDistEst()) return c;
            return applyDistanceEstimate(c, val.distEst(), params.maxDistRendered());
        };
    }

    static Color applyDistanceEstimate(Color c, double distEst, double maxDistRendered) {
        if(maxDistRendered <= 0 || distEst > maxDistRendered) return c;
        float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
        hsb[2] *= (float) (distEst / maxDistRendered);
        return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
    }

}
