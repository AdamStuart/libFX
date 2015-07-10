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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/** @author Daniel Bechler */
public final class UrlParameters
{
	public static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");
	private final Map<String, String> parameters = new TreeMap<String, String>();

	public UrlParameters set(final String name, final String value)
	{
		if (value != null)			parameters.put(name, value);
		else						parameters.remove(name);
		return this;
	}
//@formatter: off
	public boolean has(final String name)	{		return parameters.containsKey(name);	}
	public String get(final String name)	{		return parameters.get(name);	}
	public int size()						{		return parameters.size();	}
	public boolean isEmpty()				{		return size() == 0;	}
	public Iterable<String> getParameterNames()	{		return new TreeSet<String>(parameters.keySet());	}

	public void merge(final UrlParameters parameters)
	{
		if (parameters == null)			return;
		for (final String name : parameters.getParameterNames())
			set(name, parameters.get(name));
	}

	public String toQueryString(final Charset charset)
	{
		final StringBuilder sb = new StringBuilder();
		final Iterator<String> namesIterator = getParameterNames().iterator();
		while (namesIterator.hasNext())
		{
			final String name = namesIterator.next();
			final String value = get(name);
			sb.append(encodedKeyValueStringOf(name, value, charset));
			if (namesIterator.hasNext())
				sb.append('&');
		}
		return sb.toString();
	}

	private static String encodedKeyValueStringOf(final String name, final String value, final Charset charset)
	{
		final String encodedValue = (StringUtils.hasText(value)) ? encode(value, charset.name()) : "";
		return new StringBuilder(name).append('=').append(encodedValue).toString();
	}

	private static String encode(final String value, final String encoding)
	{
		try
		{
			return URLEncoder.encode(value, encoding);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new IllegalArgumentException("The given encoding is not supported.", e);
		}
	}

	@Override
	public String toString()	{		return toQueryString(DEFAULT_CHARSET);	}
	public String toString(final Charset charset)	{		return toQueryString(charset);	}
}