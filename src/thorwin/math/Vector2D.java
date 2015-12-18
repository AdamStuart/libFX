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

import static java.lang.Math.sqrt;

/**
 * 2-dimensional vector.
 */
public class Vector2D extends DefaultVector {

  private final double x;
  private final double y;

  /**
   * Constructs a 2 dimensional vector
   *
   * @param x x-value
   * @param y y-value
   */
  public Vector2D(double x, double y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public int getDimension() {
    return 2;
  }

  @Override
  public double get(int index) {
    switch (index) {
      case 0:
        return x;
      case 1:
        return y;
      default:
        throw new IllegalArgumentException("Invalid index: " + index);
    }
  }

  @Override
  public Vector2D multiply(double multiplicand) {
    return new Vector2D(x * multiplicand, y * multiplicand);
  }

  @Override
  public double length() {
    return sqrt((x * x) + (y * y));
  }

  @Override
  public Vector2D negate() {
    return new Vector2D(-x, -y);
  }


  @SuppressWarnings("SuspiciousNameCombination")
  @Override
  public Vector2D reverse() {
    return new Vector2D(y,x);
  }


  /**
   * Returns the value of <i>x</i>
   * @return <i>x</i>
   */
  public double getX() {
    return x;
  }


  /**
   * Creates a new vector with the specified <i>y</i> value.
   *
   * @param value x-value
   * @return a new vector
   */
  public Vector2D setX(double value) {
    return new Vector2D(value, y);
  }


  /**
   * Returns the value of <i>y</i>
   * @return <i>y</i>
   */
  public double getY() {
    return y;
  }


  /**
   * Creates a new vector with the specified <i>y</i> value.
   *
   * @param value y-value
   * @return a new vector
   */
  public Vector2D setY(double value) {
    return new Vector2D(x, value);
  }


  /**
   * Calculates the dot product for this vector and another.
   *
   * @param other other 2d-vector
   * @return dot product
   */
  public double multiply(Vector2D other) {
    return (x * other.x) + (y * other.y);
  }


  /**
   * Adds this vector to another 2d-vector
   *
   * @param other other 2d-vector
   * @return resulting 2d-vector
   */
  public Vector2D add(Vector2D other) {
    return add(other.x, other.y);
  }


  /**
   * Add a scaled 2d-vector to this 2d-vector.
   *
   * @param other The other vector
   * @param scale scale to apply to the vector.
   * @return The resulting vector
   */
  public Vector2D add(Vector2D other, double scale) {
    return add(other.x * scale, other.y * scale);
  }


  /**
   * Adds vector components to this vector
   * @param x x-value to add
   * @param y y-value to add
   * @return resulting vector
   */
  public Vector2D add(double x, double y) {
    return new Vector2D(this.x + x, this.y + y);
  }


  /**
   * Subtracts a 2d-vector
   *
   * @param other another 2-d vector
   * @return resulting 2d-vector
   */
  public Vector2D subtract(Vector2D other) {
    return new Vector2D(x - other.x, y - other.y);
  }

  /**
   * Subtracts a scaled 2d-vector to this 2d-vector.
   *
   * @param other The other vector
   * @param scale scale to apply to the vector.
   * @return The resulting vector
   */
  public Vector2D subtract(Vector2D other, double scale) {
    return add(other, -scale);
  }
}
