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

import thorwin.math.Vector2D;
import thorwin.math.geo.Geodetic;

import java.util.Optional;

/**
 * A cylindrical projection is a specific map projection in which meridians are mapped
 * to equal spaced lines, and parallels are mapped to horizontal lines.
 */
public interface CylindricalProjection extends MapProjection {

  @Override
  default Optional<Vector2D> toPoint(double latitude, double longitude) {
    return Optional.of(new Vector2D(toX(longitude), toY(latitude)));
  }

  @Override
  default Optional<Geodetic> toGeodetic(double x, double y) {
    double longitude = toLongitude(x);
    double latitude  = toLatitude(y);

    if (Geodetic.isValid(latitude, longitude)) {
      return Optional.of( new Geodetic(latitude, longitude) );
    }
    else {
      return Optional.empty();
    }
  }

  /**
   * Calculate the longitude for the specified x-coordinate
   * @param x x-coordinate
   * @return longitude, or NaN if no longitude available for specified x
   */
  double toLongitude(double x);

  /**
   * Calculate the latitude for the specified y-coordinate
   * @param y y-coordinate
   * @return latitude, or NaN if no latitude available for specified y
   */
  double toLatitude(double y);

  @Override
  default double getMinimumX() {
    return toX(Geodetic.MIN_LONGITUDE);
  }

  @Override
  default double getMinimumY() {
    return toY(Geodetic.MIN_LATITUDE);
  }

  @Override
  default double getMaximumX() {
    return toX(Geodetic.MAX_LONGITUDE);
  }

  @Override
  default double getMaximumY() {
    return toY(Geodetic.MAX_LATITUDE);
  }

  /**
   * Calculate the x-coordinate for a latitude
   * @param longitude longitude
   * @return x-coordinate
   */
  double toX(double longitude);

  /**
   * Calculate the y-coordinate for a latitude
   * @param latitude latitude
   * @return y-coordinate
   */
  double toY(double latitude);
}
