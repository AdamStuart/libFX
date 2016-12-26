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
	
	public static void set(Line line, Point2D p1, Point2D p2)
	{
		if (line == null || p1 == null || p2 == null) return;
		if (Double.isNaN(p1.getX()) || Double.isNaN(p1.getY()))
						System.err.println("BAD start");
		if (Double.isNaN(p2.getX()) || Double.isNaN(p2.getY()))
						System.err.println("BAD end");
		line.setStartX(p1.getX());	line.setStartY(p1.getY());		
		line.setEndX(p2.getX()); 	line.setEndY(p2.getY());
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
		return getIntersection(line, node, 5);
	}	
	public static Point2D getIntersection(Line line, Node node, double padding)
	{
	    if (node == null) return new Point2D(line.getEndX(), line.getEndY());
	   Bounds b = node.getBoundsInParent();
	   double halfpadding =  padding / 2;
	   Rectangle r = new Rectangle(b.getMinX() - halfpadding, b.getMinY() - padding, b.getWidth() + padding, b.getHeight() + 2 * padding);
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


	/**
	 * Determines angle between two vectors defined by p1 and p2
	 * Both vectors start at 0.0
	 *
	 * @param p1 start point of vector
	 * @param p2 end point of vector
	 * @return angle in radians
	 */
	public static double angle(Point2D p1, Point2D p2)
	{
		//Angle:
		//					p1.p2
        //cos(angle) = --------------
        //          	||p1||*||p2||

		double cos = dot(p1,p2) / (p1.magnitude() * p2.magnitude());
		if(cos>1)
		{
			cos=1;
		}
		return direction(p1,p2) * Math.acos(cos);
	}

	/**
	 * negative: ccw positive: cw
	 */
	public static double direction(Point2D p1, Point2D p2)
	{
		return Math.signum(p1.getX() * p2.getY() - p1.getY() * p2.getX());
	}

	/**
	 * dot product
	 */
	private static double dot(Point2D v1, Point2D v2)
	{
		double[] d1 = { v1.getX(), v1.getY() };  //v1.asArray();
		double[] d2 = { v2.getX(), v2.getY() };  //v2.asArray();
		double sum = 0;
		for(int i = 0; i < Math.min(d1.length, d2.length); i++) 
			sum += d1[i]*d2[i];
		return sum;
	}

	/**
	   Projection of point q on a line through p with direction vector v

	   If p is 0,0, it's the same as the two-argument function with the same name.
	 */
	public static Point2D project (Point2D p, Point2D q, Point2D v)
	{
		Point2D q2 = new Point2D (q.getX() - p.getX(), q.getY() - p.getY());
		double vlen = dot (v, v);
		if (vlen == 0)
		{
			return p;
		}
		else
		{
			double c = dot (q2, v) / dot (v, v);
			return new Point2D (p.getX() + v.getX() * c, p.getY() + v.getY() * c);
		}
	}

	/**
	 * Convert a 2-D point to 1-D line coordinates (relative position on the line, range {0,1})
	 */
	public static double toLineCoordinates (Point2D start, Point2D end, Point2D p) {
		//Project v position on line and calculate relative position
		Point2D direction = start.subtract(end);
		Point2D projection = project(start, p, direction);
		double lineLength = distance(start, end);
		double anchorLength = distance(start, projection);
		double position = anchorLength / lineLength;

		double ldir = direction(start, end);
		double adir = direction(start, projection);
		if(adir != ldir) {
			position = 0;
		}
		if(position > 1) position = 1;
		if(position < 0) position = 0;
		if(Double.isNaN(position)) position = 0;
		return position;
	}
	/**
	  Projection of p1 on p2:

	   p1.p2
	   ----- . p2
	   p2.p2
	*/
	public static Point2D project(Point2D p1, Point2D p2)
	{
		double c = dot(p1, p2) / dot(p2, p2);
		return new Point2D(p2.getX() * c, p2.getY() * c);
	}

	public static double distance(Point2D p1, Point2D p2)
	{
		Point2D dp = p2.subtract(p1);
		return dp.magnitude();
	}

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
