package util;

import icon.FontAwesomeIcons;
import icon.GlyphIcon;
import icon.GlyphsDude;
import javafx.scene.control.Button;

public class DialogUtil {

	
public void Alert(String s)  {
	System.out.println(s);
}

public static void useGlyph(Button b, FontAwesomeIcons def)
{
	b.setGraphic(	GlyphsDude.createIcon(def, GlyphIcon.DEFAULT_ICON_SIZE));
	b.setText("");
}


public static void useGlyphToo(Button b, FontAwesomeIcons def)
{
	b.setGraphic(	GlyphsDude.createIcon(def, GlyphIcon.DEFAULT_ICON_SIZE));
//	b.setText("");
}

}
