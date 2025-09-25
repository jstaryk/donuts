package pl.st.donuts.conveyors;

import java.awt.Color;
import java.awt.Graphics2D;

import pl.st.donuts.Donut;
import pl.st.donuts.V;

public class ConveyorW implements Conveyor {
	
	private Conveyor conveyor;
	
	private int secWidth;
	private int secLeftBorder;
	private int secRightBorder;
	
	public ConveyorW(Conveyor conveyor, int secWidth) {
		this.conveyor = conveyor;
		this.secWidth = secWidth;
		this.secLeftBorder = conveyor.axis() - this.secWidth / 2;
		this.secRightBorder = conveyor.axis() + this.secWidth / 2;
	}
	
	@Override
	public void draw(Graphics2D gr) {
		gr.setColor(conveyor.color());
		gr.fillPolygon(
				 new int[] {conveyor.start(), conveyor.end(), conveyor.end(), conveyor.start(), conveyor.start()},
				 new int[] {conveyor.borderLeft(), secLeftBorder, secRightBorder, conveyor.borderRight(), conveyor.borderLeft()},
				5);
		gr.setColor(Color.GRAY);
		var c = 10;
		double stepY = conveyor.width() / c;
		for (var i = 0; i <= c; i++) {
			double dy = stepY * i;
			double ry = dy + conveyor.borderLeft();
			gr.drawLine(
					conveyor.start(),
//					(int)ry,
					(int)(conveyor.axis()+(locF(ry) * conveyor.width()/2)), 
					conveyor.end(), 
//					(int)y
					(int)(conveyor.axis()+(locF(ry) * secWidth/2)) 
//					(int)(conveyor.axis()+(locF(y) * y/2))
					);
		}
	}
	
	@Override
	public V v(Donut d) {
		double f = locF(d.oy()); 
		double alphaRadians = Math.atan2(f * (double)(conveyor.borderLeft() - secLeftBorder), conveyor.length());
		V v = conveyor.v(d).rotate(alphaRadians);
		if (d.coords().x() + v.x() > conveyor.end()) {
			d.setConveyor(conveyor.next());
		}
		return v;
	}
	
	private double axisLoc(double loc) {
		return loc - conveyor.axis(); 
	}
	
	private double locF(double loc) {
		return (axisLoc(loc) / (conveyor.width() / 2));
	}

	@Override
	public int start() {
		return conveyor.start();
	}

	@Override
	public int axis() {
		return conveyor.axis();
	}

	@Override
	public int length() {
		return conveyor.length();
	}

	@Override
	public int width() {
		return conveyor.width();
	}

	@Override
	public int end() {
		return conveyor.end();
	}

	@Override
	public int borderLeft() {
		return conveyor.borderLeft();
	}

	@Override
	public int borderRight() {
		return conveyor.borderRight();
	}

	@Override
	public V v() {
		return conveyor.v();
	}

	@Override
	public Color color() {
		return conveyor.color();
	}

	@Override
	public Conveyor next() {
		return conveyor.next();
	}

	@Override
	public void setNext(Conveyor next) {
		conveyor.setNext(next);
	}
}
