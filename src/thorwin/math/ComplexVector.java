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

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Math.abs;
import static java.lang.Math.max;


/**
 * Complex vector.
 */
public interface ComplexVector {
  /**
   * Creates a random complex vector. This vector is filled with pseudo-random
   * values.
   *
   * @param size size of the vector
   * @param seed random seed
   * @return the random vector
   */
  static ComplexVector random(int size, int seed) {
    return DefaultComplexVector.random(size, seed);
  }

  /**
   * Constructs a complex vector a real vector
   *
   * @param vector the real vector
   * @return the complex vector
   */
  static ComplexVector valueOf(Vector vector) {
    return DefaultComplexVector.valueOf(vector);
  }

  /**
   * Constructs a complex vector from a number of real elements
   *
   * @param reals real elements
   * @return complex vector
   */
  static ComplexVector valueOf(double... reals) {
    return DefaultComplexVector.valueOf(reals);
  }

  /**
   * Performs a Fast Fourier Transformation on the input samples.
   *
   * @param reals real input samples (length should be a order of two)
   * @return Fourier transformed result
   */
  static ComplexVector fft(double... reals) {
    return DefaultComplexVector.fft(reals);
  }

  /**
   * Adds a complex vector to this vector
   *
   * @param other the other complex vector
   * @return the vector that is the result of the addition
   */
  ComplexVector add(ComplexVector other);

  /**
   * Subtracts a complex vector from this vector
   *
   * @param other the other complex vector
   * @return the vector that is the result of the subtraction
   */
  ComplexVector subtract(ComplexVector other);

  /**
   * Returns a stream of this vector's complex elements
   *
   * @return element stream
   */
  default Stream<Complex> stream() {
    return IntStream.range(0, getDimension()).mapToObj(this::get);
  }

  /**
   * Returns the length of this vector.
   *
   * @return number of elements in this vector
   */
  int getDimension();

  /**
   * Returns the real value at the specified index
   *
   * @param index index
   * @return real value
   */
  double real(int index);

  /**
   * Returns the imaginary value at the specified index
   *
   * @param index index
   * @return imaginary value
   */
  double imag(int index);

  /**
   * Converts this complex vector to a real vector (imaginary part is lost)
   *
   * @return a real vector
   */
  default Vector toVector() {
    return new DefaultVector() {

      @Override
      public int getDimension() {
        return ComplexVector.this.getDimension();
      }

      @Override
      public double get(int index) {
        return ComplexVector.this.get(index).real();
      }
    };
  }

  /**
   * Return the complex element at the specified index
   *
   * @param index the index
   * @return the complex value
   */
  Complex get(int index);

  /**
   * Convert to an array of complex values
   *
   * @return an array
   */
  default Complex[] toArray() {
    Complex[] array = new Complex[getDimension()];
    for (int i = 0; i < array.length; i++) array[i] = get(i);
    return array;
  }

  /**
   * Calculates the linear convolution
   *
   * @param other other vector
   * @return the linear convolution result
   */
  default ComplexVector convolve(ComplexVector other) {
    return pad(getDimension() << 1).cconvolve(other.pad(other.getDimension() << 1));
  }

  /**
   * Calculates circular convolution of this vector with the other.
   *
   * @param other other vector
   * @return the circular convolution result
   */
  default ComplexVector cconvolve(ComplexVector other) {
    // calculate length to fit in power of two
    int maxDimension  = max(getDimension(), other.getDimension());
    int highestOneBit = Integer.highestOneBit(maxDimension);
    int
        length        =
        (highestOneBit == maxDimension) ? highestOneBit : (highestOneBit << 1);

    ComplexVector a = pad(length).fft();
    ComplexVector b = other.pad(length).fft();
    Complex[] c = new Complex[length];

    for (int i = 0; i < length; i++) {
      c[i] = a.get(i).multiply(b.get(i));
    }

    return valueOf(c).ifft();
  }

  /**
   * Pads the values with complex zero values.
   *
   * @param length target length
   * @return the padded vector, or the original if no padding was necessary
   */
  ComplexVector pad(int length);

  /**
   * Performs a Fast Fourier Transformation on the input samples.
   *
   * @return Fourier transformed result
   */
  ComplexVector fft();

  /**
   * Performs the inverse Fast Fourier Transform.
   *
   * @return the negate FTT result
   */
  ComplexVector ifft();

  /**
   * Constructs a complex vector from an array of complex numbers
   *
   * @param values complex values
   * @return complex vector
   */
  static ComplexVector valueOf(Complex... values) {
    return DefaultComplexVector.valueOf(values);
  }

  /**
   * Creates a string representation
   *
   * @param precision precision to use
   * @return text representation of the vector
   */
  default String toString(int precision) {
    String format = "%." + precision + "g%+." + precision + "gi";
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < getDimension(); i++) {
      if (i != 0) {
        builder.append(" ");
      }
      builder.append(String.format(format, get(i).real(), get(i).imag()));
    }
    return builder.toString();
  }

  /**
   * Compares this complex vector with another. This method has limited
   * mathematical applicability.
   *
   * @param other   the other complex vector
   * @param epsilon maximum difference in real or imaginary value
   * @return true if both vectors are approximately equal
   */
  default boolean equals(ComplexVector other, double epsilon) {
    if (other == this) {
      return true;
    }
    if (other.getDimension() != getDimension()) {
      return false;
    }

    for (int i = 0; i < getDimension(); i++) {
      if ((abs(get(i).real() - other.get(i).real()) > epsilon) ||
          (abs(get(i).imag() - other.get(i).imag()) > epsilon)) {
        return false;
      }
    }
    return true;
  }

}
