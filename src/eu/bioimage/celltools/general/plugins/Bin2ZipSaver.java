package eu.bioimage.celltools.general.plugins;

import java.util.ArrayList;
import java.util.List;

import eu.bioimage.celltools.general.datamanaging.IOtoolBox;
import eu.bioimage.celltools.general.datamanaging.Zipper;
import eu.bioimage.celltools.general.imageprocessing.BooleanImage;
import eu.bioimage.celltools.general.imageprocessing.ImageBinarizator;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.plugin.PlugIn;

public class Bin2ZipSaver implements PlugIn{

	@Override
	public void run(String arg) {
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null) {
			IJ.noImage();
			return;
		}

		String path = IOtoolBox.commonSaveDialog("Binary and Save image file", null, imp.getTitle(), "zip");
		if(path==null)
			return;
		
		List<BooleanImage> imgs = new ArrayList<BooleanImage>(imp.getStackSize());
		
		ImageStack st = imp.getStack();
		
		for (int i = 0; i < st.getSize(); i++)
			imgs.add(ImageBinarizator.makeBinary(st.getProcessor(i+1), true));
		Zipper.saveImageToZip(path, imgs);		
	}

}
