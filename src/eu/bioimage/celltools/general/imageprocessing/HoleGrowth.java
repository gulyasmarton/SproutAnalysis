package eu.bioimage.celltools.general.imageprocessing;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import eu.bioimage.celltools.experiments.lyukno.LyukNoMain;
import eu.bioimage.celltools.general.datamanaging.DataExporter;
import eu.bioimage.celltools.holeanalyzer.model.HoleBinarizator;

public class HoleGrowth {

	public static void main(String[] args) {
		if (args == null || args.length < 3) {
			System.out.println("No Args");
			System.out.println("step minSize inputZipFile1 inputZipFile2... inputZipFileN (ablosute path!)");
			return;
		}
		
		int step = Integer.parseInt(args[0]);
		int minSize = Integer.parseInt(args[1]);

		LyukNoMain main = new LyukNoMain(minSize, step);

		for (int i = 2; i < args.length; i++) {

			File source = new File(args[i]);
			
			System.out.println(source.getName());

			List<AbstractBlob>[] holes = HoleBinarizator.GetHoleFromBinImage(source.getAbsolutePath());
			StringBuilder txt = main.analyzeHoles(Arrays.asList(holes));

			String output = Paths.get(source.getParent(), source.getName().replace(".zip", "st" + step + "-grow.txt"))
					.toString();

			DataExporter.saveStringBuilder(output, txt);
		}

	}

}
