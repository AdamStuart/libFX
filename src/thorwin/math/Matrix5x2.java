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
 * 5x2 matrix.
 */
final class Matrix5x2 extends DefaultMatrix {

  /**
   * Value of row 0, column 0.
   */
  final double r0c0;

  /**
   * Value of row 0, column 1.
   */
  final double r0c1;

  /**
   * Value of row 1, column 0.
   */
  final double r1c0;

  /**
   * Value of row 1, column 1.
   */
  final double r1c1;

  /**
   * Value of row 2, column 0.
   */
  final double r2c0;

  /**
   * Value of row 2, column 1.
   */
  final double r2c1;

  /**
   * Value of row 3, column 0.
   */
  final double r3c0;

  /**
   * Value of row 3, column 1.
   */
  final double r3c1;

  /**
   * Value of row 4, column 0.
   */
  final double r4c0;

  /**
   * Value of row 4, column 1.
   */
  final double r4c1;




  /**
   * Construct a new Matrix5x2 instance.
   * @param r0c0 Value for row 0, column 0
   * @param r0c1 Value for row 0, column 1
   * @param r1c0 Value for row 1, column 0
   * @param r1c1 Value for row 1, column 1
   * @param r2c0 Value for row 2, column 0
   * @param r2c1 Value for row 2, column 1
   * @param r3c0 Value for row 3, column 0
   * @param r3c1 Value for row 3, column 1
   * @param r4c0 Value for row 4, column 0
   * @param r4c1 Value for row 4, column 1
   */
  public Matrix5x2(
    double r0c0,
    double r0c1,
    double r1c0,
    double r1c1,
    double r2c0,
    double r2c1,
    double r3c0,
    double r3c1,
    double r4c0,
    double r4c1
) {
  super();
    this.r0c0 = r0c0;
    this.r0c1 = r0c1;
    this.r1c0 = r1c0;
    this.r1c1 = r1c1;
    this.r2c0 = r2c0;
    this.r2c1 = r2c1;
    this.r3c0 = r3c0;
    this.r3c1 = r3c1;
    this.r4c0 = r4c0;
    this.r4c1 = r4c1;
  }







  /**
   * @return The number of rows in this matrix, which is 5.
   */
  @Override
  public int getRowDimension() {
    return 5;
  }


  /**
   * @return The number of columns in this matrix, which is 2.
   */
  @Override
  public int getColumnDimension() {
    return 2;
  }




  /**
   * Get the row as a vector.
   * @return The vector
   */
  @Override
  public Vector2D row(int index) {
    return new Vector2D(
      get(index, 0),
      get(index, 1)
    );
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
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 1:
        switch (column) {
          case 0: return this.r1c0;
          case 1: return this.r1c1;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 2:
        switch (column) {
          case 0: return this.r2c0;
          case 1: return this.r2c1;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 3:
        switch (column) {
          case 0: return this.r3c0;
          case 1: return this.r3c1;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 4:
        switch (column) {
          case 0: return this.r4c0;
          case 1: return this.r4c1;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      default: throw new IllegalArgumentException("No such row: " + row);
    }
  }



  /**
   * Add a Matrix5x2 this this Matrix5x2.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix5x2 add(Matrix5x2 other) {
    return new Matrix5x2(
      this.r0c0 + other.r0c0,
      this.r0c1 + other.r0c1,
      this.r1c0 + other.r1c0,
      this.r1c1 + other.r1c1,
      this.r2c0 + other.r2c0,
      this.r2c1 + other.r2c1,
      this.r3c0 + other.r3c0,
      this.r3c1 + other.r3c1,
      this.r4c0 + other.r4c0,
      this.r4c1 + other.r4c1
    );
  }

  /**
   * Overridden to optimize addition if the other matrix
   * if of this class.
   */
  @Override
  public Matrix add(Matrix other) {
    if (other instanceof Matrix5x2) {
      return add((Matrix5x2) other);
    } else {
      return super.add(other);
    }
  }


  /**
   * Add a Matrix5x2 this this Matrix5x2.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix5x2 subtract(Matrix5x2 other) {
    return new Matrix5x2(
      this.r0c0 - other.r0c0,
      this.r0c1 - other.r0c1,
      this.r1c0 - other.r1c0,
      this.r1c1 - other.r1c1,
      this.r2c0 - other.r2c0,
      this.r2c1 - other.r2c1,
      this.r3c0 - other.r3c0,
      this.r3c1 - other.r3c1,
      this.r4c0 - other.r4c0,
      this.r4c1 - other.r4c1
    );
  }


  /**
   * Overridden to optimize addition if the other matrix
   * if of this class.
   */
  @Override
  public Matrix subtract(Matrix other) {
    if (other instanceof Matrix5x2) {
      return subtract((Matrix5x2) other);
    } else {
      return super.subtract(other);
    }
  }



  /**
   * Multiply this Matrix5x2 with a Matrix2x1.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix5x1 multiply(Matrix2x1 other) {
    return new Matrix5x1(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0,
      this.r4c0 * other.r0c0 +
      this.r4c1 * other.r1c0
    );
  }


  /**
   * Multiply this Matrix5x2 with a Matrix2x2.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix5x2 multiply(Matrix2x2 other) {
    return new Matrix5x2(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0,
      this.r1c0 * other.r0c1 +
      this.r1c1 * other.r1c1,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0,
      this.r2c0 * other.r0c1 +
      this.r2c1 * other.r1c1,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0,
      this.r3c0 * other.r0c1 +
      this.r3c1 * other.r1c1,
      this.r4c0 * other.r0c0 +
      this.r4c1 * other.r1c0,
      this.r4c0 * other.r0c1 +
      this.r4c1 * other.r1c1
    );
  }


  /**
   * Multiply this Matrix5x2 with a Matrix2x3.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix5x3 multiply(Matrix2x3 other) {
    return new Matrix5x3(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1,
      this.r0c0 * other.r0c2 +
      this.r0c1 * other.r1c2,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0,
      this.r1c0 * other.r0c1 +
      this.r1c1 * other.r1c1,
      this.r1c0 * other.r0c2 +
      this.r1c1 * other.r1c2,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0,
      this.r2c0 * other.r0c1 +
      this.r2c1 * other.r1c1,
      this.r2c0 * other.r0c2 +
      this.r2c1 * other.r1c2,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0,
      this.r3c0 * other.r0c1 +
      this.r3c1 * other.r1c1,
      this.r3c0 * other.r0c2 +
      this.r3c1 * other.r1c2,
      this.r4c0 * other.r0c0 +
      this.r4c1 * other.r1c0,
      this.r4c0 * other.r0c1 +
      this.r4c1 * other.r1c1,
      this.r4c0 * other.r0c2 +
      this.r4c1 * other.r1c2
    );
  }


  /**
   * Multiply this Matrix5x2 with a Matrix2x4.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix5x4 multiply(Matrix2x4 other) {
    return new Matrix5x4(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1,
      this.r0c0 * other.r0c2 +
      this.r0c1 * other.r1c2,
      this.r0c0 * other.r0c3 +
      this.r0c1 * other.r1c3,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0,
      this.r1c0 * other.r0c1 +
      this.r1c1 * other.r1c1,
      this.r1c0 * other.r0c2 +
      this.r1c1 * other.r1c2,
      this.r1c0 * other.r0c3 +
      this.r1c1 * other.r1c3,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0,
      this.r2c0 * other.r0c1 +
      this.r2c1 * other.r1c1,
      this.r2c0 * other.r0c2 +
      this.r2c1 * other.r1c2,
      this.r2c0 * other.r0c3 +
      this.r2c1 * other.r1c3,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0,
      this.r3c0 * other.r0c1 +
      this.r3c1 * other.r1c1,
      this.r3c0 * other.r0c2 +
      this.r3c1 * other.r1c2,
      this.r3c0 * other.r0c3 +
      this.r3c1 * other.r1c3,
      this.r4c0 * other.r0c0 +
      this.r4c1 * other.r1c0,
      this.r4c0 * other.r0c1 +
      this.r4c1 * other.r1c1,
      this.r4c0 * other.r0c2 +
      this.r4c1 * other.r1c2,
      this.r4c0 * other.r0c3 +
      this.r4c1 * other.r1c3
    );
  }


  /**
   * Multiply this Matrix5x2 with a Matrix2x5.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix5x5 multiply(Matrix2x5 other) {
    return new Matrix5x5(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1,
      this.r0c0 * other.r0c2 +
      this.r0c1 * other.r1c2,
      this.r0c0 * other.r0c3 +
      this.r0c1 * other.r1c3,
      this.r0c0 * other.r0c4 +
      this.r0c1 * other.r1c4,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0,
      this.r1c0 * other.r0c1 +
      this.r1c1 * other.r1c1,
      this.r1c0 * other.r0c2 +
      this.r1c1 * other.r1c2,
      this.r1c0 * other.r0c3 +
      this.r1c1 * other.r1c3,
      this.r1c0 * other.r0c4 +
      this.r1c1 * other.r1c4,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0,
      this.r2c0 * other.r0c1 +
      this.r2c1 * other.r1c1,
      this.r2c0 * other.r0c2 +
      this.r2c1 * other.r1c2,
      this.r2c0 * other.r0c3 +
      this.r2c1 * other.r1c3,
      this.r2c0 * other.r0c4 +
      this.r2c1 * other.r1c4,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0,
      this.r3c0 * other.r0c1 +
      this.r3c1 * other.r1c1,
      this.r3c0 * other.r0c2 +
      this.r3c1 * other.r1c2,
      this.r3c0 * other.r0c3 +
      this.r3c1 * other.r1c3,
      this.r3c0 * other.r0c4 +
      this.r3c1 * other.r1c4,
      this.r4c0 * other.r0c0 +
      this.r4c1 * other.r1c0,
      this.r4c0 * other.r0c1 +
      this.r4c1 * other.r1c1,
      this.r4c0 * other.r0c2 +
      this.r4c1 * other.r1c2,
      this.r4c0 * other.r0c3 +
      this.r4c1 * other.r1c3,
      this.r4c0 * other.r0c4 +
      this.r4c1 * other.r1c4
    );
  }



  @Override
  public Matrix multiply(Matrix other) {
    if(other instanceof Matrix2x1) {
      return multiply((Matrix2x1) other);
    }
    if(other instanceof Matrix2x2) {
      return multiply((Matrix2x2) other);
    }
    if(other instanceof Matrix2x3) {
      return multiply((Matrix2x3) other);
    }
    if(other instanceof Matrix2x4) {
      return multiply((Matrix2x4) other);
    }
    if(other instanceof Matrix2x5) {
      return multiply((Matrix2x5) other);
    }
    return super.multiply(other);
  }


  /**
   * Scale this class.
   * @return The resulting matrix
   */
  @Override
  public Matrix5x2 multiply(double multiplicand) {
    return new Matrix5x2(
      this.r0c0 * multiplicand,
      this.r0c1 * multiplicand,
      this.r1c0 * multiplicand,
      this.r1c1 * multiplicand,
      this.r2c0 * multiplicand,
      this.r2c1 * multiplicand,
      this.r3c0 * multiplicand,
      this.r3c1 * multiplicand,
      this.r4c0 * multiplicand,
      this.r4c1 * multiplicand
    );
  }


  /**
   * Transpose the matrix.
   * @return The resulting matrix
   */
  public Matrix2x5 transpose() {
    return new Matrix2x5(
      this.r0c0,
      this.r1c0,
      this.r2c0,
      this.r3c0,
      this.r4c0,
      this.r0c1,
      this.r1c1,
      this.r2c1,
      this.r3c1,
      this.r4c1
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
      this.r1c0,
      this.r1c1,
      this.r2c0,
      this.r2c1,
      this.r3c0,
      this.r3c1,
      this.r4c0,
      this.r4c1
    };
  }





}