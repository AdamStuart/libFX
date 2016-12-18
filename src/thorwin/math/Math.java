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

import static java.lang.Math.abs;

import java.util.Arrays;

/**
 * This class contains very general math functions. Usually these functions are
 * statically imported.
 */
public final class Math {

  /**
   * 1/2 PI
   */
  public static final double HALF_PI = java.lang.Math.PI / 2.0;

  /**
   * 1/4 PI
   */
  public static final double QUARTER_PI = java.lang.Math.PI / 4.0;

  /**
   * 3/4 PI
   */
  public static final double THREE_QUARTER_PI = 0.75 * java.lang.Math.PI;

  /**
   * 2 PI
   */
  public static final double TWO_PI = java.lang.Math.PI * 2.0;

  /**
   * Private constructor to prevent instantiation
   */
  private Math() {
  }


  /**
   * Calculates greatest common divisor of two integers. If 0 is passed to one
   * parameter, the other parameter is returned.
   *
   * @param a integer value a
   * @param b integer value b
   * @return greatest common divisor
   * @throws ArithmeticException if the GCD results in 2<sup>31</sup>, which can not be
   *                             represented in an integer. This can only happen
   *                             if you pass Integer.MIN_VALUE to one of its
   *                             values.
   */
  public static int gcd(int a, int b) {

    // gcd(0,b) == b
    // gcd(a,0) == a
    // gcd(0,0) == 0
    if (a == 0) return abs(b);
    if (b == 0) return abs(a);

    if (a != Integer.MIN_VALUE && b != Integer.MIN_VALUE) {
      return gcdUnsigned(abs(a), abs(b));
    } else {
      return gcdSigned(a, b);
    }
  }

  /**
   * Calculates greatest common divisor of two positive integers. As the input
   * values are known to be positive integers, it can use some specific
   * optimizations.
   *
   * @param a positive integer value a
   * @param b positive integer value b
   * @return greatest common divisor
   */
  private static int gcdUnsigned(int a, int b) {

    assert a > 0;
    assert b > 0;

    // handle powers of 2
    int shift = 0;
    while (even(a) && even(b)) {
      a >>= 1;
      b >>= 1;
      shift++;
    }

    while (even(a)) a >>= 1;

    assert odd(a);

    do {
      while (even(b)) {
        b >>= 1;
      }

      if (a > b) {
        int t = b;
        b = a;
        a = t;
      }
      b = b - a;
    } while (b != 0);

    return a << shift;
  }

  /**
   * Calculates greatest common divisor of two integers. If 0 is passed to one
   * parameter, the other parameter is returned.
   *
   * @param a integer value (not zero) a
   * @param b integer value (not zero) b
   * @return greatest common divisor
   * @throws ArithmeticException if the GCD results in 2<sup>31</sup>, which can not be
   *                             represented in an integer. This can only happen
   *                             if you pass Integer.MIN_VALUE to one of its
   *                             values.
   */
  private static int gcdSigned(int a, int b) {

    assert a != 0;
    assert b != 0;

    // make sure both values have same (negative) sign
    if (a > 0) a = -a;
    if (b > 0) b = -b;

    // handle powers of 2
    int shift = 0;
    while (even(a) && even(b) && shift < 31) {
      a /= 2;
      b /= 2;
      shift++;
    }

    // Check if return value is 2^31 as this can't be represented in a
    // signed 32-bit integer (-2147483648 to 2147483647)
    if (shift == 31) {
      throw new ArithmeticException("Integer overflow, can't represent 2^31");
    }

    int tmp = odd(a) ? b : -(a / 2);
    do {
      while (even(tmp)) {
        tmp /= 2;
      }
      if (tmp > 0) {
        a = -tmp;
      } else {
        b = tmp;
      }
      tmp = (b - a) / 2;
    } while (tmp != 0);
    return -a * (1 << shift);
  }

  /**
   * Determines if the value is even
   *
   * @param a value to test
   * @return true if the value is even
   */
  public static boolean even(int a) {
    return (a & 1) == 0;
  }

  /**
   * Determines if the value is odd
   *
   * @param a value to test
   * @return true if the value is odd
   */
  public static boolean odd(int a) {
    return (a & 1) == 1;
  }

  /**
   * Calculate greatest common divisor of two long integers. If 0 is passed to one
   * parameter, the other parameter is returned.
   *
   * @param a integer value a
   * @param b integer value b
   * @return greatest common divisor
   * @throws ArithmeticException if the GCD results in 2<sup>63</sup>, which can not be
   *                             represented in an integer. This can only happen
   *                             if you pass Long.MIN_VALUE to one of its
   *                             values.
   */
  public static long gcd(long a, long b) {

    // gcd(0,b) == b
    // gcd(a,0) == a
    // gcd(0,0) == 0
    if (a == 0L) return abs(b);
    if (b == 0L) return abs(a);

    if (a != Long.MIN_VALUE && b != Long.MIN_VALUE) {
      return gcdUnsigned(abs(a), abs(b));
    } else {
      return gcdSigned(a, b);
    }
  }

  /**
   * Calculates greatest common divisor of two positive long integers. As the input
   * values are known to be positive integers, it can use some specific
   * optimizations.
   *
   * @param a positive integer value a
   * @param b positive integer value b
   * @return greatest common divisor
   */
  private static long gcdUnsigned(long a, long b) {

    assert a > 0L;
    assert b > 0L;

    // handle powers of 2
    int shift = 0;
    while (even(a) && even(b)) {
      a >>= 1;
      b >>= 1;
      shift++;
    }

    while (even(a)) a >>= 1;

    assert odd(a);

    do {
      while (even(b)) {
        b >>= 1;
      }

      if (a > b) {
        long t = b;
        b = a;
        a = t;
      }
      b = b - a;
    } while (b != 0L);

    return a << shift;
  }

  /**
   * Calculate greatest common divisor of two long integers. If 0 is passed to one
   * parameter, the other parameter is returned.
   *
   * @param a integer value (not zero) a
   * @param b integer value (not zero) b
   * @return greatest common divisor
   * @throws ArithmeticException if the GCD results in 2<sup>63</sup>, which can not be
   *                             represented in an integer. This can only happen
   *                             if you pass Long.MIN_VALUE to one of its
   *                             values.
   */
  private static long gcdSigned(long a, long b) {

    assert a != 0L;
    assert b != 0L;

    // make sure both values have same (negative) sign
    if (a > 0L) a = -a;
    if (b > 0L) b = -b;

    // handle powers of 2
    int shift = 0;
    while (even(a) && even(b) && shift < 31) {
      a /= 2L;
      b /= 2L;
      shift++;
    }

    // Check if return value is 2^63 as this can't be represented in a
    // signed 64-bit integer
    if (shift == 63) {
      throw new ArithmeticException("Integer overflow, can't represent 2^63");
    }

    long tmp = odd(a) ? b : -(a / 2L);
    do {
      while (even(tmp)) {
        tmp /= 2L;
      }
      if (tmp > 0L) {
        a = -tmp;
      } else {
        b = tmp;
      }
      tmp = (b - a) / 2L;
    } while (tmp != 0L);
    return -a * (1L << shift);
  }

  /**
   * Determines if the value is even
   *
   * @param a value to test
   * @return true if the value is even
   */
  public static boolean even(long a) {
    return (a & 1) == 0;
  }

  /**
   * Determines if the value is odd
   *
   * @param a value to test
   * @return true if the value is odd
   */
  public static boolean odd(long a) {
    return (a & 1) == 1;
  }

  /**
   * Performs a polynomial fitting
   *
   * @param xs x-values
   * @param ys y-values
   * @param order    order of the polynomial fit (should be less than
   *                 number of
   *                 points).
   * @return polynomial coefficients
   */
  public static double[] polyfit(double[] xs, double[] ys, int order) {
    int xLength = xs.length;
    int yLength = ys.length;

    if (xLength != yLength) {
      throw new IllegalArgumentException("x and y data arrays should " +
                                             "have same length");
    }
    if (xLength <= 1) {
      throw new IllegalArgumentException("at least two points are " +
                                             "required");
    }
    if (order < 0) {
      throw new IllegalArgumentException("order should be positive");
    }
    if (order >= xLength) {
      throw new IllegalArgumentException("order equal or larger than " +
                                             "number of input values: " + order);
    }

    // Matrix from y values
    Matrix yMatrix = Matrix.columnPacked(yLength, ys);

    // Vandermonde matrix
    Matrix vandermonde = Matrix.vandermonde(order + 1, xs);

    // matrix qr decomposition
    QR qr = vandermonde.qr();

    // determine R
    Matrix upperTriangularMatrix = qr.getR();

    // determine result matrix
    Matrix result = upperTriangularMatrix
                        .invert()
                        .get()
                        .multiply(qr.getQ().transpose().multiply(yMatrix));

    // determine polynomial coefficients
    return result.transpose().row(0).reverse().toArray();
  }

  /**
   * Calculates the polynomial for the specified x value.
   *
   * @param x x value
   * @param coefficients polynomial coefficients
   * @return  result of the polynomial calculation
   */
  public static double polynomial(double x, double... coefficients) {

    // check coefficients length
    if (coefficients.length == 0) {
      return 0;
    }

    // coefficient 0
    double y = coefficients[0];

    // Calculate polynomial terms by iterating over the coefficients
    // using variable xn to hold the value of x^n.
    // This will speed up the calculation as this is much faster than
    // calling Math.pow constantly.
    int    count = coefficients.length;
    double xn     = x;

    for (int coefficient = 1; coefficient < count; coefficient++)
    {
      y  += coefficients[coefficient] * xn;
      xn *= x;
    }

    return y;
  }


  /**
   * Calculates the mean of a number of values.
   *
   * @param values input values
   * @return median of the values
   */
  public static double median(double... values) {
    if (values.length == 0) {
      throw new IllegalArgumentException("at least one values should be" +
                                             " given");
    }

    double median = values[0];

    if (values.length > 1) {

      Arrays.sort(values);

      int index = values.length / 2;

      // length is even, use the average of the two middle values
      if ((values.length % 2) == 0) {
        median = (values[index] + values[index - 1]) * 0.5;
      }

      // length is odd, use the median
      else {
        median = values[index];
      }
    }

    return median;
  }


  /**
   * Calculates the standard deviation for samples.
   *
   * @param samples samples
   * @return standard deviation
   */
  public static double standardDeviation(double... samples) {
    return standardDeviation(false, samples);
  }


  /**
   * Calculates the standard deviation.
   *
   * @param values     samples
   * @param population flag that indicates the data is a complete
   *                   population (false indicates a
   *                   sample).
   * @return standard deviation
   */
  public static double standardDeviation(boolean population, double... values) {
    return java.lang.Math.sqrt(variance(population, values));
  }


  /**
   * Calculates the variance using a compensated-summation algorithm.
   *
   * @param population indicates the values are the complete population,
   *                   instead of
   *                   samples
   * @param values     the values
   * @return the variance
   */
  public static double variance(boolean population, double... values) {
    double mean = mean(values);
    double sum = 0;
    double compensation = 0;
    double n = values.length;
    double denominator = population ? n : (n - 1);

    for (double value : values) {
      double delta = value - mean;

      sum += delta * delta;
      compensation += delta;
    }

    return (sum - ((compensation * compensation) / n)) / denominator;
  }


  /**
   * Calculates the average for the specified values.
   *
   * @param values input values
   * @return average of the values
   */
  public static double mean(double... values) {
    return sum(values) / values.length;
  }


  /**
   * Utility method for summing up values.
   *
   * @param values The input values
   * @return the sum of the values
   */
  private static double sum(double... values) {
    double sum = 0;

    for (double value : values) {
      sum += value;
    }

    return sum;
  }


  /**
   * Calculates the variance of samples.
   *
   * @param values samples
   * @return the variance
   */
  public static double variance(double... values) {
    return variance(false, values);
  }

  /**
   * Get a prime number. This method allows access to the first 10.000 prime
   * numbers (all primes from 2 up to 104729).
   * @param index The index of the prime number in the range [0-9999]
   * @return The prime number.
   * @throws IndexOutOfBoundsException for index greater than 9999
   */
  public static int prime(int index) {
    // delegated to Primes class to lazily load known primes
    return Primes.prime(index);
  }

  /**
   * Determines if a value is a prime number. This method is limited
   * to the first 10.000 prime numbers (prime 2...104729).
   * @param value the value to test
   * @return true if the value is a prime number
   * @throws IllegalArgumentException for values exceeding 104729
   */
  public static boolean isPrime(int value) {
    // delegated to Primes class to lazily load known primes
    return Primes.isPrime(value);
  }

}
