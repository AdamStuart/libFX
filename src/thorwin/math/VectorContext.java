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

package thorwin.math;

/**
 * Context for vectors calculations. This class is used for implementing
 * custom vector implementations.
 */
class VectorContext {

  private static final VectorContext INSTANCE = new VectorContext();


  /**
   * Returns the vector context instance.
   * @return context
   */
  public static VectorContext getInstance() {
    return INSTANCE;
  }

  /**
   * Constructs a builder for vectors.
   * @param dimension dimension of the vector
   * @return the builder
   */
  public VectorBuilder<?> create(int dimension) {
    switch (dimension) {
      case 2:
        return new Vector2DBuilder();
      case 3:
        return new Vector3DBuilder();
      default:
        return new ArrayVectorBuilder(dimension);
    }
  }

  /**
   * Construct a builder for vectors, and initialize it with the values of the
   * array.
   * @param values initial values of the vector
   * @return the builder
   */
  public VectorBuilder<?> create(double[] values) {
    VectorBuilder<?> builder = create(values.length);

    for (int i = 0; i < values.length; i++) {
      builder.set(i, values[i]);
    }

    return builder;
  }

}
