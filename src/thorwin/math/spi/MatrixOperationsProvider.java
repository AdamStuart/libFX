/******************************************************************************
 * Copyright (C) 2015 Sebastiaan R. Hogenbirk                                 *
 * * This program is free software: you can redistribute it and/or modify
 * * it under the terms of the GNU Lesser General Public License as published
 * by* the Free Software Foundation, either version 3 of the License, or
 * * (at your option) any later version.
 * * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * * GNU Lesser General Public License for more details.
 * * * You should have received a copy of the GNU Lesser General Public License
 * * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * *
 ******************************************************************************/

package thorwin.math.spi;

import java.util.Arrays;


/**
 * Implementation of matrix operations.
 */
public class MatrixOperationsProvider {


  private static final int MAX_COLUMNS_FOR_MULTIPLY_SMALL = 36;

  /**
   * Calculates <i>C<sub>out</sub> = &alpha; &times; A &times; B + &beta;
   * &times; C</i>. The
   * resulting matrix is stored in matrix c. This is basically the fortran DGEMM
   * function, without the stride support.
   *
   * @param a     input matrix A (row-major packed)
   * @param b     input matrix B (row-major packed)
   * @param c     input/output matrix C (row-major packed).
   * @param m     row count of matrix A and C
   * @param n     column count of matrix b and C
   * @param k     column count of matrix A and row count of matrix B
   * @param ta    matrix A transposition state
   * @param tb    matrix B transposition state
   * @param alpha alpha multiplicand
   * @param beta  beta multiplicand
   */
  public void dgemm(double[] a,
                    double[] b,
                    double[] c,
                    int m,
                    int n,
                    int k,
                    boolean ta,
                    boolean tb,
                    double alpha,
                    double beta) {
    if (ta) {
      a = transpose(a, k, m);
    }

    if (tb) {
      b = transpose(b, n, k);
    }

    if (c == a) {
      a = a.clone();
    }
    if (c == b) {
      b = b.clone();
    }

    // Special case dgemm is used just for multiplying the matrices and storing
    // the result in c. This is the use case for matrix classes using this
    // method to implement their multiply method.
    if (alpha == 1 && beta == 0) {
      if (n < MAX_COLUMNS_FOR_MULTIPLY_SMALL) {
        multiplySmall(a, b, c, m, n, k);
      }
      else {
        multiplyLarge(a, b, c, m, n, k);
      }
    }

    // All other cases where alpha and beta are actually used
    else {
      scale(c, beta);

      if (alpha == 1) {
        multiplyAdd(a, b, c, m, n, k);
      }
      else {
        multiplyAdd(a, b, c, m, n, k, alpha);
      }
    }
  }


  /**
   * Creates a transposed version of the packed array
   * @param packed source array
   * @param rows rows
   * @param columns columns
   * @return new transposed packed array
   */
  private static double[] transpose(double[] packed, int rows, int columns) {
    double[] transposed = new double[packed.length];
    int      index1     = 0;
    for (int i = 0; i < columns; i++) {
      int index2 = i;
      int fence  = index1 + rows;
      while (index1 < fence) {
        transposed[index1] = packed[index2];
        index1++;
        index2 += columns;
      }
    }
    return transposed;
  }


  /**
   * Calculates <i>C<sub>out</sub> = &times; A &times; B</i>. The
   * resulting matrix is stored in matrix c. Optimized for smaller matrices.
   *
   * @param a     input matrix A (row-major packed)
   * @param b     input matrix B (row-major packed)
   * @param c     input/output matrix C (row-major packed).
   * @param m     row count of matrix A and C
   * @param n     column count of matrix b and C
   * @param k     column count of matrix A and row count of matrix B
   */
  private static void multiplySmall(double[] a,
                                    double[] b,
                                    double[] c,
                                    int m,
                                    int n,
                                    int k) {
    int base = 0;
    int ic   = 0;

    for (int i = 0; i < m; ++i) {
      for (int j = 0; j < n; ++j) {
        int    ia    = base;
        int    ib    = j;
        double total = 0;

        int end = base + k;
        while (ia < end) {
          total += a[ia] * b[ib];
          ia++;
          ib += n;
        }

        c[ic++] = total;
      }

      base += k;
    }
  }


  /**
   * Calculates <i>C<sub>out</sub> = &times; A &times; B </i>. The
   * resulting matrix is stored in matrix c. Optimized for larger matrices.
   *
   * @param a     input matrix A (row-major packed)
   * @param b     input matrix B (row-major packed)
   * @param c     input/output matrix C (row-major packed).
   * @param m     row count of matrix A and C
   * @param n     column count of matrix b and C
   * @param k     column count of matrix A and row count of matrix B
   */
  private void multiplyLarge(double[] a,
                             double[] b,
                             double[] c,
                             int m,
                             int n,
                             int k) {
    int base  = 0;
    int fence = k * n;

    for (int i = 0; i < m; i++) {
      int i1       = i * k;
      int i2       = 0;
      int i3       = base;
      int rowFence = n;

      double value = a[i1];
      i1++;

      while (i2 < rowFence) {
        c[i3] += value * b[i2++];
        i3++;
      }

      while (i2 != fence) {
        i3 = base;
        rowFence = i2 + n;
        value = a[i1];
        i1++;

        while (i2 < rowFence) {
          c[i3] += value * b[i2];
          i2++;
          i3++;
        }
      }
      base += n;
    }
  }


  /**
   * Calculates <i>C<sub>out</sub> =  &beta; &times; C</i>. The
   * resulting matrix is stored in matrix c.
   *
   * @param c     input/output matrix C (row-major packed).
   * @param beta  beta multiplicand
   */
  private static void scale(double[] c, double beta) {
    // if beta is 0, all values of c are zero
    if (beta == 0.0) {
      Arrays.fill(c, 0.0);
    }
    // if beta is not 0 or 1, all values of c need to be multiplied
    else if (beta != 1.0) {
      for (int i = 0; i < c.length; i++) {
        c[i] *= beta;
      }
    }
  }


  /**
   * Calculates <i>C<sub>out</sub> = &times; A &times; B + C</i>. The
   * resulting matrix is stored in matrix c.
   *
   * @param a     input matrix A (row-major packed)
   * @param b     input matrix B (row-major packed)
   * @param c     input/output matrix C (row-major packed).
   * @param m     row count of matrix A and C
   * @param n     column count of matrix b and C
   * @param k     column count of matrix A and row count of matrix B
   */
  private static void multiplyAdd(double[] a,
                                  double[] b,
                                  double[] c,
                                  int m,
                                  int n,
                                  int k) {
    int base  = 0;
    int fence = k * n;

    for (int i = 0; i < m; i++) {
      int i1       = i * k;
      int i2       = 0;
      int i3       = base;
      int rowFence = n;

      double value = a[i1];
      i1++;

      while (i2 < rowFence) {
        c[i3] += value * b[i2++];
        i3++;
      }

      while (i2 != fence) {
        i3 = base;
        rowFence = i2 + n;
        value = a[i1];
        i1++;

        while (i2 < rowFence) {
          c[i3] += value * b[i2];
          i2++;
          i3++;
        }
      }
      base += n;
    }
  }


  /**
   * Calculates <i>C<sub>out</sub> = &alpha; &times; A &times; B + C</i>. The
   * resulting matrix is stored in matrix c.
   *
   * @param a     input matrix A (row-major packed)
   * @param b     input matrix B (row-major packed)
   * @param c     input/output matrix C (row-major packed).
   * @param m     row count of matrix A and C
   * @param n     column count of matrix b and C
   * @param k     column count of matrix A and row count of matrix B
   * @param alpha alpha multiplicand
   */
  private static void multiplyAdd(double[] a,
                                  double[] b,
                                  double[] c,
                                  int m,
                                  int n,
                                  int k,
                                  double alpha) {
    int fence = k * n;
    int base  = 0;
    for (int i = 0; i < m; i++) {
      int i1       = i * k;
      int i2       = 0;
      int i3       = base;
      int rowFence = n;

      double value = a[i1++] * alpha;

      while (i2 < rowFence) {
        c[i3++] += value * b[i2++];
      }

      while (i2 != fence) {
        i3 = base;
        rowFence = i2 + n;
        value = a[i1++] * alpha;

        while (i2 < rowFence) {
          c[i3++] += value * b[i2++];
        }
      }
      base += n;
    }
  }

  /**
   * Returns true if the provider is accelerated.
   * @return true if accelerated
   */
  public boolean isAccelerated() {
    return false;
  }

}
