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
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

import thorwin.math.Vector3D;

/**
 * Triangle in 3-dimensional space
 */
public final class Triangle3D implements Iterable<Vector3D>, Serializable{

  /**
   * Constant for 1/3. Used instead of a division by 3.
   */
  private static final double ONE_THIRD = 1.0 / 3.0;

  /**
   * Epsilon used in triangle/triangle intersection.
   */
  private static final double EPSILON = 1.0E-12;

  private final Vector3D p1;
  private final Vector3D p2;
  private final Vector3D p3;

  /**
   * Creates a 3D triangle.
   *
   * @param x1 x-coordinate of point 1
   * @param y1 y-coordinate of point 1
   * @param z1 z-coordinate of point 1
   * @param x2 x-coordinate of point 2
   * @param y2 y-coordinate of point 2
   * @param z2 z-coordinate of point 2
   * @param x3 x-coordinate of point 3
   * @param y3 y-coordinate of point 3
   * @param z3 z-coordinate of point 3
   */
  public Triangle3D(double x1,
                    double y1,
                    double z1,
                    double x2,
                    double y2,
                    double z2,
                    double x3,
                    double y3,
                    double z3) {
    this(new Vector3D(x1, y1, z1),
         new Vector3D(x2, y2, z2),
         new Vector3D(x3, y3, z3));
  }

  /**
   * Creates a 3D triangle.
   *
   * @param p1 point 1
   * @param p2 point 2
   * @param p3 point 3
   */
  public Triangle3D(Vector3D p1, Vector3D p2, Vector3D p3) {
    super();
    this.p1 = p1;
    this.p2 = p2;
    this.p3 = p3;
  }

  /**
   * Calculates if this triangle intersects with another triangle.
   * @param other the other triangle
   * @return true if the triangle intersects
   */
  public boolean intersects(Triangle3D other) {
    return intersects(getP1(),
                      getP2(),
                      getP3(),
                      other.getP1(),
                      other.getP2(),
                      other.getP3(),
                      EPSILON);
  }

  /**
   * Checks if two triangles intersect. This intersection method uses the
   * triangle/triangle intersection algorithm by Tomas Moller.
   *
   * @param v1      triangle 1, vertex 1
   * @param v2      triangle 1, vertex 2
   * @param v3      triangle 1, vertex 3
   * @param u1      triangle 2, vertex 1
   * @param u2      triangle 2, vertex 2
   * @param u3      triangle 2, vertex 3
   * @param epsilon coplanar epsilon
   * @return true if the triangles intersect
   */
  @SuppressWarnings("ConstantConditions")
  public static boolean intersects(Vector3D v1,
                                   Vector3D v2,
                                   Vector3D v3,
                                   Vector3D u1,
                                   Vector3D u2,
                                   Vector3D u3,
                                   double epsilon) {
    Vector3D normal1 = planeNormal(v1, v2, v3);

    double d1 = -normal1.multiply(v1);
    double du1 = normal1.multiply(u1) + d1;
    double du2 = normal1.multiply(u2) + d1;
    double du3 = normal1.multiply(u3) + d1;

    if (Math.abs(du1) < epsilon) {
      du1 = 0.0;
    }
    if (Math.abs(du2) < epsilon) {
      du2 = 0.0;
    }
    if (Math.abs(du3) < epsilon) {
      du3 = 0.0;
    }

    double du1du2 = du1 * du2;
    double du1du3 = du1 * du3;
    if (du1du2 > 0.0 && du1du3 > 0.0) {
      return false;
    }

    Vector3D normal2 = planeNormal(u1, u2, u3);

    double d2 = -normal2.multiply(u1);
    double dv1 = normal2.multiply(v1) + d2;
    double dv2 = normal2.multiply(v2) + d2;
    double dv3 = normal2.multiply(v3) + d2;

    if (Math.abs(dv1) < epsilon) {
      dv1 = 0.0;
    }
    if (Math.abs(dv2) < epsilon) {
      dv2 = 0.0;
    }
    if (Math.abs(dv3) < epsilon) {
      dv3 = 0.0;
    }

    double dv1dv2 = dv1 * dv2;
    double dv1dv3 = dv1 * dv3;

    if (dv1dv2 > 0.0 && dv1dv3 > 0.0) {
      return false;
    }

    Vector3D D = normal1.cross(normal2);
    double max = Math.abs(D.get(0));
    int index = 0;
    double bb = Math.abs(D.get(1));
    double cc = Math.abs(D.get(2));
    if (bb > max) {
      max = bb;
      index = 1;
    }
    if (cc > max) {
      index = 2;
    }

    double vp1 = v1.get(index);
    double vp2 = v2.get(index);
    double vp3 = v3.get(index);
    double up1 = u1.get(index);
    double up2 = u2.get(index);
    double up3 = u3.get(index);

    double x0, x1;
    double a, b, c;

    if (dv1dv2 > 0.0) {
      a = vp3;
      b = (vp1 - vp3) * dv3;
      c = (vp2 - vp3) * dv3;
      x0 = dv3 - dv1;
      x1 = dv3 - dv2;
    } else if (dv1dv3 > 0.0) {
      a = vp2;
      b = (vp1 - vp2) * dv2;
      c = (vp3 - vp2) * dv2;
      x0 = dv2 - dv1;
      x1 = dv2 - dv3;
    } else if (dv2 * dv3 > 0.0 || dv1 != 0.0) {
      a = vp1;
      b = (vp2 - vp1) * dv1;
      c = (vp3 - vp1) * dv1;
      x0 = dv1 - dv2;
      x1 = dv1 - dv3;
    } else if (dv2 != 0.0) {
      a = vp2;
      b = (vp1 - vp2) * dv2;
      c = (vp3 - vp2) * dv2;
      x0 = dv2 - dv1;
      x1 = dv2 - dv3;
    } else if (dv3 != 0.0) {
      a = vp3;
      b = (vp1 - vp3) * dv3;
      c = (vp2 - vp3) * dv3;
      x0 = dv3 - dv1;
      x1 = dv3 - dv2;
    } else {
      return Triangle3D.intersectsCoplanar(normal1, v1, v2, v3, u1, u2, u3);
    }


    double y0, y1;
    double d, e, f;

    if (du1du2 > 0.0) {
      d = up3;
      e = (up1 - up3) * du3;
      f = (up2 - up3) * du3;
      y0 = du3 - du1;
      y1 = du3 - du2;
    } else if (du1du3 > 0.0) {
      d = up2;
      e = (up1 - up2) * du2;
      f = (up3 - up2) * du2;
      y0 = du2 - du1;
      y1 = du2 - du3;
    } else if (du2 * du3 > 0.0 || du1 != 0.0) {
      d = up1;
      e = (up2 - up1) * du1;
      f = (up3 - up1) * du1;
      y0 = du1 - du2;
      y1 = du1 - du3;
    } else if (du2 != 0.0) {
      d = up2;
      e = (up1 - up2) * du2;
      f = (up3 - up2) * du2;
      y0 = du2 - du1;
      y1 = du2 - du3;
    } else if (du3 != 0.0) {
      d = up3;
      e = (up1 - up3) * du3;
      f = (up2 - up3) * du3;
      y0 = du3 - du1;
      y1 = du3 - du2;
    } else {
      return Triangle3D.intersectsCoplanar(normal1, v1, v2, v3, u1, u2, u3);
    }

    double[] isect1 = new double[2];
    double[] isect2 = new double[2];

    double xx = x0 * x1;
    double yy = y0 * y1;
    double xxyy = xx * yy;
    double tmp = a * xxyy;

    isect1[0] = tmp + b * x1 * yy;
    isect1[1] = tmp + c * x0 * yy;

    tmp = d * xxyy;
    isect2[0] = tmp + e * xx * y1;
    isect2[1] = tmp + f * xx * y0;
    if (isect1[0] > isect1[1]) {
      double swap1 = isect1[0];
      isect1[0] = isect1[1];
      isect1[1] = swap1;
    }
    if (isect2[0] > isect2[1]) {
      double swap = isect2[0];
      isect2[0] = isect2[1];
      isect2[1] = swap;
    }
    return !(isect1[1] < isect2[0] || isect2[1] < isect1[0]);
  }

  /**
   * Returns point <i>P1</i>
   *
   * @return vector
   */
  public Vector3D getP1() {
    return p1;
  }

  /**
   * Returns point <i>P2</i>
   *
   * @return vector
   */
  public Vector3D getP2() {
    return p2;
  }

  /**
   * Returns point <i>P3</i>
   *
   * @return vector
   */
  public Vector3D getP3() {
    return p3;
  }

  /**
   * Calculates the normal vector of the triangle's plane.
   * @param p1 triangle vertex 1
   * @param p2 triangle vertex 2
   * @param p3 triangle vertex 3
   * @return the plane normal vector
   */
  private static Vector3D planeNormal(Vector3D p1,
                                      Vector3D p2,
                                      Vector3D p3) {
    Vector3D edge1 = p2.subtract(p1);
    Vector3D edge2 = p3.subtract(p1);
    return edge1.cross(edge2);
  }

  /**
   * Part of the Thomas Moller triangle/triangle intersection algorithm. Checks
   * intersection in coplanar triangles.
   *
   * @param n  plane normal
   * @param v1 triangle 1, vertex 1
   * @param v2 triangle 1, vertex 2
   * @param v3 triangle 1, vertex 3
   * @param u1 triangle 2, vertex 1
   * @param u2 triangle 2, vertex 2
   * @param u3 triangle 2, vertex 3
   * @return true if the triangles intersect
   */
  private static boolean intersectsCoplanar(Vector3D n,
                                            Vector3D v1,
                                            Vector3D v2,
                                            Vector3D v3,
                                            Vector3D u1,
                                            Vector3D u2,
                                            Vector3D u3) {
    int i0;
    int i1;
    Vector3D A = new Vector3D(Math.abs(n.getX()),
                              Math.abs(n.getY()),
                              Math.abs(n.getZ()));
    if (A.getX() > A.getY()) {
      if (A.getX() > A.getZ()) {
        i0 = 1;
        i1 = 2;
      } else {
        i0 = 0;
        i1 = 1;
      }
    } else if (A.getZ() > A.getY()) {
      i0 = 0;
      i1 = 1;
    } else {
      i0 = 0;
      i1 = 2;
    }
    return
        edgeAgainstTriEdges(v2, v1, u1, u2, u3, i0, i1) ||
        edgeAgainstTriEdges(v3, v2, u1, u2, u3, i0, i1) ||
        edgeAgainstTriEdges(v1, v3, u1, u2, u3, i0, i1) ||
        pointInTriangle(v1, u1, u2, u3, i0, i1) ||
        pointInTriangle(u1, v1, v2, v3, i0, i1);
  }

  /**
   * Test the edge against triangle edges. Part of the triangle/triangle
   * intersection algorithm.
   * @param v1 edge vertex 1
   * @param v2 edge vertex 2
   * @param u1 triangle vertex 1
   * @param u2 triangle vertex 2
   * @param u3 triangle vertex 3
   * @param i0 i0
   * @param i1 i1
   * @return true if intersecting
   */
  private static boolean edgeAgainstTriEdges(Vector3D v1,
                                             Vector3D v2,
                                             Vector3D u1,
                                             Vector3D u2,
                                             Vector3D u3,
                                             int i0,
                                             int i1) {
    double e;
    double ax = v1.get(i0) - v2.get(i0);
    double ay = v1.get(i1) - v2.get(i1);
    double bx = u1.get(i0) - u2.get(i0);
    double by = u1.get(i1) - u2.get(i1);
    double cx = v2.get(i0) - u1.get(i0);
    double cy = v2.get(i1) - u1.get(i1);
    double f = ay * bx - ax * by;
    double d = by * cx - bx * cy;
    if (f > 0.0 && d >= 0.0 && d <= f || f < 0.0 && d <= 0.0 && d >= f) {
      e = ax * cy - ay * cx;
      if (f > 0.0 ? e >= 0.0 && e <= f : e <= 0.0 && e >= f) {
        return true;
      }
    }
    bx = u2.get(i0) - u3.get(i0);
    by = u2.get(i1) - u3.get(i1);
    cx = v2.get(i0) - u2.get(i0);
    cy = v2.get(i1) - u2.get(i1);
    f = ay * bx - ax * by;
    d = by * cx - bx * cy;
    if (f > 0.0 && d >= 0.0 && d <= f || f < 0.0 && d <= 0.0 && d >= f) {
      e = ax * cy - ay * cx;
      if (f > 0.0 ? e >= 0.0 && e <= f : e <= 0.0 && e >= f) {
        return true;
      }
    }
    bx = u3.get(i0) - u1.get(i0);
    by = u3.get(i1) - u1.get(i1);
    cx = v2.get(i0) - u3.get(i0);
    cy = v2.get(i1) - u3.get(i1);
    f = ay * bx - ax * by;
    d = by * cx - bx * cy;
    if (f > 0.0 && d >= 0.0 && d <= f || f < 0.0 && d <= 0.0 && d >= f) {
      e = ax * cy - ay * cx;
      if (f > 0.0 ? e >= 0.0 && e <= f : e <= 0.0 && e >= f) {
        return true;
      }
    }
    return false;
  }

  /**
   * Tests the point in the triangle. Part of the triangle/triangle
   * intersection algorithm.
   * @param V0 point
   * @param U0 triangle edge 1
   * @param U1 triangle edge 2
   * @param U2 triangle edge 3
   * @param i0 i0
   * @param i1 i1
   * @return true if point in triangle
   */
  private static boolean pointInTriangle(Vector3D V0,
                                         Vector3D U0,
                                         Vector3D U1,
                                         Vector3D U2,
                                         int i0,
                                         int i1) {
    double a = U1.get(i1) - U0.get(i1);
    double b = -U1.get(i0) - U0.get(i0);
    double c = (-a) * U0.get(i0) - b * U0.get(i1);
    double d0 = a * V0.get(i0) + b * V0.get(i1) + c;
    a = U2.get(i1) - U1.get(i1);
    b = -U2.get(i0) - U1.get(i0);
    c = (-a) * U1.get(i0) - b * U1.get(i1);
    double d1 = a * V0.get(i0) + b * V0.get(i1) + c;
    a = U0.get(i1) - U2.get(i1);
    b = -U0.get(i0) - U2.get(i0);
    c = (-a) * U2.get(i0) - b * U2.get(i1);
    double d2 = a * V0.get(i0) + b * V0.get(i1) + c;
    return d0 * d1 > 0.0 && d0 * d2 > 0.0;
  }

  /**
   * Returns a stream of the points in this triangle: <i>P1</i>, <i>P2</i>,
   * <i>P3</i>.
   *
   * @return stream of vectors
   */
  public Stream<Vector3D> stream() {
    return Stream.of(getP1(), getP2(), getP3());
  }

  /**
   * Transforms the triangle.
   *
   * @param transformation transformation
   * @return transformed triangle
   */
  public Triangle3D transform(Transform3D transformation) {
    Vector3D p1 = transformation.transform(this.p1);
    Vector3D p2 = transformation.transform(this.p2);
    Vector3D p3 = transformation.transform(this.p3);

    return new Triangle3D(p1, p2, p3);
  }

  /**
   * Splits the triangle along the specified plane into three triangles
   *
   * @param plane plane
   * @return an array of triangles (length 3, or 1 if there is no intersection)
   */
  public Triangle3D[] split(Plane3D plane) {
    // get the edges
    Segment3D edge1 = new Segment3D(p1, p2);
    Segment3D edge2 = new Segment3D(p2, p3);
    Segment3D edge3 = new Segment3D(p3, p1);

    Optional<Vector3D> i1 = edge1.intersection(plane);
    Optional<Vector3D> i2 = edge2.intersection(plane);
    Optional<Vector3D> i3 = edge3.intersection(plane);


    // count the number of intersections
    int intersections = 0;

    if (i1.isPresent()) intersections++;
    if (i2.isPresent()) intersections++;
    if (i3.isPresent()) intersections++;

    // with 0 or 1 intersections, triangle can not be split
    if (intersections > 1) {
      // edge 1 was intersected
      if (i1.isPresent()) {
        if (i2.isPresent()) {
          // edge 1 & 2 were intersected
          return new Triangle3D[]{new Triangle3D(getP1(),
                                                 i1.get(),
                                                 i2.get()), new Triangle3D(i1.get(),
                                                                           getP2(),
                                                                           i2.get()), new Triangle3D(getP1(),
                                                                                                     i2.get(),
                                                                                                     getP3())};
        } else {
          // edge 1 & 3 were intersected
          return new Triangle3D[]{new Triangle3D(getP1(),
                                                 i1.get(),
                                                 i3.get()), new Triangle3D(i1.get(),
                                                                           getP2(),
                                                                           getP3()), new Triangle3D(i1.get(),
                                                                                                    getP3(),
                                                                                                    i3.get())};
        }
      } else {
        // edge 2 & 3 were intersected
        return new Triangle3D[]{new Triangle3D(getP1(),
                                               getP2(),
                                               i2.get()), new Triangle3D
                                                              (getP1(),
                                                                         i2.get(),
                                                                         i3.get()), new Triangle3D(i2.get(),
                                                                                                   getP3(),
                                                                                                   i3.get())};
      }
    }
    return new Triangle3D[]{this};
  }

  /**
   * Determines if the specified plane intersects with this triangle. The
   * intersection may be in an edge or on a vertex (in which case {@code
   * split()} will not actually result in more triangles).
   *
   * @param plane plane
   * @return true if there is an intersection with the specified plane.
   */
  public boolean intersects(Plane3D plane) {
    // get the edges
    return (new Segment3D(p1, p2).intersection(plane) != null) ||
           (new Segment3D(p2, p3).intersection(plane) != null) ||
           (new Segment3D(p3, p1).intersection(plane) != null);

  }

  /**
   * Returns the plane formed by the tree points of this triangle.
   *
   * @return plane
   */
  public Plane3D plane() {
    Vector3D p1 = getP1();
    Vector3D p2 = getP2();
    Vector3D p3 = getP3();

    // calculate relative vectors needed to calculate the normal
    Vector3D v = p2.subtract(p1);
    Vector3D w = p3.subtract(p1);

    // we choose p1 as the origin and calculate the normal
    return new Plane3D(p1, v.cross(w).normalize());
  }

  /**
   * Calculates the intersection line segment between this triangle and the
   * plane.
   *
   * @param plane plane
   * @return intersection line segment, if any
   */
  public Optional<Segment3D> intersection(Plane3D plane) {

    double d1 = plane.distance(getP1());
    double d2 = plane.distance(getP2());
    double d3 = plane.distance(getP3());

    // edgeAgainstTriangleEdges if all points of triangle  lie on a the same
    // side of the plane,
    // or triangle lies exactly on plane
    double s1 = Math.signum(d1);
    double s2 = Math.signum(d2);
    double s3 = Math.signum(d3);

    if (s1 == s2 && s2 == s3) {
      return Optional.empty();
    }

    if (s1 == s2) {
      Vector3D v1 = plane.intersection(new Line3D(getP2(), getP3())).get();
      Vector3D v2 = plane.intersection(new Line3D(getP3(), getP1())).get();
      return Optional.of(new Segment3D(v1, v2));
    } else if (s2 == s3) {
      Vector3D v1 = plane.intersection(new Line3D(getP3(), getP1())).get();
      Vector3D v2 = plane.intersection(new Line3D(getP1(), getP2())).get();
      return Optional.of(new Segment3D(v1, v2));
    } else {
      Vector3D v1 = plane.intersection(new Line3D(getP1(), getP2())).get();
      Vector3D v2 = plane.intersection(new Line3D(getP2(), getP3())).get();
      return Optional.of(new Segment3D(v1, v2));
    }
  }

  /**
   * Calculates the surface area of this triangle
   *
   * @return area
   */
  public double area() {
    Vector3D v = getP2().subtract(getP1());
    Vector3D w = getP3().subtract(getP1());

    return v.cross(w).length() * 0.5;
  }

  /**
   * Calculates the center point of this triangle
   *
   * @return center point
   */
  public Vector3D center() {
    return getP1().add(getP2()).add(getP3()).multiply(ONE_THIRD);
  }

  /**
   * Subdivides this triangle into 4 other triangles.
   *
   * @return triangles
   */
  public Triangle3D[] subdivide() {
    return subdivide(null);
  }

  /**
   * Subdivides this triangle into 4 other triangles.
   *
   * @param transform transformation to apply (may be null)
   * @return triangles
   */
  public Triangle3D[] subdivide(Transform3D transform) {

    Triangle3D[] triangles = new Triangle3D[4];

    Vector3D a = getP1();
    Vector3D b = getP2();
    Vector3D c = getP3();

    Vector3D midAB = a.mid(b);
    Vector3D midBC = b.mid(c);
    Vector3D midAC = a.mid(c);

    if (transform != null) {
      midAB = transform.transform(midAB);
      midBC = transform.transform(midBC);
      midAC = transform.transform(midAC);
    }

    triangles[0] = new Triangle3D(a, midAB, midAC);
    triangles[1] = new Triangle3D(midAB, b, midBC);
    triangles[2] = new Triangle3D(midAB, midBC, midAC);
    triangles[3] = new Triangle3D(midAC, midBC, c);

    return triangles;
  }

  /**
   * Subdivides this triangle in other triangles by dividing the triangles into
   * 4 other triangles, in a number of iterations
   *
   * @param iterations Number of iterations to perform subdivision
   * @param transform  The toAffine3D to generate to all new vertices (may be
   *                   null).
   * @return array of triangles (dimension is 4^iterations)
   */
  public Triangle3D[] subdivide(int iterations, Transform3D transform) {
    if (iterations < 0)
      throw new IllegalArgumentException("number of iterations should " + "be" +
                                         " positive");

    if (iterations == 0) {
      return new Triangle3D[]{this};
    } else {
      Triangle3D[] result = new Triangle3D[(int) Math.pow(4, iterations)];

      int pos = 0;
      for (Triangle3D recursive : subdivide(iterations - 1, transform)) {
        System.arraycopy(recursive.subdivide(transform), 0, result, pos, 4);
        pos += 4;
      }

      return result;
    }
  }

  @Override
  public int hashCode() {
    int result = p1 != null ? p1.hashCode() : 0;
    result = 31 * result + (p2 != null ? p2.hashCode() : 0);
    result = 31 * result + (p3 != null ? p3.hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Triangle3D vector3Ds = (Triangle3D) o;

    return !(p1 != null ? !p1.equals(vector3Ds.p1) : vector3Ds.p1 != null) &&
           !(p2 != null ? !p2.equals(vector3Ds.p2) : vector3Ds.p2 != null) &&
           !(p3 != null ? !p3.equals(vector3Ds.p3) : vector3Ds.p3 != null);

  }

  @Override
  public String toString() {
    return "Triangle3D{" +
           "p1=" + p1 +
           ", p2=" + p2 +
           ", p3=" + p3 +
           '}';
  }

  @Override
  public Iterator<Vector3D> iterator() {
    return new Iterator<Vector3D>() {

      int index = 0;

      @Override
      public boolean hasNext() {
        return index < 3;
      }

      @Override
      public Vector3D next() {
        switch (index++) {
          case 0:
            return getP1();
          case 1:
            return getP2();
          case 2:
            return getP3();

          default:
            throw new IllegalStateException();
        }
      }
    };
  }
}
