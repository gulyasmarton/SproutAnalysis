package eu.bioimage.celltools.general.imageprocessing;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.bioimage.celltools.general.datamanaging.Statistic;
import eu.bioimage.celltools.nucleuscounter.controller.MosaicZviLoader;
import eu.bioimage.celltools.nucleuscounter.model.Parallel;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.plugin.filter.GaussianBlur;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import loci.plugins.in.ImagePlusReader;

public class Pearl3D {

	public static void main(String[] args) {
		String path = "/mnt/data/meas/meso/Z319/zeiss_red/Z319-red-dish4-ring3-z15-m6x8.zvi";

		ImagePlus imp2 = MosaicZviLoader.saveToZvi(path);
		// imp.show();
		/*
		 * File f = new
		 * File("/mnt/local/home_shared/marci/meas/meso/Z307/snaps/");
		 * 
		 * File[] ff = f.listFiles();
		 * 
		 * Arrays.sort(ff);
		 * 
		 * ImageStack st = null;
		 * 
		 * for (int i = 0; i < ff.length; i++) { if
		 * (!ff[i].getName().contains("_fluo")) continue; //
		 * System.out.println(ff[i].getName()); ImagePlus imp = new
		 * ImagePlus(ff[i].getAbsolutePath()); if (st == null) st = new
		 * ImageStack(imp.getWidth(), imp.getHeight());
		 * st.addSlice(imp.getProcessor()); }
		 */

		ImageStack st = imp2.getStack();
		int w = st.getWidth();
		int h = st.getHeight();
		int d = st.getSize();

		ImagePlus imp = new ImagePlus("stack", st);
		// imp.show();
		FileSaver saver = new FileSaver(imp);
		saver.saveAsTiffStack("/mnt/data/meas/meso/Z319/zeiss_red/Z319-red-dish4-ring3-z15-m6x8.tif");
		ImageProcessor ip = getFocus(st, 5);
		imp = new ImagePlus("focused", ip);
		// imp.show();
		saver = new FileSaver(imp);
		// saver.saveAsTiff("/mnt/local/home_shared/marci/demo/grid.tif");
		imp = new ImagePlus("th", makeStack(ip, w, h, d));
		// imp.show();
		saver = new FileSaver(imp);
		saver.saveAsTiff("/mnt/data/meas/meso/Z319/zeiss_red/Z319-red-dish4-ring3-z15-m6x8-proj.tif");
		System.out.println("kész");

	}

	private static ImageStack makeStack(ImageProcessor ip, int w, int h, int d) {
		float[] pixIn = (float[]) ip.getPixels();
		ImageStack st = new ImageStack(w, h);
		int bigW = w * h;
		//

		for (int z = 0; z < d; z++) {
			float[] pixOut = new float[w * h];
			for (int y = 0; y < h; y++)
				for (int x = 0; x < w; x++) {
					int xk = x + y * w;
					int yk = z;
					pixOut[x + y * w] = pixIn[xk + yk * bigW];
				}
			st.addSlice(new FloatProcessor(w, h, pixOut));
			System.out.println(z);
		}

		return st;
	}

	private static ImageProcessor getFocus(ImageStack st, int kernel) {
		int w = st.getWidth();
		int h = st.getHeight();
		int d = st.getSize();

		int bigW = w * h;
		int bigH = d;

		float[] pix = new float[bigW * bigH];

		GaussianBlur gauss = new GaussianBlur();

		final ImageStack st2 = convert2Gray8(st);
		st = null;
		Parallel p = new Parallel(st2.getSize()) {

			@Override
			public void coreProcess(int z) {
				// TODO Auto-generated method stub
				for (int y = 0; y < h; y++) {
					for (int x = 0; x < w; x++) {
						int xk = x + y * w;
						int yk = z;

						ImageProcessor ip = st2.getProcessor(z + 1);

						pix[xk + yk * bigW] = (float) AutofocusZ.getScore(x, y, ip, kernel);
					}
				}
			}
		};
		
		p.start();
/*
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				for (int z = 0; z < st.getSize(); z++) {
					int xk = x + y * w;
					int yk = z;

					ImageProcessor ip = st.getProcessor(z + 1);

					pix[xk + yk * bigW] = (float) AutofocusZ.getScore(x, y, ip, kernel);
				}
			}
			*System.out.println(y);
		}*/
		

		FloatProcessor ip = new FloatProcessor(bigW, bigH, pix);
		gauss.blur1Direction(ip, 2.0, 0.02, false, 0);

		return ip;
	}

	private static ImageStack convert2Gray8(ImageStack st) {
		// TODO Auto-generated method stub
		ImageStack st2 = new ImageStack(st.getWidth(), st.getHeight());
		for (int i = 0; i < st.getSize(); i++) {
			st2.addSlice(st.getProcessor(i + 1).convertToByte(false));
		}
		return st2;
	}

	private static float[] getGauss(float[] input, float[][] kernel) {
		float[] output;

		return null;
	}

	final static private void convolveLine(final float[] input, final float[] pixels, final float[][] kernel,
			final int readFrom, final int readTo, final int writeFrom, final int writeTo, final int point0,
			final int pointInc) {
		final int length = input.length;
		final float first = input[0]; // out-of-edge pixels are replaced by
										// nearest edge pixels
		final float last = input[length - 1];
		final float[] kern = kernel[0]; // the kernel itself
		final float kern0 = kern[0];
		final float[] kernSum = kernel[1]; // the running sum over the kernel
		final int kRadius = kern.length;
		final int firstPart = kRadius < length ? kRadius : length;
		int p = point0 + writeFrom * pointInc;
		int i = writeFrom;
		for (; i < firstPart; i++, p += pointInc) { // while the sum would
													// include pixels < 0
			float result = input[i] * kern0;
			result += kernSum[i] * first;
			if (i + kRadius > length)
				result += kernSum[length - i - 1] * last;
			for (int k = 1; k < kRadius; k++) {
				float v = 0;
				if (i - k >= 0)
					v += input[i - k];
				if (i + k < length)
					v += input[i + k];
				result += kern[k] * v;
			}
			pixels[p] = result;
		}
		final int iEndInside = length - kRadius < writeTo ? length - kRadius : writeTo;
		for (; i < iEndInside; i++, p += pointInc) { // while only pixels within
														// the line are be
														// addressed (the easy
														// case)
			float result = input[i] * kern0;
			for (int k = 1; k < kRadius; k++)
				result += kern[k] * (input[i - k] + input[i + k]);
			pixels[p] = result;
		}
		for (; i < writeTo; i++, p += pointInc) { // while the sum would include
													// pixels >= length
			float result = input[i] * kern0;
			if (i < kRadius)
				result += kernSum[i] * first;
			if (i + kRadius >= length)
				result += kernSum[length - i - 1] * last;
			for (int k = 1; k < kRadius; k++) {
				float v = 0;
				if (i - k >= 0)
					v += input[i - k];
				if (i + k < length)
					v += input[i + k];
				result += kern[k] * v;
			}
			pixels[p] = result;
		}
	}
}
