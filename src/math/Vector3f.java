package math;

public class Vector3f {
	public float Xpos;
	public float Ypos;
	public float Zpos;

	public Vector3f() {
		Xpos = 0;
		Ypos = 0;
		Zpos = 0;
	}

	public Vector3f(float Xpos, float Ypos, float Zpos) {
		this.Xpos = Xpos;
		this.Ypos = Ypos;
		this.Zpos = Zpos;
	}

	public Vector3f(Vector3f vector) {
		this.Xpos = vector.Xpos;
		this.Ypos = vector.Ypos;
		this.Zpos = vector.Zpos;
	}

	public void normalize() {
		float magnitude = getMagnitude();
		Xpos /= magnitude;
		Ypos /= magnitude;
		Zpos /= magnitude;
	}

	public float getMagnitude() {
		return (float) Math.sqrt(Xpos * Xpos + Ypos * Ypos + Zpos * Zpos);
	}

	public void scale(float scaleFactor) {
		Xpos *= scaleFactor;
		Ypos *= scaleFactor;
		Zpos *= scaleFactor;
	}

	public float dot(Vector3f a, Vector3f b) {
		return (a.Xpos * b.Xpos + a.Ypos * b.Ypos + a.Zpos * b.Zpos);
	}

	public Vector3f cross(Vector3f a, Vector3f b) {
		return new Vector3f(a.Ypos * b.Zpos - a.Zpos * b.Ypos, (a.Zpos * b.Xpos - a.Xpos * b.Zpos), (a.Xpos * b.Ypos - a.Ypos * b.Xpos));
	}

	public void shiftToScreenSpace(float width, float height) {
		this.Xpos = ((Xpos * width) + width) / 2;
		this.Ypos = (height - (Ypos * height)) / 2;
	}

	public Vector3f shiftToWorldSpace(float x, float y, float z, float width, float height, float aspectRatio, double fov) {
		Vector3f result = new Vector3f();
		result.Xpos = (((2 * x) - width) / width) * z;
		result.Xpos *= (aspectRatio * Math.tan(Math.toRadians(fov / 2)));
		result.Ypos = ((height - (2 * y)) / height) * z;
		result.Ypos *= Math.tan(Math.toRadians(fov / 2));
		result.Zpos = z;
		return result;
	}

	public Vector3f interpolate(Vector3f a, Vector3f b, float alpha) {
		Vector3f result = new Vector3f();
		result.Xpos = ((1 - alpha) * a.Xpos) + (alpha * b.Xpos);
		result.Ypos = ((1 - alpha) * a.Ypos) + (alpha * b.Ypos);
		result.Zpos = ((1 - alpha) * a.Zpos) + (alpha * b.Zpos);
		return result;
	}

	public Vector3f multiply(Matrix4f matrix) {
		Vector4f result = new Vector4f(Xpos, Ypos, Zpos, 1);
		result = result.multiply(matrix);
		return new Vector3f(result.Xpos, result.Ypos, result.Zpos);
	}
}
