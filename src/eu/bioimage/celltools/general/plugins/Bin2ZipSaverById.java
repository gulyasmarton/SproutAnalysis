package eu.bioimage.celltools.general.plugins;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import eu.bioimage.celltools.general.datamanaging.Zipper;
import eu.bioimage.celltools.general.imageprocessing.BooleanImage;
import eu.bioimage.celltools.general.imageprocessing.ImageBinarizator;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

public class Bin2ZipSaverById implements PlugIn {
	String basePath = "/mnt/data/meas";

	@Override
	public void run(String arg) {
		ImagePlus imp = WindowManager.getCurrentImage();
		if (imp == null) {
			IJ.noImage();
			return;
		}

		String title = imp.getTitle();

		String[] sub = title.split("-");

		GenericDialog dialog = new GenericDialog("Save");
		dialog.addStringField("ID", sub.length == 2 ? sub[0] : "S");
		dialog.addStringField("Field", sub.length == 2 ? sub[1] : "X");
		dialog.addStringField("Post Tag", "bin4");
		dialog.showDialog();
		if (dialog.wasCanceled())
			return;

		String expID = dialog.getNextString();
		String fieldID = dialog.getNextString();
		String postTag = dialog.getNextString();

		File p1 = Paths.get(basePath, "meso", expID).toFile();
		File p2 = Paths.get(basePath, "motil", expID).toFile();
		File p = null;
		if (p1.exists() && p1.isDirectory()) {
			p = p1;
		} else if (p2.exists() && p2.isDirectory()) {
			p = p2;
		} else {
			int dialogResult = JOptionPane.showConfirmDialog(null,
					"I can't find the '" + expID + "'. Would you like to set another?", "Warning",
					JOptionPane.YES_NO_OPTION);
			if (dialogResult == JOptionPane.YES_OPTION) {
				run(arg);
			}
			return;
		}
		p = Paths.get(p.getAbsolutePath(), "analysis").toFile();		
		p.mkdirs();
		
		String path = Paths.get(p.getAbsolutePath(), expID + "-" + fieldID + "-" + postTag + ".zip")
				.toString();
		if (path == null)
			return;

		List<BooleanImage> imgs = new ArrayList<BooleanImage>(imp.getStackSize());

		ImageStack st = imp.getStack();

		for (int i = 0; i < st.getSize(); i++)
			imgs.add(ImageBinarizator.makeBinary(st.getProcessor(i + 1), true));
		Zipper.saveImageToZip(path, imgs);
		IJ.showMessage("Ready");
	}

}
