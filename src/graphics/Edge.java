package graphics;

import math.Vector3f;

public class Edge {
	public Vertex a;// top
	public Vertex b;// bottom
	public float xStep;
	public float zStep;
	public float xTexStep;
	public float yTexStep;

	public Vector3f normalStep;

	public Edge(Vertex a, Vertex b) {
		this.a = a;
		this.b = b;
		float Ydist = b.position.Ypos - a.position.Ypos;
		xStep = (b.position.Xpos - a.position.Xpos) / Ydist;
		xTexStep = (b.texCoords.Xpos - a.texCoords.Xpos) / Ydist;
		yTexStep = (b.texCoords.Ypos - a.texCoords.Ypos) / Ydist;
		zStep = (b.position.Zpos - a.position.Zpos) / Ydist;
		normalStep = new Vector3f((b.normals.Xpos - a.normals.Xpos) / Ydist, (b.normals.Ypos - a.normals.Ypos) / Ydist, (b.normals.Zpos - a.normals.Zpos) / Ydist);
	}
}
