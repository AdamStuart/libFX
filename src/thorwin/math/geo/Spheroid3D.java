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

import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sinh;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;
import static java.lang.Math.tanh;
import static thorwin.math.Math.TWO_PI;

import thorwin.math.ConvergeException;
import thorwin.math.Vector3D;


/**
 * Spheroid or ellipsoid of revolution.
 */
public final class Spheroid3D implements java.io.Serializable {

  private static final double VOLUME_FACTOR = (4.0 / 3.0) * Math.PI;

  /**
   * The origin of the spheroid
   */
  private final Vector3D origin;

  /**
   * Orientation of the spheroid
   */
  private final Quaternion orientation;

  /**
   * Radius along the spheroid's local x- and y-axis.
   */
  private final double a;

  /**
   * Radius along spheroid's the z-axis.
   */
  private final double c;


  /**
   * Construct a new spheroid
   *
   * @param origin      origin of the spheroid in global coordinate system
   * @param orientation orientation of the spheroid
   * @param a           radius along the spheroid's local x- and y-axis
   * @param c           radius along the spheroid's local z-axis
   */
  public Spheroid3D(Vector3D origin,
                    Quaternion orientation,
                    double a,
                    double c) {
    this.origin = origin;
    this.orientation = orientation;
    this.a = a;
    this.c = c;
  }


  /**
   * Calculates the great-circle distance and forward/reverse azimuth between
   * two geodetic coordinates using the Vincenty algorithm.
   *
   * @param a          radius along the spheroid's local x- and y-axis (for
   *                   Earth the semi-major axis).
   * @param c          radius along the spheroid's local z-axis (for Earth the
   *                   semi-minor axis).
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
   * @throws ConvergeException if the algorithm does not converge
   *                                       within the specified number of
   *                                       iterations.
   * @see <a href="https://en.wikipedia.org/wiki/Vincenty%27s_formulae">
   * Wikipedia on Vincenty's formulae</a>
   */
  public static GreatCircleDistance vincenty(double a,
                                             double c,
                                             double lat0,
                                             double lon0,
                                             double lat1,
                                             double lon1,
                                             double precision,
                                             int iterations) {
    // flattening
    double f = 1 - c / a;

    // pre-calculte some sine and cosine functions
    double u1 = atan((1 - f) * tan(lat0));
    double u2 = atan((1 - f) * tan(lat1));

    double sinU1 = sin(u1);
    double cosU1 = cos(u1);
    double sinU2 = sin(u2);
    double cosU2 = cos(u2);

    double l = lon1 - lon0;

    // start the iteration
    double lambda = l;
    double previousLambda;

    for (int i = 0; i < iterations; i++) {
      double sinLambda = sin(lambda);
      double cosLambda = cos(lambda);

      double sinSigma = sqrt((cosU2 * sinLambda * cosU2 * sinLambda) +
                             (((cosU1 * sinU2) - (sinU1 *
                                                  cosU2 *
                                                  cosLambda)) *
                              ((cosU1 * sinU2) - (sinU1 * cosU2 * cosLambda))));

      if (sinSigma == 0) {
        return new GreatCircleDistance(0, Double.NaN, Double.NaN);
      }

      double cosSigma = (sinU1 * sinU2) + (cosU1 * cosU2 * cosLambda);
      double sigma = atan2(sinSigma, cosSigma);
      double sinAlpha = (cosU1 * cosU2 * sinLambda) / sinSigma;
      double cosSqAlpha = 1 - (sinAlpha * sinAlpha);
      double cos2SigmaM = cosSigma - ((2 * sinU1 * sinU2) / cosSqAlpha);

      // make sure it has a valid value
      if (Double.isNaN(cos2SigmaM)) {
        cos2SigmaM = 0;
      }

      double d = (f / 16) *
                 cosSqAlpha *
                 (4 + (f * (4 - (3 * cosSqAlpha))));

      // store the previous value, as we need to compare it to the one
      // calculated in this iteration
      previousLambda = lambda;

      // calculate lambda
      lambda = l + (1 - d) * f * sinAlpha * (sigma + (d * sinSigma *
                                                      (cos2SigmaM + (d *
                                                                     cosSigma *
                                                                     (-1 + (2 *
                                                                            cos2SigmaM *
                                                                            cos2SigmaM))))));

      // if the difference is negligible, we can finish the calculation
      if (abs(lambda - previousLambda) < precision) {
        double lU2 = (cosSqAlpha * ((a * a) - (c * c))) / (c * c);
        double
            lA =
            1 +
            ((lU2 / 16384) *
             (4096 + (lU2 * (-768 + (lU2 * (320 - (175 * lU2)))))));
        double
            lB =
            (lU2 / 1024) * (256 + (lU2 * (-128 + (lU2 * (74 - (47 * lU2))))));
        double deltaSigma = lB *
                            sinSigma *
                            (cos2SigmaM + ((lB / 4) * ((cosSigma * (-1 + (2 *
                                                                          cos2SigmaM *
                                                                          cos2SigmaM))) -
                                                       ((lB / 6) *
                                                        cos2SigmaM *
                                                        (-3 + (4 *
                                                               sinSigma *
                                                               sinSigma)) *
                                                        (-3 + (4 *
                                                               cos2SigmaM *
                                                               cos2SigmaM))))));

        double distance = c * lA * (sigma - deltaSigma);
        double forward = atan2(cosU2 * sinLambda,
                               (cosU1 * sinU2) - (sinU1 * cosU2 * cosLambda));
        double reverse = atan2(cosU1 * sinLambda,
                               (-sinU1 * cosU2) + (cosU1 * sinU2 * cosLambda));

        return new GreatCircleDistance(distance, forward, reverse);
      }
    }

    // iterations were not enough to converge
    throw new ArithmeticException("not enough iterations to converge");
  }


  /**
   * Returns the origin of the spheroid.
   *
   * @return vector
   */
  public Vector3D getOrigin() {
    return origin;
  }


  /**
   * Returns the orientation of the spheroid.
   *
   * @return quaternion
   */
  public Quaternion getOrientation() {
    return orientation;
  }


  /**
   * Calculates the spheroid's volume.
   *
   * @return volume
   */
  public double volume() {
    return a * a * c * VOLUME_FACTOR;
  }


  /**
   * Returns the Gaussian curvature at the specified latitude.
   *
   * @param latitude latitude in the range <i>&lt;-PI/2, PI/2&gt;</i>
   * @return curvature
   */
  public double gaussianCurvature(double latitude) {
    double aa  = a * a;
    double cc  = c * c;
    double cl  = cos(latitude);
    double den = aa + (cc - aa) * cl * cl;
    return cc / (den * den);
  }


  /**
   * Returns the mean curvature at the specified latitude.
   *
   * @param latitude latitude in the range <i>&lt;-PI/2, PI/2&gt;</i>
   * @return curvature
   */
  public double meanCurvature(double latitude) {
    double aa  = a * a;
    double cc  = c * c;
    double cl  = cos(latitude);
    double v   = (cc - aa) * cl * cl;
    double num = c * (2 * aa + v);
    double den = 2 * a * Math.pow(aa + v, 1.5);

    return num / den;
  }


  /**
   * Returns the flattening (or oblateness) of the spheroid.
   *
   * @return flattening
   */
  public double flattening() {
    return 1 - c / a;
  }


  /**
   * Returns the surface area of the spheroid
   *
   * @return surface area
   */
  public double surface() {
    if (isOblate()) {
      double aa = a * a;
      double cc = c * c;
      double ee = 1 - cc / aa;
      double e = sqrt(ee);
      return TWO_PI * aa * (1 + tanh(e) * (1 - ee) / e);
    }
    else if (isProlate()) {
      double aa = a * a;
      double cc = c * c;
      double ee = 1 - aa / cc;
      double e = sqrt(ee);
      return TWO_PI * aa * (1 + sinh(e) * c / (a * e));
    }
    else {
      // a and c are equal, this means this is a sphere
      return Sphere3D.surface(a);
    }
  }


  /**
   * Returns true if the spheroid is oblate.
   *
   * @return true if oblate
   */
  public boolean isOblate() {
    return c < a;
  }


  /**
   * Returns true if the spheroid is prolate.
   *
   * @return true if prolate
   */
  public boolean isProlate() {
    return c > a;
  }


  /**
   * Calculate the distance between two coordinates using the vincenty method.
   * @param lat0 latitude 0
   * @param lon0 longitude 0
   * @param lat1 latitude 1
   * @param lon1 longitude 1
   * @param precision desired precision
   * @param iterations maximum number of iterations
   * @return distance
   * @throws ConvergeException if the algorithm does not converge
   */
  public double distance(double lat0,
                         double lon0,
                         double lat1,
                         double lon1,
                         double precision,
                         int iterations) {
    return vincenty(a,c,lat0,lon0,lat1,lon1,precision,iterations).getDistance();
  }
}
