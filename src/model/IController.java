package model;

import icon.GlyphIcon;
import icon.GlyphIcons;
import icon.GlyphsDude;
import javafx.scene.control.Button;
import javafx.scene.input.DataFormat;
import javafx.scene.input.MouseEvent;
import model.bio.Species;

public interface IController {
	public void getInfo(DataFormat fmt, String a, String colname, MouseEvent event);
	public void resetTableColumns();
	public void setState(String state);
	public String getState();
	public void reorderColumns(int draggedIndex, int index);
	
	static public void setGraphic(Button b, GlyphIcons i)
	{
		b.setGraphic(GlyphsDude.createIcon(i, GlyphIcon.DEFAULT_ICON_SIZE));
		b.setText("");
	}

}
