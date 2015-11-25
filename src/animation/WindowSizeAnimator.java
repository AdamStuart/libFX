package animation;

import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.stage.Stage;
import javafx.util.Duration;

public class WindowSizeAnimator
{
	Animation grower;

	public WindowSizeAnimator(Stage stage, double fromSize, double toSize)
	{
		grower = new Transition() 
		{
			
	      { setCycleDuration(Duration.millis(250)); }
        
        protected void interpolate(double frac) 
        {
          final double curVal = fromSize + (frac * (toSize - fromSize));
          stage.setHeight(curVal);
      };
  
		};
	}
	public void play()
	{
		grower.play();
	}
}