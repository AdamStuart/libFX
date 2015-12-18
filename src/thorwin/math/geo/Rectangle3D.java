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
 * 2-dimensional rectangle in 3-dimensional space.
 */
public final class Rectangle3D implements java.io.Serializable {

  private final Vector3D origin;
  private final Vector3D up;
  private final Vector3D right;
  private final double width;
  private final double height;

  /**
   * Constructs a rectangle
   *
   * @param origin origin of the rectangle
   * @param up     up direction (normal vector)
   * @param right  right direction (normal vector)
   * @param width  width of the rectangle
   * @param height height of the rectangle
   */
  public Rectangle3D(Vector3D origin,
                     Vector3D up,
                     Vector3D right,
                     double width,
                     double height) {
    super();

    if (origin == null)
      throw new IllegalArgumentException("origin should not be null");

    this.origin = origin;
    this.up = up;
    this.right = right;
    this.width = width;
    this.height = height;
  }

  /**
   * Returns the up normal vector
   * @return The up normal
   */
  public Vector3D getUp() {
    return up;
  }

  /**
   * Returns the width of the rectangle
   * @return width
   */
  public double getWidth() {
    return width;
  }

  /**
   * Returns the height of the rectangle
   * @return height
   */
  public double getHeight() {
    return height;
  }

  /**
   * Returns the origin of the rectangle
   * @return The origin of the rectangle
   */
  public Vector3D getOrigin() {

    return origin;
  }

  /**
   * Returns the top left corner
   * @return top left corner
   */
  public Vector3D getTopLeft() {
    return origin
        .subtract(this.right.multiply(this.width * 0.5))
        .add(this.up.multiply(this.height * 0.5));
  }

  /**
   * Returns the top right corner
   * @return top right corner
   */
  public Vector3D getTopRight() {
    return origin
        .add(this.right.multiply(this.width * 0.5))
        .add(this.up.multiply(this.height * 0.5));
  }

  /**
   * Returns the bottom left corner
   * @return bottom left corner
   */
  public Vector3D getBottomLeft() {
    return origin
        .subtract(this.right.multiply(this.width * 0.5))
        .subtract(this.up.multiply(this.height * 0.5));
  }

  /**
   * Returns the bottom right corner
   * @return bottom right corner
   */
  public Vector3D getBottomRight() {
    return origin
        .add(this.right.multiply(this.width * 0.5))
        .subtract(this.up.multiply(this.height * 0.5));
  }

}
