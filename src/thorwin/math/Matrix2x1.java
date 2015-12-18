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
 * 2x1 matrix.
 */
final class Matrix2x1 extends DefaultMatrix {

  /**
   * Value of row 0, column 0.
   */
  final double r0c0;

  /**
   * Value of row 1, column 0.
   */
  final double r1c0;




  /**
   * Construct a new Matrix2x1 instance.
   * @param r0c0 Value for row 0, column 0
   * @param r1c0 Value for row 1, column 0
   */
  public Matrix2x1(
    double r0c0,
    double r1c0
) {
  super();
    this.r0c0 = r0c0;
    this.r1c0 = r1c0;
  }




  /**
   * Construct a matrix from its column vectors.
   * @param column0 Column vector for column 0
   */
  public Matrix2x1(
    Vector2D column0
  ) {
    super();
    this.r0c0 = column0.getX();
    this.r1c0 = column0.getY();
  }



  /**
   * @return The number of rows in this matrix, which is 2.
   */
  @Override
  public int getRowDimension() {
    return 2;
  }


  /**
   * @return The number of columns in this matrix, which is 1.
   */
  @Override
  public int getColumnDimension() {
    return 1;
  }




  /**
   * Get the column as a vector.
   * @return The vector
   */
  @Override
  public Vector2D column(int index) {
    return new Vector2D(
      get(0, index),
      get(1, index)
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
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 1:
        switch (column) {
          case 0: return this.r1c0;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      default: throw new IllegalArgumentException("No such row: " + row);
    }
  }



  /**
   * Add a Matrix2x1 this this Matrix2x1.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix2x1 add(Matrix2x1 other) {
    return new Matrix2x1(
      this.r0c0 + other.r0c0,
      this.r1c0 + other.r1c0
    );
  }

  /**
   * Overridden to optimize addition if the other matrix
   * if of this class.
   */
  @Override
  public Matrix add(Matrix other) {
    if (other instanceof Matrix2x1) {
      return add((Matrix2x1) other);
    } else {
      return super.add(other);
    }
  }


  /**
   * Add a Matrix2x1 this this Matrix2x1.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix2x1 subtract(Matrix2x1 other) {
    return new Matrix2x1(
      this.r0c0 - other.r0c0,
      this.r1c0 - other.r1c0
    );
  }


  /**
   * Overridden to optimize addition if the other matrix
   * if of this class.
   */
  @Override
  public Matrix subtract(Matrix other) {
    if (other instanceof Matrix2x1) {
      return subtract((Matrix2x1) other);
    } else {
      return super.subtract(other);
    }
  }



  /**
   * Multiply this Matrix2x1 with a Matrix1x1.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix2x1 multiply(Matrix1x1 other) {
    return new Matrix2x1(
      this.r0c0 * other.r0c0,
      this.r1c0 * other.r0c0
    );
  }


  /**
   * Multiply this Matrix2x1 with a Matrix1x2.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix2x2 multiply(Matrix1x2 other) {
    return new Matrix2x2(
      this.r0c0 * other.r0c0,
      this.r0c0 * other.r0c1,
      this.r1c0 * other.r0c0,
      this.r1c0 * other.r0c1
    );
  }


  /**
   * Multiply this Matrix2x1 with a Matrix1x3.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix2x3 multiply(Matrix1x3 other) {
    return new Matrix2x3(
      this.r0c0 * other.r0c0,
      this.r0c0 * other.r0c1,
      this.r0c0 * other.r0c2,
      this.r1c0 * other.r0c0,
      this.r1c0 * other.r0c1,
      this.r1c0 * other.r0c2
    );
  }


  /**
   * Multiply this Matrix2x1 with a Matrix1x4.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix2x4 multiply(Matrix1x4 other) {
    return new Matrix2x4(
      this.r0c0 * other.r0c0,
      this.r0c0 * other.r0c1,
      this.r0c0 * other.r0c2,
      this.r0c0 * other.r0c3,
      this.r1c0 * other.r0c0,
      this.r1c0 * other.r0c1,
      this.r1c0 * other.r0c2,
      this.r1c0 * other.r0c3
    );
  }


  /**
   * Multiply this Matrix2x1 with a Matrix1x5.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix2x5 multiply(Matrix1x5 other) {
    return new Matrix2x5(
      this.r0c0 * other.r0c0,
      this.r0c0 * other.r0c1,
      this.r0c0 * other.r0c2,
      this.r0c0 * other.r0c3,
      this.r0c0 * other.r0c4,
      this.r1c0 * other.r0c0,
      this.r1c0 * other.r0c1,
      this.r1c0 * other.r0c2,
      this.r1c0 * other.r0c3,
      this.r1c0 * other.r0c4
    );
  }



  @Override
  public Matrix multiply(Matrix other) {
    if(other instanceof Matrix1x1) {
      return multiply((Matrix1x1) other);
    }
    if(other instanceof Matrix1x2) {
      return multiply((Matrix1x2) other);
    }
    if(other instanceof Matrix1x3) {
      return multiply((Matrix1x3) other);
    }
    if(other instanceof Matrix1x4) {
      return multiply((Matrix1x4) other);
    }
    if(other instanceof Matrix1x5) {
      return multiply((Matrix1x5) other);
    }
    return super.multiply(other);
  }


  /**
   * Scale this class.
   * @return The resulting matrix
   */
  @Override
  public Matrix2x1 multiply(double multiplicand) {
    return new Matrix2x1(
      this.r0c0 * multiplicand,
      this.r1c0 * multiplicand
    );
  }


  /**
   * Transpose the matrix.
   * @return The resulting matrix
   */
  public Matrix1x2 transpose() {
    return new Matrix1x2(
      this.r0c0,
      this.r1c0
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
      this.r1c0
    };
  }





}