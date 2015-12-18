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

package thorwin.math.geo;

import thorwin.math.Vector3D;

/**
 * A cubic B&eacute;zier curve in 3D space.
 * @see <a href="https://en.wikipedia.org/wiki/B%C3%A9zier_curve">Wikipedia on B&eacute;zier Curves</a>
 */
public final class BezierCurve3D implements java.io.Serializable  {

  /**
   * Control point p0
   */
  private final Vector3D p0;

  /**
   * Control point p1
   */
  private final Vector3D p1;

  /**
   * Control point p2
   */
  private final Vector3D p2;

  /**
   * Control point p3
   */
  private final Vector3D p3;

  /**
   * Constructs a cubic bezier curve.
   *
   * @param p0 control point #0
   * @param p1 control point #1
   * @param p2 control point #2
   * @param p3 control point #3
   */
  public BezierCurve3D(Vector3D p0, Vector3D p1, Vector3D p2, Vector3D p3) {
    super();

    if (p0 == null || p1 == null || p2 == null || p3 == null) {
      throw new IllegalArgumentException("Control point should not be null");
    }

    this.p0 = p0;
    this.p1 = p1;
    this.p2 = p2;
    this.p3 = p3;
  }

  /**
   * Calculates the point using a cubic Bezier curve.
   *
   * @param t  time in range 0.0 - 1.0
   * @param p0 control point #0
   * @param p1 control point #1
   * @param p2 control point #2
   * @param p3 control point #3
   * @return the calculated position
   */
  private static Vector3D calculate(double t,
                                    Vector3D p0,
                                    Vector3D p1,
                                    Vector3D p2,
                                    Vector3D p3) {

    double u = 1.0 - t;
    double u2 = u * u;
    double u3 = u2 * u;
    double t2 = t * t;
    double t3 = t2 * t;

    Vector3D b0 = p0.multiply(u3);
    Vector3D b1 = p1.multiply(3.0 * u2 * t);
    Vector3D b2 = p2.multiply(3.0 * u * t2);
    Vector3D b3 = p3.multiply(t3);

    return b0.add(b1).add(b2).add(b3);
  }

  /**
   * Returns control point 0.
   * @return control point #0
   */
  public Vector3D getP0() {
    return p0;
  }

  /**
   * Returns control point 1.
   * @return control point #1
   */
  public Vector3D getP1() {
    return p1;
  }

  /**
   * Returns control point 2.
   * @return control point #2
   */
  public Vector3D getP2() {
    return p2;
  }

  /**
   * Returns control point 3.
   * @return control point #3
   */
  public Vector3D getP3() {
    return p3;
  }

  /**
   * Flattens the curve
   *
   * @param threshold maximum deviation of the center of the line from the
   *                  curve
   * @return vectors compromising the flattened curve
   */
  public Vector3D[] flatten(double threshold) {
    return flatten(threshold * threshold, 0.0, 1.0);
  }

  /**
   * Recursively flattens the curve
   *
   * @param thresholdSquared threshold (squared for performance reasons)
   * @param t0               minimum time
   * @param t1               maximum time
   * @return flattened curve vectors
   */
  private Vector3D[] flatten(double thresholdSquared, double t0, double t1) {
    double tMid = (t0 + t1) * 0.5;
    Vector3D pMid = calculate(tMid);
    Vector3D p0 = calculate(t0);
    Vector3D p1 = calculate(t1);

    if (p0.mid(p1).subtract(pMid).lengthSquared() < thresholdSquared) {
      return new Vector3D[]{p0, pMid, p1};
    }

    // recurse into left and right parts
    Vector3D[] left = flatten(thresholdSquared, t0, tMid);
    Vector3D[] right = flatten(thresholdSquared, tMid, t1);

    // construct resulting array
    Vector3D[] points = new Vector3D[left.length + right.length - 1];

    // copy left, mid and right points
    System.arraycopy(left, 0, points, 0, left.length);
    System.arraycopy(right, 0, points, left.length - 1, right.length);

    return points;
  }

  /**
   * Calculates the position at a specified time.
   *
   * @param time time in the range 0.0 - 1.0
   * @return calculated position
   */
  public Vector3D calculate(double time) {
    return calculate(time, p0, p1, p2, p3);
  }

}
