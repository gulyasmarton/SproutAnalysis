package eu.bioimage.celltools.general.imageprocessing;

import ij.process.AutoThresholder;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

public class ImageBinarizator {

	public static BooleanImage makeBinary(ImageProcessor ip, boolean darkBackground) {
		if (!(ip instanceof ByteProcessor))
			ip = ip.convertToByte(true);

		ip.setAutoThreshold(AutoThresholder.Method.Default, darkBackground);
		ip.autoThreshold();

		boolean[] p = new boolean[ip.getWidth() * ip.getHeight()];

		byte[] pix = (byte[]) ip.getPixels();
		for (int i = 0; i < pix.length; i++)
			if (pix[i] != 0)
				p[i] = true;

		return new BooleanImage(ip.getWidth(), ip.getHeight(), p);

	}

	public static BooleanImage makeBinary(ImageProcessor ip, Double min, Double max) {
		boolean[] p = new boolean[ip.getWidth() * ip.getHeight()];
		if (ip.isBinary()) {
			byte[] pix = (byte[]) ip.getPixels();
			for (int i = 0; i < pix.length; i++) {
				if (pix[i] != 0)
					p[i] = true;
			}
			return new BooleanImage(ip.getWidth(), ip.getHeight(), p);
		}
		if (min != null && max != null)
			ip.setThreshold(min, max, ImageProcessor.RED_LUT);
		else
			ip.setAutoThreshold(AutoThresholder.Method.Default, true, ImageProcessor.RED_LUT);

		min = ip.getMinThreshold();
		max = ip.getMaxThreshold();

		if (ip instanceof ByteProcessor) {
			byte[] pix = (byte[]) ip.getPixels();
			for (int i = 0; i < pix.length; i++) {
				if ((pix[i] & 0xff) >= min && (pix[i] & 0xff) <= max)
					p[i] = true;
			}
		} else if (ip instanceof ShortProcessor) {
			short[] pix = (short[]) ip.getPixels();
			for (int i = 0; i < pix.length; i++) {
				if (pix[i] >= min && pix[i] <= max)
					p[i] = true;
			}
		} else if (ip instanceof FloatProcessor) {
			float[] pix = (float[]) ip.getPixels();
			for (int i = 0; i < pix.length; i++) {
				if (pix[i] >= min && pix[i] <= max)
					p[i] = true;
			}
		} else {
			ip = ip.convertToByte(true);
			byte[] pix = (byte[]) ip.getPixels();
			for (int i = 0; i < pix.length; i++) {
				if ((pix[i] & 0xff) >= min && (pix[i] & 0xff) <= max)
					p[i] = true;
			}
		}

		return new BooleanImage(ip.getWidth(), ip.getHeight(), p);
	}
}
