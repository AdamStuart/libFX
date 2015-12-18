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
 * Implementation of an 3-dimensional vector builder
 */
final class Vector3DBuilder extends VectorBuilder<Vector3D> {

  /**
   * The x-coordinate
   */
  private double x;

  /**
   * The y-coordinate
   */
  private double y;

  /**
   * The z-coordinate
   */
  private double z;

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
      case 2:
        z = value;
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
      case 2: return z;
      default:
        throw new IllegalArgumentException("Invalid index: " + index);
    }
  }

  /**
   * Constructs the immutable vector
   *
   * @return a new 3-dimensional vector
   */
  public Vector3D toVector() {
    return new Vector3D(x,y,z);
  }
}
