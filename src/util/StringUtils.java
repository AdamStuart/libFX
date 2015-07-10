package util;

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

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** @author Daniel Bechler */
public class StringUtils
{
	private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("\\s*\\n\\s*");

	private StringUtils()
	{
	}

	public static boolean hasText(final String s)	{	return s != null && s.trim().length() > 0;	}
	public static boolean isEmpty(final String s)	{		return !hasText(s);	}

	public static String capitalize(final String s)
	{
		if (s != null && s.length() > 0)
		{
			final char[] chars = s.toCharArray();
			chars[0] = Character.toUpperCase(chars[0]);
			return new String(chars).intern();
		}
		return s;
	}

	public static String toPropertyExpression(final String s)
	{
		final char[] chars = s.toCharArray();
		final StringBuilder sb = new StringBuilder();
		char previousChar = ' ';
		for (final char aChar : chars)
		{
			if (aChar != '_')
			{
				if (previousChar == '_')
					sb.append(Character.toUpperCase(aChar));
				else
					sb.append(Character.toLowerCase(aChar));
			}
			previousChar = aChar;
		}
		return sb.toString();
	}

	/**
	 * Converts an optionally camel-cased character sequence (e.g. ThisIsSparta) into underscore-case (e.g.
	 * this_is_sparta).
	 *
	 * @param s The text to convert.
	 *
	 * @return A underscore-cased version of the given text.
	 */
	public static String toUnderscoreCase(final String s)
	{
		final char[] chars = s.toCharArray();
		final StringBuilder sb = new StringBuilder();
		char previousChar = 0;
		for (final char aChar : chars)
		{
			if (Character.isUpperCase(aChar))
			{
				if (previousChar != 0)
					sb.append('_');
				sb.append(Character.toLowerCase(aChar));
			}
			else
				sb.append(aChar);
			previousChar = aChar;
		}
		return sb.toString();
	}

	/**
	 * Joins all non-null elements of the given <code>elements</code> into one String.
	 *
	 * @param delimiter Inserted as separator between consecutive elements.
	 * @param elements  The elements to join.
	 *
	 * @return A long string containing all non-null elements.
	 */
	public static String join(final String delimiter, final Object... elements)
	{
		final StringBuilder sb = new StringBuilder();
		for (final Object part : elements)
		{
			if (part == null)				continue;
			if (sb.length() > 0)		sb.append(delimiter);
			sb.append(part.toString());
		}
		return sb.toString();
	}

	/**
	 * Same as {@link #join(String, Object...)} but with a {@link java.util.Collection} instead of an Array
	 * for the elements.
	 *
	 * @see #join(String, java.util.Collection)
	 */
	public static String join(final String delimiter, final Collection<?> elements)
	{
		if (elements == null || elements.isEmpty())
			return "";
		return join(delimiter, elements.toArray(new Object[elements.size()]));
	}

	public static String toSingleLineString(final Object object)
	{
		if (object != null)
		{
			final String s = object.toString().trim();
			final Matcher matcher = LINE_BREAK_PATTERN.matcher(s);
			return matcher.replaceAll(" \\\\ ");
		}
		return null;
	}

	public static String randomSequence(final int length)
	{
		return RandomSequenceGenerator.generate(length);
	}

	public static String randomSequence(final int length, final CharSequence alphabet)
	{
		return RandomSequenceGenerator.generate(length, alphabet);
	}
}