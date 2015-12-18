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

import java.util.Optional;

/**
 * Cholesky decomposition.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Cholesky_decomposition">
 * Wikipedia on the Cholesky Decomposition</a>
 */
public interface Cholesky extends java.io.Serializable {

  /**
   * Returns the triangular factor (left).
   *
   * @return L
   */

  Matrix getL();

  /**
   * Returns the triangular factor (right).
   *
   * @return R
   */
  Matrix getR();

  /**
   * Checks if the decomposition calculated matrix L. The method
   * <code>getL</code> will return R' if L was not calculated.
   *
   * @return True if the decomposition has calculated L
   */
  boolean hasL();

  /**
   * Checks if the decomposition calculated matrix R. The method
   * <code>getL</code> will return L' if R was not calculated.
   *
   * @return True if the decomposition has calculated R
   */
  boolean hasR();

  /**
   * Solve <i>X</i> for <i>A&times;X = B</i>. Will return <i>X</i> so that
   * <i>L&times;L&prime;&times;X = B</i>.
   *
   * @param matrix a matrix with as many rows as A and any number of columns
   * @return the solution matrix, if any
   */
  Optional<Matrix> solve(Matrix matrix);

}
