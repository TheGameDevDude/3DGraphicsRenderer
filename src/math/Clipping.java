package math;

import java.util.ArrayList;
import java.util.List;

import graphics.Triangle;
import graphics.Vertex;

public class Clipping {
	public List<Triangle> triZnear;
	public List<Triangle> triZfar;

	// clips the triangle by the NEAR Z plane and returns a list of triangles
	public void clipNearZPlane(Triangle triangle, float NEAR) {
		triZnear = new ArrayList<Triangle>();
		if (triangle.a.position.Zpos >= NEAR && triangle.b.position.Zpos >= NEAR && triangle.c.position.Zpos >= NEAR) {
			triZnear.add(triangle);
		} else if (triangle.a.position.Zpos < NEAR && triangle.b.position.Zpos > NEAR && triangle.c.position.Zpos > NEAR) {
			float alphaAB = (NEAR - triangle.a.position.Zpos) / (triangle.b.position.Zpos - triangle.a.position.Zpos);
			float alphaAC = (NEAR - triangle.a.position.Zpos) / (triangle.c.position.Zpos - triangle.a.position.Zpos);
			Vertex AB = new Vertex(new Vector3f().interpolate(triangle.a.position, triangle.b.position, alphaAB), new Vector2f().interpolate(triangle.a.texCoords, triangle.b.texCoords, alphaAB), new Vector3f().interpolate(triangle.a.normals, triangle.b.normals, alphaAB));
			Vertex AC = new Vertex(new Vector3f().interpolate(triangle.a.position, triangle.c.position, alphaAC), new Vector2f().interpolate(triangle.a.texCoords, triangle.c.texCoords, alphaAC), new Vector3f().interpolate(triangle.a.normals, triangle.c.normals, alphaAC));
			triZnear.add(new Triangle(AB, triangle.b, triangle.c));
			triZnear.add(new Triangle(AB, triangle.c, AC));
		} else if (triangle.a.position.Zpos > NEAR && triangle.b.position.Zpos < NEAR && triangle.c.position.Zpos > NEAR) {
			float alphaBC = (NEAR - triangle.b.position.Zpos) / (triangle.c.position.Zpos - triangle.b.position.Zpos);
			float alphaBA = (NEAR - triangle.b.position.Zpos) / (triangle.a.position.Zpos - triangle.b.position.Zpos);
			Vertex BC = new Vertex(new Vector3f().interpolate(triangle.b.position, triangle.c.position, alphaBC), new Vector2f().interpolate(triangle.b.texCoords, triangle.c.texCoords, alphaBC), new Vector3f().interpolate(triangle.b.normals, triangle.c.normals, alphaBC));
			Vertex BA = new Vertex(new Vector3f().interpolate(triangle.b.position, triangle.a.position, alphaBA), new Vector2f().interpolate(triangle.b.texCoords, triangle.a.texCoords, alphaBA), new Vector3f().interpolate(triangle.b.normals, triangle.a.normals, alphaBA));
			triZnear.add(new Triangle(BC, triangle.c, triangle.a));
			triZnear.add(new Triangle(BA, BC, triangle.a));
		} else if (triangle.a.position.Zpos > NEAR && triangle.b.position.Zpos > NEAR && triangle.c.position.Zpos < NEAR) {
			float alphaCA = (NEAR - triangle.c.position.Zpos) / (triangle.a.position.Zpos - triangle.c.position.Zpos);
			float alphaCB = (NEAR - triangle.c.position.Zpos) / (triangle.b.position.Zpos - triangle.c.position.Zpos);
			Vertex CA = new Vertex(new Vector3f().interpolate(triangle.c.position, triangle.a.position, alphaCA), new Vector2f().interpolate(triangle.c.texCoords, triangle.a.texCoords, alphaCA), new Vector3f().interpolate(triangle.c.normals, triangle.a.normals, alphaCA));
			Vertex CB = new Vertex(new Vector3f().interpolate(triangle.c.position, triangle.b.position, alphaCB), new Vector2f().interpolate(triangle.c.texCoords, triangle.b.texCoords, alphaCB), new Vector3f().interpolate(triangle.c.normals, triangle.b.normals, alphaCB));
			triZnear.add(new Triangle(CA, triangle.a, triangle.b));
			triZnear.add(new Triangle(CB, CA, triangle.b));
		} else if (triangle.a.position.Zpos < NEAR && triangle.b.position.Zpos < NEAR && triangle.c.position.Zpos > NEAR) {
			float alphaBC = (NEAR - triangle.b.position.Zpos) / (triangle.c.position.Zpos - triangle.b.position.Zpos);
			float alphaAC = (NEAR - triangle.a.position.Zpos) / (triangle.c.position.Zpos - triangle.a.position.Zpos);
			Vertex BC = new Vertex(new Vector3f().interpolate(triangle.b.position, triangle.c.position, alphaBC), new Vector2f().interpolate(triangle.b.texCoords, triangle.c.texCoords, alphaBC), new Vector3f().interpolate(triangle.b.normals, triangle.c.normals, alphaBC));
			Vertex AC = new Vertex(new Vector3f().interpolate(triangle.a.position, triangle.c.position, alphaAC), new Vector2f().interpolate(triangle.a.texCoords, triangle.c.texCoords, alphaAC), new Vector3f().interpolate(triangle.a.normals, triangle.c.normals, alphaAC));
			triZnear.add(new Triangle(BC, triangle.c, AC));
		} else if (triangle.a.position.Zpos > NEAR && triangle.b.position.Zpos < NEAR && triangle.c.position.Zpos < NEAR) {
			float alphaCA = (NEAR - triangle.c.position.Zpos) / (triangle.a.position.Zpos - triangle.c.position.Zpos);
			float alphaBA = (NEAR - triangle.b.position.Zpos) / (triangle.a.position.Zpos - triangle.b.position.Zpos);
			Vertex CA = new Vertex(new Vector3f().interpolate(triangle.c.position, triangle.a.position, alphaCA), new Vector2f().interpolate(triangle.c.texCoords, triangle.a.texCoords, alphaCA), new Vector3f().interpolate(triangle.c.normals, triangle.a.normals, alphaCA));
			Vertex BA = new Vertex(new Vector3f().interpolate(triangle.b.position, triangle.a.position, alphaBA), new Vector2f().interpolate(triangle.b.texCoords, triangle.a.texCoords, alphaBA), new Vector3f().interpolate(triangle.b.normals, triangle.a.normals, alphaBA));
			triZnear.add(new Triangle(CA, triangle.a, BA));
		} else if (triangle.a.position.Zpos < NEAR && triangle.b.position.Zpos > NEAR && triangle.c.position.Zpos < NEAR) {
			float alphaAB = (NEAR - triangle.a.position.Zpos) / (triangle.b.position.Zpos - triangle.a.position.Zpos);
			float alphaCB = (NEAR - triangle.c.position.Zpos) / (triangle.b.position.Zpos - triangle.c.position.Zpos);
			Vertex AB = new Vertex(new Vector3f().interpolate(triangle.a.position, triangle.b.position, alphaAB), new Vector2f().interpolate(triangle.a.texCoords, triangle.b.texCoords, alphaAB), new Vector3f().interpolate(triangle.a.normals, triangle.b.normals, alphaAB));
			Vertex CB = new Vertex(new Vector3f().interpolate(triangle.c.position, triangle.b.position, alphaCB), new Vector2f().interpolate(triangle.c.texCoords, triangle.b.texCoords, alphaCB), new Vector3f().interpolate(triangle.c.normals, triangle.b.normals, alphaCB));
			triZnear.add(new Triangle(AB, triangle.b, CB));
		}
	}

	// clips for FAR Z plane and returns a list of triangles
	public void clipFarZPlane(Triangle triangle, float FAR) {
		triZfar = new ArrayList<Triangle>();
		if (triangle.a.position.Zpos <= FAR && triangle.b.position.Zpos <= FAR && triangle.c.position.Zpos <= FAR) {
			triZfar.add(triangle);
		} else if (triangle.a.position.Zpos > FAR && triangle.b.position.Zpos < FAR && triangle.c.position.Zpos < FAR) {
			float alphaAB = (FAR - triangle.a.position.Zpos) / (triangle.b.position.Zpos - triangle.a.position.Zpos);
			float alphaAC = (FAR - triangle.a.position.Zpos) / (triangle.c.position.Zpos - triangle.a.position.Zpos);
			Vertex AB = new Vertex(new Vector3f().interpolate(triangle.a.position, triangle.b.position, alphaAB), new Vector2f().interpolate(triangle.a.texCoords, triangle.b.texCoords, alphaAB), new Vector3f().interpolate(triangle.a.normals, triangle.b.normals, alphaAB));
			Vertex AC = new Vertex(new Vector3f().interpolate(triangle.a.position, triangle.c.position, alphaAC), new Vector2f().interpolate(triangle.a.texCoords, triangle.c.texCoords, alphaAC), new Vector3f().interpolate(triangle.a.normals, triangle.c.normals, alphaAC));
			triZfar.add(new Triangle(AB, triangle.b, triangle.c));
			triZfar.add(new Triangle(AB, triangle.c, AC));
		} else if (triangle.a.position.Zpos < FAR && triangle.b.position.Zpos > FAR && triangle.c.position.Zpos < FAR) {
			float alphaBC = (FAR - triangle.b.position.Zpos) / (triangle.c.position.Zpos - triangle.b.position.Zpos);
			float alphaBA = (FAR - triangle.b.position.Zpos) / (triangle.a.position.Zpos - triangle.b.position.Zpos);
			Vertex BC = new Vertex(new Vector3f().interpolate(triangle.b.position, triangle.c.position, alphaBC), new Vector2f().interpolate(triangle.b.texCoords, triangle.c.texCoords, alphaBC), new Vector3f().interpolate(triangle.b.normals, triangle.c.normals, alphaBC));
			Vertex BA = new Vertex(new Vector3f().interpolate(triangle.b.position, triangle.a.position, alphaBA), new Vector2f().interpolate(triangle.b.texCoords, triangle.a.texCoords, alphaBA), new Vector3f().interpolate(triangle.b.normals, triangle.a.normals, alphaBA));
			triZfar.add(new Triangle(BC, triangle.c, triangle.a));
			triZfar.add(new Triangle(BA, BC, triangle.a));
		} else if (triangle.a.position.Zpos < FAR && triangle.b.position.Zpos < FAR && triangle.c.position.Zpos > FAR) {
			float alphaCA = (FAR - triangle.c.position.Zpos) / (triangle.a.position.Zpos - triangle.c.position.Zpos);
			float alphaCB = (FAR - triangle.c.position.Zpos) / (triangle.b.position.Zpos - triangle.c.position.Zpos);
			Vertex CA = new Vertex(new Vector3f().interpolate(triangle.c.position, triangle.a.position, alphaCA), new Vector2f().interpolate(triangle.c.texCoords, triangle.a.texCoords, alphaCA), new Vector3f().interpolate(triangle.c.normals, triangle.a.normals, alphaCA));
			Vertex CB = new Vertex(new Vector3f().interpolate(triangle.c.position, triangle.b.position, alphaCB), new Vector2f().interpolate(triangle.c.texCoords, triangle.b.texCoords, alphaCB), new Vector3f().interpolate(triangle.c.normals, triangle.b.normals, alphaCB));
			triZfar.add(new Triangle(CA, triangle.a, triangle.b));
			triZfar.add(new Triangle(CB, CA, triangle.b));
		} else if (triangle.a.position.Zpos > FAR && triangle.b.position.Zpos > FAR && triangle.c.position.Zpos < FAR) {
			float alphaBC = (FAR - triangle.b.position.Zpos) / (triangle.c.position.Zpos - triangle.b.position.Zpos);
			float alphaAC = (FAR - triangle.a.position.Zpos) / (triangle.c.position.Zpos - triangle.a.position.Zpos);
			Vertex BC = new Vertex(new Vector3f().interpolate(triangle.b.position, triangle.c.position, alphaBC), new Vector2f().interpolate(triangle.b.texCoords, triangle.c.texCoords, alphaBC), new Vector3f().interpolate(triangle.b.normals, triangle.c.normals, alphaBC));
			Vertex AC = new Vertex(new Vector3f().interpolate(triangle.a.position, triangle.c.position, alphaAC), new Vector2f().interpolate(triangle.a.texCoords, triangle.c.texCoords, alphaAC), new Vector3f().interpolate(triangle.a.normals, triangle.c.normals, alphaAC));
			triZfar.add(new Triangle(BC, triangle.c, AC));
		} else if (triangle.a.position.Zpos < FAR && triangle.b.position.Zpos > FAR && triangle.c.position.Zpos > FAR) {
			float alphaCA = (FAR - triangle.c.position.Zpos) / (triangle.a.position.Zpos - triangle.c.position.Zpos);
			float alphaBA = (FAR - triangle.b.position.Zpos) / (triangle.a.position.Zpos - triangle.b.position.Zpos);
			Vertex CA = new Vertex(new Vector3f().interpolate(triangle.c.position, triangle.a.position, alphaCA), new Vector2f().interpolate(triangle.c.texCoords, triangle.a.texCoords, alphaCA), new Vector3f().interpolate(triangle.c.normals, triangle.a.normals, alphaCA));
			Vertex BA = new Vertex(new Vector3f().interpolate(triangle.b.position, triangle.a.position, alphaBA), new Vector2f().interpolate(triangle.b.texCoords, triangle.a.texCoords, alphaBA), new Vector3f().interpolate(triangle.b.normals, triangle.a.normals, alphaBA));
			triZfar.add(new Triangle(CA, triangle.a, BA));
		} else if (triangle.a.position.Zpos > FAR && triangle.b.position.Zpos < FAR && triangle.c.position.Zpos > FAR) {
			float alphaAB = (FAR - triangle.a.position.Zpos) / (triangle.b.position.Zpos - triangle.a.position.Zpos);
			float alphaCB = (FAR - triangle.c.position.Zpos) / (triangle.b.position.Zpos - triangle.c.position.Zpos);
			Vertex AB = new Vertex(new Vector3f().interpolate(triangle.a.position, triangle.b.position, alphaAB), new Vector2f().interpolate(triangle.a.texCoords, triangle.b.texCoords, alphaAB), new Vector3f().interpolate(triangle.a.normals, triangle.b.normals, alphaAB));
			Vertex CB = new Vertex(new Vector3f().interpolate(triangle.c.position, triangle.b.position, alphaCB), new Vector2f().interpolate(triangle.c.texCoords, triangle.b.texCoords, alphaCB), new Vector3f().interpolate(triangle.c.normals, triangle.b.normals, alphaCB));
			triZfar.add(new Triangle(AB, triangle.b, CB));
		}
	}
}
