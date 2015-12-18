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

import thorwin.math.spi.VectorOperationsProvider;

import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Low-level matrix operation support. Implementation of these operations can be
 * replaced by a platform optimized (native) implementation.
 * @since 1.1.0
 */
public class VectorOperations {
  private static final VectorOperationsProvider PROVIDER;

  static {
    VectorOperationsProvider operations = new VectorOperationsProvider();
    try {
      for (VectorOperationsProvider service : ServiceLoader.load(VectorOperationsProvider.class)) {
        operations = service;
      }
    }
    catch (RuntimeException e) {
      String
          msg =
          "Could not load VectorOperationsProvider, using default";
      Logger.getLogger(VectorOperations.class.getName()).log(Level.SEVERE,
                                                             msg,
                                                             e);
    }
    PROVIDER = operations;
  }

  /**
   * Calculates <i>X<sub>out</sub> = &alpha; &times; X</i>.
   * @param alpha scaling factor
   * @param x vector X
   */
  public static void dscal(double alpha, double[] x) {
    PROVIDER.dscal(alpha, x);
  }


  /**
   * Calculates <i>Y<sub>out</sub> = A&times;X + Y</i>
   * @param a value A
   * @param x input vector X
   * @param y input/output vector Y
   */
  public static void daxpy(double a, double[] x, double[] y) {
    PROVIDER.daxpy(a,x,y);
  }
}
