package eu.bioimage.celltools.sproutanalyzing.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.bioimage.celltools.general.datamanaging.DataGroup;
import eu.bioimage.celltools.general.datamanaging.DataImporter;
import eu.bioimage.celltools.general.datamanaging.DataPoint;

public class SproutSorter {

	private class MergedCylinder extends DataPoint {
		public MergedCylinder(Double distance, List<Double> list) {
			super(list);
			this.distance = distance;
		}

		double distance;

		@Override
		public String toString() {
			return distance + " " + getMean() + " " + getSd() + " " + getSem()
					+ " " + getN();
		}

	}

	int distanceIdx = 0;
	int holeCountIdx = 1;
	public SproutSorter(int distanceIdx, int holeCountIdx) {
		super();
		this.distanceIdx = distanceIdx;
		this.holeCountIdx = holeCountIdx;
	}
	
	public  DataGroup getMergedData(String path, String pattern,String name){		
		List<File> files = DataImporter.getFile(path);
		List<File> selectedFile = DataImporter.filterFileByPattern(pattern, files);
		System.out.println(pattern +": "+selectedFile.size());
		DataGroup group = new DataGroup(name,selectedFile);
		group.loadYourselfAsFiles();
		group.setElements(getInterpole(group.getElements()));
		return group;
	}

	private List<MergedCylinder> getInterpole(List<Object> elements) {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;

		for (Object object : elements) {
			List<double[]> cylinders = (List<double[]>) object;
			for (double[] ds : cylinders) {
				if (max < ds[distanceIdx])
					max = ds[distanceIdx];
				if (min > ds[distanceIdx])
					min = ds[distanceIdx];
			}
		}
		List<Double> distance = new ArrayList<Double>();
		List<List<Double>> data = new ArrayList<List<Double>>();
		for (double dist = min; dist < max; dist += 10) {
			distance.add(dist);
			List<Double> cd = new ArrayList<Double>();
			for (Object object : elements) {
				List<double[]> cylinders = (List<double[]>) object;
				Double holeSize = getInterpole(dist, cylinders);
				if (holeSize != null)
					cd.add(holeSize);
			}
			data.add(cd);
		}

		List<MergedCylinder> cylinders = new ArrayList<MergedCylinder>();
		for (int i = 0; i < distance.size(); i++) {
			cylinders.add(new MergedCylinder(distance.get(i), data.get(i)));
		}

		return cylinders;
	}

	private Double getInterpole(double dist, List<double[]> cylinders) {

		if (cylinders.get(0)[distanceIdx] > dist)
			return null;
		if (cylinders.get(cylinders.size() - 1)[distanceIdx] < dist)
			return null;

		for (int i = 0; i < cylinders.size() - 1; i++) {
			if (dist == cylinders.get(i)[distanceIdx])
				return cylinders.get(i)[holeCountIdx];
			if (dist == cylinders.get(i + 1)[distanceIdx])
				return cylinders.get(i + 1)[holeCountIdx];
			if (dist > cylinders.get(i)[distanceIdx]
					&& dist < cylinders.get(i + 1)[distanceIdx]) {
				double r1 = Math.abs(dist - cylinders.get(i)[distanceIdx]);
				double r2 = Math.abs(dist - cylinders.get(i + 1)[distanceIdx]);
				return cylinders.get(i)[holeCountIdx] * r2 / (r1 + r2)
						+ cylinders.get(i)[holeCountIdx] * r1 / (r1 + r2);
			}
		}

		return null;
	}

}
