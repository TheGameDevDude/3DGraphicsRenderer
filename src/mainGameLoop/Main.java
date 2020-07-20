package mainGameLoop;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JFrame;

import input.Keyboard;
import input.Mouse;

public class Main extends Canvas implements Runnable {
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 280;
	public static final int HEIGHT = WIDTH * 3 / 4;
	private static final int SCALE = 4;
	public static int widthResize = WIDTH;
	public static int heightResize = HEIGHT;

	private Thread thread;
	private static JFrame frame;
	private boolean running = false;
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	private Game game = new Game(WIDTH, HEIGHT);
	private static float deltaTime;
	private Mouse mouse;
	private Keyboard key;

	public Main() {
		Dimension size = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
		setPreferredSize(size);
		frame = new JFrame("Fullscreen");
		key = new Keyboard();
		mouse = new Mouse();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		addKeyListener(key);
	}

	public synchronized void start() {
		running = true;
		thread = new Thread(this, "Main");
		thread.start();
	}

	public synchronized void stop() {
		running = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		requestFocus();
		long lastTime = System.nanoTime();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;
		while (running == true) {
			long now = System.nanoTime();
			delta = (now - lastTime) / ns;
			deltaTime = (float) delta;
			System.out.println("FPS : " + (int) (2 * 60 / deltaTime));
			lastTime = now;
			tick();
			render();

		}
		stop();
	}

	private void tick() {
		widthResize = getWidth();
		heightResize = getHeight();
		game.tick(mouse, key);
	}

	private void render() {
		BufferStrategy bs = getBufferStrategy();

		if (bs == null) {
			createBufferStrategy(2);
			return;
		}

		game.render(pixels);

		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}

	public static float getFrameTime() {
		return deltaTime;
	}

	public static JFrame getJFrame() {
		return frame;
	}

	public static void main(String[] args) {
		Main game = new Main();
		frame.add(game);
		frame.setUndecorated(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setTitle("3D Graphics Renderer");
		frame.setResizable(true);
		frame.setCursor(frame.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "null"));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		game.start();
	}
}
