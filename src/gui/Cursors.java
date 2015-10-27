package gui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import util.NodeUtil;

public class Cursors
{
	public static Cursor getResizeCursor(Pos p)
	{
		if (p == Pos.CENTER) return Cursor.HAND;
		
		if (p == Pos.TOP_LEFT) return Cursor.NW_RESIZE;
		if (p == Pos.TOP_RIGHT) return Cursor.NE_RESIZE;
		if (p == Pos.TOP_CENTER) return Cursor.N_RESIZE;

		if (p == Pos.BOTTOM_LEFT) return Cursor.SW_RESIZE;
		if (p == Pos.BOTTOM_RIGHT) return Cursor.SE_RESIZE;
		if (p == Pos.BOTTOM_CENTER) return Cursor.S_RESIZE;

		if (p == Pos.CENTER_LEFT) return Cursor.W_RESIZE;
		if (p == Pos.CENTER_RIGHT) return Cursor.E_RESIZE;

		return Cursor.HAND;
	}
	
	// TODO -- this doesnt center the text properly, imbed it to center
    public static Cursor getTextCursor(String txt)
    {
    	return  getTextCursor(txt, Color.GREEN); 
    }
	// TODO -- this doesnt center the text properly, imbed it to center
    public static Cursor getTextCursor(String txt, Color col)
    {
	    String STYLE = "-fx-background-color: whitesmoke; -fx-font-size: 36; ";
    	int W = 50;
    	Label label = new Label(txt);
		label.setStyle(STYLE );
	    label.setWrapText(true);
	    label.setTextFill(col);
	    StackPane pane = new StackPane(label);
	    NodeUtil.forceSize(pane, W, W);
	    pane.setBorder(Borders.blueBorder1);
	    Scene scene = new Scene(pane);
	    WritableImage img = new WritableImage(W, W) ;
	    scene.snapshot(img);
    	return new ImageCursor(img); 
    }
}

