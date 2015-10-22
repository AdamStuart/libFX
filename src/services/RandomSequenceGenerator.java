package services;

/*
 * Copyright 2012 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.Random;

import javafx.geometry.Point2D;

/** @author Daniel Bechler */
public class RandomSequenceGenerator
{
	public static final String DEFAULT_ALPHABET =
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private RandomSequenceGenerator()
	{
	}

	public static String generate(final int length)
	{
		return generate(length, DEFAULT_ALPHABET);
	}

	public static String generate(final int length, final CharSequence alphabet)
	{
		final Random random = new Random(System.currentTimeMillis());
		final StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++)
		{
			sb.append(alphabet.charAt(random.nextInt(alphabet.length())));
		}
		return sb.toString();
	}
	
	//-----------------------------------------------------------------
	// Box-Mueller method to generate values in a normal distribution
	// http://en.wikipedia.org/wiki/Normal_distribution#Generating_values_from_normal_distribution
	static double TAO = 2.0 * Math.PI;

	public static Point2D randomNormal(double xMean, double xCv, double yMean, double yCV)
	{
		double U = Math.random();
		double V = Math.random();
		double xStdev = xCv * xMean;
		double yStdev = yCV * yMean;
		double x = Math.sqrt(-2.0 * Math.log(U)) * Math.cos(TAO * V);
		double y = Math.sqrt(-2.0 * Math.log(U)) * Math.sin(TAO * V);
		return new Point2D(xMean + x * xStdev,yMean + y * yStdev);
	}

}