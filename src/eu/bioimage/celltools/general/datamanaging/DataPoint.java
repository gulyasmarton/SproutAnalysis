package eu.bioimage.celltools.general.datamanaging;

import java.util.ArrayList;
import java.util.List;

public class DataPoint {
	List<Double> data;

	public List<Double> getData() {
		return data;
	}

	Double mean;
	Double sem;
	Double sd;

	public DataPoint(double value) {
		data = new ArrayList<Double>();
		data.add(value);
	}

	public DataPoint(List<? extends Number> data) {

		if (data.get(0) instanceof Double)
			this.data = (List<Double>) data;
		else if (data.get(0) instanceof Integer) {
			this.data = new ArrayList<Double>(data.size());
			for (Number number : data) {
				this.data.add(number.doubleValue());
			}
		}
	}

	public DataPoint(double[] ds) {
		data = new ArrayList<Double>();
		if (ds == null || ds.length == 0)
			data.add(0d);
		else
			for (double d : ds)
				data.add(d);
	}

	public void addData(double value) {
		data.add(value);
	}

	public int getN() {
		return data.size();
	}

	public Double getMean() {
		if (mean == null)
			mean = Statistic.mean(data);
		return mean;
	}

	public Double getSem() {
		if (sem == null)
			sem = Statistic.sem(data);
		return sem;
	}

	public Double getSd() {
		if (sd == null)
			sd = Statistic.sd(data);
		return sd;
	}
	
	public static List<DataPoint> merge(List<DataPoint>... groups){
		List<DataPoint> back = new ArrayList<DataPoint>();
		for (List<DataPoint> list : groups) {
			for (int i = 0; i < list.size(); i++) {
				if(back.size()>i)
					back.get(i).getData().addAll(list.get(i).getData());
				else
					back.add(list.get(i));
					
			}
		}
		return back;
	}

	@Override
	public String toString() {
		return getMean() + " " + getSd() + " " + getSem() + " " + getN();
	}
}
