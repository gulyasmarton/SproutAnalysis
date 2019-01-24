package eu.bioimage.celltools.general.imageprocessing;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

import eu.bioimage.celltools.general.datamanaging.DataGroup;
import eu.bioimage.celltools.general.datamanaging.DataImporter;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.filter.GaussianBlur;
import ij.process.ImageProcessor;

public class LaplacianPyramid {

	private GaussianBlur blur = new GaussianBlur();
	
	double sigma = 2;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LaplacianPyramid p = new LaplacianPyramid();
		
		String idx = "S476";
		int fieldIdx = 1;
		int level = 2;
		String source = Paths.get("/mnt/home_shared/marci/meas/motil", idx, "jpegs").toString();
		
		List<File> files = DataImporter.getFile(source);
		String id = "X" + (fieldIdx < 10 ? "0" + fieldIdx : fieldIdx);
		DataGroup group = new DataGroup(id, DataImporter.filterFileByPattern(idx + "-ph_" + id + ".*\\.jpg", files));

		ImagePlus imp = new ImagePlus(((File) group.getElements().get(100)).getPath());
		ImageProcessor ip = p.run(imp.getProcessor(),level);
		imp.show();
		imp = new ImagePlus("Level - "+level, ip);
		imp.show();
		
		FileSaver saver = new FileSaver(imp);
		saver.saveAsTiff("/home/marci/test1.tif");
	}
	
	
	public ImageProcessor run(ImageProcessor ip, int level){
		
		if(level<=0)
			return ip;
			
		
		ImageProcessor ipBlur = ip.convertToFloat();
		ImageProcessor ipBackground = ipBlur.duplicate();
		
		blur.blurGaussian(ipBlur, sigma);
		
		float[] f1 = (float[]) ipBackground.getPixels();
		float[] f2 = (float[]) ipBlur.getPixels();
		
		for (int i = 0; i < f2.length; i++) {
			f1[i]=Math.abs(f1[i]-f2[i]);
		}
		
		ip =  ipBackground.resize(ip.getWidth()/2);
		
		
		
		return run(ip, --level);
	}

}
