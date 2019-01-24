package eu.bioimage.celltools.general.imageprocessing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import eu.bioimage.celltools.holeanalyzer.model.HoleAnalyser;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

/**
 * Csak binariz�lt k�pen, ahol a lyuk a feh�r.
 * 
 * @author Marci
 *
 */
public class Bin2Holes {

	public static List<List<AbstractBlob>> getBlobs(ImagePlus imp, Integer minSize) {

		ImageStack st = imp.getStack();
		List<List<AbstractBlob>> blobs = new ArrayList<List<AbstractBlob>>();

		for (int i = 0; i < st.getSize(); i++)
			blobs.add(getBlobs(st.getProcessor(i + 1), minSize));
		return blobs;
	}

	public static List<AbstractBlob> getBlobs(ImageProcessor ip, Integer minSize) {
		BooleanImage bin = HoleAnalyser.getHoleImage(ip, null, null, null, null);
		FloodFill fill = new FloodFill(bin);
		List<AbstractBlob> blobs = fill.getBlobs();
		for (Iterator<AbstractBlob> iterator = blobs.iterator(); iterator.hasNext();) {
			AbstractBlob blob = (AbstractBlob) iterator.next();
			if (minSize != null && blob.getSize() < minSize)
				iterator.remove();
		}
		return blobs;
	}
	
	

	public static List<AbstractBlob> getBlobs(BooleanImage bin, Integer minSize) {		
		FloodFill fill = new FloodFill(bin);
		List<AbstractBlob> blobs = fill.getBlobs();
		for (Iterator<AbstractBlob> iterator = blobs.iterator(); iterator.hasNext();) {
			AbstractBlob blob = (AbstractBlob) iterator.next();
			if (minSize != null && blob.getSize() < minSize)
				iterator.remove();
		}
		return blobs;
	}
}
