package algorithm;

import util.Complex;

/**
 * Has a static method <code>run</code>, which runs an algorithm on a complex point and returns
 * a <code>RepresentationValue</code>, which contains useful information for creating images of the Mandelbrot Set.
 */
public class Algorithm {

    public static final double ESCAPE_RADIUS = 2d;

    /**
     * Runs the Mandelbrot Set algorithm on a complex point <code>c</code>.
     * This implementation detects if <code>z</code> has entered an orbit so we may be able to
     * deduce that a point is in the set without running through <code>maxN</code> iterations.
     * @param c the complex point we are evaluating
     * @param z the initial value of z in the algorithm
     * @param maxN the maximum number of iterations allowed
     * @return a <code>RepresentationValue</code> containing the information gathered by iterating
     * <code>c</code> by the algorithm.
     */
    public static RepresentationValue run(Complex c, Complex z, int maxN) {
        Complex zSlow = z;
        Complex dz = Complex.ZERO;
        int i = 0;
        while(i < maxN) {
            dz = z.times(dz).times(2).plus(Complex.ONE);
            z = z.squared().plus(c);
            i++;
            if(i % 2 == 0) {
                zSlow = zSlow.squared().plus(c);
            }
            if(pointEscaped(z)) return new RepresentationValue(z, i, distEst(z, dz), false);
            if(z.equals(zSlow)) return new RepresentationValue(z, Integer.MAX_VALUE, 0, true);
        }
        return new RepresentationValue(z, Integer.MAX_VALUE, 0, false);
    }

    /**
     * Determines if a point is outside the escape radius. Avoids using a square root for
     * optimisation reasons.
     * @param z the points we are checking
     * @return true if <code>z</code> has escaped
     */
    private static boolean pointEscaped(Complex z) {
        return z.real() * z.real() + z.imaginary() * z.imaginary() > ESCAPE_RADIUS * ESCAPE_RADIUS;
    }

    /**
     * Computes the distance estimate
     */
    private static double distEst(Complex z, Complex dz) {
        double zMag = z.abs();
        double dzMag = dz.abs();
        return Math.log(zMag * zMag) * zMag / dzMag;
    }
}
