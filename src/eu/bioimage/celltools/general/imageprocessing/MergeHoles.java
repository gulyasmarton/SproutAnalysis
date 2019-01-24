package eu.bioimage.celltools.general.imageprocessing;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import eu.bioimage.celltools.general.datamanaging.DataGroup;
import eu.bioimage.celltools.general.datamanaging.DataPoint;
import eu.bioimage.celltools.holeanalyzer.model.HoleAnalyser;

public class MergeHoles {
	public static void main(String[] args) {
		if (args == null || args.length < 2) {
			System.out.println("No Args");
			System.out.println("limit inputFile1 inputFile2... inputZipFileN (ablosute path!)");
			return;
		}

		int size = Integer.parseInt(args[0]);
		Double limit = size == 0 ? null : size * 1d;

		List<File> files = new ArrayList<>();

		for (int i = 1; i < args.length; i++) {
			File source = new File(args[i]);
			files.add(source);
		}

		DataGroup group = new DataGroup("group", files);
		group.loadYourselfAsFiles();

		List<List<Double>> allHole = HoleAnalyser.getMegredHoles(group);
		for (List<Double> list : allHole) {
			for (Iterator<Double> iterator = list.iterator(); iterator.hasNext();) {
				Double v = iterator.next();
				if (v == null || v < limit)
					iterator.remove();

			}
			if (list.isEmpty())
				list.add(limit);
		}

		
		List<DataPoint> dp = HoleAnalyser.convertToDataPoint(allHole);
		
		for (DataPoint dataPoint : dp) {
			System.out.println(dataPoint);
		}
	}
}
