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


import static java.lang.Math.PI;
import static thorwin.math.Math.TWO_PI;

/**
 * Bounds of a rectangular area in geodetic coordinates (may multiply
 * anti-meridian by defining the eastern border to the left of the western
 * border).
 */
public final class GeodeticBounds implements java.io.Serializable {

  private final double west;
  private final double east;
  private final double south;
  private final double north;

  /**
   * Constructs a new GeodeticBounds instance. Note that if the south-boundary
   * is specified to be north of the north-boundary, both boundaries are
   * automatically corrected.
   *
   * @param south south
   * @param west  west
   * @param north north
   * @param east  west
   */
  public GeodeticBounds(double south, double west, double north, double east) {
    if (!Geodetic.isValid(south, west) || !Geodetic.isValid(north, east)) {
      throw new IllegalArgumentException("Invalid lat/lon value");
    }

    if (south > north) {
      this.south = north;
      this.west = west;
      this.north = south;
      this.east = east;
    } else {
      this.south = south;
      this.west = west;
      this.north = north;
      this.east = east;
    }
  }

  /**
   * Constructs a new GeodeticBounds instance. Note that if south-west-boundary
   * is specified to be to the north of the north-east-boundary, the north and
   * south bounds are automatically corrected.
   *
   * @param southWest southwest bounds
   * @param northEast northeast bounds
   */
  public GeodeticBounds(Geodetic southWest, Geodetic northEast) {

    if (southWest.getLatitude() > northEast.getLatitude()) {
      south = northEast.getLatitude();
      west = southWest.getLongitude();
      north = southWest.getLatitude();
      east = northEast.getLongitude();
    } else {
      south = southWest.getLatitude();
      west = southWest.getLongitude();
      north = northEast.getLatitude();
      east = northEast.getLongitude();
    }
  }


  /**
   * Return the center of the bounds
   * @return the center of the bounds.
   */
  public Geodetic getCenter() {
    double latitude = south / 2.0 + north / 2.0;

    if (isOverAntiMeridian()) {
      // Longitude for west has a higher value than for east,
      // which means we need to change the calculation for the center
      // a little. We place the east to the right of west by adding
      // 360 degrees (2PI), and than take the average of the two.
      // After calculating the center longitude, it is normalized as
      // it may have ended up with a value greater than 180 deg.

      // The following calculation is a reduced form of :
      // (east + 2 PI)/2.0 + west/2.0
      double longitude = west / 2.0 + PI + east / 2.0;

      // correct longitude if center wrapped around 180 deg.
      if (longitude > Math.PI) longitude -= TWO_PI;

      return new Geodetic(latitude, longitude);
    } else {
      // West and east have regular values (west to the left of east).
      // This means we can calculate the center longitude by taking the
      // average of the two.
      double longitude = west / 2.0 + east / 2.0;

      return new Geodetic(latitude, longitude);
    }
  }

  /**
   * Creates a union of this bounds and the other. The union is the smallest
   * extent that contains both bounds.
   *
   * @param other other geodetic bounds
   * @return union of the bounds
   */
  public GeodeticBounds union(GeodeticBounds other) {

    // Make sure we use the bounds with the largest extent as THIS.
    if (getLongitudeExtent() < other.getLongitudeExtent()) {
      return other.union(this);
    }

    double minSouth = Math.min(south, other.south);
    double maxNorth = Math.max(north, other.north);
    double minExtent = getLongitudeExtent(); // guaranteed larger than other

    GeodeticBounds union0 = new GeodeticBounds(minSouth,
                                               other.west,
                                               maxNorth,
                                               east);
    GeodeticBounds union1 = new GeodeticBounds(minSouth,
                                               west,
                                               maxNorth,
                                               other.east);

    if (union0.getLongitudeExtent() > minExtent &&
        union0.getLongitudeExtent() < union1.getLongitudeExtent()) {

      // Union #0 seems to have a greater extent than THIS and a smaller
      // extent than Union #1, so this is the candidate we were looking
      // for.
      return union0;
    } else if (union1.getLongitudeExtent() > minExtent) {

      // union #1 seems to have a greater extent than THIS, so we can
      // return it.
      return union1;
    } else {
      // Union #0 and #1 are not good candidates as they reduce the
      // longitude extent of this bounds.
      // Because we made sure THIS has the largest extent, we can use
      // the longitude bounds of this instance.
      return new GeodeticBounds(minSouth, west, maxNorth, east);
    }
  }

  /**
   * Extends the bounds to contain the specified location.
   *
   * @param location location to extend
   * @return union of the bounds
   */
  public GeodeticBounds extend(Geodetic location) {

    // Make sure we actually need to extend the bounds.
    if (contains(location)) {
      return this;
    }

    double minSouth = Math.min(south, location.getLatitude());
    double maxNorth = Math.max(north, location.getLatitude());
    double minExtent = getLongitudeExtent();

    GeodeticBounds union0 = new GeodeticBounds(minSouth,
                                               location.getLongitude(),
                                               maxNorth,
                                               east);
    GeodeticBounds union1 = new GeodeticBounds(minSouth,
                                               west,
                                               maxNorth,
                                               location.getLongitude());

    if (union0.getLongitudeExtent() > minExtent &&
        union0.getLongitudeExtent() < union1.getLongitudeExtent()) {

      // Union #0 seems to have a greater extent than THIS and a smaller
      // extent than Union #1, so this is the candidate we were looking
      // for.
      return union0;
    } else if (union1.getLongitudeExtent() > minExtent) {

      // union #1 seems to have a greater extent than THIS, so we can
      // return it.
      return union1;
    } else {
      // Union #0 and #1 are not good candidates as they reduce the
      // longitude extent of this bounds.
      return new GeodeticBounds(minSouth, west, maxNorth, east);
    }
  }

  /**
   * Determine if a geodetic location is contained in these bounds.
   * @param location location
   * @return true if the specific coordinate is contained within the bounds.
   */
  public boolean contains(Geodetic location) {
    return contains(location.getLatitude(), location.getLongitude());
  }

  /**
   * Returns the longitude extent
   * @return The width of the bounds (in range <i>[0,2PI&lt;</i>).
   */
  public double getLongitudeExtent() {
    if (west > east) {
      return TWO_PI - (west - east);
    } else {
      return east - west;
    }
  }

  /**
   * Determines if a geodetic coordinate is contained within these bounds
   * @param latitude  latitude (in radians)
   * @param longitude longitude (in radians)
   * @return true if the specific coordinate is contained within the bounds.
   */
  public boolean contains(double latitude, double longitude) {
    if (!Geodetic.isValid(latitude, longitude)) {
      throw new IllegalArgumentException("Not a valid geodetic " +
                                             "coordinate");
    }

    // if bounds  wrap around anti-meridian...
    if (isOverAntiMeridian()) {
      return (latitude <= north && latitude >= south) &&
          (longitude <= east || longitude >= west);
    }

    // bounds are regular...
    else {
      return latitude <= north && latitude >= south &&
          longitude >= west && longitude <= east;
    }
  }

  /**
   * Returns true if the bounds fall over the anti-meridian
   * @return true if the longitude of the east is actually smaller than the
   * west.
   */
  public boolean isOverAntiMeridian() {
    return east < west;
  }

  /**
   * Determine if other bounds are contained within these bounds
   * @param other other bounds
   * @return true if the bounds are contained within this bounds.
   */
  public boolean contains(GeodeticBounds other) {
    return contains(other.south, other.west) &&
        contains(other.north, other.east);
  }

  /**
   * Determine if other bounds intersect with these
   * @param other The other bounds
   * @return true if this bounds share any area with the other
   */
  public boolean intersects(GeodeticBounds other) {
    Rectangle2D r1 = new Rectangle2D(west,
                                     south,
                                     getLongitudeExtent(),
                                     getLatitudeExtent());
    Rectangle2D r2 = new Rectangle2D(other.west,
                                     other.south,
                                     other.getLongitudeExtent(),
                                     other.getLatitudeExtent());

    return r1.intersects(r2);
  }

  /**
   * Returns the latitude extent
   * @return The height of the bounds (in range [0,PI]).
   */
  public double getLatitudeExtent() {
    return north - south;
  }

  /**
   * Returns the south west location
   * @return The south-west boundary location.
   */
  public Geodetic getSouthWest() {
    return new Geodetic(south, west);
  }

  /**
   * Returns the north-east location
   * @return The north-east boundary location.
   */
  public Geodetic getNorthEast() {
    return new Geodetic(north, east);
  }

  /**
   * Returns the south-east location
   * @return The south-east boundary location.
   */
  public Geodetic getSouthEast() {
    return new Geodetic(south, east);
  }

  /**
   * Returns the north-west location
   * @return The north-east boundary location.
   */
  public Geodetic getNorthWest() {
    return new Geodetic(north, west);
  }

  /**
   * Returns the western boundary
   * @return longitude
   */
  public double getWest() {
    return west;
  }

  /**
   * Returns the eastern boundary
   * @return longitude
   */
  public double getEast() {
    return east;
  }

  /**
   * Returns the southern boundary
   * @return latitude
   */
  public double getSouth() {
    return south;
  }

  /**
   * Returns the northern boundary
   * @return latitude
   */
  public double getNorth() {
    return north;
  }

  /**
   * Subdivides the bounds in four parts, along the center
   *
   * @return subdivided {@code GeodeticBounds} parts
   */
  public GeodeticBounds[] subdivide() {
    GeodeticBounds[] bounds = new GeodeticBounds[4];

    Geodetic center = getCenter();
    double clat = center.getLatitude();
    double clon = center.getLongitude();

    bounds[0] = new GeodeticBounds(south, west, clat, clon);
    bounds[1] = new GeodeticBounds(clat, west, north, clon);
    bounds[2] = new GeodeticBounds(clat, clon, north, east);
    bounds[3] = new GeodeticBounds(south, clon, clat, east);

    return bounds;
  }


  /**
   * Subdivides the bounds in four parts, along the center, in multiple
   * iterations.
   *
   * @param iterations number of iterations to perform
   * @return an array of <i>pow(4, iterations)</i> geodetic bounds.
   */
  public GeodeticBounds[] subdivide(int iterations) {

    switch (iterations) {

      case 0:
        return new GeodeticBounds[]{this};

      case 1:
        return subdivide();

      default:
        GeodeticBounds[] recursive = subdivide(iterations - 1);
        int length = recursive.length * 4;
        GeodeticBounds[] bounds = new GeodeticBounds[length];

        for (int i = 0; i < recursive.length; i++) {
          System.arraycopy(recursive[i].subdivide(),
                           0,
                           bounds,
                           i * 4,
                           4);
        }

        return bounds;
    }
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(west);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(east);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(south);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(north);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GeodeticBounds that = (GeodeticBounds) o;

    return Double.compare(that.east,
                          east) == 0 && Double.compare(that.north,
                                                       north) == 0 &&
        Double.compare(
            that.south,
            south) == 0 && Double.compare(that.west, west) == 0;

  }

  @Override
  public String toString() {
    return "GeodeticBounds{" + "west=" + Math.toDegrees(west) + ", east=" +
        Math.toDegrees(
        east) + ", " +
        "south=" + Math.toDegrees(south) + ", north=" + Math.toDegrees(north)
        + ", " +
        "latitude extent=" + Math.toDegrees(getLatitudeExtent()) + ", " +
        "longitude extent=" + Math.toDegrees
        (getLongitudeExtent()) + '}';
  }
}
