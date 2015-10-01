package util;

import javafx.scene.shape.Line;

public class LineUtil
{
	public static void set(Line line, double x1, double y1, double x2, double y2)
	{
		line.setStartX(x1);	line.setStartY(y1);		line.setEndX(x2);		line.setEndY(y2);

	}
}
