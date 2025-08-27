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
            case DISCRETE_BANDS -> (val, params) -> {
                Gradient grad = params.gradient();
                int index = (int) (Math.log(val.escapeIter()) * params.flux() * grad.size());
                index %= grad.size();
                return grad.get(index);
            };
            case CONTINUOUS -> (val, params) -> {
                Gradient grad = params.gradient();
                double index = (Math.log(val.smoothDwell()) * params.flux() * grad.size());
                index %= grad.size();
                int i0 = (int) Math.floor(index);
                int i1 = (i0 + 1) % grad.size();
                float t = (float) (index - i0);
                return Gradient.interpolateRGB(grad.get(i0), grad.get(i1), t);
            };
            case ODD_EVEN -> (val, params) -> {
                Gradient grad = params.gradient();
                int index = (int) (Math.log(val.escapeIter()) * params.flux() * grad.size());
                index %= grad.size();
                Color c = grad.get(index);
                float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                if(val.escapeIter() % 2 == 0) hsb[1] *= 0.67f;
                return Color.getHSBColor(hsb[0], hsb[1], hsb[2]);
            };
            case ESCAPE_DIRECTION -> (val, params) -> {
                Color c = of(ColorFunctionType.ODD_EVEN).apply(val, params);
                float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                float h = hsb[0];
                if(val.lastZ().angle() < 0) h = (h + 0.025f) % 1f;
                return Color.getHSBColor(h, hsb[1], hsb[2]);
            };
            case DWELL_ANGLE_RADIUS -> (val, params) -> {
                float h = (float) (Math.log(val.smoothDwell()) * params.flux());
                float s = (float) (val.lastZ().angle() / (2 * Math.PI) + 1);
                float b = (float) (Math.log(val.lastZ().abs()) % 1); // log may need changing
                return Color.getHSBColor(h, s, b);
            };
        };

        return (val, params) -> {
            if (val.escapeIter() == Integer.MAX_VALUE) return Color.BLACK;
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
