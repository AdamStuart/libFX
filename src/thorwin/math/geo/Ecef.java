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

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.io.Serializable;

import thorwin.math.Matrix;
import thorwin.math.Vector3D;

/**
 * Earth-Centered-Earth-Fixed coordinate.
 * @see Enu
 * @see thorwin.math.geo.Geodetic
 */
public final class Ecef implements Serializable {

  /**
   * Serial version unique identifier.
   */
  private static final long serialVersionUID = -3257931161932792853L;
  private final double x;
  private final double y;
  private final double z;

  /**
   * Constructs a new Earth-centered-earth-fixed coordinate.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   * @param z z-coordinate
   */
  public Ecef(double x, double y, double z) {
    super();
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Returns the x-coordinate.
   *
   * @return double coordinate value
   */
  public double getX() {
    return x;
  }

  /**
   * Returns the y-coordinate.
   *
   * @return double coordinate value
   */
  public double getY() {
    return y;
  }

  /**
   * Returns the z-coordinate.
   *
   * @return double coordinate value
   */
  public double getZ() {
    return z;
  }

  /**
   * Converts to ENU using a reference point.
   *
   * @param reference reference point
   * @return coordinate in Enu coordinate system
   */
  public Enu toENU(Geodetic reference) {
    return toENU(reference.getLatitude(),
                 reference.getLongitude(),
                 reference.getAltitude());
  }

  /**
   * Converts to ENU using a reference point
   *
   * @param latitude  latitude of reference point
   * @param longitude longitude of reference point
   * @param altitude  altitude of reference point
   * @return coordinate in Enu coordinate system
   */
  public Enu toENU(double latitude, double longitude, double altitude) {
    Ecef reference = Geodetic.toECEF(latitude, longitude, altitude);

    Matrix a = Matrix.columnPacked(3,
                                       -sin(longitude),
                                       -sin(latitude) * cos(longitude),
                                       cos(latitude) * cos(longitude),

                                       cos(longitude),
                                       -sin(latitude) * sin(longitude),
                                       cos(latitude) * sin(longitude),

                                       0,
                                       cos(latitude),
                                       sin(longitude));

    Matrix b = Matrix.columnPacked(3,
                                       x - reference.x,
                                       y - reference.y,
                                       z - reference.z);

    Matrix c = a.multiply(b);

    return new Enu(reference, c.get(0, 0), c.get(1, 0), c.get(2, 0));
  }

  /**
   * Converts this coordinate to a geodetic coordinate. Note that this method
   * only does a basic approximation. This method is relatively fast but a more
   * accurate method is available.
   *
   * @return the coordinate as a geodetic coordinate
   */
  public Geodetic toGeodetic() {
    return toGeodetic(x, y, z);
  }

  /**
   * Calculates latitude, longitude and altitude from Earth Centered Earth Fixed
   * coordinate.
   *
   * @param x x coordinate
   * @param y y coordinate
   * @param z z coordinate
   * @return geodetic coordinate
   */
  public static Geodetic toGeodetic(double x, double y, double z) {
    double a = Geodetic.WGS84_SEMI_MAJOR_AXIS;
    double b = Geodetic.WGS84_SEMI_MINOR_AXIS;
    double fes = Geodetic.WGS84_FIRST_ECCENTRICITY_SQ;
    double seq = Geodetic.WGS84_SECOND_ECCENTRICITY_SQ;
    double p = hypot(x, y);
    double q = atan2(z * a, p * b);
    double sinQ = sin(q);
    double cosQ = cos(q);
    double cosQ3 = cosQ * cosQ * cosQ;
    double sinQ3 = sinQ * sinQ * sinQ;
    double lat = atan2(z + (seq * b * sinQ3), p - (fes * a * cosQ3));
    double lon = atan2(y, x);
    double sinLat = sin(lat);
    double n = a / sqrt(1 - (fes * sinLat * sinLat));
    double alt = (p / cos(lat)) - n;

    // clip the latitude to allowed values (may be slightly off due to
    // rounding errors)
    lat = min(lat, Geodetic.MAX_LATITUDE);
    lat = max(lat, Geodetic.MIN_LATITUDE);

    return new Geodetic(lat, lon, alt);
  }

  /**
   * Converts this coordinate to a geodetic coordinate. The number of iterations
   * can be specified. This is the more accurate method for converting to
   * geodetic coordinates. The higher the number of iterations, the more precise
   * the conversion will be.
   *
   * @param iterations the number of iterations to perform, the higher the more
   *                   accurate
   * @return geodetic coordinate
   */
  public Geodetic toGeodetic(int iterations) {
    return toGeodetic(x, y, z, iterations);
  }

  /**
   * Calculate latitude, longitude and altitude from Earth Centered Earth Fixed
   * coordinate using an precise, iterative algorithm.
   *
   * @param x          x coordinate
   * @param y          y coordinate
   * @param z          z coordinate
   * @param iterations number of iterations.
   * @return geodetic coordinate
   */
  public static Geodetic toGeodetic(double x,
                                    double y,
                                    double z,
                                    int iterations) {
    double a = Geodetic.WGS84_SEMI_MAJOR_AXIS;
    double e2 = Geodetic.WGS84_FIRST_ECCENTRICITY_SQ;
    double p = hypot(x, y);
    double lat = atan2(z, p * (1 - e2));
    double lon = atan2(y, x);
    double alt = 0;

    for (int i = 0; i < iterations; i++) {
      double sinLat = sin(lat);
      double n = a / sqrt(1 - (e2 * sinLat * sinLat));

      alt = p / cos(lat) - n;
      lat = atan2(z, p * (1 - ((e2 * n) / (n + alt))));
    }

    return new Geodetic(lat, lon, alt);
  }

  /**
   * Convert to a {@code Vector3D}
   * @return 3-dimensional vector
   */
  public Vector3D toVector3D() {
    return new Vector3D(x, y, z);
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(z);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if ((obj == null) || (getClass() != obj.getClass())) return false;

    Ecef ecef = (Ecef) obj;

    return Double.compare(ecef.x, x) == 0 && Double.compare(ecef.y, y) == 0 &&
           Double.compare(ecef.z, z) == 0;

  }

  @Override
  public String toString() {
    return "Ecef{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
  }
}
