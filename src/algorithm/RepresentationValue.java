package algorithm;

import util.Complex;

/**
 * Record containing Mandelbrot Set related information about a point <code>c</code> on the complex plane. <p>
 *
 * An array of <code>RepresentationValue</code> objects will be used in a <code>MandelbrotImage</code>,
 * with each value determining the colour of a pixel in the image.
 * A RepresentationValue object is the output of
 * <code>Algorithm.run(Complex c, Complex z, int maxN)</code>
 * @param lastZ the value of the variable <code>z</code> during the last iteration of the algorithm on the
 *              point associated with this RepresentationValue
 * @param escapeIter the number of iterations of the algorithm it took before the point 'escaped'.
 *                   Is equal to <code>Integer.MAX_VALUE</code> if the point never escaped or if
 *                   an orbit was detected
 * @param distEst the output of the 'distance estimate' algorithm on this point, which is an estimation
 *                of the distance from the point associated with this RepresentationValue,
 *                to the closest point which is in the Mandelbrot Set
 * @param orbitFound true if an orbit was detected while iterating the point associated with
 *                   this RepresentationValue
 */
public record RepresentationValue(Complex lastZ, int escapeIter, double distEst, boolean orbitFound) {}
