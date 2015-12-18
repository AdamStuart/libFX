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

package thorwin.math.geo.projection;

import thorwin.math.geo.Geodetic;

/**
 * Very simple projection that maps longitude/latitude directly to x/y. This
 * projection is often used to projection raster data-sets (such as NASA's Blue
 * Marble) to an images. This projection inverts the y-coordinate as images'
 * positive y-axis points down.
 */
public final class PlateCarreProjection implements CylindricalProjection {

  private static final long serialVersionUID = 22796377142259847L;

  private final double sx;
  private final double sy;
  private final double tx;
  private final double ty;
  private final double height;

  /**
   * Create a Plate Carre projection.
   *
   * @param width  maximum width
   * @param height maximum height
   */
  public PlateCarreProjection(double width, double height) {
    super();

    this.height = height;

    sx = width / (Math.PI * 2.0);
    tx = width / 2;
    sy = height / Math.PI;
    ty = height / 2;
  }

  @Override
  public double toLatitude(double y) {
    double latitude = (height - y - ty) / sy;
    return Geodetic.isValidLatitude(latitude) ? latitude : Double.NaN;
  }

  @Override
  public double toLongitude(double x) {
    double longitude = (x - tx) / sx;
    return Geodetic.isValidLongitude(longitude) ? longitude : Double.NaN;
  }

  @Override
  public double toY(double latitude) {
    return height - ((latitude * sy) + ty);
  }

  @Override
  public double toX(double longitude) {
    return (longitude * sx) + tx;
  }

  @Override
  public String toString() {
    return "PlateCarreProjection{" + "sx=" + sx + ", sy=" + sy + ", tx=" + tx
        + ", ty=" + ty + ", " +
        "height=" + height + '}';
  }
}
