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

import static java.lang.Math.abs;
import static java.lang.Math.min;

import java.util.Optional;


/**
 * Lower/Upper Decomposition.
 */
final class DefaultLU implements LU {

  /**
   * Pivot sign
   */
  private final int pivSign;

  /**
   * Internal storage of pivot vector.
   */
  private final int[] piv;

  /**
   * lu Matrix.
   */
  private final Matrix lu;


  /**
   * Constructor
   *
   * @param lu      decomposed matrix
   * @param pivSign pivot sign
   * @param piv     pivot vector
   */
  private DefaultLU(Matrix lu, int pivSign, int[] piv) {
    super();
    this.lu = lu;
    this.pivSign = pivSign;
    this.piv = piv;
  }


  /**
   * Creates a lu decomposition using the daxpy-based elimination algorithm.
   *
   * @param matrix  matrix to decompose
   * @return LU decomposition
   */
  static LU daxpy(Matrix matrix) {
    MatrixBuilder<?> LU  = MatrixContext.getInstance().create(matrix);
    int              m   = matrix.getRowDimension();
    int              n   = matrix.getColumnDimension();
    int[]            piv = new int[m];
    for (int i = 0; i < m; i++) {
      piv[i] = i;
    }
    int pivSign = 1;

    // Main loop.
    for (int k = 0; k < n; k++) {
      // Find pivot.
      int p = k;
      for (int i = k + 1; i < m; i++) {
        if (abs(LU.get(i, k)) > abs(LU.get(p, k))) {
          p = i;
        }
      }
      // Exchange if necessary.
      if (p != k) {
        for (int j = 0; j < n; j++) {
          double t = LU.get(p, j);
          LU.set(p, j, LU.get(k, j));
          LU.set(k, j, t);
        }
        int t = piv[p];
        piv[p] = piv[k];
        piv[k] = t;
        pivSign = -pivSign;
      }
      // Compute multipliers and eliminate k-th column.
      if (LU.get(k, k) != 0.0) {
        for (int i = k + 1; i < m; i++) {
          LU.set(i, k, LU.get(i, k) / LU.get(k, k));
          for (int j = k + 1; j < n; j++) {
            LU.set(i, j, LU.get(i, j) - LU.get(i, k) * LU.get(k, j));
          }
        }
      }
    }
    return new DefaultLU(LU.toMatrix(), pivSign, piv);
  }


  /**
   * Creates a LU decomposition using the Crout/Doolittle algorithm.
   *
   * @param A       matrix to decompose
   * @return LU decomposition
   */
  static LU crout(Matrix A) {
    MatrixBuilder<?> LU  = MatrixContext.getInstance().create(A);
    int              m   = A.getRowDimension();
    int              n   = A.getColumnDimension();
    int[]            piv = new int[m];
    for (int i = 0; i < m; i++) {
      piv[i] = i;
    }
    int pivSign = 1;

    // Use a "left-looking", dot-product, Crout/Doolittle algorithm.
    double[] LUcolj = new double[m];

    // Outer loop.

    for (int j = 0; j < n; j++) {

      // Make a copy of the j-th column to localize references.

      for (int i = 0; i < m; i++) {
        LUcolj[i] = LU.get(i, j);
      }

      // Apply previous transformations.

      for (int i = 0; i < m; i++) {

        // Most of the time is spent in the following dot product.

        int kmax = min(i, j);
        double s = 0.0;
        for (int k = 0; k < kmax; k++) {
          s += LU.get(i, k) * LUcolj[k];
        }
        LUcolj[i] -= s;
        LU.set(i, j, LUcolj[i]);
      }

      // Find pivot and exchange if necessary.

      int p = j;
      for (int i = j + 1; i < m; i++) {
        if (abs(LUcolj[i]) > abs(LUcolj[p])) {
          p = i;
        }
      }
      if (p != j) {
        for (int k = 0; k < n; k++) {
          double t = LU.get(p, k);
          LU.set(p, k, LU.get(j, k));
          LU.set(j, k, t);
        }
        int k = piv[p];
        piv[p] = piv[j];
        piv[j] = k;
        pivSign = -pivSign;
      }

      // Compute multipliers.

      if (j < m & LU.get(j, j) != 0.0) {
        for (int i = j + 1; i < m; i++) {
          LU.set(i, j, LU.get(i, j) / LU.get(j, j));
        }
      }
    }
    return new DefaultLU(LU.toMatrix(), pivSign, piv);
  }


  @Override
  public Matrix getL() {
    int              m = lu.getRowDimension();
    int              n = lu.getColumnDimension();
    MatrixBuilder<?> L = MatrixContext.getInstance().create(m, n);
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        if (i > j) {
          L.set(i, j, lu.get(i, j));
        }
        else if (i == j) {
          L.set(i, j, 1.0);
        }
      }
    }
    return L.toMatrix();
  }


  @Override
  public Matrix getU() {
    int              n = lu.getColumnDimension();
    MatrixBuilder<?> U = MatrixContext.getInstance().create(n, n);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (i <= j) {
          U.set(i, j, lu.get(i, j));
        }
      }
    }
    return U.toMatrix();
  }


  @Override
  public Optional<Matrix> solve(Matrix matrix) {
    int m = lu.getRowDimension();
    int n = lu.getColumnDimension();
    if (matrix.getRowDimension() != m) {
      throw new IllegalArgumentException("Matrix row dimensions must agree.");
    }
    if (!isNonSingular()) {
      return Optional.empty();
    }

    // Copy right hand side with pivoting
    int              nx = matrix.getColumnDimension();
    MatrixBuilder<?>
        X =
        MatrixContext.getInstance().create(matrix.getMatrix(piv, 0, nx - 1));

    // Solve L*Y = B(piv,:)
    for (int k = 0; k < n; k++) {
      for (int i = k + 1; i < n; i++) {
        for (int j = 0; j < nx; j++) {
          X.set(i, j, X.get(i, j) - X.get(k, j) * lu.get(i, k));
        }
      }
    }
    // Solve U*X = Y;
    for (int k = n - 1; k >= 0; k--) {
      for (int j = 0; j < nx; j++) {
        X.set(k, j, X.get(k, j) / lu.get(k, k));
      }
      for (int i = 0; i < k; i++) {
        for (int j = 0; j < nx; j++) {
          X.set(i, j, X.get(i, j) - X.get(k, j) * lu.get(i, k));
        }
      }
    }
    return Optional.of(X.toMatrix());
  }


  /**
   * Is the matrix non-singular?
   *
   * @return true if <i>U</i>, and hence <a>A</a>, is non-singular.
   */
  private boolean isNonSingular() {
    int n = lu.getColumnDimension();
    for (int j = 0; j < n; j++) {
      if (lu.get(j, j) == 0) {
        return false;
      }
    }
    return true;
  }


  @Override
  public int[] getPivot() {
    return piv.clone();
  }


  @Override
  public double det() {
    int m = lu.getRowDimension();
    int n = lu.getColumnDimension();
    if (m != n) {
      throw new IllegalStateException("Matrix must be square");
    }
    double d = (double) pivSign;
    for (int j = 0; j < n; j++) {
      d *= lu.get(j, j);
    }
    return d;
  }

}
