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

/**
 * Builder for the array vector class.
 */
final class ArrayVectorBuilder extends VectorBuilder<ArrayVector> {

  private double[] data;

  public ArrayVectorBuilder(int size) {
    super();
    this.data = new double[size];
  }

  @Override
  public void set(int index, double value) {
    data[index] = value;
  }

  @Override
  public double get(int index) {
    return data[index];
  }

  @Override
  public ArrayVector toVector() {
    try {
      return new ArrayVector(data);
    } finally {
      this.data = null;
    }
  }
}
