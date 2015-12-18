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

import thorwin.math.Affine3D;
import thorwin.math.Matrix3x3;
import thorwin.math.Vector3D;

import java.io.Serializable;


/**
 * Cuboid in 3-dimensional space.
 */
public final class Cuboid3D implements Serializable, Volume {

  /**
   * The center of the cuboid
   */
  private final Vector3D origin;

  /**
   * The orientation of the cuboid
   */
  private final Quaternion orientation;

  /**
   * The dimension of the cuboid
   */
  private final Vector3D halfSize;

  /**
   * Constructs a cuboid
   *
   * @param origin      center of the cuboid
   * @param orientation orientation of the cuboid
   * @param size        dimensions of the cuboid
   */
  public Cuboid3D(Vector3D origin, Quaternion orientation, Vector3D size) {
    super();
    if (origin == null)
      throw new IllegalArgumentException("origin should not be null");
    if (orientation == null)
      throw new IllegalArgumentException("orientation should not be null");
    if (size == null)
      throw new IllegalArgumentException("getDimension should not be null");
    this.origin = origin;
    this.orientation = orientation;
    this.halfSize = size.multiply(0.5);
  }

  /**
   * Returns the center of the cuboid
   * @return a vector from [0,0,0] to the origin of the cuboid
   */
  public Vector3D getOrigin() {
    return origin;
  }

  /**
   * Returns the orientation of the cuboid
   * @return a quaternion describing the orientation
   */
  public Quaternion getOrientation() {
    return orientation;
  }


  /**
   * Calculates the surface area of the cuboid
   * @return the surface area of the cuboid
   */
  public double surface() {
    return halfSize.getX() * halfSize.getY() * 8.0 +
           halfSize.getX() * halfSize.getZ() * 8.0 +
           halfSize.getY() * halfSize.getZ() * 8.0;
  }


  /**
   * Calculates the volume of the cuboid
   * @return volume of the cuboid
   */
  public double volume() {
    return 8.0 * halfSize.getX() * halfSize.getY() * halfSize.getZ();
  }


  /**
   * Determines if this cuboid contains the specified point
   * @param point the vector from [0,0,0] to the point
   * @return true if the point is contained by this cuboid
   */
  public boolean contains(Vector3D point) {

    Vector3D point0 = point;

    // translate & rotate the point into the cuboid coordinate system
    // so we can test the point in the same way as we would test a
    // axis aligned box (AABB).
    point0 = point0.subtract(origin);
    point0 = orientation.invert().toMatrix3x3().multiply(point0);

    return point0.getX() >= -halfSize.getX() &&
        point0.getX() <= halfSize.getX() &&
        point0.getY() >= -halfSize.getY() &&
        point0.getY() <= halfSize.getY() &&
        point0.getZ() >= -halfSize.getZ() &&
        point0.getZ() <= halfSize.getZ();
  }


  @Override
  public int hashCode() {
    int result = origin.hashCode();
    result = 31 * result + orientation.hashCode();
    result = 31 * result + halfSize.hashCode();
    return result;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Cuboid3D cuboid3D = (Cuboid3D) o;

    return orientation.equals(cuboid3D.orientation) && origin.equals
        (cuboid3D.origin) && halfSize.equals(
        cuboid3D.halfSize);

  }


  @Override
  public String toString() {
    return "Cuboid3D{" +
        "origin=" + origin +
        ", orientation=" + orientation +
        ", size=" + getSize() +
        '}';
  }


  /**
   * Returns the size of the cuboid
   * @return a vector that holds the 3-dimensions of the cuboid
   */
  public Vector3D getSize() {
    return halfSize.multiply(2.0);
  }


  /**
   * Returns all vertices of this cuboid.
   * @return an array of the vertices
   */
  public Vector3D[] vertices() {

    final Matrix3x3 m = orientation.toMatrix3x3();

    Transform3D transform =
        (x1, y1, z1) ->  m.multiply(new Vector3D(x1,y1,z1));

    Vector3D halfSize = getHalfSize();

    double x = halfSize.getX();
    double y = halfSize.getY();
    double z = halfSize.getZ();

    return new Vector3D[]{
        origin.add(transform.transform(x, y, z)),
        origin.add(transform.transform(x, y, -z)),
        origin.add(transform.transform(x, -y, z)),
        origin.add(transform.transform(x, -y, -z)),
        origin.add(transform.transform(-x, y, z)),
        origin.add(transform.transform(-x, y, -z)),
        origin.add(transform.transform(-x, -y, z)),
        origin.add(transform.transform(-x, -y, -z))
    };
  }

  /**
   * Return half the size of this cuboid
   * @return vector that holds the half-size of the 3 dimensions
   */
  public Vector3D getHalfSize() {
    return halfSize;
  }

  /**
   * Returns the axis aligned bounds of the box.
   * @return bounds
   */
  public AABB getBounds() {

    final Affine3D m = orientation.toAffine3D(origin);

    Transform3D transform = m::multiply;

    Vector3D halfSize = getHalfSize();

    double x = halfSize.getX();
    double y = halfSize.getY();
    double z = halfSize.getZ();

    return AABB.valueOf(origin.add(transform.transform(x, y, z)),
                        origin.add(transform.transform(x, y, -z)),
                        origin.add(transform.transform(x, -y, z)),
                        origin.add(transform.transform(x, -y, -z)),
                        origin.add(transform.transform(-x, y, z)),
                        origin.add(transform.transform(-x, y, -z)),
                        origin.add(transform.transform(-x, -y, z)),
                        origin.add(transform.transform(-x, -y, -z)));
  }




}
