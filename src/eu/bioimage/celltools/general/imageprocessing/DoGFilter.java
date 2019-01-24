package eu.bioimage.celltools.general.imageprocessing;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import ij.plugin.filter.GaussianBlur;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class DoGFilter implements PlugIn {

	@Override
	public void run(String arg) {
		// TODO Auto-generated method stub
		ImagePlus imp = IJ.getImage();
		
		ImageStack st = imp.getStack();
		ImageStack st2 = new ImageStack(imp.getWidth(), imp.getHeight());
		
		GenericDialog dg = new GenericDialog("Parameters");
		
		dg.addNumericField("sigma1", 1, 2);
		dg.addNumericField("sigma2", 1.6, 2);
		
		dg.showDialog();
		if(dg.wasCanceled())return;
		
		double sigma1 = dg.getNextNumber();
		double sigma2 = dg.getNextNumber();
		
		for (int i = 0; i < st.getSize(); i++) {
			st2.addSlice(dog(st.getProcessor(i+1),sigma2,sigma1));
		}
		
		new ImagePlus("DoG",st2).show();
		
	}
	
	GaussianBlur blur = new GaussianBlur();

	public ImageProcessor dog(ImageProcessor ip, double sigma1,double sigma2) {
		ImageProcessor ip1 = ip.duplicate();
		ImageProcessor ip2 = ip.duplicate();
		blur.blurGaussian(ip1, sigma1);
		blur.blurGaussian(ip2, sigma2);

		byte[] pix1 = (byte[]) ip1.getPixels();
		byte[] pix2 = (byte[]) ip2.getPixels();

		float[] pix3 = new float[pix1.length];
		for (int i = 0; i < pix3.length; i++)
			pix3[i] = (( pix1[i]&0xFF) - (pix2[i]&0xFF));

		return (new FloatProcessor(ip.getWidth(), ip.getHeight(), pix3));
	}

}
