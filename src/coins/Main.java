package coins;

public class Main {
    public static void main(String[] args) {
        World world = new World();

        // Dodaj kilka monet
        for (int i = 0; i < 5; i++) {
            world.addBody(new Body(new Vector2(2 + i * 1.1, 0), 0.5, 1));
        }

        // Popychająca moneta
        Body pusher = new Body(new Vector2(0, 0), 0.5, 1);
        pusher.velocity = new Vector2(5, 0);
        world.addBody(pusher);

        // Symulacja
        for (int step = 0; step < 300; step++) {
            world.step();

            System.out.println("Step " + step);
            for (int i = 0; i < world.bodies.size(); i++) {
                Body b = world.bodies.get(i);
                System.out.printf("Body %d: pos=(%.2f, %.2f) vel=(%.2f, %.2f)%n",
                        i, b.position.x, b.position.y, b.velocity.x, b.velocity.y);
            }
        }
    }
}
