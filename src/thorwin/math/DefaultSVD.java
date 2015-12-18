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

import static java.lang.Math.*;


/**
 * Singular Value Decomposition.
 */
final class DefaultSVD implements SVD {

  /**
   * Array for internal storage of u.
   */
  private final Matrix u;

  /**
   * Array for internal storage of v.
   */
  private final Matrix v;

  /**
   * Array for internal storage of singular values.
   */
  private final Vector singularValues;

  /**
   * Constructs a new Singular Value Decomposition
   *
   * @param u              matrix <i>U</i>
   * @param v              matrix <i>V</i>
   * @param singularValues singular values matrix
   */
  private DefaultSVD(Matrix u,
                     Matrix v,
                     Vector singularValues) {
    this.u = u;
    this.v = v;
    this.singularValues = singularValues;
  }

  /**
   * Constructs the Singular Value Decomposition
   *
   * @param matrixA       rectangular matrix
   * @return The Singular Value Decomposition
   */
  @SuppressWarnings("ConstantConditions")
  public static SVD svd(Matrix matrixA) {

    MatrixBuilder data = MatrixContext.getInstance().create(matrixA);
    int m = matrixA.getRowDimension();
    int n = matrixA.getColumnDimension();

    int nu = min(m, n);
    double[] singularValues = new double[min(m + 1, n)];
    MatrixBuilder matrixUBuilder = MatrixContext.getInstance().create(m, nu);
    MatrixBuilder matrixVBuilder = MatrixContext.getInstance().create(n, n);
    double[] e = new double[n];
    double[] work = new double[m];
    boolean wantU = true;
    boolean wantV = true;

    int nct = min(m - 1, n);
    int nrt = max(0, min(n - 2, m));
    for (int k = 0; k < max(nct, nrt); k++) {
      if (k < nct) {

        singularValues[k] = 0;
        for (int i = k; i < m; i++) {
          singularValues[k] = hypot(singularValues[k], data.get(i, k));
        }
        if (singularValues[k] != 0.0) {
          if (data.get(k, k) < 0.0) {
            singularValues[k] = -singularValues[k];
          }
          for (int i = k; i < m; i++) {
            data.set(i, k, data.get(i, k) / singularValues[k]);
          }
          data.set(k, k, data.get(k, k) + 1.0);
        }
        singularValues[k] = -singularValues[k];
      }
      for (int j = k + 1; j < n; j++) {
        if ((k < nct) && (singularValues[k] != 0.0)) {

          double t = 0;
          for (int i = k; i < m; i++) {
            t += data.get(i, k) * data.get(i, j);
          }
          t = -t / data.get(k, k);
          for (int i = k; i < m; i++) {
            data.set(i, j, data.get(i, j) + (t * data.get(i, k)));
          }
        }

        e[j] = data.get(k, j);
      }
      if (k < nct) {

        for (int i = k; i < m; i++) {
          matrixUBuilder.set(i, k, data.get(i, k));
        }
      }
      if (k < nrt) {

        e[k] = 0;
        for (int i = k + 1; i < n; i++) {
          e[k] = hypot(e[k], e[i]);
        }
        if (e[k] != 0.0) {
          if (e[k + 1] < 0.0) {
            e[k] = -e[k];
          }
          for (int i = k + 1; i < n; i++) {
            e[i] /= e[k];
          }
          e[k + 1] += 1.0;
        }
        e[k] = -e[k];
        if (((k + 1) < m) && (e[k] != 0.0)) {

          for (int i = k + 1; i < m; i++) {
            work[i] = 0.0;
          }
          for (int j = k + 1; j < n; j++) {
            for (int i = k + 1; i < m; i++) {
              work[i] += e[j] * data.get(i, j);
            }
          }
          for (int j = k + 1; j < n; j++) {
            double t = -e[j] / e[k + 1];
            for (int i = k + 1; i < m; i++) {
              data.set(i, j, data.get(i, j) + (t * work[i]));
            }
          }
        }
        if (wantV) {

          for (int i = k + 1; i < n; i++) {
            matrixVBuilder.set(i, k, e[i]);
          }
        }
      }
    }

    int p = min(n, m + 1);
    if (nct < n) {
      singularValues[nct] = data.get(nct, nct);
    }
    if (m < p) {
      singularValues[p - 1] = 0.0;
    }
    if ((nrt + 1) < p) {
      e[nrt] = data.get(nrt, p - 1);
    }
    e[p - 1] = 0.0;

    if (wantU) {
      for (int j = nct; j < nu; j++) {
        for (int i = 0; i < m; i++) {
          matrixUBuilder.set(i, j, 0.0);
        }
        matrixUBuilder.set(j, j, 1.0);
      }
      for (int k = nct - 1; k >= 0; k--) {
        if (singularValues[k] == 0.0) {
          for (int i = 0; i < m; i++) {
            matrixUBuilder.set(i, k, 0.0);
          }
          matrixUBuilder.set(k, k, 1.0);
        } else {
          for (int j = k + 1; j < nu; j++) {
            double t = 0;
            for (int i = k; i < m; i++) {
              t += matrixUBuilder.get(i, k) * matrixUBuilder.get(i, j);
            }
            t = -t / matrixUBuilder.get(k, k);
            for (int i = k; i < m; i++) {
              matrixUBuilder.set(i,
                                 j,
                                 matrixUBuilder.get(i,
                                                    j) + (t * matrixUBuilder
                                                                  .get(i, k)));
            }
          }
          for (int i = k; i < m; i++) {
            matrixUBuilder.set(i, k, -matrixUBuilder.get(i, k));
          }
          matrixUBuilder.set(k, k, 1.0 + matrixUBuilder.get(k, k));
          for (int i = 0; i < (k - 1); i++) {
            matrixUBuilder.set(i, k, 0.0);
          }
        }
      }
    }

    if (wantV) {
      for (int k = n - 1; k >= 0; k--) {
        if ((k < nrt) && (e[k] != 0.0)) {
          for (int j = k + 1; j < nu; j++) {
            double t = 0;
            for (int i = k + 1; i < n; i++) {
              t += matrixVBuilder.get(i, k) * matrixVBuilder.get(i, j);
            }
            t = -t / matrixVBuilder.get(k + 1, k);
            for (int i = k + 1; i < n; i++) {
              matrixVBuilder.set(i,
                                 j,
                                 matrixVBuilder.get(i,
                                                    j) + (t * matrixVBuilder
                                                                  .get(i, k)));
            }
          }
        }
        for (int i = 0; i < n; i++) {
          matrixVBuilder.set(i, k, 0.0);
        }
        matrixVBuilder.set(k, k, 1.0);
      }
    }

    int pp = p - 1;
    double eps = pow(2.0, -52.0);
    double tiny = pow(2.0, -966.0);
    while (p > 0) {
      int k;
      int kase;

      for (k = p - 2; k >= -1; k--) {
        if (k == -1) {
          break;
        }
        if (abs(e[k]) <= (tiny + (eps * (abs(singularValues[k]) + abs(singularValues[k + 1]))))) {
          e[k] = 0.0;
          break;
        }
      }
      if (k == (p - 2)) {
        kase = 4;
      } else {
        int ks;
        for (ks = p - 1; ks >= k; ks--) {
          if (ks == k) {
            break;
          }
          double t = ((ks == p) ? 0.0 : abs(e[ks])) + ((ks == (k + 1)) ? 0.0
                                                                       : abs(e[ks - 1]));
          if (abs(singularValues[ks]) <= (tiny + (eps * t))) {
            singularValues[ks] = 0.0;
            break;
          }
        }
        if (ks == k) {
          kase = 3;
        } else if (ks == (p - 1)) {
          kase = 1;
        } else {
          kase = 2;
          k = ks;
        }
      }
      k++;

      switch (kase) {

        case 1: {
          double f = e[p - 2];
          e[p - 2] = 0.0;
          for (int j = p - 2; j >= k; j--) {
            double t = hypot(singularValues[j], f);
            double cs = singularValues[j] / t;
            double sn = f / t;
            singularValues[j] = t;
            if (j != k) {
              f = -sn * e[j - 1];
              e[j - 1] = cs * e[j - 1];
            }
            if (wantV) {
              for (int i = 0; i < n; i++) {
                t = cs * matrixVBuilder.get(i, j) + sn * matrixVBuilder.get(i,
                                                                            p
                                                                            -
                                                                            1);
                matrixVBuilder.set(i,
                                   p - 1,
                                   (-sn * matrixVBuilder.get(i,
                                                             j)) + (cs *
                                                                    matrixVBuilder.get(i,
                                                                                            p - 1)));
                matrixVBuilder.set(i, j, t);
              }
            }
          }
        }
        break;


        case 2: {
          double f = e[k - 1];
          e[k - 1] = 0.0;
          for (int j = k; j < p; j++) {
            double t = hypot(singularValues[j], f);
            double cs = singularValues[j] / t;
            double sn = f / t;
            singularValues[j] = t;
            f = -sn * e[j];
            e[j] = cs * e[j];
            if (wantU) {
              for (int i = 0; i < m; i++) {
                t = cs * matrixUBuilder.get(i, j) + sn * matrixUBuilder.get(i,
                                                                            k
                                                                            -
                                                                            1);
                matrixUBuilder.set(i,
                                   k - 1,
                                   (-sn * matrixUBuilder.get(i,
                                                             j)) + (cs *
                                                                    matrixUBuilder.get(i,
                                                                                            k - 1)));
                matrixUBuilder.set(i, j, t);
              }
            }
          }
        }
        break;

        case 3:

          double scale = max(max(max(max(abs(singularValues[p - 1]),
                                         abs(singularValues[p - 2])),
                                     abs(e[p - 2])), abs(singularValues[k])),
                             abs(e[k]));
          double sp = singularValues[p - 1] / scale;
          double spm1 = singularValues[p - 2] / scale;
          double epm1 = e[p - 2] / scale;
          double sk = singularValues[k] / scale;
          double ek = e[k] / scale;
          double b = (((spm1 + sp) * (spm1 - sp)) + (epm1 * epm1)) / 2.0;
          double c = sp * epm1 * sp * epm1;
          double shift = 0.0;
          if ((b != 0.0) || (c != 0.0)) {
            shift = sqrt((b * b) + c);
            if (b < 0.0) {
              shift = -shift;
            }
            shift = c / (b + shift);
          }
          double f = ((sk + sp) * (sk - sp)) + shift;
          double g = sk * ek;

          for (int j = k; j < (p - 1); j++) {
            double t = hypot(f, g);
            double cs = f / t;
            double sn = g / t;
            if (j != k) {
              e[j - 1] = t;
            }
            f = cs * singularValues[j] + sn * e[j];
            e[j] = cs * e[j] - sn * singularValues[j];
            g = sn * singularValues[j + 1];
            singularValues[j + 1] = cs * singularValues[j + 1];
            if (wantV) {
              for (int i = 0; i < n; i++) {
                t = cs * matrixVBuilder.get(i, j) + sn * matrixVBuilder.get(i,
                                                                            j
                                                                            +
                                                                            1);
                matrixVBuilder.set(i,
                                   j + 1,
                                   (-sn * matrixVBuilder.get(i,
                                                             j)) + (cs *
                                                                    matrixVBuilder.get(i,
                                                                                            j + 1)));
                matrixVBuilder.set(i, j, t);
              }
            }
            t = hypot(f, g);
            cs = f / t;
            sn = g / t;
            singularValues[j] = t;
            f = cs * e[j] + sn * singularValues[j + 1];
            singularValues[j + 1] = -sn * e[j] + cs * singularValues[j + 1];
            g = sn * e[j + 1];
            e[j + 1] = cs * e[j + 1];
            if (j < (m - 1)) {
              for (int i = 0; i < m; i++) {
                t = cs * matrixUBuilder.get(i, j) + sn * matrixUBuilder.get(i,
                                                                            j
                                                                            +
                                                                            1);
                matrixUBuilder.set(i,
                                   j + 1,
                                   (-sn * matrixUBuilder.get(i,
                                                             j)) + (cs *
                                                                    matrixUBuilder.get(i,
                                                                                            j + 1)));
                matrixUBuilder.set(i, j, t);
              }
            }
          }
          e[p - 2] = f;
          break;

        case 4:

          if (singularValues[k] <= 0.0) {
            singularValues[k] = singularValues[k] < 0.0 ? -singularValues[k]
                                                        : 0.0;
            if (wantV) {
              for (int i = 0; i <= pp; i++) {
                matrixVBuilder.set(i, k, -matrixVBuilder.get(i, k));
              }
            }
          }

          while (k < pp) {
            if (singularValues[k] >= singularValues[k + 1]) {
              break;
            }
            double t = singularValues[k];
            singularValues[k] = singularValues[k + 1];
            singularValues[k + 1] = t;
            if (k < (n - 1)) {
              for (int i = 0; i < n; i++) {
                t = matrixVBuilder.get(i, k + 1);
                matrixVBuilder.set(i, k + 1, matrixVBuilder.get(i, k));
                matrixVBuilder.set(i, k, t);
              }
            }
            if (k < (m - 1)) {
              for (int i = 0; i < m; i++) {
                t = matrixUBuilder.get(i, k + 1);
                matrixUBuilder.set(i, k + 1, matrixUBuilder.get(i, k));
                matrixUBuilder.set(i, k, t);
              }
            }
            k++;
          }
          p--;
          break;
      }
    }

    return new DefaultSVD(matrixUBuilder.toMatrix(),
                          matrixVBuilder.toMatrix(),
                          VectorContext.getInstance().create(singularValues)
                                       .toVector());
  }

  @Override
  public double condition() {
    int m = u.getRowDimension();
    int n = v.getColumnDimension();
    return singularValues.get(0) / singularValues.get(min(m, n) - 1);
  }

  @Override
  public Matrix getS() {
    int n = v.getColumnDimension();
    MatrixBuilder matrixS = MatrixContext.getInstance().create(n, n);
    for (int i = 0; i < n; i++) {
      matrixS.set(i, i, singularValues.get(i));
    }
    return matrixS.toMatrix();
  }

  @Override
  public Vector getSingularValues() {
    return singularValues;
  }

  @Override
  public Matrix getU() {
    return u;
  }

  @Override
  public Matrix getV() {
    return v;
  }

  @Override
  public int rank() {
    int m = u.getRowDimension();
    int n = v.getColumnDimension();
    double eps = pow(2.0, -52.0);
    double tol = max(m, n) * norm2() * eps;
    int r = 0;
    for (int i = 0; i < singularValues.length(); i++) {
      double element = singularValues.get(i);
      if (element > tol) {
        r++;
      }
    }
    return r;
  }

  @Override
  public double norm2() {
    return singularValues.get(0);
  }
}
