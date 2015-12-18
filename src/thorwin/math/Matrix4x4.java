/******************************************************************************
 * Copyright (C) 2013  Sebastiaan R. Hogenbirk                                *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU General Public License as published by       *
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package thorwin.math;

/**
 * 4x4 matrix.
 */
final class Matrix4x4 extends DefaultMatrix {

  /**
   * Value of row 0, column 0.
   */
  final double r0c0;

  /**
   * Value of row 0, column 1.
   */
  final double r0c1;

  /**
   * Value of row 0, column 2.
   */
  final double r0c2;

  /**
   * Value of row 0, column 3.
   */
  final double r0c3;

  /**
   * Value of row 1, column 0.
   */
  final double r1c0;

  /**
   * Value of row 1, column 1.
   */
  final double r1c1;

  /**
   * Value of row 1, column 2.
   */
  final double r1c2;

  /**
   * Value of row 1, column 3.
   */
  final double r1c3;

  /**
   * Value of row 2, column 0.
   */
  final double r2c0;

  /**
   * Value of row 2, column 1.
   */
  final double r2c1;

  /**
   * Value of row 2, column 2.
   */
  final double r2c2;

  /**
   * Value of row 2, column 3.
   */
  final double r2c3;

  /**
   * Value of row 3, column 0.
   */
  final double r3c0;

  /**
   * Value of row 3, column 1.
   */
  final double r3c1;

  /**
   * Value of row 3, column 2.
   */
  final double r3c2;

  /**
   * Value of row 3, column 3.
   */
  final double r3c3;




  /**
   * Construct a new Matrix4x4 instance.
   * @param r0c0 Value for row 0, column 0
   * @param r0c1 Value for row 0, column 1
   * @param r0c2 Value for row 0, column 2
   * @param r0c3 Value for row 0, column 3
   * @param r1c0 Value for row 1, column 0
   * @param r1c1 Value for row 1, column 1
   * @param r1c2 Value for row 1, column 2
   * @param r1c3 Value for row 1, column 3
   * @param r2c0 Value for row 2, column 0
   * @param r2c1 Value for row 2, column 1
   * @param r2c2 Value for row 2, column 2
   * @param r2c3 Value for row 2, column 3
   * @param r3c0 Value for row 3, column 0
   * @param r3c1 Value for row 3, column 1
   * @param r3c2 Value for row 3, column 2
   * @param r3c3 Value for row 3, column 3
   */
  public Matrix4x4(
    double r0c0,
    double r0c1,
    double r0c2,
    double r0c3,
    double r1c0,
    double r1c1,
    double r1c2,
    double r1c3,
    double r2c0,
    double r2c1,
    double r2c2,
    double r2c3,
    double r3c0,
    double r3c1,
    double r3c2,
    double r3c3
) {
  super();
    this.r0c0 = r0c0;
    this.r0c1 = r0c1;
    this.r0c2 = r0c2;
    this.r0c3 = r0c3;
    this.r1c0 = r1c0;
    this.r1c1 = r1c1;
    this.r1c2 = r1c2;
    this.r1c3 = r1c3;
    this.r2c0 = r2c0;
    this.r2c1 = r2c1;
    this.r2c2 = r2c2;
    this.r2c3 = r2c3;
    this.r3c0 = r3c0;
    this.r3c1 = r3c1;
    this.r3c2 = r3c2;
    this.r3c3 = r3c3;
  }







  /**
   * @return The number of rows in this matrix, which is 4.
   */
  @Override
  public int getRowDimension() {
    return 4;
  }


  /**
   * @return The number of columns in this matrix, which is 4.
   */
  @Override
  public int getColumnDimension() {
    return 4;
  }







  /**
   * Return the value of the specified entry.
   */
  @Override
  public double get(int row, int column) {
    switch (row) {

      case 0:
        switch (column) {
          case 0: return this.r0c0;
          case 1: return this.r0c1;
          case 2: return this.r0c2;
          case 3: return this.r0c3;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 1:
        switch (column) {
          case 0: return this.r1c0;
          case 1: return this.r1c1;
          case 2: return this.r1c2;
          case 3: return this.r1c3;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 2:
        switch (column) {
          case 0: return this.r2c0;
          case 1: return this.r2c1;
          case 2: return this.r2c2;
          case 3: return this.r2c3;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 3:
        switch (column) {
          case 0: return this.r3c0;
          case 1: return this.r3c1;
          case 2: return this.r3c2;
          case 3: return this.r3c3;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      default: throw new IllegalArgumentException("No such row: " + row);
    }
  }



  /**
   * Add a Matrix4x4 this this Matrix4x4.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix4x4 add(Matrix4x4 other) {
    return new Matrix4x4(
      this.r0c0 + other.r0c0,
      this.r0c1 + other.r0c1,
      this.r0c2 + other.r0c2,
      this.r0c3 + other.r0c3,
      this.r1c0 + other.r1c0,
      this.r1c1 + other.r1c1,
      this.r1c2 + other.r1c2,
      this.r1c3 + other.r1c3,
      this.r2c0 + other.r2c0,
      this.r2c1 + other.r2c1,
      this.r2c2 + other.r2c2,
      this.r2c3 + other.r2c3,
      this.r3c0 + other.r3c0,
      this.r3c1 + other.r3c1,
      this.r3c2 + other.r3c2,
      this.r3c3 + other.r3c3
    );
  }

  /**
   * Overridden to optimize addition if the other matrix
   * if of this class.
   */
  @Override
  public Matrix add(Matrix other) {
    if (other instanceof Matrix4x4) {
      return add((Matrix4x4) other);
    } else {
      return super.add(other);
    }
  }


  /**
   * Add a Matrix4x4 this this Matrix4x4.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix4x4 subtract(Matrix4x4 other) {
    return new Matrix4x4(
      this.r0c0 - other.r0c0,
      this.r0c1 - other.r0c1,
      this.r0c2 - other.r0c2,
      this.r0c3 - other.r0c3,
      this.r1c0 - other.r1c0,
      this.r1c1 - other.r1c1,
      this.r1c2 - other.r1c2,
      this.r1c3 - other.r1c3,
      this.r2c0 - other.r2c0,
      this.r2c1 - other.r2c1,
      this.r2c2 - other.r2c2,
      this.r2c3 - other.r2c3,
      this.r3c0 - other.r3c0,
      this.r3c1 - other.r3c1,
      this.r3c2 - other.r3c2,
      this.r3c3 - other.r3c3
    );
  }


  /**
   * Overridden to optimize addition if the other matrix
   * if of this class.
   */
  @Override
  public Matrix subtract(Matrix other) {
    if (other instanceof Matrix4x4) {
      return subtract((Matrix4x4) other);
    } else {
      return super.subtract(other);
    }
  }



  /**
   * Multiply this Matrix4x4 with a Matrix4x1.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix4x1 multiply(Matrix4x1 other) {
    return new Matrix4x1(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0 +
      this.r0c3 * other.r3c0,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0 +
      this.r1c2 * other.r2c0 +
      this.r1c3 * other.r3c0,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0 +
      this.r2c2 * other.r2c0 +
      this.r2c3 * other.r3c0,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0 +
      this.r3c2 * other.r2c0 +
      this.r3c3 * other.r3c0
    );
  }


  /**
   * Multiply this Matrix4x4 with a Matrix4x2.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix4x2 multiply(Matrix4x2 other) {
    return new Matrix4x2(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0 +
      this.r0c3 * other.r3c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1 +
      this.r0c2 * other.r2c1 +
      this.r0c3 * other.r3c1,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0 +
      this.r1c2 * other.r2c0 +
      this.r1c3 * other.r3c0,
      this.r1c0 * other.r0c1 +
      this.r1c1 * other.r1c1 +
      this.r1c2 * other.r2c1 +
      this.r1c3 * other.r3c1,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0 +
      this.r2c2 * other.r2c0 +
      this.r2c3 * other.r3c0,
      this.r2c0 * other.r0c1 +
      this.r2c1 * other.r1c1 +
      this.r2c2 * other.r2c1 +
      this.r2c3 * other.r3c1,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0 +
      this.r3c2 * other.r2c0 +
      this.r3c3 * other.r3c0,
      this.r3c0 * other.r0c1 +
      this.r3c1 * other.r1c1 +
      this.r3c2 * other.r2c1 +
      this.r3c3 * other.r3c1
    );
  }


  /**
   * Multiply this Matrix4x4 with a Matrix4x3.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix4x3 multiply(Matrix4x3 other) {
    return new Matrix4x3(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0 +
      this.r0c3 * other.r3c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1 +
      this.r0c2 * other.r2c1 +
      this.r0c3 * other.r3c1,
      this.r0c0 * other.r0c2 +
      this.r0c1 * other.r1c2 +
      this.r0c2 * other.r2c2 +
      this.r0c3 * other.r3c2,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0 +
      this.r1c2 * other.r2c0 +
      this.r1c3 * other.r3c0,
      this.r1c0 * other.r0c1 +
      this.r1c1 * other.r1c1 +
      this.r1c2 * other.r2c1 +
      this.r1c3 * other.r3c1,
      this.r1c0 * other.r0c2 +
      this.r1c1 * other.r1c2 +
      this.r1c2 * other.r2c2 +
      this.r1c3 * other.r3c2,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0 +
      this.r2c2 * other.r2c0 +
      this.r2c3 * other.r3c0,
      this.r2c0 * other.r0c1 +
      this.r2c1 * other.r1c1 +
      this.r2c2 * other.r2c1 +
      this.r2c3 * other.r3c1,
      this.r2c0 * other.r0c2 +
      this.r2c1 * other.r1c2 +
      this.r2c2 * other.r2c2 +
      this.r2c3 * other.r3c2,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0 +
      this.r3c2 * other.r2c0 +
      this.r3c3 * other.r3c0,
      this.r3c0 * other.r0c1 +
      this.r3c1 * other.r1c1 +
      this.r3c2 * other.r2c1 +
      this.r3c3 * other.r3c1,
      this.r3c0 * other.r0c2 +
      this.r3c1 * other.r1c2 +
      this.r3c2 * other.r2c2 +
      this.r3c3 * other.r3c2
    );
  }


  /**
   * Multiply this Matrix4x4 with a Matrix4x4.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix4x4 multiply(Matrix4x4 other) {
    return new Matrix4x4(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0 +
      this.r0c3 * other.r3c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1 +
      this.r0c2 * other.r2c1 +
      this.r0c3 * other.r3c1,
      this.r0c0 * other.r0c2 +
      this.r0c1 * other.r1c2 +
      this.r0c2 * other.r2c2 +
      this.r0c3 * other.r3c2,
      this.r0c0 * other.r0c3 +
      this.r0c1 * other.r1c3 +
      this.r0c2 * other.r2c3 +
      this.r0c3 * other.r3c3,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0 +
      this.r1c2 * other.r2c0 +
      this.r1c3 * other.r3c0,
      this.r1c0 * other.r0c1 +
      this.r1c1 * other.r1c1 +
      this.r1c2 * other.r2c1 +
      this.r1c3 * other.r3c1,
      this.r1c0 * other.r0c2 +
      this.r1c1 * other.r1c2 +
      this.r1c2 * other.r2c2 +
      this.r1c3 * other.r3c2,
      this.r1c0 * other.r0c3 +
      this.r1c1 * other.r1c3 +
      this.r1c2 * other.r2c3 +
      this.r1c3 * other.r3c3,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0 +
      this.r2c2 * other.r2c0 +
      this.r2c3 * other.r3c0,
      this.r2c0 * other.r0c1 +
      this.r2c1 * other.r1c1 +
      this.r2c2 * other.r2c1 +
      this.r2c3 * other.r3c1,
      this.r2c0 * other.r0c2 +
      this.r2c1 * other.r1c2 +
      this.r2c2 * other.r2c2 +
      this.r2c3 * other.r3c2,
      this.r2c0 * other.r0c3 +
      this.r2c1 * other.r1c3 +
      this.r2c2 * other.r2c3 +
      this.r2c3 * other.r3c3,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0 +
      this.r3c2 * other.r2c0 +
      this.r3c3 * other.r3c0,
      this.r3c0 * other.r0c1 +
      this.r3c1 * other.r1c1 +
      this.r3c2 * other.r2c1 +
      this.r3c3 * other.r3c1,
      this.r3c0 * other.r0c2 +
      this.r3c1 * other.r1c2 +
      this.r3c2 * other.r2c2 +
      this.r3c3 * other.r3c2,
      this.r3c0 * other.r0c3 +
      this.r3c1 * other.r1c3 +
      this.r3c2 * other.r2c3 +
      this.r3c3 * other.r3c3
    );
  }


  /**
   * Multiply this Matrix4x4 with a Matrix4x5.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix4x5 multiply(Matrix4x5 other) {
    return new Matrix4x5(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0 +
      this.r0c3 * other.r3c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1 +
      this.r0c2 * other.r2c1 +
      this.r0c3 * other.r3c1,
      this.r0c0 * other.r0c2 +
      this.r0c1 * other.r1c2 +
      this.r0c2 * other.r2c2 +
      this.r0c3 * other.r3c2,
      this.r0c0 * other.r0c3 +
      this.r0c1 * other.r1c3 +
      this.r0c2 * other.r2c3 +
      this.r0c3 * other.r3c3,
      this.r0c0 * other.r0c4 +
      this.r0c1 * other.r1c4 +
      this.r0c2 * other.r2c4 +
      this.r0c3 * other.r3c4,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0 +
      this.r1c2 * other.r2c0 +
      this.r1c3 * other.r3c0,
      this.r1c0 * other.r0c1 +
      this.r1c1 * other.r1c1 +
      this.r1c2 * other.r2c1 +
      this.r1c3 * other.r3c1,
      this.r1c0 * other.r0c2 +
      this.r1c1 * other.r1c2 +
      this.r1c2 * other.r2c2 +
      this.r1c3 * other.r3c2,
      this.r1c0 * other.r0c3 +
      this.r1c1 * other.r1c3 +
      this.r1c2 * other.r2c3 +
      this.r1c3 * other.r3c3,
      this.r1c0 * other.r0c4 +
      this.r1c1 * other.r1c4 +
      this.r1c2 * other.r2c4 +
      this.r1c3 * other.r3c4,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0 +
      this.r2c2 * other.r2c0 +
      this.r2c3 * other.r3c0,
      this.r2c0 * other.r0c1 +
      this.r2c1 * other.r1c1 +
      this.r2c2 * other.r2c1 +
      this.r2c3 * other.r3c1,
      this.r2c0 * other.r0c2 +
      this.r2c1 * other.r1c2 +
      this.r2c2 * other.r2c2 +
      this.r2c3 * other.r3c2,
      this.r2c0 * other.r0c3 +
      this.r2c1 * other.r1c3 +
      this.r2c2 * other.r2c3 +
      this.r2c3 * other.r3c3,
      this.r2c0 * other.r0c4 +
      this.r2c1 * other.r1c4 +
      this.r2c2 * other.r2c4 +
      this.r2c3 * other.r3c4,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0 +
      this.r3c2 * other.r2c0 +
      this.r3c3 * other.r3c0,
      this.r3c0 * other.r0c1 +
      this.r3c1 * other.r1c1 +
      this.r3c2 * other.r2c1 +
      this.r3c3 * other.r3c1,
      this.r3c0 * other.r0c2 +
      this.r3c1 * other.r1c2 +
      this.r3c2 * other.r2c2 +
      this.r3c3 * other.r3c2,
      this.r3c0 * other.r0c3 +
      this.r3c1 * other.r1c3 +
      this.r3c2 * other.r2c3 +
      this.r3c3 * other.r3c3,
      this.r3c0 * other.r0c4 +
      this.r3c1 * other.r1c4 +
      this.r3c2 * other.r2c4 +
      this.r3c3 * other.r3c4
    );
  }



  @Override
  public Matrix multiply(Matrix other) {
    if(other instanceof Matrix4x1) {
      return multiply((Matrix4x1) other);
    }
    if(other instanceof Matrix4x2) {
      return multiply((Matrix4x2) other);
    }
    if(other instanceof Matrix4x3) {
      return multiply((Matrix4x3) other);
    }
    if(other instanceof Matrix4x4) {
      return multiply((Matrix4x4) other);
    }
    if(other instanceof Matrix4x5) {
      return multiply((Matrix4x5) other);
    }
    return super.multiply(other);
  }


  /**
   * Scale this class.
   * @return The resulting matrix
   */
  @Override
  public Matrix4x4 multiply(double multiplicand) {
    return new Matrix4x4(
      this.r0c0 * multiplicand,
      this.r0c1 * multiplicand,
      this.r0c2 * multiplicand,
      this.r0c3 * multiplicand,
      this.r1c0 * multiplicand,
      this.r1c1 * multiplicand,
      this.r1c2 * multiplicand,
      this.r1c3 * multiplicand,
      this.r2c0 * multiplicand,
      this.r2c1 * multiplicand,
      this.r2c2 * multiplicand,
      this.r2c3 * multiplicand,
      this.r3c0 * multiplicand,
      this.r3c1 * multiplicand,
      this.r3c2 * multiplicand,
      this.r3c3 * multiplicand
    );
  }


  /**
   * Transpose the matrix.
   * @return The resulting matrix
   */
  public Matrix4x4 transpose() {
    return new Matrix4x4(
      this.r0c0,
      this.r1c0,
      this.r2c0,
      this.r3c0,
      this.r0c1,
      this.r1c1,
      this.r2c1,
      this.r3c1,
      this.r0c2,
      this.r1c2,
      this.r2c2,
      this.r3c2,
      this.r0c3,
      this.r1c3,
      this.r2c3,
      this.r3c3
    );
  }




  /**
   * Create a packed array from this matrix.
   * @return row-major packed array
   */
  @Override
  public double[] toArray() {
    return new double[]{
      this.r0c0,
      this.r0c1,
      this.r0c2,
      this.r0c3,
      this.r1c0,
      this.r1c1,
      this.r1c2,
      this.r1c3,
      this.r2c0,
      this.r2c1,
      this.r2c2,
      this.r2c3,
      this.r3c0,
      this.r3c1,
      this.r3c2,
      this.r3c3
    };
  }





}