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

import static java.lang.Math.max;
import static java.lang.Math.sqrt;

import java.util.Optional;

/**
 * Cholesky Decomposition.
 */
final class DefaultCholesky implements Cholesky {

  /**
   * Matrix L
   */
  private final Matrix L;

  /**
   * Matrix R
   */
  private final Matrix R;

  /**
   * @param L     Matrix L
   * @param R     Matrix R
   */
  private DefaultCholesky(Matrix L, Matrix R) {
    assert (L != null || R != null);
    this.L = L;
    this.R = R;
  }

  /**
   * Cholesky algorithm for symmetric and positive definite matrix.
   *
   * @param matrix  Square, symmetric matrix.
   * @return Cholesky decomposition
   */
  static Cholesky left(
    Matrix matrix) {

    if (!matrix.isSquare()) {
      throw new IllegalArgumentException("Matrix is not square");
    }

    MatrixBuilder<?> A = MatrixContext.getInstance().create(matrix);
    int n = matrix.getRowDimension();
    MatrixBuilder<?> L = MatrixContext.getInstance().create(n, n);

    // Main loop.
    for (int j = 0; j < n; j++) {
      double d = 0.0;
      for (int k = 0; k < j; k++) {
        double s = 0.0;
        for (int i = 0; i < k; i++) {
          s += L.get(k, i) * L.get(j, i);
        }
        s = (A.get(j, k) - s) / L.get(k, k);
        L.set(j, k, s);
        d = d + s * s;

        // Still symmetric?
        if (A.get(k, j) != A.get(j, k)) {
          throw new IllegalArgumentException("Matrix is not symmetric");
        }
      }
      d = A.get(j, j) - d;

      // Still positive definite?
      if (d <= 0.0) {
        throw new IllegalArgumentException("Matrix is not positive definite");
      }

      L.set(j, j, sqrt(max(d, 0.0)));
      for (int k = j + 1; k < n; k++) {
        L.set(j, k, 0.0);
      }
    }

    return new DefaultCholesky(L.toMatrix(), null);
  }


  /**
   * Cholesky algorithm for symmetric and positive definite matrix.
   *
   * @param matrix  Square, symmetric matrix.
   * @return Cholesky
   */
  static Cholesky right(Matrix matrix) {

    if (!matrix.isSquare()) {
      throw new IllegalArgumentException("Matrix is not square");
    }

    MatrixBuilder<?> A = MatrixContext.getInstance().create(matrix);
    int n = matrix.getColumnDimension();
    MatrixBuilder<?> R = MatrixContext.getInstance().create(n, n);

    // Main loop.
    for (int j = 0; j < n; j++) {
      double d = 0.0;
      for (int k = 0; k < j; k++) {
        double s = A.get(k, j);
        for (int i = 0; i < k; i++) {
          s = s - R.get(i, k) * R.get(i, j);
        }
        s = s / R.get(k,k);
        R.set(k, j, s);
        d = d + s * s;

        // Still symmetric?
        if (A.get(k, j) != A.get(j, k)) {
          throw new IllegalArgumentException("Matrix is not symmetric");
        }
      }
      d = A.get(j, j) - d;

      // Still positive definite?
      if (d <= 0.0) {
        throw new IllegalArgumentException("Matrix is not positive definite");
      }

      R.set(j, j, sqrt(max(d, 0.0)));
      for (int k = j + 1; k < n; k++) {
        R.set(k, j, 0.0);
      }
    }

    return new DefaultCholesky(null, R.toMatrix());
  }

  @Override
  public Matrix getL() {
    if (L != null) return L;
    else return R.transpose();
  }

  @Override
  public Matrix getR() {
    if (R != null) return R;
    else return L.transpose();
  }

  @Override
  public boolean hasL() {
    return L != null;
  }

  @Override
  public boolean hasR() {
    return R != null;
  }

  @Override
  public Optional<Matrix> solve(Matrix matrix) {
    if (hasL()) {
      return solveLeft(matrix);
    }
    else {
      return solveRight(matrix);
    }
  }

  private Optional<Matrix> solveLeft(Matrix matrix) {
    int n = L.getColumnDimension();
    if (matrix.getRowDimension() != n) {
      throw new IllegalArgumentException("Matrix row dimensions must agree.");
    }

    // Copy right hand side.
    MatrixBuilder<?> X = MatrixContext.getInstance().create(matrix);
    int nx = matrix.getColumnDimension();

    // Solve L*Y = B;
    for (int k = 0; k < n; k++) {
      for (int j = 0; j < nx; j++) {
        for (int i = 0; i < k; i++) {
          X.set(k, j, X.get(k, j) - X.get(i, j) * L.get(k, i));
        }
        X.set(k, j, X.get(k, j) / L.get(k, k));
      }
    }

    // Solve L'*X = Y;
    for (int k = n - 1; k >= 0; k--) {
      for (int j = 0; j < nx; j++) {
        for (int i = k + 1; i < n; i++) {
          X.set(k, j, X.get(k, j) - X.get(i, j) * L.get(i, k));
        }
        X.set(k, j, X.get(k, j) / L.get(k, k));
      }
    }

    return Optional.of( X.toMatrix() );
  }

  private Optional<Matrix> solveRight(Matrix matrix) {
    int n = R.getRowDimension();
    if (matrix.getRowDimension() != n) {
      throw new IllegalArgumentException("Matrix row dimensions must agree.");
    }

    // Copy right hand side.
    MatrixBuilder<?> X = MatrixContext.getInstance().create(matrix);
    int nx = matrix.getColumnDimension();

    // Solve R'*Y = B;
    for (int k = 0; k < n; k++) {
      for (int j = 0; j < nx; j++) {
        for (int i = 0; i < k; i++) {
          X.set(k, j, X.get(k, j) - X.get(i, j) * R.get(i,k));
        }
        X.set(k, j, X.get(k, j) / R.get(k, k));
      }
    }

    // Solve R*X = Y;
    for (int k = n - 1; k >= 0; k--) {
      for (int j = 0; j < nx; j++) {
        for (int i = k + 1; i < n; i++) {
          X.set(k, j, X.get(k, j) - X.get(i, j) * R.get(k,i));
        }
        X.set(k, j, X.get(k, j) / R.get(k, k));
      }
    }

    return Optional.of( X.toMatrix() );
  }
}
