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

import static java.lang.Math.multiplyExact;


/**
 * Builder for columnPacked matrices.
 */
final class PackedMatrixBuilder extends MatrixBuilder<PackedMatrix> {

  /**
   * Number of rowsInBlock
   */
  private final int rowCount;

  /**
   * Number of getColumnDimension
   */
  private final int columnCount;

  /**
   * Packed columnPacked array
   */
  private double[] packed;

  /**
   * Construct packed matrix builder
   *
   * @param rowCount    number of rows
   * @param columnCount number of columns
   */
  public PackedMatrixBuilder(int rowCount, int columnCount) {
    super();
    this.rowCount = rowCount;
    this.columnCount = columnCount;
  }

  /**
   * Construct packed matrix builder
   *
   * @param rowCount    number of rows
   * @param columnCount number of columns
   */
  public PackedMatrixBuilder(int rowCount, int columnCount, double[] packed) {
    super();
    assert packed == null || packed.length == rowCount * columnCount;
    this.rowCount = rowCount;
    this.columnCount = columnCount;
    this.packed = packed;
  }

  /**
   * Returns a value from the packed array.
   *
   * @param index index into the packed array
   * @return value
   */
  double get(int index) {
    return packed == null ? 0.0 : packed[index];
  }


  /**
   * Set a range of values in the packed array.
   * @param index index into the packed array
   * @param values values to copy
   */
  void set(int index, double[] values, int offset, int length) {
    if (packed == null) {
        packed = new double[multiplyExact(rowCount, columnCount)];
    }
    System.arraycopy(values, offset, packed, index, length);
  }

  /**
   * Sets a value in the packed array
   *
   * @param index index in the packed array
   * @param value value to set
   */
  void set(int index, double value) {
    if (packed == null) {
      if (value != 0) {
        packed = new double[multiplyExact(rowCount, columnCount)];
        packed[index] = value;
      }
    }
    else {
      packed[index] = value;
    }
  }

  @Override
  public double get(int row, int column) {
    int index = toIndex(row, column);
    return get(index);
  }


  @Override
  public void set(int row, int column, double value) {
    int index = toIndex(row, column);
    set(index, value);
  }

  @Override
  public PackedMatrix toMatrix() {
    try {
      return new PackedMatrix(rowCount, columnCount, false, this.packed);
    } finally {
      // Null out the columnPacked array to prevent wrong use of the builder.
      // Subsequent calls to the builder's get/set/toMatrix methods will result
      // in NPE.
      this.packed = null;
    }
  }

  /**
   * Calculates the index in the columnPacked array
   *
   * @param row    row index
   * @param column column index
   * @return index into the packed array
   */
  private int toIndex(int row, int column) {
    return row * this.columnCount + column;
  }


  /**
   * Adds the result of a multiplication of two matrices to this builder.
   * @param a matrix a
   * @param b matrix b
   */
  void add(PackedMatrix a, PackedMatrix b) {
    this.packed = a.multiply(b, this.packed);
  }
}
