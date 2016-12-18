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

import java.io.Serializable;
import java.util.Optional;

import thorwin.math.Vector2D;

/**
 * A line in 2-dimensional space.
 */
public final class Line2D implements Serializable {
  private static final long serialVersionUID = 3457567445750841016L;

  private final double x1;
  private final double x2;
  private final double y1;
  private final double y2;

  /**
   * Construct a line through two points.
   *
   * @param p1 point 1
   * @param p2 point 2
   */
  public Line2D(Vector2D p1, Vector2D p2) {
    this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }

  /**
   * Construct a line through two points.
   *
   * @param x1 reference point 1, x
   * @param y1 reference point 1, y
   * @param x2 reference point 2, x
   * @param y2 reference point 2, y
   */
  public Line2D(double x1, double y1, double x2, double y2) {
    super();
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  /**
   * Returns the x coordinate of reference point 1
   * @return the x1
   */
  public double getX1() {
    return x1;
  }

  /**
   * Returns the x coordinate of reference point 2
   * @return the x2
   */
  public double getX2() {
    return x2;
  }

  /**
   * Returns the y coordinate of reference point 1
   * @return the y1
   */
  public double getY1() {
    return y1;
  }

  /**
   * Returns the y coordinate of reference point 2
   * @return the y2
   */
  public double getY2() {
    return y2;
  }

  /**
   * Calculate the intersection point between this line and
   * another. Parallel lines have no intersection.
   * @param line other line
   * @return intersection point, if any
   */
  public Optional<Vector2D> intersect(Line2D line) {
    return intersect(x1, y1, x2, y2, line.x1, line.y1, line.x2, line.y2);
  }

  /**
   * Calculates intersection of lines <i>(x1,y2),(x2,y2)</i> and
   * <i>(x3,y3),(x4,y4)</i>.
   * Parallel lines do not intersect.
   *
   * @param x1 line 1, reference point 1, x
   * @param y1 line 1, reference point 1, y
   * @param x2 line 1, reference point 2, x
   * @param y2 line 1, reference point 2, y
   * @param x3 line 2, reference point 1, x
   * @param y3 line 2, reference point 1, y
   * @param x4 line 2, reference point 2, x
   * @param y4 line 2, reference point 2, y
   * @return the intersection point if any.
   */
  public static Optional<Vector2D> intersect(double x1, double y1, double x2,
                                             double y2, double x3, double y3,
                                             double x4, double y4) {
    double x12 = x1 - x2;
    double x34 = x3 - x4;
    double y12 = y1 - y2;
    double y34 = y3 - y4;

    double c = (x12 * y34) - (y12 * x34);

    if (Math.abs(c) == 0) {
      return Optional.empty();
    } else {
      double a = (x1 * y2) - (y1 * x2);
      double b = (x3 * y4) - (y3 * x4);

      double x = ((a * x34) - (b * x12)) / c;
      double y = ((a * y34) - (b * y12)) / c;

      return Optional.of(new Vector2D(x, y));
    }

  }

  /**
   * Calculates the intersection point of this line and a line segment.
   * @param segment other segment
   * @return intersection, if any
   */
  public Optional<Vector2D> intersect(Segment2D segment) {
    return segment.intersect(this);
  }

  /**
   * Determines if this line intersects with another. The line
   * will not intersect if they are parallel.
   * @param line other line
   * @return true if there is an intersection
   */
  public boolean intersects(Line2D line) {
    return intersects(x1, y1, x2, y2, line.x1, line.y1, line.x2, line.y2);
  }

  /**
   * Determines if there is an intersection between lines <i>(x1,y2),(x2,y2)</i> and
   * <i>(x3,y3),(x4,y4)</i>.
   *
   * @param x1 line 1, reference point 1, x
   * @param y1 line 1, reference point 1, y
   * @param x2 line 1, reference point 2, x
   * @param y2 line 1, reference point 2, y
   * @param x3 line 2, reference point 1, x
   * @param y3 line 2, reference point 1, y
   * @param x4 line 2, reference point 2, x
   * @param y4 line 2, reference point 2, y
   * @return true if the lines intersect.
   */
  public static boolean intersects(double x1, double y1, double x2,
                                   double y2, double x3, double y3,
                                   double x4, double y4) {
    double x12 = x1 - x2;
    double x34 = x3 - x4;
    double y12 = y1 - y2;
    double y34 = y3 - y4;

    double c = (x12 * y34) - (y12 * x34);

    return Math.abs(c) != 0;
  }

  /**
   * Determines if this line intersects a line segment.
   * @param segment segment
   * @return true if the line intersects with the segment
   */
  public boolean intersects(Segment2D segment) {
    return segment.intersects(this);
  }

  /**
   * Transforms the line.
   *
   * @param transformation The toAffine3D to use.
   * @return transformed line
   */
  public Line2D transform(Transform2D transformation) {
    return new Line2D(
        transformation.transform(getP1()),
        transformation.transform(getP2())
    );
  }

  /**
   * Returns the first reference point
   * @return <i>P1</i>
   */
  public Vector2D getP1() {
    return new Vector2D(x1, y1);
  }

  /**
   * Return the second reference point
   * @return <i>P2</i>
   */
  public Vector2D getP2() {
    return new Vector2D(x2, y2);
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(x1);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(x2);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y1);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y2);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if ((obj == null) || (getClass() != obj.getClass())) return false;

    Line2D line2D = (Line2D) obj;

    return Double.compare(line2D.x1, x1) == 0 && Double.compare(line2D.x2,
                                                                x2) == 0
        && Double.compare(
        line2D.y1,
        y1) == 0 && Double.compare(line2D.y2, y2) == 0;

  }

  @Override
  public String toString() {
    return "Line2D{" + "x1=" + x1 + ", x2=" + x2 + ", y1=" + y1 + ", y2=" +
        y2 + '}';
  }
}
