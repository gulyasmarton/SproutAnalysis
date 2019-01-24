package eu.bioimage.celltools.sproutanalyzing.controller;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import mdbtools.dbengine.functions.Count;
import eu.bioimage.celltools.cluster3d.model.C3D;
import eu.bioimage.celltools.general.datamanaging.ClassNameMappingObjectInputStream;
import eu.bioimage.celltools.general.datamanaging.IOtoolBox;
import eu.bioimage.celltools.general.datamanaging.OpenFileFilter;
import eu.bioimage.celltools.sproutanalyzing.model.SPR;
import eu.bioimage.celltools.sproutanalyzing.model.SerializableRoi;
import eu.bioimage.celltools.sproutanalyzing.model.SproutFinder;
import eu.bioimage.celltools.sproutanalyzing.view.SproutAnalyzerView;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.OvalRoi;
import ij.gui.Roi;
import ij.io.FileSaver;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

public class SproutAnalyzerController implements PlugIn {

	private C3D c3d;
	SproutFinder sf;
	private ArrayList<SerializableRoi> masks;
	private ImagePlus imp;
	private ImagePlus colorImp;
	String saveFolder;
	private double ratio = 1d;

	private int subImgCounter;

	private List<SproutAnalyzerListener> listeners = new ArrayList<SproutAnalyzerListener>();

	int fromCenter = 700;
	int distance = 20;

	public void addSproutAnalyzerListener(SproutAnalyzerListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public void removeSproutAnalyzerListener(SproutAnalyzerListener listener) {
		listeners.remove(listener);
	}

	protected void firedchangedFromCenter(int value) {
		for (SproutAnalyzerListener listener : listeners)
			listener.changedFromCenter(value);
	}

	protected void firedchangedDistance(int value) {
		for (SproutAnalyzerListener listener : listeners)
			listener.changedDistance(value);
	}

	@Override
	public void run(String arg) {
		SproutAnalyzerView view = new SproutAnalyzerView(this);
		view.setVisible(true);
		firedchangedDistance(distance);
		firedchangedFromCenter(fromCenter);

	}

	public void setCircles() {
		if (sf != null)
			sf.setCircle(fromCenter, distance);
	}

	public void showCylinders() {
		if (sf != null) {
			sf.getCylinder(true);
		}
	}

	public void removeMask() {
		masks = null;
		maskRemove();

	}

	public void openZVI() {
		if (c3d != null) {
			imp = new ImagePlus(saveFolder + "/" + c3d.getFilename());
			imp.show();
			imp = WindowManager.getCurrentImage();
		}
	}

	public void copyRoi() {
		if (imp != null) {
			Roi r = imp.getRoi();
			if (r == null)
				return;
			colorImp.setRoi(r);
			setCircles();
		}
	}

	public void SaveAllAs() {
		if (sf != null) {
			sf.getCylinder(false);
		}

		StringSelection selection = new StringSelection("a" + subImgCounter++
				+ "-");
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);

		String title = c3d.getFilename().replace(".zvi", ".none");
		SaveDialog dlg = new SaveDialog("Save cell log", title, ".none");

		if (dlg.getFileName() == null)
			return;

		saveFolder = dlg.getDirectory();

		title = dlg.getFileName().replace(".none", "-spr.txt");
		exportTXT(saveFolder + System.getProperty("file.separator") + title);
		FileSaver fs = new FileSaver(sf.getCylinderImp());

		fs.saveAsJpeg(saveFolder + System.getProperty("file.separator")
				+ dlg.getFileName().replace(".none", "-spr.jpg"));

		SaveSPR(saveFolder + System.getProperty("file.separator")
				+ dlg.getFileName().replace(".none", ".spr"));

		if(masks!=null)
			masks.clear();
		// colorImp.close();
		// if(imp!=null) imp.close();
	}

	public void setMask() {
		if (sf == null)
			return;

		Roi r = sf.getImp().getRoi();
		if (r == null)
			return;

		if (masks == null)
			masks = new ArrayList<SerializableRoi>();
		masks.add(new SerializableRoi(r));
		maskApply();

	}

	public void SaveAll() {
		String title = c3d.getFilename().replace(".zvi", "-spr.txt");
		exportTXT(saveFolder + System.getProperty("file.separator") + title);
		FileSaver fs = new FileSaver(sf.getCylinderImp());
		fs.saveAsJpeg(saveFolder + System.getProperty("file.separator")
				+ sf.getCylinderImp().getTitle());

		SaveSPR(saveFolder + System.getProperty("file.separator")
				+ c3d.getFilename().replace(".zvi", ".spr"));
	}

	public void saveSPRdialog() {
		if (sf == null)
			return;
		SaveDialog sd = new SaveDialog("Save SPR file ...", c3d.getFilename()
				.replace(".zvi", ".spr"), ".spr");
		String directory = sd.getDirectory();
		String fileName = sd.getFileName();
		if (fileName == null)
			return;
		SaveSPR(directory + System.getProperty("file.separator") + fileName);
	}

	protected void SaveSPR(String path) {
		if (sf == null)
			return;
		SPR spr = new SPR(fromCenter, distance, sf.getKorok()[0].getBounds(),
				masks, ratio);
		
		IOtoolBox.saveSPR(path, spr);		
	}

	public void OpenSPR() {
		if (sf == null)
			return;
		
		String path = IOtoolBox.commonOpenDialog("Open SPR file ...", null, null,
				new OpenFileFilter("spr", "Sprout data file"));
		
		
		SPR spr = IOtoolBox.openSPR(path);
		
		if(spr==null)
			return;
		
		setFromCenter(spr.getFromUm());
		setDistance(spr.getDistUm());
		firedchangedFromCenter(fromCenter);
		firedchangedDistance(distance);
		ratio = spr.getRatio();
		Rectangle rec = spr.getOval();
		masks = spr.getMasks();
		maskApply();
		OvalRoi r = new OvalRoi(rec.x, rec.y, rec.width, rec.height);
		sf.getImp().setRoi(r);
		sf.setCircle(fromCenter, distance);
		sf.getCylinder(true);		
	}

	private void maskRemove() {
		if (c3d == null)
			return;
		sf.getImp().changes = false;
		if (sf.getImp().isVisible())
			sf.getImp().close();
		OvalRoi roi = sf.getKorok() != null ? sf.getKorok()[0] : null;
		sf = new SproutFinder(c3d.getForceColorImage());
		sf.getImp().setRoi(roi);
		colorImp = sf.getImp();
		sf.getImp().show();
		if (sf != null) {
			sf.setCircle(fromCenter, distance);
		}
	}

	public void clearMask() {
		if (masks != null)
			masks.clear();
	}

	private void maskApply() {
		maskRemove();
		if (masks == null)
			return;
		
		maskApply(sf.getImp(),masks);
		colorImp = sf.getImp();
//		ImageStack curr = sf.getImp().getStack();
//		ImageStack st = new ImageStack(sf.getImp().getWidth(), sf.getImp()
//				.getHeight());
//
//		for (int i = 1; i <= curr.getSize(); i++) {
//			ImageProcessor ip = curr.getProcessor(i);
//			ip.setColor(Color.black);
//			for (SerializableRoi roi : masks) {
//				ip.fill(roi.getRoi());
//			}
//			st.addSlice(ip);
//		}
//		sf.getImp().setStack(st);
//		colorImp = sf.getImp();

	}
	
	public static void maskApply(ImagePlus imp,List<SerializableRoi> masks){
		if(imp==null || masks==null)
			return;
		ImageStack curr = imp.getStack();
		ImageStack st = new ImageStack(imp.getWidth(), imp
				.getHeight());

		for (int i = 1; i <= curr.getSize(); i++) {
			ImageProcessor ip = curr.getProcessor(i);
			ip.setColor(Color.black);
			for (SerializableRoi roi : masks) {
				ip.fill(roi.getRoi());
			}
			st.addSlice(ip);
		}
		imp.setStack(st);
	}

	public void SaveTextDiagol() {
		if (c3d == null || sf == null)
			return;
		String title = c3d.getFilename().replace(".zvi", "-spr.txt");
		SaveDialog dlg = new SaveDialog("Save cell log", title, ".txt");

		exportTXT(dlg.getDirectory() + System.getProperty("file.separator")
				+ dlg.getFileName());

	}

	private void exportTXT(String path) {

		GenericDialog gd = new GenericDialog("Lefedettség");

		gd.addNumericField("Százalék: ", 100, 2);
		gd.showDialog();
		if (gd.wasCanceled())
			return;

		ratio = 100d / gd.getNextNumber();

		String[] sk = sf.getTXT(ratio);
		if (sk == null) {
			IJ.error("No holes!");
			return;
		}
		try {
			FileWriter outFile = new FileWriter(path);
			PrintWriter out = new PrintWriter(outFile);

			for (String s : sk)
				out.println(s);
			out.close();
			IJ.log("TXT ok: " + path);
		} catch (IOException e) {
			IJ.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void LoadC3D() {	
		String path = IOtoolBox.commonOpenDialog("Open C3D file ...", null, null,
				new OpenFileFilter("c3d", "3D model file"));
		
		if(path==null)
			return;
		
		c3d = IOtoolBox.openC3D(path);
	
		if (c3d == null)
			return;
		
		File f = new File(path);
		
		saveFolder = f.getParent();
		
		if (colorImp != null && colorImp.getWindow() != null) {
			colorImp.changes = false;
			colorImp.close();
		}

		if (imp != null && imp.getWindow() != null) {
			imp.changes = false;
			imp.close();
		}
		
			if(masks!=null)
				masks.clear();
			subImgCounter = 1;
			sf = new SproutFinder(c3d.getColorImage());
			colorImp = sf.getImp();
			colorImp.show();
		
	}

	public int getFromCenter() {
		return fromCenter;
	}

	public void setFromCenter(int fromCenter) {
		this.fromCenter = fromCenter;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

}
