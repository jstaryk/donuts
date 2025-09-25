package pl.st.donuts.conveyors;

import java.awt.Color;
import java.awt.Graphics2D;

import pl.st.donuts.Donut;
import pl.st.donuts.V;

public class ConveyorZ implements Conveyor {
	
	private Conveyor conveyor;
	
	protected V secV;
	
	public ConveyorZ(Conveyor conveyor, V secV) {
		this.conveyor = conveyor;
		this.secV = secV;
	}
	
	@Override
	public void draw(Graphics2D gr) {
		conveyor.draw(gr);
	}

	@Override
	public V v(Donut d) {
		V v = V.of(conveyor.v().x() + (secV.x() - conveyor.v().x()) * (d.oy()-10) / conveyor.width(), 0);
		if (d.coords().x() + v.x() > conveyor.end()) {
			d.setConveyor(conveyor.next());
		}
		return v;
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
