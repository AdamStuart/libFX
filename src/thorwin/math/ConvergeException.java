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

/**
 * Exception thrown for algorithms that (unexpectedly) do not converge.
 */
public class ConvergeException extends ArithmeticException {
  /**
   * Constructs an {@code ArithmeticException} with no detail message.
   */
  public ConvergeException() {
    super();
  }


  /**
   * Constructs an {@code ArithmeticException} with the specified detail
   * message.
   *
   * @param s the detail message.
   */
  public ConvergeException(String s) {
    super(s);
  }
}
