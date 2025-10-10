package coins;

import java.util.*;

public class World {
    public List<Body> bodies = new ArrayList<>();
    public double dt = 1.0 / 60.0;
    public int solverIterations = 10;

    public void step() {
        // Integruj siły
        for (Body b : bodies) b.integrateForces(dt);

        // Detekcja kolizji
        List<Contact> contacts = new ArrayList<>();
        for (int i = 0; i < bodies.size(); i++) {
            for (int j = i + 1; j < bodies.size(); j++) {
                Body a = bodies.get(i);
                Body b = bodies.get(j);
                if (a.isStatic && b.isStatic) continue;

                Contact c = new Contact(a, b);
                if (c.solve()) contacts.add(c);
            }
        }

        // Rozwiązuj impulsy
        for (int i = 0; i < solverIterations; i++) {
            for (Contact c : contacts) c.resolve(dt);
        }

        // Integruj prędkości
        for (Body b : bodies) b.integrateVelocity(dt);

        // Korekta pozycji
        for (Contact c : contacts) c.positionalCorrection();
    }

    public void addBody(Body b) {
        bodies.add(b);
    }
}
