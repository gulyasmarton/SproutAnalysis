package eu.bioimage.celltools.cluster3d.controller;

import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.plugin.filter.GaussianBlur;
import ij.process.AutoThresholder;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;


import eu.bioimage.celltools.cluster3d.view.StackWindowWithScrollEvent;
import eu.bioimage.celltools.general.imageprocessing.FloodFill3D;
import eu.bioimage.celltools.general.imageprocessing.VarianceFilter;

public class Cluster3DVarianceController extends AbstractCluster3DController {

	private ImagePlus impVariance;
	
	private final static int VARIANCE_KERNEL_SIZE = 7;
	

	private void VarianceFilter() {
		if (impOrg == null)
			return;
		VarianceFilter vf = new VarianceFilter(VARIANCE_KERNEL_SIZE, impOrg);
		impVariance = vf.getImagePlus();
		impVariance.setCalibration(impOrg.getCalibration());
	}

	@Override
	public String getTitle() {
		if (impOrg != null)
			return "C3D VT - " + impOrg.getTitle();
		return "Cluster 3D - Variance";
	}

	@Override
	public void setActiveImage() {
		impOrg = WindowManager.getCurrentImage();
		impThreshold = null;
		impVariance = null;
		floodfill = null;
		c3d = null;
	}

	@Override
	public void setThresholdFilter() {
		if (impVariance == null)
			VarianceFilter();

		ImageStack st = impVariance.getStack();
		ImageStack stout = new ImageStack(impVariance.getWidth(),
				impVariance.getHeight());
		GaussianBlur bg = new GaussianBlur();

		aktivE = new boolean[st.getSize()];
		thrs = new double[st.getSize()];
		varazs.getModel().clear();
		for (int i = 1; i <= st.getSize(); i++) {
			FloatProcessor fp = (FloatProcessor) st.getProcessor(i).duplicate();

			bg.blurGaussian(fp, 2, 2, 0.02);
			ByteProcessor bp = fp.convertToByteProcessor();
			bp.setAutoThreshold(AutoThresholder.Method.Default, true,
					ImageProcessor.RED_LUT);

			aktivE[i - 1] = true;
			thrs[i - 1] = bp.getMinThreshold();

			varazs.set((int) bp.getMinThreshold());

			stout.addSlice(bp);
		}

		fireSliderMinimumChanged((int) 0);
		fireSliderMaximumChanged((int) 255);

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
				varazs.set(i, Math.round((min + max) / 2));
			}
		}

		impThreshold.getProcessor().setThreshold(varazs.get(idx), 255,
				ImageProcessor.RED_LUT);
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
			byte[] a = (byte[]) impThreshold.getStack().getPixels(i);
			byte[] b = new byte[a.length];
			int t = (int) varazs.get(i - 1);
			for (int j = 0; j < b.length; j++) {
				if ((a[j] & 0xff) >= t)
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

		impThreshold.getProcessor().setThreshold(varazs.get(idx), 255,
				ImageProcessor.RED_LUT);

	}

	
	@Override
	public void backgroundIsBlack(boolean isBlack) {
		
	}


}
