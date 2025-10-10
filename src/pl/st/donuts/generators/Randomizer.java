package pl.st.donuts.generators;

import java.util.List;
import java.util.Random;

import pl.st.donuts.Donut;
import pl.st.donuts.V;

public class Randomizer {
	private double xRange;
	private double yRange;
	private Random r;
	
	public Randomizer(double xRange, double yRange) {
		this.xRange = xRange;
		this.yRange = yRange;
		this.r = new Random();
	}
	
	public List<Donut> randomize(List<Donut> donutsBatch) {
		for (var d : donutsBatch) {
			V shift = V.of(r.nextDouble(-xRange, xRange), r.nextDouble(-yRange, yRange));
			d.move(shift);
		}
		return donutsBatch;
	}
}
