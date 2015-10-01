package util;

import javafx.animation.Interpolator;

public class SpringInterpolator extends Interpolator
{
	// the amplitude of the wave
	// controls how far out the object can go from it's final stopping point.
	private final double amplitude;

	// determines the weight of the object
	// makes the wave motion go longer and farther
	public final double mass;

	// the stiffness of the wave motion / spring
	// makes the motion shorter and more snappy
	public final double stiffness;

	// makes the wave motion be out of phase, so that the object
	// doesn't end up on the final resting spot.
	// this variable is usually never changed
	public final double phase;

	// if this should do a normal spring or a bounce motion
	public final boolean bounce;

	// internal variables used for calcuations
	private double pulsation;

	// ******************** Constructors **************************************
	public SpringInterpolator()
		{
			this(1.0, 0.058, 12.0, 0.0, false);
		}

	public SpringInterpolator(final double AMPLITUDE, final double MASS, final double STIFFNESS, final double PHASE,
			final boolean BOUNCE)
		{
			amplitude = AMPLITUDE;
			mass = MASS;
			stiffness = STIFFNESS;
			phase = PHASE;
			bounce = BOUNCE;
			pulsation = Math.sqrt(stiffness / mass);
		}

	// ******************** Spring equation ***********************************
	@Override
	protected double curve(double t)
	{
		double t2 = -Math.cos(pulsation * t + phase + Math.PI) * (1 - t) * amplitude;
		// use the absolute value of the distance if doing a bounces
		if (bounce)
		{
			return 1 - Math.abs(t2);
		} else
		{
			return 1 - t2;
		}
	}
}
