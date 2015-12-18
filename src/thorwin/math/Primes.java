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

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * This class contains the first 10.000 prime numbers. This class is used
 * by the <code>Math.prime</code>. This table is stored in a separate class
 * so the data will only be loaded when used.
 */
final class Primes {

  private static final int PRIME_COUNT = 10_000;

  static final int[] PRIMES = new int[PRIME_COUNT];
  static {
    try {
      try (DataInputStream in = new DataInputStream(Primes.class.getResourceAsStream("primes.bin"))) {
        for (int i = 0; i < PRIME_COUNT; i++) {
          PRIMES[i] = in.readInt();
        }
      }
    }
    catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Private constructor to prevent instantiation.
   */
  private Primes() {}

  /**
   * Get a prime number. This method allows access to the first 10.000 prime
   * numbers (all primes from 2 up to 104729).
   * @param index The index of the prime number in the range [0-9999]
   * @return The prime number.
   * @throws IndexOutOfBoundsException for index greater than 9999
   */
  static int prime(int index) {
    return PRIMES[index];
  }

  /**
   * Determines if a value is a prime number. This method is limited
   * to the first 10.000 prime numbers (prime 2...104729).
   * @param value the value to test
   * @return true if the value is a prime number
   * @throws IllegalArgumentException for values exceeding 104729
   */
  static boolean isPrime(int value) {

    // get the absolute value to determine prime number
    value = java.lang.Math.abs(value);

    if (value > PRIMES[PRIMES.length -1]) {
      throw new IllegalArgumentException("Value exceeds maximum known prime: " + value);
    }
    int index = Arrays.binarySearch(PRIMES, value);
    return index >= 0;
  }
}

