package model.dao;

import javax.swing.Icon;

import icon.FontAwesomeIcons;
import icon.GlyphsDude;
import javafx.scene.text.Text;

public enum ColumnType
{
	Number(	"N", FontAwesomeIcons.TIMES),
	Text(	"T", FontAwesomeIcons.FONT),
	Boolean("B", FontAwesomeIcons.CHECK),
	Date(	"D", FontAwesomeIcons.CALENDAR),
	Color(	"C", FontAwesomeIcons.PAINT_BRUSH),
	Enum(	"E", FontAwesomeIcons.HEART),
	List(	"L", FontAwesomeIcons.TH_LIST);

	String mnemonic; 
	Text text;
	FontAwesomeIcons icon;
	private ColumnType(String m, FontAwesomeIcons i)
	{
		mnemonic = m;
		icon = i;
		text = GlyphsDude.createIcon(i, "8");
	}
	
	public String toString()
	{
		return mnemonic;
	}
	
	public Text getGraphic()
	{
		return text;
	}
}
