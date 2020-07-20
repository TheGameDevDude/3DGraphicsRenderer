package entities;

import math.Vector3f;

public class Light {
	public Vector3f position;
	public float constantvalue;
	public float linearValue;
	public float quadraticValue;
	public int R;
	public int G;
	public int B;

	public Light(Vector3f position, int R, int G, int B) {
		this.position = position;
		this.R = R;
		this.G = G;
		this.B = B;
		constantvalue = 0.7f;
		linearValue = 0.01f;
		quadraticValue = 0.01f;
	}

	public Light(Vector3f position, float constantValue, float linearValue, float quadraticValue, int R, int G, int B) {
		this.position = position;
		this.R = R;
		this.G = G;
		this.B = B;
		this.constantvalue = constantValue;
		this.linearValue = linearValue;
		this.quadraticValue = quadraticValue;
	}
}
