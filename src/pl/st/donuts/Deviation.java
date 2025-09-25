package pl.st.donuts;

import java.util.Random;

public class Deviation {

    private static final Deviation ZERO = new Deviation(0d, 0d);
    
    private static final Random r = new Random();

    private double x;
    private double y;

    private Deviation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Deviation of(double x, double y) {
        return new Deviation(x, y);
    }

    public static Deviation of(Deviation d) {
        return new Deviation(d.x, d.y);
    }

    public static Deviation zero() {
        return ZERO;
    }

    public void deviate(double dev) {
    	switch(r.nextInt(10)) {
    	case 1 -> x+=dev;
    	case 2 -> x-=dev;
    	case 3 -> y+=dev;
    	case 4 -> y-=dev;
    	}
    }
    
   

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }
}
