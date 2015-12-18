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

package thorwin.math;

/**
 * Implementation of an 2-dimensional vector builder
 */
final class Vector2DBuilder extends VectorBuilder<Vector2D> {

  /**
   * The x-coordinate
   */
  private double x;

  /**
   * The y-coordinate
   */
  private double y;

  /**
   * Sets the value at the specified index
   *
   * @param index index
   * @param value value
   */
  public void set(int index, double value) {
    switch (index) {
      case 0:
        x = value;
        break;
      case 1:
        y = value;
        break;
      default:
        throw new IllegalArgumentException("Invalid index: " + index);
    }
  }

  /**
   * Returns the value at the specified index
   *
   * @param index index
   * @return value at the specified index
   */
  @Override
  public double get(int index) {
    switch (index) {
      case 0: return x;
      case 1: return y;
      default:
        throw new IllegalArgumentException("Invalid index: " + index);
    }
  }

  /**
   * Constructs the immutable vector
   *
   * @return the vector
   */
  public Vector2D toVector() {
    return new Vector2D(x,y);
  }
}
