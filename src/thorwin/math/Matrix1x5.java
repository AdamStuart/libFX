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
 * 1x5 matrix.
 */
final class Matrix1x5 extends DefaultMatrix {

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
   * Value of row 0, column 4.
   */
  final double r0c4;




  /**
   * Construct a new Matrix1x5 instance.
   * @param r0c0 Value for row 0, column 0
   * @param r0c1 Value for row 0, column 1
   * @param r0c2 Value for row 0, column 2
   * @param r0c3 Value for row 0, column 3
   * @param r0c4 Value for row 0, column 4
   */
  public Matrix1x5(
    double r0c0,
    double r0c1,
    double r0c2,
    double r0c3,
    double r0c4
) {
  super();
    this.r0c0 = r0c0;
    this.r0c1 = r0c1;
    this.r0c2 = r0c2;
    this.r0c3 = r0c3;
    this.r0c4 = r0c4;
  }







  /**
   * @return The number of rows in this matrix, which is 1.
   */
  @Override
  public int getRowDimension() {
    return 1;
  }


  /**
   * @return The number of columns in this matrix, which is 5.
   */
  @Override
  public int getColumnDimension() {
    return 5;
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
          case 4: return this.r0c4;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      default: throw new IllegalArgumentException("No such row: " + row);
    }
  }



  /**
   * Add a Matrix1x5 this this Matrix1x5.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix1x5 add(Matrix1x5 other) {
    return new Matrix1x5(
      this.r0c0 + other.r0c0,
      this.r0c1 + other.r0c1,
      this.r0c2 + other.r0c2,
      this.r0c3 + other.r0c3,
      this.r0c4 + other.r0c4
    );
  }

  /**
   * Overridden to optimize addition if the other matrix
   * if of this class.
   */
  @Override
  public Matrix add(Matrix other) {
    if (other instanceof Matrix1x5) {
      return add((Matrix1x5) other);
    } else {
      return super.add(other);
    }
  }


  /**
   * Add a Matrix1x5 this this Matrix1x5.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix1x5 subtract(Matrix1x5 other) {
    return new Matrix1x5(
      this.r0c0 - other.r0c0,
      this.r0c1 - other.r0c1,
      this.r0c2 - other.r0c2,
      this.r0c3 - other.r0c3,
      this.r0c4 - other.r0c4
    );
  }


  /**
   * Overridden to optimize addition if the other matrix
   * if of this class.
   */
  @Override
  public Matrix subtract(Matrix other) {
    if (other instanceof Matrix1x5) {
      return subtract((Matrix1x5) other);
    } else {
      return super.subtract(other);
    }
  }



  /**
   * Multiply this Matrix1x5 with a Matrix5x1.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix1x1 multiply(Matrix5x1 other) {
    return new Matrix1x1(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0 +
      this.r0c3 * other.r3c0 +
      this.r0c4 * other.r4c0
    );
  }


  /**
   * Multiply this Matrix1x5 with a Matrix5x2.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix1x2 multiply(Matrix5x2 other) {
    return new Matrix1x2(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0 +
      this.r0c3 * other.r3c0 +
      this.r0c4 * other.r4c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1 +
      this.r0c2 * other.r2c1 +
      this.r0c3 * other.r3c1 +
      this.r0c4 * other.r4c1
    );
  }


  /**
   * Multiply this Matrix1x5 with a Matrix5x3.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix1x3 multiply(Matrix5x3 other) {
    return new Matrix1x3(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0 +
      this.r0c3 * other.r3c0 +
      this.r0c4 * other.r4c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1 +
      this.r0c2 * other.r2c1 +
      this.r0c3 * other.r3c1 +
      this.r0c4 * other.r4c1,
      this.r0c0 * other.r0c2 +
      this.r0c1 * other.r1c2 +
      this.r0c2 * other.r2c2 +
      this.r0c3 * other.r3c2 +
      this.r0c4 * other.r4c2
    );
  }


  /**
   * Multiply this Matrix1x5 with a Matrix5x4.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix1x4 multiply(Matrix5x4 other) {
    return new Matrix1x4(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0 +
      this.r0c3 * other.r3c0 +
      this.r0c4 * other.r4c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1 +
      this.r0c2 * other.r2c1 +
      this.r0c3 * other.r3c1 +
      this.r0c4 * other.r4c1,
      this.r0c0 * other.r0c2 +
      this.r0c1 * other.r1c2 +
      this.r0c2 * other.r2c2 +
      this.r0c3 * other.r3c2 +
      this.r0c4 * other.r4c2,
      this.r0c0 * other.r0c3 +
      this.r0c1 * other.r1c3 +
      this.r0c2 * other.r2c3 +
      this.r0c3 * other.r3c3 +
      this.r0c4 * other.r4c3
    );
  }


  /**
   * Multiply this Matrix1x5 with a Matrix5x5.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix1x5 multiply(Matrix5x5 other) {
    return new Matrix1x5(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0 +
      this.r0c3 * other.r3c0 +
      this.r0c4 * other.r4c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1 +
      this.r0c2 * other.r2c1 +
      this.r0c3 * other.r3c1 +
      this.r0c4 * other.r4c1,
      this.r0c0 * other.r0c2 +
      this.r0c1 * other.r1c2 +
      this.r0c2 * other.r2c2 +
      this.r0c3 * other.r3c2 +
      this.r0c4 * other.r4c2,
      this.r0c0 * other.r0c3 +
      this.r0c1 * other.r1c3 +
      this.r0c2 * other.r2c3 +
      this.r0c3 * other.r3c3 +
      this.r0c4 * other.r4c3,
      this.r0c0 * other.r0c4 +
      this.r0c1 * other.r1c4 +
      this.r0c2 * other.r2c4 +
      this.r0c3 * other.r3c4 +
      this.r0c4 * other.r4c4
    );
  }



  @Override
  public Matrix multiply(Matrix other) {
    if(other instanceof Matrix5x1) {
      return multiply((Matrix5x1) other);
    }
    if(other instanceof Matrix5x2) {
      return multiply((Matrix5x2) other);
    }
    if(other instanceof Matrix5x3) {
      return multiply((Matrix5x3) other);
    }
    if(other instanceof Matrix5x4) {
      return multiply((Matrix5x4) other);
    }
    if(other instanceof Matrix5x5) {
      return multiply((Matrix5x5) other);
    }
    return super.multiply(other);
  }


  /**
   * Scale this class.
   * @return The resulting matrix
   */
  @Override
  public Matrix1x5 multiply(double multiplicand) {
    return new Matrix1x5(
      this.r0c0 * multiplicand,
      this.r0c1 * multiplicand,
      this.r0c2 * multiplicand,
      this.r0c3 * multiplicand,
      this.r0c4 * multiplicand
    );
  }


  /**
   * Transpose the matrix.
   * @return The resulting matrix
   */
  public Matrix5x1 transpose() {
    return new Matrix5x1(
      this.r0c0,
      this.r0c1,
      this.r0c2,
      this.r0c3,
      this.r0c4
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
      this.r0c4
    };
  }





}