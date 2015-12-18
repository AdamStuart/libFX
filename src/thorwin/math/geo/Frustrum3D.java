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

import java.io.Serializable;


/**
 * Frustrum
 */
public final class Frustrum3D implements Serializable {

  /**
   * The origin of the frustrum
   */
  private final Vector3D origin;

  /**
   * The viewing direction.
   */
  private final Vector3D direction;

  /**
   * The vector indicating up.
   */
  private final Vector3D up;

  /**
   * The field of view angle.
   */
  private final double fieldOfView;

  /**
   * The ratio between horizontal and vertical edges of the view port.
   */
  private final double ratio;

  /**
   * The distance to the near plane.
   */
  private final double nearPlaneDistance;

  /**
   * The distance to the far plane.
   */
  private final double farPlaneDistance;

  /**
   * The planes of the frustrum.
   */
  private final Plane3D[] planes;

  /**
   * The far rectangle.
   */
  private final Rectangle3D farRectangle;

  /**
   * The near rectangle.
   */
  private final Rectangle3D nearRectangle;

  /**
   * Constructs a frustrum.
   *
   * @param origin            origin of the frustrum (the eye of the user)
   * @param direction         direction of the frustrum
   * @param up                up orientation
   * @param fieldOfView       field of view angle
   * @param ratio             ratio between horizontal and vertical edges of the
   *                          near and far rectangles (viewport)
   * @param nearPlaneDistance distance to the near plane
   * @param farPlaneDistance  distance to the far plane
   */
  public Frustrum3D(Vector3D origin,
                    Vector3D direction,
                    Vector3D up,
                    double fieldOfView,
                    double ratio,
                    double nearPlaneDistance,
                    double farPlaneDistance) {
    super();
    this.origin = origin;
    this.direction = direction.normalize();
    this.up = up.normalize();
    this.fieldOfView = fieldOfView;
    this.ratio = ratio;
    this.nearPlaneDistance = nearPlaneDistance;
    this.farPlaneDistance = farPlaneDistance;

    double nearPlaneHeight = 2 * Math.tan(fieldOfView * 0.5) *
                             nearPlaneDistance;
    double farPlaneHeight = 2 * Math.tan(fieldOfView * 0.5) * farPlaneDistance;
    double nearPlaneWidth = nearPlaneHeight * ratio;
    double farPlaneWidth = farPlaneHeight * ratio;

    Vector3D farPlaneCenter = origin.add(this.direction.multiply
                                                            (farPlaneDistance));
    Vector3D nearPlaneCenter = origin.add(this.direction.multiply
                                                             (nearPlaneDistance));

    Vector3D right = this.up.cross(this.direction);

    nearRectangle = new Rectangle3D(nearPlaneCenter,
                                    this.up,
                                    right,
                                    nearPlaneWidth,
                                    nearPlaneHeight);
    farRectangle = new Rectangle3D(farPlaneCenter,
                                   this.up,
                                   right,
                                   farPlaneWidth,
                                   farPlaneHeight);


    Plane3D rightPlane = new Plane3D(this.origin,
                                     this.up.cross(nearPlaneCenter.add(right
                                                                           .multiply(nearPlaneWidth * 0.5)).subtract(this.origin).normalize()).negate());

    Plane3D leftPlane = new Plane3D(this.origin,
                                    this.up.cross(nearPlaneCenter.add(right
                                                                          .multiply(nearPlaneWidth * -0.5)).subtract(this.origin).normalize()));

    Plane3D topPlane = new Plane3D(this.origin,
                                   right.cross(nearPlaneCenter.add(this.up
                                                                       .multiply(nearPlaneHeight * 0.5)).subtract(this.origin).normalize()));

    Plane3D bottomPlane = new Plane3D(this.origin,
                                      right.cross(nearPlaneCenter.add(this.up
                                                                          .multiply(nearPlaneHeight * -0.5)).subtract(this.origin).normalize()).negate());

    Plane3D nearPlane = new Plane3D(nearPlaneCenter, this.direction);
    Plane3D farPlane = new Plane3D(farPlaneCenter, this.direction.negate());

    planes = new Plane3D[]{topPlane, bottomPlane, leftPlane, rightPlane,
                           nearPlane, farPlane};
  }

  /**
   * Returns the origin of the frustrum
   *
   * @return Vector from [0,0,0] to the origin of the frustrum
   */
  public Vector3D getOrigin() {
    return origin;
  }

  /**
   * Returns the direction normal vector.
   *
   * @return direction vector
   */
  public Vector3D getDirection() {
    return direction;
  }

  /**
   * Returns the up normal vector.
   *
   * @return up orientation
   */
  public Vector3D getUp() {
    return up;
  }

  /**
   * Returns the field of view angle in radians
   *
   * @return field of view angle
   */
  public double getFieldOfView() {
    return fieldOfView;
  }

  /**
   * Returns the view port ratio between width and height
   *
   * @return width/height
   */
  public double getRatio() {
    return ratio;
  }

  /**
   * Returns the distance from the origin to the near-plane
   *
   * @return distance to the near-plane
   */
  public double getNearPlaneDistance() {
    return nearPlaneDistance;
  }

  /**
   * Returns the distance from the origin to the far-plane
   *
   * @return distance to the far plane
   */
  public double getFarPlaneDistance() {
    return farPlaneDistance;
  }

  /**
   * Returns the near plane rectangle.
   *
   * @return rectangle
   */
  public Rectangle3D getNearRectangle() {
    return nearRectangle;
  }

  /**
   * Returns the far plane rectangle
   *
   * @return rectangle
   */
  public Rectangle3D getFarRectangle() {
    return farRectangle;
  }

  /**
   * Returns the left plane
   *
   * @return left plane
   */
  public Plane3D getLeftPlane() {
    return planes[2];
  }

  /**
   * Returns the right plane
   *
   * @return right plane
   */
  public Plane3D getRightPlane() {
    return planes[3];
  }

  /**
   * Returns the top plane
   *
   * @return top plane
   */
  public Plane3D getTopPlane() {
    return planes[0];
  }

  /**
   * Returns the bottom plane
   *
   * @return bottom plane
   */
  public Plane3D getBottomPlane() {
    return planes[1];
  }

  /**
   * Returns the near plane
   *
   * @return near plane
   */
  public Plane3D getNearPlane() {
    return planes[4];
  }

  /**
   * Returns the far plane
   *
   * @return The far plane
   */
  public Plane3D getFarPlane() {
    return planes[5];
  }


  /**
   * Determine if the specified point is contained within this frustrum
   *
   * @param point point to test
   * @return true if the frustrum contains the point
   */
  public boolean contains(Vector3D point) {

    for (Plane3D plane : planes) {
      if (plane.distance(point) < 0) return false;
    }
    return true;
  }

  /**
   * Determine the intersection of a sphere with this frustrum.
   *
   * @param sphere sphere
   * @return intersection
   */
  public Intersection intersects(Sphere3D sphere) {
    return intersects(sphere.getOrigin(), sphere.getRadius());
  }

  /**
   * Determine the intersection of a sphere with this frustrum.
   *
   * @param origin origin of the sphere
   * @param radius radius of the sphere
   * @return intersection
   */
  public Intersection intersects(Vector3D origin, double radius) {

    Intersection intersection = Intersection.INSIDE;
    for (Plane3D plane : planes) {
      double distance = plane.distance(origin);
      if (distance < -radius) {
        return Intersection.OUTSIDE;
      } else if (distance < radius) {
        intersection = Intersection.INTERSECT;
      }
    }
    return intersection;
  }

  /**
   * Determine the intersection with an axis-aligned-bounding-box.
   * This method performs relatively fast, and is a good candidate
   * for coarse intersection elimination tests of other volumes.
   * @param bounds bounding box to test
   * @return intersection with the bounding box
   */
  public Intersection intersects(AABB bounds) {
    Intersection intersection = Intersection.INSIDE;

    for (Plane3D plane : planes) {
      Vector3D normal = plane.getNormal();

      Vector3D p = new Vector3D(normal.getX() >= 0 ? bounds.getMaxX() :
                                bounds.getMinX(),
                                normal.getY() >= 0 ? bounds.getMaxY() :
                                bounds.getMinY(),
                                normal.getZ() >= 0 ? bounds.getMaxZ() :
                                bounds.getMinZ());

      if (plane.distance(p) < 0) {
        return Intersection.OUTSIDE;
      }

      Vector3D n = new Vector3D(normal.getX() >= 0 ? bounds.getMinX() :
                                bounds.getMaxX(),
                                normal.getY() >= 0 ? bounds.getMinY() :
                                bounds.getMaxY(),
                                normal.getZ() >= 0 ? bounds.getMinZ() :
                                bounds.getMaxZ());

      if (plane.distance(n) < 0) {
        intersection = Intersection.INTERSECT;
      }
    }
    return intersection;
  }

}
