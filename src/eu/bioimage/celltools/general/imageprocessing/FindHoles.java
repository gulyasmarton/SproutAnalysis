package eu.bioimage.celltools.general.imageprocessing;

import eu.bioimage.celltools.holeanalyzer.model.HoleBinarizator;

public class FindHoles {

	public static void main(String[] args) {
		if (args == null || args.length < 2) {
			System.out.println("No Args");
			System.out.println("source-binary-ZIP-file-path output-txt-file-path");
			return;
		}

		String source = args[0];
		String output = args[1];
		
		HoleBinarizator.GetHoleFromBinImage(source, output);
	}

}
