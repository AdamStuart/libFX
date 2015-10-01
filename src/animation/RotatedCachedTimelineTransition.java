package animation;

import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;

public class RotatedCachedTimelineTransition extends CachedTimelineTransition
{
    private Rotate rotate;
    double x;
    double y;
    boolean rotateIn;
    boolean clockwise;
  
    public Rotate getRotate()	{ return rotate;	}
    public RotatedCachedTimelineTransition(final Node node, boolean rollIn, boolean rotRight, double startX, double startY, Timeline timeline) {
        super(node, timeline);
        x = startX;
        y = startY;
        rotateIn = rollIn;
        clockwise =  rotRight;

   }
    
    /**
     * Called when the animation is starting
     */
    protected void starting() {
       super.starting();
       rotate = new Rotate(0, x, y );
       node.getTransforms().add(rotate);
       
    }
    
    /**
     * Called when the animation is stopping
     */
    protected void stopping() {
        super.stopping();
        node.getTransforms().remove(rotate);

    }


}
