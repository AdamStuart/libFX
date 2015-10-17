package gui;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class Backgrounds
{
	public static Background lightGray = new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background black = new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background tan = new Background(new BackgroundFill(Color.TAN, CornerRadii.EMPTY, Insets.EMPTY));
	public static Background colored(Color c) { return  new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY));  }
}
