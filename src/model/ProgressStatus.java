package model;

import icon.GlyphsDude;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public enum ProgressStatus
{
	NONE, PLANNED, INPROGRESS, COMPLETE, ABORTED, ERROR;

	public static ProgressStatus valueOf(int i)
	{
		return i >= values().length ? ERROR : ProgressStatus.values()[i];
	}

	public Text getIcon()
	{
		return GlyphsDude.createProgressStatusIcon(toString());
	}

	public Color getColor()
	{
		if (this == NONE) return Color.RED;
		if (this == PLANNED) return Color.BLUE;
		if (this == INPROGRESS) return Color.ORANGE;
		if (this == COMPLETE) return Color.GREEN;
		if (this == ABORTED) return Color.ORANGE;
		if (this == NONE) return Color.RED;
		return Color.BLACK;
	}
};

