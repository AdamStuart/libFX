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
 * QR decomposition.
 * @see <a href="https://en.wikipedia.org/wiki/QR_decomposition">
 * Wikipedia on the QR decomposition</a>
 */
public interface QR extends java.io.Serializable {

  /**
   * @return The lower trapezoidal matrix whose getColumnDimension define the reflections
   */
  Matrix getH();

  /**
   * @return The upper triangular factor
   */

  Matrix getR();

  /**
   * @return Return the (economy-sized) orthogonal factor
   */

  Matrix getQ();

  /**
   * Solves the least squares solution of <i>A&times;X = B</i>.
   *
   * @param matrix m with as many rows as A and any number of columns.
   * @return <i>X</i> that minimizes the two norm of <i>Q&times;R&times;X-B</i>.
   */

  Optional<Matrix> solve(Matrix matrix);

  /**
   * Calculates if the matrix is full-rank.
   *
   * @return true if <i>R</i>, and hence <i>A</i>, has full rank.
   */
  boolean isFullRank();

}
