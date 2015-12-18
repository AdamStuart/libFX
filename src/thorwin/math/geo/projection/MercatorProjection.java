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

package thorwin.math.geo.projection;

import thorwin.math.geo.Geodetic;

import static java.lang.Math.*;

/**
 * Mercator projection implementation.
 */
public final class MercatorProjection implements CylindricalProjection {

  /**
   * The default Mercator projection with the longitude of the y-axis set to
   * 0.0.
   */
  public static final MercatorProjection DEFAULT = new MercatorProjection(0);
  private static final long serialVersionUID = 3410097050688049707L;

  /**
   * Center longitude.
   */
  private final double l0;

  /**
   * Construct a Mercator map projection
   * @param l0 zero meridian
   */
  public MercatorProjection(double l0) {
    super();
    this.l0 = l0;
  }

  /**
   * @return the longitude of the y-axis
   */
  public double getL0() {
    return l0;
  }

  @SuppressWarnings("SuspiciousNameCombination")
  @Override
  public double toLatitude(double y) {
    double lat = atan(sinh(y));
    return Geodetic.isValidLatitude(lat) ? lat : Double.NaN;
  }

  @Override
  public double toLongitude(double x) {
    double lon = x + l0;
    return Geodetic.isValidLongitude(lon) ? lon : Double.NaN;
  }

  @Override
  public double toY(double latitude) {
    return log(tan((PI / 4.0) + (latitude / 2.0)));
  }

  @Override
  public double toX(double longitude) {
    return longitude - l0;
  }

  @Override
  public String toString() {
    return "MercatorProjection{" + "l0=" + l0 + '}';
  }
}
