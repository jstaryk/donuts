package pl.st.donuts;

public class V {

    private static final V ZERO = new V(0, 0);

    private double x;
    private double y;

    private V(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static V of(double x, double y) {
        return new V(x, y);
    }

    public static V of(V v) {
        return new V(v.x, v.y);
    }

    public static V zero() {
        return ZERO;
    }

    public V add(V d) {
    	return new V(x + d.x(), y + d.y());
    }

    public V withX(double newX) {
        return new V(newX, y);
    }

    public V withY(double newY) {
        return new V(x, newY);
    }

    public V plusX(double dx) {
        return new V(x + dx, y);
    }

    public V plusY(double dy) {
        return new V(x, y + dy);
    }

    public double x() {
        return x;
    }
    
    public double y() {
        return y;
    }

    public int intX() {
    	return (int)x;
    }
    
    public int intY() {
    	return (int)y;
    }
   
    public double distance(V from) {
    	double dx = x - from.x;
    	double dy = y - from.y;
        return (int) Math.sqrt(dx * dx + dy * dy);
    }
    
    public V rotate(double angleRadians) {
        double cos = Math.cos(angleRadians);
        double sin = Math.sin(angleRadians);
        double newX = x * cos - y * sin;
        double newY = x * sin + y * cos;
        return V.of(newX, newY);
    }
   
    @Override
    public String toString() {
        return "DC[x=" + x + ", y=" + y + "]";
    }
}
