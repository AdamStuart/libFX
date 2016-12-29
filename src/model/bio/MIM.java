package model.bio;


import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import util.StringUtil;

public enum MIM
{
	MIM_NECESSARY_STIMULATION("mim-necessary-stimulation", "Arrow"),
    MIM_BINDING("mim-binding", "Arrow"),
    MIM_CONVERSION("mim-conversion", "Arrow"),
    MIM_STIMULATION("mim-stimulation", "Arrow"),
    MIM_MODIFICATION("mim-modification", "Arrow"),
    MIM_CATALYSIS("mim-catalysis", "Arrow"),
    MIM_INHIBITION("mim-inhibition", "Arrow"),
    MIM_CLEAVAGE("mim-cleavage", "Arrow"),
    MIM_COVALENT_BOND("mim-covalent-bond", "Arrow"),
    MIM_BRANCHING_LEFT("mim-branching-left", null),
    MIM_BRANCHING_RIGHT("mim-branching-right", null),
    MIM_TRANSLATION("mim-transcription-translation", "Arrow"),
    MIM_GAP("mim-gap", null);

	private String id;
	private String type;
	
	MIM(String name, String arrow)
	{
		id = name;
		type = arrow;
	}

	public String getDescription()
	{
		String cleaner = id.substring(4).replace("-", " ");
		return StringUtil.capitalize(cleaner);
	}
	public MIM lookup(String name)
	{
		for (MIM m : values())
			if (m.id.equals(name)) return m;
		return MIM_BINDING;
	}

	public Shape getShape()
	{
//		if (id.equals("Arrow"))						return getMIMCatalysis ();
		if (id.equals("mim-necessary-stimulation"))		return getMIMNecessary ();
		if (id.equals("mim-conversion"))				return getMIMConversion ();
		if (id.equals("mim-binding"))					return getMIMBinding ();
		if (id.equals("mim-stimulation"))				return getMIMStimulation ();
		if (id.equals("mim-modification"))				return getMIMConversion ();
		if (id.equals("mim-catalysis"))					return getMIMCatalysis ();
		if (id.equals("mim-inhibition"))				return getMIMInhibition ();
		if (id.equals("mim-cleavage"))					return getMIMCleavage ();
		if (id.equals("mim-gap"))						return getMIMGap();
		if (id.equals("mim-covalent-bond"))				return getMIMCovalentBond ();
		if (id.equals("mim-branching-left"))			return getMIMBranching (LEFT);
		if (id.equals("mim-branching-right"))			return getMIMBranching (RIGHT);
		if (id.equals("mim-transcription-translation"))	return getMIMTranslation ();
		
		return null;
	}
	//----------------------------------------------------------------------------------
	//Cleavage line ending constants
		static final int CLEAVAGE_FIRST = 10;
		static final int CLEAVAGE_SECOND = 20;
		static final int CLEAVAGE_GAP = CLEAVAGE_SECOND - CLEAVAGE_FIRST;

		private static final int TBARHEIGHT = 15;
		private static final int TBARWIDTH = 1;

		//Branch line ending constants
		 private static final int LEFT = 0;
		 private static final int RIGHT = 1;
		 private static final int BRANCH_LOCATION = 8;
		 private static final int BRANCHTHICKNESS = 1;

		static final int CATALYSIS_RADIUS = 3;
		static final int CATALYSIS_GAP = CATALYSIS_RADIUS + 2;
//		static final int CATALYSIS_GAP_HEIGHT = 6;
		//create the ellipse for catalysis line ending

		static private Shape getMIMCatalysis () 	
		{ 	
			Circle c = new Circle(0, 0,CATALYSIS_RADIUS);
			c.setFill(Color.WHITE);
			c.setStroke(Color.BLACK);
			c.setStrokeWidth(0.66);
			return c; 	
		}
		static private Shape getMIMStimulation () 	{	return getArrowShapedPath();		}
		static private Shape getMIMConversion () 	{	return getArrowShapedPath();		}
	    static private Shape getMIMInhibition() 	{ 	return new Rectangle(0, -TBARHEIGHT / 2,TBARWIDTH, TBARHEIGHT	); 	}

		 //method to create the MIM Branch RIGHT and LEFT line endings
		 // a 4 sided structure with small thickness works better than
		 // a line.(Maybe the affine transform has a issue with a line
		 //as opposed to a thin quadrilateral)
		static private Shape getMIMBranching (int direction)
	    {
			double sign = (direction == RIGHT) ? -1 : 1;
			Path path = new Path();
			path.getElements().add(new MoveTo(0, 0));
			path.getElements().add(new LineTo(BRANCH_LOCATION, sign * BRANCH_LOCATION));
			path.getElements().add(new LineTo(BRANCH_LOCATION, sign * (BRANCH_LOCATION  - BRANCHTHICKNESS)));
			path.getElements().add(new LineTo(BRANCHTHICKNESS, 0));
			return path;
	    }

		//method to create the MIM Cleavage lie ending
		static private Shape getMIMCleavage ()
		{
			Path path = new Path();
			path.getElements().add(new MoveTo(0, 0));
			path.getElements().add(new LineTo(0, -CLEAVAGE_FIRST));
			path.getElements().add(new LineTo(CLEAVAGE_SECOND, CLEAVAGE_FIRST));
			return path;
		}

		private static final int ARROWHEIGHT = 4;
		private static final int ARROWWIDTH = 9;
		private static final int ARROW_NECESSARY_CROSSBAR = 6;

		
		private static Path getArrowShapedPath() {
			Path path = new Path();
			path.getElements().add(new MoveTo(0, -ARROWHEIGHT));
			path.getElements().add(new LineTo(ARROWWIDTH, 0));
			path.getElements().add(new LineTo(0, ARROWHEIGHT));
			return path;
		}

	    static private Path getMIMCovalentBond ()
	    {
			Path path = new Path();
			path.getElements().add(new MoveTo(0, -7));
			path.getElements().add(new LineTo (0, 7));
	        path.getElements().add(new MoveTo(0, -7));
	        path.getElements().add(new LineTo (8, -7));
	        path.getElements().add(new MoveTo(0, 7));
	        path.getElements().add(new LineTo (8, 7));
	        return path;
	    }
	    
		static private Shape getArrow () {
			Path path = new Path();
			path.getElements().add(new MoveTo(0, 0));
			path.getElements().add(new LineTo(-ARROWWIDTH, -ARROWHEIGHT));
			path.getElements().add(new LineTo(-ARROWWIDTH / 2, 0));
			path.getElements().add(new LineTo(-ARROWWIDTH, ARROWHEIGHT));
			return path;
		}

		static private Shape getMIMBinding () {
			Path path = new Path();
			path.getElements().add(new MoveTo(0, 0));
			path.getElements().add(new LineTo(-ARROWWIDTH, -ARROWHEIGHT));
			path.getElements().add(new LineTo(-ARROWWIDTH / 2, 0));
			path.getElements().add(new LineTo(-ARROWWIDTH, ARROWHEIGHT));
			return path;
		}

		static private Shape getMIMNecessary () {
			Path path = getArrowShapedPath();
			path.getElements().add(new MoveTo(-ARROW_NECESSARY_CROSSBAR, -ARROWHEIGHT));
			path.getElements().add(new LineTo(-ARROW_NECESSARY_CROSSBAR, ARROWHEIGHT));
			return path;
		}

	    final static int TAIL = ARROWWIDTH / 2;

	    static private Path getMIMTranslation() {
	        Path path = new Path();
			path.getElements().add(new MoveTo(-TAIL, 0));
			path.getElements().add(new LineTo(-TAIL, ARROWHEIGHT *2));
	        path.getElements().add(new LineTo(TAIL, ARROWHEIGHT * 2));
	        path.getElements().add(new LineTo(TAIL, ARROWHEIGHT * 3));
			path.getElements().add(new LineTo(TAIL + ARROWWIDTH, ARROWHEIGHT * 2));
	        path.getElements().add(new LineTo(TAIL, ARROWHEIGHT));
	        path.getElements().add(new LineTo(TAIL, ARROWHEIGHT * 2));
	        return path;
	    }

	    static private Shape getMIMGap () {
	    	Path path = new Path();
	        path.getElements().add(new MoveTo(0, 0));
	        path.getElements().add(new MoveTo(0, 5));
	        return path;
	    }
	
}
