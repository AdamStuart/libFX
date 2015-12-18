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
 * Builder for constructing a vector. This class is used for creating
 * custom vector implementations.
 */
abstract class VectorBuilder<V extends Vector> {

  /**
   * Sets the value at the specified index
   *
   * @param index index
   * @param value value
   */
  public abstract void set(int index, double value);

  /**
   * Returns the value at the specified index
   *
   * @param index index
   * @return value at the specified index
   */
  public abstract double get(int index);

  /**
   * Constructs the immutable vector
   *
   * @return a new vector
   */
  public abstract V toVector();
}
