package coins;

public class Vector2 {
    public double x, y;

    public Vector2(double x, double y) { this.x = x; this.y = y; }
    public Vector2() { this(0, 0); }

    public Vector2 add(Vector2 v) { return new Vector2(x + v.x, y + v.y); }
    public Vector2 sub(Vector2 v) { return new Vector2(x - v.x, y - v.y); }
    public Vector2 mul(double s) { return new Vector2(x * s, y * s); }
    public double dot(Vector2 v) { return x * v.x + y * v.y; }
    public double length() { return Math.sqrt(x * x + y * y); }
    public Vector2 normalize() {
        double len = length();
        return (len > 0) ? this.mul(1.0 / len) : new Vector2(0, 0);
    }
    public double cross(Vector2 v) { return x * v.y - y * v.x; }

    public static Vector2 cross(double s, Vector2 v) {
        return new Vector2(-s * v.y, s * v.x);
    }
}