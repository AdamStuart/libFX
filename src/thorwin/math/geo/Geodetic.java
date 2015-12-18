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

import java.io.Serializable;
import java.util.Locale;

import static java.lang.Math.*;


/**
 * Geodetic coordinate on WGS 84 geodetic datum.
 */
public final class Geodetic implements Serializable {

  public static final double MAX_LATITUDE = PI / 2;

  public static final double MAX_LONGITUDE = PI;

  public static final double MIN_LATITUDE = -PI / 2;

  public static final double MIN_LONGITUDE = -PI;

  /**
   * The mean radius of the WGS84 spheroid when modelled as a sphere. Some other
   * algorithms use 6371km for the earth's radius, but we use this as this is
   * for WGS84 spheroid.
   */
  public static final double WGS84_EARTH_RADIUS = 6367444.6575;

  /**
   * WGS84 Constant.
   */
  public static final double WGS84_FIRST_ECCENTRICITY_SQ = 6.69437999014E-3;

  /**
   * WGS84 Second Eccentricity Squared Constant.
   */
  public static final double WGS84_SECOND_ECCENTRICITY_SQ = 6.73949674228E-3;

  /**
   * WGS84 Constant.
   */
  public static final double WGS84_SEMI_MAJOR_AXIS = 6378137.0;   // m

  /**
   * WGS84 Constant.
   */
  public static final double WGS84_SEMI_MINOR_AXIS = 6356752.315; // m

  /**
   * Accuracy constant
   */
  private static final double EPSILON = 1.0e-12;

  /**
   * Serial version unique identifier
   */
  private static final long serialVersionUID = 6986501233033414700L;

  /**
   * Altitude (may be NaN if unknown)
   */
  private final double altitude;

  /**
   * Latitude in radians
   */
  private final double latitude;

  /**
   * Longitude in radians
   */
  private final double longitude;


  /**
   * Constructs a geodetic coordinate (without a defined altitude).
   *
   * @param latitude  latitude <i>[-PI/2, PI]</i>
   * @param longitude longitude <i>[-PI,PI]</i>
   */
  public Geodetic(double latitude, double longitude) {
    this(latitude, longitude, Double.NaN);
  }


  /**
   * Constructs a geodetic coordinate.
   *
   * @param latitude  latitude <i>[-PI/2, PI]</i>
   * @param longitude longitude <i>[-PI,PI]</i>
   * @param altitude  altitude in meters above WGS-84 (or NaN if undefined)
   */
  public Geodetic(double latitude, double longitude, double altitude) {
    super();

    if (isValid(latitude, longitude, altitude)) {
      this.latitude = latitude;
      this.longitude = longitude;
      this.altitude = altitude;
    }
    else {
      // Create a debug message for the exception.
      // This message is intended for the programmer to understand why
      // the parameters
      // are illegal.
      String format = "latitude=%f longitude=%f alitude=%f";
      String message = String.format(Locale.US,
                                     format,
                                     latitude,
                                     longitude,
                                     altitude);
      throw new IllegalArgumentException(message);
    }
  }


  private static boolean isValid(double latitude,
                                 double longitude,
                                 double altitude) {
    return isValid(latitude, longitude) && isValidAltitude(altitude);
  }


  /**
   * Tests the validity of a geodetic coordinate
   *
   * @param latitude  latitude
   * @param longitude longitude
   * @return true if the specified latitude and longitude are valid
   */
  public static boolean isValid(double latitude, double longitude) {
    return isValidLatitude(latitude) && isValidLongitude(longitude);
  }


  /**
   * Tests if the altitude is valid
   *
   * @param altitude altitude in meters
   * @return true if the specified value is a valid altitude
   */
  private static boolean isValidAltitude(double altitude) {
    return !Double.isInfinite(altitude);
  }


  /**
   * Tests if the latitude is valid
   *
   * @param latitude latitude in radians
   * @return true if the specified value is a valid latitude
   */
  public static boolean isValidLatitude(double latitude) {
    return !Double.isInfinite(latitude) && !Double.isNaN(latitude) &&
           (latitude >= MIN_LATITUDE) && (latitude <= MAX_LATITUDE);
  }


  /**
   * Tests if the longitude is valid
   *
   * @param longitude longitude in radians
   * @return true if the specified value is a valid longitude
   */
  public static boolean isValidLongitude(double longitude) {
    return !Double.isInfinite(longitude) && !Double.isNaN(longitude) &&
           (longitude >= MIN_LONGITUDE) && (longitude <= MAX_LONGITUDE);
  }


  /**
   * Calculates the great-circle distance between the two locations using the
   * Haversine formula. Average radius of the WGS84 spheroid is used. This is a
   * fast, but quite inaccurate calculation. For a more accurate algorithm, use
   * the {@code vincenty} method.
   *
   * @param lat0 latitude of first location
   * @param lon0 longitude of first location
   * @param lat1 latitude of second location
   * @param lon1 longitude of second location
   * @return the distance
   * @see <a href="https://en.wikipedia.org/wiki/Haversine_formula">Wikipedia on
   * the Haversine formula</a>
   */
  public static double haversine(double lat0,
                                 double lon0,
                                 double lat1,
                                 double lon1) {
    return Sphere3D.haversine(lat0, lon0, lat1, lon1, WGS84_EARTH_RADIUS);
  }


  /**
   * Calculates the great-circle distance and forward/reverse azimuth between
   * two geodetic coordinates using the GreatCircleDistance algorithm. This
   * algorithm can yield very accurate results, as it uses the WGS84 spheriod
   * (and not the approximate sphere).
   *
   * @param lat0       latitude of first location
   * @param lon0       longitude of first location
   * @param lat1       latitude of second location
   * @param lon1       longitude of second location
   * @param iterations maximum number of iterations to use. The formulate needs
   *                   to converge to a value within 1E-12 with this number of
   *                   iterations to yield a value.
   * @return vincenty
   * @throws ArithmeticException if the algorithm does not converge
   *                                       within the specified number of
   *                                       iterations.
   * @see <a href="https://en.wikipedia.org/wiki/Vincenty%27s_formulae">
   * Wikipedia on GreatCircleDistance's formulae</a>
   */
  public static GreatCircleDistance vincenty(double lat0,
                                             double lon0,
                                             double lat1,
                                             double lon1,
                                             int iterations) {
    return vincenty(lat0, lon0, lat1, lon1, EPSILON, iterations);
  }


  /**
   * Calculates the great-circle distance and forward/reverse azimuth between
   * two geodetic coordinates using the GreatCircleDistance algorithm.This
   * algorithm can yield very accurate results, as it uses the WGS84 spheriod
   * (and not the approximate sphere).
   *
   * @param lat0       latitude of first location
   * @param lon0       longitude of first location
   * @param lat1       latitude of second location
   * @param lon1       longitude of second location
   * @param precision  the precision used to stop the iteration. The formula
   *                   needs to converge within this precision to yield a
   *                   value.
   * @param iterations maximum number of iterations to use. The formulate needs
   *                   to converge to the specified precision with this number
   *                   of iterations to yield a value.
   * @return vincenty
   * @throws ArithmeticException if the algorithm does not converge
   *                                       within the specified number of
   *                                       iterations.
   * @see <a href="https://en.wikipedia.org/wiki/Vincenty%27s_formulae">
   * Wikipedia on GreatCircleDistance's formulae</a>
   */
  public static GreatCircleDistance vincenty(double lat0,
                                             double lon0,
                                             double lat1,
                                             double lon1,
                                             double precision,
                                             int iterations) {
    return Spheroid3D.vincenty(WGS84_SEMI_MAJOR_AXIS,
                               WGS84_SEMI_MINOR_AXIS,
                               lat0,
                               lon0,
                               lat1,
                               lon1,
                               precision,
                               iterations);
  }


  /**
   * Utility method for converting an angle into degree-minute-second format.
   *
   * @param rad value in radians
   * @return degrees-minutes-seconds
   */
  private static String toDegreesMinutesSeconds(double rad) {
    double deg      = toDegrees(rad);
    double degFloor = floor(deg);
    double min      = (deg - degFloor) * 60.0;
    double minFloor = floor(min);
    double sec      = (min - minFloor) * 60.0;
    double secFloor = floor(sec);

    return String.format("%.0f\u00B0%.0f'%.0f\"", degFloor, minFloor, secFloor);
  }


  /**
   * Calculates the destination. The altitude is preserved, if any. Note that
   * the calculation uses a sphere to model the Earth, which is limited in
   * precision.
   *
   * @param bearing  bearing to travel in
   * @param distance distance to travel (in meters)
   * @return the destination
   */
  public Geodetic destination(double bearing, double distance) {
    Geodetic destination = destination(latitude, longitude, bearing, distance);

    if (hasAltitude()) {
      destination = new Geodetic(destination.latitude,
                                 destination.longitude,
                                 altitude);
    }

    return destination;
  }


  /**
   * Calculates the destination when travelling from the specified location in
   * the direction of the bearing over a distance in meters. Note that the
   * calculation uses a sphere to model the Earth, which is limited in
   * precision.
   *
   * @param lat      latitude of start
   * @param lon      longitude of start
   * @param bearing  bearing
   * @param distance distance in meters
   * @return the location
   */
  public static Geodetic destination(double lat,
                                     double lon,
                                     double bearing,
                                     double distance) {
    return destination(lat, lon, bearing, distance, WGS84_EARTH_RADIUS);
  }


  /**
   * @return true if this geodetic coordinate has a defined altitude
   */
  public boolean hasAltitude() {
    return !Double.isNaN(altitude);
  }


  /**
   * Calculates the destination when traveling from the specified location in
   * the direction of the bearing over a distance in meters.
   *
   * @param lat      latitude of start
   * @param lon      longitude of start
   * @param bearing  bearing
   * @param distance distance in meters
   * @param radius   earth radius
   * @return the location
   */
  private static Geodetic destination(double lat,
                                      double lon,
                                      double bearing,
                                      double distance,
                                      double radius) {
    double dr     = distance / radius;
    double sinLat = sin(lat);
    double cosLat = cos(lat);
    double sinDr  = sin(dr);
    double cosDr  = cos(dr);
    double lat1   = asin((sinLat * cosDr) + (cosLat * sinDr * cos(bearing)));
    double lon1 = lon + atan2(sin(bearing) * sinDr * cosLat,
                              cosDr - (sinLat * sin(lat)));

    return new Geodetic(lat1, lon1);
  }


  /**
   * Calculates the great-circle distance between this location and the
   * specified location in meters. The Haversine function is used to calculate
   * the distance. If greater than approximately 0.5% accuracy is needed, take a
   * look at the {@code vincenty} method.
   *
   * @param latitude  latitude in radians
   * @param longitude longitude in radians
   * @return the distance
   */
  public double distance(double latitude, double longitude) {
    return Sphere3D.haversine(this.latitude,
                              this.longitude,
                              latitude,
                              longitude,
                              WGS84_EARTH_RADIUS);
  }


  /**
   * Calculates the great-circle distance between this location and the
   * specified location in meters. The Haversine function is used to calculate
   * the distance. If this coordinate and the parameter coordinate both have a
   * defined altitude, the distance is incremented to account for the difference
   * (using hypotenuse).
   * <p>
   * If greater than approximately 0.5% accuracy is needed, take a look at the
   * {@code vincenty} method.
   *
   * @param to location
   * @return the distance
   */
  public double distance(Geodetic to) {
    double distance = Sphere3D.haversine(latitude,
                                         longitude,
                                         to.latitude,
                                         to.longitude,
                                         WGS84_EARTH_RADIUS);

    if (hasAltitude() && to.hasAltitude()) {
      double delta = abs(to.altitude - altitude);

      distance = hypot(distance, delta);
    }

    return distance;
  }


  /**
   * Calculate the forward azimuth
   *
   * @param to location
   * @return the forward azimuth
   */
  public double forwardAzimuth(Geodetic to) {
    return forwardAzimuth(latitude, longitude, to.latitude, to.longitude);
  }


  /**
   * Calculate the forward azimuth is the initial bearing that will take you
   * from a location to another.
   *
   * @param lat0 latitude of first location
   * @param lon0 longitude of first location
   * @param lat1 latitude of second location
   * @param lon1 longitude of second location
   * @return the forward azimuth in the range [-PI,PI]
   */
  public static double forwardAzimuth(double lat0,
                                      double lon0,
                                      double lat1,
                                      double lon1) {
    double dlon    = lon1 - lon0;
    double cosLat1 = cos(lat1);
    double y       = sin(dlon) * cosLat1;
    double x = (cos(lat0) * sin(lat1)) - (sin(lat0) * cosLat1 * cos(dlon));
    return atan2(y, x);
  }


  /**
   * Returns the altitude in meters above WGS84 geoid.
   *
   * @return altitude, or NaN if no altitude is known
   */
  public double getAltitude() {
    return altitude;
  }


  /**
   * Return the latitude in radians
   *
   * @return latitude
   */
  public double getLatitude() {
    return latitude;
  }


  /**
   * Return the longitude in radians
   *
   * @return longitude
   */
  public double getLongitude() {
    return longitude;
  }


  /**
   * Calculates the midpoint between the this and the specified location.
   *
   * @param latitude  latitude in radians
   * @param longitude longitude in radians
   * @return mid point between this and the other location.
   */
  public Geodetic midpoint(double latitude, double longitude) {
    return midpoint(this.latitude, this.longitude, latitude, longitude);
  }


  /**
   * Calculates the midpoint between two geodetic coordinates.
   *
   * @param lat0 latitude of first location
   * @param lon0 longitude of first location
   * @param lat1 latitude of second location
   * @param lon1 longitude of second location
   * @return mid-point between two locations
   */
  public static Geodetic midpoint(double lat0,
                                  double lon0,
                                  double lat1,
                                  double lon1) {
    double dlon      = lon1 - lon0;
    double cosLat0   = cos(lat0);
    double cosLat1   = cos(lat1);
    double bx        = cosLat1 * cos(dlon);
    double by        = cosLat1 * sin(dlon);
    double cosLat0Bx = cosLat0 + bx;
    double lat = atan2(sin(lat0) + sin(lat1), sqrt((cosLat0Bx * cosLat0Bx) +
                                                   (by * by)));
    double lon = lon0 + atan2(by, cosLat0 + bx);

    return new Geodetic(lat, lon);
  }


  /**
   * Calculate the midpoint between the this and the location 'to'. If both
   * geodetic coordinates have a defined altitude, the result will contain the
   * average altitude.
   *
   * @param to location
   * @return mid point between this and the other location.
   */
  public Geodetic midpoint(Geodetic to) {
    Geodetic midpoint = midpoint(latitude,
                                 longitude,
                                 to.latitude,
                                 to.longitude);

    if (hasAltitude() && to.hasAltitude()) {
      // calculate the average altitude
      double alt = (altitude + to.altitude) / 2.0;

      // matrix a new midpoint
      midpoint = new Geodetic(midpoint.latitude, midpoint.longitude, alt);
    }

    return midpoint;
  }


  /**
   * Convert to earth-centered-earth-fixed coordinate
   *
   * @return Ecef
   */
  public Ecef toECEF() {
    return toECEF(latitude, longitude, altitude);
  }


  /**
   * Convert a geodetic coordinate to ECEF
   *
   * @param lat      latitude in radians
   * @param lon      longitude in radians
   * @param altitude altitude in meters (if NaN, 0 is assumed)
   * @return coordinate in Earth Centered Earth Fixed (Ecef) coordinate.
   */
  public static Ecef toECEF(double lat, double lon, double altitude) {
    double a  = WGS84_SEMI_MAJOR_AXIS;
    double e2 = WGS84_FIRST_ECCENTRICITY_SQ;
    double h  = Double.isNaN(altitude) ? 0 : altitude;

    // pre-calculate duplicate functions
    double cosLat = cos(lat);
    double sinLat = sin(lat);
    double n      = a / sqrt(1 - (e2 * sinLat * sinLat));

    // calculate x,y,z
    double x = (n + h) * cosLat * cos(lon);
    double y = (n + h) * cosLat * sin(lon);
    double z = ((n * (1 - e2)) + h) * sinLat;

    return new Ecef(x, y, z);
  }


  @Override
  public int hashCode() {
    int  result;
    long temp;
    temp = Double.doubleToLongBits(altitude);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(latitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(longitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }

    Geodetic geodetic = (Geodetic) obj;

    return Double.compare(geodetic.latitude, latitude) == 0 &&
           Double.compare(geodetic.longitude, longitude) == 0 &&
           Double.compare(geodetic.altitude, altitude) == 0;

  }


}
