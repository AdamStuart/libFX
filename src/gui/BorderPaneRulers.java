package gui;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.util.Duration;

public class BorderPaneRulers
{
	Animation hider, shower;
	double rulerWidth = 30;
	boolean useInches = true;
	boolean useCM = false;

	
	public BorderPaneRulers(BorderPane pane, Button togl)
	{
		Node content = pane.getCenter();
		assert(content != null);
		addRulers(pane, content);
		togl.setOnAction(new EventHandler<ActionEvent>()    {
		    @Override public void handle(ActionEvent actionEvent) 
		    {	toggleRulers(pane);  }
	    });
	    Region topRuler = (Region)pane.getTop();
	    Region leftRuler = (Region)pane.getLeft();
		Rectangle clipRect = new Rectangle();

		pane.setClip(clipRect);
		clipRect.widthProperty().bind(pane.widthProperty());
		clipRect.heightProperty().bind(pane.heightProperty());

		topRuler.translateXProperty().bind(content.translateXProperty().add(rulerWidth));
		leftRuler.translateYProperty().bind(content.translateYProperty());		//.add(rulerWidth)
		topRuler.scaleXProperty().bind(content.scaleXProperty());
		leftRuler.scaleYProperty().bind(content.scaleYProperty());
		
    
		hider = new Transition() 
		{
	        { setCycleDuration(Duration.millis(250)); }
	        
	        protected void interpolate(double frac) 
	        {
	          final double curWidth = rulerWidth * (1.0 - frac);
	          topRuler.setPrefHeight(curWidth);
	          topRuler.setMaxHeight(curWidth);
	          topRuler.setMinHeight(curWidth);
	          
	          leftRuler.setPrefWidth(curWidth);
	          leftRuler.setMaxWidth(curWidth);
	          leftRuler.setMinWidth(curWidth);
	//          System.out.println("" + curWidth);
	          leftRuler.setTranslateX( curWidth - rulerWidth);
	          topRuler.setTranslateY( curWidth - rulerWidth);
	//          center.setPrefWidth(centerWidth + expandedWidth - curWidth );
	        }
		};
      hider.onFinishedProperty().set(new EventHandler<ActionEvent>() {
          @Override public void handle(ActionEvent actionEvent) {
        	  topRuler.setVisible(false);
        	  leftRuler.setVisible(false);
          	togl.getStyleClass().remove("hide-ruler");
          	togl.getStyleClass().add("show-ruler");
         }
        });
      // create an animation to show a sidebar.
      shower = new Transition() {
        { setCycleDuration(Duration.millis(250)); }
        protected void interpolate(double frac) {
          final double curWidth = rulerWidth * frac;
          
          topRuler.setVisible(true);
        topRuler.setPrefHeight(curWidth);  
          topRuler.setMaxHeight(curWidth); 
          topRuler.setMinHeight(curWidth);
          
          leftRuler.setVisible(true);
          leftRuler.setPrefWidth(curWidth);  
          leftRuler.setMaxWidth(curWidth); 
          leftRuler.setMinWidth(curWidth);
       
//          System.out.println("" + curWidth);
          leftRuler.setTranslateX( curWidth-rulerWidth);
          topRuler.setTranslateY( curWidth-rulerWidth);
      }
      };
      shower.onFinishedProperty().set(new EventHandler<ActionEvent>() {
        @Override public void handle(ActionEvent actionEvent) {
      	  topRuler.setTranslateY(0);
      	  leftRuler.setTranslateX(0);
          leftRuler.setPrefWidth(rulerWidth);  
          leftRuler.setMaxWidth(rulerWidth); 
          leftRuler.setMinWidth(rulerWidth);
     	  togl.getStyleClass().add("hide-ruler");
      	  togl.getStyleClass().remove("show-ruler");
        }
      });			
	}

	public void toggleRulers(BorderPane pane )
	{
	    boolean isVis = ((Region)pane.getTop()).isVisible();

		if (shower.statusProperty().get() == Animation.Status.STOPPED
				&& hider.statusProperty().get() == Animation.Status.STOPPED)
		{
			if (isVis)		hider.play();
			else			shower.play();
		}
	}
	
	private void addRulers(BorderPane pane, Node content )
	{
//		Button rulerButton = new Button();
//		rulerButton.setGraphic(GlyphsDude.createIcon(FontAwesomeIcons.STAR, GlyphIcon.DEFAULT_ICON_SIZE));
//		rulerButton.setMaxHeight(rulerWidth);
//		rulerButton.setMaxWidth(rulerWidth);
		pane.setTop(new TopRulerPane(content));
		pane.setLeft(new LeftRulerPane(content));
		
	}
	public static final int	HORIZONTAL	= 0;
	public static final int	VERTICAL	= 1;
	public static final double DEFAULT_TICK_LENGTH	= 12;
	public static final int MIN_TICK_SPACING = 8;
	public static final int RULER_PAGE_END_SPACE = 30;		//don't draw labels in this distance from the end of the page
	private double				maxTickLength	= DEFAULT_TICK_LENGTH;
	public double getLongTick()					{	return maxTickLength;				}
	public double getMediumTick()				{	return (int) (maxTickLength * .7);	}
	public double getShortTick()				{	return (int) (maxTickLength * .4);	}

	class TopRulerPane extends RulerPane
	{
		TopRulerPane(Node content)
		{
			super(true, content);
//			Rectangle clipRect = new Rectangle(rulerWidth, 0, 100000, rulerWidth);
//			setClip(clipRect);
//			widthProperty().addListener(new ChangeListener<Object>(){
//			       @Override public void changed(ObservableValue<?> o,Object oldVal,  Object newVal){
//			        	clipRect.widthProperty().set((double) newVal);
//			        }
//			      });
//			heightProperty().addListener(new ChangeListener<Object>(){
//			        @Override public void changed(ObservableValue<?> o,Object oldVal,  Object newVal){
//			        	clipRect.heightProperty().set((double) newVal);
//			        }
//			      });
			getChildren().add(new Line(-pageLengthPixels, rulerWidth,pageLengthPixels, rulerWidth));
			getChildren().add(new Line(-pageLengthPixels, 0, pageLengthPixels, 0));
			for (int page=0; page<pageCount; page++)
			{
				double offset = 0;
				int ct = 0;
				double hght = rulerWidth;
				while (offset < pageLengthPixels)
				{
					if (useInches)
					{
						for (double i=0; i<8; i++)	
						{
							double x = offset + i * interval * scaleX;
							double tickLen = i == 0 ? getLongTick() : ((i == 4) ? getMediumTick() : getShortTick());
	//						Line line = new Line(x, 0, x, tickLen);
							double weight = i == 0 ? 1 : ((i == 4) ? .6 : .4);
							Line line = new Line(x, hght, x, hght-tickLen);
							line.setStrokeWidth(weight);
							Line negLine = new Line(-x, hght, -x, hght-tickLen);
							negLine.setStrokeWidth(weight);
							getChildren().addAll(line, negLine);
							if (i== 0 && ct > 0)
							{
								Text t = new Text("" + ct);
								Text negT = new Text("" + ct);
								t.setLayoutX(x - (ct > 9 ? 18 : 12));		// extra 5 pixels for two digit numbers
								negT.setLayoutX(-(x - (ct > 9 ? 18 : 12)));		// extra 5 pixels for two digit numbers
								t.setLayoutY(20);
								negT.setLayoutY(20);
								getChildren().addAll(t, negT);
							}
						}
					offset += 8 * interval * scaleX;
					}
					else if (useCM)
					{
						interval = Screen.getPrimary().getDpi() / 25.4;			// dots per mm
						for (double i=0; i<10; i++)	
						{
							double x = offset + i * interval * scaleX;
							double tickLen = i == 0 ? getLongTick() : ((i == 4) ? getMediumTick() : getShortTick());
	//						Line line = new Line(x, 0, x, tickLen);
							double weight = i == 0 ? 1 : ((i == 4) ? .6 : .4);
							Line line = new Line(x, hght, x, hght-tickLen);
							line.setStrokeWidth(weight);
							Line negLine = new Line(-x, hght, -x, hght-tickLen);
							negLine.setStrokeWidth(weight);
							getChildren().add(line);
							if (i== 0 && ct > 0)
							{
								Text t = new Text("" + ct);
								Text negT = new Text("" + ct);
								t.setLayoutX(x - (ct > 9 ? 18 : 12));		// extra 5 pixels for two digit numbers
								negT.setLayoutX(-(x - (ct > 9 ? 18 : 12)));		// extra 5 pixels for two digit numbers
								t.setLayoutY(20);
								negT.setLayoutY(20);
								getChildren().add(t);
							}
						}
						offset += 10 * interval * scaleX;
				}
					ct++;
				}
			}
		}
		
	}
	class LeftRulerPane extends RulerPane
	{
		LeftRulerPane(Node content)
		{
			super(false, content);
//			Rectangle clipRect = new Rectangle(0, rulerWidth, rulerWidth, 100000 );
//			setClip(clipRect);
//			heightProperty().addListener(new ChangeListener<Object>(){
//			       @Override public void changed(ObservableValue<?> o,Object oldVal,  Object newVal){
//			        	clipRect.heightProperty().set((double) newVal);
//			        }
//			      });
		
			getChildren().add(new Line(rulerWidth,-pageLengthPixels, rulerWidth, pageLengthPixels));
			getChildren().add(new Line(0, -pageLengthPixels, 0, pageLengthPixels));
			for (int page=0; page<pageCount; page++)
			{
				double offset = offsetY;
				int ct = 0;
					int width = 30;
					while (offset < pageLengthPixels)
					{
						if (useInches)
						{
							for (double i = 0; i < 8; i++)
							{
								double weight = i == 0 ? 1 : ((i == 4) ? .6 : .4);
								double y = offset + i * interval * scaleY;
								double tickLen = i == 0 ? getLongTick() : ((i == 4) ? getMediumTick() : getShortTick());
								Line line = new Line(width - tickLen, y, width, y);
								line.setStrokeWidth(weight);
								Line negLine = new Line(width - tickLen, -y, width, -y);
								negLine.setStrokeWidth(weight);
								getChildren().addAll(line, negLine);
								if (i == 0 && ct > 0)
								{
									Text t = new Text("" + ct);
									Text negT = new Text("" + ct);
									t.setLayoutY(y - 6);
									t.setLayoutX(12);
									negT.setLayoutY(-(y - 6));
									negT.setLayoutX(12);
									getChildren().addAll(t, negT);
								}
							}
							offset += 8 * interval;
						}
						else if (useCM)
						{
							interval = Screen.getPrimary().getDpi() / 25.4;			// dots per mm
							for (double i = 0; i < 10; i++)

							{
								double weight = i == 0 ? 1 : ((i == 4) ? .6 : .4);
								double y = offset + i * interval * scaleY;
								double tickLen = i == 0 ? getLongTick() : ((i == 5) ? getMediumTick() : getShortTick());
								Line line = new Line(width - tickLen, y, width, y);
								line.setStrokeWidth(weight);
								Line negLine = new Line(width - tickLen, -y, width, -y);
								negLine.setStrokeWidth(weight);
								getChildren().addAll(line, negLine);
								if (i == 0 && ct > 0)
								{
									Text t = new Text("" + ct);
									t.setLayoutY(y - 6);
									t.setLayoutX(12);
									Text negT = new Text("" + ct);
									negT.setLayoutY(-(y - 6));
									negT.setLayoutX(12);
									getChildren().addAll(t, negT);
								}
							}
							offset += 10 * interval * scaleY;
						
						}
							
						ct++;
					}
				}
		}
	}

	class RulerPane extends Region
	{
		protected int pageCount = 1;
		protected double interval = Screen.getPrimary().getDpi() / 8;
		protected double pageLengthPixels = 25000;
		protected boolean horizontal;
		double scaleX, scaleY, offsetX, offsetY;
		Node drawPane;
		RulerPane(boolean horiz, Node content)
		{
			horizontal = horiz;
//			Background backgd = new Background(new BackgroundFill(Color.LIGHTGRAY, null, null));
			drawPane = content;
			
			scaleX = drawPane.getScaleX();
			scaleY = drawPane.getScaleY();
			offsetX = drawPane.getTranslateX();
			offsetY = drawPane.getTranslateY();

			Stop[] stops = { new Stop(0, Color.LIGHTGRAY), new Stop(1, Color.BLUE)};
		    LinearGradient g= new LinearGradient(0,0,10,100,true, CycleMethod.REFLECT, stops);
			Background backgd = new Background(new BackgroundFill(g, null, null));
			setBackground(backgd);
		}
	}
	
}
