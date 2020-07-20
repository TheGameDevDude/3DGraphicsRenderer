package entities;

import input.Keyboard;
import input.Mouse;
import mainGameLoop.Main;
import math.Matrix4f;
import math.Vector3f;

public class Camera {
	public Vector3f cameraPosition;
	public Vector3f cameraDirection;
	protected float angleX;
	protected float angleY;
	protected float mouseSensitivity = 0.07f;

	public Camera(Vector3f position, float angleX, float angleY) {
		this.cameraPosition = position;
		cameraDirection = new Vector3f(0, 0, 1.0f);
		this.angleX = angleX;
		this.angleY = angleY;
	}

	public void mouseControl(Mouse mouse) {
		mouse.tick();
		angleY += mouse.dx * mouseSensitivity;
		angleX += mouse.dy * mouseSensitivity;

		if (angleX >= 90) {
			angleX = 90;
		} else if (angleX <= -90) {
			angleX = -90;
		}
		cameraDirection = new Vector3f(0, 0, 1.0f).multiply(new Matrix4f().rotateX(-angleX)).multiply(new Matrix4f().rotateY(-angleY));
	}

	public void keyboardControl(Keyboard key) {
		float sin = (float) Math.sin(Math.toRadians(angleY));
		float cos = (float) Math.cos(Math.toRadians(angleY));

		key.tick();
		if (key.front) {
			cameraPosition.Zpos += Main.getFrameTime() * cos * 0.1f;
			cameraPosition.Xpos += Main.getFrameTime() * sin * 0.1f;
		} else if (key.back) {
			cameraPosition.Zpos -= Main.getFrameTime() * cos * 0.1f;
			cameraPosition.Xpos -= Main.getFrameTime() * sin * 0.1f;
		}

		if (key.left) {
			cameraPosition.Zpos += Main.getFrameTime() * sin * 0.1f;
			cameraPosition.Xpos -= Main.getFrameTime() * cos * 0.1f;
		} else if (key.right) {
			cameraPosition.Zpos -= Main.getFrameTime() * sin * 0.1f;
			cameraPosition.Xpos += Main.getFrameTime() * cos * 0.1f;
		}

		if (key.up) {
			cameraPosition.Ypos += Main.getFrameTime() * 0.1f;
		} else if (key.down) {
			cameraPosition.Ypos -= Main.getFrameTime() * 0.1f;
		}
	}

	public float getAngleY() {
		return angleY;
	}

	public void setAngleY(float angleY) {
		this.angleY = angleY;
	}

	public float getAngleX() {
		return angleX;
	}

	public void setAngleX(float angleX) {
		this.angleX = angleX;
	}

	public Camera getCamera() {
		return this;
	}

}
