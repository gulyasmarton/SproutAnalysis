package eu.bioimage.celltools.sproutanalyzing.model;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class SPR implements Serializable {

	private static final long serialVersionUID = 5198876210153655743L;
	
	private int fromUm;
	private int distUm;
	private Rectangle oval;
	private ArrayList<SerializableRoi> masks;
	private double ratio;

	public SPR(int fromUm, int distUm, Rectangle oval,ArrayList<SerializableRoi> masks,double ratio) {
		super();
		this.fromUm = fromUm;
		this.distUm = distUm;
		this.oval = oval;
		this.masks = masks;
		this.ratio = ratio;
	}
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
	    if (ratio == 0) {
	    	ratio = 1d;
	    }
	}

	public int getFromUm() {
		return fromUm;
	}

	public int getDistUm() {
		return distUm;
	}

	public Rectangle getOval() {
		return oval;
	}

	public ArrayList<SerializableRoi> getMasks() {
		return masks;
	}

	public double getRatio() {
		return ratio;
	}

}
