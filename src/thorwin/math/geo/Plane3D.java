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

import thorwin.math.Vector3D;

/**
 * A plane in 3-dimensional space
 */
public final class Plane3D implements Serializable {

  private static final long serialVersionUID = -5084925631400955216L;

  private final Vector3D origin;
  private final Vector3D normal;

  /**
   * Constructs a plane
   * @param origin the origin point of the plane
   * @param normal the normal vector perpendicular to the plane
   */
  public Plane3D(Vector3D origin, Vector3D normal) {
    super();
    if (origin == null) {
      throw new IllegalArgumentException("origin should not be null");
    }
    if (normal == null) {
      throw new IllegalArgumentException("normal should not be null");
    }
    this.origin = origin;
    this.normal = normal;
  }

  /**
   * Returns a point on the plane
   * @return the origin
   */
  public Vector3D getOrigin() {
    return origin;
  }

  /**
   * Returns the normal vector perpendicular to the plane
   * @return normal vector
   */
  public Vector3D getNormal() {
    return normal;
  }

  /**
   * Projects the point to the closest point on the plane
   *
   * @param point point to project
   * @return the projected point
   */
  public Vector3D project(Vector3D point) {
    return point.subtract(normal.multiply(distance(point)));
  }

  /**
   * Calculates the shortest distance between this plane and the specified
   * point.
   *
   * @param point point to use in the distance calculation
   * @return distance to the point
   */
  public double distance(Vector3D point) {
    return normal.multiply(point) - normal.multiply(origin);
  }

  /**
   * Returns x-coordinate of intersection with x-axis
   * @return x
   */
  public double interceptX() {
    return normal.multiply(origin) / normal.getX();
  }

  /**
   * Returns y-coordinate of intersection with y-axis
   * @return y
   */
  public double interceptY() {
    return normal.multiply(origin) / normal.getY();
  }

  /**
   * Returns z-coordinate of intersection with z-axis
   * @return z
   */
  public double interceptZ() {
    return normal.multiply(origin) / normal.getZ();
  }

  /**
   * Calculates the intersection point of a ray with this plane
   *
   * @param ray a ray
   * @return intersection point, if any
   */
  public Optional<Vector3D> intersection(Ray3D ray) {
    return ray.intersection(this);
  }

  /**
   * Calculates the intersection point of a line with this plane
   *
   * @param line a line
   * @return intersection point, if any
   */
  public Optional<Vector3D> intersection(Line3D line) {
    return line.intersection(this);
  }

  /**
   * Calculates the intersection point of a line segment with this plane
   *
   * @param segment a segment
   * @return intersection point, if any
   */
  public Optional<Vector3D> intersection(Segment3D segment) {
    return segment.intersection(this);
  }

  @Override
  public int hashCode() {
    int result = origin.hashCode();
    result = 31 * result + normal.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if ((obj == null) || (getClass() != obj.getClass())) return false;

    Plane3D plane3D = (Plane3D) obj;

    return normal.equals(plane3D.normal) && origin.equals(plane3D.origin);

  }

  @Override
  public String toString() {
    return "Plane3D{" + "origin=" + origin + ", normal=" + normal + '}';
  }
}
