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

package thorwin.math.spi;

/**
 * Implementation of vector operations.
 */
public class VectorOperationsProvider {

  /**
   * Multiply a vector by a constant
   * @param alpha value
   * @param x vector
   */
  public void dscal(double alpha, double[] x) {
    for (int i = 0; i < x.length; i++) {
      x[i] *= alpha;
    }
  }


  /**
   * Calculates <i>Y<sub>out</sub> = A&times;X + Y</i>
   * @param a value A
   * @param x input vector X
   * @param y input/output vector Y
   */
  public void daxpy(double a, double[] x, double[] y) {
    if (x.length != y.length) {
      throw new IllegalArgumentException("length of x and y should be equal");
    }
    for (int i = 0; i < x.length; i++) {
      y[i] = a * x[i];
    }
  }
}
