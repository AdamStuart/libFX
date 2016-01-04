package services;

/*
 * Copyright 2012 Daniel Bechler
 * added SHA256
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class HashCodeBuilder
{
	public enum HashCodeType
	{
		MD5,
		SHA1, 
		SHA256
	}

	private HashCodeBuilder()	{	}

	public static String md5(final String text)		{		return hash(text, HashCodeType.MD5);	}
	public static String sha1(final String text)	{		return hash(text, HashCodeType.SHA1);	}
	public static String sha256(final String text)	{		return hash(text, HashCodeType.SHA256);	}
	public static String md5(final byte[] bytes)	{		return hash(bytes, HashCodeType.MD5);	}
	public static String sha1(final byte[] bytes)	{		return hash(bytes, HashCodeType.SHA1);	}
	public static String sha256(final byte[] bytes)	{		return hash(bytes, HashCodeType.SHA256);	}
	
	public static String hash(final String text, final HashCodeType type)	{		return hash(text.getBytes(), type);	}

	public static String hash(final byte[] bytes, final HashCodeType type)	
	{		
		final byte[] hash;
		try
		{
			hash = MessageDigest.getInstance(type.name()).digest(bytes);
		}
		catch (final NoSuchAlgorithmException e)
		{
			throw new RuntimeException("Cannot generate MD5 hash. The " +
					"algorithm you wanted to use seems to be unknown to the JVM.", e);
		}
		final StringBuilder sb = new StringBuilder();
		for (final byte b : hash)
		{
			sb.append(toHexString(b));
		}
		return sb.toString();
	}

	private static String toHexString(final byte bytes)
	{
		final int value = (bytes & 0x7F) + (bytes < 0 ? 128 : 0);
		String ret = (value < 16 ? "0" : "");
		ret += Integer.toHexString(value).toLowerCase();
		return ret;
	}
}