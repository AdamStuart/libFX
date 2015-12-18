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
 * Lower/Upper decomposition.
 *
 * @see <a href="https://en.wikipedia.org/wiki/LU_decomposition"> Wikipedia on
 * the LU decomposition</a>
 */
public interface LU extends java.io.Serializable {

  /**
   * Returns the lower triangular matrix
   *
   * @return matrix L
   */
  Matrix getL();

  /**
   * Returns the upper triangular matrix
   *
   * @return matrix U
   */
  Matrix getU();

  /**
   * Solves <i>A&times;X = B</i>. Where <i>A</i> is the matrix that was
   * decomposed, <i>B</i> the other matrix.
   *
   * @param matrix the other matrix (<i>B</i>)
   * @return the solution matrix, if any
   */
  Optional<Matrix> solve(Matrix matrix);

  /**
   * Returns the pivot permutation vector
   *
   * @return array of indices
   */
  int[] getPivot();

  /**
   * Returns the determinant of the matrix. The matrix must be square to be ably
   * to determine the discriminant.
   *
   * @return determinant
   */
  double det();

}
