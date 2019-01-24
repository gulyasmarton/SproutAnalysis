package eu.bioimage.celltools.cluster3d.controller;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import eu.bioimage.celltools.cluster3d.view.StackWindowWithScrollEvent;
import eu.bioimage.celltools.general.imageprocessing.FloodFill3D;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.GaussianBlur;
import ij.process.AutoThresholder;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class Cluster3DSimpleThrController extends AbstractCluster3DController 
		 {
	private double teoriticMaxThr = Short.MAX_VALUE;
	private double teoriticMinThr = 0;

	private boolean isBlack;

	

	public String getTitle() {
		if (impOrg != null)
			return "C3D ST - " + impOrg.getTitle();
		return "Cluster 3D - Simple Threshold";
	}

	public void setActiveImage() {
		impOrg = WindowManager.getCurrentImage();
		impThreshold = null;
		floodfill = null;
		c3d = null;
	}

	public void setThresholdFilter() {
		if (impOrg == null) {
			IJ.noImage();
			return;
		}

		ImageStack st = impOrg.getStack();
		aktivE = new boolean[st.getSize()];
		thrs = new double[st.getSize()];
		cellRenderer.setAktivE(aktivE);
		varazs.getModel().clear();

		ImageStack stout = new ImageStack(impOrg.getWidth(), impOrg.getHeight());
		GaussianBlur bg = new GaussianBlur();

		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (int i = 1; i <= st.getSize(); i++) {
			ImageProcessor ip = st.getProcessor(i).duplicate();

			if (min > ip.getMin())
				min = ip.getMin();

			if (max < ip.getMax())
				max = ip.getMax();

			bg.blurGaussian(ip, 2, 2, 0.02);
			ip.setAutoThreshold(AutoThresholder.Method.Default, isBlack,
					ImageProcessor.RED_LUT);
			aktivE[i - 1] = true;
			thrs[i - 1] = isBlack ? ip.getMinThreshold() : ip.getMaxThreshold();
			varazs.set((int)thrs[i - 1]);
			stout.addSlice(ip);
		}
		fireSliderMinimumChanged((int) min);
		fireSliderMaximumChanged((int) max);

		if (impThreshold != null && impThreshold.getWindow() != null) {
			impThreshold.changes = false;
			impThreshold.close();
		}
		impThreshold = new ImagePlus("Threshold - " + impOrg.getTitle(), stout);
		StackWindowWithScrollEvent window = new StackWindowWithScrollEvent(
				impThreshold);
		window.getScrollbar().addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				updateThresholds();
			}
		});
		updateThresholds();
	}

	protected void updateThresholds() {
		if (impThreshold == null)
			return;
		int idx = impThreshold.getCurrentSlice() - 1;

		for (int i = 0; i < aktivE.length; i++) {
			if (!aktivE[i]) {

				Integer min = null;
				for (int v = i; v > 0; v--)
					if (aktivE[v]) {
						min = varazs.get(v);// thrs[v];
						break;
					}

				Integer max = null;
				for (int v = i; v < aktivE.length; v++)
					if (aktivE[v]) {
						max = varazs.get(v);// thrs[v];
						break;
					}
				if (max == null && min == null)
					break;
				if (max == null)
					max = min;
				if (min == null)
					min = max;
				varazs.set(i,(int)Math.round((min + max) / 2d));
			}
		}

		if (isBlack)
			impThreshold.getProcessor().setThreshold(varazs.get(idx),
					teoriticMaxThr, ImageProcessor.RED_LUT);
		else
			impThreshold.getProcessor().setThreshold(teoriticMinThr,
					varazs.get(idx), ImageProcessor.RED_LUT);
		
		impThreshold.updateAndDraw();
		fireSliderValueChanged((int) varazs.get(idx));
		fireSelectedThresholdChanged(idx);
	}

	@Override
	public void clusterDectecting() {
		if (impThreshold == null) {
			IJ.noImage();
			return;
		}

		ImageStack st = new ImageStack(impOrg.getWidth(), impOrg.getHeight());

		for (int i = 1; i <= impThreshold.getStack().getSize(); i++) {
			short[] a = (short[]) impThreshold.getStack().getPixels(i);
			byte[] b = new byte[a.length];
			int min = (int) (isBlack?varazs.get(i - 1):teoriticMinThr);
			int max = (int) (isBlack?teoriticMaxThr:varazs.get(i - 1));
			for (int j = 0; j < b.length; j++) {
				if (a[j] >=min && a[j]<=max)
					b[j] = (byte) 255;
			}
			st.addSlice(new ByteProcessor(impOrg.getWidth(),
					impOrg.getHeight(), b));
		}
		ImagePlus imp = new ImagePlus(impOrg.getTitle(), st);
		imp.setCalibration(impOrg.getCalibration());
		floodfill = new FloodFill3D(imp);
		GenericDialog gd = new GenericDialog("New Image");
		gd.addNumericField("Minimum size (vaxel): ", 100, 0);
		gd.showDialog();
		if (gd.wasCanceled())
			return;
		int min = (int) gd.getNextNumber();
		floodfill.setMinCellSize(min);
		ImagePlus img = floodfill.getC3D().getColorImage();
		c3d = floodfill.getC3D();
		img.show();

	}
	
	@Override
	public void listaSelectionChanged(int idx) {
		if (idx == -1 || impThreshold == null)
			return;
		impThreshold.setSlice(idx + 1);
		if (isBlack)
			impThreshold.getProcessor().setThreshold(varazs.get(idx),
					teoriticMaxThr, ImageProcessor.RED_LUT);
		else
			impThreshold.getProcessor().setThreshold(teoriticMinThr,
					varazs.get(idx), ImageProcessor.RED_LUT);
	}

	@Override
	public void backgroundIsBlack(boolean isBlack) {
		this.isBlack = isBlack;

	}

}
