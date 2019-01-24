package eu.bioimage.celltools.general.imageprocessing;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.Roi;
import ij.plugin.PlugIn;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Yprojection implements PlugIn {

	@Override
	public void run(String arg) {
		// TODO Auto-generated method stub
		ImagePlus imp = WindowManager.getCurrentImage();

		if (imp == null) {
			IJ.noImage();
			return;
		}

		Roi r = imp.getRoi();

		if (r == null) {
			IJ.error("No roi");
			return;
		}

		if (r.getType() != Roi.RECTANGLE) {
			IJ.error("No Roi RECTANGLE");
			return;
		}
		ImageStack st = new ImageStack(1, r.getBounds().height);
		ImageStack stOrg = imp.getStack();
		for (int i = 0; i < imp.getStackSize(); i++) {
			st.addSlice(project(stOrg.getProcessor(i + 1), r));

		}

		imp = new ImagePlus(imp.getTitle()+"-proj", st);
		imp.show();
	}

	private ImageProcessor project(ImageProcessor ip, Roi r) {

		byte[] pix = (byte[]) ip.getPixels();
		byte[] result = new byte[r.getBounds().height];

		int roiW = r.getBounds().width;
		int w = ip.getWidth();

		int xk1 = r.getBounds().x;
		int yk1 = r.getBounds().y;
		int xk2 = xk1 + r.getBounds().width;
		int yk2 = yk1 + r.getBounds().height;

		int i=0;
		for (int y = yk1; y < yk2; y++) {
			byte[] row = new byte[roiW];
			int c = 0;
			for (int x = xk1; x < xk2; x++) {
				row[c++] = pix[x + y * w];
			}
			double m = 0;
			for (byte b : row) {
				m += (b & 0xff);
			}
			m=m/row.length;
			result[i++] = (byte) m;
		}

		return new ByteProcessor(1, result.length, result);
	}

}
