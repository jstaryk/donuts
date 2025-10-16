package coins;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PhysicsSimulation extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
//    Timer timer = new Timer(16, this); // ~60 FPS
//    World world = new World();
//    double accumulator = 0;
//    double dt = 1.0 / 60.0;
//
//    public PhysicsSimulation() {
//        setPreferredSize(new Dimension(800, 600));
//        setBackground(Color.WHITE);
//
//        // 5 monet w rzędzie
//        for (int i = 0; i < 5; i++) {
//            Body b = new Body(new Vector2(200 + i * 55, 300), 25, 1);
//            world.addBody(b);
//        }
//
//        // moneta-popychacz
//        Body pusher = new Body(new Vector2(50, 300), 25, 1);
//        pusher.velocity = new Vector2(200, 0);
//        world.addBody(pusher);
//
//        timer.start();
//    }

//    long lastTime = System.nanoTime();
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        long now = System.nanoTime();
//        double frameTime = (now - lastTime) / 1e9;
//        lastTime = now;
//
//        accumulator += frameTime;
//
//        while (accumulator >= dt) {
//            world.step();
//            accumulator -= dt;
//        }
//
//        repaint();
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        Graphics2D g2 = (Graphics2D) g;
//
//        for (Body b : world.bodies) {
//            int x = (int)(b.position.x - b.radius);
//            int y = (int)(b.position.y - b.radius);
//            int d = (int)(b.radius * 2);
//
//            g2.setColor(Color.ORANGE);
//            g2.fill(new Ellipse2D.Double(x, y, d, d));
//
//            g2.setColor(Color.BLACK);
//            g2.drawString(String.format("(%.1f, %.1f)", b.position.x, b.position.y), x, y - 5);
//        }
//    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Monety – fizyka 2D");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new PhysicsSimulation());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
//}

//public class PhysicsSimulation extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
    Timer timer = new Timer(16, this);
    World world = new World();
    double accumulator = 0;
    double dt = 1.0 / 60.0;

    Body draggedBody = null;
    Vector2 mousePos = new Vector2();
    Vector2 lastMousePos = new Vector2();

    public PhysicsSimulation() {
        setPreferredSize(new Dimension(1000, 1800));
        setBackground(Color.WHITE);
        
        int v = 40;

        for (int i = 0; i < 10; i++) {
            Body b = new Body(new Vector2(210 + i * 55, 0), 25, 50);
            b.velocity.y = v;
            world.addBody(b);
        }
        
        for (int i = 0; i < 10; i++) {
            Body b = new Body(new Vector2(210 + i * 55, 50), 25, 50);
            b.velocity.y = v;
            world.addBody(b);
        }
        
        for (int i = 0; i < 10; i++) {
            Body b = new Body(new Vector2(210 + i * 55, 100), 25, 50);
            b.velocity.y = v;
            world.addBody(b);
        }
        
        for (int i = 0; i < 10; i++) {
            Body b = new Body(new Vector2(210 + i * 55, 150), 25, 50);
            b.velocity.y = v;
            world.addBody(b);
        }
        
        for (int i = 0; i < 10; i++) {
            Body b = new Body(new Vector2(210 + i * 55, 200), 25, 50);
            b.velocity.y = v;
            world.addBody(b);
        }
        
        for (int i = 0; i < 10; i++) {
            Body b = new Body(new Vector2(210 + i * 55, 250), 25, 50);
            b.velocity.y = v;
            world.addBody(b);
        }
        
        for (int i = 0; i < 10; i++) {
            Body b = new Body(new Vector2(210 + i * 55, 300), 25, 50);
            b.velocity.y = v;
            world.addBody(b);
        }
        
        for (int i = 0; i < 10; i++) {
            Body b = new Body(new Vector2(210 + i * 55, 350), 25, 50);
            b.velocity.y = v;
            world.addBody(b);
        }

        
        //wall
        
        List<Body> wall = wall(new Vector2(100, 350), new Vector2(295, 600));
        wall.forEach(world::addBody);
        wall = wall(new Vector2(295, 1000), new Vector2(295, 600));
        wall.forEach(world::addBody);
        wall = wall(new Vector2(700, 350), new Vector2(505, 600));
        wall.forEach(world::addBody);
        wall = wall(new Vector2(505, 1000), new Vector2(505, 600));
        wall.forEach(world::addBody);
        
        wall = wall(new Vector2(200, 1000), new Vector2(600, 1000));
        wall.forEach(world::addBody);
        wall = wall(new Vector2(200, 1001), new Vector2(600, 1001));
        wall.forEach(world::addBody);
        
        
//        Body pusher = new Body(new Vector2(200, 550), 25, 1);
////        pusher.velocity = new Vector2(200, 0);
//        pusher.isStatic = true;
//        pusher.radius=100;
//        world.addBody(pusher);
//        
//        pusher = new Body(new Vector2(250, 600), 25, 1);
//    //  pusher.velocity = new Vector2(200, 0);
//            pusher.isStatic = true;
//            pusher.radius=100;
//            world.addBody(pusher);
//        
//        pusher = new Body(new Vector2(250, 600), 25, 1);
////  pusher.velocity = new Vector2(200, 0);
//        pusher.isStatic = true;
//        pusher.radius=100;
//        world.addBody(pusher);
//        
//        pusher = new Body(new Vector2(700, 550), 25, 1);
////      pusher.velocity = new Vector2(200, 0);
//      pusher.isStatic = true;
//      pusher.radius=100;
//      world.addBody(pusher);
//      
//      pusher = new Body(new Vector2(650, 600), 25, 1);
////    pusher.velocity = new Vector2(200, 0);
//    pusher.isStatic = true;
//    pusher.radius=100;
//    world.addBody(pusher);
        
        // wall
        
//        Body wall = new Body(new Vector2(50, 400), 25, 1);
////      pusher.velocity = new Vector2(200, 0);
//        wall.isStatic = true;
//      world.addBody(wall);

        addMouseListener(this);
        addMouseMotionListener(this);

        timer.start();
    }
    
    List<Body> wall(Vector2 start, Vector2 end) {
    	List<Body> w = new ArrayList<>();
    	
    	double distance = end.sub(start).length();
    	int wallElemRadius = 3;
    	long count = Math.round(distance / wallElemRadius) + 1;
    	
    	double stepX = (end.x - start.x) / count;
    	double stepY = (end.y - start.y) / count;
    	
    	for (var i = 0; i < count; i++) {
    		Body b = new Body(new Vector2(start.x+stepX*i, start.y+stepY*i), wallElemRadius, Double.POSITIVE_INFINITY);
    		b.isStatic = true;
    		w.add(b);
    	}
    	
    	return w;
    }

    long lastTime = System.nanoTime();

    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        double frameTime = (now - lastTime) / 1e9;
        lastTime = now;

        accumulator += frameTime;

        while (accumulator >= dt) {
            if (draggedBody != null) {
                // wymuś pozycję ciała na pozycji myszy
                draggedBody.velocity = new Vector2();
                draggedBody.angularVelocity = 0;
                draggedBody.position = new Vector2(mousePos.x, mousePos.y);
            }

            world.step();
            accumulator -= dt;
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (Body b : world.bodies) {
            int x = (int)(b.position.x - b.radius);
            int y = (int)(b.position.y - b.radius);
            int d = (int)(b.radius * 2);

            g2.setColor((b == draggedBody) ? Color.RED : Color.ORANGE);
            g2.fill(new Ellipse2D.Double(x, y, d, d));
            g2.setColor(Color.gray);
            g2.fillArc(x, y, d, d, -90, (int) b.angle);

            g2.setColor(Color.BLACK);
            if (!b.isStatic) {
            	g2.drawString(String.format("(%.0f, %.0f)", b.angle, b.velocity.y), x, y - 5);
            }
        }
    }

    // MYSZ

    @Override
    public void mousePressed(MouseEvent e) {
        Vector2 click = new Vector2(e.getX(), e.getY());
        for (Body b : world.bodies) {
            if (click.sub(b.position).length() <= b.radius) {
                draggedBody = b;
                lastMousePos = click;
                break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (draggedBody != null) {
            Vector2 release = new Vector2(e.getX(), e.getY());
            Vector2 velocity = release.sub(lastMousePos).mul(10); // przeskalowana różnica = prędkość

            draggedBody.velocity = velocity;
            draggedBody = null;
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mousePos = new Vector2(e.getX(), e.getY());
    }

    // Niepotrzebne, ale wymagane przez interfejsy
    @Override public void mouseMoved(MouseEvent e) { mousePos = new Vector2(e.getX(), e.getY()); }
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
