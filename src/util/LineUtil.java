package util;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;

public class LineUtil
{
	public static double getLength(Line line)
	{
		double x1 = line.getStartX();
		double x2 = line.getStartY();
		double y1 = line.getEndX();
		double y2 = line.getEndY();
		return Math.sqrt(((x2-x1) * (x2-x1)) + ((y2-y1) * (y2-y1)));
	}
	// **-------------------------------------------------------------------------------
	
	public static void set(Line line, double x1, double y1, double x2, double y2)
	{
		line.setStartX(x1);	line.setStartY(y1);		line.setEndX(x2);		line.setEndY(y2);

	}
	public static void set(Line line, Line orig)
	{
		set(line, orig.getStartX(), orig.getStartY(), orig.getEndX(),orig.getEndY());

	}
	public static void translateLine(Line line, double dx, double dy)
	{
		set(line, line.getStartX() + dx, line.getStartY() + dy, line.getEndX() + dx, line.getEndY() + dy);
	}
	static double EPSILON = 4;
	// **-------------------------------------------------------------------------------
	static public int onVertex(Point2D pt, Polygon p) {		return onVertex(pt, p.getPoints());		}
	static public int onVertex(Point2D pt, Polyline p) {	return onVertex(pt, p.getPoints());		}

	static public int onVertex(Point2D pt, ObservableList<Double>p ) {
		Object[] pts = p.toArray();
		if (pt == null) return -1;
		for (int i = 0; i < pts.length; i += 2)
			if (Math.abs(pt.getX() - (double) pts[i]) < EPSILON)
				if (Math.abs(pt.getY() - (double) pts[i + 1]) < EPSILON)
					return i;
		return -1;
	}
	// **-------------------------------------------------------------------------------
	static public boolean onEdge(Point2D pt, Circle c) {
		double d2 = (pt.getX() - c.getCenterX()) * (pt.getX() - c.getCenterX())
				+ (pt.getY() - c.getCenterY()) * (pt.getY() - c.getCenterY());
		double dist = Math.sqrt(d2);
		return Math.abs(dist - c.getRadius()) < EPSILON;
	}
	// **-------------------------------------------------------------------------------
	static public int onEndpoint(Point2D pt, Line p) {
		if (Math.abs(pt.getX() - p.getStartX()) < EPSILON)
			if (Math.abs(pt.getY() -p.getStartY()) < EPSILON)
					return 0;
		if (Math.abs(pt.getX() - p.getEndX()) < EPSILON)
			if (Math.abs(pt.getY() -p.getEndY()) < EPSILON)
					return 1;
		return -1;
	}

	// **-------------------------------------------------------------------------------
	public static double getStartAngle(Line line)
	{
		double x1 = line.getStartX();
		double y1 = line.getStartY();
		double x2 = line.getEndX();
		double y2 = line.getEndY();
		double dx = x2 - x1;
		double dy = y2 - y1;
		return Math.atan2(dy, dx);
	}
	
	public static double getEndAngle(Line line)
	{
		return -1 * getStartAngle(line);
	}
	// **-------------------------------------------------------------------------------

	public static Point2D midPoint(Line line, double d)
	{
		double x1 = line.getStartX();
		double y1 = line.getStartY();
		double x2 = line.getEndX();
		double y2 = line.getEndY();
		double dx = x2 - x1;
		double dy = y2 - y1;
		double x = x1 + d * dx;
		double y = y1 + d * dy;
		return new Point2D(x,y);
	}
	
	   public static Point2D getIntersection(Line line, Node node)
	{
	    if (node == null) return new Point2D(line.getEndX(), line.getEndY());
	   Bounds b = node.getBoundsInLocal();
	   double padding = 8;
	   Rectangle r = new Rectangle(b.getMinX() - padding, b.getMinY() - padding, b.getWidth() + 2 * padding, b.getHeight() + 2 * padding);
	   return getIntersection(line, r);
	}
	   
   public static Point2D getIntersection(Line line, Rectangle r)
    {
        double x, y;
    	double left = r.getX();
    	double right = r.getX() + r.getWidth();
    	double top = r.getY();
    	double bottom = r.getY() + r.getHeight();
    	double x1 = line.getStartX(), y1 = line.getStartY();
    	double x2 = line.getEndX(), y2 = line.getEndY();
    	
    	boolean endptInR = isBetween(x2, left, right) && isBetween(y2, top, bottom);
    	if (!endptInR)  return new Point2D(x2, y2);
    	if (x1 == x2)			// avoid the div by 0 case
    	{
    		x = x1;
    		y = (line.getStartY() < line.getEndY()) ? top : bottom;
        	return new Point2D(x, y);
    	}
    	double dy = (y2 - y1);
    	double dx = (x2 - x1);
    	double slope = dy / dx;
    	// handle intersection with top or bottom
    	y = (y2 > y1) ? top : bottom;
    	x =  ((y - y2) / slope) + x2;
    	if (isBetween(x, left, right))     	return new Point2D(x, y);

    	// handle intersection with left or right
    	x = (x2 > x1) ? left : right;
    	y = ((x - x2 ) * slope) + y2;
    	if (isBetween(x, left, right))     	return new Point2D(x, y);
    	return new Point2D(x2, y2);
    }
	
    private static boolean isBetween(double x, double min, double max)	{		return x >= min && x <= max;	}


  //http://stackoverflow.com/questions/19748744/javafx-how-to-connect-two-nodes-by-a-line
    /**
     * Evaluate the cubic curve at a parameter 0<=t<=1, returns a Point2D
     * @param c the CubicCurve 
     * @param t param between 0 and 1
     * @return a Point2D 
     */
    public static Point2D eval(CubicCurve c, float t){
  	  double x = Math.pow(1-t,3)*c.getStartX()+ 3*t*Math.pow(1-t,2)*c.getControlX1()+
                3*(1-t)*t*t*c.getControlX2()+ Math.pow(t, 3)*c.getEndX();
  	  double y = Math.pow(1-t,3)*c.getStartY()+ 3*t*Math.pow(1-t, 2)*c.getControlY1()+
                3*(1-t)*t*t*c.getControlY2()+ Math.pow(t, 3)*c.getEndY();
        Point2D p=new Point2D(x,y);
        return p;
    }

    /**
     * Evaluate the tangent of the cubic curve at a parameter 0<=t<=1, returns a Point2D
     * @param c the CubicCurve 
     * @param t param between 0 and 1
     * @return a Point2D 
     */
    public static  Point2D evalDt(CubicCurve c, float t){
        Point2D p=new Point2D(
      		  -3*Math.pow(1-t,2)*c.getStartX()+3*(Math.pow(1-t, 2)-2*t*(1-t))*c.getControlX1()+
         3*((1-t)*2*t-t*t)*c.getControlX2()+3*Math.pow(t, 2)*c.getEndX(),
                -3*Math.pow(1-t,2)*c.getStartY()+3*(Math.pow(1-t, 2)-2*t*(1-t))*c.getControlY1()+
          3*((1-t)*2*t-t*t)*c.getControlY2()+3*Math.pow(t, 2)*c.getEndY());
        return p;
    }

}
