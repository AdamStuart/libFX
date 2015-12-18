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

package thorwin.math.geo;

import thorwin.math.*;

import static java.lang.Math.*;

/**
 * Quaternion
 */
public final class Quaternion implements java.io.Serializable {

  /**
   * The identity quaternion.
   */
  public static final Quaternion IDENTITY = new Quaternion(1, 0, 0, 0);

  /**
   * The zero quaternion.
   */
  public static final Quaternion ZERO = new Quaternion(0, 0, 0, 0);

  private final double w;
  private final double x;
  private final double y;
  private final double z;

  /**
   * Constructs a quaternion.
   *
   * @param w W
   * @param x X
   * @param y Y
   * @param z Z
   */
  public Quaternion(double w, double x, double y, double z) {
    this.w = w;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Creates a quaternion from a 3x3 matrix.
   *
   * @param matrix 3x3 matrix
   * @return quaternion
   */
  public static Quaternion valueOf(Matrix matrix) {

    // check dimension of matrix
    if (matrix.getRowDimension() != 3 || matrix.getColumnDimension() != 3) {
      throw new IllegalArgumentException("Matrix should be 3x3");
    }

    // we take some much used elements from the matrix
    double m00 = matrix.get(0, 0);
    double m11 = matrix.get(1, 1);
    double m22 = matrix.get(2, 2);

    double trace = m00 + m11 + m22;

    if (trace > 0.0) {
      double root = sqrt(trace + 1.0);
      double w = 0.5 * root;
      root = 0.5 / root;
      double x = (matrix.get(2, 1) - matrix.get(1, 2)) * root;
      double y = (matrix.get(0, 2) - matrix.get(2, 0)) * root;
      double z = (matrix.get(1, 0) - matrix.get(0, 1)) * root;
      return new Quaternion(w, x, y, z);
    } else {
      if (m00 > m11 && m00 > m22) {
        double s = 2.0 * sqrt(m00 - m11 - m22 + 1.0);
        double w = (matrix.get(2, 1) - matrix.get(1, 2)) / s;
        double x = 0.25 * s;
        double y = (matrix.get(0, 1) + matrix.get(1, 0)) / s;
        double z = (matrix.get(0, 2) + matrix.get(2, 0)) / s;
        return new Quaternion(w, x, y, z);
      } else if (m11 > m22) {
        double s = 2.0 * sqrt(m11 - m00 - m22 + 1.0);
        double w = (matrix.get(0, 2) - matrix.get(2, 0)) / s;
        double x = (matrix.get(0, 1) + matrix.get(1, 0)) / s;
        double y = 0.25 * s;
        double z = (matrix.get(1, 2) + matrix.get(2, 1)) / s;
        return new Quaternion(w, x, y, z);
      } else {
        double s = 2.0 * sqrt(m22 - m00 - m11 + 1.0);
        double w = (matrix.get(1, 0) - matrix.get(0, 1)) / s;
        double x = (matrix.get(0, 2) + matrix.get(2, 0)) / s;
        double y = (matrix.get(1, 2) + matrix.get(2, 1)) / s;
        double z = 0.25 * s;
        return new Quaternion(w, x, y, z);
      }
    }
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(w);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(x);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(z);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Quaternion that = (Quaternion) o;

    return Double.compare(that.w, w) == 0 && Double.compare(that.x,
                                                            x) == 0 &&
               Double.compare(
                                 that.y,
                                 y) == 0 && Double.compare(that.z, z) == 0;

  }

  @Override
  public String toString() {
    return w + " " + x + " " + y + " " + z;
  }

  /**
   * Returns the length of this quaternion
   * @return length
   */
  public double length() {
    return sqrt(w * w + x * x + y * y + z * z);
  }

  /**
   * Returns the conjugate of this quaternion. The conjugate negates
   * the imaginary part of the quaternion.
   * @return quaternion
   */
  public Quaternion conjugate() {
    return new Quaternion(w, -x, -y, -z);
  }

  /**
   * Adds a quaternion.
   * @param other quaternion to add
   * @return resulting quaternion
   */
  public Quaternion add(Quaternion other) {
    return new Quaternion(w + other.w, x + other.x, y + other.y, z +
                                                                     other.z);
  }

  /**
   * Divides the quaternion
   *
   * @param other other quaternion
   * @return resulting quaternion
   */
  public Quaternion divide(Quaternion other) {
    return invert().multiply(other);
  }

  /**
   * Multiplies this quaternion with another
   * @param other other quaternion
   * @return resulting quaternion
   */
  public Quaternion multiply(Quaternion other) {
    double w0 = this.w * other.w - this.x * other.x - this.y * other.y - this
                                                                             .z * other.z;
    double x0 = this.w * other.x + this.x * other.w + this.y * other.z - this
                                                                             .z * other.y;
    double y0 = this.w * other.y - this.x * other.z + this.y * other.w + this
                                                                             .z * other.x;
    double z0 = this.w * other.z + this.x * other.y - this.y * other.x +
                    this.z * other.w;
    return new Quaternion(w0, x0, y0, z0);
  }

  /**
   * Inverts this quaternion
   * @return inverted quaternion
   */
  public Quaternion invert() {
    double lengthSquared = w * w + x * x + y * y + z * z;
    return new Quaternion(
                             w / lengthSquared,
                             -x / lengthSquared,
                             -y / lengthSquared,
                             -z / lengthSquared);
  }

  /**
   * Convert to a rotation matrix. This quaternion needs to be
   * normalized for this method to result in a proper rotation matrix. This is
   * not tested at runtime, as this would incur a performance penalty.
   *
   * @return 3x3 rotation matrix
   */
  public Matrix3x3 toMatrix3x3() {
    // Assert the quaternion is normalized. This assertion is added to quickly
    // catch wrong use of this method when testing. Note that the epsilon is
    // arbitrarily chosen.
    assert isNormalized(0.001) : "Quaternion should be normalized";

    return toMatrix3x3(w, x, y, z);
  }

  /**
   * Checks if this quaternion is normalized.
   *
   * @param epsilon If difference between 1 and length squared of this
   *                quaternion is less than epsilon, it is considered
   *                normalized.
   * @return if this quaternion is normalized
   */
  boolean isNormalized(double epsilon) {
    double lengthSquared = w * w + x * x + y * y + z * z;
    return abs(1 - lengthSquared) < epsilon;
  }

  /**
   * Creates a matrix matrix for a normalized quaternion. Note that the given
   * quaternion is expected to be normalized.
   *
   * @param w real part
   * @param x first element of the imaginary vector
   * @param y second element of the imaginary vector
   * @param z third element of the imaginary vector
   * @return 3x3 rotation matrix
   */
  public static Matrix3x3 toMatrix3x3(double w,
                                      double x,
                                      double y,
                                      double z) {

    double xx = x * x;
    double xy = x * y;
    double xz = x * z;
    double xw = x * w;

    double yy = y * y;
    double yz = y * z;
    double yw = y * w;

    double zz = z * z;
    double zw = z * w;

    double mxx = 1 - 2 * (yy + zz);
    double mxy = 2 * (xy - zw);
    double mxz = 2 * (xz + yw);

    double myx = 2 * (xy + zw);
    double myy = 1 - 2 * (xx + zz);
    double myz = 2 * (yz - xw);

    double mzx = 2 * (xz - yw);
    double mzy = 2 * (yz + xw);
    double mzz = 1 - 2 * (xx + yy);

    return new Matrix3x3(mxx, mxy, mxz, myx, myy, myz, mzx, mzy, mzz);
  }

  /**
   * Creates an affine matrix from this quaternion. This quaternion needs
   * to be normalized for this method to result in a proper rotation matrix.
   * This is not tested at runtime, as this would incur a performance penalty.
   *
   * @return affine matrix
   */
  public Affine3D toAffine3D() {
    // Assert the quaternion is normalized. This assertion is added to quickly
    // catch wrong use of this method when testing. Note that the epsilon is
    // arbitrarily chosen.
    assert isNormalized(0.001) : "Quaternion should be normalized";

    return toAffine3D(w, x, y, z);
  }


  /**
   * Creates a rotation affine matrix from a normalized quaternion. Note that the
   * quaternion is expected to be normalized. The translation components of the
   * affine may be directly specified, as it is very common to combine a
   * rotation and translation into a affine transformation.
   *
   * @param w real part
   * @param x first entry of the imaginary vector
   * @param y second entry of the imaginary vector
   * @param z third entry of the imaginary vector
   * @return affine matrix
   */
  public static Affine3D toAffine3D(double w,
                                    double x,
                                    double y,
                                    double z) {
    return toAffine3D(w, x, y, z, 0, 0, 0);
  }


  /**
   * Creates an affine matrix from a normalized quaternion. Note that the
   * quaternion is expected to be normalized. The translation components of the
   * affine may be directly specified, as it is very common to combine a
   * rotation and translation into a single affine matrix.
   *
   * @param w  real part
   * @param x  first entry of the imaginary vector
   * @param y  second entry of the imaginary vector
   * @param z  third entry of the imaginary vector
   * @param tx x-translation to use
   * @param ty y-translation to use
   * @param tz z-translation to use
   * @return affine matrix
   */
  static Affine3D toAffine3D(double w,
                             double x,
                             double y,
                             double z,
                             double tx,
                             double ty,
                             double tz) {
    double xx = x * x;
    double xy = x * y;
    double xz = x * z;
    double xw = x * w;

    double yy = y * y;
    double yz = y * z;
    double yw = y * w;

    double zz = z * z;
    double zw = z * w;

    double mxx = 1 - 2 * (yy + zz);
    double mxy = 2 * (xy - zw);
    double mxz = 2 * (xz + yw);

    double myx = 2 * (xy + zw);
    double myy = 1 - 2 * (xx + zz);
    double myz = 2 * (yz - xw);

    double mzx = 2 * (xz - yw);
    double mzy = 2 * (yz + xw);
    double mzz = 1 - 2 * (xx + yy);

    return new Affine3D(mxx,
                        mxy,
                        mxz,
                        tx,
                        myx,
                        myy,
                        myz,
                        ty,
                        mzx,
                        mzy,
                        mzz,
                        tz);
  }


  /**
   * Creates an affine matrix from this quaternion. This quaternion needs
   * to be normalized for this method to result in a proper rotation matrix.
   * This is not tested at runtime, as this would incur a performance penalty.
   *
   * @param translation translation to use
   * @return affine matrix
   */
  public Affine3D toAffine3D(Vector3D translation) {
    return toAffine3D(w, x, y, z, translation.getX(), translation.getY(), translation.getZ());
  }


  /**
   * Creates an affine matrix from this quaternion. This quaternion needs
   * to be normalized for this method to result in a proper rotation matrix.
   * This is not tested at runtime, as this would incur a performance penalty.
   *
   * @param tx x-translation to use
   * @param ty y-translation to use
   * @param tz z-translation to use
   * @return affine matrix
   */
  public Affine3D toAffine3D(double tx, double ty, double tz) {
    return toAffine3D(w, x, y, z, tx, ty, tz);
  }


  /**
   * Interpolates between this and another quaternion. Interpolation
   * is usually used when animating rotating objects.
   *
   * @param other another quaternion
   * @param t     value between 0.0 and 1.0
   * @return interpolated quaternion
   */
  public Quaternion interpolate(Quaternion other, double t) {
    double u = 1.0 - t;
    double w = u * this.w + t * other.w;
    double x = u * this.x + t * other.x;
    double y = u * this.y + t * other.y;
    double z = u * this.z + t * other.z;
    return new Quaternion(w, x, y, z);
  }

  /**
   * Returns the euler representation of a normalized quaternion.
   *
   * @return Euler
   */
  public Euler toEuler() {
    assert isNormalized(0.001) : "Quaternion should be normalized";
    return toEuler(w, x, y, z);
  }

  /**
   * Converts a normalized quaternion to Euler.
   *
   * @param w W
   * @param x X
   * @param y Y
   * @param z Z
   * @return Euler
   */
  @SuppressWarnings("SuspiciousNameCombination")
  public static Euler toEuler(double w, double x, double y, double z) {
    double test = x * y + z * w;
    if (test > 0.499) {
      double heading = 2 * atan2(x, w);
      double attitude = PI / 2;
      double bank = 0;
      return new Euler(bank, heading, attitude);
    } else if (test < -0.499) {
      double heading = -2 * atan2(x, w);
      double attitude = -PI / 2;
      double bank = 0;
      return new Euler(bank, heading, attitude);
    } else {
      // pre-calculate some squares
      double xx = x * x;
      double yy = y * y;
      double zz = z * z;

      double heading = atan2(2 * y * w - 2 * x * z, 1 - 2 * yy - 2 * zz);
      double attitude = asin(2 * test);
      double bank = atan2(2 * x * w - 2 * y * z, 1 - 2 * xx -
                                                          2 * zz);
      return new Euler(bank, heading, attitude);
    }
  }

  /**
   * Converts a normalized quaternion to axis/angle.
   *
   * @return axis/angle
   */
  public AxisAngle toAxisAngle() {
    assert isNormalized(0.001) : "Quaternion should be normalized";
    return toAxisAngle(w, x, y, z);
  }

  /**
   * Converts normalized Quaternion to an axis/angle rotation.
   *
   * @param w W
   * @param x X
   * @param y Y
   * @param z Z
   * @return rotation in axis angle representation
   */
  public static AxisAngle toAxisAngle(double w, double x, double y, double z) {

    double angle = 2 * acos(w);
    double norm = sqrt(1-w*w);

    if (norm > 0.000001) {
      x /= norm;
      y /= norm;
      z /= norm;
    }
    else {
      x = 1;
      y = 0;
      z = 0;
    }

    return new AxisAngle(new Vector3D(x, y, z), angle);
  }

  /**
   * Normalizes the quaternion
   *
   * @param epsilon if length squared less than this value, return a
   *                non-rotating quaternion
   * @return normalized quaternion
   */
  public Quaternion normalize(double epsilon) {
    double lengthSquared = w * w + x * x + y * y + z * z;

    if (lengthSquared < epsilon) {
      return new Quaternion(1.0, x, y, z);
    } else {
      double scale = 1.0 / sqrt(lengthSquared);
      return new Quaternion(w * scale, x * scale, y * scale, z * scale);
    }
  }

  /**
   * Adds a scaled vector to this quaternion.
   *
   * @param vector vector to add
   * @param scale  scale to apply to the vector before adding
   * @return quaternion
   */
  public Quaternion add(Vector3D vector, double scale) {
    double vx = vector.getX() * scale;
    double vy = vector.getY() * scale;
    double vz = vector.getZ() * scale;
    double w0 = -vx * this.x -vy * this.y - vz * this.z;
    double x0 = vx * this.w + vy * this.z - vz * this.y;
    double y0 = -vx * this.z + vy * this.w + vz * this.x;
    double z0 = vx * this.y - vy * this.x + vz * this.w;

    return new Quaternion(this.w + w0 * 0.5,
                          this.x + x0 * 0.5,
                          this.y + y0 * 0.5,
                          this.z + z0 * 0.5);
  }


  /**
   * Adds a vector to this quaternion.
   *
   * @param vector vector (3-dimensional) to add
   * @return resulting quaternion
   */
  public Quaternion add(Vector vector) {
    Quaternion q =
        new Quaternion(0.0,
                       vector.get(0),
                       vector.get(1),
                       vector.get(2))
            .multiply(this);

    return new Quaternion(
                             w + q.getW() * 0.5,
                             x + q.getX() * 0.5,
                             y + q.getY() * 0.5,
                             z + q.getZ() * 0.5);
  }


  /**
   * The real part of the quaternion
   * @return <i>W</i>
   */
  public double getW() {
    return w;
  }


  /**
   * The first element of the imaginary vector
   * @return <i>X</i>
   */
  public double getX() {
    return x;
  }


  /**
   * The second element of the imaginary vector
   * @return <i>Y</i>
   */
  public double getY() {
    return y;
  }


  /**
   * The third element of the imaginary vector
   * @return <i>Z</i>
   */
  public double getZ() {
    return z;
  }


  /**
   * Adds a vector to this quaternion.
   *
   * @param vector vector to add
   * @return resulting quaternion
   */
  public Quaternion add(Vector3D vector) {
    double vx = vector.getX();
    double vy = vector.getY();
    double vz = vector.getZ();
    double w0 = -vx * this.x -vy * this.y - vz * this.z;
    double x0 = vx * this.w + vy * this.z - vz * this.y;
    double y0 = -vx * this.z + vy * this.w + vz * this.x;
    double z0 = vx * this.y - vy * this.x + vz * this.w;

    return new Quaternion(this.w + w0 * 0.5,
                          this.x + x0 * 0.5,
                          this.y + y0 * 0.5,
                          this.z + z0 * 0.5);
  }
}
