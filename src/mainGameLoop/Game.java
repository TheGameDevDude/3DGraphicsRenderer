package mainGameLoop;

import java.util.ArrayList;
import java.util.List;

import entities.Camera;
import entities.Entity;
import entities.Light;
import graphics.Bitmap;
import graphics.Render3D;
import input.Keyboard;
import input.Mouse;
import math.Vector3f;
import model.OBJLoader;

public class Game {
	public int width;
	public int height;
	private Camera camera;
	private Render3D renderer;

	private List<Entity> entities = new ArrayList<Entity>();
	private List<Light> lights = new ArrayList<Light>();

	public Game(int width, int height) {
		this.width = width;
		this.height = height;
		camera = new Camera(new Vector3f(), 0, 0);
		renderer = new Render3D(width, height);
		entities.add(new Entity(new Vector3f(-2, 0, 0), 0, 0, 0, new Vector3f(1, 1, 1), OBJLoader.crate, Bitmap.wood, Bitmap.wood_normal, Bitmap.wood_depth));
		entities.add(new Entity(new Vector3f(-2, 3, 0), 0, 0, 0, new Vector3f(1, 1, 1), OBJLoader.crate, Bitmap.brick, Bitmap.brick_normal, Bitmap.brick_displacement));
		entities.add(new Entity(new Vector3f(3, 3, 0), 0, 0, 0, new Vector3f(1, 1, 1), OBJLoader.crate, Bitmap.container));
		entities.add(new Entity(new Vector3f(3, 0, 0), 0, 0, 0, new Vector3f(1, 1, 1), OBJLoader.crate, Bitmap.container, Bitmap.container_emissive));
		lights.add(new Light(new Vector3f(), 255, 150, 150));
	}

	public void tick(Mouse mouse, Keyboard key) {
		renderer.tick();
		camera.mouseControl(mouse);
		camera.keyboardControl(key);
		lights.get(0).position = camera.cameraPosition;
		entities.get(0).Yrot += Main.getFrameTime();
		entities.get(1).Yrot -= Main.getFrameTime();
		entities.get(2).Yrot += Main.getFrameTime();
		entities.get(3).Yrot -= Main.getFrameTime();
	}

	public void render(int[] screenPixels) {
		renderer.clear(screenPixels);
		for (Entity entity : entities) {
			entity.renderXYZ(renderer, screenPixels, lights, camera);
		}
	}
}
