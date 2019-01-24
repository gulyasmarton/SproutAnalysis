package eu.bioimage.celltools.general.imageprocessing;

import java.awt.Point;
import java.util.Stack;
import java.util.Vector;

import ij.process.ByteProcessor;

public class FloodFill2D {
	boolean[] pix;
	int width;
	int height;
	public Vector<Vector<Point>> reszecskek;
	
	public FloodFill2D(ByteProcessor bp){
		width = bp.getWidth();
		height = bp.getHeight();
		byte[] inpix = (byte[])bp.getPixels();
		pix= new boolean[inpix.length];
		for (int i = 0; i < inpix.length; i++) {
			if(inpix[i]!=0) pix[i] = true;
		}
	}
	
	public int getSize(){
		if(reszecskek==null) findparticle();
		return reszecskek.size();
	}
	
	private void findparticle(){
		reszecskek = new Vector<Vector<Point>>();		
		
		for(int y=0; y<height;y++)
			for(int x=0; x<width;x++)
				if(pix[x+y*width])
					reszecskek.add(findparticle(x,y));
		
	}

	private Vector<Point> findparticle(int x, int y) {
		// TODO Auto-generated method stub
		Point p = new Point(x, y);
		Vector<Point> back = new Vector<Point>();
		Stack<Point> st = new Stack<Point>();
		st.push(p);
		 while (!st.empty()) {
			 p = st.pop();
			 back.add(p);
			 pix[p.x+p.y*width]=false;
			 for(int xk=-1;xk<=1;xk++)
					for(int yk=-1;yk<=1;yk++)
						if(pixCheck(p.x+xk,p.y+yk))
							st.push(new Point(p.x+xk,p.y+yk));
		 }
		
		return back;
	}

	private boolean pixCheck(int x, int y) {
		// TODO Auto-generated method stub
		if(x<0 || y<0 || x>=width || y>=height) return false;		
		return pix[x+y*width];
	}	
}
