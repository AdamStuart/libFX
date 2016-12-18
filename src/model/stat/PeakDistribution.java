package model.stat;

public class PeakDistribution
{
	double mean = 0.;
	double stDev = 0.;
	
	PeakDistribution()
	{
		
	}
	
	PeakDistribution(double m, double s)
	{
		mean = m;
		stDev = s;
	}
	
	
	double valueAt(double x)
	{
		double exp = ((x-mean) * (x-mean)) / (2 * stDev  * stDev);
		double scale = 1 / (stDev) * Math.sqrt(2 * Math.PI);
		return scale * Math.exp(exp);
	}
	
}
