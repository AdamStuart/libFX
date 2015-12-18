/******************************************************************************
 * Copyright (C) 2015 Sebastiaan R. Hogenbirk                                 *
 * * This program is free software: you can redistribute it and/or modify * it
 * under the terms of the GNU Lesser General Public License as published by* the
 * Free Software Foundation, either version 3 of the License, or * (at your
 * option) any later version. * * This program is distributed in the hope that
 * it will be useful, * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *
 * GNU Lesser General Public License for more details. * * You should have
 * received a copy of the GNU Lesser General Public License * along with this
 * program.  If not, see <http://www.gnu.org/licenses/>. *
 ******************************************************************************/

package thorwin.math;

import java.util.ServiceLoader;


/**
 * Context for matrix operations. The {@code MatrixContext} allows the
 * construction of {@code Matrix} implementations based on matrix dimensions,
 * system resources (i.e. native libraries, GPU), system properties and
 * available matrix implementations.
 *
 */
class MatrixContext {

  /**
   * The MatrixContext instance used to create matrices.
   */
  private static final MatrixContext INSTANCE;
  static {
    MatrixContext context = new MatrixContext();

    for (MatrixContext service : ServiceLoader.load(MatrixContext.class)) {
      context = service;
    }
    INSTANCE = context;
  }

  static final long RECURSIVE =
      Integer.getInteger("thorwin.math.matrix.recursive", 128);


  /**
   * Returns the {@code MatrixContext} used for constructing new {@code Matrix}
   * instances.
   */
  public static MatrixContext getInstance() {
    return INSTANCE;
  }


  /**
   * Constructs a new builder using data from the source matrix.
   * @param source source matrix
   * @return matrix builder
   */
  public MatrixBuilder create(Matrix source) {
    if (source instanceof PackedMatrix) {
      return ((PackedMatrix) source).toBuilder();
    }
    if (source instanceof BlockMatrix) {
      return ((BlockMatrix) source).toBuilder();
    }

    MatrixBuilder
        builder =
        MatrixContext.getInstance().create(source.getRowDimension(),
                                           source.getColumnDimension());

    for (int row = 0; row < source.getRowDimension(); row++) {
      for (int column = 0; column < source.getColumnDimension(); column++) {
        builder.set(row, column, source.get(row, column));
      }
    }

    return builder;
  }

  @SuppressWarnings("unchecked")
  public MatrixBuilder create(int rows, int columns) {
    switch (rows) {
      case 1:
        switch (columns) {
          case 1: return new Matrix1x1Builder();
          case 2: return new Matrix1x2Builder();
          case 3: return new Matrix1x3Builder();
          case 4: return new Matrix1x4Builder();
          case 5: return new Matrix1x5Builder();
        }
        break;
      case 2:
        switch (columns) {
          case 1: return new Matrix2x1Builder();
          case 2: return new Matrix2x2Builder();
          case 3: return new Matrix2x3Builder();
          case 4: return new Matrix2x4Builder();
          case 5: return new Matrix2x5Builder();
        }
        break;
      case 3:
        switch (columns) {
          case 1: return new Matrix3x1Builder();
          case 2: return new Matrix3x2Builder();
          case 3: return new Matrix3x3Builder();
          case 4: return new Matrix3x4Builder();
          case 5: return new Matrix3x5Builder();
        }
        break;
      case 4:
        switch (columns) {
          case 1: return new Matrix4x1Builder();
          case 2: return new Matrix4x2Builder();
          case 3: return new Matrix4x3Builder();
          case 4: return new Matrix4x4Builder();
          case 5: return new Matrix4x5Builder();
        }
        break;
      case 5:
        switch (columns) {
          case 1: return new Matrix5x1Builder();
          case 2: return new Matrix5x2Builder();
          case 3: return new Matrix5x3Builder();
          case 4: return new Matrix5x4Builder();
          case 5: return new Matrix5x5Builder();
        }
        break;
    }

    // If this instance is allowed to use recursive matrices, check if this would
    // be a good candidate.
    if (rows > RECURSIVE || columns > RECURSIVE) {
        return new BlockMatrixBuilder(rows, columns);
    }

    return new PackedMatrixBuilder(rows, columns);
  }

  /**
   * Creates a new matrix builder.
   * @param rows rows in the matrix
   * @param columns columns in the matrix
   * @param packed row-major packed matrix
   * @return matrix
   */
  public MatrixBuilder create(int rows, int columns, double[] packed) {
    if (rows > RECURSIVE || columns > RECURSIVE) {
      return new BlockMatrixBuilder(rows, columns, packed);
    }
    return new PackedMatrixBuilder(rows, columns, packed);
  }
}
