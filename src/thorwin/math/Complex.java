/******************************************************************************
 * Copyright (C) 2015 Sebastiaan R. Hogenbirk                                 *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU Lesser General Public License as published by*
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Lesser General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public License   *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package thorwin.math;

import java.io.Serializable;

/**
 * Class for representing complex numbers.
 * @see <a href="https://en.wikipedia.org/wiki/Complex_number">Wikipedia on
 * Complex Numbers</a>
 */
public final class Complex implements Serializable {
  /**
   * Constant for zero.
   */
  public static final Complex ZERO = new Complex(0.0, 0.0);

  /**
   * Constant for 1.
   */
  public static final Complex ONE = new Complex(1.0, 0.0);

  /**
   * The serial version unique identifier.
   */
  private static final long serialVersionUID = -6003038793969832771L;
  /**
   * The complex part.
   */
  private final double imaginary;

  /**
   * The real part.
   */
  private final double real;

  /**
   * Create a new complex number
   *
   * @param real      real part value
   * @param imaginary imaginary part value
   */
  public Complex(double real, double imaginary) {
    super();
    this.real = real;
    this.imaginary = imaginary;
  }

  /**
   * Calculates the conjugation (negate imaginary part).
   *
   * @return the conjugated complex number
   */
  public Complex conjugate() {
    return new Complex(real, -imaginary);
  }

  /**
   * Calculates the exponential.
   *
   * @return the exponential
   */
  public Complex exp() {
    return new Complex(java.lang.Math.exp(real) * java.lang.Math.cos(imaginary),
                       java.lang.Math.exp(real) * java.lang.Math.sin(imaginary));
  }

  /**
   * Returns the imaginary part of this complex number.
   *
   * @return the imaginary part
   */
  public double imag() {
    return imaginary;
  }

  /**
   * Returns the natural logarithm.
   *
   * @return The logarithm
   */
  public Complex log() {
    return new Complex(java.lang.Math.log(abs()), phase());
  }

  /**
   * Calculates the absolute (or modulus).
   *
   * @return the absolute
   */
  public double abs() {
    return java.lang.Math.hypot(real, imaginary);
  }

  /**
   * Calculates the phase.
   *
   * @return the phase
   */
  public double phase() {
    return java.lang.Math.atan2(imaginary, real);
  }

  /**
   * Subtracts a complex number from this one.
   *
   * @param value value
   * @return the result
   */
  public Complex subtract(Complex value) {
    return new Complex(real - value.real, imaginary - value.imaginary);
  }

  /**
   * Calculates the negative (negate real and imaginary part).
   *
   * @return negative
   */
  public Complex negate() {
    return new Complex(-real, -imaginary);
  }

  /**
   * Adds a complex number to this one.
   *
   * @param other the other complex number
   * @return the result of the addition
   */
  public Complex add(Complex other) {
    return new Complex(real + other.real, imaginary + other.imaginary);
  }


  /**
   * The real part of this complex number.
   *
   * @return the real part
   */
  public double real() {
    return real;
  }

  /**
   * Calculates the square root.
   *
   * @return square root
   */
  public Complex sqrt() {
    return new Complex(java.lang.Math.sqrt(abs()) * java.lang.Math.cos(phase
                                                                           ()
                                                                       / 2.0),
                       java.lang.Math.sqrt(abs()) * java.lang.Math.sin(phase
                                                                           ()
                                                                       / 2.0));
  }

  /**
   * Calculates the tangent.
   *
   * @return tangent
   */
  public Complex tan() {
    return sin().divide(cos());
  }

  /**
   * Divides this complex number by another
   *
   * @param other the other complex number
   * @return the result of the division
   */
  public Complex divide(Complex other) {
    return multiply(other.reciprocal());
  }

  /**
   * Calculates the sine.
   *
   * @return sine
   */
  public Complex sin() {
    return new Complex(java.lang.Math.sin(real) * java.lang.Math.cosh(imaginary),
                       java.lang.Math.cos(real) * java.lang.Math.sinh(imaginary));
  }

  /**
   * Calculates the cosine.
   *
   * @return cosine
   */
  public Complex cos() {
    return new Complex(java.lang.Math.cos(real) * java.lang.Math.cosh(imaginary),
                       -java.lang.Math.sin(real) * java.lang.Math.sinh(imaginary));
  }

  /**
   * Multiplies a complex number with this one.
   *
   * @param other the other complex number
   * @return the result
   */
  public Complex multiply(Complex other) {
    return multiply(real, imaginary, other.real, other.imaginary);
  }

  /**
   * Calculates the reciprocal.
   *
   * @return reciprocal
   */
  public Complex reciprocal() {
    double scale = (real * real) + (imaginary * imaginary);

    return new Complex(real / scale, -imaginary / scale);
  }

  /**
   * Multiplies two complex numbers. This method allows multiplication
   * without having to instantiate complex numbers to do so.
   *
   * @param ar a, real part
   * @param ai a, imaginary part
   * @param br b, real part
   * @param bi b, imaginary part
   * @return the complex result
   */
  static Complex multiply(double ar, double ai, double br, double bi) {
    return new Complex((ar * br) - (ai * bi), (ar * bi) + (ai * br));
  }

  /**
   * Multiplies this complex number with another.
   *
   * @param real real part of the other value
   * @param imag imaginary part of the other value
   * @return the result of the multiplication
   */
  public Complex multiply(double real, double imag) {
    return multiply(this.real, this.imaginary, real, imag);
  }

  /**
   * Multiplies this complex number with a real number (scale this number).
   *
   * @param multiplicand the scale
   * @return the result
   */
  public Complex multiply(double multiplicand) {
    return new Complex(multiplicand * real, multiplicand * imaginary);
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(imaginary);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(real);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    Complex complex = (Complex) obj;

    return Double.compare(complex.real,
                          real) == 0 && Double.compare(complex.imaginary,
                                                       imaginary) == 0;

  }

  /**
   * Returns the complex number as a text string
   */
  @Override
  public String toString() {
    return String.format("%f %+fi", real, imaginary);
  }
}
