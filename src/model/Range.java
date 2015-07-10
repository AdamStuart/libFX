package model;

public class Range
{
	double min;
	double max;
	double min() { return min;	}
	double max() { return max;	}
	double width() { return max-min;	}
	public Range(float mini, float maxi)	{		min = mini; max = maxi;	}
	public Range(double mini, double maxi)	{		min = mini; max = maxi;	}
	public Range(int mini, int maxi)		{		min = mini; max = maxi;	}
}
