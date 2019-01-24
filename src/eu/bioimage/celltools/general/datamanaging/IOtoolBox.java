package eu.bioimage.celltools.general.datamanaging;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.io.TiffDecoder;
import ij.plugin.FileInfoVirtualStack;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;

import javax.swing.Action;
import javax.swing.JFileChooser;

import eu.bioimage.celltools.cluster3d.model.C3D;
import eu.bioimage.celltools.general.imageprocessing.AbstractBlob;
import eu.bioimage.celltools.general.imageprocessing.BooleanImage;
import eu.bioimage.celltools.sproutanalyzing.model.SPR;

public class IOtoolBox {

	public static <T> T as(Class<T> clazz, Object o) {
		if (clazz.isInstance(o)) {
			return clazz.cast(o);
		}
		return null;
	}

	public static String commonOpenDialog(String title, String defaultDir, String defaultName,
			OpenFileFilter... extension) {
		JFileChooser chooser = new JFileChooser();

		chooser.setDialogTitle(title);
		for (OpenFileFilter ex : extension)
			chooser.addChoosableFileFilter(ex);

		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setPreferredSize(new Dimension(600,500));
		if (defaultName != null)
			chooser.setSelectedFile(new File(defaultName));
		if (defaultDir != null)
			chooser.setCurrentDirectory(new File(defaultDir));
		else if (OpenDialog.getDefaultDirectory() != null)
			chooser.setCurrentDirectory(new File(OpenDialog.getDefaultDirectory()));
		
		Action details = chooser.getActionMap().get("viewTypeDetails");
		details.actionPerformed(null);

		int returnVal = chooser.showDialog(IJ.getInstance(), "Open");		

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			return file.getAbsolutePath();
		}
		return null;

	}

	public static String commonOpenDialog(String title, String defaultDir, String defaultName) {
		OpenDialog od = null;
		if (defaultName == null)
			od = new OpenDialog(title, defaultDir == null ? "" : defaultDir);
		else {
			od = new OpenDialog(title, defaultDir == null ? "" : defaultDir, defaultDir);
		}
		String directory = od.getDirectory();
		String fileName = od.getFileName();
		if (fileName == null)
			return null;
		return directory + File.separator + fileName;
	}

	public static String commonSaveDialog(String title, String defaultDir, String defaultName, String extension) {
		SaveDialog sd = null;
		if (defaultDir == null)
			defaultDir = "";
		if (extension == null)
			sd = new SaveDialog(title, defaultDir, defaultName);
		else {
			sd = new SaveDialog(title, defaultDir, defaultName, extension);
		}
		String directory = sd.getDirectory();
		String fileName = sd.getFileName();
		if (fileName == null)
			return null;
		return directory + File.separator + fileName;
	}

	public static boolean saveObject(String path, Object object) {
		try {
			FileOutputStream fileOut = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(object);
			out.close();
			fileOut.close();
			return true;
		} catch (IOException i) {
			i.printStackTrace();
		}
		return false;
	}

	public static Object openObject(String path) {
		try {
			FileInputStream fileIn = new FileInputStream(path);
			ObjectInputStream in = new ClassNameMappingObjectInputStream(fileIn);
			Object object = in.readObject();
			in.close();
			fileIn.close();
			return object;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean saveSPR(String path, SPR spr) {
		return saveObject(path, spr);
	}

	public static SPR openSPR(String path) {
		Object spr = openObject(path);
		if (spr == null)
			return null;
		return (SPR) spr;
	}

	public static boolean saveC3D(String path, C3D c3d) {
		return saveObject(path, c3d);
	}

	public static C3D openC3D(String path) {
		Object c3d = openObject(path);
		if (c3d == null)
			return null;
		return (C3D) c3d;
	}
			
	public static boolean saveBLS(String path, List<List<AbstractBlob>>  bls) {
		return saveObject(path, bls);
	}
	
	public static List<List<AbstractBlob>> openBLS(String path) {
		Object bls = openObject(path);
		if (bls == null)
			return null;
		return (List<List<AbstractBlob>>) bls;
	}
	
	public static boolean saveBooleanImage(String path, List<BooleanImage>  list) {
		return saveObject(path, list);
	}
	
	public static List<BooleanImage> openBooleanImage(String path) {
		Object list = openObject(path);
		if (list == null)
			return null;
		return (List<BooleanImage>) list;
	}
	
	public static ImagePlus openTifAsVirtual(String path){
		File file = new File(path);
		if(!file.getName().contains(".tif")) return null;
		FileInfo info = new FileInfo();
		info.directory = file.getParent();
		info.fileName = file.getName();
		TiffDecoder td = new TiffDecoder(file.getParent(), file.getName());
		FileInfo[] infos;
		IJ.showStatus("Decoding TIFF header...");
		try {
			infos = td.getTiffInfo();
		} catch (IOException e) {
			String msg = e.getMessage();
			if (msg == null || msg.equals(""))
				msg = "" + e;
			IJ.log("TiffDecoder: " + msg);
			return null;
		}
		FileInfoVirtualStack vs = new FileInfoVirtualStack(infos[0], false);
		ImagePlus imp = new ImagePlus(file.getName(), vs);
		imp.setFileInfo(info);
		return imp;
	}

	public static void saveTextFile(String path, String string) {
		try {
			PrintWriter writer = new PrintWriter(path);
			writer.write(string);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
