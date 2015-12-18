package thorwin.math;

/**
 * Implementation of a 5x2 matrix. This class has been
 * generated for optimal performance.
 */
final class Matrix5x2Builder extends MatrixBuilder<Matrix5x2> {

  /**
   * Value of row 0, column 0.
   */
  private double r0c0;

  /**
   * Value of row 0, column 1.
   */
  private double r0c1;

  /**
   * Value of row 1, column 0.
   */
  private double r1c0;

  /**
   * Value of row 1, column 1.
   */
  private double r1c1;

  /**
   * Value of row 2, column 0.
   */
  private double r2c0;

  /**
   * Value of row 2, column 1.
   */
  private double r2c1;

  /**
   * Value of row 3, column 0.
   */
  private double r3c0;

  /**
   * Value of row 3, column 1.
   */
  private double r3c1;

  /**
   * Value of row 4, column 0.
   */
  private double r4c0;

  /**
   * Value of row 4, column 1.
   */
  private double r4c1;


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
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 1:
        switch (column) {
          case 0: return this.r1c0;
          case 1: return this.r1c1;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 2:
        switch (column) {
          case 0: return this.r2c0;
          case 1: return this.r2c1;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 3:
        switch (column) {
          case 0: return this.r3c0;
          case 1: return this.r3c1;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 4:
        switch (column) {
          case 0: return this.r4c0;
          case 1: return this.r4c1;
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
          default: throw new IllegalArgumentException("No such column: " + column);
        }
        break;
      case 1:
        switch (column) {
          case 0:
            this.r1c0 = value;
            break;
          case 1:
            this.r1c1 = value;
            break;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
        break;
      case 2:
        switch (column) {
          case 0:
            this.r2c0 = value;
            break;
          case 1:
            this.r2c1 = value;
            break;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
        break;
      case 3:
        switch (column) {
          case 0:
            this.r3c0 = value;
            break;
          case 1:
            this.r3c1 = value;
            break;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
        break;
      case 4:
        switch (column) {
          case 0:
            this.r4c0 = value;
            break;
          case 1:
            this.r4c1 = value;
            break;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
        break;
      default: throw new IllegalArgumentException("No such row: " + row);
    }
  }

  /**
   * Construct a new Matrix5x2 instance.
   */
  public Matrix5x2 toMatrix() {
    return new Matrix5x2(
      this.r0c0,
      this.r0c1,
      this.r1c0,
      this.r1c1,
      this.r2c0,
      this.r2c1,
      this.r3c0,
      this.r3c1,
      this.r4c0,
      this.r4c1
    );
  }
}