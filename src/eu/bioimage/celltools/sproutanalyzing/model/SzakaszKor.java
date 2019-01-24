package eu.bioimage.celltools.sproutanalyzing.model;

import ij.gui.PolygonRoi;

import java.awt.Point;
import java.util.Vector;

public class SzakaszKor {
	private Vector<SzakaszPoligon> poligonok;
	
	public Vector<SzakaszPoligon> getSzakaszPoligonok(){
		return poligonok;
	}
	
	private SzakaszKor(Object poligonok){
		this.poligonok = (Vector<SzakaszPoligon>)poligonok;
	}
	
	public SzakaszKor(Vector<PolygonRoi> polygons){
		poligonok = new Vector<SzakaszPoligon>(polygons.size());
		for (PolygonRoi roi : polygons) {
			poligonok.add(new SzakaszPoligon(roi));
		}
	}
	
	public SzakaszKor Egyesit(SzakaszKor kor){
		Vector<SzakaszPoligon> szum = new Vector<SzakaszPoligon>();
		for (SzakaszPoligon pl1 : poligonok) {	
			boolean vanParja = false;
			for (SzakaszPoligon pl2 : kor.getSzakaszPoligonok()) {
				if(pl1.Atfed(pl2)){
					vanParja = true;
					szum.add( pl1.Egyesit(pl2));
				}
			}
			if(!vanParja) szum.add(pl1);
		}
		
		for (SzakaszPoligon pl1 : kor.getSzakaszPoligonok()) {	
			boolean vanParja = false;
			for (SzakaszPoligon pl2 : poligonok) {
				if(pl1.Atfed(pl2)){
					vanParja = true;
					break;
				}
			}
			if(!vanParja) szum.add(pl1);
		}
		
		
		return new SzakaszKor(szum);
	}
}
