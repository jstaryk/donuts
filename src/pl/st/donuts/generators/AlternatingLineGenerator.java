package pl.st.donuts.generators;

import java.util.ArrayList;
import java.util.List;

import pl.st.donuts.Donut;
import pl.st.donuts.conveyors.Conveyor;

public class AlternatingLineGenerator extends BaseGenerator {
	
	private int rowSize = 2;
	private double rowWidth = w / rowSize;
	private double margin = rowWidth / 2;
	
	private boolean fullRow = true; 
	
	public AlternatingLineGenerator(Conveyor conveyor) {
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
		int count = fullRow ? rowSize : rowSize - 1;
		double shiftX = fullRow ? 0 : rowWidth / 2;
		for (var i = 0; i < count; i++) {
			row.add(Donut.create(ox, oy + margin + i * rowWidth + shiftX, conveyor));
		}
		fullRow = !fullRow;
		return row;
	}
	
	public void setRowSize(int rowSize) {
		if (rowSize < 1) {
			throw new IllegalArgumentException("Rowsize < 2");
		}
		this.rowSize = rowSize;
		rowWidth = w / rowSize;
		margin = rowWidth / 2;
	}

}
