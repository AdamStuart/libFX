package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil
{

	static public String inject(String full, String after, String until, String insert)
	{
		int idx1 = full.indexOf(after) + after.length();
		int idx2 = full.indexOf(until, idx1);
		return full.substring(0,idx1) + insert + full.substring(idx2);
	}

	static public String chopLast(String in)	{	return in.substring(0,in.length()-1);	}
	static public String chopLast2(String in)	{	return in.substring(0,in.length()-2);	}

//	static public ObservableList<String> lines(String in)
//	{
//		ObservableList<String> strs = FXCollections.observableArrayList();
//		return strs;
	// }

	public static String callURL(String urlString)
	{

		StringBuffer buffer = new StringBuffer();
		try
		{
			URL url = new URL(urlString);
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String strTemp = "";
			while (null != (strTemp = br.readLine()))
				buffer.append(strTemp);
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return buffer.toString();
	}
	
	public enum TYPES { EMPTY, DATE, DOUBLE, STRING };

	public static boolean isEmpty(TYPES t)  { return t == TYPES.EMPTY; }
	public static boolean isString(TYPES t)  { return t == TYPES.STRING; }
	public static boolean isNumber(TYPES t)  { return t == TYPES.DOUBLE; }
	
	static public TYPES inferType(String s)
	{
		if (s.isEmpty()) return TYPES.EMPTY;
		if (isDate(s)) return TYPES.DATE;
		if (isNumber(s)) return TYPES.DOUBLE;
		return TYPES.STRING;
	}
	
	public static String gensym()
	{
	
		return ("" + Math.random()).substring(0,8);
	}
	public static String decapitalize(final String s)
	{
		return s.substring(0, 1) + s.substring(1).toLowerCase();  
	}

	static public List<StringUtil.TYPES> inferTypes(String[] s)
	{
		List<StringUtil.TYPES> types = new ArrayList<StringUtil.TYPES>();
		for (String str : s)
			types.add(inferType(str));
		return types;
	}
	
	List<StringUtil.TYPES> types = new ArrayList<StringUtil.TYPES>();

	public static boolean isDate (String s) 
	{
		try
		{
			DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
			LocalDate.parse(s, formatter);
			return true;
		}
		catch(DateTimeParseException e)
		{
			return false;
		}
	}

	
	public static boolean isNumber (String s) 
	{
		try
		{
			Double.parseDouble(s);
			return true;
		}
		catch(NumberFormatException e)
		{
			return false;
		}
	}
	
	public static double toDouble (String s) 
	{
		if (s == null ) return Double.NaN;
		try
		{
			return Double.parseDouble(s);
		}
		catch(NumberFormatException e)
		{
			return Double.NaN;
		}
	}
	public static int toInteger (String s) 
	{
		if (s == null ) return -1;
		try
		{
			return Integer.parseInt(s);
		}
		catch(NumberFormatException e)
		{
			return -1;
		}
	}
	private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("\\s*\\n\\s*");

//-------------------------------------------------------
	/** @author Daniel Bechler */

	
	public static boolean hasText(final String s)	{	return s != null && s.trim().length() > 0;	}
	public static boolean isEmpty(final String s)	{		return !hasText(s);	}
	public static boolean anyEmpty(final String ... s)	
	{	for (String a : s) 
			if (isEmpty(a)) return true; 
		return false;
	}

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

	public static Object singleQuote(String content)	
	{	
		return "'" + content.replaceAll("'", "") + "'";	
	}

}
