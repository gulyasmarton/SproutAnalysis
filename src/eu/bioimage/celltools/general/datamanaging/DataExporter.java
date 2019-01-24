package eu.bioimage.celltools.general.datamanaging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class DataExporter {

	/**
	 * mean, sd, sem, n
	 * 
	 * @param path
	 * @param values
	 */
	public static void saveStat(String path, List<List<? extends Number>> values) {
		PrintWriter out;
		try {
			out = new PrintWriter(path);
			for (List<? extends Number> list : values) {
				String line = Statistic.mean(list) + " ";
				line += Statistic.sd(list) + " ";
				line += Statistic.sem(list) + " ";
				line += list.size();
				out.println(line);
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public static void saveDataPoint(String path, List<DataPoint> values) {
		PrintWriter out;
		try {
			out = new PrintWriter(path);
			for (DataPoint d : values) {
				out.println(d.toString());
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void saveStatWithLabel(String path, List<String> labels,
			List<List<? extends Number>> values) {
		PrintWriter out;
		try {
			out = new PrintWriter(path);
			int c = 0;
			for (List<? extends Number> list : values) {
				String line = labels.get(c++)+ " ";
				line += Statistic.mean(list) + " ";
				line += Statistic.sd(list) + " ";
				line += Statistic.sem(list) + " ";
				line += list.size();
				out.println(line);
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void saveNumber(String path,
			List<List<? extends Number>> values) {
		PrintWriter out;
		try {
			out = new PrintWriter(path);
			for (List<? extends Number> list : values) {
				StringBuilder line = new StringBuilder(list.size() * 10);
				for (Number s : list) {
					line.append(s);
					line.append(" ");
				}
				out.println(line.toString().substring(0, line.length() - 1));

			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void saveStringBuilder(String path, StringBuilder sb) {
		try {
			BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(
					path)));
			bwr.write(sb.toString());
			bwr.flush();
			bwr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveNumber1D(String path, List<Double> values) {
		PrintWriter out;
		try {
			out = new PrintWriter(path);
			for (Double d : values) {				
				out.println(d);
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
