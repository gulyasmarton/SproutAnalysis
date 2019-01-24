package eu.bioimage.celltools.sproutanalyzing.model;

import ij.gui.PolygonRoi;

import java.awt.Point;
import java.awt.Polygon;
import java.util.Vector;

public class SzakaszPoligon {
	private Vector<Point> pointok;
	
	public Vector<Point> getPontok(){
		return pointok;
	}
	
	public SzakaszPoligon(Vector<Point> pointok){
		this.pointok = pointok;
	}
	
	public SzakaszPoligon(Polygon pl){
		pointok = new Vector<Point>(pl.npoints); 
		for (int i = 0; i < pl.npoints; i++) {
			pointok.add(new Point(pl.xpoints[i], pl.ypoints[i]));
		}		
	}
	public SzakaszPoligon(PolygonRoi pr){
		this(pr.getPolygon());
	}
		
	public boolean Atfed(SzakaszPoligon szp){
		for (Point point1 : pointok) {
			for (Point point2 : szp.getPontok()) {
				if(point1.equals(point2)) return true;
			}
		}
		return false;
	}
	
	public SzakaszPoligon Egyesit(SzakaszPoligon p){
		Vector<Point> vp = new Vector<Point>(pointok);
		vp.addAll(p.getPontok());
		
		return new SzakaszPoligon(vp);				
	}
}
