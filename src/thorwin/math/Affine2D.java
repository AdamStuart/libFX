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

import java.util.Optional;


/**
 * Affine matrix for transforming Vector2D instances.
 */
public final class Affine2D extends DefaultMatrix {

  /**
   * Identity affine matrix.
   */
  public static final Affine2D IDENTITY = new Affine2D(1,0,0, 0,1,0);

  private final double mxx;
  private final double mxy;
  private final double tx;

  private final double myx;
  private final double myy;
  private final double ty;


  /**
   * Constructor.
   *
   * @param mxx Row 0, column 0
   * @param mxy Row 0, column 1
   * @param tx  Row 0, column 2
   * @param myx Row 1, column 0
   * @param myy Row 1, column 1
   * @param ty  Row 1, column 2
   */
  public Affine2D(double mxx,
                  double mxy,
                  double tx,
                  double myx,
                  double myy,
                  double ty) {
    super();
    this.mxx = mxx;
    this.mxy = mxy;
    this.tx = tx;
    this.myx = myx;
    this.myy = myy;
    this.ty = ty;
  }

  /**
   * Prepends an affine
   *
   * @param other affine to prepend
   * @return resulting affine
   */
  public Affine2D prepend(Affine2D other) {
    return multiply(other);
  }

  /**
   * Multiplies this affine with another.
   *
   * @param other another affine
   * @return resulting affine
   */
  public Affine2D multiply(Affine2D other) {
    return new Affine2D(
        this.mxx * other.mxx +
            this.mxy * other.myx,
        this.mxx * other.mxy +
            this.mxy * other.myy,
        this.mxx * other.tx +
            this.mxy * other.ty +
            this.tx,
        this.myx * other.mxx +
            this.myy * other.myx,
        this.myx * other.mxy +
            this.myy * other.myy,
        this.myx * other.tx +
            this.myy * other.ty +
            this.ty);
  }

  /**
   * Appends a rotation to this affine.
   *
   * @param angle angle (rad)
   * @return resulting affine
   */
  public Affine2D rotate(double angle) {
    return append(rotation(angle));
  }

  /**
   * Appends another affine
   *
   * @param other affine to append
   * @return resulting affine
   */
  public Affine2D append(Affine2D other) {
    return other.multiply(this);
  }

  /**
   * Creates a rotation affine.
   *
   * @param angle angle (rad)
   * @return new affine
   */
  public static Affine2D rotation(double angle) {
    double cosA = java.lang.Math.cos(angle);
    double sinA = java.lang.Math.sin(angle);

    return new Affine2D(cosA, -sinA, 0, sinA, cosA, 0);
  }

  /**
   * Appends a scaling affine.
   *
   * @param sx x-scale
   * @param sy y-scale
   * @return resulting affine
   */
  public Affine2D scale(double sx, double sy) {
    return append(scaling(sx, sy));
  }

  /**
   * Creates a scaling affine.
   *
   * @param sx x-scale
   * @param sy y-scale
   * @return new affine
   */
  public static Affine2D scaling(double sx, double sy) {
    return new Affine2D(sx, 0, 0, 0, sy, 0);
  }

  /**
   * Transform a vector.
   *
   * @param vector vector
   * @return transformed vector
   */
  public Vector2D multiply(Vector2D vector) {
    return multiply(vector.getX(), vector.getY());
  }

  /**
   * Transform a vector.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   * @return transformed vector
   */
  public Vector2D multiply(double x, double y) {
    Matrix v = Matrix.columnPacked(3, x, y, 1);
    Matrix m = this.multiply(v);

    return new Vector2D(m.get(0, 0), m.get(1, 0));
  }

  /**
   * Append a translation.
   *
   * @param dx x-rotate
   * @param dy y-rotate
   * @return resulting affine
   */
  public Affine2D translate(double dx, double dy) {
    return append(translation(dx, dy));
  }

  /**
   * Creates a translation affine.
   *
   * @param dx x-rotate
   * @param dy y-rotate
   * @return new affine
   */
  public static Affine2D translation(double dx, double dy) {
    return new Affine2D(1, 0, dx, 0, 1, dy);
  }


  @Override
  public int getRowDimension() {
    return 3;
  }


  @Override
  public int getColumnDimension() {
    return 3;
  }


  @Override
  public double get(int row, int column) {
    if (row == 0) {
      switch (column) {
        case 0:
          return mxx;
        case 1:
          return mxy;
        case 2:
          return tx;
        default:
          throw new IllegalArgumentException("invalid column: " + column);
      }
    } else if (row == 1) {
      switch (column) {
        case 0:
          return myx;
        case 1:
          return myy;
        case 2:
          return ty;
        default:
          throw new IllegalArgumentException("invalid column: " + column);
      }
    } else if (row == 2) {
      switch (column) {
        case 0:
        case 1:
          return 0.0;
        case 2:
          return 1.0;
        default:
          throw new IllegalArgumentException("invalid column: " + column);
      }
    } else {
      throw new IllegalArgumentException("invalid row: " + row);
    }
  }


  @SuppressWarnings("unchecked")
  @Override
  public Optional<Affine2D> invert() {
    double t4  = this.mxx * this.myy;
    double t6  = this.mxx * this.ty;
    double t8  = this.mxy * this.myx;
    double t10 = this.tx * this.myx;

    double det = (t4 - t8);

    if (det != 0.0) {
      double t17 = 1.0 / det;
      double mxx = this.myy * t17;
      double mxy = -this.mxy * t17;
      double tx = (this.mxy * this.ty - this.tx * this.myy) * t17;
      double myx = -this.myx * t17;
      double myy = this.mxx * t17;
      double ty = -(t6 - t10) * t17;

      return Optional.of(new Affine2D(mxx,
                                       mxy,
                                       tx,
                                       myx,
                                       myy,
                                       ty));
    }
    else {
      return Optional.empty();
    }
  }


  @Override
  public Matrix multiply(Matrix other) {
    if (other instanceof Affine2D) {
      return multiply((Affine2D) other);
    } else {
      return super.multiply(other);
    }
  }


  /**
   * Returns the significant entries for the specified column as a 2D vector.
   * The last row of the matrix is excluded as it is constant in the affine
   * matrix.
   *
   * @param column column index
   * @return The specified column as a 2D vector
   */
  public Vector2D columnData(int column) {
    switch (column) {
      case 0:
        return new Vector2D(mxx, myx);
      case 1:
        return new Vector2D(mxy, myy);
      case 2:
        return new Vector2D(tx, ty);
      default:
        throw new IllegalArgumentException("invalid column: " + column);
    }
  }

}
