package thorwin.math;

/**
 * Implementation of a 5x4 matrix. This class has been
 * generated for optimal performance.
 */
final class Matrix5x4Builder extends MatrixBuilder<Matrix5x4> {

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
   * Value of row 1, column 0.
   */
  private double r1c0;

  /**
   * Value of row 1, column 1.
   */
  private double r1c1;

  /**
   * Value of row 1, column 2.
   */
  private double r1c2;

  /**
   * Value of row 1, column 3.
   */
  private double r1c3;

  /**
   * Value of row 2, column 0.
   */
  private double r2c0;

  /**
   * Value of row 2, column 1.
   */
  private double r2c1;

  /**
   * Value of row 2, column 2.
   */
  private double r2c2;

  /**
   * Value of row 2, column 3.
   */
  private double r2c3;

  /**
   * Value of row 3, column 0.
   */
  private double r3c0;

  /**
   * Value of row 3, column 1.
   */
  private double r3c1;

  /**
   * Value of row 3, column 2.
   */
  private double r3c2;

  /**
   * Value of row 3, column 3.
   */
  private double r3c3;

  /**
   * Value of row 4, column 0.
   */
  private double r4c0;

  /**
   * Value of row 4, column 1.
   */
  private double r4c1;

  /**
   * Value of row 4, column 2.
   */
  private double r4c2;

  /**
   * Value of row 4, column 3.
   */
  private double r4c3;


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
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 1:
        switch (column) {
          case 0: return this.r1c0;
          case 1: return this.r1c1;
          case 2: return this.r1c2;
          case 3: return this.r1c3;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 2:
        switch (column) {
          case 0: return this.r2c0;
          case 1: return this.r2c1;
          case 2: return this.r2c2;
          case 3: return this.r2c3;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 3:
        switch (column) {
          case 0: return this.r3c0;
          case 1: return this.r3c1;
          case 2: return this.r3c2;
          case 3: return this.r3c3;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
      case 4:
        switch (column) {
          case 0: return this.r4c0;
          case 1: return this.r4c1;
          case 2: return this.r4c2;
          case 3: return this.r4c3;
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
          case 2:
            this.r1c2 = value;
            break;
          case 3:
            this.r1c3 = value;
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
          case 2:
            this.r2c2 = value;
            break;
          case 3:
            this.r2c3 = value;
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
          case 2:
            this.r3c2 = value;
            break;
          case 3:
            this.r3c3 = value;
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
          case 2:
            this.r4c2 = value;
            break;
          case 3:
            this.r4c3 = value;
            break;
          default: throw new IllegalArgumentException("No such column: " + column);
        }
        break;
      default: throw new IllegalArgumentException("No such row: " + row);
    }
  }

  /**
   * Construct a new Matrix5x4 instance.
   */
  public Matrix5x4 toMatrix() {
    return new Matrix5x4(
      this.r0c0,
      this.r0c1,
      this.r0c2,
      this.r0c3,
      this.r1c0,
      this.r1c1,
      this.r1c2,
      this.r1c3,
      this.r2c0,
      this.r2c1,
      this.r2c2,
      this.r2c3,
      this.r3c0,
      this.r3c1,
      this.r3c2,
      this.r3c3,
      this.r4c0,
      this.r4c1,
      this.r4c2,
      this.r4c3
    );
  }
}