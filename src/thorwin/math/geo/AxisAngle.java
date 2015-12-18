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


import thorwin.math.Affine3D;
import thorwin.math.Matrix3x3;
import thorwin.math.Vector3D;

/**
 * Representation of a rotation using an axis and angle. The angle's positive
 * direction determined using the right-hand-rule. <p> <img
 * src="doc-files/AxisAngle-1.png" alt="Right-hand-rule"> </p>
 */
public final class AxisAngle implements java.io.Serializable  {

  /**
   * The axis.
   */
  private final Vector3D axis;

  /**
   * The angle (radians)
   */
  private final double angle;

  /**
   * Constructs an axis angle instance.
   *
   * @param axis  rotation axis
   * @param angle rotation angle (radians)
   */
  public AxisAngle(Vector3D axis, double angle) {
    super();

    if (axis == null) {
      throw new IllegalArgumentException("axis should not be null");
    }

    this.axis = axis;
    this.angle = angle;
  }

  /**
   * Creates an axis/angle rotation from a quaternion.
   *
   * @param quaternion quaternion
   * @return axis/angle rotation
   */
  public static AxisAngle valueOf(Quaternion quaternion) {
    return quaternion.toAxisAngle();
  }

  /**
   * Creates an axis/angle rotation from an euler rotation.
   *
   * @param euler euler rotation
   * @return axis/angle rotation
   */
  public static AxisAngle valueOf(Euler euler) {
    return euler.toAxisAngle();
  }

  /**
   * Converts a rotation in axis/angle format to an Euler rotation
   * @param axis axis
   * @param angle angle (radians)
   * @return The euler rotation
   */
  public static Euler toEuler(Vector3D axis, double angle) {
    return new AxisAngle(axis, angle).toEuler();
  }

  /**
   * Converts the axis/angle rotation to euler angles.
   * @return euler rotation
   */
  public Euler toEuler() {
    return toQuaternion().toEuler();
  }

  /**
   * Converts the axis/angle rotation to a quaternion
   * @return quaternion
   */
  public Quaternion toQuaternion() {
    return toQuaternion(axis, angle);
  }

  /**
   * Converts this axis/angle rotation to a quaternion.
   * @param axis axis
   * @param angle angle (radians)
   * @return quaternion
   */
  public static Quaternion toQuaternion(Vector3D axis, double angle) {
    Vector3D normalized = axis.normalize();
    double halfAngle = angle / 2.0;
    double sinHalfAngle = Math.sin(halfAngle);

    double w = Math.cos(halfAngle);
    double x = normalized.getX() * sinHalfAngle;
    double y = normalized.getY() * sinHalfAngle;
    double z = normalized.getZ() * sinHalfAngle;

    return new Quaternion(w, x, y, z);
  }

  /**
   * Returns the axis of this rotation.
   * @return the axis
   */
  public Vector3D getAxis() {
    return axis;
  }

  /**
   * Returns the angle of this rotation in radians.
   * @return the angle
   */
  public double getAngle() {
    return angle;
  }

  /**
   * Converts this axis/angle rotation to a rotation matrix
   *
   * @return 3x3 matrix
   */
  public Matrix3x3 toMatrix3x3() {
    return toQuaternion().toMatrix3x3();
  }

  /**
   * Converts this axis/angle rotation to an affine matrix
   *
   * @return affine matrix
   */
  public Affine3D toAffine3D() {
    return toQuaternion().toAffine3D();
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    result = axis.hashCode();
    temp = Double.doubleToLongBits(angle);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AxisAngle axisAngle = (AxisAngle) o;

    return Double.compare(axisAngle.angle, angle) == 0 && axis.equals(axisAngle.axis);

  }

  @Override
  public String toString() {
    return axis + " " + angle;
  }

}
