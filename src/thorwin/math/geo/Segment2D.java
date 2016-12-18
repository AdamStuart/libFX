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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import thorwin.math.Vector2D;

/**
 * A line segment between two points in 2-dimensional space.
 */
public final class Segment2D implements Serializable {
  private static final long serialVersionUID = -864064223336964646L;
  private final double x1;
  private final double y1;
  private final double x2;
  private final double y2;

  /**
   * Constructs a line segment between two points.
   *
   * @param p1 point 1
   * @param p2 point 2
   */
  public Segment2D(Vector2D p1, Vector2D p2) {
    this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
  }

  /**
   * Constructs a line segment between two points.
   *
   * @param x1 x-coordinate of point 1
   * @param y1 y-coordinate of point 1
   * @param x2 x-coordinate of point 2
   * @param y2 y-coordinate of point 2
   */
  public Segment2D(double x1, double y1, double x2, double y2) {
    super();
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  /**
   * Calculates all intersection points given the set of segments.
   *
   * @param segments segments
   * @return the intersection points
   */
  public static Map<Vector2D, List<Segment2D>> intersections(Segment2D[]
                                                                 segments) {
    Map<Vector2D, List<Segment2D>> map = new HashMap<>(32);

    // iterate over all segments and find intersections with other segments
    for (int i = 0; i < segments.length; i++) {
      Segment2D s1 = segments[i];

      for (int j = i + 1; j < segments.length; j++) {
        Segment2D s2 = segments[j];

        Optional<Vector2D> intersection = s1.intersect(s2);

        if (intersection.isPresent()) {
          List<Segment2D> indexes = map.get(intersection.get());

          if (indexes == null) {
            indexes = new ArrayList<>(2);
            map.put(intersection.get(), indexes);
          }

          indexes.add(s1);
          indexes.add(s2);
        }
      }
    }

    return map;
  }

  /**
   * @param value the value to evaluate
   * @param ref1  reference value 1
   * @param ref2  reference value 2
   * @return true if value is between ref1 and ref2
   */
  private static boolean between(double value, double ref1, double ref2) {
    if (ref1 > ref2) return between(value, ref2, ref1);

    return (value >= ref1) && (value <= ref2);
  }

  /**
   * Flips the two reference points <i>P1</i> and <i>P2</i>
   *
   * @return new segment
   */
  public Segment2D flip() {
    return new Segment2D(x2, y2, x1, y1);
  }

  /**
   * Returns the x-coordinate of reference point <i>P1</i>
   * @return <i>P1.x</i>
   */
  public double getX1() {
    return x1;
  }

  /**
   * Returns the x-coordinate of reference point <i>P2</i>
   * @return <i>P2.x</i>
   */
  public double getX2() {
    return x2;
  }

  /**
   * Returns the y-coordinate of reference point <i>P1</i>
   * @return <i>P1.y</i>
   */
  public double getY1() {
    return y1;
  }

  /**
   * Returns the y-coordinate of reference point <i>P2</i>
   * @return <i>P2.y</i>
   */
  public double getY2() {
    return y2;
  }

  /**
   * Determines the intersection point between this line segment and a line
   * @param line a line
   * @return the intersection point, if any
   */
  public Optional<Vector2D> intersect(Line2D line) {
    double x3 = line.getX1();
    double x4 = line.getX2();
    double y3 = line.getY1();
    double y4 = line.getY2();
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

      if (between(x, x1, x2) || between(y, y1, y2)) {
        return Optional.of(new Vector2D(x, y));
      } else {
        return Optional.empty();
      }
    }

  }

  /**
   * Determines the intersection point between this line segment and another
   * line segment
   * @param segment segment
   * @return the intersection point, if any
   */
  public Optional<Vector2D> intersect(Segment2D segment) {
    double x3 = segment.x1;
    double x4 = segment.x2;
    double y3 = segment.y1;
    double y4 = segment.y2;
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

      if ((between(x, x1, x2) && between(x, x3, x4)) ||
          (between(y, y1, y2) && between(y, y3, y4))) {

        return Optional.of(new Vector2D(x, y));
      } else {
        return Optional.empty();
      }
    }
  }

  /**
   * Calculates the intersections of this segment with the specified rectangle.
   * The current implementation uses the Liang-Barsky algorithm.
   *
   * @param rectangle rectangle
   * @return the intersection segment, if any
   */
  public Optional<Segment2D> clip(Rectangle2D rectangle) {
    double t0 = 0.0;
    double t1 = 1.0;
    double dx = x2 - x1;
    double dy = y2 - y1;

    for (int edge = 0; edge < 4; edge++) {
      double p, q, r;
      switch (edge) {
        case 0:
          p = -dx;
          q = -(rectangle.getX() - x1);
          break;
        case 1:
          p = dx;
          q = (rectangle.getX() + rectangle.getWidth() - x1);
          break;
        case 2:
          p = -dy;
          q = -(rectangle.getY() - y1);
          break;
        case 3:
          p = dy;
          q = (rectangle.getY() + rectangle.getHeight() - y1);
          break;
        default:
          throw new IllegalStateException();
      }

      r = q / p;

      if (p == 0 && q < 0) return Optional.empty();

      if (p < 0) {
        if (r > t1) return Optional.empty();
        else if (r > t0) t0 = r;
      } else if (p > 0) {
        if (r < t0) return Optional.empty();
        else if (r < t1) t1 = r;
      }
    }

    return Optional.of(
        new Segment2D(
            x1 + t0 * dx, y1 + t0 * dy,
            x1 + t1 * dx, y1 + t1 * dy));
  }

  /**
   * Determine if this line segment intersects a line
   * @param line The line
   * @return true if this line segment intersects line
   */
  public boolean intersects(Line2D line) {
    double x3 = line.getX1();
    double x4 = line.getX2();
    double y3 = line.getY1();
    double y4 = line.getY2();
    double x12 = x1 - x2;
    double x34 = x3 - x4;
    double y12 = y1 - y2;
    double y34 = y3 - y4;

    double c = (x12 * y34) - (y12 * x34);

    if (Math.abs(c) == 0) {
      return false;
    } else {
      double a = (x1 * y2) - (y1 * x2);
      double b = (x3 * y4) - (y3 * x4);

      double x = ((a * x34) - (b * x12)) / c;
      double y = ((a * y34) - (b * y12)) / c;

      return between(x, x1, x2) || between(y, y1, y2);
    }
  }

  /**
   * Determine if this line segment intersects another line segment
   * @param segment segment
   * @return true if this line segment intersects line
   */
  public boolean intersects(Segment2D segment) {
    double x3 = segment.x1;
    double x4 = segment.x2;
    double y3 = segment.y1;
    double y4 = segment.y2;
    double x12 = x1 - x2;
    double x34 = x3 - x4;
    double y12 = y1 - y2;
    double y34 = y3 - y4;

    double c = (x12 * y34) - (y12 * x34);

    if (Math.abs(c) == 0) {
      return false;
    } else {
      double a = (x1 * y2) - (y1 * x2);
      double b = (x3 * y4) - (y3 * x4);

      double x = ((a * x34) - (b * x12)) / c;
      double y = ((a * y34) - (b * y12)) / c;

      return (between(x, x1, x2) && between(x, x3, x4)) ||
          (between(y, y1, y2) && between(y, y3, y4));
    }
  }

  /**
   * Transforms the segment.
   *
   * @param transformation transformation
   * @return transformed segment
   */
  public Segment2D transform(Transform2D transformation) {
    return new Segment2D(
        transformation.transform(getP1()),
        transformation.transform(getP2())
    );
  }

  /**
   * Returns reference point <i>P1</i>
   * @return <i>P1</i>
   */
  public Vector2D getP1() {
    return new Vector2D(x1, y1);
  }

  /**
   * Returns reference point <i>P2</i>
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
    temp = Double.doubleToLongBits(y1);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(x2);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y2);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if ((obj == null) || (getClass() != obj.getClass())) return false;

    Segment2D segment2D = (Segment2D) obj;

    return Double.compare(segment2D.x1, x1) == 0 && Double.compare(segment2D.x2,
                                                                   x2) == 0
        && Double.compare(
        segment2D.y1,
        y1) == 0 && Double.compare(segment2D.y2, y2) == 0;

  }

  @Override
  public String toString() {
    return "Segment2D{" + "x1=" + x1 + ", y1=" + y1 + ", x2=" + x2 + ", y2="
        + y2 + '}';
  }
}
