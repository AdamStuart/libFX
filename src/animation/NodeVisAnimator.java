package animation;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class NodeVisAnimator
{
	Animation hider, shower;
	Node visNode;
	/*
	 * NodeVisAnimator provides a utility to link a button with the visibility of any node.
	 * 
	 */
	
	public NodeVisAnimator(Node node, Button togl)
	{
		visNode = node;
		togl.setOnAction(new EventHandler<ActionEvent>()    {
		    @Override public void handle(ActionEvent actionEvent) 	    {	toggleGridVis();  }
	    });
		hider = new Transition() 
		{
        { setCycleDuration(Duration.millis(250)); }
        
        protected void interpolate(double frac)         {        	visNode.setOpacity(1.0 - frac);        };
		};
		
      hider.onFinishedProperty().set(new EventHandler<ActionEvent>() {
          @Override public void handle(ActionEvent actionEvent) {
        	  visNode.setVisible(false);		
        	  //user feedback here?
          }
        });

      shower = new Transition() {
        { setCycleDuration(Duration.millis(250)); }
        protected void interpolate(double frac) {        	visNode.setOpacity(frac);        }
      };
//      shower.onFinishedProperty().set(new EventHandler<ActionEvent>() {
//        @Override public void handle(ActionEvent actionEvent) {		user feedback here?
//        }
//      });			
	}

	public void toggleGridVis()
	{
		if (shower.statusProperty().get() == Animation.Status.STOPPED
				&& hider.statusProperty().get() == Animation.Status.STOPPED)
		{
			if (visNode.isVisible())
				hider.play();
			else
			{
				visNode.setVisible(true);
				shower.play();
			}
		}
	}

}
