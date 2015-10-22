package services;

import util.StringUtil;

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


/** @author Daniel Bechler */
public final class MD5
{
	private MD5()
	{
	}

	public static String forString(final String text)
	{
		if (StringUtil.isEmpty(text))
		{
			throw new IllegalArgumentException();
		}
		return HashCodeBuilder.md5(text);
	}

	public static String forBytes(final byte[] bytes)
	{
		return HashCodeBuilder.md5(bytes);
	}
}