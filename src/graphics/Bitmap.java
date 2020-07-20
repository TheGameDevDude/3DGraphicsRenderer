package graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import math.Vector2f;

public class Bitmap {
	private String path;
	public final int width;
	public final int height;
	public int[] pixels;

	public static Bitmap brick = new Bitmap("/textures/brick.png", 512, 512);
	public static Bitmap brick_normal = new Bitmap("/textures/brick_normal.png", 512, 512);
	public static Bitmap brick_displacement = new Bitmap("/textures/brick_displacement.png", 512, 512);
	public static Bitmap wood = new Bitmap("/textures/wood.png", 256, 256);
	public static Bitmap wood_normal = new Bitmap("/textures/wood_normal.png", 256, 256);
	public static Bitmap wood_depth = new Bitmap("/textures/wood_depth.png", 256, 256);
	public static Bitmap container = new Bitmap("/textures/container.png", 500, 500);
	public static Bitmap container_emissive = new Bitmap("/textures/container_emissive.png", 500, 500);

	public Bitmap(String path, int width, int height) {
		this.path = path;
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
		load();
	}

	public int getColor(int xTexCoords, int yTexCoords) {
		if (xTexCoords >= 0 && xTexCoords < width && yTexCoords >= 0 && yTexCoords < height) {
			return pixels[xTexCoords + yTexCoords * width];
		}
		return 0xffff00ff;
	}

	public int getColor(float Xpos, float Ypos) {
		if (Xpos >= 0 && Xpos <= 1 && Ypos >= 0 && Xpos <= 1) {
			int xTexCoords = (int) Math.min(Xpos * width, width - 1);
			int yTexCoords = (int) Math.min(Ypos * height, height - 1);
			return pixels[xTexCoords + yTexCoords * width];
		}
		return 0xffff00ff;
	}

	public int getColor(Vector2f textureCoords) {
		if (textureCoords.Xpos >= 0 && textureCoords.Xpos <= 1 && textureCoords.Ypos >= 0 && textureCoords.Xpos <= 1) {
			int xTexCoords = (int) Math.min(textureCoords.Xpos * width, width - 1);
			int yTexCoords = (int) Math.min(textureCoords.Ypos * height, height - 1);
			return pixels[xTexCoords + yTexCoords * width];
		}
		return 0xffff00ff;
	}

	private void load() {
		try {
			BufferedImage image = ImageIO.read(Bitmap.class.getResource(path));
			int w = image.getWidth();
			int h = image.getHeight();
			image.getRGB(0, 0, w, h, pixels, 0, w);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
