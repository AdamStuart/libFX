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

import static java.lang.Integer.min;

import java.util.Arrays;


/**
 * Implementation of a matrix that stores its data in an array (row
 * packed).
 * This class is immutable.
 */
final class PackedMatrix extends DefaultMatrix {

  /**
   * The number of rows in this matrix.
   */
  private final int rows;
  /**
   * The number of getColumnDimension in this matrix.
   */
  private final int columns;
  /**
   * The columnPacked in this matrix.
   */
  private final double[] packed;

  /**
   * Transposed flag.
   */
  private final boolean transposed;


  /**
   * Constructs a new column columnPacked matrix. This (package-private)
   * constructor is designed to allow optimal performance of internal
   * calculations. It does not copy the columnPacked array or validate the
   * parameters. Not copying the columnPacked array potentially exposes the
   * internal columnPacked and break immutability. Callers are expected to
   * maintain encapsulation and immutability.
   *
   * @param rows       number of rows in the matrix
   * @param columns    number of columns in the matrix
   * @param transposed if true, this matrix has been transposed
   * @param packed     elements
   */
  PackedMatrix(int rows, int columns, boolean transposed, double... packed) {
    assert rows > 0;

    this.rows = rows;
    this.columns = columns;
    this.packed = packed;
    // only transposed if there is any data
    this.transposed = transposed;
  }

  @Override
  public int getRowDimension() {
    return transposed ? this.columns : this.rows;
  }

  @Override
  public int getColumnDimension() {
    return transposed ? this.rows :  this.columns;
  }

  @Override
  public double get(int row, int column) {

    if (transposed) {
      // check validity of indices
      if (column < 0 || column >= this.rows) {
        throw new IndexOutOfBoundsException("Invalid row index: " + row);
      }
      if (row < 0 || row >= this.columns) {
        throw new IndexOutOfBoundsException("Invalid column index: " + column);
      }

      if (this.packed == null) {
        return 0.0;
      }
      else {
        int index = column * this.columns + row;
        return this.packed[index];
      }
    }
    else {
      // check validity of indices
      if (row < 0 || row >= this.rows) {
        throw new IndexOutOfBoundsException("Invalid row index: " + row);
      }
      if (column < 0 || column >= this.columns) {
        throw new IndexOutOfBoundsException("Invalid column index: " + column);
      }

      if (this.packed == null) {
        return 0.0;
      }
      else {
        int index = row * this.columns + column;
        return this.packed[index];
      }
    }
  }

  @Override
  public PackedMatrix transpose() {
    return new PackedMatrix(rows, columns, !transposed, packed);
  }

  @Override
  public PackedMatrix evaluate() {
    if (transposed) {
      if (this.packed == null) {
        return new PackedMatrix(columns, rows, false, null);
      }
      else {
        return new PackedMatrix(columns, rows, false, transpose(this.packed,
                                                                rows,
                                                                columns));
      }
    }
    else {
      return this;
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
    int index1 = 0;
    for (int i = 0; i < columns; i++) {
      int index2 = i;
      int fence  = index1 + rows;
      while ( index1 < fence ) {
        transposed[index1] = packed[index2];
        index1++;
        index2 += columns;
      }
    }
    return transposed;
  }


  @Override
  public Matrix add(Matrix other) {

    // check if the other matrix is actually column columnPacked so we can use
    // the better performing add method
    if (other instanceof PackedMatrix) {
      return add((PackedMatrix) other);
    } else {
      return super.add(other);
    }
  }

  /**
   * Adds a packed matrix to this matrix builder.
   *
   * @param other the other packed matrix
   * @return resulting matrix
   */
  public PackedMatrix add(PackedMatrix other) {

    // check matrix dimensions
    if (this.getRowDimension() != other.getRowDimension() ||
        this.getColumnDimension() != other.getColumnDimension()) {
      throw new IllegalArgumentException("Matrices should have equal number of rows and columns");
    }

    // if matrix only contains zero values, no adding required
    if (this.packed == null) return other;
    if (other.packed == null) return this;

    // if transposed flags are equal, we can just add the arrays
    if (this.transposed == other.transposed) {
      double[] packed = new double[this.packed.length];
      for (int i = 0; i < packed.length; i++) {
        packed[i] = this.packed[i] + other.packed[i];
      }
      return new PackedMatrix(rows,
                              columns,
                              transposed,
                              packed);
    }

    // this matrix is transposed, the other is not
    else if (transposed) {
      double[] packed = new double[this.packed.length];
      int index1 = 0;
      for (int i = 0; i < columns; i++) {
        int index2 = i;
        int fence  = index1 + rows;
        while ( index1 < fence ) {
          packed[index1] = this.packed[index2] + other.packed[index1];
          index1++;
          index2 += columns;
        }
      }
      return new PackedMatrix(columns,
                              rows,
                              false,
                              packed);
    }
    // this matrix is not transposed, the other is
    else {
      return other.add(this);
    }
  }

  @Override
  public Matrix subtract(Matrix other) {

    // check if the other matrix is actually column columnPacked so we can use
    // the better performing subtract method
    if (other instanceof PackedMatrix) {
      return subtract((PackedMatrix) other);
    } else {
      return super.subtract(other);
    }
  }

  /**
   * Subtracts a packed matrix from this matrix.
   *
   * @param other The other packed matrix
   * @return The resulting matrix
   */
  public PackedMatrix subtract(PackedMatrix other) {

    // check matrix dimensions
    if (this.getRowDimension() != other.getRowDimension() ||
        this.getColumnDimension() != other.getColumnDimension()) {
      throw new IllegalArgumentException("Matrices should have equal number of rows and columns");
    }

    // if either matrix contains only contains zero's
    if (other.packed == null) return this;
    if (this.packed == null) return other.multiply(-1);

    // if transposed flags are equal, we can just subtract the arrays
    if (this.transposed == other.transposed) {
      double[] packed = new double[this.packed.length];
      for (int i = 0; i < packed.length; i++) {
        packed[i] = this.packed[i] - other.packed[i];
      }
      return new PackedMatrix(this.rows, this.columns, transposed, packed);
    }

    // this matrix is transposed, the other is not
    else if (transposed) {
      double[] packed = new double[this.packed.length];
      int index1 = 0;
      for (int i = 0; i < columns; i++) {
        int index2 = i;
        int fence  = index1 + rows;
        while ( index1 < fence ) {
          packed[index1] = this.packed[index2] - other.packed[index1];
          index1++;
          index2 += columns;
        }
      }

      return new PackedMatrix(this.columns, this.rows, false, packed);
    }
    // this matrix is not transposed, the other is
    else {
      double[] packed = new double[this.packed.length];
      int index1 = 0;
      for (int i = 0; i < other.columns; i++) {
        int index2 = i;
        int fence  = index1 + other.rows;
        while ( index1 < fence ) {
          packed[index1] =  this.packed[index1] - other.packed[index2];
          index1++;
          index2 += other.columns;
        }
      }

      return new PackedMatrix(other.columns, other.rows, false, packed);
    }
  }

  /**
   * Multiplies this matrix with a real value (scale)
   *
   * @param multiplicand multiplicand
   * @return resulting matrix
   */
  public PackedMatrix multiply(double multiplicand) {

    // if only zero's multiplication will not do anything
    if (this.packed == null) return this;

    double[] packed = new double[this.packed.length];

    for (int i = 0; i < packed.length; i++) {
      packed[i] = this.packed[i] * multiplicand;
    }

    return new PackedMatrix(this.rows, this.columns, transposed, packed);
  }

  @Override
  public Matrix multiply(Matrix other) {
    if (other instanceof PackedMatrix) {
      return multiply((PackedMatrix) other);
    } else {
      return super.multiply(other);
    }
  }

  /**
   * Multiplies this matrix with another packed matrix.
   *
   * @param other another packed matrix
   * @return resulting matrix
   */
  public PackedMatrix multiply(PackedMatrix other) {

    if (getColumnDimension() != other.getRowDimension()) {
      throw new IllegalArgumentException("Matrix inner dimensions must agree.");
    }

    // if either side is zero, we know the outcome
    if (other.packed == null || this.packed == null) {
      return new PackedMatrix(getRowDimension(),
                              other.getColumnDimension(),
                              false,
                              null);
    }

    int m = getRowDimension();
    int n = other.getColumnDimension();
    int k = getColumnDimension();

    double[] packed = new double[m * n];

    MatrixOperations.dgemm(this.packed,
                           other.packed,
                           packed,
                           m,
                           n,
                           k,
                           this.transposed,
                           other.transposed,
                           1,
                           0);

    return new PackedMatrix(m,n , false, packed);
  }


  /**
   * Multiply the matrix with the other and add the result to the specified
   * packed array.
   * @param other other matrix
   * @param packed row-major packed matrix (or null)
   * @return the altered packed array (may be null)
   */
  double[] multiply(PackedMatrix other, double[] packed) {

    // if either matrix contains only zero's we know the resulting
    // matrix also only contains zero's, so we're done
    if (this.packed == null || other.packed == null) {
      return packed;
    }

    int m = getRowDimension();
    int n = other.getColumnDimension();
    int k = getColumnDimension();

    if (packed == null) {
      packed = new double[m * n];
    }

    MatrixOperations.dgemm(this.packed,
                           other.packed,
                           packed,
                           m,
                           n,
                           k,
                           this.transposed,
                           other.transposed,
                           1,
                           1);

    return packed;
  }

  @Override
  public Vector row(int row) {
    if (this.packed == null) {
      return new ArrayVector(new double[getColumnDimension()]);
    }

    if (transposed) {
      return super.row(row);
    }
    else {
      int offset = row * columns;
      double[] data = new double[columns];
      System.arraycopy(this.packed, offset, data, 0, data.length);
      return new ArrayVector(data);
    }
  }


  /**
   * Convert the matrix to a row-major packed array.
   * @return array
   */
  public double[] toArray() {
    // make sure to return an empty array if no data
    if (packed == null) {
      return new double[rows * columns];
    }

    // make sure to evaluate transposition
    if (transposed) return evaluate().toArray();

    return packed.clone();
  }


  /**
   * Constructs a builder initialized with the data from this matrix.
   *
   * @return matrix builder
   */
  MatrixBuilder<PackedMatrix> toBuilder() {

    double[] packed = null;

    if (this.packed != null) {
      packed = transposed ? transpose(this.packed,
                                      rows,
                                      columns) : this.packed.clone();
    }

    return new PackedMatrixBuilder(getRowDimension(), getColumnDimension(), packed);
  }


  /**
   * Returns true if this matrix contains does not contain any non-zero values.
   * Or in more human terms, it is guaranteed to only contain zero's but
   * it still may return false if unknown.
   * @return true if guaranteed to only contain zero's
   */
  boolean isEmpty() {
    return this.packed == null;
  }


  /**
   * Copy the packed matrix to another array
   * @param dst data array
   * @param offset offset into the data array
   * @param stride row stride (usually the number of columns in the target
   *               matrix)
   */
  void copy(int rows, int columns, double[] dst, int offset, int stride) {
    if (transposed) {
      evaluate().copy(rows, columns, dst, offset, stride);
    }
    else {
      rows = min(rows, this.rows);
      columns = min(columns, this.columns);

      for (int row = 0; row < rows; row++) {
        int index = offset + stride * row;
        if (packed != null) {
          System.arraycopy(packed, row * this.columns, dst, index, columns);
        }
        else {
          Arrays.fill(dst, index, index + columns, 0);
        }
      }
    }
  }
}
