package model;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;


public class XYModel extends  ArrayList<XYDataRow>
{
	double targetX, targetY;
	double totalX = 0, totalY = 0;
	double totalXVar = 0, totalYVar = 0;
	double xMetric = 0, yMetric = 0;
	double xMean = 0, yMean = 0;
	double xMedian = 0, yMedian = 0;
	double xVar = 0, yVar = 0;
	double xCV = 0, yCV = 0;

	double totalVar = 0;
	double var = 0;
	boolean calculated = false;
	//-----------------------------------------------------------------------------
	public XYModel(double x, double y)
	{
		targetX = x;
		targetY = y;
	}
	//-----------------------------------------------------------------------------
	public void calculate()
	{
	    int count = size();
	    for (XYDataRow row : this)
	    {
	    	double x = row.getX();
	    	double y = row.getY();
	    	totalX += x;
	    	totalY += y;
	    	double dx = targetX - x;
	    	double dy = targetY - y;
	    	totalXVar += dx * dx;
	    	totalYVar += dy * dy;
	    	totalVar += Math.sqrt((dx * dx) + ( dy * dy));
	    }
	    xMean = totalX / count;
	    yMean = totalY / count;
	    xVar = totalXVar / (count-1);
	    yVar = totalYVar / (count-1);
	    var = totalVar / (count-1);
	    xMetric = Math.sqrt(xVar) / xMean;
	    yMetric = Math.sqrt(yVar) / yMean;
	    calculated = true;

	}
	
//	public SlingshotDataRow getSlingshotRow(int id,String name, long date )
//	{
//		if (!calculated) calculate();
//		return  new SlingshotDataRow(id, name, date, xMedian, yMedian, xCV, yCV, targetX, targetY, xVar, yVar, xMetric, yMetric, var);
//		
//	}
	
	public double[] getStats() { return new double[]{xMedian, yMedian, xCV, yCV, xVar, yVar, xMetric, yMetric, var, targetX, targetY }; }
	
	public void fillDataSeries(ObservableList<Data<Number, Number>> dataSeries)
	{
		for (XYDataRow row : this)
			dataSeries.add(new XYChart.Data<Number, Number>(row.getX(),row.getY()));
	}
}