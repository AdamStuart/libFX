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
 * Eigenvalues and eigenvectors of a real matrix.
 * @see <a href="https://en.wikipedia.org/wiki/Eigendecomposition_of_a_matrix">
 * Wikipedia on the Eigendecomposition</a>
 */
public interface Eigen extends java.io.Serializable  {

  /**
   * Returns the eigenvector matrix
   *
   * @return matrix V
   */
  Matrix getV();

  /**
   * Returns the complex eigen values
   * @return complex vector
   */
  ComplexVector getEigenvalues();

  /**
   * Returns the block diagonal eigenvalue matrix
   *
   * @return matrix D
   */
  Matrix getD();

}
