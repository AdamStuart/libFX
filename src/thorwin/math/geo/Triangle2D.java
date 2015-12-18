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

import thorwin.math.Affine2D;
import thorwin.math.Vector2D;

import java.io.Serializable;

/**
 * Triangle in 2-dimensional space
 */
public final class Triangle2D implements Serializable {
  private static final long serialVersionUID = -3178203481630352951L;
  private final double x1;
  private final double y1;
  private final double x2;
  private final double y2;
  private final double x3;
  private final double y3;

  /**
   * Constructs a new triangle
   *
   * @param p1 point 1
   * @param p2 point 2
   * @param p3 point 3
   */
  public Triangle2D(Vector2D p1, Vector2D p2, Vector2D p3) {
    this(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY());
  }

  /**
   * Constructs a triangle using three points.
   *
   * @param x1 x-coordinate of point 1
   * @param y1 y-coordinate of point 1
   * @param x2 x-coordinate of point 2
   * @param y2 y-coordinate of point 2
   * @param x3 x-coordinate of point 3
   * @param y3 y-coordinate of point 3
   */
  public Triangle2D(double x1, double y1, double x2, double y2, double x3,
                    double y3) {
    super();
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
    this.x3 = x3;
    this.y3 = y3;
  }

  /**
   * Calculates angle between three points. The returned value is defined as
   * the
   * angle between segment <i>P1-p2</i> and <i>P2-P3</i>, in a clockwise
   * direction. Returned values will be within <i>[-PI, PI]</i> where negative
   * values indicate counter clockwise direction.
   *
   * @param p1 point 1
   * @param p2 point 2
   * @param p3 point 2
   * @return angle in radians
   */
  public static double angle(Vector2D p1, Vector2D p2, Vector2D p3) {
    return angle(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(),
                 p3.getY());
  }

  /**
   * Calculates angle between three points. The returned value is defined as
   * the
   * angle between segment <i>P1-p2</i> and <i>P2-P3</i>, in a clockwise
   * direction. Returned values will be within <i>[-PI, PI]</i> where negative
   * values indicate counter clockwise direction.
   *
   * @param x1 x-coordinate of point 1
   * @param y1 y-coordinate of point 1
   * @param x2 x-coordinate of point 2
   * @param y2 y-coordinate of point 2
   * @param x3 x-coordinate of point 3
   * @param y3 y-coordinate of point 3
   * @return angle in radians
   */
  public static double angle(double x1,
                             double y1,
                             double x2,
                             double y2,
                             double x3,
                             double y3) {
    double dx21 = x2 - x1;
    double dy21 = y2 - y1;
    double dx31 = x3 - x1;
    double dy31 = y3 - y1;
    double dx32 = x3 - x2;
    double dy32 = y3 - y2;

    double d12 = Math.hypot(dx21, dy21);
    double d13 = Math.hypot(dx31, dy31);
    double d23 = Math.hypot(dx32, dy32);

    double angle = Math.acos((((d12 * d12) + (d23 * d23)) - (d13 * d13)) / (2
        * d12 * d23));
    boolean ccw = (dx21 * dy31) > (dx31 * dy21);

    return ccw ? -angle : angle;
  }

  /**
   * Returns true if traversing <i>P1</i> to <i>P3</i> is counter clockwise
   *
   * @param p1 point 1
   * @param p2 point 2
   * @param p3 point 3
   * @return true if counter clockwise
   */
  public static boolean ccw(Vector2D p1, Vector2D p2, Vector2D p3) {
    return ccw(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3
        .getY());
  }

  /**
   * Returns true if traversing <i>P1</i> to <i>P3</i> is counter clockwise
   *
   * @param x1 x-coordinate of point 1
   * @param y1 y-coordinate of point 1
   * @param x2 x-coordinate of point 2
   * @param y2 y-coordinate of point 2
   * @param x3 x-coordinate of point 3
   * @param y3 y-coordinate of point 3
   * @return true if counter clockwise
   */
  public static boolean ccw(double x1, double y1, double x2, double y2,
                            double x3, double y3) {
    return ((x2 - x1) * (y3 - y1)) > ((x3 - x1) * (y2 - y1));
  }

  /**
   * Returns true if traversing <i>P1</i> to <i>P3</i> is clockwise
   *
   * @param x1 x-coordinate of point 1
   * @param y1 y-coordinate of point 1
   * @param x2 x-coordinate of point 2
   * @param y2 y-coordinate of point 2
   * @param x3 x-coordinate of point 3
   * @param y3 y-coordinate of point 3
   * @return true if clockwise
   */
  public static boolean cw(double x1, double y1, double x2, double y2,
                           double x3, double y3) {
    return !ccw(x1, y1, x2, y2, x3, y3);
  }

  /**
   * Returns true if traversing <i>P1</i> to <i>P3</i> is clockwise
   *
   * @param p1 point 1
   * @param p2 point 2
   * @param p3 point 3
   * @return true if clockwise
   */
  public static boolean cw(Vector2D p1, Vector2D p2, Vector2D p3) {
    return !ccw(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3
        .getY());
  }

  /**
   * Returns true if the point is inside this triangle
   *
   * @param p point
   * @return true if inside
   */
  public boolean contains(Vector2D p) {
    return contains(p.getX(), p.getY());
  }

  /**
   * Returns true if the point is inside this triangle
   *
   * @param x x-coordinate
   * @param y y-coordinate
   * @return true if inside
   */
  public boolean contains(double x, double y) {
    return contains(x1, y1, x2, y2, x3, y3, x, y);
  }

  /**
   * Utility function for performing the contains function without having to
   * allocate a triangle instance.
   *
   * @param x1 x-coordinate of point 1
   * @param y1 y-coordinate of point 1
   * @param x2 x-coordinate of point 2
   * @param y2 y-coordinate of point 2
   * @param x3 x-coordinate of point 3
   * @param y3 y-coordinate of point 3
   * @param x  x-coordinate of the tested point
   * @param y  y-coordinate of the tested point
   * @return true if the point is contained in the triangle
   */
  public static boolean contains(double x1,
                                 double y1,
                                 double x2,
                                 double y2,
                                 double x3,
                                 double y3,
                                 double x,
                                 double y) {
    return ((((x3 - x2) * (y - y2)) - ((y3 - y2) * (x - x2))) >= 0.0) &&
        ((((x1 - x3) * (y - y3)) - ((y1 - y3) * (x - x3))) >= 0.0) &&
        ((((x2 - x1) * (y - y1)) - ((y2 - y1) * (x - x1))) >= 0.0);
  }

  /**
   * Returns the x-coordinate of point <i>P1</i>
   * @return The x-coordinate
   */
  public double getX1() {
    return x1;
  }

  /**
   * Returns the x-coordinate of point <i>P2</i>
   * @return The x-coordinate
   */
  public double getX2() {
    return x2;
  }

  /**
   * Returns the x-coordinate of point <i>P31</i>
   * @return The x-coordinate
   */
  public double getX3() {
    return x3;
  }

  /**
   * Returns the y-coordinate of point <i>P1</i>
   * @return The y-coordinate
   */
  public double getY1() {
    return y1;
  }

  /**
   * Returns the y-coordinate of point <i>P2</i>
   * @return The y-coordinate
   */
  public double getY2() {
    return y2;
  }

  /**
   * Returns the y-coordinate of point <i>P3</i>
   * @return The y-coordinate
   */
  public double getY3() {
    return y3;
  }

  /**
   * Determine if this triangle is clockwise
   * @return true if this triangle is defined clockwise.
   */
  public boolean cw() {
    return area() < 0;
  }

  /**
   * Calculate the area of the triangle. Area is positive for clockwise
   * triangle, negative for clockwise triangles
   *
   * @return area of the triangle
   */
  public double area() {
    return ((
        (x1 * y2) +
            (x2 * y3) +
            (x3 * y1)
    ) -
        (x1 * y3) -
        (x3 * y2) -
        (x2 * y1)
    ) / 2.0;
  }

  /**
   * Converts this triangle to a polygon
   *
   * @return polygon
   */
  public Polygon2D toPolygon() {
    return new Polygon2D(getP1(), getP2(), getP3());
  }

  /**
   * Returns triangle point <i>P1</i>
   * @return point
   */
  public Vector2D getP1() {
    return new Vector2D(x1, y1);
  }

  /**
   * Returns triangle point <i>P2</i>
   * @return point
   */
  public Vector2D getP2() {
    return new Vector2D(x2, y2);
  }

  /**
   * Returns triangle point <i>P3</i>
   * @return point
   */
  public Vector2D getP3() {
    return new Vector2D(x3, y3);
  }

  /**
   * Transforms the triangle
   *
   * @param transformation transformation
   * @return transformed triangle
   */
  public Triangle2D transform(Affine2D transformation) {
    return new Triangle2D(
        transformation.multiply(getP1()),
        transformation.multiply(getP2()),
        transformation.multiply(getP3()));
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(x1);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y1);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(x2);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y2);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(x3);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y3);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if ((obj == null) || (getClass() != obj.getClass())) return false;

    Triangle2D that = (Triangle2D) obj;

    return Double.compare(that.x1, x1) == 0 && Double.compare(that.x2,
                                                              x2) == 0 &&
        Double.compare(
            that.x3,
            x3) == 0 && Double.compare(that.y1,
                                       y1) == 0 && Double.compare(that.y2,
                                                                  y2) == 0 &&
        Double.compare(
            that.y3,
            y3) == 0;

  }

  @Override
  public String toString() {
    return "Triangle2D{" + "x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2="
        + y2 + ", x3=" + x3 + ", " +
        "y3=" + y3 + '}';
  }
}
