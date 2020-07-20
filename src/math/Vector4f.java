package math;

public class Vector4f {
	public float Xpos;
	public float Ypos;
	public float Zpos;
	public float Wpos;

	public Vector4f() {
		Xpos = 0;
		Ypos = 0;
		Zpos = 0;
		Wpos = 0;
	}

	public Vector4f(float Xpos, float Ypos, float Zpos, float Wpos) {
		this.Xpos = Xpos;
		this.Ypos = Ypos;
		this.Zpos = Zpos;
		this.Wpos = Wpos;
	}

	public Vector4f(Vector4f vector) {
		this.Xpos = vector.Xpos;
		this.Ypos = vector.Ypos;
		this.Zpos = vector.Zpos;
		this.Wpos = vector.Wpos;
	}

	public Vector4f multiply(Matrix4f matrix) {
		Vector4f result = new Vector4f();
		result.Xpos = Xpos * matrix.matrix[0 + 0 * 4] + Ypos * matrix.matrix[1 + 0 * 4]
				+ Zpos * matrix.matrix[2 + 0 * 4] + Wpos * matrix.matrix[3 + 0 * 4];

		result.Ypos = Xpos * matrix.matrix[0 + 1 * 4] + Ypos * matrix.matrix[1 + 1 * 4]
				+ Zpos * matrix.matrix[2 + 1 * 4] + Wpos * matrix.matrix[3 + 1 * 4];

		result.Zpos = Xpos * matrix.matrix[0 + 2 * 4] + Ypos * matrix.matrix[1 + 2 * 4]
				+ Zpos * matrix.matrix[2 + 2 * 4] + Wpos * matrix.matrix[3 + 2 * 4];

		result.Wpos = Xpos * matrix.matrix[0 + 3 * 4] + Ypos * matrix.matrix[1 + 3 * 4]
				+ Zpos * matrix.matrix[2 + 3 * 4] + Wpos * matrix.matrix[3 + 3 * 4];
		return result;
	}
}
