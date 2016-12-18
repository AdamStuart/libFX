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
import static java.lang.Math.hypot;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Eigenvalues and eigenvectors of a real matrix.
 */
final class DefaultEigen implements Eigen {

  /**
   * Array for internal storage of eigenvectors.
   */
  private final Matrix V;

  /**
   * The complex eigenvalues
   */
  private final ComplexVector eigenvalues;

  /**
   * Constructor
   * @param V matrix V
   * @param eigenvalues complex eigenvalues
   */
  private DefaultEigen(Matrix V,
                       ComplexVector eigenvalues) {
    super();
    this.V = V;
    this.eigenvalues = eigenvalues;
  }

  /**
   * Performs the Eigendecomposition on a matrix
   * @param matrix matrix to decompose
   * @return Eigenvalue decomposition
   */
  static Eigen decompose(Matrix matrix) {
    if (matrix.getColumnDimension() != matrix.getRowDimension()) {
      throw new IllegalArgumentException("Matrix should be square");
    }

    int n = matrix.getColumnDimension();
    MatrixBuilder<?> V = MatrixContext.getInstance().create(n, n);
    MatrixBuilder<?> H = MatrixContext.getInstance().create(n, n);

    double[] reals = new double[n];
    double[] imags = new double[n];

    boolean isSymmetric = true;
    for (int j = 0; (j < n) & isSymmetric; j++) {
      for (int i = 0; (i < n) & isSymmetric; i++) {
        isSymmetric = (matrix.get(i, j) == matrix.get(j, i));
      }
    }

    if (isSymmetric) {
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          V.set(i, j, matrix.get(i, j));
        }
      }

      // Tridiagonalize.
      tred2(n, reals, imags, V);

      // Diagonalize.
      tql2(n, reals, imags, V);

    } else {

      for (int j = 0; j < n; j++) {
        for (int i = 0; i < n; i++) {
          H.set(i, j, matrix.get(i, j));
        }
      }

      // Reduce to Hessenberg form.
      orthes(n, V, H);

      // Reduce Hessenberg to real Schur form.
      hqr2(n, reals, imags, V, H);
    }

    return new DefaultEigen(V.toMatrix(),
                     new DefaultComplexVector(reals, imags));
  }

  // Non-symmetric reduction to Hessenberg form.

  private static void tred2(int n, double[] reals, double[] imags, MatrixBuilder<?> V) {

    //  This is derived from the Algol procedures tred2 by
    //  Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
    //  Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
    //  Fortran subroutine in EISPACK.

    for (int j = 0; j < n; j++) {
      reals[j] = V.get(n - 1,j);
    }

    // Householder reduction to tridiagonal form.

    for (int i = n - 1; i > 0; i--) {

      // Scale to avoid under/overflow.

      double scale = 0.0;
      double h = 0.0;
      for (int k = 0; k < i; k++) {
        scale = scale + abs(reals[k]);
      }
      if (scale == 0.0) {
        imags[i] = reals[i - 1];
        for (int j = 0; j < i; j++) {
          reals[j] = V.get(i - 1,j);
          V.set(i, j, 0.0);
          V.set(j, i, 0.0);
        }
      } else {

        // Generate Householder vector.

        for (int k = 0; k < i; k++) {
          reals[k] /= scale;
          h += reals[k] * reals[k];
        }
        double f = reals[i - 1];
        double g = sqrt(h);
        if (f > 0) {
          g = -g;
        }
        imags[i] = scale * g;
        h = h - f * g;
        reals[i - 1] = f - g;
        for (int j = 0; j < i; j++) {
          imags[j] = 0.0;
        }

        // Apply similarity transformation to remaining getColumnDimension.

        for (int j = 0; j < i; j++) {
          f = reals[j];
          V.set(j, i, f);
          g = imags[j] + V.get(j,j) * f;
          for (int k = j + 1; k <= i - 1; k++) {
            g += V.get(k,j) * reals[k];
            imags[k] += V.get(k,j) * f;
          }
          imags[j] = g;
        }
        f = 0.0;
        for (int j = 0; j < i; j++) {
          imags[j] /= h;
          f += imags[j] * reals[j];
        }
        double hh = f / (h + h);
        for (int j = 0; j < i; j++) {
          imags[j] -= hh * reals[j];
        }
        for (int j = 0; j < i; j++) {
          f = reals[j];
          g = imags[j];
          for (int k = j; k <= i - 1; k++) {
            V.set(k, j, V.get(k, j) - (f * imags[k] + g * reals[k]));
          }
          reals[j] = V.get(i - 1, j);
          V.set(i, j, 0.0);
        }
      }
      reals[i] = h;
    }

    // Accumulate transformations.

    for (int i = 0; i < n - 1; i++) {
      V.set(n - 1, i, V.get(i, i));
      V.set(i, i, 1.0);
      double h = reals[i + 1];
      if (h != 0.0) {
        for (int k = 0; k <= i; k++) {
          reals[k] = V.get(k,i + 1) / h;
        }
        for (int j = 0; j <= i; j++) {
          double g = 0.0;
          for (int k = 0; k <= i; k++) {
            g += V.get(k,i + 1) * V.get(k,j);
          }
          for (int k = 0; k <= i; k++) {
            V.set(k, j, V.get(k, j) - g * reals[k]);
          }
        }
      }
      for (int k = 0; k <= i; k++) {
        V.set(k, i + 1, 0.0);
      }
    }
    for (int j = 0; j < n; j++) {
      reals[j] = V.get(n - 1,j);
      V.set(n - 1, j, 0.0);
    }
    V.set(n - 1, n - 1, 1.0);
    imags[0] = 0.0;
  }

  private static void tql2(int n, double[] reals, double[] imags, MatrixBuilder<?> V) {

    //  This is derived from the Algol procedures tql2, by
    //  Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
    //  Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
    //  Fortran subroutine in EISPACK.

    System.arraycopy(imags, 1, imags, 0, n - 1);
    imags[n - 1] = 0.0;

    double f = 0.0;
    double tst1 = 0.0;
    double eps = pow(2.0, -52.0);
    for (int l = 0; l < n; l++) {

      // Find small sub-diagonal element

      tst1 = max(tst1, abs(reals[l]) + abs(imags[l]));
      int m = l;
      while (m < n) {
        if (abs(imags[m]) <= eps * tst1) {
          break;
        }
        m++;
      }

      // If m == l, reals[l] is an eigenvalue,
      // otherwise, iterate.

      if (m > l) {
        int iteration = 0;
        do {
          iteration = iteration + 1;  // (Could check iteration count here.)

          // Compute implicit shift

          double g = reals[l];
          double p = (reals[l + 1] - g) / (2.0 * imags[l]);
          double r = hypot(p, 1.0);
          if (p < 0) {
            r = -r;
          }
          reals[l] = imags[l] / (p + r);
          reals[l + 1] = imags[l] * (p + r);
          double dl1 = reals[l + 1];
          double h = g - reals[l];
          for (int i = l + 2; i < n; i++) {
            reals[i] -= h;
          }
          f = f + h;

          // Implicit QL transformation.

          p = reals[m];
          double c = 1.0;
          double c2 = c;
          double c3 = c;
          double el1 = imags[l + 1];
          double s = 0.0;
          double s2 = 0.0;
          for (int i = m - 1; i >= l; i--) {
            c3 = c2;
            c2 = c;
            s2 = s;
            g = c * imags[i];
            h = c * p;
            r = hypot(p, imags[i]);
            imags[i + 1] = s * r;
            s = imags[i] / r;
            c = p / r;
            p = c * reals[i] - s * g;
            reals[i + 1] = h + s * (c * g + s * reals[i]);

            // Accumulate transformation.

            for (int k = 0; k < n; k++) {
              h = V.get(k,i + 1);
              V.set(k, i + 1, s * V.get(k, i) + c * h);
              V.set(k, i, c * V.get(k, i) - s * h);
            }
          }
          p = -s * s2 * c3 * el1 * imags[l] / dl1;
          imags[l] = s * p;
          reals[l] = c * p;

          // Check for convergence.

        } while (abs(imags[l]) > eps * tst1);
      }
      reals[l] = reals[l] + f;
      imags[l] = 0.0;
    }

    // Sort eigenvalues and corresponding vectors.

    for (int i = 0; i < n - 1; i++) {
      int k = i;
      double p = reals[i];
      for (int j = i + 1; j < n; j++) {
        if (reals[j] < p) {
          k = j;
          p = reals[j];
        }
      }
      if (k != i) {
        reals[k] = reals[i];
        reals[i] = p;
        for (int j = 0; j < n; j++) {
          p = V.get(j, i);
          V.set(j, i, V.get(j, k));
          V.set(j, k, p);
        }
      }
    }
  }

  private static void orthes(int n,  MatrixBuilder<?> V, MatrixBuilder<?> H) {

    double[] ort = new double[n];

    //  This is derived from the Algol procedures orthes and ortran,
    //  by Martin and Wilkinson, Handbook for Auto. Comp.,
    //  Vol.ii-Linear Algebra, and the corresponding
    //  Fortran subroutines in EISPACK.

    int low = 0;
    int high = n - 1;

    for (int m = low + 1; m <= high - 1; m++) {

      // Scale column.

      double scale = 0.0;
      for (int i = m; i <= high; i++) {
        scale = scale + abs(H.get(i, m - 1));
      }
      if (scale != 0.0) {

        // Compute Householder transformation.

        double h = 0.0;
        for (int i = high; i >= m; i--) {
          ort[i] = H.get(i,m - 1) / scale;
          h += ort[i] * ort[i];
        }
        double g = sqrt(h);
        if (ort[m] > 0) {
          g = -g;
        }
        h = h - ort[m] * g;
        ort[m] = ort[m] - g;

        // Apply Householder similarity transformation
        // H = (I-u*u'/h)*H*(I-u*u')/h)

        for (int j = m; j < n; j++) {
          double f = 0.0;
          for (int i = high; i >= m; i--) {
            f += ort[i] * H.get(i,j);
          }
          f = f / h;
          for (int i = m; i <= high; i++) {
            H.set(i, j, H.get(i, j) - f * ort[i]);
          }
        }

        for (int i = 0; i <= high; i++) {
          double f = 0.0;
          for (int j = high; j >= m; j--) {
            f += ort[j] * H.get(i,j);
          }
          f = f / h;
          for (int j = m; j <= high; j++) {
            H.set(i, j, H.get(i, j) - f * ort[j]);
          }
        }
        ort[m] = scale * ort[m];
        H.set(m, m - 1, scale * g);
      }
    }

    // Accumulate transformations (Algol's ortran).

    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        V.set(i, j, (i == j ? 1.0 : 0.0));
      }
    }

    for (int m = high - 1; m >= low + 1; m--) {
      if (H.get(m,m - 1) != 0.0) {
        for (int i = m + 1; i <= high; i++) {
          ort[i] = H.get(i,m - 1);
        }
        for (int j = m; j <= high; j++) {
          double g = 0.0;
          for (int i = m; i <= high; i++) {
            g += ort[i] * V.get(i,j);
          }
          // Double division avoids possible underflow
          g = (g / ort[m]) / H.get(m,m - 1);
          for (int i = m; i <= high; i++) {
            V.set(i, j, V.get(i, j) + g * ort[i]);
          }
        }
      }
    }
  }


  @SuppressWarnings("ConstantConditions")
  private static void hqr2(int n, double[] reals, double[] imags, MatrixBuilder<?> V, MatrixBuilder<?> H) {

    //  This is derived from the Algol procedure hqr2,
    //  by Martin and Wilkinson, Handbook for Auto. Comp.,
    //  Vol.ii-Linear Algebra, and the corresponding
    //  Fortran subroutine in EISPACK.

    // Initialize

    int n1 = n - 1;
    int low = 0;
    int high = n - 1;
    double eps = pow(2.0, -52.0);
    double exshift = 0.0;
    double p = 0, q = 0, r = 0, s = 0, z = 0, t, w, x, y;

    // Store roots isolated by balanc and compute matrix norm

    double norm = 0.0;
    for (int i = 0; i < n; i++) {
      if (i < low | i > high) {
        reals[i] = H.get(i,i);
        imags[i] = 0.0;
      }
      for (int j = max(i - 1, 0); j < n; j++) {
        norm = norm + abs(H.get(i, j));
      }
    }

    // Outer loop over eigenvalue index

    int iter = 0;
    while (n1 >= low) {

      // Look for single small sub-diagonal element

      int l = n1;
      while (l > low) {
        s = abs(H.get(l - 1, l - 1)) + abs(H.get(l, l));
        if (s == 0.0) {
          s = norm;
        }
        if (abs(H.get(l, l - 1)) < eps * s) {
          break;
        }
        l--;
      }

      // Check for convergence
      // One root found

      if (l == n1) {
        H.set(n1, n1, H.get(n1, n1) + exshift);
        reals[n1] = H.get(n1, n1);
        imags[n1] = 0.0;
        n1--;
        iter = 0;

        // Two roots found

      } else if (l == n1 - 1) {
        w = H.get(n1,n1 - 1) * H.get(n1 - 1,n1);
        p = (H.get(n1 - 1,n1 - 1) - H.get(n1,n1)) / 2.0;
        q = p * p + w;
        z = sqrt(abs(q));
        H.set(n1, n1, H.get(n1, n1) + exshift);
        H.set(n1 - 1, n1 - 1, H.get(n1 - 1, n1 - 1) + exshift);
        x = H.get(n1,n1);

        // Real pair

        if (q >= 0) {
          if (p >= 0) {
            z = p + z;
          } else {
            z = p - z;
          }
          reals[n1 - 1] = x + z;
          reals[n1] = reals[n1 - 1];
          if (z != 0.0) {
            reals[n1] = x - w / z;
          }
          imags[n1 - 1] = 0.0;
          imags[n1] = 0.0;
          x = H.get(n1,n1 - 1);
          s = abs(x) + abs(z);
          p = x / s;
          q = z / s;
          r = sqrt(p * p + q * q);
          p = p / r;
          q = q / r;

          // Row modification

          for (int j = n1 - 1; j < n; j++) {
            z = H.get(n1 - 1,j);
            H.set(n1 - 1, j, q * z + p * H.get(n1, j));
            H.set(n1, j, q * H.get(n1, j) - p * z);
          }

          // Column modification

          for (int i = 0; i <= n1; i++) {
            z = H.get(i,n1 - 1);
            H.set(i, n1 - 1, q * z + p * H.get(i, n1));
            H.set(i, n1, q * H.get(i, n1) - p * z);
          }

          // Accumulate transformations

          for (int i = low; i <= high; i++) {
            z = V.get(i,n1 - 1);
            V.set(i, n1 - 1, q * z + p * V.get(i, n1));
            V.set(i, n1, q * V.get(i, n1) - p * z);
          }

          // Complex pair

        } else {
          reals[n1 - 1] = x + p;
          reals[n1] = x + p;
          imags[n1 - 1] = z;
          imags[n1] = -z;
        }
        n1 = n1 - 2;
        iter = 0;

        // No convergence yet

      } else {

        // Form shift

        x = H.get(n1, n1);
        y = 0.0;
        w = 0.0;
        if (l < n1) {
          y = H.get(n1 - 1, n1 - 1);
          w = H.get(n1, n1 - 1) * H.get(n1 - 1, n1);
        }

        // Wilkinson's original ad hoc shift

        if (iter == 10) {
          exshift += x;
          for (int i = low; i <= n1; i++) {
            H.set(i, i, H.get(i, i) - x);
          }
          s = abs(H.get(n1, n1 - 1)) + abs(H.get(n1 - 1, n1 - 2));
          x = y = 0.75 * s;
          w = -0.4375 * s * s;
        }

        // MATLAB new ad hoc shift

        if (iter == 30) {
          s = (y - x) / 2.0;
          s = s * s + w;
          if (s > 0) {
            s = sqrt(s);
            if (y < x) {
              s = -s;
            }
            s = x - w / ((y - x) / 2.0 + s);
            for (int i = low; i <= n1; i++) {
              H.set(i, i, H.get(i, i) - s);
            }
            exshift += s;
            x = y = w = 0.964;
          }
        }

        iter = iter + 1;   // (Could check iteration count here.)

        // Look for two consecutive small sub-diagonal elements

        int m = n1 - 2;
        while (m >= l) {
          z = H.get(m,m);
          r = x - z;
          s = y - z;
          p = (r * s - w) / H.get(m + 1,m) + H.get(m,m + 1);
          q = H.get(m + 1,m + 1) - z - r - s;
          r = H.get(m + 2,m + 1);
          s = abs(p) + abs(q) + abs(r);
          p = p / s;
          q = q / s;
          r = r / s;
          if (m == l) {
            break;
          }
          if (abs(H.get(m,
                        m - 1)) * (abs(q) + abs(r)) < eps * (abs(p) * (abs(H.get(m - 1,
                                                                                 m - 1)) + abs(z) +
                                                                       abs(H.get(m + 1,
                                                                                 m + 1))))) {
            break;
          }
          m--;
        }

        for (int i = m + 2; i <= n1; i++) {
          H.set(i, i - 2, 0.0);
          if (i > m + 2) {
            H.set(i, i - 3, 0.0);
          }
        }

        // Double QR step involving getRowDimension l:n and getColumnDimension m:n


        for (int k = m; k <= n1 - 1; k++) {
          boolean notLast = (k != n1 - 1);
          if (k != m) {
            p = H.get(k, k - 1);
            q = H.get(k + 1, k - 1);
            r = (notLast ? H.get(k + 2, k - 1) : 0.0);
            x = abs(p) + abs(q) + abs(r);
            if (x == 0.0) {
              continue;
            }
            p = p / x;
            q = q / x;
            r = r / x;
          }

          s = sqrt(p * p + q * q + r * r);
          if (p < 0) {
            s = -s;
          }
          if (s != 0) {
            if (k != m) {
              H.set(k, k - 1, -s * x);
            } else if (l != m) {
              H.set(k, k - 1, -H.get(k, k - 1));
            }
            p = p + s;
            x = p / s;
            y = q / s;
            z = r / s;
            q = q / p;
            r = r / p;

            // Row modification

            for (int j = k; j < n; j++) {
              p = H.get(k, j) + q * H.get(k + 1, j);
              if (notLast) {
                p = p + r * H.get(k + 2, j);
                H.set(k + 2, j, H.get(k + 2, j) - p * z);
              }
              H.set(k, j, H.get(k, j) - p * x);
              H.set(k + 1, j, H.get(k + 1, j) - p * y);
            }

            // Column modification

            for (int i = 0; i <= min(n1, k + 3); i++) {
              p = x * H.get(i, k) + y * H.get(i, k + 1);
              if (notLast) {
                p = p + z * H.get(i,k + 2);
                H.set(i, k + 2, H.get(i, k + 2) - p * r);
              }
              H.set(i, k, H.get(i, k) - p);
              H.set(i, k + 1, H.get(i, k + 1) - p * q);
            }

            // Accumulate transformations

            for (int i = low; i <= high; i++) {
              p = x * V.get(i,k) + y * V.get(i, k + 1);
              if (notLast) {
                p = p + z * V.get(i,k + 2);
                V.set(i, k + 2, V.get(i, k + 2) - p * r);
              }
              V.set(i, k, V.get(i, k) - p);
              V.set(i, k + 1, V.get(i, k + 1) - p * q);
            }
          }  // (s != 0)
        }  // k loop
      }  // check convergence
    }  // while (n >= low)

    // Back-substitute to find vectors of upper triangular form

    if (norm == 0.0) {
      return;
    }

    for (n1 = n - 1; n1 >= 0; n1--) {
      p = reals[n1];
      q = imags[n1];

      // Real vector

      if (q == 0) {
        int l = n1;
        H.set(n1, n1, 1.0);
        for (int i = n1 - 1; i >= 0; i--) {
          w = H.get(i, i) - p;
          r = 0.0;
          for (int j = l; j <= n1; j++) {
            r = r + H.get(i, j) * H.get(j, n1);
          }
          if (imags[i] < 0.0) {
            z = w;
            s = r;
          } else {
            l = i;
            if (imags[i] == 0.0) {
              if (w != 0.0) {
                H.set(i, n1, -r / w);
              } else {
                H.set(i, n1, -r / (eps * norm));
              }

              // Solve real equations

            } else {
              x = H.get(i,i + 1);
              y = H.get(i + 1,i);
              q = (reals[i] - p) * (reals[i] - p) + imags[i] * imags[i];
              t = (x * s - z * r) / q;
              H.set(i, n1, t);
              if (abs(x) > abs(z)) {
                H.set(i + 1, n1, (-r - w * t) / x);
              } else {
                H.set(i + 1, n1, (-s - y * t) / z);
              }
            }

            // Overflow control

            t = abs(H.get(i, n1));
            if ((eps * t) * t > 1) {
              for (int j = i; j <= n1; j++) {
                H.set(j, n1, H.get(j, n1) / t);
              }
            }
          }
        }

        // Complex vector

      } else if (q < 0) {
        int l = n1 - 1;

        // Last vector component imaginary so matrix is triangular

        if (abs(H.get(n1, n1 - 1)) > abs(H.get(n1 - 1, n1))) {
          H.set(n1 - 1, n1 - 1, q / H.get(n1, n1 - 1));
          H.set(n1 - 1, n1, -(H.get(n1, n1) - p) / H.get(n1, n1 - 1));
        } else {
          Complex cdiv = cdiv(0.0, -H.get(n1 - 1, n1), H.get(n1 - 1, n1 - 1) - p, q);
          H.set(n1 - 1, n1 - 1, cdiv.real());
          H.set(n1 - 1, n1, cdiv.imag());
        }
        H.set(n1, n1 - 1, 0.0);
        H.set(n1, n1, 1.0);
        for (int i = n1 - 2; i >= 0; i--) {
          double ra, sa, vr, vi;
          ra = 0.0;
          sa = 0.0;
          for (int j = l; j <= n1; j++) {
            ra = ra + H.get(i, j) * H.get(j, n1 - 1);
            sa = sa + H.get(i, j) * H.get(j, n1);
          }
          w = H.get(i, i) - p;

          if (imags[i] < 0.0) {
            z = w;
            r = ra;
            s = sa;
          } else {
            l = i;
            if (imags[i] == 0) {
              Complex cdiv = cdiv(-ra, -sa, w, q);
              H.set(i, n1 - 1, cdiv.real());
              H.set(i, n1, cdiv.imag());
            } else {

              // Solve complex equations

              x = H.get(i, i + 1);
              y = H.get(i + 1, i);
              vr = (reals[i] - p) * (reals[i] - p) + imags[i] * imags[i] - q * q;
              vi = (reals[i] - p) * 2.0 * q;
              if (vr == 0.0 & vi == 0.0) {
                vr = eps * norm * (abs(w) + abs(q) +
                                   abs(x) + abs(y) + abs(z));
              }
              Complex cdiv = cdiv(x * r - z * ra + q * sa, x * s - z * sa - q * ra, vr, vi);
              H.set(i, n1 - 1, cdiv.real());
              H.set(i, n1, cdiv.imag());
              if (abs(x) > (abs(z) + abs(q))) {
                H.set(i + 1,
                      n1 - 1,
                      (-ra - w * H.get(i, n1 - 1) + q * H.get(i, n1)) / x);
                H.set(i + 1,
                      n1,
                      (-sa - w * H.get(i, n1) - q * H.get(i, n1 - 1)) / x);
              } else {
                cdiv = cdiv(-r - y * H.get(i, n1 - 1), -s - y * H.get(i, n1), z, q);
                H.set(i + 1, n1 - 1, cdiv.real());
                H.set(i + 1, n1, cdiv.imag());
              }
            }

            // Overflow control

            t = max(abs(H.get(i, n1 - 1)), abs(H.get(i, n1)));
            if ((eps * t) * t > 1) {
              for (int j = i; j <= n1; j++) {
                H.set(j, n1 - 1, H.get(j, n1 - 1) / t);
                H.set(j, n1, H.get(j, n1) / t);
              }
            }
          }
        }
      }
    }

    // Vectors of isolated roots

    for (int i = 0; i < n; i++) {
      if (i < low | i > high) {
        for (int j = i; j < n; j++) {
          V.set(i, j, H.get(i, j));
        }
      }
    }

    // Back transformation to get eigenvectors of original matrix

    for (int j = n - 1; j >= low; j--) {
      for (int i = low; i <= high; i++) {
        z = 0.0;
        for (int k = low; k <= min(j, high); k++) {
          z = z + V.get(i, k) * H.get(k, j);
        }
        V.set(i, j, z);
      }
    }
  }


  private static Complex cdiv(double xr, double xi, double yr, double yi) {
    double r, d;
    if (abs(yr) > abs(yi)) {
      r = yi / yr;
      d = yr + r * yi;
      double cdivr = (xr + r * xi) / d;
      double cdivi = (xi - r * xr) / d;
      return new Complex(cdivr, cdivi);
    } else {
      r = yr / yi;
      d = yi + r * yr;
      double cdivr = (r * xr + xi) / d;
      double cdivi = (r * xi - xr) / d;
      return new Complex(cdivr, cdivi);
    }
  }

  @Override
  public Matrix getV() {
    return V;
  }

  @Override
  public ComplexVector getEigenvalues() {
    return eigenvalues;
  }


  @Override
  public Matrix getD() {
    int n = eigenvalues.getDimension();
    MatrixBuilder<?> D = MatrixContext.getInstance().create(n, n);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        D.set(i, j, 0.0);
      }
      Complex eigenvalue = eigenvalues.get(i);
      D.set(i, i, eigenvalue.real());
      if (eigenvalue.imag() > 0) {
        D.set(i, i + 1, eigenvalue.imag());
      } else if (eigenvalue.imag() < 0) {
        D.set(i, i - 1, eigenvalue.imag());
      }
    }
    return D.toMatrix();
  }
}