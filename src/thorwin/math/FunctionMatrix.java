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

/**
 * Matrix implementation that uses a lambda function to calculate its entries.
 */
final class FunctionMatrix implements Matrix {

  private final int rowCount;

  private final int columnCount;

  private final MatrixFunction matrixFunction;


  /**
   * Construct a new lambda matrix.
   *
   * @param parent parent matrix
   */
  public FunctionMatrix(Matrix parent) {
    this(parent.getRowDimension(), parent.getColumnDimension(), parent::get);
  }


  /**
   * Construct a new lambda matrix.
   *
   * @param rowCount       The number of rowsInBlock
   * @param columnCount    The number of getColumnDimension
   * @param matrixFunction The function to use to evaluate the matrix
   */
  public FunctionMatrix(int rowCount,
                        int columnCount,
                        MatrixFunction matrixFunction) {
    this.rowCount = rowCount;
    this.columnCount = columnCount;
    this.matrixFunction = matrixFunction;
  }


  @Override
  public FunctionMatrix transpose() {
    return new FunctionMatrix(columnCount,
                              rowCount,
                              (rowIndex, columnIndex) -> get(columnIndex,
                                                             rowIndex));
  }


  @Override
  public int getRowDimension() {
    return rowCount;
  }


  @Override
  public int getColumnDimension() {
    return columnCount;
  }


  @Override
  public double get(int row, int column) {
    return this.matrixFunction.apply(row, column);
  }


  @Override
  public FunctionMatrix add(Matrix other) {
    return new FunctionMatrix(columnCount,
                              rowCount,
                              (rowIndex, columnIndex) -> get(rowIndex,
                                                             columnIndex) +
                                                         other.get(rowIndex,
                                                                   columnIndex));
  }


  @Override
  public FunctionMatrix subtract(Matrix other) {
    return new FunctionMatrix(columnCount,
                              rowCount,
                              (rowIndex, columnIndex) -> get(rowIndex,
                                                             columnIndex) -
                                                         other.get(rowIndex,
                                                                   columnIndex));
  }


  @Override
  public FunctionMatrix multiply(Matrix other) {
    return new FunctionMatrix(rowCount,
                              other.getColumnDimension(),
                              (row, column) -> row(row).multiply(other.column
                                                                           (column)));
  }


}
