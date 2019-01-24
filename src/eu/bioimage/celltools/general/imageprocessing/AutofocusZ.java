package eu.bioimage.celltools.general.imageprocessing;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eu.bioimage.celltools.general.datamanaging.Statistic;
import eu.bioimage.celltools.nucleuscounter.model.Parallel;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class AutofocusZ {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ImagePlus imp1 = new ImagePlus(
				"/mnt/local/home_shared/marci/meas/meso/Z307/snaps/Z307_dish1_ring3_d12_z6350_fluo.jpg");
		ImagePlus imp2 = new ImagePlus(
				"/mnt/local/home_shared/marci/meas/meso/Z307/snaps/Z307_dish1_ring3_d12_z6350_fluo.jpg");

		File f = new File("/mnt/local/home_shared/marci/meas/meso/Z307/snaps/");

		File[] ff = f.listFiles();

		Arrays.sort(ff);

		
		
		ImagePlus imp3 = new ImagePlus(
				"/mnt/data/meas/meso/Z319/zeiss_red/Z319-red-dish4-ring3-z15-m6x8.tif");
		ImageStack st = null;
		st = imp3.getStack();
/*
		for (int i = 0; i < ff.length; i++) {
			if (!ff[i].getName().contains("_fluo"))
				continue;
			System.out.println(ff[i].getName());
			ImagePlus imp = new ImagePlus(ff[i].getAbsolutePath());
			if (st == null)
				st = new ImageStack(imp.getWidth(), imp.getHeight());
			st.addSlice(imp.getProcessor());
		}*/

		statArr = new int[st.getSize()];

		ImagePlus imp = new ImagePlus("stack", st);
		//imp.show();
		imp = new ImagePlus("focused", getFocus(st, 10));
		/*imp.show();*/

		System.out.println("stat");
		for (int i : statArr) {
			System.out.println(i);
		}
		
		FileSaver saver = new FileSaver(imp);
		saver.saveAsTiff("/mnt/data/meas/meso/Z319/zeiss_red/Z319-red-dish4-ring3-z15-m6x8-proj2.tif");
		System.out.println("kész");
	}

	private static int[] statArr;

	public static ByteProcessor getFocus(ImageStack st, int kernel) {
		int w = st.getWidth();
		int h = st.getHeight();
		byte[] pix = new byte[w * h];
/*
		Parallel p = new Parallel(w * h) {
			@Override
			public void coreProcess(int i) {
				// TODO Auto-generated method stub

			}
		};*/
		
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				double max = Double.MIN_VALUE;
				int idx = 0;
				for (int z = 0; z < st.getSize(); z++) {
					double v = getScore(x, y, st.getProcessor(z + 1), kernel);
					if (v > max) {
						max = v;
						idx = z;
					}
				}
				statArr[idx]++;
				pix[x + y * w] = ((byte[]) st.getPixels(idx + 1))[x + y * w];
			}
			System.out.println(y);
		}
		return new ByteProcessor(w, h, pix);
	}

	public static double getScore(int xk, int yk, ImageProcessor ip, int kernel) {
		int size = kernel / 2;
		int s = size + 1;
		List<Double> arr = new ArrayList<>(s * s);
		// double[] arr = new double[s * s];
		int w = ip.getWidth();
		int h = ip.getHeight();
		// int c = 0;
		byte[] pix = (byte[]) ip.getPixels();
		for (int y = yk - size; y < yk + size; y++)
			for (int x = xk - size; x < xk + size; x++) {
				if (!isValid(x, y,w,h))
					continue;
				// arr[c++] = pix[x+y*w];
				arr.add((double) (pix[x + y * w] & 0xff));
			}

		return Statistic.sd(arr);
	}

	private static boolean isValid(int x, int y, int w, int h) {
		if(x<0 || y<0)
		return false;
		if(x>=w || y>=h)
			return false;
		return true;
	}
}
