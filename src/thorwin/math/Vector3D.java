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

/**
 * 3-dimensional vector.
 */
public final class Vector3D extends DefaultVector {

  /**
   * Vector [0,0,0]
   */
  public static final Vector3D ZERO = new Vector3D(0, 0, 0);

  private final double x;
  private final double y;
  private final double z;


  /**
   * Constructs a 3-dimensional vector
   *
   * @param x x-value
   * @param y y-value
   * @param z z-value
   */
  public Vector3D(double x, double y, double z) {
    super();
    this.x = x;
    this.y = y;
    this.z = z;
  }


  @Override
  public int getDimension() {
    return 3;
  }


  @Override
  public double get(int index) {
    switch (index) {
      case 0:
        return x;
      case 1:
        return y;
      case 2:
        return z;
      default:
        throw new IllegalArgumentException("Invalid index: " + index);
    }
  }


  @Override
  public double multiply(Vector other) {
    if (other instanceof Vector3D) {
      return multiply((Vector3D) other);
    } else {
      return super.multiply(other);
    }
  }


  /**
   * Calculates the dot product for this vector and the other.
   *
   * @param other another vector
   * @return dot product
   */
  public double multiply(Vector3D other) {
    return (x * other.x) + (y * other.y) + (z * other.z);
  }


  @Override
  public Vector cross(Vector other) {
    if (other instanceof Vector3D) {
      return cross((Vector3D) other);
    } else {
      return super.cross(other);
    }
  }


  @Override
  public Vector mid(Vector other) {
    if (other instanceof Vector3D) {
      return mid((Vector3D) other);
    } else {
      return super.mid(other);
    }
  }


  @Override
  public Vector add(Vector other) {
    if (other instanceof Vector3D) {
      return add((Vector3D) other);
    } else {
      return super.add(other);
    }
  }


  @Override
  public Vector3D multiply(double other) {
    return new Vector3D(x * other, y * other, z * other);
  }


  @Override
  public Vector subtract(Vector other) {
    if (other instanceof Vector3D) {
      return subtract((Vector3D) other);
    } else {
      return super.subtract(other);
    }
  }


  @Override
  public Vector3D normalize() {
    return multiply(1.0 / length());
  }


  @Override
  public double lengthSquared() {
    return (x * x) + (y * y) + (z * z);
  }


  @Override
  public Vector3D negate() {
    return new Vector3D(-x, -y, -z);
  }


  @Override
  public Vector3D reverse() {
    return new Vector3D(z,y,x);
  }


  /**
   * Subtracts a vector
   *
   * @param other another vector
   * @return resulting vector
   */
  public Vector3D subtract(Vector3D other) {
    return new Vector3D(x - other.x, y - other.y, z -
        other.z);
  }

  /**
   * Subtract a 3d-scaled vector from this 3d-vector.
   *
   * @param other The other vector
   * @param scale scale of the vector
   * @return The resulting vector
   */
  public Vector3D subtract(Vector3D other, double scale) {
    return add(other, -scale);
  }

  /**
   * Adds a vector to this vector
   *
   * @param other another vector
   * @return resulting vector
   */
  public Vector3D add(Vector3D other) {
    return new Vector3D(
        x + other.x,
        y + other.y,
        z + other.z);
  }


  /**
   * Add a scaled 3d-vector to this 3d-vector.
   *
   * @param other The other vector
   * @param scale scale to apply to the vector.
   * @return The resulting vector
   */
  public Vector3D add(Vector3D other, double scale) {
    return add(other.x * scale, other.y * scale, other.z * scale);
  }



  /**
   * Calculates the mid-point between this vector and the other vector.
   *
   * @param other another vector
   * @return the mid-point
   */
  public Vector3D mid(Vector3D other) {
    return add(other.subtract(this).multiply(0.5));
  }


  /**
   * Calculates the cross product for this vector and the other.
   *
   * @param other another vector
   * @return the cross product
   */
  public Vector3D cross(Vector3D other) {
    double ox = (y * other.z) - (other.y * z);
    double oy = (z * other.x) - (other.z * x);
    double oz = (x * other.y) - (other.x * y);

    return new Vector3D(ox, oy, oz);
  }


  /**
   * Add a vector
   *
   * @param x x-coordinate to add
   * @param y y-coordinate to add
   * @param z z-coordinate to add
   * @return resulting vector
   */
  public Vector3D add(double x, double y, double z) {
    return new Vector3D(
        this.x + x,
        this.y + y,
        this.z + z);
  }


  /**
   * Returns the <i>x</i> coordinate of this vector.
   * @return x-coordinate
   */
  public double getX() {
    return x;
  }


  /**
   * Creates a new vector with the <i>x</i> coordinate set.
   *
   * @param x x-coordinate
   * @return resulting vector
   */
  public Vector3D setX(double x) {
    return new Vector3D(x, this.y, this.z);
  }


  /**
   * Returns the <i>y</i> coordinate of this vector.
   * @return The y-coordinate
   */
  public double getY() {
    return y;
  }


  /**
   * Creates a new vector with the <i>y</i> coordinate set.
   *
   * @param y y-coordinate
   * @return resulting vector
   */
  public Vector3D setY(double y) {
    return new Vector3D(this.x, y, this.z);
  }


  /**
   * Returns the <i>z</i> coordinate of this vector.
   * @return The z-coordinate
   */
  public double getZ() {
    return z;
  }


  /**
   * Creates a new vector with the <i>z</i> coordinate set.
   *
   * @param z z-coordinate
   * @return resulting vector
   */
  public Vector3D setZ(double z) {
    return new Vector3D(this.x, this.y, z);
  }


  /**
   * Returns a new 3-d vector with the specified index set to the value
   * @param index index
   * @param value  value
   * @return resulting vector
   */
  public Vector3D set(int index, double value) {
    switch (index) {
      case 0: return setX(value);
      case 1: return setY(value);
      case 2: return setZ(value);
      default:
        throw new IllegalArgumentException("Not a valid index: " + index);
    }
  }
}
