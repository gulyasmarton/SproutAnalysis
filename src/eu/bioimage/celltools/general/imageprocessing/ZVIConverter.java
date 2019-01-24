package eu.bioimage.celltools.general.imageprocessing;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import eu.bioimage.celltools.nucleuscounter.model.Parallel;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.plugins.in.ImagePlusReader;
import loci.plugins.in.ImportProcess;
import loci.plugins.in.ImporterOptions;

public class ZVIConverter {

	public static void main(String[] args) {
		String path1 = "D:/munka/Elod/Z229/Z229-dish1-m4x5-z5x50-3d-tb.zvi";
		String path2 = "D:/munka/Elod/M57/zvi/M57-a3-x1-m3x4-green.zvi";
		ZVIConverter converter = new ZVIConverter();
		converter.open(path2).show();
	}

	public ImagePlus open(String path) {

		try {

			ImporterOptions options = new ImporterOptions();

			options.setId(path);
			options.setOpenAllSeries(true);
			options.setStackFormat(ImporterOptions.VIEW_STANDARD);

			ImportProcess process = new ImportProcess(options);
			process.execute();

			IFormatReader reader = process.getReader();
			Hashtable<String, Object> meta = reader.getGlobalMetadata();

			ImagePlusReader readerPR = new ImagePlusReader(process);
			final ImagePlus[] imps = readerPR.openImagePlus();
			
			Object rs = meta.get("Number Rows 0");
			Object cs = meta.get("Number Columns 0");
			
			if(rs==null || cs ==null){
				ImageStack st = new ImageStack(imps[0].getWidth(), imps[0].getHeight());
				for (ImagePlus i : imps) {
					st.addSlice(i.getProcessor());
				}
				return new ImagePlus(new File(path).getName(), st);
			}
				
			
			final int r = Integer.parseInt((String) cs);
			final int c = Integer.parseInt((String) rs);

			

//			Enumeration e = meta.keys();
//			while (e.hasMoreElements()) {
//				Object key = e.nextElement();
//				System.out.print(key);
//				System.out.print(" = ");
//				System.out.println(meta.get(key));
//			}
			
			String name = imps[0].getOriginalFileInfo().fileName.replace(
					".zvi", "");
			Calibration calibration = imps[0].getCalibration();
			
			if (imps.length==1) {
				ImagePlus imp = new ImagePlus(name, imps[0].getStack());
				imp.setCalibration(calibration);			
				return imp;
			}
			
			final int wi = imps[0].getWidth();
			final int hi = imps[0].getHeight();

			int d = imps[0].getStackSize();
			final int w = wi * c;
			final int h = hi * r;

			final ImageStack st = new ImageStack(w, h, d);

			Parallel parallel = new Parallel(d) {

				@Override
				public void coreProcess(int z) {
					// TODO Auto-generated method stub
					short[] pix = new short[w * h];
					for (int cc = 0; cc < c; cc++)
						for (int rr = 0; rr < r; rr++) {

							int nagyX = rr % 2 == 0 ? cc : c - cc - 1;

							// System.out.println(cc+rr*c);
							short[] cpix = (short[]) imps[nagyX + rr * c]
									.getStack().getPixels(z + 1);
							for (int y = 0; y < hi; y++)
								for (int x = 0; x < wi; x++)
									pix[x + cc * wi + (y + rr * hi) * w] = cpix[x
											+ y * wi];
						}
					st.setPixels(pix, z + 1);
				}
			};
			parallel.start();
			
			ImagePlus imp = new ImagePlus(name, st);
			imp.setCalibration(calibration);			
			return imp;
			
		} catch (Exception e) {

		}
		
		return null;
	}

}
