package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import graphics.Triangle;
import graphics.Vertex;
import math.Vector2f;
import math.Vector3f;

public class OBJLoader {
	public static Mesh crate = OBJLoader.loadMesh("res/models/crate.obj");

	public static Mesh loadMesh(String fileName) {
		List<Triangle> triangles = new ArrayList<Triangle>();

		FileReader fr = null;
		try {
			fr = new FileReader(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		BufferedReader reader = new BufferedReader(fr);
		String line;

		List<Vector3f> positions = new ArrayList<Vector3f>();
		List<Vector2f> textureCoords = new ArrayList<Vector2f>();
		List<Vector3f> normals = new ArrayList<Vector3f>();

		try {
			while ((line = reader.readLine()) != null) {
				String[] currentLine = line.split(" ");
				if (line.startsWith("v ")) {
					Vector3f position = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					positions.add(position);
				} else if (line.startsWith("vt ")) {
					Vector2f texture = new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
					textureCoords.add(texture);
				} else if (line.startsWith("vn ")) {
					Vector3f normal = new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f ")) {
					Vertex a = new Vertex(positions.get((int) Float.parseFloat(currentLine[1].split("/")[0]) - 1), textureCoords.get((int) Float.parseFloat(currentLine[1].split("/")[1]) - 1), normals.get((int) Float.parseFloat(currentLine[1].split("/")[2]) - 1));
					Vertex b = new Vertex(positions.get((int) Float.parseFloat(currentLine[2].split("/")[0]) - 1), textureCoords.get((int) Float.parseFloat(currentLine[2].split("/")[1]) - 1), normals.get((int) Float.parseFloat(currentLine[2].split("/")[2]) - 1));
					Vertex c = new Vertex(positions.get((int) Float.parseFloat(currentLine[3].split("/")[0]) - 1), textureCoords.get((int) Float.parseFloat(currentLine[3].split("/")[1]) - 1), normals.get((int) Float.parseFloat(currentLine[3].split("/")[2]) - 1));
					triangles.add(new Triangle(a, b, c));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int i = 0; i < triangles.size(); i++) {
			Vector3f edge1 = new Vector3f(triangles.get(i).b.position.Xpos - triangles.get(i).a.position.Xpos, triangles.get(i).b.position.Ypos - triangles.get(i).a.position.Ypos, triangles.get(i).b.position.Zpos - triangles.get(i).a.position.Zpos);
			Vector3f edge2 = new Vector3f(triangles.get(i).c.position.Xpos - triangles.get(i).a.position.Xpos, triangles.get(i).c.position.Ypos - triangles.get(i).a.position.Ypos, triangles.get(i).c.position.Zpos - triangles.get(i).a.position.Zpos);
			Vector2f deltaUV1 = new Vector2f(triangles.get(i).b.texCoords.Xpos - triangles.get(i).a.texCoords.Xpos, triangles.get(i).b.texCoords.Ypos - triangles.get(i).a.texCoords.Ypos);
			Vector2f deltaUV2 = new Vector2f(triangles.get(i).c.texCoords.Xpos - triangles.get(i).a.texCoords.Xpos, triangles.get(i).c.texCoords.Ypos - triangles.get(i).a.texCoords.Ypos);

			float f = 1.0f / (deltaUV1.Xpos * deltaUV2.Ypos - deltaUV2.Xpos * deltaUV1.Ypos);

			Vector3f tangent = new Vector3f();
			tangent.Xpos = f * (deltaUV2.Ypos * edge1.Xpos - deltaUV1.Ypos * edge2.Xpos);
			tangent.Ypos = f * (deltaUV2.Ypos * edge1.Ypos - deltaUV1.Ypos * edge2.Ypos);
			tangent.Zpos = f * (deltaUV2.Ypos * edge1.Zpos - deltaUV1.Ypos * edge2.Zpos);

			Vector3f bitangent = new Vector3f();
			bitangent.Xpos = f * (-deltaUV2.Xpos * edge1.Xpos + deltaUV1.Xpos * edge2.Xpos);
			bitangent.Ypos = f * (-deltaUV2.Xpos * edge1.Ypos + deltaUV1.Xpos * edge2.Ypos);
			bitangent.Zpos = f * (-deltaUV2.Xpos * edge1.Zpos + deltaUV1.Xpos * edge2.Zpos);

			triangles.get(i).a.tangent = tangent;
			triangles.get(i).b.tangent = tangent;
			triangles.get(i).c.tangent = tangent;

			triangles.get(i).a.bitangent = bitangent;
			triangles.get(i).b.bitangent = bitangent;
			triangles.get(i).c.bitangent = bitangent;

			tangent.normalize();
			bitangent.normalize();
		}

		return new Mesh(triangles);
	}
}
