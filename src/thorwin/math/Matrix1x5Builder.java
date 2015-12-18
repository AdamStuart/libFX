package thorwin.math;

/**
 * Implementation of a 1x5 matrix. This class has been
 * generated for optimal performance.
 */
final class Matrix1x5Builder extends MatrixBuilder<Matrix1x5> {

  /**
   * Value of row 0, column 0.
   */
  private double r0c0;

  /**
   * Value of row 0, column 1.
   */
  private double r0c1;

  /**
   * Value of row 0, column 2.
   */
  private double r0c2;

  /**
   * Value of row 0, column 3.
   */
  private double r0c3;

  /**
   * Value of row 0, column 4.
   */
  private double r0c4;


  /**
   * Return the value of the specified entry.
   */
  @Override
  public double get(int row, int column) {
    switch (row) {

      case 0:
        switch (column) {
          case 0: return this.r0c0;
          case 1: return this.r0c1;
          case 2: return this.r0c2;
          case 3: return this.r0c3;
          case 4: return this.r0c4;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      default: throw new IllegalArgumentException("No such row: " + row);
    }
  }

  /**
   * Set the value of an entry.
   */
  @Override
  public void set(int row, int column, double value) {
    switch (row) {

      case 0:
        switch (column) {
          case 0:
            this.r0c0 = value;
            break;
          case 1:
            this.r0c1 = value;
            break;
          case 2:
            this.r0c2 = value;
            break;
          case 3:
            this.r0c3 = value;
            break;
          case 4:
            this.r0c4 = value;
            break;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
        break;
      default: throw new IllegalArgumentException("No such row: " + row);
    }
  }

  /**
   * Construct a new Matrix1x5 instance.
   */
  public Matrix1x5 toMatrix() {
    return new Matrix1x5(
      this.r0c0,
      this.r0c1,
      this.r0c2,
      this.r0c3,
      this.r0c4
    );
  }
}