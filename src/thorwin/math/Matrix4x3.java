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
 * 4x3 matrix.
 */
final class Matrix4x3 extends DefaultMatrix {

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
   * Construct a new Matrix4x3 instance.
   * @param r0c0 Value for row 0, column 0
   * @param r0c1 Value for row 0, column 1
   * @param r0c2 Value for row 0, column 2
   * @param r1c0 Value for row 1, column 0
   * @param r1c1 Value for row 1, column 1
   * @param r1c2 Value for row 1, column 2
   * @param r2c0 Value for row 2, column 0
   * @param r2c1 Value for row 2, column 1
   * @param r2c2 Value for row 2, column 2
   * @param r3c0 Value for row 3, column 0
   * @param r3c1 Value for row 3, column 1
   * @param r3c2 Value for row 3, column 2
   */
  public Matrix4x3(
    double r0c0,
    double r0c1,
    double r0c2,
    double r1c0,
    double r1c1,
    double r1c2,
    double r2c0,
    double r2c1,
    double r2c2,
    double r3c0,
    double r3c1,
    double r3c2
) {
  super();
    this.r0c0 = r0c0;
    this.r0c1 = r0c1;
    this.r0c2 = r0c2;
    this.r1c0 = r1c0;
    this.r1c1 = r1c1;
    this.r1c2 = r1c2;
    this.r2c0 = r2c0;
    this.r2c1 = r2c1;
    this.r2c2 = r2c2;
    this.r3c0 = r3c0;
    this.r3c1 = r3c1;
    this.r3c2 = r3c2;
  }







  /**
   * @return The number of rows in this matrix, which is 4.
   */
  @Override
  public int getRowDimension() {
    return 4;
  }


  /**
   * @return The number of columns in this matrix, which is 3.
   */
  @Override
  public int getColumnDimension() {
    return 3;
  }




  /**
   * Get the row as a vector.
   * @return The vector
   */
  @Override
  public Vector3D row(int index) {
    return new Vector3D(
      get(index, 0),
      get(index, 1),
      get(index, 2)
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
          case 2: return this.r0c2;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 1:
        switch (column) {
          case 0: return this.r1c0;
          case 1: return this.r1c1;
          case 2: return this.r1c2;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 2:
        switch (column) {
          case 0: return this.r2c0;
          case 1: return this.r2c1;
          case 2: return this.r2c2;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 3:
        switch (column) {
          case 0: return this.r3c0;
          case 1: return this.r3c1;
          case 2: return this.r3c2;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      default: throw new IllegalArgumentException("No such row: " + row);
    }
  }



  /**
   * Add a Matrix4x3 this this Matrix4x3.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix4x3 add(Matrix4x3 other) {
    return new Matrix4x3(
      this.r0c0 + other.r0c0,
      this.r0c1 + other.r0c1,
      this.r0c2 + other.r0c2,
      this.r1c0 + other.r1c0,
      this.r1c1 + other.r1c1,
      this.r1c2 + other.r1c2,
      this.r2c0 + other.r2c0,
      this.r2c1 + other.r2c1,
      this.r2c2 + other.r2c2,
      this.r3c0 + other.r3c0,
      this.r3c1 + other.r3c1,
      this.r3c2 + other.r3c2
    );
  }

  /**
   * Overridden to optimize addition if the other matrix
   * if of this class.
   */
  @Override
  public Matrix add(Matrix other) {
    if (other instanceof Matrix4x3) {
      return add((Matrix4x3) other);
    } else {
      return super.add(other);
    }
  }


  /**
   * Add a Matrix4x3 this this Matrix4x3.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix4x3 subtract(Matrix4x3 other) {
    return new Matrix4x3(
      this.r0c0 - other.r0c0,
      this.r0c1 - other.r0c1,
      this.r0c2 - other.r0c2,
      this.r1c0 - other.r1c0,
      this.r1c1 - other.r1c1,
      this.r1c2 - other.r1c2,
      this.r2c0 - other.r2c0,
      this.r2c1 - other.r2c1,
      this.r2c2 - other.r2c2,
      this.r3c0 - other.r3c0,
      this.r3c1 - other.r3c1,
      this.r3c2 - other.r3c2
    );
  }


  /**
   * Overridden to optimize addition if the other matrix
   * if of this class.
   */
  @Override
  public Matrix subtract(Matrix other) {
    if (other instanceof Matrix4x3) {
      return subtract((Matrix4x3) other);
    } else {
      return super.subtract(other);
    }
  }



  /**
   * Multiply this Matrix4x3 with a Matrix3x1.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix4x1 multiply(Matrix3x1 other) {
    return new Matrix4x1(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0 +
      this.r1c2 * other.r2c0,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0 +
      this.r2c2 * other.r2c0,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0 +
      this.r3c2 * other.r2c0
    );
  }


  /**
   * Multiply this Matrix4x3 with a Matrix3x2.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix4x2 multiply(Matrix3x2 other) {
    return new Matrix4x2(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1 +
      this.r0c2 * other.r2c1,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0 +
      this.r1c2 * other.r2c0,
      this.r1c0 * other.r0c1 +
      this.r1c1 * other.r1c1 +
      this.r1c2 * other.r2c1,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0 +
      this.r2c2 * other.r2c0,
      this.r2c0 * other.r0c1 +
      this.r2c1 * other.r1c1 +
      this.r2c2 * other.r2c1,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0 +
      this.r3c2 * other.r2c0,
      this.r3c0 * other.r0c1 +
      this.r3c1 * other.r1c1 +
      this.r3c2 * other.r2c1
    );
  }


  /**
   * Multiply this Matrix4x3 with a Matrix3x3.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix4x3 multiply(Matrix3x3 other) {
    return new Matrix4x3(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1 +
      this.r0c2 * other.r2c1,
      this.r0c0 * other.r0c2 +
      this.r0c1 * other.r1c2 +
      this.r0c2 * other.r2c2,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0 +
      this.r1c2 * other.r2c0,
      this.r1c0 * other.r0c1 +
      this.r1c1 * other.r1c1 +
      this.r1c2 * other.r2c1,
      this.r1c0 * other.r0c2 +
      this.r1c1 * other.r1c2 +
      this.r1c2 * other.r2c2,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0 +
      this.r2c2 * other.r2c0,
      this.r2c0 * other.r0c1 +
      this.r2c1 * other.r1c1 +
      this.r2c2 * other.r2c1,
      this.r2c0 * other.r0c2 +
      this.r2c1 * other.r1c2 +
      this.r2c2 * other.r2c2,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0 +
      this.r3c2 * other.r2c0,
      this.r3c0 * other.r0c1 +
      this.r3c1 * other.r1c1 +
      this.r3c2 * other.r2c1,
      this.r3c0 * other.r0c2 +
      this.r3c1 * other.r1c2 +
      this.r3c2 * other.r2c2
    );
  }


  /**
   * Multiply this Matrix4x3 with a Matrix3x4.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix4x4 multiply(Matrix3x4 other) {
    return new Matrix4x4(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1 +
      this.r0c2 * other.r2c1,
      this.r0c0 * other.r0c2 +
      this.r0c1 * other.r1c2 +
      this.r0c2 * other.r2c2,
      this.r0c0 * other.r0c3 +
      this.r0c1 * other.r1c3 +
      this.r0c2 * other.r2c3,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0 +
      this.r1c2 * other.r2c0,
      this.r1c0 * other.r0c1 +
      this.r1c1 * other.r1c1 +
      this.r1c2 * other.r2c1,
      this.r1c0 * other.r0c2 +
      this.r1c1 * other.r1c2 +
      this.r1c2 * other.r2c2,
      this.r1c0 * other.r0c3 +
      this.r1c1 * other.r1c3 +
      this.r1c2 * other.r2c3,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0 +
      this.r2c2 * other.r2c0,
      this.r2c0 * other.r0c1 +
      this.r2c1 * other.r1c1 +
      this.r2c2 * other.r2c1,
      this.r2c0 * other.r0c2 +
      this.r2c1 * other.r1c2 +
      this.r2c2 * other.r2c2,
      this.r2c0 * other.r0c3 +
      this.r2c1 * other.r1c3 +
      this.r2c2 * other.r2c3,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0 +
      this.r3c2 * other.r2c0,
      this.r3c0 * other.r0c1 +
      this.r3c1 * other.r1c1 +
      this.r3c2 * other.r2c1,
      this.r3c0 * other.r0c2 +
      this.r3c1 * other.r1c2 +
      this.r3c2 * other.r2c2,
      this.r3c0 * other.r0c3 +
      this.r3c1 * other.r1c3 +
      this.r3c2 * other.r2c3
    );
  }


  /**
   * Multiply this Matrix4x3 with a Matrix3x5.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix4x5 multiply(Matrix3x5 other) {
    return new Matrix4x5(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0 +
      this.r0c2 * other.r2c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1 +
      this.r0c2 * other.r2c1,
      this.r0c0 * other.r0c2 +
      this.r0c1 * other.r1c2 +
      this.r0c2 * other.r2c2,
      this.r0c0 * other.r0c3 +
      this.r0c1 * other.r1c3 +
      this.r0c2 * other.r2c3,
      this.r0c0 * other.r0c4 +
      this.r0c1 * other.r1c4 +
      this.r0c2 * other.r2c4,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0 +
      this.r1c2 * other.r2c0,
      this.r1c0 * other.r0c1 +
      this.r1c1 * other.r1c1 +
      this.r1c2 * other.r2c1,
      this.r1c0 * other.r0c2 +
      this.r1c1 * other.r1c2 +
      this.r1c2 * other.r2c2,
      this.r1c0 * other.r0c3 +
      this.r1c1 * other.r1c3 +
      this.r1c2 * other.r2c3,
      this.r1c0 * other.r0c4 +
      this.r1c1 * other.r1c4 +
      this.r1c2 * other.r2c4,
      this.r2c0 * other.r0c0 +
      this.r2c1 * other.r1c0 +
      this.r2c2 * other.r2c0,
      this.r2c0 * other.r0c1 +
      this.r2c1 * other.r1c1 +
      this.r2c2 * other.r2c1,
      this.r2c0 * other.r0c2 +
      this.r2c1 * other.r1c2 +
      this.r2c2 * other.r2c2,
      this.r2c0 * other.r0c3 +
      this.r2c1 * other.r1c3 +
      this.r2c2 * other.r2c3,
      this.r2c0 * other.r0c4 +
      this.r2c1 * other.r1c4 +
      this.r2c2 * other.r2c4,
      this.r3c0 * other.r0c0 +
      this.r3c1 * other.r1c0 +
      this.r3c2 * other.r2c0,
      this.r3c0 * other.r0c1 +
      this.r3c1 * other.r1c1 +
      this.r3c2 * other.r2c1,
      this.r3c0 * other.r0c2 +
      this.r3c1 * other.r1c2 +
      this.r3c2 * other.r2c2,
      this.r3c0 * other.r0c3 +
      this.r3c1 * other.r1c3 +
      this.r3c2 * other.r2c3,
      this.r3c0 * other.r0c4 +
      this.r3c1 * other.r1c4 +
      this.r3c2 * other.r2c4
    );
  }



  @Override
  public Matrix multiply(Matrix other) {
    if(other instanceof Matrix3x1) {
      return multiply((Matrix3x1) other);
    }
    if(other instanceof Matrix3x2) {
      return multiply((Matrix3x2) other);
    }
    if(other instanceof Matrix3x3) {
      return multiply((Matrix3x3) other);
    }
    if(other instanceof Matrix3x4) {
      return multiply((Matrix3x4) other);
    }
    if(other instanceof Matrix3x5) {
      return multiply((Matrix3x5) other);
    }
    return super.multiply(other);
  }


  /**
   * Scale this class.
   * @return The resulting matrix
   */
  @Override
  public Matrix4x3 multiply(double multiplicand) {
    return new Matrix4x3(
      this.r0c0 * multiplicand,
      this.r0c1 * multiplicand,
      this.r0c2 * multiplicand,
      this.r1c0 * multiplicand,
      this.r1c1 * multiplicand,
      this.r1c2 * multiplicand,
      this.r2c0 * multiplicand,
      this.r2c1 * multiplicand,
      this.r2c2 * multiplicand,
      this.r3c0 * multiplicand,
      this.r3c1 * multiplicand,
      this.r3c2 * multiplicand
    );
  }


  /**
   * Transpose the matrix.
   * @return The resulting matrix
   */
  public Matrix3x4 transpose() {
    return new Matrix3x4(
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
      this.r3c2
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
      this.r1c0,
      this.r1c1,
      this.r1c2,
      this.r2c0,
      this.r2c1,
      this.r2c2,
      this.r3c0,
      this.r3c1,
      this.r3c2
    };
  }





}