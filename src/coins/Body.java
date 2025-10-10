package coins;

public class Body {
    public Vector2 position, velocity;
    private Vector2 force;
    public double angle, angularVelocity, torque;

    public double mass, invMass;
    public double inertia, invInertia;

    public double radius;

    public boolean isStatic = false;

    public Body(Vector2 position, double radius, double mass) {
        this.position = position;
        this.velocity = new Vector2();
        this.force = new Vector2(0,1000);
        this.angle = 0;
        this.angularVelocity = 0;
        this.radius = radius;

        this.mass = isStatic ? Double.POSITIVE_INFINITY : mass;
        this.invMass = (mass > 0) ? 1.0 / mass : 0;

        // Moment bezwładności dysku: 0.5 * m * r^2
        this.inertia = 0.5 * mass * radius * radius;
        this.invInertia = (inertia > 0) ? 1.0 / inertia : 0;
    }

    public void applyImpulse(Vector2 impulse, Vector2 contactVector) {
        velocity = velocity.add(impulse.mul(invMass));
        angularVelocity += invInertia * contactVector.cross(impulse);
    }

    public void integrateForces(double dt) {
        if (isStatic) return;
        velocity = velocity.add(force.mul(invMass * dt));
        angularVelocity += torque * invInertia * dt;
    }

    public void integrateVelocity(double dt) {
        if (isStatic) return;
        position = position.add(velocity.mul(dt));
        angle += angularVelocity * dt;
    }
}