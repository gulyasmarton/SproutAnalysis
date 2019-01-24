package eu.bioimage.celltools.general.datamanaging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GnuWrapper {
	StringBuilder sb;

	public enum LegendPosition {
		left, right, top, bottom, center, inside, outside, lmargin, rmargin, tmargin, bmargin, above, over, below, unde
	};

	public GnuWrapper() {
		sb = new StringBuilder();
	}

	public void setPng() {
		appendLine("set term pngcairo");
	}

	public void setRangeX(Double min, Double max) {
		setRange("x", min, max);
	}

	public void setRangeY(Double min, Double max) {
		setRange("y", min, max);
	}

	private void setRange(String xy, Double min, Double max) {
		appendLine("set " + xy + "range [" + (min != null ? min : "") + ":" + (max != null ? max : "") + "]");
	}

	public void setPngAdvanced() {
		appendLine("set term pngcairo enhanced font ',16' size 640,640");
	}

	public void setOutput(String file) {
		appendLine("set output '" + file + "'");
	}

	public void setLegendPosition(LegendPosition... position) {
		append("set key ");
		for (int i = 0; i < position.length; i++) {
			append(position[i].name());
			if (i != position.length - 1)
				append(" | ");
		}
		appendLine();
	}

	public void setLabelX(String label) {
		appendLine("set xlabel '" + label + "'");
	}

	public void setLabelY(String label) {
		appendLine("set ylabel '" + label + "'");
	}

	public void startPlot() {
		append("plot ");
	}

	public void plotDataCalibrateHourMM(String file, String color, int dataIdx, int barIdx) {
		plotData(FigureCreator.escaleCharacters(file), file, "($0*10/60):($" + dataIdx + "*2.51*2.51/1e6)", color);
		breakLine();
		plotDataBar(FigureCreator.escaleCharacters(file), "($0*10/60):($" + dataIdx + "*2.51*2.51/1e6+$" + barIdx
				+ "*2.51*2.51/1e6):($" + dataIdx + "*2.51*2.51/1e6-$" + barIdx + "*2.51*2.51/1e6)", color);
	}

	public void plotData(String file, String title, String patern, String color) {
		append("'" + file + ".txt' u " + patern + " title '" + title + "' w li lw 2 lt 1 lc rgb '" + color + "'");
	}

	public void plotDataBar(String file, String patern, String color) {
		append("'" + file + ".txt' u " + patern + " notitle '%lf %lf %lf' w filledcu fs transparent solid 0.4 lc rgb '"
				+ color + "'");
	}

	public void breakLine() {
		appendLine(",\\");
	}

	public void append(String p) {
		sb.append(p);
	}

	public void appendLine() {
		sb.append(System.getProperty("line.separator"));
	}

	public void appendLine(String p) {
		append(p);
		appendLine();
	}

	public void save(String name, String path) {
		// TODO Auto-generated method stub
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(path + File.separator + name + ".gnu")));
			bw.write(sb.toString());
			bw.flush();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static final String[] Colors = new String[] { "black", "red", "blue", "green", "orange", "magenta", "brown",
			"cornflowerblue", "crimson","cyan","darkmagenta","darkorchid"};
}
