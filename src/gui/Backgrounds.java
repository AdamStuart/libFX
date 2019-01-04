package gui;

import com.sun.prism.shader.FillCircle_Color_AlphaTest_Loader;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import util.ParseUtil;

public class Backgrounds
{
	public static final String whiteStr = "-fx-background-color: white;";
	public static final String ltYellow = "-fx-background-color: #fffff2;";
	public static final String lightGrayStr = "-fx-background-color: lightgray";
	public static final String blueStr = "-fx-background-color: blue";
	public static final String tanStr = "-fx-background-color: tan";
	public static void set(Node n, String string)	{	n.setStyle("-fx-background-color: " + string + "; ");  }
	
	public static Background lightGray = colored(Color.LIGHTGRAY);
	public static Background black = colored(Color.BLACK);
	public static Background tan = colored(Color.TAN);
	public static Background white = colored(Color.WHITE);
	public static Background whitesmoke = colored(Color.WHITESMOKE);
	public static Background colored(String s) { return  colored(Color.valueOf(s));  }
	public static Background colored(Color c) { return  new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY));  }
	public static Background transparent() { return  colored(Color.TRANSPARENT);  }
	
	public static Paint lightPaint = Color.WHITESMOKE;
	public static Paint darkPaint = Color.STEELBLUE;
	public static Paint sand = Color.TAN;
	
	public static Paint stdBackground = Color.TAN;

	public static Stop[] stops = new Stop[] { new Stop(0, Color.WHITESMOKE), new Stop(1, Color.WHITE)};
	public static LinearGradient whiteGradient = new LinearGradient(0, 0, 225, 200, false, CycleMethod.REFLECT, stops);
	
	//technique from: https://stackoverflow.com/questions/28870460/how-to-create-a-background-grid
	public static String backgroundGridTemplate =
					"-fx-background-color: %s,\n" + 
					"        linear-gradient(from 0.5px 0px to %d.5px 0px, repeat, black 5%%, transparent 5%%),\n" + 
					"        linear-gradient(from 0px 0.5px to 0px %d.5px, repeat, black 5%%, transparent 5%%);";
	
	public static String getBackgroundGridStyle(int gridSize, Color gridColor) {
		String style = String.format(backgroundGridTemplate, ParseUtil.colorToHex(gridColor), gridSize, gridSize);
		
		return style;
	}
	
	/** Creates a paint which renders as vertical or horizontal lines spaced by gridSize pixels.
	 * */
	public static Paint getGridLinesPaint(int gridSize, double thickness, Color gridColor, boolean vertical) {
		double startX = 0;
		double startY = 0;
		double endX = vertical ? gridSize : 0;
		double endY = vertical ? 0 : gridSize;
		
		double lineWidth = thickness / gridSize;
		LinearGradient paint = new LinearGradient(startX, startY, endX, endY, 
						false, CycleMethod.REPEAT,
						new Stop(lineWidth, gridColor), new Stop(lineWidth, Color.TRANSPARENT));
		
		return paint;
	}

}
