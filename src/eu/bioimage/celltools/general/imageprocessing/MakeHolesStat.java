package eu.bioimage.celltools.general.imageprocessing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.bioimage.celltools.general.datamanaging.DataExporter;
import eu.bioimage.celltools.general.datamanaging.DataImporter;
import eu.bioimage.celltools.general.datamanaging.DataPoint;

public class MakeHolesStat {

	public static void main(String[] args) {
		if (args == null || args.length < 3) {
			System.out.println("No Args");
			System.out.println("source-all-holes-file-path output-txt-file-path sizeLimit");
			return;
		}

		String source = args[0];
		String output = args[1];
		int limit = Integer.parseInt(args[2]);
		fieldStat(source, output, limit);
		
	}
	
	private static void fieldStat(String source, String output,double limit) {		
				List<double[]> data = DataImporter.getData(new File(source));
				List<DataPoint> output1 = new ArrayList<DataPoint>();
				for (double[] ds : data) {
					List<Double> sel = new ArrayList<Double>();
					if (ds != null)
						for (Double d : ds) {
							if (d > limit)
								sel.add(d);
						}

					if (sel.isEmpty())
						sel.add(0d);

					output1.add(new DataPoint(sel));
				}
				DataExporter.saveDataPoint(output, output1);
		

	}

}
