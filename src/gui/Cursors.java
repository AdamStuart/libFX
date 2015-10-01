package gui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;

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
	

}
