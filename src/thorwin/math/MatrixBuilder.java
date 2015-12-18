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
 * Builder for constructing a matrix. This class is used for creating
 * custom matrix implementations.
 */
abstract class MatrixBuilder<M extends Matrix> {

  /**
   * Returns the value at the specified index
   *
   * @param row    row index
   * @param column column index
   * @return element value
   */
  public abstract double get(int row, int column);

  /**
   * Sets the value of an element
   *
   * @param row    row index
   * @param column column index
   * @param value  value to set
   */
  public abstract void set(int row, int column, double value);

  /**
   * Convert this builder into a resulting matrix. This builder
   * should not be accessed after calling this method.
   * @return a new matrix
   */
  public abstract M toMatrix();
}
