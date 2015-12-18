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

import thorwin.math.Vector3D;

import java.io.Serializable;

import static java.lang.Math.*;


/**
 * Sphere
 */
public final class Sphere3D implements Serializable, Volume {

  private static final double FOUR_THIRDS_PI = (4.0 / 3.0) * Math.PI;
  private static final double FOUR_PI = 4.0 * Math.PI;
  private static final long serialVersionUID = 1343958955594417084L;
  private final double x;
  private final double y;
  private final double z;
  private final double radius;

  /**
   * Constructs a sphere
   * @param origin origin of the sphere
   * @param radius radius of the sphere
   */
  public Sphere3D(Vector3D origin, double radius) {
    this(origin.getX(), origin.getY(), origin.getZ(), radius);
  }

  /**
   * Constructs a sphere
   * @param x      x-coordinate of the origin
   * @param y      y-coordinate of the origin
   * @param z      z-coordinate of the origin
   * @param radius radius of the sphere
   */
  public Sphere3D(double x, double y, double z, double radius) {
    super();
    if (radius < 0)
      throw new IllegalArgumentException("radius should not be negative");
    this.x = x;
    this.y = y;
    this.z = z;
    this.radius = radius;
  }

  /**
   * Returns the radius of the sphere. The radius is the distance from the
   * center of the sphere to its surface.
   * @return radius
   */
  public double getRadius() {
    return radius;
  }

  /**
   * Returns the x-coordinate of the center (origin) of the sphere.
   * @return x-coordinate
   */
  public double getX() {
    return x;
  }

  /**
   * Returns the y-coordinate of the center (origin) of the sphere.
   * @return y-coordinate
   */
  public double getY() {
    return y;
  }

  /**
   * Returns the z-coordinate of the center (origin) of the sphere.
   * @return z-coordinate
   */
  public double getZ() {
    return z;
  }

  /**
   * Returns the vector to the center of the sphere
   * @return origin vector
   */
  public Vector3D getOrigin() {
    return new Vector3D(x, y, z);
  }

  /**
   * Calculates the volume of the sphere
   * @return volume
   */
  public double volume() {
    return FOUR_THIRDS_PI * radius * radius * radius;
  }

  /**
   * Calculates the surface area.
   * @return surface area
   */
  public double surface() {
    return surface(radius);
  }


  /**
   * Calculates the surface area of a sphere
   * @param radius radius of the sphere
   * @return surface area
   */
  public static double surface(double radius) {
    return FOUR_PI * radius * radius;
  }


  /**
   * Calculates the great-circle distance between two locations on the sphere.
   * @param lat0 latitude in range <i>&lt;-PI/2, PI/2&gt;</i>
   * @param lon0 longitude
   * @param lat1 latitude in range <i>&lt;-PI/2, PI/2&gt;</i>
   * @param lon1 longitude
   * @return great circle distance between to locations on the sphere
   */
  public double distance(double lat0, double lon0, double lat1, double lon1) {
    return haversine(lat0, lon0, lat1, lon1, radius);
  }

  /**
   * Calculates the great-circle distance between the two locations using the
   * Haversine formula.
   *
   * @param lat0   latitude of first location
   * @param lon0   longitude of first location
   * @param lat1   latitude of second location
   * @param lon1   longitude of second location
   * @param radius the radius of the earth
   * @return the distance
   * @see <a href="https://en.wikipedia.org/wiki/Haversine_formula">Wikipedia on
   * the Haversine formula</a>
   */
  public static double haversine(double lat0,
                                 double lon0,
                                 double lat1,
                                 double lon1,
                                 double radius) {
    double dlat = lat1 - lat0;
    double dlon = lon1 - lon0;

    double sinDlat = sin(dlat / 2);
    double sinDlon = sin(dlon / 2);

    double a = (sinDlat * sinDlat) + (sinDlon * sinDlon * cos(lat0) *
                                      cos(lat1));
    double c = 2 * atan2(sqrt(a), sqrt(1 - a));

    return radius * c;
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
    temp = Double.doubleToLongBits(radius);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if ((obj == null) || (getClass() != obj.getClass())) return false;

    Sphere3D sphere3D = (Sphere3D) obj;

    return Double.compare(sphere3D.radius, radius) == 0 && Double.compare(
        sphere3D.x,
        x) == 0 && Double.compare(sphere3D.y, y) == 0 && Double.compare(
        sphere3D.z,
        z) == 0;

  }

  @Override
  public String toString() {
    return "Sphere3D{" + "x=" + x + ", y=" + y + ", z=" + z + ", radius=" +
        radius + '}';
  }

  /**
   * Returns the axis aligned bounding box of the shape.
   *
   * @return axis-aligned box
   */
  @Override
  public AABB getBounds() {
    return new AABB(x - radius,
                    x + radius,
                    y - radius,
                    y + radius,
                    z - radius,
                    z + radius);
  }
}

