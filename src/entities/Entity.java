package entities;

import java.util.List;

import graphics.Bitmap;
import graphics.Render3D;
import graphics.Triangle;
import graphics.Vertex;
import math.Matrix4f;
import math.Vector3f;
import model.Mesh;

public class Entity {
	public Vector3f position;
	public float Xrot;
	public float Yrot;
	public float Zrot;
	public Vector3f scale;
	public Mesh mesh;
	private Bitmap texture;
	private Bitmap emissiveTexture;
	private Bitmap normalTexture;
	private Bitmap displacementTexture;

	public Entity(Vector3f position, float Xrot, float Yrot, float Zrot, Vector3f scale, Mesh mesh, Bitmap texture) {
		this.position = position;
		this.Xrot = Xrot;
		this.Yrot = Yrot;
		this.Zrot = Zrot;
		this.scale = scale;
		this.mesh = mesh;
		this.texture = texture;
		this.emissiveTexture = null;
		this.normalTexture = null;
		this.displacementTexture = null;

	}

	public Entity(Vector3f position, float Xrot, float Yrot, float Zrot, Vector3f scale, Mesh mesh, Bitmap texture, Bitmap emissiveTexture) {
		this.position = position;
		this.Xrot = Xrot;
		this.Yrot = Yrot;
		this.Zrot = Zrot;
		this.scale = scale;
		this.mesh = mesh;
		this.texture = texture;
		this.emissiveTexture = emissiveTexture;
		this.normalTexture = null;
		this.displacementTexture = null;

	}

	public Entity(Vector3f position, float Xrot, float Yrot, float Zrot, Vector3f scale, Mesh mesh, Bitmap texture, Bitmap normalTexture, Bitmap displacementTexture) {
		this.position = position;
		this.Xrot = Xrot;
		this.Yrot = Yrot;
		this.Zrot = Zrot;
		this.scale = scale;
		this.mesh = mesh;
		this.texture = texture;
		this.emissiveTexture = null;
		this.normalTexture = normalTexture;
		this.displacementTexture = displacementTexture;

	}

	public void renderXYZ(Render3D render3D, int[] screenPixels, List<Light> lights, Camera camera) {
		if (emissiveTexture == null && normalTexture == null && displacementTexture == null) {
			for (Triangle triangle : mesh.triangles) {
				Vertex a = new Vertex(triangle.a.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.a.texCoords, triangle.a.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				Vertex b = new Vertex(triangle.b.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.b.texCoords, triangle.b.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				Vertex c = new Vertex(triangle.c.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.c.texCoords, triangle.c.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				a.position.Xpos += position.Xpos;
				a.position.Ypos += position.Ypos;
				a.position.Zpos += position.Zpos;
				b.position.Xpos += position.Xpos;
				b.position.Ypos += position.Ypos;
				b.position.Zpos += position.Zpos;
				c.position.Xpos += position.Xpos;
				c.position.Ypos += position.Ypos;
				c.position.Zpos += position.Zpos;
				render3D.renderTriangle(a, b, c, texture, lights, camera, screenPixels);
			}
		} else if (normalTexture != null && emissiveTexture == null) {
			for (Triangle triangle : mesh.triangles) {
				Vertex a = new Vertex(triangle.a.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.a.texCoords, triangle.a.normals.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))));
				Vertex b = new Vertex(triangle.b.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.b.texCoords, triangle.b.normals.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))));
				Vertex c = new Vertex(triangle.c.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.c.texCoords, triangle.c.normals.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))));

				a.tangent = triangle.a.tangent.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				a.bitangent = triangle.a.bitangent.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));

				a.position.Xpos += position.Xpos;
				a.position.Ypos += position.Ypos;
				a.position.Zpos += position.Zpos;
				b.position.Xpos += position.Xpos;
				b.position.Ypos += position.Ypos;
				b.position.Zpos += position.Zpos;
				c.position.Xpos += position.Xpos;
				c.position.Ypos += position.Ypos;
				c.position.Zpos += position.Zpos;

				render3D.renderTriangle(a, b, c, texture, normalTexture, displacementTexture, lights, camera, screenPixels);
			}
		} else if (emissiveTexture != null) {
			for (Triangle triangle : mesh.triangles) {
				Vertex a = new Vertex(triangle.a.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.a.texCoords, triangle.a.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				Vertex b = new Vertex(triangle.b.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.b.texCoords, triangle.b.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				Vertex c = new Vertex(triangle.c.position.multiply(new Matrix4f().scale(scale).multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot))))), triangle.c.texCoords, triangle.c.normals.multiply(new Matrix4f().rotateX(Xrot).multiply(new Matrix4f().rotateY(Yrot).multiply(new Matrix4f().rotateZ(Zrot)))));
				a.position.Xpos += position.Xpos;
				a.position.Ypos += position.Ypos;
				a.position.Zpos += position.Zpos;
				b.position.Xpos += position.Xpos;
				b.position.Ypos += position.Ypos;
				b.position.Zpos += position.Zpos;
				c.position.Xpos += position.Xpos;
				c.position.Ypos += position.Ypos;
				c.position.Zpos += position.Zpos;
				render3D.renderTriangle(a, b, c, texture, emissiveTexture, lights, camera, screenPixels);
			}
		}
	}
}
