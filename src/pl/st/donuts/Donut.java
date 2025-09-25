package pl.st.donuts;

import java.awt.Graphics2D;

import pl.st.donuts.conveyors.Conveyor;

public class Donut {
	private Conveyor conveyor;
	private V coords;
	private double oy;
	
	public static Donut create(double x, double y, Conveyor conveyor) {
		var d = new Donut();
		d.conveyor = conveyor;
		d.oy = y;
		d.coords = V.of(x, y);
		return d;
	}
	
	public void move(V d) {
		coords = coords.add(d);
	}
	
	public void draw(Graphics2D gr) {
		int size = Settings.DONUT_RADIUS * 2;
		gr.setColor(Settings.DONUT_COLOR);
		gr.fillArc(coords.intX()-Settings.DONUT_RADIUS, coords.intY() - Settings.DONUT_RADIUS, size, size, 0, 360);
//		gr.drawRect(coords.intX(),  coords.intY(), 2,2);
	}
	
	public V coords() {
		return coords;
	}
	
	public double oy() {
		return oy;
	}
	
	public Conveyor getConveyor() {
		return conveyor;
	}
	
	public void setConveyor(Conveyor conveyor) {
		this.conveyor = conveyor;
		this.oy = this.coords.y();
	}
}
