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

/**
 * Axis aligned rectangle in 2-dimensional space.
 * The rectangle is defined using reference point (<i>P1</i>), a width and
 * height. Note that width and height may be negative.
 */
public final class Rectangle2D implements Serializable {

  private static final long serialVersionUID = -2172683519962498480L;

  private final double x;
  private final double y;
  private final double width;
  private final double height;

  /**
   * Constructs a square
   *
   * @param x    x-coordinate of point <i>P1</i>
   * @param y    y-coordinate of point <i>P1</i>
   * @param size width and height of the rectangle
   */
  public Rectangle2D(double x, double y, double size) {
    this(x, y, size, size);
  }

  /**
   * Constructs a rectangle
   *
   * @param x      x-coordinate of point <i>P1</i>
   * @param y      y-coordinate of point <i>P1</i>
   * @param width  width of the rectangle
   * @param height height of the rectangle
   */
  public Rectangle2D(double x, double y, double width, double height) {
    super();

    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  /**
   * Tests if this rectangle contains a point
   * @param point point to test
   * @return true if the rectangle contains the coordinate
   */
  public boolean contains(Vector2D point) {
    return contains(point.getX(), point.getY());
  }

  /**
   * Tests if this rectangle contains a point
   * @param x x-coordinate of the point to test
   * @param y y-coordinate of the point to test
   * @return true if the polygon contains the coordinate
   */
  public boolean contains(double x, double y) {
    Rectangle2D normalized = normalize();

    return (x >= normalized.x) &&
        (y >= normalized.y) &&
        (x <= (normalized.x + normalized.width)) &&
        (y <= (normalized.y + normalized.height));

  }

  /**
   * Normalizes this rectangle to have a positive width and height,
   * adjusting the reference position <i>P1</i> if necessary.
   * @return a new rectangle or this rectangle
   */
  public Rectangle2D normalize() {
    // quick check if anything needs to be done
    if (isNormalized()) return this;

    double normalizedX = x;
    double normalizedY = y;
    double normalizedWidth = width;
    double normalizedHeight = height;

    if (normalizedWidth < 0) {
      normalizedWidth = -normalizedWidth;
      normalizedX -= normalizedWidth;
    }

    if (normalizedHeight < 0) {
      normalizedHeight = -normalizedHeight;
      normalizedY -= height;
    }

    return new Rectangle2D(normalizedX, normalizedY, normalizedWidth,
                           normalizedHeight);
  }

  /**
   * Returns true if both width and height are positive.
   * @return true if normalized
   */
  private boolean isNormalized() {
    return (width >= 0) && (height >= 0);
  }

  /**
   * Tests if this rectangle contains another
   * @param rectangle another rectangle
   * @return true if the other rectangle is contained within this one
   */
  public boolean contains(Rectangle2D rectangle) {
    return rectangle.contains(x, y) &&
        rectangle.contains(x + width, y) &&
        rectangle.contains(x, y + height) &&
        rectangle.contains(x + width, y + height);
  }

  /**
   * Returns true if no parts of the rectangles overlap
   * @param rectangle rectangle
   * @return true if disjoint
   */
  public boolean disjoint(Rectangle2D rectangle) {
    Rectangle2D r1 = normalize();
    Rectangle2D r2 = rectangle.normalize();

    return ((r1.x + r1.width) < r2.x) ||
        ((r2.x + r2.width) < r1.x) ||
        ((r1.y + r1.height) < r2.y) ||
        ((r2.y + r2.height) < r1.y);
  }

  /**
   * Returns the height of the rectangle. The height may be negative.
   * @return height
   */
  public double getHeight() {
    return height;
  }

  /**
   * Returns the width of the rectangle. The width may be negative.
   * @return width
   */
  public double getWidth() {
    return width;
  }

  /**
   * Returns the x-coordinate of reference point <i>P1</i>
   * @return x
   */
  public double getX() {
    return x;
  }

  /**
   * Returns the y-coordinate of reference point <i>P1</i>
   * @return y
   */
  public double getY() {
    return y;
  }

  /**
   * Grows this rectangle by the given amount. The given amount
   * is added to both sides of the rectangle. This means that
   * both reference point and dimension of the rectangle is
   * changed.
   *
   * @param dx value to add to left and to right of the rectangle
   * @param dy value to add to top and bottom of the rectangle
   * @return enlarged rectangle
   */
  public Rectangle2D grow(double dx, double dy) {
    return new Rectangle2D(x - dx,
                           y - dy,
                           width + (dx * 2),
                           height + (dy * 2));
  }

  /**
   * Determines if this rectangle shares any area with another.
   *
   * @param other rectangle
   * @return true if the rectangle intersects with this one
   */
  public boolean intersects(Rectangle2D other) {
    return contains(other.x, other.y) ||
        contains(other.x + other.width, other.y) ||
        contains(other.x, other.y + other.height) ||
        contains(other.x + other.width, other.y + other.height) ||
        other.contains(x, y) ||
        other.contains(x + width, y) ||
        other.contains(x, y + height) ||
        other.contains(x + width, y + height);
  }

  /**
   * Determines if the line segment intersects with this rectangle area
   * @param segment segment to test
   * @return true if the segment intersects
   */
  public boolean intersects(Segment2D segment) {

    Rectangle2D normalized = normalize();

    if (normalized.width < 0 || normalized.height < 0)
      return normalize().intersects(segment);

    // no content means no intersection
    if (normalized.width <= 0 || normalized.height <= 0) return false;

    double x1 = segment.getX1();
    double y1 = segment.getY1();
    double x2 = segment.getX2();
    double y2 = segment.getY2();

    // check if segment end-points are contained within the rectangle
    if (x1 >= x && x1 <= x + width && y1 >= y && y1 <= y + normalized.height)
      return true;
    if (x2 >= x && x2 <= x + width && y2 >= y && y2 <= y + normalized.height)
      return true;

    double x3 = x + normalized.width;
    double y3 = y + normalized.height;

    return Line2D.intersects(x1, y1, x2, y2, x, y, x, y3)
        || Line2D.intersects(x1, y1, x2, y2, x, y3, x3, y3)
        || Line2D.intersects(x1, y1, x2, y2, x3, y3, x3, y)
        || Line2D.intersects(x1, y1, x2, y2, x3, y, x, y);
  }

  /**
   * Converts this rectangle to a polygon.
   *
   * @return a polygon
   */
  public Polygon2D toPolygon2D() {
    Vector2D[] vertices = {getP1(), getP2(), getP3(), getP4()};

    return new Polygon2D(vertices);
  }

  /**
   * Returns reference point <i>P1</i>
   * @return the first reference point (x,y).
   */
  public Vector2D getP1() {
    return new Vector2D(x, y);
  }

  /**
   * Returns reference point <i>P2</i>
   * @return the a reference point (x + width, y).
   */
  public Vector2D getP2() {
    return new Vector2D(x + width, y);
  }

  /**
   * Returns reference point <i>P3</i>
   * @return the a reference point (x + width, y + height).
   */
  public Vector2D getP3() {
    return new Vector2D(x + width, y + height);
  }

  /**
   * Returns reference point <i>P1</i>
   * @return the a reference point (x, y + height).
   */
  public Vector2D getP4() {
    return new Vector2D(x, y + height);
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(x);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(y);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(width);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(height);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if ((obj == null) || (getClass() != obj.getClass())) return false;

    Rectangle2D that = (Rectangle2D) obj;

    return Double.compare(that.height,
                          height) == 0 && Double.compare(that.width,
                                                         width) == 0 &&
        Double.compare(
            that.x,
            x) == 0 && Double.compare(that.y, y) == 0;

  }

  @Override
  public String toString() {
    return "Rectangle2D{" + "x=" + x + ", y=" + y + ", width=" + width + ", " +
        "height=" + height + '}';
  }
}
