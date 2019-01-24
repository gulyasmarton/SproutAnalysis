package eu.bioimage.celltools.sproutanalyzing.model;

import ij.gui.OvalRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.Serializable;

public class SerializableRoi implements Serializable {	
	private static final long serialVersionUID = 1220653912779269033L;
	private int type;
	private Rectangle rec;
	private Polygon pol;

	public SerializableRoi(Roi r) {
		this.type = r.getType();
		rec = r.getBounds();
		pol = r.getPolygon();
	}

	public Roi getRoi() {
		if (type == Roi.RECTANGLE) {
			return new Roi(rec);
		} else if (type == Roi.OVAL) {
			return new OvalRoi(rec.x, rec.y, rec.width, rec.height);
		} else if (type == Roi.POLYGON || type == Roi.FREEROI) {
			return new PolygonRoi(pol, type);
		} else {
			return null;
		}
	}

}
