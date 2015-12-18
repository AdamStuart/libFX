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
 * Rotation around three axis. Heading (y-axis) applied first, attitude
 * (z-axis) applied second, bank (x-axis) applied last.
 * <p>
 *   <img src="doc-files/Euler-1.png"
 *        alt="Order of matrix for each axis">
 * <p>
 * Angle positive direction is determined using the right-hand-rule.
 * <p>
 * <img src="doc-files/Euler-2.png"
 *      alt="Right-hand-rule">
 */
public final class Euler implements java.io.Serializable  {

  /**
   * Rotation around x-axis.
   */
  private final double bank;
  /**
   * Rotation around y-axis
   */
  private final double heading;
  /**
   * Rotation around z-axis
   */
  private final double attitude;

  /**
   * Constructor
   *
   * @param bank     rotation around x-axis
   * @param heading  rotation around y-axis
   * @param attitude rotation around z-axis
   */
  public Euler(double bank, double heading, double attitude) {
    this.bank = bank;
    this.heading = heading;
    this.attitude = attitude;
  }

  /**
   * Creates an Euler rotation from a Quaternion
   *
   * @param quaternion quaternion
   * @return Euler rotation
   */
  public static Euler valueOf(Quaternion quaternion) {
    return quaternion.toEuler();
  }

  /**
   * Creates an Euler rotation from an axis/angle rotation
   *
   * @param axisAngle axis/angle rotation
   * @return Euler rotation
   */
  public static Euler valueOf(AxisAngle axisAngle) {
    return axisAngle.toEuler();
  }

  /**
   * Converts to an axis/angle rotation
   *
   * @return axis/angle rotation
   */
  public AxisAngle toAxisAngle() {
    return toAxisAngle(bank, heading, attitude);
  }

  /**
   * Convert an Euler rotation to an axis/angle rotation
   * @param bank     rotation over x-axis
   * @param heading  rotation over y-axis
   * @param attitude rotation z-axis
   * @return  axis/angle rotation
   */
  public static AxisAngle toAxisAngle(double bank,
                                      double heading,
                                      double attitude) {

    double cosBank = Math.cos(bank * 0.5);
    double sinBank = Math.sin(bank * 0.5);
    double cosHeading = Math.cos(heading * 0.5);
    double sinHeading = Math.sin(heading * 0.5);
    double cosAttitude = Math.cos(attitude * 0.5);
    double sinAttitude = Math.sin(attitude * 0.5);

    double cosHeading_cosAttitude = cosHeading * cosAttitude;
    double sinHeading_sinAttitude = sinHeading * sinAttitude;

    double w = cosHeading_cosAttitude * cosBank - sinHeading_sinAttitude *
        sinBank;
    double x = cosHeading_cosAttitude * sinBank + sinHeading_sinAttitude *
        cosBank;
    double y = sinHeading * cosAttitude * cosBank + cosHeading * sinAttitude
        * sinBank;
    double z = cosHeading * sinAttitude * cosBank - sinHeading * cosAttitude
        * sinBank;

    double angle = 2 * Math.acos(w);
    double norm = Math.sqrt(x * x + y * y + z * z);

    x /= norm;
    y /= norm;
    z /= norm;

    if (!(Double.isFinite(x) && Double.isFinite(y) && Double.isFinite(z))) {
      x = 1.0;
      y = 0.0;
      z = 0.0;
    }
    return new AxisAngle(new Vector3D(x, y, z), angle);
  }

  /**
   * Returns the bank (x-axis) rotation in radians
   * @return double bank rotation
   */
  public double getBank() {
    return bank;
  }

  /**
   * Returns the heading (y-axis) rotation in radians
   * @return double heading rotation
   */
  public double getHeading() {
    return heading;
  }

  /**
   * Returns the attitude (z-axis) rotation in radians
   * @return double attitude rotation
   */
  public double getAttitude() {
    return attitude;
  }

  /**
   * Converts to a Quaternion rotation
   * @return quaternion
   */
  public Quaternion toQuaternion() {
    return toQuaternion(bank, heading, attitude);
  }

  /**
   * Converts an Euler rotation to Quaternion.
   *
   * @param bank     rotation over x-axis
   * @param heading  rotation over y-axis
   * @param attitude rotation z-axis
   * @return quaternion rotation
   */
  public static Quaternion toQuaternion(double bank,
                                        double heading,
                                        double attitude) {

    double cosBank = Math.cos(bank / 2.0);
    double sinBank = Math.sin(bank / 2.0);
    double cosHeading = Math.cos(heading / 2.0);
    double sinHeading = Math.sin(heading / 2.0);
    double cosAttitude = Math.cos(attitude / 2.0);
    double sinAttitude = Math.sin(attitude / 2.0);

    double cosHeading_cosAttitude = cosHeading * cosAttitude;
    double sinHeading_sinAttitude = sinHeading * sinAttitude;

    double w = cosHeading_cosAttitude * cosBank - sinHeading_sinAttitude *
        sinBank;
    double x = cosHeading_cosAttitude * sinBank + sinHeading_sinAttitude *
        cosBank;
    double y = sinHeading * cosAttitude * cosBank + cosHeading * sinAttitude
        * sinBank;
    double z = cosHeading * sinAttitude * cosBank - sinHeading * cosAttitude
        * sinBank;

    return new Quaternion(w, x, y, z);
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(bank);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(heading);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(attitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Euler euler = (Euler) o;

    return Double.compare(euler.attitude, attitude) == 0 && Double.compare(
        euler.bank,
        bank) == 0 && Double.compare(euler.heading, heading) == 0;

  }

  @Override
  public String toString() {
    return bank + " " + heading + " " + attitude;
  }

  /**
   * Converts the Euler rotation to a 3x3 rotation matrix.
   * @return 3x3 rotation matrix
   */
  public Matrix3x3 toMatrix3x3() {
    double cb = Math.cos(bank);
    double sb = Math.sin(bank);
    double ch = Math.cos(heading);
    double sh = Math.sin(heading);
    double ca = Math.cos(attitude);
    double sa = Math.sin(attitude);

    double mxx = ch * ca;
    double mxy = sh*sb - ch*sa*cb;
    double mxz = ch*sa*sb + sh*cb;
    double myx = sa;
    double myy = ca*cb;
    double myz = -ca*sb;
    double mzx = -sh*ca;
    double mzy = sh*sa*cb + ch*sb;
    double mzz = -sh*sa*sb + ch*cb;

    return new Matrix3x3(mxx, mxy, mxz,  myx, myy, myz,  mzx, mzy, mzz);
  }

  /**
   * Converts the Euler rotation to an affine matrix.
   * @return affine matrix
   */
  public Affine3D toAffine3D() {
    double cb = Math.cos(bank);
    double sb = Math.sin(bank);
    double ch = Math.cos(heading);
    double sh = Math.sin(heading);
    double ca = Math.cos(attitude);
    double sa = Math.sin(attitude);

    double mxx = ch * ca;
    double mxy = sh*sb - ch*sa*cb;
    double mxz = ch*sa*sb + sh*cb;
    double myx = sa;
    double myy = ca*cb;
    double myz = -ca*sb;
    double mzx = -sh*ca;
    double mzy = sh*sa*cb + ch*sb;
    double mzz = -sh*sa*sb + ch*cb;

    return new Affine3D(mxx, mxy, mxz,  0, myx, myy, myz, 0, mzx, mzy, mzz, 0);
  }

}
