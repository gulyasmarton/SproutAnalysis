package eu.bioimage.celltools.general.imageprocessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import eu.bioimage.celltools.general.datamanaging.DataExporter;

public class HoleSizeCorrect {
	public static void main(String[] args) {
		if (args == null || args.length < 3) {
			System.out.println("No Args");
			System.out.println("source-all-holes-file-path output-txt-file-path corrNum(double)");
			return;
		}

		String source = args[0];
		String output = args[1];
		double corrNum = Double.parseDouble(args[2]);

		StringBuilder sb = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new FileReader(source))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] holes = line.split("\\t");
				StringBuilder h2 = new StringBuilder();
				for (String h : holes) {
					if(h.isEmpty())
						continue;
					double s = Double.parseDouble(h) * corrNum;
					h2.append(s + "\t");
				}
				sb.append(h2.toString().trim());
				sb.append(System.lineSeparator());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DataExporter.saveStringBuilder(output, sb);
	}
}
