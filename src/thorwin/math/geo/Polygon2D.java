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

import thorwin.math.Vector2D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A closed polygon in 2-dimensional space
 */
public final class Polygon2D implements Serializable {
  private static final long serialVersionUID = 955509001202467173L;
  private final Rectangle2D bounds;
  private final double[] xs;
  private final double[] ys;

  /**
   * Constructs a new polygon
   * @param vertices vertices of the polygon
   */
  public Polygon2D(List<Vector2D> vertices) {
    super();

    int n = vertices.size();

    if (n < 2) {
      throw new IllegalArgumentException("at least two vertices required");
    }

    xs = new double[n];
    ys = new double[n];

    double minX = vertices.get(0).getX();
    double maxX = minX;
    double minY = vertices.get(0).getY();
    double maxY = minY;

    for (int i = 0; i < n; i++) {
      Vector2D vertex = vertices.get(i);
      double x = vertex.getX();
      double y = vertex.getY();

      xs[i] = x;
      ys[i] = y;

      if (x < minX) minX = x;
      if (x > maxX) maxX = x;
      if (y < minY) minY = y;
      if (y > maxY) maxY = y;
    }

    bounds = new Rectangle2D(minX, minY, maxX - minX, maxY - minY);
  }

  /**
   * Constructs a new polygon
   * @param vertices vertices of the polygon.
   */
  public Polygon2D(Vector2D... vertices) {
    this(vertices, vertices.length);
  }

  /**
   * Constructs a new polygon
   * @param vertices vertices of the polygon
   * @param count    number of vertices in the array to use
   */
  private Polygon2D(Vector2D[] vertices, int count) {
    super();

    if (count < 2) {
      throw new IllegalArgumentException("at least two vertices required");
    }

    xs = new double[count];
    ys = new double[count];

    double minX = vertices[0].getX();
    double maxX = minX;
    double minY = vertices[0].getY();
    double maxY = minY;

    for (int i = 0; i < count; i++) {
      Vector2D vertex = vertices[i];
      double x = vertex.getX();
      double y = vertex.getY();

      xs[i] = x;
      ys[i] = y;

      if (x < minX) minX = x;
      if (x > maxX) maxX = x;
      if (y < minY) minY = y;
      if (y > maxY) maxY = y;
    }

    bounds = new Rectangle2D(minX, minY, maxX - minX, maxY - minY);
  }

  /**
   * Determine if this polygon is counter-clockwise
   * @return true if counter-clockwise
   */
  public boolean ccw() {
    return area() > 0;
  }

  /**
   * Returns the area of the polygon. The area is negative if
   * the polygon is defined clockwise.
   * @return surface area
   */
  public double area() {
    int n = xs.length;

    double area = 0.0;

    for (int p = n - 1, q = 0; q < n; p = q++) {
      area += xs[p] * ys[q] - xs[q] * ys[p];
    }
    return area / 2.0;
  }

  /**
   * Clips everything outside the rectangular area. Will result
   * in a new polygon if a polygon remains after clipping.
   *
   * @param bounds clipping rectangle
   * @return the clipped polygon, if any
   */
  public Optional<Polygon2D> clip(Rectangle2D bounds) {
    // quick check if clipping can possibly yield a result
    if (bounds.disjoint(this.bounds)) return Optional.empty();

    Rectangle2D normalized = bounds.normalize();

    double left = normalized.getX();
    double right = left + normalized.getWidth();
    double top = normalized.getY();
    double bottom = top + normalized.getHeight();

    Optional<Polygon2D> clip = clipRight(right);

    if (clip.isPresent()) {
      clip = clip.get().clipLeft(left);

      if (clip.isPresent()) {
        clip = clip.get().clipTop(top);

        if (clip.isPresent()) {
          clip = clip.get().clipBottom(bottom);
        }
      }
    }
    return clip;
  }

  /**
   * Clips everything right of vertical line
   *
   * @param x x-coordinate of the vertical line
   * @return the clipped polygon, if any
   */
  public Optional<Polygon2D> clipRight(double x) {
    int n = xs.length;
    double previousX = xs[n - 1];
    double previousY = ys[n - 1];

    List<Vector2D> output = new ArrayList<>(n);

    for (int i = 0; i < n; i++) {
      double currentX = xs[i];
      double currentY = ys[i];

      if (previousX <= x) {
        if (currentX <= x) {
          // Previous IN, Current IN
          output.add(new Vector2D(currentX, currentY));
        } else {
          // Previous IN, Current OUT
          Optional<Vector2D> intersection = Line2D.intersect(previousX,
                                                             previousY,
                                                             currentX,
                                                             currentY,
                                                             x,
                                                             0,
                                                             x,
                                                             1);

          output.add(intersection.get());
        }
      } else if (currentX <= x) {
        // Previous OUT, Current IN
        Optional<Vector2D> intersection = Line2D.intersect(previousX,
                                                           previousY,
                                                           currentX,
                                                           currentY,
                                                           x,
                                                           0,
                                                           x,
                                                           1);
        Vector2D current = new Vector2D(currentX, currentY);

        output.add(intersection.get());
        output.add(current);
      }

      previousX = currentX;
      previousY = currentY;
    }

    // remove duplicates
    output = optimize(output);

    // check if enough vertices remain to matrix a polygon
    if (output.size() < 3) return Optional.empty();

    return Optional.of(new Polygon2D(output));
  }

  /**
   * Clips all everything left of vertical line
   *
   * @param x x-coordinate of the vertical line
   * @return the clipped polygon, if any
   */
  public Optional<Polygon2D> clipLeft(double x) {
    int n = xs.length;
    double previousX = xs[n - 1];
    double previousY = ys[n - 1];

    List<Vector2D> output = new ArrayList<>(n);

    for (int i = 0; i < n; i++) {
      double currentX = xs[i];
      double currentY = ys[i];

      if (previousX >= x) {
        if (currentX >= x) {
          // Previous IN, Current IN
          output.add(new Vector2D(currentX, currentY));
        } else {
          // Previous IN, Current OUT
          Optional<Vector2D> intersection = Line2D.intersect(previousX,
                                                             previousY,
                                                             currentX,
                                                             currentY,
                                                             x,
                                                             0,
                                                             x,
                                                             1);

          output.add(intersection.get());
        }
      } else if (currentX >= x) {
        // Previous OUT, Current IN
        Optional<Vector2D> intersection = Line2D.intersect(previousX,
                                                           previousY,
                                                           currentX,
                                                           currentY,
                                                           x,
                                                           0,
                                                           x,
                                                           1);
        Vector2D current = new Vector2D(currentX, currentY);

        output.add(intersection.get());
        output.add(current);
      }

      previousX = currentX;
      previousY = currentY;
    }

    // remove duplicates
    output = optimize(output);

    // check if enough vertices remain to matrix a polygon
    if (output.size() < 3) return Optional.empty();

    return Optional.of(new Polygon2D(output));
  }

  /**
   * Clips everything above horizontal line
   *
   * @param y y-coordinate of the horizontal line
   * @return the clipped polygon, if any
   */
  public Optional<Polygon2D> clipTop(double y) {
    int n = xs.length;
    double previousX = xs[n - 1];
    double previousY = ys[n - 1];

    List<Vector2D> output = new ArrayList<>(n);

    for (int i = 0; i < n; i++) {
      double currentX = xs[i];
      double currentY = ys[i];

      if (previousY >= y) {
        if (currentY >= y) {
          // Previous IN, Current IN
          Vector2D current = new Vector2D(currentX, currentY);

          output.add(current);
        } else {
          // Previous IN, Current OUT
          Optional<Vector2D> intersection = Line2D.intersect(previousX,
                                                             previousY,
                                                             currentX,
                                                             currentY,
                                                             0,
                                                             y,
                                                             1,
                                                             y);

          output.add(intersection.get());
        }
      } else if (currentY >= y) {
        // Previous OUT, Current IN
        Optional<Vector2D> intersection = Line2D.intersect(previousX,
                                                           previousY,
                                                           currentX,
                                                           currentY,
                                                           0,
                                                           y,
                                                           1,
                                                           y);
        Vector2D current = new Vector2D(currentX, currentY);

        output.add(intersection.get());
        output.add(current);
      }

      previousX = currentX;
      previousY = currentY;
    }

    // remove duplicates
    output = optimize(output);

    // check if enough vertices remain to matrix a polygon
    if (output.size() < 3) return Optional.empty();

    return Optional.of(new Polygon2D(output));
  }

  /**
   * Clips everything under horizontal line
   *
   * @param y y-coordinate of the horizontal line
   * @return the clipped polygon, if any
   */
  public Optional<Polygon2D> clipBottom(double y) {
    int n = xs.length;
    double previousX = xs[n - 1];
    double previousY = ys[n - 1];

    List<Vector2D> output = new ArrayList<>(n);

    for (int i = 0; i < n; i++) {
      double currentX = xs[i];
      double currentY = ys[i];

      if (previousY <= y) {
        if (currentY <= y) {
          // Previous IN, Current IN
          output.add(new Vector2D(currentX, currentY));
        } else {
          // Previous IN, Current OUT
          Optional<Vector2D> intersection = Line2D.intersect(previousX,
                                                             previousY,
                                                             currentX,
                                                             currentY,
                                                             0,
                                                             y,
                                                             1,
                                                             y);

          output.add(intersection.get());
        }
      } else if (currentY <= y) {
        // Previous OUT, Current IN
        Optional<Vector2D> intersection = Line2D.intersect(previousX,
                                                           previousY,
                                                           currentX,
                                                           currentY,
                                                           0,
                                                           y,
                                                           1,
                                                           y);
        Vector2D current = new Vector2D(currentX, currentY);

        output.add(intersection.get());
        output.add(current);
      }

      previousX = currentX;
      previousY = currentY;
    }

    // remove duplicates
    output = optimize(output);

    // check if enough vertices remain to matrix a polygon
    if (output.size() < 3) return Optional.empty();

    return Optional.of(new Polygon2D(output));
  }

  /**
   * Optimizes the vertices of a polygon by removing successive duplicates.
   *
   * @param vertices vertices to optimize
   * @return list of polygon vertices
   */
  private static List<Vector2D> optimize(List<Vector2D> vertices) {
    List<Vector2D> points = new ArrayList<>(vertices.size());

    if (!vertices.isEmpty()) {
      Vector2D previous = vertices.get(vertices.size() - 1);


      for (Vector2D current : vertices) {
        if (!current.equals(previous)) {
          points.add(current);
          previous = current;
        }
      }
    }

    return points;
  }

  /**
   * Determines if this polygon contains a point
   * @param p point
   * @return true if the polygon contains the coordinate
   */
  public boolean contains(Vector2D p) {
    return contains(p.getX(), p.getY());
  }

  @SuppressWarnings("SuspiciousNameCombination")
  public boolean contains(double x, double y) {

    int n = xs.length;

    if (n < 3) return false;

    // quick check to see if point is contained in bounds of the polygon
    if (!bounds.contains(x, y)) {
      return false;
    }

    double currentX;
    double currentY;
    double previousX = ys[n - 1];
    double lastX = xs[n - 1];
    int hits = 0;

    for (int i = 0; i < n; lastX = currentX, previousX = currentY, i++) {
      currentX = xs[i];
      currentY = ys[i];

      if ((Double.compare(currentX, lastX) == 0) && (Double.compare
          (currentY, previousX) == 0)) {
        continue;
      }

      double leftX;
      double testX;
      double testY;

      if (currentX < lastX) {
        if (x >= lastX) {
          continue;
        }
        leftX = currentX;
      } else {
        if (x >= currentX) {
          continue;
        }
        leftX = lastX;
      }


      if (currentY < previousX) {
        if ((y < currentY) || (y >= previousX)) {
          continue;
        }
        if (x < leftX) {
          hits++;
          continue;
        }
        testX = x - currentX;
        testY = y - currentY;
      } else {
        if ((y < previousX) || (y >= currentY)) {
          continue;
        }
        if (x < leftX) {
          hits++;
          continue;
        }
        testX = x - lastX;
        testY = y - previousX;
      }

      if (testX < ((testY / (previousX - currentY)) * (lastX -
          currentX))) {
        hits++;
      }
    }

    // return true if event hits are even
    return (hits & 1) != 0;
  }

  /**
   * Determines if this polygon contains a rectangle
   * @param rectangle a rectangle
   * @return true if the rectangle is contained within this polygon
   */
  public boolean contains(Rectangle2D rectangle) {
    // optimize by checking bounds are disjoint
    if (bounds.disjoint(rectangle)) return false;

    double x = rectangle.getX();
    double y = rectangle.getY();
    double w = rectangle.getWidth();
    double h = rectangle.getHeight();

    // quickly test corner points, as they all need to be contained
    if (!contains(x, y) ||
        !contains(x + w, y) ||
        !contains(x, y + h) ||
        !contains(x + w, y + h)) return false;

    // now test if there are no intersections
    Segment2D s1 = new Segment2D(x, y, x + w, y);
    Segment2D s2 = new Segment2D(x + w, y, x + w, y + h);
    Segment2D s3 = new Segment2D(x + w, y + h, x, y + h);
    Segment2D s4 = new Segment2D(x, y + h, x, y);

    for (Segment2D segment : segments().toArray(Segment2D[]::new)) {
      if (s1.intersects(segment) ||
          s2.intersects(segment) ||
          s3.intersects(segment) ||
          s4.intersects(segment)) return false;
    }

    // all points in polygon and no intersections. this means the rectangle
    // is completely within the polygon
    return true;
  }

  /**
   * Creates an array of this polygons segments.
   *
   * @return this polygon's segments
   */
  public Stream<Segment2D> segments() {
    Segment2D[] segments = new Segment2D[xs.length];

    for (int i = 0; i < segments.length; i++) {
      Vector2D p1 = new Vector2D(xs[i], ys[i]);
      Vector2D p2 = ((i + 1) == segments.length) ? new Vector2D(xs[0],
                                                                ys[0]) : new
          Vector2D(
          xs[i + 1],
          ys[i + 1]);

      segments[i] = new Segment2D(p1, p2);
    }

    return Arrays.stream(segments);
  }

  /**
   * Determine if this polygon is defined clockwise.
   * @return true if this polygon is clockwise.
   */
  public boolean cw() {
    return area() < 0;
  }

  /**
   * Returns the bounds of this polygon
   * @return rectangle
   */
  public Rectangle2D getBounds() {
    return bounds;
  }

  /**
   * Returns the vertex in the polygon
   * @param index index number of the vertex
   * @return vertex
   */
  public Vector2D getVertex(int index) {

    if (index >= xs.length)
      return getVertex(index % xs.length);

    return new Vector2D(xs[index], ys[index]);
  }

  /**
   * Calculates if the rectangle is contained within the polygon
   * @param rectangle rectangle
   * @return true if contained
   */
  public boolean intersects(Rectangle2D rectangle) {
    // optimize by checking bounds
    if (bounds.disjoint(rectangle)) return false;

    double x = rectangle.getX();
    double y = rectangle.getY();
    double w = rectangle.getWidth();
    double h = rectangle.getHeight();

    // now test if there are no intersections
    Segment2D s1 = new Segment2D(x, y, x + w, y);
    Segment2D s2 = new Segment2D(x + w, y, x + w, y + h);
    Segment2D s3 = new Segment2D(x + w, y + h, x, y + h);
    Segment2D s4 = new Segment2D(x, y + h, x, y);

    for (Segment2D segment : segments().toArray(Segment2D[]::new)) {
      if (s1.intersects(segment) ||
          s2.intersects(segment) ||
          s3.intersects(segment) ||
          s4.intersects(segment)) return false;
    }

    // all points in polygon and no intersections. this means the rectangle
    // is completely within the polygon
    return true;
  }

  /**
   * Transforms this polygon
   * @param transformation transformation
   * @return transformed polygon
   */
  public Polygon2D transform(Transform2D transformation) {
    Vector2D[] vertices = vertices().map(transformation::transform)
                                    .toArray(Vector2D[]::new);

    return new Polygon2D(vertices);
  }

  /**
   * Creates stream of this polygons vertices
   *
   * @return stream of vertices
   */
  public Stream<Vector2D> vertices() {
    return IntStream.range(0, size())
                    .mapToObj(this::getVertex);
  }


  /**
   * Returns the number of vertices in this polygon
   * @return size
   */
  public int size() {
    return xs.length;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("Polygon2D {");

    for (int i = 0; i < xs.length; i++) {
      if (i > 0) builder.append(", ");
      builder.append("(");
      builder.append(xs[i]);
      builder.append(", ");
      builder.append(ys[i]);
      builder.append(")");
    }

    builder.append("}");

    return builder.toString();
  }

}
