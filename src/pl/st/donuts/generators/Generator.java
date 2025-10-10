package pl.st.donuts.generators;

import java.util.List;

import pl.st.donuts.Donut;

public interface Generator {
	List<Donut> generateBatch();
}
