package graphics;

import java.util.ArrayList;
import java.util.List;

import entities.Camera;
import entities.Light;
import mainGameLoop.Main;
import math.Clipping;
import math.Vector2f;
import math.Vector3f;
import math.Matrix4f;

public class Render3D extends Renderer {

	private Clipping clipping = new Clipping();
	private static float NEAR = 0.01f;
	private static float FAR = 100f;
	private static float aspectRatio;
	private static double fov = 60;
	private static int clearColor = 0;

	private double[] depthBuffer;
	private Matrix4f TBN = new Matrix4f();

	public Render3D(int width, int height) {
		super(width, height);
		aspectRatio = (float) Main.widthResize / (float) Main.heightResize;

		depthBuffer = new double[width * height];
		for (int i = 0; i < depthBuffer.length; i++) {
			depthBuffer[i] = Float.MAX_VALUE;
		}
	}

	public void tick() {
		aspectRatio = (float) Main.widthResize / (float) Main.heightResize;
		fov = 90;
	}

	public void clear() {
		for (int i = 0; i < depthBuffer.length; i++) {
			depthBuffer[i] = Float.MAX_VALUE;
		}
	}

	public void clear(int[] screenPixels) {
		for (int i = 0; i < depthBuffer.length; i++) {
			depthBuffer[i] = Float.MAX_VALUE;
		}

		clear(screenPixels, clearColor);
	}

	public void renderTriangle(Vertex a, Vertex b, Vertex c, Bitmap texture, List<Light> lights, Camera camera, int[] screenPixels) {
		Vertex aTranslate = new Vertex(new Vector3f(a.position), new Vector2f(a.texCoords), new Vector3f(a.normals));
		Vertex bTranslate = new Vertex(new Vector3f(b.position), new Vector2f(b.texCoords), new Vector3f(b.normals));
		Vertex cTranslate = new Vertex(new Vector3f(c.position), new Vector2f(c.texCoords), new Vector3f(c.normals));

		// translate opposite to camera position(if the camera moves left the whole
		// world moves right to create an illusion that you move left)
		aTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		aTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		aTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		bTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		bTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		bTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		cTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		cTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		cTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		// same as translation we do rotation relative to camera
		aTranslate.position = aTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		bTranslate.position = bTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		cTranslate.position = cTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));

		aTranslate.position = aTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		bTranslate.position = bTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		cTranslate.position = cTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));

		aTranslate.normals = aTranslate.normals.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		bTranslate.normals = bTranslate.normals.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		cTranslate.normals = cTranslate.normals.multiply(new Matrix4f().rotateY(camera.getAngleY()));

		aTranslate.normals = aTranslate.normals.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		bTranslate.normals = bTranslate.normals.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		cTranslate.normals = cTranslate.normals.multiply(new Matrix4f().rotateX(camera.getAngleX()));

		// if the triangles are too far or behind the NEAR then don't render them
		if (aTranslate.position.Zpos <= NEAR && bTranslate.position.Zpos <= NEAR && cTranslate.position.Zpos <= NEAR) {
			return;
		} else if (aTranslate.position.Zpos >= FAR && bTranslate.position.Zpos >= FAR && cTranslate.position.Zpos >= FAR) {
			return;
		}

		List<Light> rotatedLights = new ArrayList<Light>();
		for (Light light : lights) {
			Vector3f rotatedPosition = new Vector3f(light.position.Xpos - camera.cameraPosition.Xpos, light.position.Ypos - camera.cameraPosition.Ypos, light.position.Zpos - camera.cameraPosition.Zpos);
			rotatedPosition = rotatedPosition.multiply(new Matrix4f().rotateY(camera.getAngleY()));
			rotatedPosition = rotatedPosition.multiply(new Matrix4f().rotateX(camera.getAngleX()));
			rotatedLights.add(new Light(rotatedPosition, light.constantvalue, light.linearValue, light.quadraticValue, light.R, light.G, light.B));
		}

		Triangle triangle = new Triangle(aTranslate, bTranslate, cTranslate);
		// geometric clipping in clockwise
		clipping.clipNearZPlane(triangle, NEAR);
		for (Triangle tri1 : clipping.triZnear) {
			Triangle triangle1 = new Triangle(tri1.a, tri1.b, tri1.c);
			clipping.clipFarZPlane(triangle1, FAR);
			for (Triangle tri2 : clipping.triZfar) {
				rasterizeTriangle(tri2.a, tri2.b, tri2.c, texture, rotatedLights, screenPixels);
			}
		}
		clipping.triZnear.clear();
		clipping.triZfar.clear();
	}

	private void rasterizeTriangle(Vertex a, Vertex b, Vertex c, Bitmap texture, List<Light> lights, int[] screenPixels) {
		Vertex min = new Vertex(new Vector3f(a.position), new Vector2f(a.texCoords), new Vector3f(a.normals));
		Vertex mid = new Vertex(new Vector3f(b.position), new Vector2f(b.texCoords), new Vector3f(b.normals));
		Vertex max = new Vertex(new Vector3f(c.position), new Vector2f(c.texCoords), new Vector3f(c.normals));

		min.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));
		mid.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));
		max.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));

		min.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));
		mid.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));
		max.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));

		min.position.Xpos /= min.position.Zpos;
		min.position.Ypos /= min.position.Zpos;

		mid.position.Xpos /= mid.position.Zpos;
		mid.position.Ypos /= mid.position.Zpos;

		max.position.Xpos /= max.position.Zpos;
		max.position.Ypos /= max.position.Zpos;

		min.position.shiftToScreenSpace(width, height);
		mid.position.shiftToScreenSpace(width, height);
		max.position.shiftToScreenSpace(width, height);

		// if the triangle is out of screen then don't render it
		if (min.position.Xpos >= width && mid.position.Xpos >= width && max.position.Xpos >= width) {
			return;
		} else if (min.position.Xpos <= 0 && mid.position.Xpos <= 0 && max.position.Xpos <= 0) {
			return;
		}

		if (min.position.Ypos >= height && mid.position.Ypos >= height && max.position.Ypos >= height) {
			return;
		} else if (min.position.Ypos <= 0 && mid.position.Ypos <= 0 && max.position.Ypos <= 0) {
			return;
		}

		min.texCoords.Xpos /= min.position.Zpos;
		min.texCoords.Ypos /= min.position.Zpos;

		mid.texCoords.Xpos /= mid.position.Zpos;
		mid.texCoords.Ypos /= mid.position.Zpos;

		max.texCoords.Xpos /= max.position.Zpos;
		max.texCoords.Ypos /= max.position.Zpos;

		min.normals.Xpos /= min.position.Zpos;
		mid.normals.Xpos /= mid.position.Zpos;
		max.normals.Xpos /= max.position.Zpos;

		min.normals.Ypos /= min.position.Zpos;
		mid.normals.Ypos /= mid.position.Zpos;
		max.normals.Ypos /= max.position.Zpos;

		min.normals.Zpos /= min.position.Zpos;
		mid.normals.Zpos /= mid.position.Zpos;
		max.normals.Zpos /= max.position.Zpos;

		min.position.Zpos = 1 / min.position.Zpos;
		mid.position.Zpos = 1 / mid.position.Zpos;
		max.position.Zpos = 1 / max.position.Zpos;

		Vector2f left = new Vector2f(mid.position.Xpos - min.position.Xpos, mid.position.Ypos - min.position.Ypos);
		Vector2f right = new Vector2f(max.position.Xpos - min.position.Xpos, max.position.Ypos - min.position.Ypos);

		float backfaceCulling = left.Xpos * right.Ypos - left.Ypos * right.Xpos;

		if (backfaceCulling < 0) {
			return;
		}

		Vertex[] sort = new Vertex[3];

		sort[0] = new Vertex(new Vector3f(min.position), new Vector2f(min.texCoords), new Vector3f(min.normals));
		sort[1] = new Vertex(new Vector3f(mid.position), new Vector2f(mid.texCoords), new Vector3f(mid.normals));
		sort[2] = new Vertex(new Vector3f(max.position), new Vector2f(max.texCoords), new Vector3f(max.normals));

		// sorting according to the y position for rasterization
		for (int i = 0; i < sort.length - 1; i++) {
			for (int j = i + 1; j < sort.length; j++) {
				if (sort[j].position.Ypos < sort[i].position.Ypos) {
					Vertex temp = sort[j];
					sort[j] = sort[i];
					sort[i] = temp;
				}
			}
		}

		min = sort[0];
		mid = sort[1];
		max = sort[2];

		float alpha = (mid.position.Ypos - min.position.Ypos) / (max.position.Ypos - min.position.Ypos);
		Vector3f interpolatedVector = new Vector3f().interpolate(min.position, max.position, alpha);
		Vector2f interpolatedTexCoords = new Vector2f().interpolate(min.texCoords, max.texCoords, alpha);
		Vector3f interpolatedNormal = new Vector3f().interpolate(min.normals, max.normals, alpha);
		Vertex interpolatedVertex = new Vertex(interpolatedVector, interpolatedTexCoords, interpolatedNormal);

		if (interpolatedVector.Xpos > mid.position.Xpos) {
			// flatBottom
			rasterize(new Edge(min, mid), new Edge(min, interpolatedVertex), texture, lights, screenPixels);
			// flatTop
			rasterize(new Edge(mid, max), new Edge(interpolatedVertex, max), texture, lights, screenPixels);
		} else {
			// flatBottom
			rasterize(new Edge(min, interpolatedVertex), new Edge(min, mid), texture, lights, screenPixels);
			// flatTop
			rasterize(new Edge(interpolatedVertex, max), new Edge(mid, max), texture, lights, screenPixels);
		}
	}

	private void rasterize(Edge left, Edge right, Bitmap texture, List<Light> lights, int[] screenPixels) {
		int yStart = (int) Math.ceil(left.a.position.Ypos - 0.5f);
		int yEnd = (int) Math.ceil(right.b.position.Ypos - 0.5f);

		// raster clipping in Y - axis
		float yDistTop = 0;

		if (yStart < 0) {
			yDistTop = -yStart;
			yStart = 0;
		}

		if (yEnd >= height) {
			yEnd = height;
		}

		float LYPreStep = ((float) ((int) Math.ceil(left.a.position.Ypos - 0.5f)) + 0.5f - left.a.position.Ypos);
		float RYPreStep = ((float) ((int) Math.ceil(right.a.position.Ypos - 0.5f)) + 0.5f - right.a.position.Ypos);

		float leftXTex = (left.a.texCoords.Xpos + left.xTexStep * LYPreStep) + left.xTexStep * yDistTop;
		float leftYTex = (left.a.texCoords.Ypos + left.yTexStep * LYPreStep) + left.yTexStep * yDistTop;
		float rightXTex = (right.a.texCoords.Xpos + right.xTexStep * RYPreStep) + right.xTexStep * yDistTop;
		float rightYTex = (right.a.texCoords.Ypos + right.yTexStep * RYPreStep) + right.yTexStep * yDistTop;

		float leftZ = (left.a.position.Zpos + left.zStep * LYPreStep) + left.zStep * yDistTop;
		float rightZ = (right.a.position.Zpos + right.zStep * RYPreStep) + right.zStep * yDistTop;

		Vector3f leftNormal = new Vector3f((left.a.normals.Xpos + left.normalStep.Xpos * LYPreStep) + left.normalStep.Xpos * yDistTop, (left.a.normals.Ypos + left.normalStep.Ypos * LYPreStep) + left.normalStep.Ypos * yDistTop, (left.a.normals.Zpos + left.normalStep.Zpos * LYPreStep) + left.normalStep.Zpos * yDistTop);
		Vector3f rightNormal = new Vector3f((right.a.normals.Xpos + right.normalStep.Xpos * RYPreStep) + right.normalStep.Xpos * yDistTop, (right.a.normals.Ypos + right.normalStep.Ypos * RYPreStep) + right.normalStep.Ypos * yDistTop, (right.a.normals.Zpos + right.normalStep.Zpos * RYPreStep) + right.normalStep.Zpos * yDistTop);

		for (int y = yStart; y < yEnd; y++) {
			float xMin = left.xStep * ((float) (y) + 0.5f - left.a.position.Ypos) + left.a.position.Xpos;
			float xMax = right.xStep * ((float) (y) + 0.5f - right.a.position.Ypos) + right.a.position.Xpos;

			int xStart = (int) Math.ceil(xMin - 0.5f);
			int xEnd = (int) Math.ceil(xMax - 0.5f);

			int leftXClamp = 0;
			float XpreStep = ((float) (xStart) + 0.5f - xMin);

			// raster clipping in X - axis
			if (xStart < 0) {
				leftXClamp = -xStart;
				xStart = 0;
			}

			if (xEnd >= width) {
				xEnd = width;
			}

			float xDist = xMax - xMin;

			float xTexStep = (rightXTex - leftXTex) / xDist;
			float yTexStep = (rightYTex - leftYTex) / xDist;

			float xTex = leftXTex + xTexStep * XpreStep + xTexStep * leftXClamp;
			float yTex = leftYTex + yTexStep * XpreStep + yTexStep * leftXClamp;

			float zStep = (rightZ - leftZ) / xDist;
			float zCoords = leftZ + zStep * XpreStep + zStep * leftXClamp;

			Vector3f normalStep = new Vector3f((rightNormal.Xpos - leftNormal.Xpos) / xDist, (rightNormal.Ypos - leftNormal.Ypos) / xDist, (rightNormal.Zpos - leftNormal.Zpos) / xDist);
			Vector3f normalX = new Vector3f(leftNormal.Xpos + normalStep.Xpos * XpreStep + normalStep.Xpos * leftXClamp, leftNormal.Ypos + normalStep.Ypos * XpreStep + normalStep.Ypos * leftXClamp, leftNormal.Zpos + normalStep.Zpos * XpreStep + normalStep.Zpos * leftXClamp);
			for (int x = xStart; x < xEnd; x++) {
				float z = 1 / zCoords;

				int index = x + y * width;
				int texColor = texture.getColor(xTex * z, yTex * z);

				int tRed = (texColor >> 16) & 0xff;
				int tGreen = (texColor >> 8) & 0xff;
				int tBlue = (texColor) & 0xff;

				int cRed = (clearColor >> 16) & 0xff;
				int cGreen = (clearColor >> 8) & 0xff;
				int cBlue = (clearColor) & 0xff;

				int R = 0, G = 0, B = 0;

				for (int i = 0; i < lights.size(); i++) {
					Vector3f pixelWorldPosition = new Vector3f().shiftToWorldSpace((float) x, (float) y, (float) z, width, height, aspectRatio, fov);
					Vector3f pixelWorldPositionToLight = new Vector3f(lights.get(i).position.Xpos - pixelWorldPosition.Xpos, lights.get(i).position.Ypos - pixelWorldPosition.Ypos, lights.get(i).position.Zpos - pixelWorldPosition.Zpos);
					Vector3f normal = new Vector3f(normalX.Xpos * z, normalX.Ypos * z, normalX.Zpos * z);
					float distance = pixelWorldPositionToLight.getMagnitude();
					normal.normalize();
					pixelWorldPositionToLight.normalize();
					float lightValue = new Vector3f().dot(pixelWorldPositionToLight, normal);

					if (lightValue < 0.1f) {
						lightValue = 0.1f;
					}
					if (lightValue > 1) {
						lightValue = 1;
					}

					float attenuation = 1 / (lights.get(i).constantvalue + distance * lights.get(i).linearValue + distance * distance * lights.get(i).quadraticValue);

					R += (int) ((tRed * lightValue * lights.get(i).R * lightValue * attenuation) / 255);
					G += (int) ((tGreen * lightValue * lights.get(i).G * lightValue * attenuation) / 255);
					B += (int) ((tBlue * lightValue * lights.get(i).B * lightValue * attenuation) / 255);
				}

				if (R >= 255) {
					R = 255;
				}

				if (G >= 255) {
					G = 255;
				}

				if (B >= 255) {
					B = 255;
				}

				Vector3f skyColor = new Vector3f(cRed, cGreen, cBlue);
				Vector3f objectColor = new Vector3f(R, G, B);
				float visibility = (z * 1.2f) / FAR;
				Vector3f fog = new Vector3f().interpolate(objectColor, skyColor, visibility + 0.1f);

				R = (int) fog.Xpos;
				G = (int) fog.Ypos;
				B = (int) fog.Zpos;

				// main color
				int color = (R << 16) | (G << 8) | (B);

				if (z < depthBuffer[index]) {
						pixel(x, y, screenPixels, color);
						depthBuffer[index] = z;
				}
				xTex += xTexStep;
				yTex += yTexStep;
				zCoords += zStep;
				normalX.Xpos += normalStep.Xpos;
				normalX.Ypos += normalStep.Ypos;
				normalX.Zpos += normalStep.Zpos;
			}

			leftXTex += left.xTexStep;
			leftYTex += left.yTexStep;
			rightXTex += right.xTexStep;
			rightYTex += right.yTexStep;
			leftZ += left.zStep;
			rightZ += right.zStep;
			leftNormal.Xpos += left.normalStep.Xpos;
			leftNormal.Ypos += left.normalStep.Ypos;
			leftNormal.Zpos += left.normalStep.Zpos;
			rightNormal.Xpos += right.normalStep.Xpos;
			rightNormal.Ypos += right.normalStep.Ypos;
			rightNormal.Zpos += right.normalStep.Zpos;
		}
	}

	public void renderTriangle(Vertex a, Vertex b, Vertex c, Bitmap texture, Bitmap emissiveTexture, List<Light> lights, Camera camera, int[] screenPixels) {
		Vertex aTranslate = new Vertex(new Vector3f(a.position), new Vector2f(a.texCoords), new Vector3f(a.normals));
		Vertex bTranslate = new Vertex(new Vector3f(b.position), new Vector2f(b.texCoords), new Vector3f(b.normals));
		Vertex cTranslate = new Vertex(new Vector3f(c.position), new Vector2f(c.texCoords), new Vector3f(c.normals));

		// translate opposite to camera position(if the camera moves left the whole
		// world moves right to create an illusion that you move left)
		aTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		aTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		aTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		bTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		bTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		bTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		cTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		cTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		cTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		// same as translation we do rotation relative to camera
		aTranslate.position = aTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		bTranslate.position = bTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		cTranslate.position = cTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));

		aTranslate.position = aTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		bTranslate.position = bTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		cTranslate.position = cTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));

		aTranslate.normals = aTranslate.normals.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		bTranslate.normals = bTranslate.normals.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		cTranslate.normals = cTranslate.normals.multiply(new Matrix4f().rotateY(camera.getAngleY()));

		aTranslate.normals = aTranslate.normals.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		bTranslate.normals = bTranslate.normals.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		cTranslate.normals = cTranslate.normals.multiply(new Matrix4f().rotateX(camera.getAngleX()));

		// if the triangles are too far or behind the NEAR then don't render them
		if (aTranslate.position.Zpos <= NEAR && bTranslate.position.Zpos <= NEAR && cTranslate.position.Zpos <= NEAR) {
			return;
		} else if (aTranslate.position.Zpos >= FAR && bTranslate.position.Zpos >= FAR && cTranslate.position.Zpos >= FAR) {
			return;
		}

		List<Light> rotatedLights = new ArrayList<Light>();
		for (Light light : lights) {
			Vector3f rotatedPosition = new Vector3f(light.position.Xpos - camera.cameraPosition.Xpos, light.position.Ypos - camera.cameraPosition.Ypos, light.position.Zpos - camera.cameraPosition.Zpos);
			rotatedPosition = rotatedPosition.multiply(new Matrix4f().rotateY(camera.getAngleY())).multiply(new Matrix4f().rotateX(camera.getAngleX()));
			rotatedLights.add(new Light(rotatedPosition, light.constantvalue, light.linearValue, light.quadraticValue, light.R, light.G, light.B));
		}

		Triangle triangle = new Triangle(aTranslate, bTranslate, cTranslate);
		// geometric clipping in clockwise
		clipping.clipNearZPlane(triangle, NEAR);
		for (Triangle tri1 : clipping.triZnear) {
			Triangle triangle1 = new Triangle(tri1.a, tri1.b, tri1.c);
			clipping.clipFarZPlane(triangle1, FAR);
			for (Triangle tri2 : clipping.triZfar) {
				rasterizeTriangle(tri2.a, tri2.b, tri2.c, texture, emissiveTexture, rotatedLights, screenPixels);
			}
		}
		clipping.triZnear.clear();
		clipping.triZfar.clear();
	}

	private void rasterizeTriangle(Vertex a, Vertex b, Vertex c, Bitmap texture, Bitmap emissiveTexture, List<Light> lights, int[] screenPixels) {
		Vertex min = new Vertex(new Vector3f(a.position), new Vector2f(a.texCoords), new Vector3f(a.normals));
		Vertex mid = new Vertex(new Vector3f(b.position), new Vector2f(b.texCoords), new Vector3f(b.normals));
		Vertex max = new Vertex(new Vector3f(c.position), new Vector2f(c.texCoords), new Vector3f(c.normals));

		min.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));
		mid.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));
		max.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));

		min.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));
		mid.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));
		max.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));

		min.position.Xpos /= min.position.Zpos;
		min.position.Ypos /= min.position.Zpos;

		mid.position.Xpos /= mid.position.Zpos;
		mid.position.Ypos /= mid.position.Zpos;

		max.position.Xpos /= max.position.Zpos;
		max.position.Ypos /= max.position.Zpos;

		min.position.shiftToScreenSpace(width, height);
		mid.position.shiftToScreenSpace(width, height);
		max.position.shiftToScreenSpace(width, height);

		// if the triangle is out of screen then don't render it
		if (min.position.Xpos >= width && mid.position.Xpos >= width && max.position.Xpos >= width) {
			return;
		} else if (min.position.Xpos <= 0 && mid.position.Xpos <= 0 && max.position.Xpos <= 0) {
			return;
		}

		if (min.position.Ypos >= width && mid.position.Ypos >= height && max.position.Ypos >= height) {
			return;
		} else if (min.position.Ypos <= 0 && mid.position.Ypos <= 0 && max.position.Ypos <= 0) {
			return;
		}

		min.texCoords.Xpos /= min.position.Zpos;
		min.texCoords.Ypos /= min.position.Zpos;

		mid.texCoords.Xpos /= mid.position.Zpos;
		mid.texCoords.Ypos /= mid.position.Zpos;

		max.texCoords.Xpos /= max.position.Zpos;
		max.texCoords.Ypos /= max.position.Zpos;

		min.normals.Xpos /= min.position.Zpos;
		mid.normals.Xpos /= mid.position.Zpos;
		max.normals.Xpos /= max.position.Zpos;

		min.normals.Ypos /= min.position.Zpos;
		mid.normals.Ypos /= mid.position.Zpos;
		max.normals.Ypos /= max.position.Zpos;

		min.normals.Zpos /= min.position.Zpos;
		mid.normals.Zpos /= mid.position.Zpos;
		max.normals.Zpos /= max.position.Zpos;

		min.position.Zpos = 1 / min.position.Zpos;
		mid.position.Zpos = 1 / mid.position.Zpos;
		max.position.Zpos = 1 / max.position.Zpos;

		Vector2f left = new Vector2f(mid.position.Xpos - min.position.Xpos, mid.position.Ypos - min.position.Ypos);
		Vector2f right = new Vector2f(max.position.Xpos - min.position.Xpos, max.position.Ypos - min.position.Ypos);

		float backfaceCulling = left.Xpos * right.Ypos - left.Ypos * right.Xpos;

		if (backfaceCulling < 0) {
			return;
		}

		Vertex[] sort = new Vertex[3];

		sort[0] = new Vertex(new Vector3f(min.position), new Vector2f(min.texCoords), new Vector3f(min.normals));
		sort[1] = new Vertex(new Vector3f(mid.position), new Vector2f(mid.texCoords), new Vector3f(mid.normals));
		sort[2] = new Vertex(new Vector3f(max.position), new Vector2f(max.texCoords), new Vector3f(max.normals));

		// sorting according to the y position for rasterization
		for (int i = 0; i < sort.length - 1; i++) {
			for (int j = i + 1; j < sort.length; j++) {
				if (sort[j].position.Ypos < sort[i].position.Ypos) {
					Vertex temp = sort[j];
					sort[j] = sort[i];
					sort[i] = temp;
				}
			}
		}

		min = sort[0];
		mid = sort[1];
		max = sort[2];

		float alpha = (mid.position.Ypos - min.position.Ypos) / (max.position.Ypos - min.position.Ypos);
		Vector3f interpolatedVector = new Vector3f().interpolate(min.position, max.position, alpha);
		Vector2f interpolatedTexCoords = new Vector2f().interpolate(min.texCoords, max.texCoords, alpha);
		Vector3f interpolatedNormal = new Vector3f().interpolate(min.normals, max.normals, alpha);
		Vertex interpolatedVertex = new Vertex(interpolatedVector, interpolatedTexCoords, interpolatedNormal);

		if (interpolatedVector.Xpos > mid.position.Xpos) {
			// flatBottom
			rasterize(new Edge(min, mid), new Edge(min, interpolatedVertex), texture, emissiveTexture, lights, screenPixels);
			// flatTop
			rasterize(new Edge(mid, max), new Edge(interpolatedVertex, max), texture, emissiveTexture, lights, screenPixels);
		} else {
			// flatBottom
			rasterize(new Edge(min, interpolatedVertex), new Edge(min, mid), texture, emissiveTexture, lights, screenPixels);
			// flatTop
			rasterize(new Edge(interpolatedVertex, max), new Edge(mid, max), texture, emissiveTexture, lights, screenPixels);
		}
	}

	private void rasterize(Edge left, Edge right, Bitmap texture, Bitmap emissiveTexture, List<Light> lights, int[] screenPixels) {
		int yStart = (int) Math.ceil(left.a.position.Ypos - 0.5f);
		int yEnd = (int) Math.ceil(right.b.position.Ypos - 0.5f);

		// raster clipping in Y - axis
		float yDistTop = 0;

		if (yStart < 0) {
			yDistTop = -yStart;
			yStart = 0;
		}

		if (yEnd >= height) {
			yEnd = height;
		}

		float LYPreStep = ((float) ((int) Math.ceil(left.a.position.Ypos - 0.5f)) + 0.5f - left.a.position.Ypos);
		float RYPreStep = ((float) ((int) Math.ceil(right.a.position.Ypos - 0.5f)) + 0.5f - right.a.position.Ypos);

		float leftXTex = (left.a.texCoords.Xpos + left.xTexStep * LYPreStep) + left.xTexStep * yDistTop;
		float leftYTex = (left.a.texCoords.Ypos + left.yTexStep * LYPreStep) + left.yTexStep * yDistTop;
		float rightXTex = (right.a.texCoords.Xpos + right.xTexStep * RYPreStep) + right.xTexStep * yDistTop;
		float rightYTex = (right.a.texCoords.Ypos + right.yTexStep * RYPreStep) + right.yTexStep * yDistTop;

		float leftZ = (left.a.position.Zpos + left.zStep * LYPreStep) + left.zStep * yDistTop;
		float rightZ = (right.a.position.Zpos + right.zStep * RYPreStep) + right.zStep * yDistTop;

		Vector3f leftNormal = new Vector3f((left.a.normals.Xpos + left.normalStep.Xpos * LYPreStep) + left.normalStep.Xpos * yDistTop, (left.a.normals.Ypos + left.normalStep.Ypos * LYPreStep) + left.normalStep.Ypos * yDistTop, (left.a.normals.Zpos + left.normalStep.Zpos * LYPreStep) + left.normalStep.Zpos * yDistTop);
		Vector3f rightNormal = new Vector3f((right.a.normals.Xpos + right.normalStep.Xpos * RYPreStep) + right.normalStep.Xpos * yDistTop, (right.a.normals.Ypos + right.normalStep.Ypos * RYPreStep) + right.normalStep.Ypos * yDistTop, (right.a.normals.Zpos + right.normalStep.Zpos * RYPreStep) + right.normalStep.Zpos * yDistTop);

		for (int y = yStart; y < yEnd; y++) {
			float xMin = left.xStep * ((float) (y) + 0.5f - left.a.position.Ypos) + left.a.position.Xpos;
			float xMax = right.xStep * ((float) (y) + 0.5f - right.a.position.Ypos) + right.a.position.Xpos;

			int xStart = (int) Math.ceil(xMin - 0.5f);
			int xEnd = (int) Math.ceil(xMax - 0.5f);

			int leftXClamp = 0;
			float XpreStep = ((float) (xStart) + 0.5f - xMin);

			// raster clipping in X - axis
			if (xStart < 0) {
				leftXClamp = -xStart;
				xStart = 0;
			}

			if (xEnd >= width) {
				xEnd = width;
			}

			float xDist = xMax - xMin;

			float xTexStep = (rightXTex - leftXTex) / xDist;
			float yTexStep = (rightYTex - leftYTex) / xDist;

			float xTex = leftXTex + xTexStep * XpreStep + xTexStep * leftXClamp;
			float yTex = leftYTex + yTexStep * XpreStep + yTexStep * leftXClamp;

			float zStep = (rightZ - leftZ) / xDist;
			float zCoords = leftZ + zStep * XpreStep + zStep * leftXClamp;

			Vector3f normalStep = new Vector3f((rightNormal.Xpos - leftNormal.Xpos) / xDist, (rightNormal.Ypos - leftNormal.Ypos) / xDist, (rightNormal.Zpos - leftNormal.Zpos) / xDist);
			Vector3f normalX = new Vector3f(leftNormal.Xpos + normalStep.Xpos * XpreStep + normalStep.Xpos * leftXClamp, leftNormal.Ypos + normalStep.Ypos * XpreStep + normalStep.Ypos * leftXClamp, leftNormal.Zpos + normalStep.Zpos * XpreStep + normalStep.Zpos * leftXClamp);

			for (int x = xStart; x < xEnd; x++) {
				float z = 1 / zCoords;

				int xTexCoords = (int) Math.min(xTex * z * texture.width, texture.width - 1);
				int yTexCoords = (int) Math.min(yTex * z * texture.height, texture.height - 1);

				int index = x + y * width;
				int texColor = 0;
				int texEmissiveColor = 0;

				texColor = texture.getColor(xTexCoords, yTexCoords);
				texEmissiveColor = emissiveTexture.getColor(xTexCoords, yTexCoords);

				int color = 0;// main color

				if (texEmissiveColor == 0xff000000) {
					int tRed = (texColor >> 16) & 0xff;
					int tGreen = (texColor >> 8) & 0xff;
					int tBlue = (texColor) & 0xff;

					int cRed = (clearColor >> 16) & 0xff;
					int cGreen = (clearColor >> 8) & 0xff;
					int cBlue = (clearColor) & 0xff;

					int R = 0, G = 0, B = 0;

					for (int i = 0; i < lights.size(); i++) {
						Vector3f pixelWorldPosition = new Vector3f().shiftToWorldSpace((float) x, (float) y, (float) z, width, height, aspectRatio, fov);
						Vector3f pixelWorldPositionToLight = new Vector3f(lights.get(i).position.Xpos - pixelWorldPosition.Xpos, lights.get(i).position.Ypos - pixelWorldPosition.Ypos, lights.get(i).position.Zpos - pixelWorldPosition.Zpos);
						Vector3f normal = new Vector3f(normalX.Xpos * z, normalX.Ypos * z, normalX.Zpos * z);
						float distance = pixelWorldPositionToLight.getMagnitude();
						normal.normalize();
						pixelWorldPositionToLight.normalize();
						float lightValue = new Vector3f().dot(pixelWorldPositionToLight, normal);

						if (lightValue < 0.1f) {
							lightValue = 0.1f;
						}
						if (lightValue > 1) {
							lightValue = 1;
						}

						float attenuation = 1 / (lights.get(i).constantvalue + distance * lights.get(i).linearValue + distance * distance * lights.get(i).quadraticValue);

						R += (int) ((tRed * lightValue * lights.get(i).R * lightValue * attenuation) / 255);
						G += (int) ((tGreen * lightValue * lights.get(i).G * lightValue * attenuation) / 255);
						B += (int) ((tBlue * lightValue * lights.get(i).B * lightValue * attenuation) / 255);

					}

					if (R >= 255) {
						R = 255;
					}

					if (G >= 255) {
						G = 255;
					}

					if (B >= 255) {
						B = 255;
					}

					Vector3f skyColor = new Vector3f(cRed, cGreen, cBlue);
					Vector3f objectColor = new Vector3f(R, G, B);
					float visibility = (z * 1.2f) / FAR;
					Vector3f fog = new Vector3f().interpolate(objectColor, skyColor, visibility + 0.1f);

					R = (int) fog.Xpos;
					G = (int) fog.Ypos;
					B = (int) fog.Zpos;

					color = (R << 16) | (G << 8) | (B);
				} else {
					int red = (texEmissiveColor >> 16) & 0xff;
					int green = (texEmissiveColor >> 8) & 0xff;
					int blue = (texEmissiveColor) & 0xff;

					int tRed = (texColor >> 16) & 0xff;
					int tGreen = (texColor >> 8) & 0xff;
					int tBlue = (texColor) & 0xff;

					int cRed = (clearColor >> 16) & 0xff;
					int cGreen = (clearColor >> 8) & 0xff;
					int cBlue = (clearColor) & 0xff;

					int R = 0, G = 0, B = 0;

					Vector3f skyColor = new Vector3f(cRed, cGreen, cBlue);
					Vector3f objectColor = new Vector3f(tRed, tGreen, tBlue);
					float visibility = (z) / FAR;
					Vector3f fog = new Vector3f().interpolate(objectColor, skyColor, visibility);

					R = (int) (fog.Xpos * 0.1f + red * 0.9f);
					G = (int) (fog.Ypos * 0.1f + green * 0.9f);
					B = (int) (fog.Zpos * 0.1f + blue * 0.9f);

					color = (R << 16) | (G << 8) | (B);
				}

				if (z < depthBuffer[index]) {
						pixel(x, y, screenPixels, color);
						depthBuffer[index] = z;
				}
				xTex += xTexStep;
				yTex += yTexStep;
				zCoords += zStep;
				normalX.Xpos += normalStep.Xpos;
				normalX.Ypos += normalStep.Ypos;
				normalX.Zpos += normalStep.Zpos;
			}

			leftXTex += left.xTexStep;
			leftYTex += left.yTexStep;
			rightXTex += right.xTexStep;
			rightYTex += right.yTexStep;
			leftZ += left.zStep;
			rightZ += right.zStep;
			leftNormal.Xpos += left.normalStep.Xpos;
			leftNormal.Ypos += left.normalStep.Ypos;
			leftNormal.Zpos += left.normalStep.Zpos;
			rightNormal.Xpos += right.normalStep.Xpos;
			rightNormal.Ypos += right.normalStep.Ypos;
			rightNormal.Zpos += right.normalStep.Zpos;
		}
	}

	public void renderTriangle(Vertex a, Vertex b, Vertex c, Bitmap texture, Bitmap normalMap, Bitmap displacementMap, List<Light> lights, Camera camera, int[] screenPixels) {
		Vertex aTranslate = new Vertex(new Vector3f(a.position), new Vector2f(a.texCoords), new Vector3f(a.position));
		Vertex bTranslate = new Vertex(new Vector3f(b.position), new Vector2f(b.texCoords), new Vector3f(b.position));
		Vertex cTranslate = new Vertex(new Vector3f(c.position), new Vector2f(c.texCoords), new Vector3f(c.position));

		// translate opposite to camera position(if the camera moves left the whole
		// world moves right to create an illusion that you move left)
		aTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		aTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		aTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		bTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		bTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		bTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		cTranslate.position.Xpos -= camera.cameraPosition.Xpos;
		cTranslate.position.Ypos -= camera.cameraPosition.Ypos;
		cTranslate.position.Zpos -= camera.cameraPosition.Zpos;

		// same as translation we do rotation relative to camera
		aTranslate.position = aTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		bTranslate.position = bTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));
		cTranslate.position = cTranslate.position.multiply(new Matrix4f().rotateY(camera.getAngleY()));

		aTranslate.position = aTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		bTranslate.position = bTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));
		cTranslate.position = cTranslate.position.multiply(new Matrix4f().rotateX(camera.getAngleX()));

		// if the triangles are too far or behind the NEAR then don't render them
		if (aTranslate.position.Zpos <= NEAR && bTranslate.position.Zpos <= NEAR && cTranslate.position.Zpos <= NEAR) {
			return;
		} else if (aTranslate.position.Zpos >= FAR && bTranslate.position.Zpos >= FAR && cTranslate.position.Zpos >= FAR) {
			return;
		}

		a.tangent.normalize();
		a.bitangent.normalize();
		a.normals.normalize();

		TBN = new Matrix4f().TBN(a.tangent, a.bitangent, a.normals);

		Triangle triangle = new Triangle(aTranslate, bTranslate, cTranslate);
		// geometric clipping in clockwise
		clipping.clipNearZPlane(triangle, NEAR);
		for (Triangle tri1 : clipping.triZnear) {
			Triangle triangle1 = new Triangle(tri1.a, tri1.b, tri1.c);
			clipping.clipFarZPlane(triangle1, FAR);
			for (Triangle tri2 : clipping.triZfar) {
				rasterizeTriangle(tri2.a, tri2.b, tri2.c, texture, normalMap, displacementMap, lights, camera, screenPixels);
			}
		}
		clipping.triZnear.clear();
		clipping.triZfar.clear();
	}

	private void rasterizeTriangle(Vertex a, Vertex b, Vertex c, Bitmap texture, Bitmap normalMap, Bitmap displacementMap, List<Light> lights, Camera camera, int[] screenPixels) {
		Vertex min = new Vertex(new Vector3f(a.position), new Vector2f(a.texCoords), new Vector3f(a.normals));
		Vertex mid = new Vertex(new Vector3f(b.position), new Vector2f(b.texCoords), new Vector3f(b.normals));
		Vertex max = new Vertex(new Vector3f(c.position), new Vector2f(c.texCoords), new Vector3f(c.normals));

		min.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));
		mid.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));
		max.position.Xpos *= 1 / (aspectRatio * Math.tan(Math.toRadians(fov / 2)));

		min.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));
		mid.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));
		max.position.Ypos *= 1 / Math.tan(Math.toRadians(fov / 2));

		min.position.Xpos /= min.position.Zpos;
		min.position.Ypos /= min.position.Zpos;
		min.texCoords.Xpos /= min.position.Zpos;
		min.texCoords.Ypos /= min.position.Zpos;

		mid.position.Xpos /= mid.position.Zpos;
		mid.position.Ypos /= mid.position.Zpos;
		mid.texCoords.Xpos /= mid.position.Zpos;
		mid.texCoords.Ypos /= mid.position.Zpos;

		max.position.Xpos /= max.position.Zpos;
		max.position.Ypos /= max.position.Zpos;
		max.texCoords.Xpos /= max.position.Zpos;
		max.texCoords.Ypos /= max.position.Zpos;

		min.position.shiftToScreenSpace(width, height);
		mid.position.shiftToScreenSpace(width, height);
		max.position.shiftToScreenSpace(width, height);

		// if the triangle is out of screen then don't render it
		if (min.position.Xpos >= width && mid.position.Xpos >= width && max.position.Xpos >= width) {
			return;
		} else if (min.position.Xpos <= 0 && mid.position.Xpos <= 0 && max.position.Xpos <= 0) {
			return;
		}

		if (min.position.Ypos >= height && mid.position.Ypos >= height && max.position.Ypos >= height) {
			return;
		} else if (min.position.Ypos <= 0 && mid.position.Ypos <= 0 && max.position.Ypos <= 0) {
			return;
		}

		min.normals = min.normals.multiply(TBN);
		mid.normals = mid.normals.multiply(TBN);
		max.normals = max.normals.multiply(TBN);

		List<Light> rotatedLights = new ArrayList<Light>();
		for (Light light : lights) {
			rotatedLights.add(new Light(light.position.multiply(TBN), light.constantvalue, light.linearValue, light.quadraticValue, light.R, light.G, light.B));
		}

		Camera rotatedCamera = new Camera(camera.cameraPosition.multiply(TBN), camera.getAngleX(), camera.getAngleY());

		min.normals.Xpos /= min.position.Zpos;
		min.normals.Ypos /= min.position.Zpos;
		min.normals.Zpos /= min.position.Zpos;

		mid.normals.Xpos /= mid.position.Zpos;
		mid.normals.Ypos /= mid.position.Zpos;
		mid.normals.Zpos /= mid.position.Zpos;

		max.normals.Xpos /= max.position.Zpos;
		max.normals.Ypos /= max.position.Zpos;
		max.normals.Zpos /= max.position.Zpos;

		min.position.Zpos = 1 / min.position.Zpos;
		mid.position.Zpos = 1 / mid.position.Zpos;
		max.position.Zpos = 1 / max.position.Zpos;

		Vector2f left = new Vector2f(mid.position.Xpos - min.position.Xpos, mid.position.Ypos - min.position.Ypos);
		Vector2f right = new Vector2f(max.position.Xpos - min.position.Xpos, max.position.Ypos - min.position.Ypos);

		float backfaceCulling = left.Xpos * right.Ypos - left.Ypos * right.Xpos;

		if (backfaceCulling < 0) {
			return;
		}

		Vertex[] sort = new Vertex[3];

		sort[0] = new Vertex(new Vector3f(min.position), new Vector2f(min.texCoords), new Vector3f(min.normals));
		sort[1] = new Vertex(new Vector3f(mid.position), new Vector2f(mid.texCoords), new Vector3f(mid.normals));
		sort[2] = new Vertex(new Vector3f(max.position), new Vector2f(max.texCoords), new Vector3f(max.normals));

		// sorting according to the y position for rasterization
		for (int i = 0; i < sort.length - 1; i++) {
			for (int j = i + 1; j < sort.length; j++) {
				if (sort[j].position.Ypos < sort[i].position.Ypos) {
					Vertex temp = sort[j];
					sort[j] = sort[i];
					sort[i] = temp;
				}
			}
		}

		min = sort[0];
		mid = sort[1];
		max = sort[2];

		float alpha = (mid.position.Ypos - min.position.Ypos) / (max.position.Ypos - min.position.Ypos);
		Vector3f interpolatedVector = new Vector3f().interpolate(min.position, max.position, alpha);
		Vector2f interpolatedTexCoords = new Vector2f().interpolate(min.texCoords, max.texCoords, alpha);
		Vector3f interpolatedNormal = new Vector3f().interpolate(min.normals, max.normals, alpha);
		Vertex interpolatedVertex = new Vertex(interpolatedVector, interpolatedTexCoords, interpolatedNormal);

		if (interpolatedVector.Xpos > mid.position.Xpos) {
			// flatBottom
			rasterize(new Edge(min, mid), new Edge(min, interpolatedVertex), texture, normalMap, displacementMap, rotatedLights, rotatedCamera, screenPixels);
			// flatTop
			rasterize(new Edge(mid, max), new Edge(interpolatedVertex, max), texture, normalMap, displacementMap, rotatedLights, rotatedCamera, screenPixels);
		} else {
			// flatBottom
			rasterize(new Edge(min, interpolatedVertex), new Edge(min, mid), texture, normalMap, displacementMap, rotatedLights, rotatedCamera, screenPixels);
			// flatTop
			rasterize(new Edge(interpolatedVertex, max), new Edge(mid, max), texture, normalMap, displacementMap, rotatedLights, rotatedCamera, screenPixels);
		}
	}

	private void rasterize(Edge left, Edge right, Bitmap texture, Bitmap normalMap, Bitmap displacementMap, List<Light> lights, Camera camera, int[] screenPixels) {
		int yStart = (int) Math.ceil(left.a.position.Ypos - 0.5f);
		int yEnd = (int) Math.ceil(right.b.position.Ypos - 0.5f);

		// raster clipping in Y - axis
		float yDistTop = 0;

		if (yStart < 0) {
			yDistTop = -yStart;
			yStart = 0;
		}

		if (yEnd >= height) {
			yEnd = height;
		}

		float LYPreStep = ((float) ((int) Math.ceil(left.a.position.Ypos - 0.5f)) + 0.5f - left.a.position.Ypos);
		float RYPreStep = ((float) ((int) Math.ceil(right.a.position.Ypos - 0.5f)) + 0.5f - right.a.position.Ypos);

		float leftXTex = (left.a.texCoords.Xpos + left.xTexStep * LYPreStep) + left.xTexStep * yDistTop;
		float leftYTex = (left.a.texCoords.Ypos + left.yTexStep * LYPreStep) + left.yTexStep * yDistTop;
		float rightXTex = (right.a.texCoords.Xpos + right.xTexStep * RYPreStep) + right.xTexStep * yDistTop;
		float rightYTex = (right.a.texCoords.Ypos + right.yTexStep * RYPreStep) + right.yTexStep * yDistTop;

		float leftZ = (left.a.position.Zpos + left.zStep * LYPreStep) + left.zStep * yDistTop;
		float rightZ = (right.a.position.Zpos + right.zStep * RYPreStep) + right.zStep * yDistTop;

		Vector3f leftNormal = new Vector3f(left.a.normals.Xpos + left.normalStep.Xpos * LYPreStep + left.normalStep.Xpos * yDistTop, left.a.normals.Ypos + left.normalStep.Ypos * LYPreStep + left.normalStep.Ypos * yDistTop, left.a.normals.Zpos + left.normalStep.Zpos * LYPreStep + left.normalStep.Zpos * yDistTop);
		Vector3f rightNormal = new Vector3f(right.a.normals.Xpos + right.normalStep.Xpos * RYPreStep + right.normalStep.Xpos * yDistTop, right.a.normals.Ypos + right.normalStep.Ypos * RYPreStep + right.normalStep.Ypos * yDistTop, right.a.normals.Zpos + right.normalStep.Zpos * RYPreStep + right.normalStep.Zpos * yDistTop);

		for (int y = yStart; y < yEnd; y++) {
			float xMin = left.xStep * ((float) (y) + 0.5f - left.a.position.Ypos) + left.a.position.Xpos;
			float xMax = right.xStep * ((float) (y) + 0.5f - right.a.position.Ypos) + right.a.position.Xpos;

			int xStart = (int) Math.ceil(xMin - 0.5f);
			int xEnd = (int) Math.ceil(xMax - 0.5f);

			int leftXClamp = 0;
			float XpreStep = ((float) (xStart) + 0.5f - xMin);

			// raster clipping in X - axis
			if (xStart < 0) {
				leftXClamp = -xStart;
				xStart = 0;
			}

			if (xEnd >= width) {
				xEnd = width;
			}

			float xDist = xMax - xMin;

			float xTexStep = (rightXTex - leftXTex) / xDist;
			float yTexStep = (rightYTex - leftYTex) / xDist;

			float xTex = leftXTex + xTexStep * XpreStep + xTexStep * leftXClamp;
			float yTex = leftYTex + yTexStep * XpreStep + yTexStep * leftXClamp;

			float zStep = (rightZ - leftZ) / xDist;
			float zCoords = leftZ + zStep * XpreStep + zStep * leftXClamp;

			Vector3f normalStep = new Vector3f((rightNormal.Xpos - leftNormal.Xpos) / xDist, (rightNormal.Ypos - leftNormal.Ypos) / xDist, (rightNormal.Zpos - leftNormal.Zpos) / xDist);
			Vector3f normalX = new Vector3f(leftNormal.Xpos + normalStep.Xpos * XpreStep + normalStep.Xpos * leftXClamp, leftNormal.Ypos + normalStep.Ypos * XpreStep + normalStep.Ypos * leftXClamp, leftNormal.Zpos + normalStep.Zpos * XpreStep + normalStep.Zpos * leftXClamp);

			for (int x = xStart; x < xEnd; x++) {
				float z = 1 / zCoords;
				Vector2f currentTexCoords = new Vector2f(xTex * z, yTex * z);

				int index = x + y * width;
				int texColor = 0xffff00ff;
				int normalMapColor = 0;
				int depthMapColor = 0;

				Vector3f viewDirection = new Vector3f(camera.cameraPosition.Xpos - normalX.Xpos * z, camera.cameraPosition.Ypos - normalX.Ypos * z, camera.cameraPosition.Zpos - normalX.Zpos * z);
				viewDirection.Ypos *= -1;
				viewDirection.normalize();
				depthMapColor = displacementMap.getColor(currentTexCoords);
				float depthHeight = (float) ((depthMapColor >> 16) & 0xff) / 255.0f;

				float minLayers = 1.0f;
				float maxLayers = 3.0f;
				float dot = new Vector3f().dot(new Vector3f(0, 0, 1), viewDirection);
				if (dot < 0) {
					dot = 0;
				}

				float numLayers = minLayers * dot + maxLayers * (1 - dot);

				float layerDepth = 1.0f / numLayers;
				float currentLayerDepth = 0.0f;

				Vector2f p = new Vector2f((viewDirection.Xpos / viewDirection.Zpos) * depthHeight * 0.05f, -(viewDirection.Ypos / viewDirection.Zpos) * depthHeight * 0.05f);
				Vector2f deltaTexCoords = new Vector2f(p.Xpos / numLayers, p.Ypos / numLayers);

				int currentDepthColor = texture.getColor(currentTexCoords);
				float currentDepthMapValue = (float) ((currentDepthColor >> 16) & 0xff) / 255.0f;

				while (currentLayerDepth < currentDepthMapValue) {
					currentTexCoords.Xpos -= deltaTexCoords.Xpos;
					currentTexCoords.Ypos -= deltaTexCoords.Ypos;
					currentDepthColor = texture.getColor(currentTexCoords);
					currentDepthMapValue = (float) ((currentDepthColor >> 16) & 0xff) / 255.0f;
					currentLayerDepth += layerDepth;
				}

				Vector2f prevTexCoords = new Vector2f(currentTexCoords.Xpos + deltaTexCoords.Xpos, currentTexCoords.Ypos + deltaTexCoords.Ypos);
				float afterDepth = currentDepthMapValue - currentLayerDepth;

				int beforeDepthColor = texture.getColor(prevTexCoords);
				float beforeDepthMapValue = (float) ((beforeDepthColor >> 16) & 0xff) / 255.0f;

				float beforeDepth = beforeDepthMapValue - currentLayerDepth + layerDepth;
				float weight = afterDepth / (afterDepth - beforeDepth);
				currentTexCoords = new Vector2f().interpolate(currentTexCoords, prevTexCoords, weight);

				texColor = texture.getColor(currentTexCoords);
				normalMapColor = normalMap.getColor(currentTexCoords);

				int tRed = (texColor >> 16) & 0xff;
				int tGreen = (texColor >> 8) & 0xff;
				int tBlue = (texColor) & 0xff;

				int cRed = (clearColor >> 16) & 0xff;
				int cGreen = (clearColor >> 8) & 0xff;
				int cBlue = (clearColor) & 0xff;

				int nRed = (normalMapColor >> 16) & 0xff;
				int nGreen = (normalMapColor >> 8) & 0xff;
				int nBlue = (normalMapColor) & 0xff;

				float normalRed = (float) nRed / 255;
				float normalGreen = (float) nGreen / 255;
				float normalBlue = (float) nBlue / 255;

				Vector3f normalWorld = new Vector3f((normalRed * 2.0f) - 1.0f, (normalGreen * 2.0f) - 1.0f, normalBlue * 1.2f);

				int R = 0, G = 0, B = 0;

				for (int i = 0; i < lights.size(); i++) {
					Vector3f pixelWorldPositionToLight = new Vector3f(lights.get(i).position.Xpos - normalX.Xpos * z, lights.get(i).position.Ypos - normalX.Ypos * z, lights.get(i).position.Zpos - normalX.Zpos * z);
					Vector3f normal = new Vector3f(normalWorld);
					float distance = pixelWorldPositionToLight.getMagnitude();
					normal.normalize();
					pixelWorldPositionToLight.normalize();
					float lightValue = new Vector3f().dot(pixelWorldPositionToLight, normal);

					if (lightValue < 0.1f) {
						lightValue = 0.1f;
					}
					if (lightValue > 1) {
						lightValue = 1;
					}

					float attenuation = 1 / (lights.get(i).constantvalue + distance * lights.get(i).linearValue + distance * distance * lights.get(i).quadraticValue);

					R += (int) ((tRed * lightValue * lights.get(i).R * lightValue * attenuation) / 255);
					G += (int) ((tGreen * lightValue * lights.get(i).G * lightValue * attenuation) / 255);
					B += (int) ((tBlue * lightValue * lights.get(i).B * lightValue * attenuation) / 255);
				}

				if (R >= 255) {
					R = 255;
				}

				if (G >= 255) {
					G = 255;
				}

				if (B >= 255) {
					B = 255;
				}

				Vector3f skyColor = new Vector3f(cRed, cGreen, cBlue);
				Vector3f objectColor = new Vector3f(R, G, B);
				float visibility = (z * 1.2f) / FAR;
				Vector3f fog = new Vector3f().interpolate(objectColor, skyColor, visibility + 0.1f);

				R = (int) fog.Xpos;
				G = (int) fog.Ypos;
				B = (int) fog.Zpos;

				// main color
				int color = (R << 16) | (G << 8) | (B);

				if (z < depthBuffer[index]) {
					if (texColor != 0xffff00ff) {
						pixel(x, y, screenPixels, color);
						depthBuffer[index] = z;
					}
				}

				xTex += xTexStep;
				yTex += yTexStep;
				zCoords += zStep;
				normalX.Xpos += normalStep.Xpos;
				normalX.Ypos += normalStep.Ypos;
				normalX.Zpos += normalStep.Zpos;
			}

			leftXTex += left.xTexStep;
			leftYTex += left.yTexStep;
			rightXTex += right.xTexStep;
			rightYTex += right.yTexStep;
			leftZ += left.zStep;
			rightZ += right.zStep;
			leftNormal.Xpos += left.normalStep.Xpos;
			leftNormal.Ypos += left.normalStep.Ypos;
			leftNormal.Zpos += left.normalStep.Zpos;
			rightNormal.Xpos += right.normalStep.Xpos;
			rightNormal.Ypos += right.normalStep.Ypos;
			rightNormal.Zpos += right.normalStep.Zpos;
		}
	}

}
