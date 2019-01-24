package eu.bioimage.celltools.general.datamanaging;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

public class DataGroup {
	protected String name;
	protected List<Object> elements;

	public DataGroup(String name, List<? extends Object> elements) {
		super();
		this.name = name;
		this.elements = (List<Object>) elements;
	}

	public DataGroup(String name) {
		super();
		this.name = name;
		this.elements = new ArrayList<Object>();
	}

	public DataGroup(String name, Object object) {
		this(name);
		elements.add(object);
	}

	public void add(Object element) {
		elements.add(element);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Object> getElements() {
		return elements;
	}

	public <T> List<T> getElements(List<T> list) {
		return (List<T>) elements;
	}

	public void setElements(List<? extends Object> elements) {
		this.elements = (List<Object>) elements;
	}

	public static DataGroup mergeAs(String name, DataGroup... groups) {
		DataGroup group = new DataGroup(name);

		for (DataGroup dataGroup : groups) {
			group.getElements().addAll(dataGroup.getElements());
		}

		return group;
	}

	/**
	 * Meg kell keresni az �sszes h�v�st �s �t�rni �j p�ld�ny kezel�sre. �s
	 * persze itt is �t kell �rni
	 * 
	 * @return
	 */
	public DataGroup loadYourselfAsFiles() {
		for (int i = 0; i < elements.size(); i++) {
			File file = (File) elements.get(i);
			elements.set(i, DataImporter.getData(file));
		}
		return this;
	}

	public DataGroup mergeDataColumn(int colIdx) {
		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		for (int i = 0; i < elements.size(); i++) {
			List<double[]> file = (List<double[]>) elements.get(i);
			for (int j = 0; j < file.size(); j++) {
				if (j >= dataPoints.size())
					dataPoints.add(new DataPoint(file.get(j)[colIdx]));
				else
					dataPoints.get(j).addData(file.get(j)[colIdx]);
			}
		}
		return new DataGroup(getName(), dataPoints);
	}

	public void addAll(List<? extends Object> filterFileByPattern) {
		elements.addAll(filterFileByPattern);
	}

	public static List<double[]> mergeByRows(List<double[]>... data) {

		List<double[]> merge = new ArrayList<double[]>();

		int max = 0;

		for (List<double[]> list : data)
			if (max < list.size())
				max = list.size();

		for (int i = 0; i < max; i++) {
			List<double[]> row = new ArrayList<double[]>();
			int size = 0;
			for (List<double[]> list : data)
				if (list.size() > i)
					row.add(list.get(i));

			for (double[] ds : row)
				if (ds != null)
					size += ds.length;

			double[] rowa = new double[size];
			int idx = 0;
			for (double[] ds : row)
				if (ds != null)
					for (double d : ds)
						rowa[idx++] = d;
			Arrays.sort(rowa);

			for (int j = 0; j < rowa.length / 2; j++) {
				double temp = rowa[j];
				rowa[j] = rowa[rowa.length - 1 - j];
				rowa[rowa.length - 1 - j] = temp;
			}
			// ArrayUtils.reverse(rowa);
			merge.add(rowa);
		}

		return merge;
	}

	public void merge(DataGroup group) {
		for (Object object : group.elements) {
			elements.add(object);
		}
		// elements.addAll(group.elements);
	}

	// public void setDataColunm(int... idxs) {
	//
	// int size = idxs.length;
	// for (int g = 0; g < elements.size(); g++) {
	// List<double[]> array = (List<double[]>) elements.get(i);
	//
	// for (int i = 0; i < array.length; i++) {
	// double[] data = (double[]) elements.get(i);
	// double[] sel = new double[size];
	// for (int j = 0; j < idxs.length; j++) {
	// sel[j] = data[idxs[j]];
	// }
	// }
	// }
	//
	// }
}
