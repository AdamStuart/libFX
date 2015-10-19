package gui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

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
	public static Background colored(Color c) { return  new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY));  }
}
