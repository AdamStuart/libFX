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
 * Singular value decomposition.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Singular_value_decomposition">
 * Wikipedia on the Singular value decomposition</a>
 */
public interface SVD extends java.io.Serializable {

  /**
   * Calculates the two-norm condition number
   *
   * @return The condition
   */
  double condition();

  /**
   * Returns the diagonal matrix of singular values
   *
   * @return matrix S
   */
  Matrix getS();

  /**
   * Returns the one-dimensional array of singular values
   *
   * @return diagonal of <i>S</i>.
   */
  Vector getSingularValues();

  /**
   * Returns the left singular vectors
   *
   * @return <i>U</i>
   */
  Matrix getU();

  /**
   * Return the right singular vectors
   *
   * @return <i>V</i>
   */
  Matrix getV();

  /**
   * Calculates the effective numerical matrix rank
   *
   * @return number of non-negligible singular values.
   */
  int rank();

  /**
   * Calculates the two-norm
   *
   * @return two-norm
   */
  double norm2();
}
