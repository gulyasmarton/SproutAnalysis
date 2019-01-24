package eu.bioimage.celltools.general.imageprocessing;

import java.awt.Point;
import java.awt.Rectangle;
import java.rmi.dgc.VMID;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.StackConverter;

public class VarianceFilter {
	ImagePlus vImp;
	int kernelSize;
	String title;
	
	public VarianceFilter(int kernerSize) {
		this.kernelSize = kernerSize;
	}

	public VarianceFilter(int kernerSize, ImagePlus imp) {
		this.kernelSize = kernerSize;
		title = imp.getTitle();
		doWork2(imp);
	}

	public VarianceFilter(int kernerSize, String path) {
		this.kernelSize = kernerSize;
		ImagePlus imp = new ImagePlus(path);
		title = imp.getTitle();
		doWork2(imp);
	}
	
	public FloatProcessor filter(ImageProcessor ip){
		return new FloatProcessor(ip.getWidth(), ip.getHeight(), getVarianceArrayParallel(ip));
	}

	private void doWork(ImagePlus imp) {
		if (imp.isHyperStack())
			throw new IllegalArgumentException("isHyperStack");
		if (imp.getStackSize() == 1)
			throw new IllegalArgumentException("not stack");

		// Ez lez�zza az eredeti k�pet is 8 bitesre!
		if (!(imp.getProcessor() instanceof ByteProcessor)) {
			StackConverter sc = new StackConverter(imp);
			sc.convertToGray8();
		}

		ImageStack stIn = imp.getStack();
		ImageStack stOut = new ImageStack(imp.getWidth(), imp.getHeight());

		for (int i = 1; i <= stIn.getSize(); i++) {
			stOut.addSlice(new FloatProcessor(imp.getWidth(), imp.getHeight(),
					getVarianceArray(stIn.getProcessor(i))));
		}
		vImp = new ImagePlus(title.replace(".", "-binary."), stOut);
		vImp.setCalibration(imp.getCalibration());
	}

	private float[] getVarianceArray(ImageProcessor processor) {
		// TODO Auto-generated method stub
		float[] outArray = new float[processor.getPixelCount()];

		// m�dos�tom azzal, hogy egy corpSize leszedi a sz�leket

		for (int y = kernelSize; y < processor.getHeight() - kernelSize; y++)
			for (int x = kernelSize; x < processor.getWidth() - kernelSize; x++)
				outArray[x + y * processor.getWidth()] = getVariance(processor,
						kernelSize, x, y);
		return outArray;
	}

	private float getVariance(ImageProcessor processor, int kernelSize, int x,
			int y) {
		// TODO Auto-generated method stub
		int size = 0;
		byte[] StoredPixel = new byte[kernelSize * kernelSize];
		byte[] pixels = (byte[]) processor.getPixels();

		for (int yk = y - (kernelSize - 1) / 2; yk <= y + (kernelSize - 1) / 2; yk++)
			for (int xk = x - (kernelSize - 1) / 2; xk <= x + (kernelSize - 1)
					/ 2; xk++) {
				if (xk + yk * processor.getWidth() > 0
						&& xk + yk * processor.getWidth() < processor
								.getPixelCount()) {
					StoredPixel[size] = pixels[xk + yk * processor.getWidth()];
					size++;
				}
			}

		float mean = 0;
		for (int i = 0; i < size; i++) {
			mean += StoredPixel[i] & 0xFF;
		}
		mean /= (float) size;
		float variance = 0;
		for (int i = 0; i < size; i++) {
			variance += ((StoredPixel[i] & 0xFF) - mean)
					* ((StoredPixel[i] & 0xFF) - mean);
		}

		return variance / (float) size;
	}

	public ImagePlus getImagePlus() {
		return vImp;
	}

	private void doWork2(ImagePlus imp) {
		if (imp.isHyperStack())
			throw new IllegalArgumentException("isHyperStack");
		// if(imp.getStackSize()==1) throw new
		// IllegalArgumentException("not stack");

		// Ez lez�zza az eredeti k�pet is 8 bitesre!
		if (!(imp.getProcessor() instanceof ByteProcessor)) {
			if (imp.getStackSize() > 1) {
				StackConverter sc = new StackConverter(imp);
				sc.convertToGray8();
			} else {
				imp = new ImagePlus(imp.getTitle(), imp.getProcessor()
						.convertToByte(true));
			}
		}

		ImageStack stIn = imp.getStack();

		int s = kernelSize;
		Rectangle rec = new Rectangle(s, s, imp.getWidth() - s * 2,
				imp.getHeight() - s * 2);

		ImageStack stOut = new ImageStack(imp.getWidth(), imp.getHeight());
		//ImageStack stOut = new ImageStack(rec.width, rec.height);

		for (int i = 1; i <= stIn.getSize(); i++) {
			ImageProcessor ip = new FloatProcessor(imp.getWidth(),
					imp.getHeight(),
					getVarianceArrayParallel(stIn.getProcessor(i)));
			//ip.setRoi(rec);
			
			float[] pix = (float[]) ip.getPixels();
			for(int y=0;y<ip.getHeight();y++)
				for(int x= 0;x<ip.getWidth();x++)
					if(!rec.contains(new Point(x, y)))
						pix[x+y*ip.getWidth()] = 0;
			
			//ip = ip.crop();
			stOut.addSlice(ip);
		}
		vImp = new ImagePlus(title.replace(".", "-binary."), stOut);

		vImp.setCalibration(imp.getCalibration());
	}

	private float[] getVarianceArrayParallel(final ImageProcessor processor) {
		// TODO Auto-generated method stub
		final float[] outArray = new float[processor.getPixelCount()];
		final int width = processor.getWidth();
		final int height = processor.getHeight();
		final int processors = Runtime.getRuntime().availableProcessors();
		// System.out.println("currently active threads: " + processors);

		Thread[] szalak = new Thread[processors];
		for (int cpu = 0; cpu < szalak.length; cpu++) {
			szalak[cpu] = new Thread(new Runnable() {
				@Override
				public void run() {
					// System.out.println("size: " + width+ "; " + height);
					int threadId = (int) (Thread.currentThread().getId() % processors);
					// System.out.println("cpu: "+threadId);
					for (int idx = threadId; idx < outArray.length; idx += processors) {
						// int y = idx / width;
						// int x = idx - (y * height);
						int y = idx / width;
						int x = idx % width;
						// if(y>=kernelSize)
						// System.out.println("index: "+idx);

						if (x >= kernelSize && y >= kernelSize
								&& x < width - kernelSize
								&& y < height - kernelSize)
							outArray[x + y * width] = getVariance(processor,
									kernelSize, x, y);
					}

				}
			});
			szalak[cpu].start();
		}

		for (Thread thread : szalak) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*
		 * for(int y=kernelSize;y<processor .getHeight()-kernelSize;y++) for(int
		 * x=kernelSize;x<processor.getWidth()-kernelSize;x++)
		 * outArray[x+y*processor.getWidth()] = getVariance(processor,
		 * kernelSize,x,y);
		 */
		return outArray;
	}
}
