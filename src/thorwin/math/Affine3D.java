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

import thorwin.math.geo.AxisAngle;
import thorwin.math.geo.Euler;
import thorwin.math.geo.Quaternion;


/**
 * Affine matrix for transforming Vector3D instances.
 */
public final class Affine3D extends DefaultMatrix {

  /**
   * Identity affine matrix.
   */
  public static final Affine3D IDENTITY = new Affine3D(1,0,0,0, 0,1,0,0, 0,0,1,0);
  private final double mxx;
  private final double mxy;
  private final double mxz;
  private final double tx;

  private final double myx;
  private final double myy;
  private final double myz;
  private final double ty;

  private final double mzx;
  private final double mzy;
  private final double mzz;
  private final double tz;

  /**
   * Constructs an affine using four vectors for the getColumnDimension.
   *
   * @param mx first column
   * @param my second column
   * @param mz third column
   * @param t  fourth column
   */
  public Affine3D(Vector3D mx, Vector3D my, Vector3D mz, Vector3D t) {
    this(mx.getX(), my.getX(), mz.getX(), t.getX(),
         mx.getY(), my.getY(), mz.getY(), t.getY(),
         mx.getZ(), my.getZ(), mz.getZ(), t.getZ());
  }

  /**
   * Constructor
   *
   * @param mxx Row 0, column 0
   * @param mxy Row 0, column 1
   * @param mxz Row 0, column 2
   * @param tx  Row 0, column 3
   * @param myx Row 1, column 0
   * @param myy Row 1, column 1
   * @param myz Row 1, column 2
   * @param ty  Row 1, column 3
   * @param mzx Row 2, column 0
   * @param mzy Row 2, column 1
   * @param mzz Row 2, column 2
   * @param tz  Row 2, column 3
   */
  public Affine3D(double mxx,
                  double mxy,
                  double mxz,
                  double tx,
                  double myx,
                  double myy,
                  double myz,
                  double ty,
                  double mzx,
                  double mzy,
                  double mzz,
                  double tz) {
    this.mxx = mxx;
    this.mxy = mxy;
    this.mxz = mxz;
    this.tx = tx;
    this.myx = myx;
    this.myy = myy;
    this.myz = myz;
    this.ty = ty;
    this.mzx = mzx;
    this.mzy = mzy;
    this.mzz = mzz;
    this.tz = tz;
  }

  /**
   * Constructs a look-at toAffine3D. Note that for camera view you need to invert
   * this matrix.
   *
   * @param eye    position of the camera
   * @param target center to look at
   * @param eyeUp  vector indicating 'up'
   * @return new affine
   */
  public static Affine3D lookAt(Vector3D eye, Vector3D target, Vector3D eyeUp) {
    Vector3D forward = target.subtract(eye).normalize();
    Vector3D side = forward.cross(eyeUp).normalize();
    Vector3D up = side.cross(forward);

    return new Affine3D(
        side.getX(), up.getX(), -forward.getX(), eye.getX(),
        side.getY(), up.getY(), -forward.getY(), eye.getY(),
        side.getZ(), up.getZ(), -forward.getZ(), eye.getZ()
    );
  }

  /**
   * Creates a translation affine
   *
   * @param translation translation vector
   * @return new affine
   */
  public static Affine3D translation(Vector3D translation) {
    return translation(translation.getX(),
                       translation.getY(),
                       translation.getZ());
  }

  /**
   * Creates a translation affine.
   *
   * @param dx x-translation
   * @param dy y-translation
   * @param dz z-translation
   * @return new affine
   */
  public static Affine3D translation(double dx, double dy, double dz) {
    return new Affine3D(1, 0, 0, dx,
                        0, 1, 0, dy,
                        0, 0, 1, dz);
  }

  /**
   * Appends a quaternion rotation to this affine.
   *
   * @param quaternion normalized quaternion
   * @return resulting affine
   */
  public Affine3D rotate(Quaternion quaternion) {
    return append(valueOf(quaternion));
  }

  /**
   * Appends another affine to this affine.
   *
   * @param other affine to append
   * @return resulting affine
   */
  public Affine3D append(Affine3D other) {
    return other.multiply(this);
  }

  /**
   * Creates an affine from a normalized quaternion.
   *
   * @param quaternion normalized quaternion
   * @return constructed affine
   */
  public static Affine3D valueOf(Quaternion quaternion) {
    return quaternion.toAffine3D();
  }

  /**
   * Multiplies this affine with another
   *
   * @param other other affine
   * @return resulting affine
   */
  public Affine3D multiply(Affine3D other) {
    return new Affine3D(
        this.mxx * other.mxx +
            this.mxy * other.myx +
            this.mxz * other.mzx,
        this.mxx * other.mxy +
            this.mxy * other.myy +
            this.mxz * other.mzy,
        this.mxx * other.mxz +
            this.mxy * other.myz +
            this.mxz * other.mzz,
        this.mxx * other.tx +
            this.mxy * other.ty +
            this.mxz * other.tz +
            this.tx,
        this.myx * other.mxx +
            this.myy * other.myx +
            this.myz * other.mzx,
        this.myx * other.mxy +
            this.myy * other.myy +
            this.myz * other.mzy,
        this.myx * other.mxz +
            this.myy * other.myz +
            this.myz * other.mzz,
        this.myx * other.tx +
            this.myy * other.ty +
            this.myz * other.tz +
            this.ty,
        this.mzx * other.mxx +
            this.mzy * other.myx +
            this.mzz * other.mzx,
        this.mzx * other.mxy +
            this.mzy * other.myy +
            this.mzz * other.mzy,
        this.mzx * other.mxz +
            this.mzy * other.myz +
            this.mzz * other.mzz,
        this.mzx * other.tx +
            this.mzy * other.ty +
            this.mzz * other.tz +
            this.tz);
  }

  /**
   * Appends a euler rotation to this affine.
   *
   * @param euler euler angle
   * @return new affine
   */
  public Affine3D rotate(Euler euler) {
    return append(valueOf(euler));
  }

  /**
   * Creates a rotation matrix from an euler rotation.
   *
   * @param euler euler angles
   * @return constructed affine
   */
  public static Affine3D valueOf(Euler euler) {
    return euler.toAffine3D();
  }

  /**
   * Appends a rotation to this affine.
   *
   * @param axisAngle axis and angle
   * @return resulting affine
   */
  public Affine3D rotate(AxisAngle axisAngle) {
    return append(valueOf(axisAngle));
  }

  /**
   * Creates a affine rotation from the specified axis/angle rotation.
   *
   * @param axisAngle axis and angle rotation
   * @return constructed affine
   */
  public static Affine3D valueOf(AxisAngle axisAngle) {
    return axisAngle.toAffine3D();
  }

  /**
   * Append a scaling to this affine.
   *
   * @param sx x-scaling factor
   * @param sy y-scaling factor
   * @param sz z-scaling factor
   * @return resulting affine
   */
  public Affine3D scale(double sx, double sy, double sz) {
    return append(scaling(sx, sy, sz));
  }

  /**
   * Creates a scaling affine.
   *
   * @param sx x-scaling factor
   * @param sy y-scaling factor
   * @param sz z-scaling factor
   * @return constructed affine
   */
  public static Affine3D scaling(double sx, double sy, double sz) {
    return new Affine3D(sx, 0, 0, 0,
                        0, sy, 0, 0,
                        0, 0, sz, 0);
  }

  /**
   * Transform a vector.
   *
   * @param vector vector
   * @return transformed vector
   */
  public Vector3D multiply(Vector3D vector) {
    return multiply(vector.getX(), vector.getY(), vector.getZ());
  }

  /**
   * Transform a vector.
   *
   * @param x x-coordinate
   * @param y y-coordinate
   * @param z z-coordinate
   * @return the transformed vector
   */
  public Vector3D multiply(double x, double y, double z) {
    return new Vector3D(
                            this.mxx * x +
                            this.mxy * y +
                            this.mxz * z +
                            this.tx,
                            this.myx * x +
                            this.myy * y +
                            this.myz * z +
                            this.ty,
                            this.mzx * x +
                            this.mzy * y +
                            this.mzz * z +
                            this.tz);
  }

  /**
   * Append a translation to this affine.
   *
   * @param translation translation vector
   * @return resulting affine
   */
  public Affine3D translate(Vector3D translation) {
    return translate(translation.getX(),
                     translation.getY(),
                     translation.getZ());
  }

  /**
   * Append a translation to this affine.
   *
   * @param dx x-translationMatrix
   * @param dy y-translationMatrix
   * @param dz z-translationMatrix
   * @return resulting affine
   */
  public Affine3D translate(double dx, double dy, double dz) {
    return append(translation(dx, dy, dz));
  }

  /**
   * Prepend an affine.
   *
   * @param affine affine to prepend
   * @return resulting affine
   */
  public Affine3D prepend(Affine3D affine) {
    return multiply(affine);
  }


  @Override
  public int getColumnDimension() {
    return 4;
  }


  @Override
  public int getRowDimension() {
    return 4;
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
          return mxz;
        case 3:
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
          return myz;
        case 3:
          return ty;
        default:
          throw new IllegalArgumentException("invalid column: " + column);
      }
    } else if (row == 2) {
      switch (column) {
        case 0:
          return mzx;
        case 1:
          return mzy;
        case 2:
          return mzz;
        case 3:
          return tz;
        default:
          throw new IllegalArgumentException("invalid column: " + column);
      }
    } else if (row == 3) {
      switch (column) {
        case 0:
        case 1:
        case 2:
          return 0.0;
        case 3:
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
  public Optional<Affine3D> invert() {

    double tmp0 = mxx * myy - myx * mxy;
    double tmp1 = mxx * myz - myx * mxz;
    double tmp2 = mxx * ty - myx * tx;
    double tmp3 = mxy * myz - myy * mxz;
    double tmp4 = mxy * ty - myy * tx;
    double tmp5 = mxz * ty - myz * tx;

    double den   = tmp0 * mzz - tmp1 * mzy + tmp3 * mzx;

    if (den == 0.0) return Optional.empty();

    double det = 1.0 / den;

    double mxx0 = ( myy * mzz - myz * mzy) * det;
    double mxy0 = (-mxy * mzz + mxz * mzy) * det;
    double mxz0 = (tmp3) * det;
    double tx0 = (-mzy * tmp5 + mzz * tmp4 - tz * tmp3) * det;

    double myx0 = (-myx * mzz + myz * mzx) * det;
    double myy0 = ( mxx * mzz - mxz * mzx) * det;
    double myz0 = -tmp1 * det;
    double ty0 = ( mzx * tmp5 - mzz * tmp2 + tz * tmp1) * det;

    double mzx0 = ( myx * mzy - myy * mzx) * det;
    double mzy0 = (-mxx * mzy + mxy * mzx) * det;
    double mzz0 = ( tmp0) * det;
    double tz0 = (-mzx * tmp4 + mzy * tmp2 - tz * tmp0) * det;

    return Optional.of(new Affine3D(mxx0,
                                    mxy0,
                                    mxz0,
                                    tx0,
                                    myx0,
                                    myy0,
                                    myz0,
                                    ty0,
                                    mzx0,
                                    mzy0,
                                    mzz0,
                                    tz0));
  }


  @Override
  public Matrix multiply(Matrix other) {
    if (other instanceof Affine3D) {
      return multiply((Affine3D) other);
    } else {
      return super.multiply(other);
    }
  }


  /**
   * Returns the significant column data as a 3D vector, excluding the last row
   * as this data is constant in an affine toAffine3D.
   *
   * @param columnIndex column index
   * @return specified column as a 3D vector
   */
  public Vector3D columnData(int columnIndex) {
    switch (columnIndex) {
      case 0:
        return new Vector3D(mxx, myx, mzx);
      case 1:
        return new Vector3D(mxy, myy, mzy);
      case 2:
        return new Vector3D(mxz, myz, mzz);
      case 3:
        return new Vector3D(tx, ty, tz);
      default:
        throw new IllegalArgumentException("invalid columnIndex: " + columnIndex);
    }
  }

}
