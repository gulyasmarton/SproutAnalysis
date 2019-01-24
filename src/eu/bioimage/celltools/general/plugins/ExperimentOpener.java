package eu.bioimage.celltools.general.plugins;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import eu.bioimage.celltools.general.datamanaging.DataImporter;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.VirtualStack;
import ij.gui.GenericDialog;
import ij.io.Opener;
import ij.plugin.FileInfoVirtualStack;
import ij.plugin.FolderOpener;
import ij.plugin.PlugIn;

public class ExperimentOpener implements PlugIn {
	static String basePath = "/mnt/data/meas";

	public static List<File> getByID(String expID, int fieldID, String other) {
		List<File> files = new ArrayList<>();
		File p1 = Paths.get(basePath, "meso", expID, "jpegs").toFile();
		File p2 = Paths.get(basePath, "motil", expID, "jpegs").toFile();
		File p;
		if (p1.exists() && p1.isDirectory()) {
			p = p1;
		} else if (p2.exists() && p2.isDirectory()) {
			p = p2;
		} else {
			return files;
		}
		files = DataImporter.getFile(p.toString());
		String id = "X" + (fieldID < 10 ? "0" + fieldID : fieldID);
		files = DataImporter.filterFileByPattern(".*" + id + ".*\\.jpg", files);
		if (!other.equals(""))
			files = DataImporter.filterFileByPattern(".*" + other + ".*\\.jpg", files);
		return files;
	}

	@Override
	public void run(String arg) {

		GenericDialog gd = new GenericDialog("New Image");
		gd.addStringField("Exp ID: ", "");
		gd.addNumericField("Field: ", 1, 0);
		gd.addStringField("Other: ", "");
		gd.addCheckbox("Virtual stack", false);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		String expID = gd.getNextString();
		int fieldID = (int) gd.getNextNumber();
		String other = gd.getNextString();
		boolean virtual = gd.getNextBoolean();

		File p1 = Paths.get(basePath, "meso", expID, "jpegs").toFile();
		File p2 = Paths.get(basePath, "motil", expID, "jpegs").toFile();
		File p;
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
		List<File> files = DataImporter.getFile(p.toString());
		String id = "X" + (fieldID < 10 ? "0" + fieldID : fieldID);
		files = DataImporter.filterFileByPattern(".*" + id + ".*\\.jpg", files);
		if (!other.equals(""))
			files = DataImporter.filterFileByPattern(".*" + other + ".*\\.jpg", files);

		ImageStack st = null;
		int idx = 0;

		if (files.isEmpty()) {
			int dialogResult = JOptionPane.showConfirmDialog(null,
					"I can't find the '" + id + " field'. Would you like to set another?", "Warning",
					JOptionPane.YES_NO_OPTION);
			if (dialogResult == JOptionPane.YES_OPTION) {
				run(arg);
			}
			return;
		}

		for (File file : files) {
			if (virtual) {
				if (st == null) {
					ImagePlus imp = new ImagePlus(file.getAbsolutePath());
					st = new VirtualStack(imp.getWidth(), imp.getHeight(), imp.getProcessor().getColorModel(),
							file.getParent());
					((VirtualStack) st).setBitDepth(imp.getBitDepth());
					// st = new VirtualStack(imp.getWidth(),
					// imp.getHeight(),files.size());
					// st = new FileInfoVirtualStack();
				}
				((VirtualStack) st).addSlice(file.getName());
			} else {

				ImagePlus imp = new ImagePlus(file.getAbsolutePath());

				if (st == null)
					st = new ImageStack(imp.getWidth(), imp.getHeight());

				st.addSlice(file.getName().replace(".jpg", ""), imp.getProcessor().convertToByte(true));
				IJ.showProgress(idx++, files.size());
				IJ.showStatus(idx + "/" + files.size());
			}
		}
		if (!other.equals(""))
			other = "-" + other;
		new ImagePlus(expID + "-" + id + other, st).show();
	}

}
