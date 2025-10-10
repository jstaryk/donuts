package coins;

public class Contact {
    public Body a, b;
    public Vector2 normal;
    public double penetration;

    public Vector2 contactPoint;

    public double restitution = 0.0;
    public double friction = 0.3;

    public Contact(Body a, Body b) {
        this.a = a;
        this.b = b;
    }

    public boolean solve() {
        Vector2 delta = b.position.sub(a.position);
        double dist = delta.length();
        double totalRadius = a.radius + b.radius;

        if (dist >= totalRadius) return false;

        penetration = totalRadius - dist;
        normal = (dist == 0) ? new Vector2(1, 0) : delta.normalize();
        contactPoint = a.position.add(normal.mul(a.radius));
        return true;
    }

    public void resolve(double dt) {
        Vector2 rA = contactPoint.sub(a.position);
        Vector2 rB = contactPoint.sub(b.position);

        Vector2 vA = a.velocity.add(Vector2.cross(a.angularVelocity, rA));
        Vector2 vB = b.velocity.add(Vector2.cross(b.angularVelocity, rB));
        Vector2 rv = vB.sub(vA);

        double velAlongNormal = rv.dot(normal);
        if (velAlongNormal > 0) return;

        double raCrossN = rA.cross(normal);
        double rbCrossN = rB.cross(normal);
        double invMassSum = a.invMass + b.invMass + 
            raCrossN * raCrossN * a.invInertia + rbCrossN * rbCrossN * b.invInertia;

        double e = Math.min(a.isStatic ? 0 : restitution, b.isStatic ? 0 : restitution);
        double j = -(1 + e) * velAlongNormal / invMassSum;

        Vector2 impulse = normal.mul(j);
        a.applyImpulse(impulse.mul(-1), rA);
        b.applyImpulse(impulse, rB);

        // Tarcie
        rv = b.velocity.add(Vector2.cross(b.angularVelocity, rB))
             .sub(a.velocity.add(Vector2.cross(a.angularVelocity, rA)));
        Vector2 tangent = rv.sub(normal.mul(rv.dot(normal))).normalize();
        double jt = -rv.dot(tangent) / invMassSum;

        double mu = Math.sqrt(friction * friction);
        Vector2 frictionImpulse = (Math.abs(jt) < j * mu)
            ? tangent.mul(jt)
            : tangent.mul(-j * mu);

        a.applyImpulse(frictionImpulse.mul(-1), rA);
        b.applyImpulse(frictionImpulse, rB);
    }

    public void positionalCorrection() {
        final double k_slop = 0.01;
        final double percent = 0.4;
        double correctionMag = Math.max(penetration - k_slop, 0.0) / (a.invMass + b.invMass) * percent;
        Vector2 correction = normal.mul(correctionMag);
        if (!a.isStatic) a.position = a.position.sub(correction.mul(a.invMass));
        if (!b.isStatic) b.position = b.position.add(correction.mul(b.invMass));
    }
}

