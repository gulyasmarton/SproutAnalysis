package eu.bioimage.celltools.general.plugins;

import java.io.File;

import eu.bioimage.celltools.general.datamanaging.IOtoolBox;
import eu.bioimage.celltools.general.datamanaging.OpenFileFilter;
import eu.bioimage.celltools.general.datamanaging.Zipper;
import ij.ImagePlus;
import ij.plugin.PlugIn;

public class OpenZipPlugin implements PlugIn {

	@Override
	public void run(String arg) {
		
		String path = IOtoolBox.commonOpenDialog("Open zip (png)", null, "pngzip", new OpenFileFilter("zip"));
		File f = new File(path);
		new ImagePlus(f.getName().replace(".zip", ""),Zipper.getImageStackFromZip(path)).show();
	}

}
