package eu.bioimage.celltools.general.imageprocessing;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.Roi;
import ij.plugin.PlugIn;

public class MorphoOpen implements PlugIn {

	@Override
	public void run(String arg) {
		GenericDialog gd = new GenericDialog("MorphoOpen");
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null) {
			IJ.noImage();
			return;
		}

		gd.addNumericField("iter: ", 1, 0);
		gd.showDialog();
		if (gd.wasCanceled())
			return;

		int iter = (int) gd.getNextNumber();

		for (int i = 0; i < iter; i++)
			IJ.run("Dilate", "stack");
		for (int i = 0; i < iter; i++)
			IJ.run("Erode", "stack");

		Roi r = new Roi(iter, iter, imp.getWidth() - iter*2 - 1, imp.getHeight() - iter*2 - 1);
		imp.setRoi(r);
		
		IJ.run("Make Inverse");
		IJ.run("Fill", "stack");
		

	}

}
