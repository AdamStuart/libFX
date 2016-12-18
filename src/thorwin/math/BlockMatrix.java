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

import static java.lang.Integer.min;

import java.util.Arrays;
import java.util.stream.IntStream;


/**
 * Implementation of a block matrix.
 */
final class BlockMatrix extends DefaultMatrix {

  /**
   * The order of the sub-matrix matrix size (2^order). By default the order is 5 which
   * corresponds to 32x32 sub-matrices.
   */
  static final int SUBMATRIX_ORDER = 5;

  /**
   * The size of the sub-matrices
   */
  static final int SUBMATRIX_DIMENSION = 2 << (SUBMATRIX_ORDER -1);
  static final int SUBMATRIX_INDEX_MASK;
  static {
    int mask = 0b1;
    for (int i = 0; i < SUBMATRIX_ORDER -1; i++) {
      mask <<= 1;
      mask |= 0b1;
    }
    SUBMATRIX_INDEX_MASK = mask;
  }

  private final int rows;
  private final int columns;
  private final PackedMatrix[] blocks;
  private final int blockRows;
  private final int blockColumns;

  /**
   * Constructs a new block matrix.
   */
  BlockMatrix(int rows,
              int columns,
              int blockRows,
              int blockColumns,
              PackedMatrix[] blocks) {
    super();
    this.rows = rows;
    this.columns = columns;
    this.blockRows = blockRows;
    this.blockColumns = blockColumns;
    this.blocks = blocks;
  }

  @Override
  public int getColumnDimension() {
    return columns;
  }


  @Override
  public int getRowDimension() {
    return rows;
  }


  @Override
  public double get(int row, int column) {
    if (row < 0 || row >= rows) {
      throw new IllegalArgumentException("Invalid row index: " + row);
    }
    if (column < 0 || column >= columns) {
      throw new IllegalArgumentException("Invalid column index: " + column);
    }

    int blockRow    = row >> SUBMATRIX_ORDER;
    int blockColumn = column >> SUBMATRIX_ORDER;

    PackedMatrix block = getBlock(blockRow, blockColumn);

    return block.get(row & SUBMATRIX_INDEX_MASK, column & SUBMATRIX_INDEX_MASK);
  }

  @Override
  public Vector row(int row) {
    return super.row(row);
  }

  @Override
  public Vector column(int column) {
    return super.column(column);
  }

  @Override
  public BlockMatrix evaluate() {
    PackedMatrix[] evaluated =
        Arrays.stream(this.blocks)
                 .parallel()
                 .map((block) -> block.evaluate())
                 .toArray(PackedMatrix[]::new);

    return new BlockMatrix(rows,
                           columns,
                           blockColumns,
                           blockRows,
                           evaluated);
  }

  @Override
  public BlockMatrix transpose() {
    PackedMatrix[] transposed =
        IntStream.range(0, this.blocks.length)
                 .parallel()
                 .mapToObj((i) -> getBlock(i % blockRows, i / blockRows))
                 .map((block) -> block.transpose())
                 .toArray(PackedMatrix[]::new);

    return new BlockMatrix(columns,
                           rows,
                           blockColumns,
                           blockRows,
                           transposed);
  }


  /**
   * Get a block from the internal storage array.
   *
   * @param blockRow    The block row index
   * @param blockColumn The block column index
   * @return The block, or null
   */
  private PackedMatrix getBlock(int blockRow, int blockColumn) {
    return blocks[blockRow * blockColumns + blockColumn];
  }


  @Override
  public Matrix add(Matrix other) {
    if (other instanceof BlockMatrix) {
      return add((BlockMatrix) other);
    }
    return super.add(other);
  }


  @Override
  public Matrix subtract(Matrix other) {
    if (other instanceof BlockMatrix) {
      return subtract((BlockMatrix) other);
    }
    return super.subtract(other);
  }

  @Override
  public Matrix multiply(Matrix other) {
    if (other instanceof BlockMatrix) {
      return multiply((BlockMatrix) other);
    }
    return super.multiply(other);
  }


  /**
   * Multiply this block matrix with the other block matrix by multiplying
   * the sub-matrices.
   * @param other The other block matrix
   * @return The multiplied matrix.
   */
  private BlockMatrix multiply(BlockMatrix other) {
    PackedMatrix[] result = IntStream.range(0, blockRows)
             .parallel()
             .mapToObj((i) -> multiply(other, i))
             .flatMap(Arrays::stream)
             .toArray(PackedMatrix[]::new);

    return new BlockMatrix(rows,
                           other.columns,
                           blockRows,
                           other.blockColumns,
                           result);
  }

  /**
   * Multiply this matrix with the other matrix. Only the specified column is
   * multiplied.
   * @param other The other matrix
   * @param row The row to multiply
   * @return The resulting matrix
   */
  private PackedMatrix[] multiply(BlockMatrix other, int row) {
    final int loopEnd = other.blockRows * other.blockColumns;
    final PackedMatrixBuilder[] data = new PackedMatrixBuilder[other.blockColumns];

    // make sure last block is always present
    for (int i = 0; i < data.length; i++) {
      data[i] = new PackedMatrixBuilder(SUBMATRIX_DIMENSION, SUBMATRIX_DIMENSION);
    }

    int i1 = row * blockColumns;
    int i2 = 0;
    int i3 = 0;
    int end = other.blockColumns;

    PackedMatrix value = blocks[i1++];

    while (i2 < end) {
      data[i3].add(value, other.blocks[i2]);
      i2++;
      i3++;
    }

    while (i2 != loopEnd) {
      i3 = 0;
      end = i2 + other.blockColumns;
      value = blocks[i1++];

      while (i2 < end) {
        data[i3].add(value, other.blocks[i2++]);
        i3++;
      }
    }

    return Arrays.stream(data)
                 .map(PackedMatrixBuilder::toMatrix)
                 .toArray(PackedMatrix[]::new);
  }

  /**
   * Subtract a block matrix from this block matrix
   * @param other The other matrix
   * @return The resulting matrix
   */
  public BlockMatrix subtract(BlockMatrix other) {
    if (rows != other.rows || columns != other.columns) {
      throw new IllegalArgumentException("PackedMatrix dimensions do not agree");
    }
    PackedMatrix[] subtracted =
      IntStream
          .range(0, this.blocks.length)
          .parallel()
          .mapToObj((i) -> this.blocks[i].subtract(other.blocks[i]))
          .toArray(PackedMatrix[]::new);

    return new BlockMatrix(rows,
                           columns,
                           blockRows,
                           blockColumns,
                           subtracted);
  }


  /**
   * Add a block matrix to this block matrix
   * @param other The other matrix
   * @return The resulting matrix
   */
  public BlockMatrix add(BlockMatrix other) {
    if (rows != other.rows || columns != other.columns) {
      throw new IllegalArgumentException("PackedMatrix dimensions do not agree");
    }
    PackedMatrix[] added =
        IntStream
            .range(0, this.blocks.length)
            .parallel()
            .mapToObj((i) -> this.blocks[i].add(other.blocks[i]))
            .toArray(PackedMatrix[]::new);

    return new BlockMatrix(rows,
                           columns,
                           blockRows,
                           blockColumns,
                           added);
  }

  @Override
  public BlockMatrix multiply(double multiplicand) {
    PackedMatrix[] scaled =
        Arrays.stream(this.blocks)
              .parallel()
              .map((block) -> block.multiply(multiplicand))
              .toArray(PackedMatrix[]::new);

    return new BlockMatrix(rows,
                           columns,
                           blockColumns,
                           blockRows,
                           scaled);
  }


  /**
   * Constructs a builder initialized with the data from this matrix.
   *
   * @return matrix builder
   */
  MatrixBuilder<BlockMatrix> toBuilder() {
    PackedMatrixBuilder[]
        blocks =
        Arrays.stream(this.blocks)
              .map(PackedMatrix::toBuilder)
              .toArray(PackedMatrixBuilder[]::new);
    return new BlockMatrixBuilder(rows,
                                  columns,
                                  blockRows,
                                  blockColumns,
                                  blocks);
  }


  @Override
  public double[] toArray() {
    double[] packed = new double[rows * columns];
    int      index  = 0;
    for (int row = 0; row < rows; row += SUBMATRIX_DIMENSION) {
      for (int column = 0; column < columns; column += SUBMATRIX_DIMENSION) {
        PackedMatrix block = this.blocks[index++];

        int rowsInBlock = min(SUBMATRIX_DIMENSION, this.rows - row);
        int columnsInBlock = min(SUBMATRIX_DIMENSION, this.columns - column);
        int offset = row * columns + column;
        int stride = columns;

        block.copy(rowsInBlock, columnsInBlock, packed, offset, stride);
      }
    }
    return packed;
  }

}
