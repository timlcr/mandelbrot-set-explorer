package image;

import java.io.Serial;
import java.io.Serializable;

/**
 * Wrapper class for 4 parameters used by <code>ColorFunction</code> instances. <p>
 *
 * <code>gradient</code> - the colour gradient the ColorFunction selects colours from <br>
 * <code>flux</code> - the level of fluctuation of colour in the image <br>
 * <code>renderDistEst</code> - if true, the distance estimate will be shown on the image <br>
 * <code>maxDistRendered</code> - the maximum value of distance estimate which will be shown <p>
 *
 * Has public getters and setters.
 */
public class ColorFunctionParameters implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Gradient gradient;
    private double flux;
    private boolean renderDistEst;
    private double maxDistRendered;

    /**
     * Constructor
     */
    public ColorFunctionParameters(Gradient gradient, double flux, boolean renderDistEst, double maxDistRendered) {
        this.gradient = gradient;
        this.flux = flux;
        this.renderDistEst = renderDistEst;
        this.maxDistRendered = maxDistRendered;
    }

    public Gradient gradient() { return gradient; }
    public double flux() { return flux; }
    public boolean renderDistEst() { return renderDistEst; }
    public double maxDistRendered() { return maxDistRendered; }

    public void setGradient(Gradient gradient) { this.gradient = gradient; }
    public void setFlux(double flux) { this.flux = flux; }
    public void setRenderDistEst(boolean renderDistEst) { this.renderDistEst = renderDistEst; }
    public void setMaxDistRendered(double maxDistRendered) { this.maxDistRendered = maxDistRendered; }

    /**
     * An instance containing the default ColorFunction parameters.
     * @return the default ColorFunction parameters
     */
    public static ColorFunctionParameters defaultParameters() {
        return new ColorFunctionParameters(Gradient.HUE, 0.4, true, 3e-2);
    }
}
