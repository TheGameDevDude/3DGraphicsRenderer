package math;

public class Matrix4f {
	public float[] matrix;

	public Matrix4f() {
		matrix = new float[4 * 4];
	}

	public Matrix4f identity() {
		Matrix4f result = new Matrix4f();
		for (int i = 0; i < 4 * 4; i++) {
			result.matrix[i] = 0;
		}
		result.matrix[0 + 0 * 4] = 1.0f;
		result.matrix[1 + 1 * 4] = 1.0f;
		result.matrix[2 + 2 * 4] = 1.0f;
		result.matrix[3 + 3 * 4] = 1.0f;
		return result;
	}

	public Matrix4f multiply(Matrix4f matrix) {
		Matrix4f result = new Matrix4f();
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				float sum = 0.0f;
				for (int e = 0; e < 4; e++) {
					sum += this.matrix[e + y * 4] * matrix.matrix[x + e * 4];
				}
				result.matrix[x + y * 4] = sum;
			}
		}
		return result;
	}

	public Matrix4f scale(float scaleFactor) {
		Matrix4f result = new Matrix4f();
		for (int i = 0; i < 4 * 4; i++) {
			result.matrix[i] = 0;
		}
		result.matrix[0 + 0 * 4] = scaleFactor;
		result.matrix[1 + 1 * 4] = scaleFactor;
		result.matrix[2 + 2 * 4] = scaleFactor;
		result.matrix[3 + 3 * 4] = 1;
		return result;
	}

	public Matrix4f scale(Vector3f scaleFactor) {
		Matrix4f result = new Matrix4f();
		for (int i = 0; i < 4 * 4; i++) {
			result.matrix[i] = 0;
		}
		result.matrix[0 + 0 * 4] = scaleFactor.Xpos;
		result.matrix[1 + 1 * 4] = scaleFactor.Ypos;
		result.matrix[2 + 2 * 4] = scaleFactor.Zpos;
		result.matrix[3 + 3 * 4] = 1;
		return result;
	}

	public Matrix4f translate(Vector3f vector) {
		Matrix4f result = identity();
		result.matrix[4 + 0 * 4] = vector.Xpos;
		result.matrix[4 + 1 * 4] = vector.Ypos;
		result.matrix[4 + 2 * 4] = vector.Zpos;
		return result;
	}

	public Matrix4f rotateX(float angle) {
		Matrix4f result = identity();
		float sin = (float) Math.sin(Math.toRadians(angle));
		float cos = (float) Math.cos(Math.toRadians(angle));
		result.matrix[1 + 1 * 4] = cos;
		result.matrix[1 + 2 * 4] = -sin;
		result.matrix[2 + 1 * 4] = sin;
		result.matrix[2 + 2 * 4] = cos;
		return result;
	}

	public Matrix4f rotateY(float angle) {
		Matrix4f result = identity();
		float sin = (float) Math.sin(Math.toRadians(angle));
		float cos = (float) Math.cos(Math.toRadians(angle));
		result.matrix[0 + 0 * 4] = cos;
		result.matrix[0 + 2 * 4] = sin;
		result.matrix[2 + 0 * 4] = -sin;
		result.matrix[2 + 2 * 4] = cos;
		return result;
	}

	public Matrix4f rotateZ(float angle) {
		Matrix4f result = identity();
		float sin = (float) Math.sin(Math.toRadians(angle));
		float cos = (float) Math.cos(Math.toRadians(angle));
		result.matrix[0 + 0 * 4] = cos;
		result.matrix[0 + 1 * 4] = -sin;
		result.matrix[1 + 0 * 4] = sin;
		result.matrix[1 + 1 * 4] = cos;
		return result;
	}

	public Matrix4f rotate(float angle, float x, float y, float z) {
		Matrix4f result = identity();

		float r = (float) Math.toRadians(angle);
		float cos = (float) Math.cos(r);
		float sin = (float) Math.sin(r);
		float omc = 1.0f - cos;

		result.matrix[0 + 0 * 4] = x * omc + cos;
		result.matrix[1 + 0 * 4] = y * x * omc + z * sin;
		result.matrix[2 + 0 * 4] = x * z * omc - y * sin;

		result.matrix[0 + 1 * 4] = x * y * omc - z * sin;
		result.matrix[1 + 1 * 4] = y * omc + cos;
		result.matrix[2 + 1 * 4] = y * z * omc + x * sin;

		result.matrix[0 + 2 * 4] = x * z * omc + y * sin;
		result.matrix[1 + 2 * 4] = y * z * omc - x * sin;
		result.matrix[2 + 2 * 4] = z * omc + cos;

		return result;
	}

	public Matrix4f TBN(Vector3f tangent, Vector3f bitangent, Vector3f normal) {
		Matrix4f result = identity();
		result.matrix[0 + 0 * 4] = tangent.Xpos;
		result.matrix[1 + 0 * 4] = tangent.Ypos;
		result.matrix[2 + 0 * 4] = tangent.Zpos;
		result.matrix[0 + 1 * 4] = bitangent.Xpos;
		result.matrix[1 + 1 * 4] = bitangent.Ypos;
		result.matrix[2 + 1 * 4] = bitangent.Zpos;
		result.matrix[0 + 2 * 4] = normal.Xpos;
		result.matrix[1 + 2 * 4] = normal.Ypos;
		result.matrix[2 + 2 * 4] = normal.Zpos;
		return result;
	}

}
