package util;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;

public class LineUtil
{
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


}
