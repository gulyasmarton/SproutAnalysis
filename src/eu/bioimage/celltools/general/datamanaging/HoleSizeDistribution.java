package eu.bioimage.celltools.general.datamanaging;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HoleSizeDistribution {

	public static void main(String[] args) {
		if (args == null || args.length < 3) {
			System.out.println("No Args");
			System.out.println("size_limit xThFrame(int) inputFile1 inputFile2... inputFileN (ablosute path!)");
			return;
		}

		// List<List<double[]>> datas = new ArrayList<List<double[]>>();

		int limit = Integer.parseInt(args[0]);
		int frame = Integer.parseInt(args[1]);

		List<double[]>[] datas = new ArrayList[args.length - 2];

		for (int i = 2; i < args.length; i++) {
			// datas.add(DataImporter.getData(new File(args[i])));
			datas[i - 2] = DataImporter.getData(new File(args[i]));
			//System.out.println(datas[i - 1].size());
		}

		// datas.toArray(new ArrayList[datas.size()])
		List<double[]> data = DataGroup.mergeByRows(datas);
		
		if(frame>=data.size()){
			System.out.println("ERROR");
			return;
		}
		double[] numbers = data.get(frame);
		List<Double> list = new ArrayList<Double>();
		for (double d : numbers) {
			if(d>limit)
				list.add(d);
		}
		
		
		for (int idx = 0; idx < list.size(); idx++) {			
			System.out.println(((idx + 1) * 1d / list.size()) + " " + list.get(idx));
		}
	}

}
