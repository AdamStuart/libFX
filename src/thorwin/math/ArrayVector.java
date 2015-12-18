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

import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 * Vector that uses an internal array for storage.
 */
final class ArrayVector extends DefaultVector {

  private final double[] data;

  /**
   * Constructor. Note that this constructor exposes the internal data for
   * possible modification. Provided array should not be altered after
   * construction.
   *
   * @param data The vector's data
   */
  ArrayVector(double[] data) {
    super();
    this.data = data;
  }


  @Override
  public double[] toArray() {
    return this.data.clone();
  }


  @Override
  public int getDimension() {
    return this.data.length;
  }


  @Override
  public double get(int index) {
    return this.data[index];
  }


  @Override
  public DoubleStream stream() {
    return Arrays.stream(this.data);
  }
}
