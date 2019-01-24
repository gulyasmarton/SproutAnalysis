package eu.bioimage.celltools.general.plugins;

import java.io.File;
import java.nio.file.Paths;

import javax.swing.JOptionPane;

import eu.bioimage.celltools.general.datamanaging.Zipper;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

public class OpenZipPluginById implements PlugIn {

	@Override
	public void run(String arg) {
		String basePath = "/mnt/data/meas";

		GenericDialog gd = new GenericDialog("New Image");
		gd.addStringField("Exp ID: ", "");
		gd.addNumericField("Field: ", 1, 0);
		gd.addStringField("Other: ", "-bin4");
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		String expID = gd.getNextString();
		int fieldID = (int) gd.getNextNumber();
		String other = gd.getNextString();

		String id = "X" + (fieldID < 10 ? "0" + fieldID : fieldID);
		File p1 = Paths.get(basePath, "meso", expID, "analysis", expID + "-" + id + other + ".zip").toFile();
		File p2 = Paths.get(basePath, "motil", expID, "analysis", expID + "-" + id + other + ".zip").toFile();
		File p;
		if (p1.exists()) {
			p = p1;
		} else if (p2.exists()) {
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

		new ImagePlus(p.getName().replace(".zip", ""), Zipper.getImageStackFromZip(p.getAbsolutePath())).show();
	}

}
