package pl.st.donuts;

import static pl.st.donuts.Settings.HEIGHT;
import static pl.st.donuts.Settings.WIDTH;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.st.donuts.conveyors.CF;
import pl.st.donuts.conveyors.Conveyor;

public class DonutsConveyor implements Game {

    private Display display;
    private int c;
    
    private List<Donut> donuts;
//    private List<Conveyor> conveyors;
    private Conveyor starter;

    public DonutsConveyor() {
    	donuts = new ArrayList<>();
    	addDLine();
    	
//    	conveyors = new ArrayList<>();
//    	conveyors.add(new StandardConveyor(604, WIDTH + 20, 380, 2.7, Settings.CONV_COLOR));
////    	conveyors.add(new ConveyorZW(1001, 1400, 380, 3.9, 3.9, -0.30, Settings.CONV_COLOR.darker()));
////    	conveyors.add(new StandardConveyor(901, 1000, 380, 3.9, Settings.CONV_COLOR.brighter()));
//    	conveyors.add(new ConveyorW(101, 603, 380, 2.7, 6, 4.25, Settings.CONV_COLOR.darker()));
////    	conveyors.add(new ConveyorZ(101, 603, 380, 2, 6, Settings.CONV_COLOR.darker()));
//    	conveyors.add(new StandardConveyor(-30, 100, 380, 2.7, Settings.CONV_COLOR));
    	
//    	starter = CF.of(0, 100, 380).vx(2).build();
//    	var r1 = CF.after(starter, 520, 380).vx(2).secVx(3.3).build();
//    	var r2 = CF.after(r1, 100, 380).vx(1.5).color(Color.ORANGE).build();
    	
    	starter = CF.of(0, 100,  280).vx(2).build();
    	
    	/////
//    	var w1 = CF.after(r2, 520, 380).vx(2).secVx(3.3).secWidth(280).build(); // 1.8/3   
//    	var p1 = CF.after(w1, 180, 280).vx(3.3).build();
    	var w2 = CF.after(starter, 40, 280).vx(3.3).secWidth(380).build();
    	var p2 = CF.after(w2, 500, 380).vx(3.3).build();
    	/////

    	/////
//    	var v1 = CF.after(r2, 520, 380).vx(2).secVx(3.3).build();
//    	var v2 = CF.after(r2, 500, 380).vx(3.3).build();
    	/////
    	
//    	var w1 = CF.after(starter, 550, 380).vx(2.5).secVx(4).build();
//    	var p1 = CF.after(w1, 450, 380).vx(3).build();
////    	var w2 = CF.after(p1, 500, 280).vx(3).secWidth(380).build();
////    	var p2 = CF.after(w2, 400, 380).vx(3).build();
    	
    	
//    	conveyors.add(CF.of(551, 1000, 280).vx(2).build());
////    	conveyors.add(CF.of(551, 100, 280).vx(3).secVx(2).build());
//    	conveyors.add(CF.of(-30, 580, 380).vx(2).secVx(3).secWidth(280).build());

    	display = new Display(this);
        display.start();

        UiFrame ui = new UiFrame(display::start, display::stop, this::reset);
        
    }

    private void reset() {
    	c = 0;
    }
    
    Random r = new Random();
    boolean full = true;
    private void addDLine() {
//    	for (var i = 0; i < 10; i++) {
//    		donuts.add(Donut.create(0,  32+i*37.5, starter));
//    	}
//    	for (var i = 0; i < 10; i++) {
//    		donuts.add(Donut.create(-10 + r.nextInt(21),  32+i*37.5 - 8 + r.nextInt(16), starter));
//    	}
    	var im = full ? 8 : 7;
    	for (var i = 0; i < im; i++) {
    		donuts.add(Donut.create(0,   50 + (full ? 32 : 44)+i*32, starter));
    	}
    	full = !full;
    }
    
    private void clearList() {
    	donuts.removeIf(d -> d.getConveyor() == null);
    }

    public void update() {
    	if (c++ > 12) {
    		c = 0;
    		addDLine();
    	}
    	donuts.forEach(d -> {
    		if (d.getConveyor() != null) {
    		d.move(d.getConveyor().v(d));
    		}
//    		conveyors.forEach(c -> 
//    			d.move(
//    					c.v(d.oy(), d.coords().intX())
//    					)
//    			)
    	});
    	
    	clearList();
    }

    public void draw(Graphics2D gr) {
    	gr.clearRect(0, 0, WIDTH, HEIGHT);
//    	gr.setColor(Settings.CONV_COLOR);
//    	gr.fillRect(0, 20, WIDTH, HEIGHT-40);
    	var conv = starter;
    	while (conv != null) {
    		conv.draw(gr);
    		conv = conv.next();
    	}
    	
    	donuts.forEach(d -> d.draw(gr));
    	
//    	gr.setColor(Color.GRAY);
//    	gr.drawLine(0, 10, WIDTH, 20);
//    	var ye = 390;
//    	gr.drawLine(0, ye, WIDTH, ye);
//    	
//    	for (var i = 1; i <= 7; i++) {
//    		var y = (int)(40 + ((double)i * (318/8)));
//    		gr.drawLine(0, y, WIDTH, y);
//    	}
    	
    }
   

    @Override
    public int width() {
        return WIDTH;
    }

    @Override
    public int height() {
        return HEIGHT;
    }

   

    @Override
    public String title() {
        return String.format("donuts conveyor [%d]", donuts.size());
    }

    @Override
    public int ups() {
        return Settings.UPDATES_PER_SECOND;
    }
}
