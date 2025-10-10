package pl.st.donuts.generators;

import java.util.ArrayList;
import java.util.List;

import pl.st.donuts.Donut;
import pl.st.donuts.conveyors.Conveyor;

public class LineGenerator extends BaseGenerator {
	
	private int rowSize = 1;
	private double rowWidth = w / rowSize;
	private double margin = rowWidth / 2;
	
	public LineGenerator(Conveyor conveyor) {
		this.conveyor = conveyor;
		this.ox = conveyor.start();
		this.oy = conveyor.borderLeft();
		this.w = conveyor.width();
		this.rowWidth = w / rowSize;
		this.margin = rowWidth / 2;
	}

	@Override
	public List<Donut> generateBatch() {
		List<Donut> row = new ArrayList<>();
		for (var i = 0; i < rowSize; i++) {
			row.add(Donut.create(ox, oy + margin + i * rowWidth, conveyor));
		}
		return row;
	}
	
	public void setRowSize(int rowSize) {
		if (rowSize < 1) {
			throw new IllegalArgumentException("Rowsize < 1");
		}
		this.rowSize = rowSize;
		rowWidth = w / rowSize;
		margin = rowWidth / 2;
	}

}
