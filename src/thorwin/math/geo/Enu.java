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

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.io.Serializable;

import thorwin.math.Matrix;
import thorwin.math.Vector3D;

/**
 * East, North, Up coordinate system. This coordinate system defines a position
 * on earth using a reference point (ECEF coordinate) and three relative
 * coordinates (east, north and up) in meters.
 * @see thorwin.math.geo.Ecef
 * @see thorwin.math.geo.Geodetic
 */
public final class Enu implements Serializable {
  private static final long serialVersionUID = -3257931161932792853L;

  private final double east;

  private final double north;

  private final double up;

  private final Ecef reference;


  /**
   * Constructs a new Enu instance.
   *
   * @param reference reference position in ECEF
   * @param east      number of meters to the east
   * @param north     number of meters to the west
   * @param up        number of meters up
   */
  public Enu(Ecef reference, double east, double north, double up) {
    super();
    this.reference = reference;
    this.east = east;
    this.north = north;
    this.up = up;
  }

  /**
   * Returns the meters to the east of the reference position
   * @return meters east
   */
  public double getEast() {
    return east;
  }

  /**
   * Returns the meters to the north of the reference position
   * @return meters north
   */
  public double getNorth() {
    return north;
  }

  /**
   * Returns the reference position in earth-centered-earth-fixed coordinate
   * system
   * @return Ecef reference position
   */
  public Ecef getReference() {
    return reference;
  }

  /**
   * Returns the meters up from the reference position
   * @return meters up
   */
  public double getUp() {
    return up;
  }

  /**
   * Converts this coordinate to earth-centered-earth-fixed
   * @return Ecef coordinate
   */
  public Ecef toECEF() {
    Geodetic referenceGeodetic = reference.toGeodetic();

    double latitude = referenceGeodetic.getLatitude();
    double longitude = referenceGeodetic.getLongitude();

    Matrix a = Matrix.columnPacked(3,
                                       -sin(longitude),
                                       cos(longitude),
                                       0,

                                       -sin(latitude) * cos(longitude),
                                       -sin(latitude) * sin(longitude),
                                       cos(latitude),

                                       cos(latitude) * cos(longitude),
                                       sin(longitude) * cos(latitude),
                                       sin(latitude));

    Matrix b = Matrix.columnPacked(3, east, north, up);

    Matrix c = Matrix.columnPacked(3,
                                       reference.getX(),
                                       reference.getY(),
                                       reference.getZ());

    Matrix d = a.multiply(b);
    Matrix e = d.add(c);

    return new Ecef(e.get(0, 0), e.get(1, 0), e.get(2, 0));
  }

  /**
   * Returns east, north and up as a 3-dimensional vector
   * @return 3-dimensional vector
   */
  public Vector3D toVector3D() {
    return new Vector3D(east, north, up);
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(east);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(north);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(up);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    result = 31 * result + reference.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if ((obj == null) || (getClass() != obj.getClass())) return false;

    Enu enu = (Enu) obj;

    return Double.compare(enu.east, east) == 0 && Double.compare(enu.north,
                                                                 north)
        == 0 && Double.compare(
        enu.up,
        up) == 0 && reference.equals(enu.reference);

  }

  @Override
  public String toString() {
    return "Enu{" + "east=" + east + ", north=" + north + ", up=" + up + ", " +
        "reference=" + reference + '}';
  }
}
