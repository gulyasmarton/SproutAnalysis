package eu.bioimage.celltools.general.imageprocessing;

import java.util.Arrays;
import java.util.List;

import ij.process.ByteProcessor;

public class BooleanImage implements Cloneable {
	int width;
	int height;
	boolean[] pixels;

	public BooleanImage(int width, int height, boolean[] pixels) {
		super();
		this.width = width;
		this.height = height;
		this.pixels = pixels;
	}

	public BooleanImage(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new boolean[width * height];
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public boolean[] getPixels() {
		return pixels;
	}

	public ByteProcessor getByteProcessor() {
		byte[] pix = new byte[width * height];
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i])
				pix[i] = (byte) 255;
		}
		return new ByteProcessor(width, height, pix);
	}

	public void add(AbstractBlob blob) {
		int xk = blob.getBound().x;
		int yk = blob.getBound().y;
		int w = blob.getBound().width;
		int h = blob.getBound().height;
		boolean[] mask = blob.getMask();
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++)
				if (mask[x + y * w])
					pixels[x + xk + (y + yk) * width] = true;

	}

	public void remove(AbstractBlob blob) {
		int xk = blob.getBound().x;
		int yk = blob.getBound().y;
		int w = blob.getBound().width;
		int h = blob.getBound().height;
		boolean[] mask = blob.getMask();
		for (int y = 0; y < h; y++)
			for (int x = 0; x < w; x++)
				if (mask[x + y * w])
					pixels[x + xk + (y + yk) * width] = false;

	}

	@Override
	protected BooleanImage clone() {
		return new BooleanImage(width, height, Arrays.copyOf(pixels,
				pixels.length));
	}

	public BooleanImage duplicate() {
		return clone();
	}

	public void invert() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = !pixels[i];
		}
	}

	public void fillHoles(int size) {
		BooleanImage inv = this.clone();
		inv.invert();

		List<AbstractBlob> holes = new FloodFill(inv).getBlobs();

		for (AbstractBlob hole : holes) {
			if (hole.getSize() < size)
				inv.remove(hole);
		}
		inv.invert();
		pixels = inv.getPixels();
	}
	

}
