package eu.bioimage.celltools.general.datamanaging;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class FigureCreator {
	String name;
	File folder;

	public FigureCreator(String name, String path) {
		super();
		this.name = name;
		folder = new File(path + File.separator + name);
		folder.mkdir();
	}

	public void saveData(String name, List<? extends DataPoint> list) {

		name = escaleCharacters(name);

		BufferedWriter bwr;

		try {
			bwr = new BufferedWriter(new FileWriter(new File(folder.getPath()
					+ File.separator + name + ".txt")));

			for (DataPoint data : list) {
				bwr.write(data.toString());
				bwr.newLine();
			}

			bwr.flush();
			bwr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveData(String filename,StringBuilder builder){
		DataExporter.saveStringBuilder(Paths.get(folder.getPath(),filename).toString(), builder);
	}

	public static String escaleCharacters(String s) {
		s = s.replace("%", "");
		s = s.replace(" ", "_");
		s = s.replace("+", "_");
		s = s.replace(".", "_");
		return s;
	}

	public void saveGnu(GnuWrapper gnu) {
		saveGnu(gnu, name);
	}

	public void saveGnu(GnuWrapper gnu, String name) {
		gnu.save(name, folder.getPath());
	}

	public File getFolder() {
		return folder;
	}

	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
