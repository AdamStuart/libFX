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

/**
 * Provides a variety of geometry related classes.
 * <p>
 * In particular this package provides classes for representing 2- and
 * 3-dimensional primitives, such as lines, line-segments, cuboids, spheres,
 * polygons, triangles and planes.
 * <p>
 * Utility classes for processing 3-dimensional data is provided in the form
 * of the {@code AABB} (Axis-Aligned-Bounding-Box) and {@code Octree} classes.
 * <p>
 * Three implementations for 3-dimensional rotations are available: Euler,
 * axis/angle and Quaternions.
 * <p>
 * Real-world coordinate systems are supported: {@code Geodetic}, {@code Ecef}
 * (Earth-Centered-Earth-Fixed) and {@code Enu} (East-North-Up).
 */
package thorwin.math.geo;
