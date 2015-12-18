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

import java.io.Serializable;
import java.util.Optional;

/**
 * Map projection of a spheroid on a 2D surface.
 * @see <a href="https://en.wikipedia.org/wiki/Map_projection">Wikipedia
 * on Map projection</a>
 */
public interface MapProjection extends Serializable {
  /**
   * Convert a location to a point in 2D space.
   *
   * @param latitude  Latitude value (radians)
   * @param longitude Longitude value (radians)
   * @return the point or null if there is no point for the specified location.
   */
  Optional<Vector2D> toPoint(double latitude, double longitude);

  /**
   * Convert a point in 2D space to a geodetic location
   *
   * @param x x-coordinate of the location
   * @param y y-coordinate of the location
   * @return the location, or null if there is no location for the specified
   * point.
   */
  Optional<Geodetic> toGeodetic(double x, double y);

  /**
   * Returns the minimum x-coordinate in this map projection
   * @return x-coordinate
   */
  double getMinimumX();

  /**
   * Returns the minimum y-coordinate in this map projection
   * @return y-coordinate
   */
  double getMinimumY();

  /**
   * Returns the maximum x-coordinate in this map projection
   * @return x-coordinate
   */
  double getMaximumX();

  /**
   * Returns the maximum y-coordinate in this map projection
   * @return y-coordinate
   */
  double getMaximumY();
}
