package gui;

import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class Borders
{
	static public Border redBorder = new Border(new BorderStroke(Color.RED, 
					BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5))	);

	static public Border etchedBorder = new Border(
					new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)),
					new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(2)),
					new BorderStroke(Color.WHEAT, BorderStrokeStyle.DOTTED, CornerRadii.EMPTY, new BorderWidths(1))
					);

	static public Border lineBorder = new Border(new BorderStroke(Color.DARKGRAY, 
					BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))	);

	static public Border dashedBorder = new Border(new BorderStroke(Color.DARKGRAY, 
					BorderStrokeStyle.DASHED, CornerRadii.EMPTY, new BorderWidths(2))	);

}
