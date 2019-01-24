package eu.bioimage.celltools.general.datamanaging;

import java.util.List;

public class Statistic {

	public static double sum(List<? extends Number> values) {
		double d = 0;
		for (Number number : values) {
			d += number.doubleValue();
		}
		return d;
	}

	public static double mean(List<? extends Number> values) {
		if (values.size() == 0)
			return 0;
		return sum(values) / values.size();
	}

	public static double sd(List<? extends Number> values) {
		if (values.size()< 2)
			return 0;
		double s = 0;
		double m = mean(values);
		for (Number p : values) {
			s += (p.doubleValue() - m) * (p.doubleValue() - m);
		}
		s = Math.sqrt(s / (values.size() - 1));
		return s;
	}

	public static double sem(List<? extends Number> values) {
		if (values.size() <2)
			return 0;
		return sd(values) / Math.sqrt(values.size());
	}
}
