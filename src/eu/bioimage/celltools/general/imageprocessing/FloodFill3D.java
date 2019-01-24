package eu.bioimage.celltools.general.imageprocessing;


import java.util.HashSet;
import java.util.Vector;

import eljarasok.Cell;
import eljarasok.HashStack;
import eljarasok.Point3D;
import eu.bioimage.celltools.cluster3d.model.C3D;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.process.ImageProcessor;


public class FloodFill3D {
	public static final int C3D_VERSION = 2;
	boolean[] mask;
	private ImagePlus imp;	
	private int xSize, ySize, zSize;
	private C3D c3d;
	private int minCellSize = 0;

	
		
	public FloodFill3D(ImagePlus imp){
		 this.imp=imp;
		 
		 ImageStack st = imp.getStack();
		 int min = (int)imp.getProcessor().getMinThreshold();
		 int max = (int)imp.getProcessor().getMaxThreshold();
		 
		 xSize = st.getWidth();
		 ySize = st.getHeight();
		 zSize = st.getSize();
		
		 
		 mask = new boolean[xSize*ySize*zSize];
		 byte[] stack;
		 if(imp.getProcessor().getMinThreshold() == ImageProcessor.NO_THRESHOLD){
			 for(int z = 0; z < zSize; z++) {
				stack = (byte[])st.getPixels(z+1);
				for(int y=0; y<ySize;y++)
					for(int x=0; x<xSize;x++){
						if(stack[x+y*xSize]==(byte)255)
							mask[x+xSize*(y+ySize*z)] = true;
						else
							mask[x+xSize*(y+ySize*z)] = false;
					}
			}
		 }else{
			 for(int z = 0; z < zSize; z++) {
					stack = (byte[])st.getPixels(z+1);
					for(int y=0; y<ySize;y++)
						for(int x=0; x<xSize;x++){
							if((stack[x+y*xSize]&0xff) > min && (stack[x+y*xSize]&0xff) < max)
								mask[x+xSize*(y+ySize*z)] = true;
							else
								mask[x+xSize*(y+ySize*z)] = false;
						}
				}
		 }		
	 }
	
	public C3D getC3D(){
		 if(c3d==null)
			 c3d = new C3D(imp.getTitle(), getCells(), xSize, ySize, zSize, imp.getCalibration());
		 return c3d;
	}
			
	private Vector<Cell> getCells(){
		
		Vector<Cell> cells = new Vector<Cell>();
		int c=1;
		for (int z = 0; z < zSize; z++) 
			for (int y = 0; y < ySize; y++) 
				for (int x = 0; x < xSize; x++) {
					if(getPixel(x,y,z)){
						Cell cell = floodFill(x,y,z);
						if(cell.getPoints().size()>minCellSize) cells.add(cell);
						IJ.showStatus("cell: "+c++);							
					}				
				}		
		return cells;
	}
	 
	 private Cell floodFill(int x, int y, int z){
		 if(!getPixel(x, y, z))return null;
		 HashStack<Point3D> st = new HashStack<Point3D>();
		 HashSet<Point3D> points = new HashSet<Point3D>();
		 st.push(new Point3D(x, y, z));
		 while (!st.empty()) {
			Point3D pix = st.pop();		
			points.add(pix);
			removePixel(pix);			
			for(int xk=-1;xk<=1;xk++)
				for(int yk=-1;yk<=1;yk++)
					for(int zk=-1;zk<=1;zk++){
						if(!(xk==0 && yk==0 && zk==0) && getPixel(pix.getShiftedPoint(xk, yk, zk))){
							st.push(pix.getShiftedPoint(xk, yk, zk));
						}
					}
		}
		 return new Cell(points);
	 }
	 
	 private boolean getPixel( Point3D p){
		 return getPixel(p.x, p.y, p.z);
	 }
	 private boolean getPixel(int x, int y, int z){
		 if(x<0 || y<0 || z<0 || x>=xSize || y>=ySize || z>=zSize) return false;
		 return mask[x+xSize*(y+ySize*z)];
	 }
	 
	 private void removePixel(Point3D p){
		 removePixel(p.x, p.y, p.z);
	 }

	private void removePixel(int x, int y, int z) {		
		 mask[x+xSize*(y+ySize*z)] = false;
	}

	public int getMinCellSize() {
		return minCellSize;
	}

	public void setMinCellSize(int minCellSize) {
		this.minCellSize = minCellSize;
	}


}
