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

import java.util.Optional;
import java.util.Random;
import java.util.StringTokenizer;

import static java.lang.Math.*;


/**
 * Real matrix.
 * <p>
 * Matrices are created using one of the static construction methods: <ul>
 * <li><code>columnPacked</code></li> <li><code>diagonal</code></li>
 * <li><code>functional</code></li> <li><code>identity</code></li>
 * <li><code>random</code></li> <li><code>rowPacked</code></li>
 * <li><code>scalar</code></li> <li><code>matrix</code></li>
 * <li><code>vandermonde</code></li> </ul>
 * <p>
 * All implementations of this interface returned by these methods are
 * immutable, and therefore completely thread safe. <h3>Implementation
 * Details</h3> The {@code Matrix} implementation have been designed to perform
 * for both small and large matrices. The static constructor methods of this
 * class will return matrix implementations that are designed for the specified
 * dimensions and role.
 * <p>
 * Small matrices will either store elements directly or in a packed array.
 * Larger matrices will be automatically partitioned. Partitioning will benefit
 * performance if the matrix can be divided into equal sized blocks. For optimal
 * performance, choose dimensions that allow partitioning (avoid prime numbers).
 * <p>
 * Operations will use parallel algorithms if they perform faster. In
 * particular, operations on large matrices will executed on multiple threads.
 */
public interface Matrix extends java.io.Serializable {

  /**
   * Constructs a matrix from a column packed array. The array contains data in
   * the following order: <i> M<sub>0,0</sub>, M<sub>1,0</sub>, M<sub>2,0</sub>,
   * ... M<sub>0,1</sub>, M<sub>1,1</sub>, M<sub>2,1</sub></i>
   * <p>
   * Note that the created matrix need not have its values stored column packed,
   * it is just the format of the offered data.
   *
   * @param rowCount Number of rowsInBlock in a column
   * @param data     The entry values
   * @return matrix
   */
  static Matrix columnPacked(int rowCount, double... data) {
    int columnCount = data.length / rowCount;

    MatrixBuilder<?> builder = MatrixContext.getInstance().create(rowCount,
                                                                  columnCount);

    for (int column = 0; column <  columnCount; column++) {
      int base = column * rowCount;
      for (int row = 0; row < rowCount; row++) {
        builder.set(row, column, data[row+base]);
      }
    }
    return builder.toMatrix();
  }

  /**
   * Constructs a matrix from a row packed array. The array contains data in the
   * following order. <i> M<sub>0,0</sub>, M<sub>0,1</sub>, M<sub>0,2</sub>, ...
   * M<sub>1,0</sub>, M<sub>1,1</sub>, M<sub>1,2</sub></i>
   *
   * @param columnCount number of columns in a row
   * @param data        element values values
   * @return matrix
   */
  static Matrix rowPacked(int columnCount, double... data) {
    int rowCount = data.length / columnCount;

    MatrixBuilder<?> builder = MatrixContext.getInstance().create(rowCount,
                                                                  columnCount);

    for (int row = 0; row < rowCount; row++) {
      int base = row * columnCount;
      for (int column = 0; column <  columnCount; column++) {
        builder.set(row, column, data[column+base]);
      }
    }
    return builder.toMatrix();
  }

  /**
   * Constructs a matrix using a lambda expression. The provided lambda
   * expression is expected to be functional: <ul> <li>It needs to operate on
   * immutable input data (if any)</li> <li>It should be without side
   * effects</li> </ul>
   * <p>
   * Note that the functional matrix will evaluate each access to its elements.
   * This may affect performance when elements are accessed repeatedly. All
   * elements of the matrix may be evaluated and stored in memory using the
   * <code>evaluate()</code> method.
   *
   * @param rowCount       number of rows in the matrix
   * @param columnCount    number of columns in the matrix
   * @param matrixFunction function that calculates the entries
   * @return a functional matrix
   */
  static Matrix function(int rowCount,
                         int columnCount,
                         MatrixFunction matrixFunction) {
    return new FunctionMatrix(rowCount, columnCount, matrixFunction);
  }

  /**
   * Creates a matrix from a text string. Rows are delimited by semicolon
   * (MATLAB) or newline characters. Columns are delimited by space characters.
   *
   * @param matrix matrix string
   * @return constructed matrix
   * @throws IllegalArgumentException If the text can not be parsed as
   *                                            a matrix.
   */
  static Matrix valueOf(String matrix) {

    StringTokenizer tokenizer = new StringTokenizer(matrix, ";\n");

    int rowCount    = tokenizer.countTokens();
    int columnCount = 0; // will be calculated during parsing of rowsInBlock

    Vector[] rows = new Vector[rowCount];

    for (int i = 0; i < rowCount; i++) {
      rows[i] = Vector.valueOf(tokenizer.nextToken());
      columnCount = max(columnCount, rows[i].getDimension());
    }

    MatrixBuilder<?> builder = MatrixContext.getInstance().create(rowCount,
                                                                  columnCount);

    for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
      Vector row = rows[rowIndex];
      for (int columnIndex = 0;
           columnIndex < row.getDimension();
           columnIndex++) {
        builder.set(rowIndex, columnIndex, row.get(columnIndex));
      }
    }

    return builder.toMatrix();
  }

  /**
   * Constructs a matrix from the specified 2-dimensional ([row][column]) array.
   * All getRowDimension should be defined (not <code>null</code>) and of equal
   * length.
   *
   * @param data 2-dimensional array containing the elements
   * @return constructed matrix
   */
  static Matrix valueOf(double[][] data) {
    if (data.length == 0) {
      throw new IllegalArgumentException("data should contain at least 1 row");
    }
    if (data[0].length == 0) {
      throw new IllegalArgumentException("data should contain at least 1 " +
                                         "column");
    }

    int rowCount    = data.length;
    int columnCount = data[0].length;

    MatrixBuilder<?> builder = MatrixContext.getInstance().create(rowCount,
                                                                  columnCount);


    for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
      double[] row = data[rowIndex];
      for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
        builder.set(rowIndex, columnIndex, row[columnIndex]);
      }
    }

    return builder.toMatrix();
  }

  /**
   * Creates a matrix with pseudo-random content.
   *
   * @param rowCount    number of rows
   * @param columnCount number of columns
   * @param seed        a random seed. Use constant for reproducible results,
   *                    System.currentTimeMillis() for actual pseudo random
   *                    value.
   * @return constructed random matrix
   */
  static Matrix random(int rowCount, int columnCount, long seed) {
    Random rnd = new Random(seed);
    MatrixBuilder<?> builder = MatrixContext.getInstance().create(rowCount,
                                                                  columnCount);
    for (int row = 0; row < rowCount; row++) {
      for (int column = 0; column < columnCount; column++) {
        builder.set(row, column, rnd.nextDouble());
      }
    }
    return builder.toMatrix();
  }

  /**
   * Creates a Vandermonde matrix. <h3>Example</h3> <code>Matrix.vandermonde(3,
   * 2.0, 3.0, 4.0) = </code>
   * <pre>
   *  4.0000   2.0000   1.0000
   *  9.0000   3.0000   1.0000
   *  16.000   4.0000   1.0000
   * </pre>
   *
   * @param columns number of getColumnDimension
   * @param alphas  values of &alpha;, length will determine number of rows.
   * @return a Vandermonde matrix
   * @see <a href="https://en.wikipedia.org/wiki/Vandermonde_matrix">Wikipedia
   * on the Vandermonde Matrix</a>
   */
  static Matrix vandermonde(int columns, double... alphas) {
    if (alphas == null) {
      throw new IllegalArgumentException("values should not be null");
    }

    if (columns <= 0) {
      throw new IllegalArgumentException("columnCount should be > 0");
    }

    int rowDimension = alphas.length;

    MatrixBuilder<?> builder = MatrixContext.getInstance().create(rowDimension,
                                                                  columns);

    for (int i = 0; i < rowDimension; i++) {
      builder.set(i, columns - 1, 1.0);

      if (columns > 1) {
        builder.set(i, columns - 2, alphas[i]);
      }

      for (int j = 2; j < columns; j++) {
        builder.set(i, columns - j - 1, pow(alphas[i], j));
      }
    }

    return builder.toMatrix();
  }

  /**
   * Creates a diagonal matrix. A diagonal matrix is a square matrix where all
   * entries are zero, except for the diagonal entries.
   *
   * @param entries diagonal elements
   * @return a diagonal matrix
   */
  static Matrix diagonal(double... entries) {
    MatrixBuilder<?> builder = MatrixContext.getInstance().create(entries.length,
                                                                  entries.length);

    for (int i = 0; i < entries.length; i++) {
      builder.set(i, i, entries[i]);
    }

    return builder.toMatrix();
  }

  /**
   * Creates a diagonal matrix. A diagonal matrix is a square matrix where all
   * entries are zero, except for the diagonal entries.
   *
   * @param entries diagonal elements
   * @return a diagonal matrix
   */
  static Matrix diagonal(Vector entries) {
    MatrixBuilder<?>
        builder =
        MatrixContext.getInstance().create(entries.getDimension(),
                                           entries.getDimension());

    for (int i = 0; i < entries.getDimension(); i++) {
      builder.set(i, i, entries.get(i));
    }

    return builder.toMatrix();
  }

  /**
   * Creates a band matrix.
   * <p>
   * A band matrix is a sparse matrix that only stores diagonal bands of the
   * matrix. All values outside the band are zero. A band matrix stores fewer
   * values. A band matrix saves memory and requires less processing power in
   * various computations.
   * <p>
   * The parent matrix  should be in a specific format. Each row should contain
   * a diagonal of the band.
   * <p>
   * For example the following matrix:
   * <pre>
   *  1.1 1.2 1.3 0.0
   *  2.1 2.2 2.3 2.4
   *  0.0 3.2 3.3 3.4
   *  0.0 0.0 4.3 4.4
   *  0.0 0.0 0.0 5.4
   * </pre>
   * Is stored as:
   * <pre>
   *  0.0 0.0 1.3 2.4
   *  0.0 1.2 2.3 3.4
   *  1.1 2.2 3.3 4.4
   *  2.1 3.2 4.3 5.4
   * </pre>
   * This columnPacked form should be provided in this constructor.
   *
   * @param packed             matrix, in packed form.
   * @param rows               number of rows in the resulting matrix
   * @param upperDiagonalCount upper diagonals
   * @return band matrix
   */
  static Matrix band(Matrix packed, int rows, int upperDiagonalCount) {
    int columns = packed.getColumnDimension();

    MatrixBuilder<?> builder = MatrixContext.getInstance().create(rows, columns);


    for (int packedRow = 0; packedRow < packed.getRowDimension(); packedRow++) {
      for (int packedColumn = 0;
           packedColumn < packed.getColumnDimension();
           packedColumn++) {
        int row = packedRow - upperDiagonalCount + packedColumn;

        // skip padding rows
        if (row < 0 || row >= rows) {
          continue;
        }

        double value = packed.get(packedRow, packedColumn);
        builder.set(row, packedColumn, value);
      }
    }

    return builder.toMatrix();
  }

  /**
   * The number of columns of this matrix.
   *
   * @return column dimension
   */
  int getColumnDimension();

  /**
   * Returns the number of rows in this matrix. The letter <i>m</i> is usually
   * used to indicate a row dimension.
   *
   * @return number of rows
   */
  int getRowDimension();

  /**
   * Returns the element value for the specified row and column index. The
   * element indices are zero based.
   *
   * @param row    row index of the element
   * @param column column index of the element
   * @return the element for the specified row and column indices
   * @throws IndexOutOfBoundsException If the specified row or column indices
   *                                   are out of bounds
   */
  double get(int row, int column);

  /**
   * Transposes the matrix
   *
   * @return transposed matrix
   */
  default Matrix transpose() {
    double[] packed     = toArray();
    double[] transposed = new double[packed.length];

    int columns = getColumnDimension();
    int rows    = getRowDimension();


    int index1 = 0;
    for (int i = 0; i < columns; i++) {
      int index2 = i;
      int fence  = index1 + rows;
      while ( index1 < fence ) {
        transposed[index1] = packed[index2];
        index1++;
        index2 += columns;
      }
    }
    return MatrixContext.getInstance().create(columns, rows, transposed).toMatrix();
  }

  /**
   * Calculates the inverse of this matrix.
   *
   * @return inverse of the matrix (if any)
   */
  default Optional<? extends Matrix> invert() {
    return solve(Matrix.identity(getRowDimension()));
  }

  /**
   * Solves <i>X</i> for <i>A&times;X = B</i>. <i>A</i>, <code>this</code>
   * matrix, <i>B</i>, the <code>other</code> matrix.
   *
   * @param other right hand side (<i>B</i>)
   * @return solution if this matrix is square, least squares solution otherwise
   */
  default Optional<? extends Matrix> solve(Matrix other) {
    return isSquare() ? lu().solve(other) : qr().solve(other);
  }

  /**
   * Creates the identity (or unit) matrix. The identity matrix is usually
   * denoted by <i>I<sub>n</sub></i>, where <i>n</i> is its dimension. It is a
   * square matrix, with ones on the main diagonal and zeros elsewhere.
   * <p>
   * <i>I<sub>m</sub>&times;A = A&times;I<sub>n</sub> = A</i>
   *
   * @param size The number of rows and columns
   * @return the identity matrix
   * @see <a href="https://en.wikipedia.org/wiki/Identity_matrix">Wikipedia on
   * the Identity Matrix</a>
   */
  static Matrix identity(int size) {
    return scalar(size, 1.0);
  }

  /**
   * Determines if this is a square matrix. A matrix is square when its number
   * of rows are equal to its number of columns.
   *
   * @return true if the matrix is square
   */
  default boolean isSquare() {
    return getRowDimension() == getColumnDimension();
  }

  /**
   * Performs a Lower/Upper Decomposition.
   * <p>
   * For an m-by-n matrix <i>A</i> with <i>m &gt;= n</i>, the LU decomposition
   * is: <ul> <li><i>L</i>, an m-by-n unit lower triangular matrix</li>
   * <li><i>U</i>, an n-by-n upper triangular matrix</li> <li><i>piv</i>, a
   * permutation vector of length m</li> </ul>
   * <p>
   * <i>A(piv,:) = L&times;U</i>.
   * <p>
   * If <i>m &lt; n</i>, then <i>L</i> is m-by-m and <i>U</i> is m-by-n.
   *
   * @return LU decomposition
   * @see <a href="https://en.wikipedia.org/wiki/LU_decomposition">Wikipedia on
   * LU decomposition</a>
   */
  default LU lu() {
    return DefaultLU.crout(this);
  }

  /**
   * Performs a QR Decomposition
   * <p>
   * For an m-by-n matrix <i>A</i> with <i>m &gt;= n</i>, the QR decomposition
   * is: <ul> <li><i>Q</i>, an m-by-n orthogonal matrix</li> <li><i>R</i>, an
   * n-by-n upper triangular matrix</li> </ul>
   * <p>
   * <i>A = Q&times;R</i>
   *
   * @return QR Decomposition
   * @see <a href="https://en.wikipedia.org/wiki/QR_decomposition">Wikipedia on
   * QR Decomposition</a>
   */
  default QR qr() {
    return DefaultQR.householder(this);
  }

  /**
   * Creates a scalar matrix. A scalar matrix is a square matrix where all
   * elements are zero, except for the diagonal elements. The diagonal elements
   * all have the same value.
   *
   * @param size  number of rows and columns
   * @param value value of the diagonal entries.
   * @return a scalar matrix
   */
  static Matrix scalar(int size, double value) {
    MatrixBuilder<?> builder = MatrixContext.getInstance().create(size, size);

    for (int i = 0; i < size; i++) {
      builder.set(i, i, value);
    }

    return builder.toMatrix();
  }

  /**
   * Solves <i>X</i> for <i>A&times;X = B</i> using iterative method
   *
   * @param other      right hand side
   * @param iterations maximum number of iterations to use
   * @param tolerance  tolerance allowed for determining convergence
   * @return Solution, if convergence is reached
   * @see <a href="http://en.wikipedia.org/wiki/Jacobi_method">Wikipedia on the
   * Jacobi method</a>
   */
  default Optional<? extends Vector> jacobi(Vector other,
                                            int iterations,
                                            double tolerance) {
    if (!isSquare()) {
      throw new IllegalArgumentException("Matrix is not square");
    }

    int n = getRowDimension();

    double[] x = new double[n];

    for (int k = 0; k < iterations; k++) {

      // update solution
      for (int i = 0; i < n; i++) {
        double s = 0;
        for (int j = 0; j < n; j++) {
          if (i != j) {
            s = s + get(i, j) * x[j];
          }
        }
        x[i] = (other.get(i) - s) / get(i, i);
      }

      // determine if the solution converged enough
      boolean convergence = true;
      for (int i = 0; i < n; i++) {
        double y = 0;
        for (int j = 0; j < n; j++) {
          y += get(i, j) * x[j];
        }
        if (abs(y - other.get(i)) > tolerance) {
          convergence = false;
          break;
        }
      }
      if (convergence) {
        return Optional.of(Vector.valueOf(x));
      }
    }
    return Optional.empty();
  }

  /**
   * Returns the maximum column sum.
   *
   * @return one norm
   * @see <a href="https://en.wikipedia.org/wiki/Matrix_norm">Wikipedia on
   * Matrix Norm</a>
   */
  default double norm1() {
    double f = 0.0;
    for (int j = 0; j < getColumnDimension(); j++) {
      double s = 0.0;
      for (int i = 0; i < getRowDimension(); i++) {
        s += abs(get(i, j));
      }
      f = max(f, s);
    }
    return f;
  }

  /**
   * Returns the maximum singular value.
   *
   * @return two norm.
   * @see <a href="https://en.wikipedia.org/wiki/Matrix_norm">Wikipedia on
   * Matrix Norm</a>
   */
  default double norm2() {
    return svd().norm2();
  }

  /**
   * Performs Singular Value Decomposition
   * <p>
   * For an m-by-n matrix (<i>A</i>) the singular value decomposition is: <ul>
   * <li><i>U</i>, an m-by-n orthogonal matrix </li> <li><i>S</i>, an n-by-n
   * diagonal matrix </li> <li><i>V</i>, an n-by-n orthogonal matrix </li>
   * </ul>
   * <p>
   * <i>A = U&times;S&times;V&prime;</i><p> The values of the singular values
   * vector (<i>s<sub>k</sub> = S<sub>k,k</sub>)</i> are descending.
   *
   * @return Singular value decomposition
   * @see <a href="https://en.wikipedia.org/wiki/Singular_value_decomposition">
   * Wikipedia on Singular Value Decomposition</a>
   */
  default SVD svd() {
    return DefaultSVD.svd(this);
  }

  /**
   * Returns the square root of sum of squares of all elements.
   *
   * @return Frobenius norm
   * @see <a href="https://en.wikipedia.org/wiki/Matrix_norm">Wikipedia on
   * Matrix Norm</a>
   */
  default double normFrobenius() {
    double f = 0.0;
    for (int i = 0; i < getRowDimension(); i++) {
      for (int j = 0; j < getColumnDimension(); j++) {
        f = hypot(f, get(i, j));
      }
    }
    return f;
  }

  /**
   * Returns a text representation of the matrix.
   *
   * @param width     minimum column width
   * @param precision precision used
   * @return matrix as a text string
   */
  default String toString(int width, int precision) {
    String        format  = "%" + width + "." + precision + "g";
    StringBuilder builder = new StringBuilder();
    for (int row = 0; row < getRowDimension(); row++) {
      for (int column = 0; column < getColumnDimension(); column++) {
        if (column > 0) {
          builder.append(' ');
        }
        builder.append(String.format(format, get(row, column)));
        if (column == getColumnDimension() - 1) {
          builder.append('\n');
        }
      }
    }
    return builder.toString();
  }

  /**
   * Compares this matrix to another for equality. Note that this method has
   * little mathematical applicability. It is mostly used for testing.
   *
   * @param other   other matrix (not null)
   * @param epsilon maximum absolute difference between entries for them to be
   *                considered equal
   * @return true, if the matrices are approximately equal
   */
  default boolean equals(Matrix other, double epsilon) {
    // simple check for reference equality
    if (this == other) {
      return true;
    }

    // check dimensions
    if (this.getRowDimension() != other.getRowDimension() ||
        this.getColumnDimension() != other.getColumnDimension()) {
      return false;
    }

    // check entries
    for (int row = 0; row < getRowDimension(); row++) {
      for (int column = 0; column < getColumnDimension(); column++) {
        double diff = get(row, column) - other.get(row, column);

        if (abs(diff) > epsilon) {
          return false;
        }
      }
    }

    // no inequalities found, so the matrices are equal
    return true;
  }

  /**
   * Adds a matrix to this matrix.
   *
   * @param other the matrix to add
   * @return resulting matrix
   */
  default Matrix add(Matrix other) {
    if (getRowDimension() != other.getRowDimension() ||
        getColumnDimension() != other.getColumnDimension()) {
      throw new IllegalArgumentException("Matrix dimensions do not agree");
    }

    double[] a = this.toArray();
    double[] b = other.toArray();

    for (int i = 0; i < a.length; i++) {
      a[i] += b[i];
    }

    return MatrixContext.getInstance().create(getRowDimension(),
                                              getColumnDimension(),
                                              a).toMatrix();
  }

  /**
   * Subtracts a matrix from this matrix.
   *
   * @param other the matrix to subtract
   * @return resulting matrix
   */
  default Matrix subtract(Matrix other) {
    if (getRowDimension() != other.getRowDimension() ||
        getColumnDimension() != other.getColumnDimension()) {
      throw new IllegalArgumentException("Matrix dimensions do not agree");
    }

    double[] a = this.toArray();
    double[] b = other.toArray();

    for (int i = 0; i < b.length; i++) {
      a[i] -= b[i];
    }

    return MatrixContext.getInstance().create(getRowDimension(),
                                              getColumnDimension(),
                                              a).toMatrix();
  }


  /**
   * Multiplies this matrix with a vector.
   *
   * @param other a vector
   * @return resulting vector
   */
  default Vector multiply(Vector other) {
    return multiply(other.toMatrix()).column(0);
  }

  /**
   * Returns the column as vector.
   *
   * @param column column index
   * @return column vector
   */
  default Vector column(int column) {
    double[] data = new double[getRowDimension()];
    for (int i = 0; i < data.length; i++) {
      data[i] = get(i, column);
    }
    return new ArrayVector(data);
  }

  /**
   * Multiplies this matrix with another matrix
   *
   * @param other the other matrix
   * @return resulting matrix
   */
  default Matrix multiply(Matrix other) {

    if (getColumnDimension() != other.getRowDimension()) {
      throw new IllegalArgumentException("Matrix inner dimensions must agree.");
    }

    int m = getRowDimension();
    int n = other.getColumnDimension();
    int k = getColumnDimension();

    double[] a = this.toArray();
    double[] b = other.toArray();
    double[] c = new double[m*n];

    MatrixOperations.dgemm(a,b,c,m,n,k,false,false,1,0);

    return MatrixContext.getInstance().create(m,n,c).toMatrix();
  }


  /**
   * Returns the row as vector.
   *
   * @param row row index
   * @return row vector
   */
  default Vector row(int row) {
    double[] data = new double[getColumnDimension()];
    for (int i = 0; i < data.length; i++) {
      data[i] = get(row, i);
    }
    return new ArrayVector(data);
  }

  /**
   * Evaluates the matrix. Evaluating the matrix ensures all values are
   * calculated and present in the returned matrix, in such a way that
   * re-querying single entries involves minimal computational overhead. This
   * method is mostly applicable to matrices calculate their entries instead of
   * having them stored.
   * <p>
   * Calling this method on a matrix that already stores its entries in memory
   * will not result in copying the matrix. In that case the matrix itself will
   * be returned.
   *
   * @return evaluated matrix
   */
  default Matrix evaluate() {
    int rowDimension    = getRowDimension();
    int columnDimension = getColumnDimension();
    MatrixBuilder<?>
        builder = MatrixContext.getInstance().create(rowDimension,
                                                     columnDimension);
    for (int row = 0; row < rowDimension; row++) {
      for (int column = 0; column < columnDimension; column++) {
        builder.set(row, column, get(row, column));
      }
    }
    return builder.toMatrix();
  }

  /**
   * Returns a sub-matrix.
   *
   * @param rowStart    initial row index
   * @param rowEnd      final row index
   * @param columnStart initial column index
   * @param columnEnd   final column index
   * @return the specified sub-matrix
   */

  default Matrix getMatrix(int rowStart,
                           int rowEnd,
                           int columnStart,
                           int columnEnd) {
    int m = (rowEnd - rowStart) + 1;
    int n = (columnEnd - columnStart) + 1;

    MatrixBuilder<?> builder = MatrixContext.getInstance().create(m, n);

    for (int i = rowStart; i <= rowEnd; i++) {
      for (int j = columnStart; j <= columnEnd; j++) {
        builder.set(i - rowStart, j - columnStart, get(i, j));
      }
    }

    return builder.toMatrix();
  }

  /**
   * Returns a sub-matrix.
   *
   * @param rowIndices  array of row indices.
   * @param columnStart initial column index
   * @param columnEnd   final column index
   * @return sub-matrix
   */
  default Matrix getMatrix(int[] rowIndices, int columnStart, int columnEnd) {
    int m = rowIndices.length;
    int n = (columnEnd - columnStart) + 1;

    MatrixBuilder<?> builder = MatrixContext.getInstance().create(m, n);

    for (int i = 0; i < m; i++) {
      int index = rowIndices[i];

      for (int j = columnStart; j <= columnEnd; j++) {
        builder.set(i, j - columnStart, get(index, j));
      }
    }
    return builder.toMatrix();
  }

  /**
   * Performs an Eigendecomposition.
   * <p>
   * If this matrix is symmetric, then the decomposition is: <ul> <li><i>D</i>,
   * the diagonal eigenvalue matrix</li> <li><i>V</i>, the orthogonal
   * eigenvector matrix</li> </ul>
   * <p>
   * <i>A = V&times;D&times;V&prime;</i>
   * <p>
   * If this is not symmetric, then the decomposition is: <ul> <li><i>D</i>, the
   * block-diagonal eigenvalue matrix. The real eigenvalues in 1-b-1 blocks, and
   * any complex eigenvalues in 2-by-2 blocks</li> <li><i>V</i>, the eigenvector
   * matrix</li> </ul>
   * <p>
   * Note that matrix <i>V</i> may be badly conditioned (singular), so <i>A =
   * V&times;D&times;inverse(V)</i> depends on <code>V.norm1()</code>.
   *
   * @return Eigendecomposition.
   * @see <a href="https://en.wikipedia.org/wiki/Eigendecomposition_of_a_matrix">
   * Wikipedia on Eigendecomposition of a Matrix</a>
   */
  default Eigen eig() {
    return DefaultEigen.decompose(this);
  }

  /**
   * Performs a Cholesky Decomposition. This matrix needs to be square,
   * symmetric and positive definite.
   * <p>
   * The Cholesky Decomposition is a lower triangular matrix L or R.
   * <p>
   * <i>A = L&times;L&prime;</i> or <i>A = R&prime;&times;R</i>
   *
   * @return Cholesky Decomposition
   * @see <a href="https://en.wikipedia.org/wiki/Cholesky_decomposition">
   *   Wikipedia on Cholesky Decomposition</a>
   */
  default Cholesky chol() {
    return DefaultCholesky.left(this);
  }

  /**
   * Multiplies this matrix with -1.0
   *
   * @return matrix
   */
  default Matrix negate() {
    return multiply(-1.0);
  }

  /**
   * Multiplies this matrix with a real value (scale).
   *
   * @param multiplicand multiplicand
   * @return resulting matrix
   */
  default Matrix multiply(double multiplicand) {
    int rowCount    = getRowDimension();
    int columnCount = getColumnDimension();

    MatrixBuilder<?> builder = MatrixContext.getInstance().create(rowCount,
                                                                  columnCount);

    for (int row = 0; row < rowCount; row++) {
      for (int column = 0; column < columnCount; column++) {
        builder.set(row, column, get(row, column) * multiplicand);
      }
    }

    return builder.toMatrix();
  }

  /**
   * Create a row-major packed array of the matrix.
   * @return matrix in a a packed array
   */
  default double[] toArray() {
    double[] packed = new double[getRowDimension() * getColumnDimension()];
    int index = 0;

    for (int row = 0; row < getRowDimension(); row++) {
      for (int column = 0; column < getColumnDimension(); column++) {
        packed[index++] = get(row, column);
      }
    }
    return packed;
  }


}
