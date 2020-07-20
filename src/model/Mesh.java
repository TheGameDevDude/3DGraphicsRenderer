package model;

import java.util.ArrayList;
import java.util.List;

import graphics.Triangle;

public class Mesh {
	public List<Triangle> triangles = new ArrayList<Triangle>();

	public Mesh(List<Triangle> triangles) {
		this.triangles = triangles;
	}
}
