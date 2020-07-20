package math;

public class Vector2f {
	public float Xpos;
	public float Ypos;

	public Vector2f() {
		Xpos = 0;
		Ypos = 0;
	}

	public Vector2f(float Xpos, float Ypos) {
		this.Xpos = Xpos;
		this.Ypos = Ypos;
	}

	public Vector2f(Vector2f vector) {
		this.Xpos = vector.Xpos;
		this.Ypos = vector.Ypos;
	}

	public void normalize() {
		float magnitude = getMagnitude();
		Xpos = Xpos / magnitude;
		Ypos = Ypos / magnitude;

	}

	public float getMagnitude() {
		return (float) Math.sqrt(Xpos * Xpos + Ypos * Ypos);
	}

	public Vector2f interpolate(Vector2f a, Vector2f b, float alpha) {
		Vector2f result = new Vector2f();
		result.Xpos = ((1 - alpha) * a.Xpos) + (alpha * b.Xpos);
		result.Ypos = ((1 - alpha) * a.Ypos) + (alpha * b.Ypos);
		return result;
	}
}
