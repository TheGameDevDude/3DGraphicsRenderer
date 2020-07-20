package graphics;

public class Renderer {
	public int width;
	public int height;

	public Renderer(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void clear(int[] screenPixels, int color) {
		for (int i = 0; i < screenPixels.length; i++) {
			screenPixels[i] = color;
		}
	}

	public void pixel(int Xpos, int Ypos, int[] screenPixels, int color) {
		screenPixels[Xpos + Ypos * width] = color;
	}
}
