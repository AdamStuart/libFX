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


/**
 * Class that holds all results from a great-circle distance
 * calculation on a sphere/spheroid.
 */
public final class GreatCircleDistance implements Serializable {
  private static final long serialVersionUID = -4007720642537820363L;

  private final double distance;

  private final double forwardAzimuth;

  private final double reverseAzimuth;


  /**
   * Construct a vincenty calculation result.
   *
   * @param distance       distance (in meters)
   * @param forwardAzimuth forward azimuth in radians
   * @param reverseAzimuth reverse azimuth in radians
   */
  public GreatCircleDistance(double distance,
                             double forwardAzimuth,
                             double reverseAzimuth) {
    super();
    this.distance = distance;
    this.forwardAzimuth = forwardAzimuth;
    this.reverseAzimuth = reverseAzimuth;
  }


  /**
   * Returns the distance (in meters)
   *
   * @return distance
   */
  public double getDistance() {
    return distance;
  }


  /**
   * Returns the forward azimuth or NaN if it was not calculated
   *
   * @return forward azimuth or NaN
   */
  public double getForwardAzimuth() {
    return forwardAzimuth;
  }


  /**
   * Returns the reverse azimuth of NaN if it was not calculated
   *
   * @return reverse azimuth.
   */
  public double getReverseAzimuth() {
    return reverseAzimuth;
  }
}
