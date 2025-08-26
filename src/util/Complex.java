package util;

import java.io.Serial;
import java.io.Serializable;

/**
 * A class representing a complex number with a real part and imaginary part.
 * Includes methods for complex arithmetic.
 * @param real the real part of the complex number
 * @param imaginary the imaginary part of the complex number
 */
public record Complex(double real, double imaginary) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final Complex ZERO = new Complex(0, 0);
    public static final Complex ONE = new Complex(1, 0);
    public static final Complex DEFAULT_CENTER = new Complex(-0.5, 0);

    /**
     * Computes the sum of this complex number and another.
     * @param other the complex number this complex number is being added to
     * @return the sum of this complex number with <code>other</code>
     */
    public Complex plus(Complex other) {
        return new Complex(real + other.real, imaginary + other.imaginary);
    }

    /**
     * Computes the product of this complex number with another.
     * @param other the complex number this complex number is being multiplied with.
     * @return the product of this complex number and <code>other</code>
     */
    public Complex times(Complex other) {
        double realPart = real * other.real - imaginary * other.imaginary;
        double imaginaryPart = real * other.imaginary + imaginary * other.real;
        return new Complex(realPart, imaginaryPart);
    }

    /**
     * Computes the product of this complex number and a scalar.
     * @param scalar the scalar this complex number is being multiplied by.
     * @return the scaled complex number
     */
    public Complex times(double scalar) {
        return new Complex(real * scalar, imaginary * scalar);
    }

    /**
     * Computes the square of this complex number.
     * @return the square of this complex number
     */
    public Complex squared() {
        return this.times(this);
    }

    /**
     * Computes the magnitude of this complex number (Euclidean distance from origin).
     * @return the magnitude of this complex number
     */
    public double abs() {
        return Math.sqrt(real * real + imaginary * imaginary);
    }

    public double angle() {
        return Math.atan2(imaginary, real);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Complex complex = (Complex) o;
        return real == complex.real && imaginary == complex.imaginary;
    }
}
