package eu.bioimage.celltools.general.imageprocessing;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.bioimage.celltools.general.datamanaging.DataImporter;
import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.io.DirectoryChooser;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.plugin.PlugIn;

public class HyperStackGenerator implements PlugIn {
	public static void main(String[] args) {
		HyperStackGenerator generator = new HyperStackGenerator();
		generator.run("");
		if (true)
			return;

		String path = "D:/munka/Elod/Z211/jpegs";
		List<File> allFiles = DataImporter.getFile(path);

		double stepSize = 10;
		double ratio = 680d / 520d;
		int field = 7;
		String[] channels = new String[] { "green", "red" };

		int frame = generator.getMaxTime(field, allFiles);
		int zStack = generator.getMaxZ(field, allFiles) + 1;

		ImagePlus imp = generator.makeHyperStack(allFiles, field, ratio, stepSize,
				channels, frame, zStack);

		FileSaver saver = new FileSaver(imp);
		String f = field < 10 ? "X0" + field : "X" + field;
		saver.saveAsTiffStack(path + File.separator + f + ".tiff");
	}

	public int getMaxTime(int field, List<File> allFiles) {
		String f = field < 10 ? "X0" + field : "X" + field;
		int max = 0;
		for (File file : allFiles) {
			if (!file.getName().contains(f) || !file.getName().contains(".jpg"))
				continue;
			String[] frag = file.getName().split("_");
			int frame = Integer.parseInt(frag[2]);
			if (max < frame)
				max = frame;
		}
		return max;
	}

	public int getMaxZ(int field, List<File> allFiles) {
		String f = field < 10 ? "X0" + field : "X" + field;
		int max = 0;
		for (File file : allFiles) {
			if (!file.getName().contains(f) || !file.getName().contains(".jpg"))
				continue;
			String[] frag = file.getName().split("\\+");
			frag = frag[1].split("_");
			int frame = Integer.parseInt(frag[0]);
			if (max < frame)
				max = frame;
		}
		return max;
	}

	@Override
	public void run(String arg) {

		DirectoryChooser dc = new DirectoryChooser(
				"Select folder of frames! (jpegs)");

		if (dc.getDirectory() == null)
			return;
		File folder = new File(dc.getDirectory());
		File[] listOfFiles = folder.listFiles();
		List<File> allFiles = new ArrayList<File>(Arrays.asList(listOfFiles));

		GenericDialog gd = new GenericDialog("New Hyper Image");
		gd.addNumericField("number of field: ", 1, 0);
		gd.addNumericField("um/pixel ratio: ", 680d / 520d, 3);
		gd.addNumericField("size of step: ", 10, 2);
		gd.addStringField("name of channels (ch1,ch2...): ", "green,red");
		gd.showDialog();

		if (gd.wasCanceled())
			return;

		int field = (int) gd.getNextNumber();
		double ratio = gd.getNextNumber();
		double stepSize = gd.getNextNumber();
		String[] ch = gd.getNextString().split(",");
		for (int i = 0; i < ch.length; i++) {
			ch[i] = ch[i].trim();
		}

		int frame = getMaxTime(field, allFiles);
		int zStack = getMaxZ(field, allFiles) + 1;

		IJ.showMessage("frames: " + frame + "; z-stack: " + zStack);
		makeHyperStack(allFiles, field, ratio, stepSize, ch, frame, zStack)
				.show();
	}

	public ImagePlus makeHyperStack(List<File> allFiles, int field,
			double ratio, double stepSize, String[] channels, int frames,
			int zStack) {

		int w = 0;
		int h = 0;
		String id = "";
		for (File file : allFiles) {
			if (file.getName().contains(".jpg")) {
				ImagePlus imp = new ImagePlus(file.getPath());
				w = imp.getWidth();
				h = imp.getHeight();
				id = file.getName().split("-")[0];
				break;
			}
		}
		String f = field < 10 ? "X0" + field : "X" + field;
		ImageStack st = new ImageStack(w, h, channels.length * zStack * frames);
		int idx = 1;
		for (int t = 1; t <= frames; t++) {
			IJ.showProgress(t, frames);
			for (int z = 0; z < zStack; z++)
				for (int ch = 0; ch < channels.length; ch++) {
					String ts = t < 10 ? "00" + t : t < 100 ? "0" + t : "" + t;
					String pathf = ".*" + channels[ch] + "_" + f + "\\+" + z
							+ "_" + ts + ".*jpg";
					File file = DataImporter.filterFileByPattern(pathf,
							allFiles).get(0);
					ImagePlus imp = new ImagePlus(file.getPath());
					st.setProcessor(imp.getProcessor(), idx++);
				}
		}
		ImagePlus imp = new ImagePlus(id + "-" + f, st);

		imp.setDimensions(channels.length, zStack, frames);
		int mode = IJ.COLOR;

		imp = new CompositeImage(imp, mode);
		imp.setOpenAsHyperStack(true);
		Calibration cal = new Calibration();
		cal.setUnit("um");
		cal.pixelHeight = ratio;
		cal.pixelWidth = ratio;
		cal.pixelDepth = ratio * stepSize;
		imp.setCalibration(cal);

		return imp;
	}

}
