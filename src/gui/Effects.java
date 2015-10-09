package gui;

import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.effect.SepiaTone;
import javafx.scene.paint.Color;

public class Effects
{
	public static Effect red_innershadow = new InnerShadow(10, Color.web("#FF6666"));
	public static Effect reflection = new Reflection(10,0.5,1.0, 0.5);
	public static Effect sepia = new SepiaTone();
	

	
}
