package input;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import mainGameLoop.Main;

public class Mouse implements MouseListener, MouseMotionListener {
	public float dx;
	public float dy;

	int currentXpos = 0;
	int currentYpos = 0;

	private Robot robot;

	public Mouse() {
		dx = 0;
		dy = 0;
		try {
			robot = new Robot();
			robot.mouseMove(Main.getJFrame().getX() + Main.widthResize / 2, Main.getJFrame().getY() + Main.heightResize / 2);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	public void tick() {
		dx = currentXpos - Main.widthResize / 2;
		dy = currentYpos - Main.heightResize / 2;

		robot.mouseMove(Main.getJFrame().getX() + Main.widthResize / 2, Main.getJFrame().getY() + Main.heightResize / 2);
	}

	public void mouseDragged(MouseEvent e) {
		currentXpos = e.getX();
		currentYpos = e.getY();
	}

	public void mouseMoved(MouseEvent e) {
		currentXpos = e.getX();
		currentYpos = e.getY();

	}

	public void mouseClicked(MouseEvent e) {
		currentXpos = e.getX();
		currentYpos = e.getY();
	}

	public void mouseEntered(MouseEvent e) {
		currentXpos = e.getX();
		currentYpos = e.getY();
	}

	public void mouseExited(MouseEvent e) {
		currentXpos = e.getX();
		currentYpos = e.getY();
	}

	public void mousePressed(MouseEvent e) {
		currentXpos = e.getX();
		currentYpos = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
		currentXpos = e.getX();
		currentYpos = e.getY();
	}

}
