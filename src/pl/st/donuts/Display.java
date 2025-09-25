package pl.st.donuts;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Display extends Canvas {

	private static final long serialVersionUID = 1361865821520813196L;


	private JFrame frame;

	private final Game game;
//	private final int srcWidth;
//	private final int pWidth;
	private Animator gameAnimator;
//	private BufferedImage image;
//	private int[] pixels;

	public Display(Game game) {
		this.game = game;

		Dimension size = new Dimension(game.width(), game.height());
//		this.srcWidth = game.width();
//		this.pWidth = size.width;

//		image = new BufferedImage(size.width,size.height,BufferedImage.TYPE_INT_ARGB);
//		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		setPreferredSize(size);

		frame = new JFrame();

//		keyboard = new Keyboard(new DirectionListener() {

//			@Override
//			public void directionChanged(Direction direction) {
//				snake.direct(direction);
//			}
//		});
//		addKeyListener(keyboard);

		frame.setResizable(false);
		frame.setTitle(game.title());
		frame.add(this);
		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		gameAnimator = new Animator.Builder(this::render)
				.setUp(this::requestFocus)
				.update(this::update)
				.updatesPerSecond(game.ups())
				.build();
	}

	public void update() {
		game.update();
		frame.setTitle(game.title());
	}

	


	public void render() {
		// todo: to musi być za każdym razem?
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3);
			return;
		}


		Graphics2D g = (Graphics2D) bs.getDrawGraphics();
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		
		game.draw(g);

//		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

		g.dispose();
		bs.show();
	}
	
	public void start() {
		gameAnimator.start();
	}

	public void stop() {
		gameAnimator.stop();
	}
}
