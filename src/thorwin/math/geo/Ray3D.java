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

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.Serializable;
import java.util.Optional;
import java.util.OptionalDouble;

import thorwin.math.Matrix3x3;
import thorwin.math.Vector3D;

/**
 * A ray consists of an origin and a direction.
 */
public final class Ray3D implements Serializable {

  private static final long serialVersionUID = 156515357184047491L;

  /**
   * Epsilon used in ray/triangle intersection.
   */
  private static final double EPSILON = 1e-12;

  private final Vector3D origin;
  private final Vector3D direction;

  /**
   * Construct a ray between two points.
   *
   * @param origin    origin of the ray
   * @param direction direction of the ray (preferably a normalized vector)
   */
  public Ray3D(Vector3D origin, Vector3D direction) {
    super();

    if (origin == null)
      throw new IllegalArgumentException("origin should not be null");
    if (direction == null)
      throw new IllegalArgumentException("direction should not be null");

    this.origin = origin;
    this.direction = direction;
  }

  /**
   * Returns the point of origin of this ray.
   *
   * @return vector to the point of origin
   */
  public Vector3D getOrigin() {
    return origin;
  }

  /**
   * Returns the normalized direction vector.
   *
   * @return vector that indicates the direction
   */
  public Vector3D getDirection() {
    return direction;
  }

  /**
   * Transforms this ray using a transformation.
   *
   * @param transformation the transformation to apply
   * @return the resulting ray
   */
  public Ray3D transform(Transform3D transformation) {
    Vector3D transformedOrigin = transformation.transform(origin);
    Vector3D transformedDirection = transformation.transform(origin.add(direction)).subtract(transformedOrigin);
    return new Ray3D(transformedOrigin, transformedDirection);
  }

  /**
   * Checks if this ray intersects with a sphere.
   *
   * @param sphere the sphere
   * @return true if the ray intersects the sphere's volume
   */
  public boolean intersects(Sphere3D sphere) {
    return intersectionDistance(sphere).isPresent();
  }

  /**
   * Calculates the intersection distance.
   *
   * @param sphere sphere used in intersection
   * @return the range from the ray's origin to the intersection point on the
   * sphere
   */
  public OptionalDouble intersectionDistance(Sphere3D sphere) {
    Vector3D p1 = origin;
    Vector3D p2 = direction;

    // translate ray's reference points into coordinate space of the sphere
    // (sphere's origin can be considered 0,0,0 after this translate)
    p1 = p1.add(sphere.getX(), sphere.getY(), sphere.getZ());
    p2 = p1.add(p2);

    double a = p1.multiply(p1);
    double b = 2 * p1.multiply(p2);
    double c = p2.multiply(p2) - (sphere.getRadius() * sphere.getRadius());

    double discriminant = (b * b) - (4 * a * c);

    if (discriminant < 0) {
      return OptionalDouble.empty();
    }

    double discriminantSqrt = Math.sqrt(discriminant);
    double q;
    if (b < 0) {
      q = (-b - discriminantSqrt) / 2.0;
    } else {
      q = (-b + discriminantSqrt) / 2.0;
    }

    double t0 = q / a;
    double t1 = c / q;

    if (t0 > t1) {
      double temp = t0;
      t0 = t1;
      t1 = temp;
    }

    if (t1 < 0) {
      return OptionalDouble.empty();
    }

    return OptionalDouble.of((t0 < 0) ? t1 : t0);
  }

  /**
   * Checks if this ray is parallel to a plane.
   *
   * @param plane the plane
   * @return true if the plane is parallel to the ray
   */
  public boolean parallel(Plane3D plane) {
    return intersectionDistance(plane).isPresent();
  }

  /**
   * Calculates the intersection distance to a plane.
   *
   * @param plane the plane
   * @return intersection distance
   */
  public OptionalDouble intersectionDistance(Plane3D plane) {
    Vector3D n = plane.getNormal();
    Vector3D p0 = plane.getOrigin();
    Vector3D l = direction;
    Vector3D l0 = origin;

    double d = l.multiply(n);

    if (d == 0) {
      return OptionalDouble.empty();
    } else {
      return OptionalDouble.of(p0.subtract(l0).multiply(n) / d);
    }
  }

  /**
   * Calculates the intersection point between this ray and a plane.
   *
   * @param plane the plane
   * @return the intersection point
   */
  public Optional<Vector3D> intersection(Plane3D plane) {
    OptionalDouble distance = intersectionDistance(plane);

    // parallel or behind origin
    if (!distance.isPresent() || (distance.getAsDouble() < 0))
      return Optional.empty();

    return Optional.of(origin.add(direction.multiply(distance.getAsDouble())));
  }

  /**
   * Calculates the intersection point between this ray and a bounding box.
   *
   * @param box the box
   * @return the intersection point
   */
  public Optional<Vector3D> intersection(AABB box) {
    OptionalDouble distance = intersectionDistance(box);

    if (!distance.isPresent() || (distance.getAsDouble() < 0))
      return Optional.empty();

    return Optional.of(origin.add(direction.multiply(distance.getAsDouble())));
  }

  /**
   * Calculates the intersection distance to an axis-aligned bounding box. Note
   * that if the box is behind the ray a negative distance is returned. If the
   * ray's origin is in the box, a negative distance will be returned. The
   * negative distance is the distance to the plane of the box that would have
   * been intersected if the ray's origin was outside the box, and the box was
   * the right side of the ray.
   *
   * @param box the box
   * @return the intersection distance
   */
  public OptionalDouble intersectionDistance(AABB box) {

    return intersectionDistance(origin,
                                direction,
                                box.getMinX(),
                                box.getMaxX(),
                                box.getMinY(),
                                box.getMaxY(),
                                box.getMinZ(),
                                box.getMaxZ());
  }

  /**
   * Calculates the intersection distance to an axis-aligned bounding box. Note
   * that if the box is behind the ray a negative distance is returned. If the
   * ray's origin is in the box, a negative distance will be returned. The
   * negative distance is the distance to the plane of the box that would have
   * been intersected if the ray's origin was outside the box, and the box was
   * the right side of the ray.
   *
   * @param origin    origin of the ray
   * @param direction direction of the ray
   * @param minX      bounding box minimum x
   * @param maxX      bounding box maximum x
   * @param minY      bounding box minimum y
   * @param maxY      bounding box maximum y
   * @param minZ      bounding box minimum z
   * @param maxZ      bounding box maximum z
   * @return the intersection distance
   */
  private static OptionalDouble intersectionDistance(Vector3D origin,
                                                     Vector3D direction,
                                                     double minX,
                                                     double maxX,
                                                     double minY,
                                                     double maxY,
                                                     double minZ,
                                                     double maxZ) {

    double x = 1.0f / direction.getX();
    double y = 1.0f / direction.getY();
    double z = 1.0f / direction.getZ();

    double t1 = (minX - origin.getX()) * x;
    double t2 = (maxX - origin.getX()) * x;
    double t3 = (minY - origin.getY()) * y;
    double t4 = (maxY - origin.getY()) * y;
    double t5 = (minZ - origin.getZ()) * z;
    double t6 = (maxZ - origin.getZ()) * z;

    double tmax = min(min(max(t1, t2), max(t3, t4)), max(t5, t6));

    // bounding box is behind us, so we return a negative number
    if (tmax < 0) {
      return OptionalDouble.of(tmax);
    }

    double tmin = max(max(min(t1, t2), min(t3, t4)), min(t5, t6));

    // no intersection
    if (tmin > tmax) {
      return OptionalDouble.empty();
    }

    return OptionalDouble.of(tmin);
  }

  /**
   * Calculates the intersection point between this ray and a cube
   *
   * @param cube cube
   * @return the intersection point
   */
  public Optional<Vector3D> intersection(Cuboid3D cube) {
    OptionalDouble distance = intersectionDistance(cube);

    if (!distance.isPresent() || (distance.getAsDouble() < 0))
      return Optional.empty();

    return Optional.of(origin.add(direction.multiply(distance.getAsDouble())));
  }

  /**
   * Calculates the intersection distance to a cuboid. Note that if the box is
   * behind the ray a negative distance is returned. If the ray's origin is in
   * the cube, a negative distance will be returned. The negative distance is
   * the distance to the plane of the cube that would have been intersected if
   * the ray's origin was outside the cube, and the cube was the right side of
   * the ray.
   *
   * @param cube cube
   * @return the intersection distance
   */
  public OptionalDouble intersectionDistance(Cuboid3D cube) {

    Quaternion rotation = cube.getOrientation().invert();
    Matrix3x3 matrix = rotation.toMatrix3x3();

    // translate & rotate the origin and direction into the cuboid
    // coordinate system
    // so we can test the point in the same way as we would test a
    // axis aligned box (BoundingBox3D).
    Vector3D origin = matrix.multiply(this.origin.subtract(cube.getOrigin()));
    Vector3D direction = matrix.multiply(this.direction);

    Vector3D size = cube.getSize();

    double dx = size.getX() / 2.0;
    double dy = size.getY() / 2.0;
    double dz = size.getZ() / 2.0;

    return intersectionDistance(origin, direction, -dx, dx, -dy, dy, -dz, dz);
  }

  /**
   * Checks if this ray intersects a bounding box.
   *
   * @param box axis-aligned bounding box
   * @return true if the ray intersects the bounding box
   */
  public boolean intersects(AABB box) {
    OptionalDouble distance = intersectionDistance(box);

    return (distance.isPresent() && distance.getAsDouble() > 0);
  }

  /**
   * Calculates the intersection distance to a triangle.
   *
   * @param triangle the triangle
   * @return the intersection distance
   */
  public OptionalDouble intersectionDistance(Triangle3D triangle) {
    return intersectionDistance(triangle, EPSILON);
  }

  /**
   * Calculates the intersection distance to a triangle.
   *
   * @param triangle the triangle
   * @param epsilon  the epsilon to use to determine if this ray is parallel to
   *                 the triangle plane
   * @return the intersection distance
   */
  public OptionalDouble intersectionDistance(Triangle3D triangle,
                                             double epsilon) {
    return intersectionDistance(triangle.getP1(),
                                triangle.getP2(),
                                triangle.getP3(),
                                origin,
                                direction,
                                epsilon);
  }

  /**
   * Calculates the intersection distance between this ray and a triangle using
   * the algorithm by Thomas Moller.
   *
   * @param v1        triangle vertex 1
   * @param v2        triangle vertex 2
   * @param v3        triangle vertex 3
   * @param origin    ray origin
   * @param direction ray direction
   * @param epsilon   epsilon value to determine if ray is in triangle plane
   * @return the intersection distance
   */
  public static OptionalDouble intersectionDistance(Vector3D v1,
                                                    Vector3D v2,
                                                    Vector3D v3,
                                                    Vector3D origin,
                                                    Vector3D direction,
                                                    double epsilon) {

    // the 2 edges that share P1
    Vector3D edge1 = v2.subtract(v1);
    Vector3D edge2 = v3.subtract(v1);

    Vector3D pvec = direction.cross(edge2);
    double det = edge1.multiply(pvec);

    if (det > -epsilon && det < epsilon) {
      return OptionalDouble.empty();
    }

    double idet = 1.0 / det;

    Vector3D tvec = origin.subtract(v1);

    double u = tvec.multiply(pvec) * idet;
    if (u < 0.0 || u > 1.0) {
      return OptionalDouble.empty();
    }

    Vector3D qvec = tvec.cross(edge1);

    double v = direction.multiply(qvec) * idet;

    if (v < 0.0 || (u + v) > 1.0) {
      return OptionalDouble.empty();
    }

    double t = edge2.multiply(qvec) * idet;

    if (t > epsilon) {
      return OptionalDouble.of(t);
    } else {
      return OptionalDouble.empty();
    }
  }

  /**
   * Calculates the intersection distance to a triangle.
   *
   * @param triangle the triangle
   * @return the intersection distance
   */
  public Optional<Vector3D> intersection(Triangle3D triangle) {
    return intersection(triangle, EPSILON);
  }

  /**
   * Calculates the intersection distance to a triangle.
   *
   * @param triangle the triangle
   * @param epsilon  the epsilon to use to determine if this ray is
   *                 parallel to
   *                 the triangle plane
   * @return the intersection distance
   */
  public Optional<Vector3D> intersection(Triangle3D triangle, double epsilon) {
    OptionalDouble distance = intersectionDistance(triangle, epsilon);

    if (distance.isPresent()) {
      return Optional.of(origin.add(direction.multiply(distance.getAsDouble()
      )));
    } else {
      return Optional.empty();
    }
  }

  /**
   * Calculates the intersection distance to a triangle.
   *
   * @param triangle the triangle
   * @return the intersection distance
   */
  public boolean intersects(Triangle3D triangle) {
    return intersects(triangle, EPSILON);
  }

  /**
   * Calculates the intersection distance to a triangle.
   *
   * @param triangle the triangle
   * @param epsilon  the epsilon to use to determine if this ray is parallel to
   *                 the triangle plane
   * @return the intersection distance
   */
  public boolean intersects(Triangle3D triangle, double epsilon) {
    return intersectionDistance(triangle, epsilon).isPresent();
  }

  @Override
  public int hashCode() {
    int result = origin.hashCode();
    result = 31 * result + direction.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if ((obj == null) || (getClass() != obj.getClass())) return false;

    Ray3D ray3D = (Ray3D) obj;

    return direction.equals(ray3D.direction) && origin.equals(ray3D.origin);

  }

  @Override
  public String toString() {
    return "Ray3D{" + "origin=" + origin + ", direction=" + direction + '}';
  }
}
