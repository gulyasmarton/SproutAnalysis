package eu.bioimage.celltools.general.datamanaging;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import sajtplugin.Csoportositas;

public class DataImporter {

	public static List<DataPoint> getDataFromDataGroup(DataGroup group,
			int dataIdx) {

		List<List<double[]>> data = new ArrayList<List<double[]>>();

		for (Object file : group.getElements()) {
			List<double[]> d = getData((File) file);
			data.add(d);
		}

		List<DataPoint> stat = new ArrayList<DataPoint>();

		for (List<double[]> d2 : data) {
			int idx = 0;
			for (double[] d3 : d2) {
				if (idx >= stat.size())
					stat.add(new DataPoint(d3[dataIdx]));
				else
					stat.get(idx).addData(d3[dataIdx]);
				idx++;
			}
		}
		return stat;
	}

	public static List<File> getFile(String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		return new ArrayList<File>(Arrays.asList(listOfFiles));
	}

	public static List<File> filterFileByPattern(String pattern,
			List<File> allFile) {
		Pattern p = Pattern.compile(pattern);
		List<File> list = new ArrayList<File>();
		for (File file : allFile) {
			if (file.isFile() && p.matcher(file.getName()).matches()) {
				list.add(file);
			}
		}
		Collections.sort(list);
		return list;
	}

	public static List<String[]> getDataAsObject(File file) {
		List<String[]> list = new ArrayList<String[]>();
		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(file.getPath()));

			while ((sCurrentLine = br.readLine()) != null) {
				String[] row = sCurrentLine.split(" ");
				if (row.length < 2)
					row = sCurrentLine.split("\t");
				list.add(row);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return list;
	}

	public static List<double[]> getData(File file) {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		List<double[]> data = new ArrayList<double[]>();
		try {

			String sCurrentLine;

			br = new BufferedReader(new FileReader(file.getPath()));

			while ((sCurrentLine = br.readLine()) != null) {

				if (sCurrentLine.equals("")) {
					data.add(null);
					continue;
				}

				String[] row = sCurrentLine.split(" ");
				if (row.length < 2)
					row = sCurrentLine.split("\t");
				double[] rd = new double[row.length];

				for (int i = 0; i < row.length; i++) {
					rd[i] = Double.parseDouble(row[i]);
				}
				data.add(rd);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return data;
	}
}
