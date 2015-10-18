package gui;

import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.SepiaTone;
import javafx.scene.paint.Color;

public class Effects
{
	public static final Effect sepia = new SepiaTone();
	public static Effect innershadow =  new InnerShadow(4d, 4d, 4d, Color.web("#2FD6FF"));
}
