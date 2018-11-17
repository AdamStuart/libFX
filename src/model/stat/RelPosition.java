package model.stat;

import javafx.geometry.Pos;
import javafx.util.Pair;
import util.StringUtil;

public class RelPosition //extends Pair<Double, Double>
{
	double key, value;
	
	public RelPosition(double k, double v)
	{
		key = k;
		value = v;
	}

	public double x()	{ return key;	}
	public double y()	{ return value;	}
	
	public String toString()	{ return String.format("(%.02f, %.02f)" , key,value);	}
	public static RelPosition ZERO = new RelPosition(0,0);
	
	// values "1" ... "9" coming from port ids
	public static RelPosition idToRelPosition(String id)
	{
		double relX, relY;
		if (!StringUtil.isInteger(id)) return RelPosition.ZERO;
		int i = StringUtil.toInteger(id) - 1;

		if (i % 3 == 0) relX = -1;
		else if (i % 3 == 1) relX = 0;
		else relX= 1;

		if (i < 3) relY = -1;
		else if (i < 6) relY = 0;
		else relY = 1;
		
		return new RelPosition(relX,relY);
	}
	
	
	public static Pos idToPosition(String id)
	{
		if (StringUtil.isInteger(id))
		{
			int i = StringUtil.toInteger(id) - 1;
			if (i < Pos.values().length)
				return Pos.values()[i];
		}
		return Pos.CENTER;
	}

	
	public static RelPosition toRelPos(Pos pos) {
		String name = pos.name();
		double relX = 0, relY = 0;

		if (name.contains("LEFT")) relX = -1;
		else if (name.contains("RIGHT")) relX = 1;
		
		if (name.contains("TOP")) relY = -1;
		else if (name.contains("BOTTOM")) relY = 1;

		return new RelPosition(relX, relY);
	}

//
//
//	private double getAdjustmentX(Pos srcPosition, double nodeWidth) {
//	
//		String s = srcPosition.name();
//		if (s.contains("LEFT")) 	return -nodeWidth / 2;
//		if (s.contains("RIGHT")) 	return nodeWidth / 2;
//		return 0;
//}
//	private double getAdjustmentY(Pos srcPosition, double nodeHeight) {
//		
//		String s = srcPosition.name();
//		if (s.contains("TOP")) 		return -nodeHeight / 2;
//		if (s.contains("BOTTOM")) 	return nodeHeight / 2;
//		return 0;
//}
}
