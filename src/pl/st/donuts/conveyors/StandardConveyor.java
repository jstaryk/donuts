package pl.st.donuts.conveyors;

import java.awt.Color;
import java.awt.Graphics2D;

import pl.st.donuts.Direction;
import pl.st.donuts.Donut;
import pl.st.donuts.V;

public class StandardConveyor implements Conveyor {
	protected int start;
	protected Direction direction = Direction.EAST;
	protected int axis;
	
	protected int length;
	protected int width;
	
	protected int end;
	protected int borderLeft;
	protected int borderRight;
	
	protected V v;
	
	protected Color color;
	
	protected Conveyor next;
	
	protected StandardConveyor() {}
	
//	protected StandardConveyor(int start, int end, int axis, int width, double v, Color color) {
//		this.start = start;
//		this.length = end - start;
//		this.end = end;
//		this.width = width;
//		this.v = V.of(v,0);
//		this.color = color;
//		this.axis = axis;
//		this.borderLeft = axis - width / 2;
//		this.borderRight = axis + width / 2;
//	}
	
	public void draw(Graphics2D gr) {
		gr.setColor(color);
		gr.fillRect(start, borderLeft, length + 1, width);
		gr.setColor(Color.LIGHT_GRAY);
		gr.drawRect(start, borderLeft, length + 1, width);
		gr.setColor(Color.DARK_GRAY);
		int c = 4;
		double stepY = width / c;
		for (var i = 0; i <= c; i++) {
			double dy = stepY * i;
			double ry = dy + borderLeft;
			gr.drawLine(
					start,
					(int)ry,
//					(int)(conveyor.axis()+(locF(ry) * conveyor.width()/2)), 
					end, 
					(int)ry
//					(int)(conveyor.axis()+(locF(ry) * secWidth/2)) 
//					(int)(conveyor.axis()+(locF(y) * y/2))
					);
		}
	}
	
	public V v(Donut d) {
		if (d.coords().x() + v.intX() > end) {
			d.setConveyor(next);
		}
		return v;
	}

	@Override
	public int start() {
		return start;
	}

	@Override
	public int axis() {
		return axis;
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public int width() {
		return width;
	}

	@Override
	public int end() {
		return end;
	}

	@Override
	public int borderLeft() {
		return borderLeft;
	}

	@Override
	public int borderRight() {
		return borderRight;
	}

	@Override
	public V v() {
		return v;
	}

	@Override
	public Color color() {
		return color;
	}

	@Override
	public Conveyor next() {
		return next;
	}
	
	@Override
	public void setNext(Conveyor next) {
		this.next = next;
	}
}
