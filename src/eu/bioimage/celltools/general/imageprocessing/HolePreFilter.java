package eu.bioimage.celltools.general.imageprocessing;

import java.io.File;
import java.nio.file.Paths;

import eu.bioimage.celltools.experiments.lefedettseg.Csoport;
import eu.bioimage.celltools.holeanalyzer.model.HoleBinarizator;

public class HolePreFilter {

	public static void main(String[] args) {

		if (args == null || args.length < 2) {
			System.out.println("No Args");
			System.out.println("ExpID(Str) filedID(int) OutputPostFix(optional, -bin)");
			return;
		}

		String expID = args[0];
		int fieldID = Integer.parseInt(args[1]);
		String postFix = args.length > 2 ? args[2] : "-bin";

		Csoport csoport = new Csoport(expID, fieldID);

		String idx = csoport.getID();
		String source = Paths.get(csoport.getPath(), "jpegs").toString();
		String output = Paths.get(csoport.getPath(), "analysis").toString();
		
//		System.out.println(expID);
//		System.out.println(fieldID);
//		System.out.println(postFix);
//		if(true) return;
		
		if(!(new File(source).isDirectory())){
			System.out.println("No jpegs folder: " + source);
			return;
		}
		
		if(!(new File(output).isDirectory())){
			boolean ok = new File(output).mkdirs();
			if(!ok){
				System.out.println("Cannot create analysis folder: " +output);
				return;
			}
		}
		
		for (int i : csoport.getFields()) {
			HoleBinarizator.VarianceMedianFilter(source, output, idx, i,postFix);
		}
	}

}
