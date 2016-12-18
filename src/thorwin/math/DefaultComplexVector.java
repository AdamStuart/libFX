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

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.Random;

/**
 * Vector for complex numbers.
 */
final class DefaultComplexVector implements ComplexVector {

  private final double[] reals;
  private final double[] imags;

  /**
   * Constructor
   *
   * @param reals array containing imaginary components
   * @param imags array containing imaginary components
   */
  DefaultComplexVector(double[] reals, double[] imags) {
    super();
    this.reals = reals;
    this.imags = imags;
  }

  /**
   * Creates a random complex vector. This vector is filled with
   * pseudo-random values.
   * @param size size of the vector
   * @param seed random seed
   * @return the random vector
   */
  public static DefaultComplexVector random(int size, int seed) {
    double[] reals = new double[size];
    double[] imags = new double[size];

    Random rnd = new Random(seed);

    for (int i = 0; i < size; i++) {
      reals[i] = rnd.nextDouble();
      imags[i] = rnd.nextDouble();
    }

    return new DefaultComplexVector(reals, imags);
  }

  /**
   * Constructs a complex vector a real vector
   *
   * @param vector the real vector
   * @return the complex vector
   */
  public static DefaultComplexVector valueOf(Vector vector) {
    return valueOf(vector.toArray());
  }

  /**
   * Constructs a complex vector from a number of real elements
   *
   * @param reals real elements
   * @return complex vector
   */
  public static DefaultComplexVector valueOf(double... reals) {
    return new DefaultComplexVector(reals.clone(), new double[reals.length]);
  }

  /**
   * Performs a Fast Fourier Transformation on the input samples.
   *
   * @param reals real input samples (length should be a order of two)
   * @return Fourier transformed result
   */
  public static DefaultComplexVector fft(double... reals) {
    double[] imags = new double[reals.length];

    fft(reals, imags);

    return new DefaultComplexVector(reals, imags);
  }


  /**
   * Performs a Fast Fourier Transformation on the input samples. The supplied
   * array is modified during the algorithm.
   *
   * @param reals real input samples (length should be a order of two)
   * @param imags imaginary input samples
   */
  private static void fft(double[] reals, double[] imags) {
    int length = reals.length;

    if (Integer.highestOneBit(length) != length) {
      throw new IllegalArgumentException("Invalid length: " + length);
    }

    int shift = 1 + Integer.numberOfLeadingZeros(length);

    for (int k = 0; k < length; k++) {
      int j = Integer.reverse(k) >>> shift;
      if (j > k) {
        swap(reals, j, k);
        swap(imags, j, k);
      }
    }

    for (int l = 2; l <= length; l <<= 1) {
      for (int k = 0; k < (l / 2); k++) {
        double kth = (-2 * k * PI) / l;
        Complex w = new Complex(cos(kth), sin(kth));

        for (int j = 0; j < (length / l); j++) {
          int index1 = (j * l) + k;
          int index2 = index1 + (l / 2);
          Complex u = w.multiply(reals[index2], imags[index2]);

          reals[index2] = reals[index1] - u.real();
          imags[index2] = imags[index1] - u.imag();
          reals[index1] = reals[index1] + u.real();
          imags[index1] = imags[index1] + u.imag();
        }
      }
    }
  }


  /**
   * Swaps two elements in the array.
   * @param array input array
   * @param index1 first element index
   * @param index2 second element index
   */
  private static void swap(double[] array, int index1, int index2) {
    double tmp = array[index1];
    array[index1] = array[index2];
    array[index2] = tmp;
  }


  /**
   * Constructs a complex vector from an array of complex numbers
   *
   * @param values complex values
   * @return complex vector
   */
  public static DefaultComplexVector valueOf(Complex... values) {
    double[] reals = new double[values.length];
    double[] imags = new double[values.length];

    for (int i = 0; i < values.length; i++) {
      reals[i] = values[i].real();
      imags[i] = values[i].imag();
    }

    return new DefaultComplexVector(reals, imags);
  }


  @Override
  public ComplexVector add(ComplexVector other) {

    // we can use the more specific method if possible (is faster)
    if (other instanceof DefaultComplexVector) {
      return add((DefaultComplexVector) other);
    }

    if (getDimension() != other.getDimension()) {
      throw new IllegalArgumentException("Dimensions should be equal");
    }
    double reals[] = this.reals.clone();
    double imags[] = this.imags.clone();

    for (int i = 0; i < reals.length; i++) {
      reals[i] += other.real(i);
      imags[i] += other.imag(i);
    }

    return new DefaultComplexVector(reals, imags);
  }

  private ComplexVector add(DefaultComplexVector other) {
    if (getDimension() != other.getDimension()) {
      throw new IllegalArgumentException("Dimensions should be equal");
    }
    double reals[] = this.reals.clone();
    double imags[] = this.imags.clone();

    for (int i = 0; i < reals.length; i++) {
      reals[i] += other.reals[i];
      imags[i] += other.imags[i];
    }

    return new DefaultComplexVector(reals, imags);
  }


  @Override
  public ComplexVector subtract(ComplexVector other) {

    // we can use the more specific method if possible (is faster)
    if (other instanceof DefaultComplexVector) {
      return subtract((DefaultComplexVector) other);
    }

    if (getDimension() != other.getDimension()) {
      throw new IllegalArgumentException("Dimensions should be equal");
    }
    double reals[] = this.reals.clone();
    double imags[] = this.imags.clone();

    for (int i = 0; i < reals.length; i++) {
      reals[i] -= other.real(i);
      imags[i] -= other.imag(i);
    }

    return new DefaultComplexVector(reals, imags);
  }


  @Override
  public int getDimension() {
    return reals.length;
  }


  @Override
  public double real(int index) {
    return reals[index];
  }


  @Override
  public double imag(int index) {
    return imags[index];
  }


  @Override
  public Complex get(int index) {
    return new Complex(reals[index], imags[index]);
  }


  @Override
  public ComplexVector pad(int length) {
    if (getDimension() < length) {
      double[] reals = new double[length];
      double[] imags = new double[length];
      System.arraycopy(this.reals, 0, reals, 0, this.reals.length);
      System.arraycopy(this.imags, 0, imags, 0, this.imags.length);
      return new DefaultComplexVector(reals, imags);
    } else {
      return this;
    }
  }


  @Override
  public ComplexVector fft() {
    double[] reals = this.reals.clone();
    double[] imags = this.imags.clone();
    fft(reals, imags);
    return new DefaultComplexVector(reals, imags);
  }


  @Override
  public ComplexVector ifft() {
    double[] reals = this.reals.clone();
    double[] imags = this.imags.clone();

    double inverseLength = 1.0 / getDimension();

    // conjugate
    for (int i = 0; i < imags.length; i++) {
      imags[i] *= -1;
    }

    fft(reals, imags);

    // conjugate and multiply with inverse length
    for (int i = 0; i < reals.length; i++) {
      reals[i] *= inverseLength;
      imags[i] *= -inverseLength;
    }

    return new DefaultComplexVector(reals, imags);
  }


  private ComplexVector subtract(DefaultComplexVector other) {
    if (getDimension() != other.getDimension()) {
      throw new IllegalArgumentException("Dimensions should be equal");
    }
    double reals[] = this.reals.clone();
    double imags[] = this.imags.clone();

    for (int i = 0; i < reals.length; i++) {
      reals[i] -= other.reals[i];
      imags[i] -= other.imags[i];
    }

    return new DefaultComplexVector(reals, imags);
  }


  @Override
  public String toString() {
    return toString(5);
  }

}
