package eu.bioimage.celltools.sproutanalyzing.model;

import ij.gui.PolygonRoi;

import java.util.Vector;

public class SzakaszZstack {
	private Vector<SzakaszKor> korok;
	
	public SzakaszZstack(Vector<Vector<PolygonRoi>> polygons){
		korok = new Vector<SzakaszKor>(polygons.size());
		for (Vector<PolygonRoi> vector : polygons) {
			korok.add(new SzakaszKor(vector));
		}
	}
	
	public Vector<SzakaszKor> getKorok() {
		return korok;
	}
	
}
