package pl.st.donuts.generators;

import pl.st.donuts.conveyors.Conveyor;

public abstract class BaseGenerator implements Generator {
	
	protected Conveyor conveyor;
	protected double ox = 0.0;
	protected double oy = 0.0;
	protected double w = 100.0;

	
}
