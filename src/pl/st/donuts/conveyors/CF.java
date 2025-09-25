package pl.st.donuts.conveyors;

import java.awt.Color;

import pl.st.donuts.Settings;
import pl.st.donuts.V;

public class CF {
	private int start;
	private int width;

	private int axis;
	
	private int length;
	
	private int end;
	
	private V v;
	
	private Color color;
	
	private int secWidth = -1;
	private V secV;
//	private int tracks = 1;
	
//	private int vs;
	
	private Conveyor previous;
	
	private CF() {}
	
	public static CF of(int start, int length, int width) {
		var cf = new CF();
		cf.start = start;
		cf.end = start + length;
		cf.length = length;
		cf.width = width;
		cf.axis = Settings.HEIGHT / 2;
		cf.v = V.of(1, 0);
		cf.color = Settings.CONV_COLOR;
		return cf;
	}
	
	public static CF after(Conveyor c, int length, int width) {
		CF cf = of(c.end()+1, length, width);
		cf.previous = c;
		return cf;
	}
	
	public CF axis(int axis) {
		this.axis = axis;
		return this;
	}
	
	public CF vx(double vx) {
		this.v = V.of(vx, 0);
		return this;
	}
	public CF color(Color color) {
		this.color = color;
		return this;
	}
	public CF secVx(double secVx) {
		this.secV = V.of(secVx, 0);
		return this;
	}
	public CF secWidth(int secWidth) {
		this.secWidth = secWidth;
		return this;
	}
	
	public Conveyor build() {
		var cf = new StandardConveyor();
		cf.start = start;
		cf.end = end;
		cf.length = length;
		cf.width = width;
		cf.axis = axis;
		cf.borderLeft = axis - width / 2;
		cf.borderRight = axis + width / 2;
		cf.v = v;
		cf.color = color;
		
		Conveyor conveyor = cf;
		
		if (secV != null) {
			conveyor = new ConveyorZ(conveyor, secV);
		}
		
		if (secWidth >= 0) {
			conveyor = new ConveyorW(conveyor, secWidth);
		}
		
		if (previous != null) {
			previous.setNext(conveyor);
		}

		return conveyor;
	}
}
