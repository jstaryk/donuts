package pl.st.donuts.conveyors;

import java.awt.Color;
import java.awt.Graphics2D;

import pl.st.donuts.Donut;
import pl.st.donuts.V;

public interface Conveyor {
	void draw(Graphics2D gr);
	V v(Donut d);
	
	int start();
	int axis();
	
	int length();
	int width();
	
	int end();
	int borderLeft();
	int borderRight();
	
	V v();
	
	Color color();
	
	Conveyor next();
	void setNext(Conveyor next);
}
