package eu.bioimage.celltools.general.imageprocessing;

import java.awt.Color;

import eu.bioimage.celltools.cluster3d.model.C3D;
import eu.bioimage.celltools.general.datamanaging.IOtoolBox;
import eu.bioimage.celltools.nucleuscounter.model.Parallel;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.plugin.ZProjector;
import ij.process.ByteProcessor;
import ij.process.ImageProcessor;

public class C3DtoProj {

	public static void main(String[] args) {
		if (args == null || args.length < 2) {
			System.out.println("No Args");
			System.out.println("input-C3D-file-path output-file-path format[jpg,png]");
			return;
		}

		String input = args[0];
		String output = args[1];
		String format = args[2];

		C3D c3d = IOtoolBox.openC3D(input);

		ImagePlus imp = c3d.getColorImage();
		ImageStack st1 = imp.getStack();
		ImageStack st2 = new ImageStack(imp.getWidth(), imp.getHeight());

		final int black = Color.BLACK.getRGB();

		for (int i = 0; i < st1.getSize(); i++) {
			ImageProcessor ip = st1.getProcessor(i + 1);
			final int[] pix1 = (int[]) ip.getPixels();
			final byte[] pix2 = new byte[pix1.length];
			Parallel p = new Parallel(pix1.length) {
				
				@Override
				public void coreProcess(int i) {
					if(pix1[i]!=black)
						pix2[i] = (byte) 255;
				}
			};
			p.start();
			st2.addSlice(new ByteProcessor(imp.getWidth(), imp.getHeight(),pix2));
		}
		
		ZProjector projector = new ZProjector(new ImagePlus("img", st2));
		projector.setMethod(ZProjector.MAX_METHOD);
		projector.doProjection();
		
		FileSaver saver = new FileSaver(projector.getProjection());
		if(format.contains("png"))
			saver.saveAsPng(output);
		else
			saver.saveAsJpeg(output);
	}

}
