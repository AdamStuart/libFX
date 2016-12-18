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

import static java.lang.Math.min;
import static java.lang.StrictMath.max;

import java.util.Iterator;

import thorwin.math.Vector3D;


/**
 * An axis aligned box.
 */
public final class AABB implements java.io.Serializable  {

  private final double minX;
  private final double maxX;
  private final double minY;
  private final double maxY;
  private final double minZ;
  private final double maxZ;

  /**
   * Constructs a bounding box. Note that the min/max values should be sane
   * (max should be greater than min).
   *
   * @param minX minimum x-coordinate
   * @param maxX maximum x-coordinate
   * @param minY minimum y-coordinate
   * @param maxY maximum y-coordinate
   * @param minZ minimum z-coordinate
   * @param maxZ maximum z-coordinate
   */
  public AABB(double minX,
              double maxX,
              double minY,
              double maxY,
              double minZ,
              double maxZ) {

    if (minX > maxX)
      throw new IllegalArgumentException("Specified minX/maxX range not valid");
    if (minY > maxY)
      throw new IllegalArgumentException("Specified minY/maxY range not valid");
    if (minZ > maxZ)
      throw new IllegalArgumentException("Specified minZ/maxZ range not valid");

    this.minX = minX;
    this.maxX = maxX;
    this.minY = minY;
    this.maxY = maxY;
    this.minZ = minZ;
    this.maxZ = maxZ;
  }

  /**
   * Creates a bounding box from a set of vertices.
   *
   * @param vertices the vertices (not null, 1 or more vertices required)
   * @return The axis aligned bounding box
   */
  public static AABB valueOf(Vector3D... vertices) {
    if (vertices == null || vertices.length == 0) {
      throw new IllegalArgumentException(
          "vertices should not be null and contain at least 1 vertex");
    }

    // get the first vertex
    Vector3D first = vertices[0];

    // initialize bounds on first vertex
    double minX = first.getX();
    double maxX = minX;
    double minY = first.getY();
    double maxY = minY;
    double minZ = first.getZ();
    double maxZ = minZ;

    // iterate over the other verices
    for (int i = 1; i < vertices.length; i++) {
      Vector3D vertex = vertices[i];

      minX = min(minX, vertex.getX());
      maxX = Math.max(maxX, vertex.getX());
      minY = min(minY, vertex.getY());
      maxY = Math.max(maxY, vertex.getY());
      minZ = min(minZ, vertex.getZ());
      maxZ = Math.max(maxZ, vertex.getZ());
    }

    return new AABB(minX, maxX, minY, maxY, minZ, maxZ);
  }

  /**
   * Creates a bounding box from a set of vertices.
   *
   * @param vertices vertices (not null, 1 or more vertices required)
   * @return The axis aligned bounding box
   */
  public static AABB valueOf(Iterator<Vector3D> vertices) {
    if (vertices == null) {
      throw new IllegalArgumentException("vertices should not be null");
    }

    if (!vertices.hasNext()) {
      throw new IllegalArgumentException(
          "vertices should have at least 1 element");
    }

    // get the first vertex
    Vector3D first = vertices.next();

    // initialize bounds on first vertex
    double minX = first.getX();
    double maxX = minX;
    double minY = first.getY();
    double maxY = minY;
    double minZ = first.getZ();
    double maxZ = minZ;

    // iterate over the other verices
    while (vertices.hasNext()) {
      Vector3D vertex = vertices.next();

      minX = min(minX, vertex.getX());
      maxX = Math.max(maxX, vertex.getX());
      minY = min(minY, vertex.getY());
      maxY = Math.max(maxY, vertex.getY());
      minZ = min(minZ, vertex.getZ());
      maxZ = Math.max(maxZ, vertex.getZ());
    }

    return new AABB(minX, maxX, minY, maxY, minZ, maxZ);
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(minX);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(maxX);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(minY);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(maxY);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(minZ);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(maxZ);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AABB that = (AABB) o;

    return Double.compare(that.maxX, maxX) == 0 && Double.compare(that.maxY,
                                                                  maxY)
        == 0 && Double.compare(
        that.maxZ,
        maxZ) == 0 && Double.compare(that.minX,
                                     minX) == 0 && Double.compare(that.minY,
                                                                  minY)
        == 0 && Double.compare(
        that.minZ,
        minZ) == 0;

  }

  @Override
  public String toString() {
    return "(" +
        "minX=" + minX +
        ", maxX=" + maxX +
        ", minY=" + minY +
        ", maxY=" + maxY +
        ", minZ=" + minZ +
        ", maxZ=" + maxZ +
        ')';
  }

  /**
   * Determines if the specified point is contained in this axis
   * aligned bounding box.
   * @param point point
   * @return true if the point is within this bounding box
   */
  public boolean contains(Vector3D point) {
    return contains(point.getX(), point.getY(), point.getZ());
  }

  /**
   * Determines if the specified point is contained in this
   * axis aligned bounding box.
   * @param x x-coordinate
   * @param y y-coordinate
   * @param z z-coordinate
   * @return true if the point is in the bounding box
   */
  public boolean contains(double x, double y, double z) {
    return x >= minX &&
        x <= maxX &&
        y >= minY &&
        y <= maxY &&
        z >= minZ &&
        z <= maxZ;
  }


  /**
   * Determines if another axis aligned bounding box intersects with
   * this one.
   * @param other The other bounding box
   * @return true if the bounding box intersects with the other bounding box
   */
  public boolean intersects(AABB other) {
    return maxX > other.minX &&
        minX < other.maxX &&
        maxY > other.minY &&
        minY < other.maxY &&
        maxZ > other.minZ &&
        minZ < other.maxZ;
  }


  /**
   * Returns the minimum x
   * @return the minimum x-coordinate
   */
  public double getMinX() {
    return minX;
  }


  /**
   * Returns the maximum x
   * @return the maximum x-coordinate
   */
  public double getMaxX() {
    return maxX;
  }


  /**
   * Returns the minimum y
   * @return the minimum y-coordinate
   */
  public double getMinY() {
    return minY;
  }


  /**
   * Returns the maximum y
   * @return the maximum y-coordinate
   */
  public double getMaxY() {
    return maxY;
  }


  /**
   * Returns the minimum z
   * @return the minimum z-coordinate
   */
  public double getMinZ() {
    return minZ;
  }


  /**
   * @return the maximum z-coordinate
   */
  public double getMaxZ() {
    return maxZ;
  }


  /**
   * Translates the <code>AABB</code> over the specified distance.
   * @param v translation vector
   * @return the translated bounding box
   */
  public AABB translate(Vector3D v) {
    return translate(v.getX(), v.getY(), v.getZ());
  }


  /**
   * Translates the <code>AABB</code> over the specified distance.
   * @param x translation in x-direction
   * @param y translation in y-direction
   * @param z translation in z-direction
   * @return translated bounding box
   */
  public AABB translate(double x, double y, double z) {
    return new AABB(
        minX + x,
        maxX + x,
        minY + y,
        maxY + y,
        minZ + z,
        maxZ + z
    );
  }


  /**
   * Grows the box around its center by applying the given scale to its lengths
   *
   * @param scale scale to grow
   * @return grown bounding box
   */
  public AABB grow(double scale) {
    return grow(scale, scale, scale);
  }


  /**
   * Grows the box around its center by applying the given scales to its
   * lengths
   *
   * @param sx scale factor along the x-axis
   * @param sy scale factor along the y-axis
   * @param sz scale factor along the z-axis
   * @return grown bounding box
   */
  public AABB grow(double sx, double sy, double sz) {
    double dx = getLengthX() * sx / 2.0;
    double dy = getLengthY() * sy / 2.0;
    double dz = getLengthZ() * sz / 2.0;

    double cx = getCenterX();
    double cy = getCenterY();
    double cz = getCenterZ();

    return new AABB(
        cx - dx,
        cx + dx,
        cy - dy,
        cy + dy,
        cz - dz,
        cz + dz);
  }


  /**
   * Returns the length along the x-axis.
   * @return maxX - minX
   */
  public double getLengthX() {
    return maxX - minX;
  }


  /**
   * Returns the length along the y-axis.
   * @return maxY - minY
   */
  public double getLengthY() {
    return maxY - minY;
  }


  /**
   * Returns the length along the z-axis.
   * @return maxZ-minZ
   */
  public double getLengthZ() {
    return maxZ - minZ;
  }


  /**
   * Returns the x-coordinate of the center of the box.
   * @return x-coordinate
   */
  public double getCenterX() {
    return minX / 2.0 + maxX / 2.0;
  }


  /**
   * Returns the y-coordinate of the center of the box.
   * @return y-coordinate
   */
  public double getCenterY() {
    return minY / 2.0 + maxY / 2.0;
  }


  /**
   * Returns the z-coordinate of the center of the box.
   * @return z-coordinate
   */
  public double getCenterZ() {
    return minZ / 2.0 + maxZ / 2.0;
  }


  /**
   * Returns the union of this box with another.
   * @param other other box
   * @return the union
   */
  public AABB union(AABB other) {
    if (contains(other)) {
      return this;
    } else if (other.contains(this)) {
      return other;
    } else {
      return new AABB(min(minX, other.minX),
                      max(maxX, other.maxX),
                      min(minY, other.minY),
                      max(maxY, other.maxY),
                      min(minZ, other.minZ),
                      max(maxZ, other.maxZ));
    }
  }


  /**
   * Determines if another axis aligned bounding box is contained within
   * this one.
   * @param other The other bounding box
   * @return true if the other box is contained in this box
   */
  public boolean contains(AABB other) {
    return contains(other.minX, other.minY, other.minZ) &&
        contains(other.maxX, other.maxY, other.maxZ);
  }
}
