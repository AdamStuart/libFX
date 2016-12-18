/******************************************************************************
 * Copyright (C) 2015 Sebastiaan R. Hogenbirk                                 *
 * * This program is free software: you can redistribute it and/or modify * it
 * under the terms of the GNU Lesser General Public License as published by* the
 * Free Software Foundation, either version 3 of the License, or * (at your
 * option) any later version. * * This program is distributed in the hope that
 * it will be useful, * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *
 * GNU Lesser General Public License for more details. * * You should have
 * received a copy of the GNU Lesser General Public License * along with this
 * program.  If not, see <http://www.gnu.org/licenses/>. *
 ******************************************************************************/

package thorwin.math;

import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import thorwin.math.spi.MatrixOperationsProvider;


/**
 * Low-level matrix operation support. Implementation of these operations can be
 * replaced by a platform optimized (native) implementation.
 * @since 1.1.0
 */
public class MatrixOperations {
  private static final MatrixOperationsProvider PROVIDER;

  static {
    MatrixOperationsProvider provider = new MatrixOperationsProvider();
    try {
      for (MatrixOperationsProvider service : ServiceLoader.load(MatrixOperationsProvider.class)) {
        provider = service;
      }
    }
    catch (RuntimeException e) {
      String
          msg =
          "Could not load MatrixOperationsProvider, using default";
      Logger.getLogger(MatrixOperations.class.getName()).log(Level.SEVERE,
                                                             msg,
                                                             e);
    }
    PROVIDER = provider;
  }

  /**
   * Calculates <i>C<sub>out</sub> = &alpha; &times; A &times; B + &beta; &times; C</i>. The
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
  public static void dgemm(double[] a,
                           double[] b,
                           double[] c,
                           int m,
                           int n,
                           int k,
                           boolean ta,
                           boolean tb,
                           double alpha,
                           double beta) {
    PROVIDER.dgemm(a, b, c, m, n, k, ta, tb, alpha, beta);
  }


}
