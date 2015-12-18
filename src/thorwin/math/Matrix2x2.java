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

import java.util.Optional;

/**
 * 2x2 matrix.
 */
public final class Matrix2x2 extends DefaultMatrix {

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
   * Construct a new Matrix2x2 instance.
   * @param r0c0 Value for row 0, column 0
   * @param r0c1 Value for row 0, column 1
   * @param r1c0 Value for row 1, column 0
   * @param r1c1 Value for row 1, column 1
   */
  public Matrix2x2(
    double r0c0,
    double r0c1,
    double r1c0,
    double r1c1
) {
  super();
    this.r0c0 = r0c0;
    this.r0c1 = r0c1;
    this.r1c0 = r1c0;
    this.r1c1 = r1c1;
  }




  /**
   * Construct a matrix from its column vectors.
   * @param column0 Column vector for column 0
   * @param column1 Column vector for column 1
   */
  public Matrix2x2(
    Vector2D column0,
    Vector2D column1
  ) {
    super();
    this.r0c0 = column0.getX();
    this.r1c0 = column0.getY();
    this.r0c1 = column1.getX();
    this.r1c1 = column1.getY();
  }



  /**
   * @return The number of rows in this matrix, which is 2.
   */
  @Override
  public int getRowDimension() {
    return 2;
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
          case 1: return this.r0c1;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 1:
        switch (column) {
          case 0: return this.r1c0;
          case 1: return this.r1c1;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      default: throw new IllegalArgumentException("No such row: " + row);
    }
  }



  /**
   * Add a Matrix2x2 this this Matrix2x2.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix2x2 add(Matrix2x2 other) {
    return new Matrix2x2(
      this.r0c0 + other.r0c0,
      this.r0c1 + other.r0c1,
      this.r1c0 + other.r1c0,
      this.r1c1 + other.r1c1
    );
  }

  /**
   * Overridden to optimize addition if the other matrix
   * if of this class.
   */
  @Override
  public Matrix add(Matrix other) {
    if (other instanceof Matrix2x2) {
      return add((Matrix2x2) other);
    } else {
      return super.add(other);
    }
  }


  /**
   * Add a Matrix2x2 this this Matrix2x2.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix2x2 subtract(Matrix2x2 other) {
    return new Matrix2x2(
      this.r0c0 - other.r0c0,
      this.r0c1 - other.r0c1,
      this.r1c0 - other.r1c0,
      this.r1c1 - other.r1c1
    );
  }


  /**
   * Overridden to optimize addition if the other matrix
   * if of this class.
   */
  @Override
  public Matrix subtract(Matrix other) {
    if (other instanceof Matrix2x2) {
      return subtract((Matrix2x2) other);
    } else {
      return super.subtract(other);
    }
  }



  /**
   * Multiply this Matrix2x2 with a Matrix2x1.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix2x1 multiply(Matrix2x1 other) {
    return new Matrix2x1(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0
    );
  }


  /**
   * Multiply this Matrix2x2 with a Matrix2x2.
   * @param other The other matrix
   * @return The resulting matrix
   */
  public Matrix2x2 multiply(Matrix2x2 other) {
    return new Matrix2x2(
      this.r0c0 * other.r0c0 +
      this.r0c1 * other.r1c0,
      this.r0c0 * other.r0c1 +
      this.r0c1 * other.r1c1,
      this.r1c0 * other.r0c0 +
      this.r1c1 * other.r1c0,
      this.r1c0 * other.r0c1 +
      this.r1c1 * other.r1c1
    );
  }


  /**
   * Multiply this Matrix2x2 with a Matrix2x3.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix2x3 multiply(Matrix2x3 other) {
    return new Matrix2x3(
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
      this.r1c1 * other.r1c2
    );
  }


  /**
   * Multiply this Matrix2x2 with a Matrix2x4.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix2x4 multiply(Matrix2x4 other) {
    return new Matrix2x4(
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
      this.r1c1 * other.r1c3
    );
  }


  /**
   * Multiply this Matrix2x2 with a Matrix2x5.
   * @param other The other matrix
   * @return The resulting matrix
   */
  Matrix2x5 multiply(Matrix2x5 other) {
    return new Matrix2x5(
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
      this.r1c1 * other.r1c4
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
  public Matrix2x2 multiply(double multiplicand) {
    return new Matrix2x2(
      this.r0c0 * multiplicand,
      this.r0c1 * multiplicand,
      this.r1c0 * multiplicand,
      this.r1c1 * multiplicand
    );
  }


  /**
   * Transpose the matrix.
   * @return The resulting matrix
   */
  public Matrix2x2 transpose() {
    return new Matrix2x2(
      this.r0c0,
      this.r1c0,
      this.r0c1,
      this.r1c1
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
      this.r1c1
    };
  }




  /**
   * Transform the given 2-dimensional vector by this matrix.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   * @return Resulting vector
   */
  public Vector2D multiply(double x, double y) {
    return new Vector2D(
        x * r0c0 + y * r0c1,
        x * r1c0 + y * r1c1
    );
  }

  /**
   * Transform the given vector by this matrix.
   *
   * @param vector The vector to transform.
   * @return Resulting vector
   */
  public Vector2D multiply(Vector2D vector) {
    return multiply(vector.getX(), vector.getY());
  }



  /**
   * Overridden to optimize Vector2D multiplications.
   * @param vector The vector to multiply
   * @return The resulting vector
   */
  public Vector multiply(Vector vector) {
    if (vector instanceof Vector2D) {
      return multiply((Vector2D) vector);
    } else {
      return super.multiply(vector);
    }
  }


  /**
   * Calculate the inverse of this matrix
   *
   * @return The invert matrix
   */
  @Override
  public Optional<Matrix2x2> invert() {
    double d = 1.0 / (r0c0 * r1c1 - r0c1 * r1c0);

    if (d == 0) {
        return Optional.empty();
    }
    else {
        return
            Optional.of(
                new Matrix2x2(
                     d * r1c1,
                    -d * r0c1,
                    -d * r1c0,
                     d * r0c0
                ));
    }
  }



}