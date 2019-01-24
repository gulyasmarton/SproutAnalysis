package eu.bioimage.celltools.general.imageprocessing;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.process.FloatProcessor;

public class StackToImages {
	public static void main(String[] args) {
		String path = "D:/munka/Elod/M57/a3-b9-red-features20001.tif";
		
		ImagePlus imp  = new ImagePlus(path);
		
		
		ImageStack st = imp.getStack();
		ImageStack st2 = new ImageStack(imp.getWidth(), imp.getHeight());
		for (int i = 0; i < st.getSize(); i++) {
			FloatProcessor ip = (FloatProcessor) st.getProcessor(i+1);
			
			imp = new ImagePlus("", ip);
			imp.show();
			IJ.run("Enhance Contrast", "saturated=0.35");
			IJ.run("8-bit");
			IJ.run("Enhance Contrast", "saturated=0.35");
			IJ.run("Apply LUT");
			
			st2.addSlice(st.getShortSliceLabel(i+1), imp.getProcessor());
			imp.changes = false;
			imp.close();
			System.out.println(st.getShortSliceLabel(i+1));	
		}
		FileSaver saver = new FileSaver(new ImagePlus("", st2));
		saver.saveAsTiffStack(path.replace(".tif", "-scale.tif"));
		System.out.println("finish");
	}
}
