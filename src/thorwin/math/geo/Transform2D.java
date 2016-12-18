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

import thorwin.math.Vector2D;

/**
 * Interface to objects that can transforms 2D coordinates.
 */
@FunctionalInterface
public interface Transform2D extends Serializable {

  /**
   * Transforms a point
   *
   * @param point source point
   * @return transformed point
   */
  default Vector2D transform(Vector2D point) {
    return transform(point.getX(), point.getY());
  }

  /**
   * Transforms a point.
   *
   * @param x x-coordinate of the point
   * @param y y-coordinate of the point
   * @return transformed point
   */
  Vector2D transform(double x, double y);
}
