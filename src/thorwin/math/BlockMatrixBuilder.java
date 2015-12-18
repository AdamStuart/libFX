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

import java.util.Arrays;

import static java.lang.Integer.min;
import static java.lang.Math.multiplyExact;
import static thorwin.math.BlockMatrix.*;


/**
 * Builder for block matrix.
 */
final class BlockMatrixBuilder extends MatrixBuilder<BlockMatrix> {

  private final int blockRows;

  private final int blockColumns;

  private final int rows;

  private final int columns;

  private final PackedMatrixBuilder[] blocks;


  /**
   * Constructs a block matrix.
   *
   * @param rows    number of rows
   * @param columns number of columns
   */
  BlockMatrixBuilder(int rows, int columns) {
    super();

    this.rows = rows;
    this.columns = columns;
    this.blockRows = calculateBlockCount(rows);
    this.blockColumns = calculateBlockCount(columns);


    this.blocks = new PackedMatrixBuilder[multiplyExact(blockRows,
                                                        blockColumns)];

    for (int i = 0; i < this.blocks.length; i++) {
      this.blocks[i] = new PackedMatrixBuilder(SUBMATRIX_DIMENSION, SUBMATRIX_DIMENSION);
    }
  }


  /**
   * Calculate the number of blocks required for the specified dimension.
   * @param dimension dimension
   * @return number of blocks
   */
  static int calculateBlockCount(int dimension) {
    int base = dimension >> SUBMATRIX_ORDER;
    int leftover = dimension & SUBMATRIX_INDEX_MASK;
    return base + (leftover == 0 ? 0 : 1);
  }


  /**
   * Constructs a block matrix.
   *
   * @param rows number of rows
   * @param columns number of columns
   * @param blockRows    number of block rows
   * @param blockColumns number of block columns
   * @param blocks       block builders
   */
  BlockMatrixBuilder(int rows,
                     int columns,
                     int blockRows,
                     int blockColumns,
                     PackedMatrixBuilder[] blocks) {
    super();
    assert blocks.length == blockRows * blockColumns;
    this.rows = rows;
    this.columns = columns;
    this.blockRows = blockRows;
    this.blockColumns = blockColumns;
    this.blocks = blocks;
  }


  /**
   * Constructs a block matrix.
   *
   * @param rows number of rows
   * @param columns number of columns
   * @param packed       packed matrix
   */
  BlockMatrixBuilder(int rows,
                     int columns,
                     double[] packed) {
    super();
    this.rows = rows;
    this.columns = columns;
    this.blockRows = calculateBlockCount(rows);
    this.blockColumns =calculateBlockCount(columns);
    this.blocks = new PackedMatrixBuilder[this.blockRows * this.blockColumns];

    int index = 0;
    for (int row = 0; row < rows; row += SUBMATRIX_DIMENSION) {
      for (int column = 0; column < columns; column += SUBMATRIX_DIMENSION) {
        PackedMatrixBuilder block = new PackedMatrixBuilder(SUBMATRIX_DIMENSION,
                                                            SUBMATRIX_DIMENSION);

        int length = min(SUBMATRIX_DIMENSION, columns - column);
        int fence = min(SUBMATRIX_DIMENSION, rows-row);
        for (int i = 0; i < fence; i++) {
          int offset = (row + i) * columns + column;
          block.set(i * SUBMATRIX_DIMENSION, packed, offset, length);
        }
        this.blocks[index++] = block;
      }
    }
  }


  @Override
  public double get(int row, int column) {
    int blockRow    = row >> SUBMATRIX_ORDER;
    int blockColumn = column >> SUBMATRIX_ORDER;

    PackedMatrixBuilder builder = getBlock(blockRow, blockColumn);


    return builder.get(row & SUBMATRIX_INDEX_MASK,
                       column & SUBMATRIX_INDEX_MASK);
  }


  @Override
  public void set(int row, int column, double value) {
    int blockRow    = row >> SUBMATRIX_ORDER;
    int blockColumn = column >> SUBMATRIX_ORDER;

    PackedMatrixBuilder builder = getBlock(blockRow, blockColumn);

    builder.set(row & SUBMATRIX_INDEX_MASK, column & SUBMATRIX_INDEX_MASK, value);
  }


  @Override
  public BlockMatrix toMatrix() {
    PackedMatrix[]
        blocks =
        Arrays.stream(this.blocks).map((builder) -> builder.toMatrix())
              .toArray(PackedMatrix[]::new);

    return new BlockMatrix(rows,
                           columns,
                           blockRows,
                           blockColumns,
                           blocks);
  }


  /**
   * Get a block
   *
   * @param blockRow    The block row index
   * @param blockColumn The block column index
   * @return The block, or null
   */
  PackedMatrixBuilder getBlock(int blockRow, int blockColumn) {
    return blocks[blockRow * blockColumns + blockColumn];
  }
}
