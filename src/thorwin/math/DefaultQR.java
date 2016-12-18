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

import static java.lang.Math.hypot;

import java.util.Optional;


/**
 * QR Decomposition.
 */

final class DefaultQR implements thorwin.math.QR {

  /**
   * QR Matrix
   */
  private final Matrix QR;

  /**
   * Array for internal storage of diagonal of <i>R</i>.
   */
  private final double[] diagonalR;

  /**
   * Constructor.
   * @param QR QR Matrix
   * @param diagonalR Diagonal for matrix <i>R</i>
   */
  private DefaultQR(Matrix QR, double[] diagonalR) {
    super();
    this.QR = QR;
    this.diagonalR = diagonalR;
  }

  /**
   * Decompose QR using householder vectors.
   *
   * @param matrix  Matrix to decompose
   * @return The decomposition
   */
  static QR householder(Matrix matrix) {

    int m = matrix.getRowDimension();
    int n = matrix.getColumnDimension();

    double[] diagonalR = new double[n];

    MatrixBuilder<?> QR =MatrixContext.getInstance().create(matrix);

    // Main loop.
    for (int k = 0; k < n; k++) {
      // Compute 2-norm of k-th column without under/overflow.
      double nrm = 0;
      for (int i = k; i < m; i++) {
        nrm = hypot(nrm, QR.get(i, k));
      }

      if (nrm != 0.0) {
        // Form k-th Householder vector.
        if (QR.get(k,k) < 0) {
          nrm = -nrm;
        }
        for (int i = k; i < m; i++) {
          QR.set(i, k, QR.get(i, k) / nrm);
        }
        QR.set(k, k, QR.get(k, k) + 1.0);

        // Apply transformation to remaining getColumnDimension.
        for (int j = k + 1; j < n; j++) {
          double s = 0.0;
          for (int i = k; i < m; i++) {
            s += QR.get(i, k) * QR.get(i, j);
          }
          s = -s / QR.get(k, k);
          for (int i = k; i < m; i++) {
            QR.set(i, j, QR.get(i, j) + s * QR.get(i, k));
          }
        }
      }
      diagonalR[k] = -nrm;
    }
    return new DefaultQR(QR.toMatrix(), diagonalR);
  }

  @Override
  public Matrix getH() {
    int m = QR.getRowDimension();
    int n = QR.getColumnDimension();
    MatrixBuilder<?> h = MatrixContext.getInstance().create(m, n);
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        if (i >= j) {
          h.set(i, j, QR.get(i, j));
        }
      }
    }
    return h.toMatrix();
  }

  @Override
  public Matrix getR() {
    int n = QR.getColumnDimension();
    MatrixBuilder<?> r = MatrixContext.getInstance().create(n, n);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (i < j) {
          r.set(i, j, QR.get(i, j));
        } else if (i == j) {
          r.set(i, j, diagonalR[i]);
        }
      }
    }
    return r.toMatrix();
  }

  @Override
  public Matrix getQ() {
    int m = QR.getRowDimension();
    int n = QR.getColumnDimension();
    MatrixBuilder<?> q = MatrixContext.getInstance().create(m, n);

    for (int k = n - 1; k >= 0; k--) {
      for (int i = 0; i < m; i++) {
        q.set(i, k, 0);
      }
      q.set(k, k, 1);
      for (int j = k; j < n; j++) {
        if (QR.get(k, k) != 0) {
          double s = 0.0;
          for (int i = k; i < m; i++) {
            s += QR.get(i, k) * q.get(i, j);
          }
          s = -s / QR.get(k,k);
          for (int i = k; i < m; i++) {
            q.set(i, j, q.get(i, j) + s * QR.get(i, k));
          }
        }
      }
    }
    return q.toMatrix();
  }

  @Override
  public Optional<Matrix> solve(Matrix matrix) {
    int m = QR.getRowDimension();
    int n = QR.getColumnDimension();
    if (matrix.getRowDimension() != m) {
      throw new IllegalArgumentException("Matrix row dimensions must agree.");
    }
    if (!this.isFullRank()) {
      return Optional.empty();
    }

    // Copy right hand side
    int nx = matrix.getColumnDimension();
    MatrixBuilder<?> X = MatrixContext.getInstance().create(matrix);

    // Compute Y = transpose(Q)*B
    for (int k = 0; k < n; k++) {
      for (int j = 0; j < nx; j++) {
        double s = 0.0;
        for (int i = k; i < m; i++) {
          s += QR.get(i, k) * X.get(i, j);
        }
        s = -s / QR.get(k, k);
        for (int i = k; i < m; i++) {
          X.set(i, j, X.get(i, j) + s * QR.get(i, k));
        }
      }
    }
    // Solve R*X = Y;
    for (int k = n - 1; k >= 0; k--) {
      for (int j = 0; j < nx; j++) {
        X.set(k, j, X.get(k, j) / diagonalR[k]);
      }
      for (int i = 0; i < k; i++) {
        for (int j = 0; j < nx; j++) {
          X.set(i, j, X.get(i, j) - X.get(k, j) * QR.get(i, k));
        }
      }
    }
    return Optional.of(getMatrix(X.toMatrix(), 0, n - 1, 0, nx - 1));
  }

  /**
   * Returns a sub-matrix.
   *
   * @param rowStart    start row index
   * @param rowEnd      end row index
   * @param columnStart start column index
   * @param columnEnd   end column index
   * @return the specified sub-matrix
   */

  private static Matrix getMatrix(Matrix matrix,
                                  int rowStart,
                                  int rowEnd,
                                  int columnStart,
                                  int columnEnd) {
    int m = (rowEnd - rowStart) + 1;
    int n = (columnEnd - columnStart) + 1;

    MatrixBuilder<?> builder = MatrixContext.getInstance().create(m, n);

    for (int i = rowStart; i <= rowEnd; i++) {
      for (int j = columnStart; j <= columnEnd; j++) {
        builder.set(i - rowStart, j - columnStart, matrix.get(i, j));
      }
    }

    return builder.toMatrix();
  }

  @Override
  public boolean isFullRank() {
    int n = QR.getColumnDimension();
    for (int j = 0; j < n; j++) {
      if (diagonalR[j] == 0) return false;
    }
    return true;
  }
}