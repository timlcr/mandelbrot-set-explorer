package image;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * An ArrayList of colours. Gradients can be constructed from a list of colour stops
 * (<code>Gradient.ColorStop</code>), or from a <code>Function< Float, Color ></code>
 */
public class Gradient extends ArrayList<Color> {

    public final String name;
    protected final List<Gradient.ColorStop> stops;

    /**
     * Constructs a gradient which smoothly transitions between the provided colour stops.
     * @param name the name of the gradient
     * @param samples the number of samples taken, i.e. the size of the ArrayList
     * @param stops the colour stops
     */
    public Gradient(String name, int samples, List<Gradient.ColorStop> stops) {
        if(stops.size() < 2) throw new IllegalArgumentException("Gradient must have at least 2 stops");
        this.name = name;
        this.stops = stops;
        for(int i = 0; i < samples; i++) { add(get((float) i / samples)); }
    }

    /**
     * Constructs a gradient from a function. The function maps floats between 0 and 1 to colours.
     * @param name the name of the gradient
     * @param samples the number of samples taken, i.e. the size of the ArrayList
     * @param f a Function from floats between 0 and 1 to Colors
     * @return the resulting gradient
     */
    public static Gradient fromFunction(String name, int samples, Function<Float, Color> f) {
        return new Gradient(name, samples, f) {

            @Override
            public Gradient resample(int samples) { return fromFunction(name, samples, f); }

            @Override  protected Color get(float t) { return f.apply(t); }

        };
    }

    protected Gradient(String name, int samples, Function<Float, Color> f) {
        this.name = name;
        this.stops = null;
        for(int i=0; i<samples; i++) { add(f.apply((float) i / samples)); }
    }

    protected Gradient(String name, List<Color> colors) {
        super(colors);
        this.name = name;
        this.stops = null;
    }

    /**
     * Resamples the gradient with the specified number of samples.
     * @param samples the number of samples taken, i.e. the size of the ArrayList
     * @return this gradient but with size <code>samples</code>
     */
    public Gradient resample(int samples) {
        return new Gradient(name, samples, stops);
    }

    protected Color get(float t) {
        t %= 1f;
        for(int i=0; i<stops.size(); i++) {
            ColorStop sA = stops.get(i);
            ColorStop sB = stops.get(i + 1);
            if(sA.position <= t && sB.position > t) {
                t = (t - sA.position) / (sB.position - sA.position);
                return interpolateRGB(sA.color(), sB.color(), t);
            }
        }
        throw new RuntimeException("error");
    }

    /**
     * A colour to be interpolated in a gradient.
     * @param color the colour of this ColorStop
     * @param position the position of this colour in the gradient (between 0 and 1)
     */
    public record ColorStop(Color color, float position) {
        public ColorStop {
            if(position < 0 || position > 1)
                throw new IllegalArgumentException("position must be between 0 and 1");
        }
    }

    /**
     * Interpolates between 2 colours to find a colour between them.
     * @param color1 the first colour
     * @param color2 the second colour
     * @param t how far to go from the first colour to the second (e.g. <code>t = 0.5</code>)
     *          will return the colour with RGB values which are the average of <code>color1</code>
     *          and <code>color2</code>
     * @return the interpolated colour
     */
    public static Color interpolateRGB(Color color1, Color color2, float t) {
        int r = (int) ((1-t) * color1.getRed() + t * color2.getRed());
        int g = (int) ((1-t) * color1.getGreen() + t * color2.getGreen());
        int b = (int) ((1-t) * color1.getBlue() + t * color2.getBlue());
        return new Color(r, g, b);
    }

    @Override public String toString() { return name; }


    // Gradients

    public static final Gradient RASTA = new Gradient(
            "Rasta", 256, List.of(
            new ColorStop(Color.decode("#1E9600"), 0),
            new ColorStop(Color.decode("#FFF200"), 0.5f),
            new ColorStop(Color.decode("#FF0000"), 1)
    ));

    public static final Gradient ARGON = new Gradient(
            "Argon", 256, List.of(
            new ColorStop(Color.decode("#fdeff9"), 0f),
            new ColorStop(Color.decode("#EC38BC"), 0.33f),
            new ColorStop(Color.decode("#7303c0"), 0.66f),
            new ColorStop(Color.decode("#03001e"), 1f)
    ));

    public static final Gradient KING_YNA = new Gradient(
            "King Yna", 256, List.of(
            new ColorStop(Color.decode("#FDBB2D"), 0f),
            new ColorStop(Color.decode("#b21f1f"), 0.5f),
            new ColorStop(Color.decode("#1a2a6c"), 1f)
    ));

    public static final Gradient TERMINAL = new Gradient(
            "Terminal", 256, List.of(
            new ColorStop(Color.decode("#0f9b0f"), 0),
            new ColorStop(Color.decode("#000000"), 1)
    ));

    public static final Gradient JUPITER = new Gradient(
            "Jupiter", 256, List.of(
            new ColorStop(Color.decode("#ffd89b"), 0),
            new ColorStop(Color.decode("#19547b"), 1)
    ));

    public static final Gradient HUE = fromFunction("Hue", 256, t ->
            Color.getHSBColor(t, 1, 1));


    public static final Gradient GREYSCALE = fromFunction("Grayscale", 256, t ->
            Color.getHSBColor(0, 0, 1-t));

    public static Gradient[] ALL = {HUE, RASTA, ARGON, KING_YNA, TERMINAL, JUPITER, GREYSCALE};

}
