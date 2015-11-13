package model;

public class Range
{
	double min;
	double max;
	public double min() { return min;	}
	public double max() { return max;	}
	public double width() { return max-min;	}
	public Range(float mini, float maxi)	{		min = mini; max = maxi;	}
	public Range(double mini, double maxi)	{		min = mini; max = maxi;	}
	public Range(int mini, int maxi)		{		min = mini; max = maxi;	}
	public String toString()				{ return "[" + min + " - " + max + "]"; }
}
