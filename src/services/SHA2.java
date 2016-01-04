package services;

import java.io.File;

import util.FileUtil;
import util.StringUtil;



public final class SHA2
{
	private SHA2()
	{
	}

	public static String forString(final String text)
	{
		if (StringUtil.isEmpty(text))
		{
			throw new IllegalArgumentException();
		}
		return HashCodeBuilder.sha256(text);
	}

	public static String forBytes(final byte[] bytes)
	{
		return HashCodeBuilder.sha256(bytes);
	}

	public static String forFile(File f)
	{
		try
		{
			byte[] bytes = FileUtil.readAsBytes(f);
			return forBytes(bytes);
		}
		catch(Exception e) { return null; 	} 
	}
}