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

import java.util.Random;
import java.util.StringTokenizer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import static java.lang.Math.*;


/**
 * Real vector.
 */
public interface Vector extends java.io.Serializable {
  /**
   * Create a vector.
   *
   * @param data The data in the vector
   * @return The vector
   */
  static Vector valueOf(double... data) {
    VectorBuilder<?> builder = VectorContext.getInstance().create(data.length);

    for (int i = 0; i < data.length; i++) {
      builder.set(i, data[i]);
    }
    return builder.toVector();
  }

  /**
   * Create a vector from a text string. Entries are separated by space
   * characters.
   *
   * @param text The vector as a text string
   * @return The vector
   */
  static Vector valueOf(String text) {

    StringTokenizer tokenizer = new StringTokenizer(text);

    double[] data = new double[tokenizer.countTokens()];

    for (int i = 0; i < data.length; i++) {
      String token = tokenizer.nextToken();
      data[i] = Double.parseDouble(token);
    }

    VectorBuilder<?> builder = VectorContext.getInstance().create(data.length);

    for (int i = 0; i < data.length; i++) {
      builder.set(i, data[i]);
    }
    return builder.toVector();
  }

  /**
   * Convert this vector to an n-by-1 matrix.
   *
   * @return This vector, represented as a matrix
   */
  default Matrix toMatrix() {
    double[] data = toArray();
    return new PackedMatrix(1, data.length, true, data);
  }

  /**
   * Convert this vector to an array.
   *
   * @return The array
   */
  default double[] toArray() {
    double[] array = new double[getDimension()];
    for (int i = 0; i < array.length; i++) {
      array[i] = get(i);
    }
    return array;
  }

  /**
   * The dimension (number of elements) of this vector.
   *
   * @return The dimension of the vector
   */
  int getDimension();

  /**
   * Return the vector element at the specified index.
   *
   * @param index The index
   * @return The value
   */
  double get(int index);

  /**
   * Calculate the multiply product, also known as the 'scalar product'.
   *
   * @param other The other vector
   * @return The multiply product.
   */
  default double multiply(Vector other) {
    double dot   = 0.0;
    int    count = min(getDimension(), other.getDimension());
    for (int i = 0; i < count; i++) {
      dot += get(i) * other.get(i);
    }
    return dot;
  }

  /**
   * Calculate the cross product, also known as the 'vector product'. Note that
   * the cross product is only defined for vectors with a dimension of 3.
   *
   * @param other The other vector
   * @return The cross product
   */
  default Vector cross(Vector other) {
    if (getDimension() != 3 || other.getDimension() != 3) {
      throw new IllegalArgumentException("Cross product only defined for 3 " +
                                         "dimensional vectors");
    }

    VectorBuilder<?> builder = VectorContext.getInstance().create(3);
    double           x1      = get(0);
    double           x2      = other.get(0);
    double           y1      = get(1);
    double           y2      = other.get(1);
    double           z1      = get(2);
    double           z2      = other.get(2);
    builder.set(0, (y1 * z2) - (y2 * z1));
    builder.set(1, (z1 * x2) - (z2 * x1));
    builder.set(2, (x1 * y2) - (x2 * y1));
    return builder.toVector();
  }

  /**
   * Compares the two vectors for approximate equality. This method has little
   * mathematical applicability.
   *
   * @param other   The other vector
   * @param epsilon The maximum delta between element values
   * @return True if approximately equal
   */
  default boolean equals(Vector other, double epsilon) {
    if (other == this) {
      return true;
    }
    if (other.getDimension() != getDimension()) {
      return false;
    }

    for (int i = 0; i < getDimension(); i++) {
      if (abs(get(i) - other.get(i)) > epsilon) {
        return false;
      }
    }

    return true;
  }

  /**
   * Calculate the mid-point between this vector and the other vector.
   *
   * @param other another vector
   * @return the mid-point
   */
  default Vector mid(Vector other) {
    return add(other.subtract(this).multiply(0.5));
  }

  /**
   * Add a vector to this vector.
   *
   * @param other The other vector
   * @return The resulting vector
   */
  default Vector add(Vector other) {
    int dimension = getDimension();

    if (dimension != other.getDimension()) {
      throw new IllegalArgumentException("Dimensions of both vectors should " +
                                         "be equal");
    }
    VectorBuilder<?> builder = VectorContext.getInstance().create(dimension);

    for (int i = 0; i < dimension; i++) {
      builder.set(i, get(i) + other.get(i));
    }

    return builder.toVector();
  }

  /**
   * Add a scaled vector to this vector.
   *
   * @param other The other vector
   * @param scale scale to apply to the vector.
   * @return The resulting vector
   */
  default Vector add(Vector other, double scale) {
    int dimension = getDimension();

    if (dimension != other.getDimension()) {
      throw new IllegalArgumentException("Dimensions of both vectors should " +
                                         "be equal");
    }
    VectorBuilder<?> builder = VectorContext.getInstance().create(dimension);

    for (int i = 0; i < dimension; i++) {
      builder.set(i, get(i) + other.get(i) * scale);
    }

    return builder.toVector();
  }

  /**
   * Scale this vector.
   *
   * @param multiplicand The scale
   * @return The resulting vector
   */
  default Vector multiply(double multiplicand) {
    int dimension = getDimension();

    VectorBuilder<?> builder = VectorContext.getInstance().create(dimension);

    for (int i = 0; i < dimension; i++) {
      builder.set(i, get(i) + multiplicand);
    }

    return builder.toVector();
  }

  /**
   * Subtract a vector from this vector.
   *
   * @param other The other vector
   * @return The resulting vector
   */
  default Vector subtract(Vector other) {
    int dimension = getDimension();

    if (dimension != other.getDimension()) {
      throw new IllegalArgumentException("Dimensions of both vectors should " +
                                         "be equal");
    }
    VectorBuilder<?> builder = VectorContext.getInstance().create(dimension);

    for (int i = 0; i < dimension; i++) {
      builder.set(i, get(i) - other.get(i));
    }

    return builder.toVector();
  }

  /**
   * Subtract a scaled vector from this vector.
   *
   * @param other The other vector
   * @param scale scale of the vector
   * @return The resulting vector
   */
  default Vector subtract(Vector other, double scale) {
    int dimension = getDimension();

    if (dimension != other.getDimension()) {
      throw new IllegalArgumentException("Dimensions of both vectors should " +
                                         "be equal");
    }
    VectorBuilder<?> builder = VectorContext.getInstance().create(dimension);

    for (int i = 0; i < dimension; i++) {
      builder.set(i, get(i) - other.get(i) * scale);
    }

    return builder.toVector();
  }

  /**
   * Perform a normalization of this vector.
   *
   * @return the normalized (unit) vector.
   */
  default Vector normalize() {
    return multiply(1.0 / length());
  }

  /**
   * The length (magnitude) of this vector.
   *
   * @return the length of the vector
   */
  default double length() {
    return sqrt(lengthSquared());
  }

  /**
   * The squared length of this vector. The length squared calculation is
   * considerable faster than the calculating the actual length of the vector.
   *
   * @return the length squared of the vector
   */
  default double lengthSquared() {
    int    dimension     = getDimension();
    double lengthSquared = 0.0;
    for (int i = 0; i < dimension; i++) {
      double value = get(i);
      lengthSquared += value * value;
    }
    return lengthSquared;
  }

  /**
   * Negate the vector by multiplying it with -1.0.
   *
   * @return The negated vector
   */
  default Vector negate() {
    return multiply(-1.0);
  }

  /**
   * Convert this vector to its string representation.
   *
   * @param precision The precision to use
   * @return The text representation of the vector
   */
  default String toString(int precision) {
    String        format  = "%." + precision + "g";
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < getDimension(); i++) {
      if (i != 0) {
        builder.append(" ");
      }
      builder.append(String.format(format, get(i)));
    }
    return builder.toString();
  }

  /**
   * Reverse the elements in this vector
   *
   * @return Reverse the elements of the vector
   */
  default Vector reverse() {
    VectorBuilder<?> builder   = VectorContext.getInstance().create(getDimension());
    int              dimension = getDimension();
    for (int i = 0; i < dimension; i++) {
      builder.set(i, get(dimension - 1 - i));
    }
    return builder.toVector();
  }

  /**
   * Create a stream to access the elements of this vector.
   *
   * @return Stream of this vector's elements.
   */
  default DoubleStream stream() {
    return IntStream.range(0, getDimension()).mapToDouble(this::get);
  }

  /**
   * Creates a vector with random values
   * @param dimension dimension of the vector
   * @param seed random seed
   * @return vector
   */
  static Vector random(int dimension, long seed) {
    Random rnd = new Random(seed);
    VectorBuilder<?> builder   = VectorContext.getInstance().create(dimension);
    for (int i = 0; i < dimension; i++) {
      builder.set(i, rnd.nextDouble());
    }
    return builder.toVector();
  }
}
