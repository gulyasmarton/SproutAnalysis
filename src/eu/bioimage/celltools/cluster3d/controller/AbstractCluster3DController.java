package eu.bioimage.celltools.cluster3d.controller;

import ij.ImagePlus;
import ij.io.OpenDialog;
import ij.io.SaveDialog;
import ij.plugin.PlugIn;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListModel;

import eljarasok.Cell;
import eu.bioimage.celltools.cluster3d.controller.listeners.Cluster3DControllerListener;
import eu.bioimage.celltools.cluster3d.model.C3D;
import eu.bioimage.celltools.cluster3d.view.Cluster3DView;
import eu.bioimage.celltools.cluster3d.view.ListaCellRenderer;
import eu.bioimage.celltools.cluster3d.view.VarazsLista;
import eu.bioimage.celltools.general.imageprocessing.FloodFill3D;

public abstract class AbstractCluster3DController implements Cluster3DController, PlugIn {
	protected boolean[] aktivE;
	protected double[] thrs;
	protected VarazsLista varazs = new VarazsLista();
	protected List<Cluster3DControllerListener> listeners = new ArrayList<Cluster3DControllerListener>();
	protected ListaCellRenderer cellRenderer = new ListaCellRenderer();

	protected ImagePlus impOrg, impThreshold;
	
	protected FloodFill3D floodfill;
	
	protected C3D c3d;

	
	public void addCluster3DControllerListener(
			Cluster3DControllerListener listener) {
		listeners.add(listener);
	}

	public void removeCluster3DControllerListener(
			Cluster3DControllerListener listener) {
		listeners.remove(listener);
	}

	public void fireSliderValueChanged(int value) {
		for (Cluster3DControllerListener listener : listeners)
			listener.sliderValueChanged(value);
	}

	public void fireSliderMaximumChanged(int value) {
		for (Cluster3DControllerListener listener : listeners)
			listener.sliderMaximumChanged(value);
	}

	public void fireSliderMinimumChanged(int value) {
		for (Cluster3DControllerListener listener : listeners)
			listener.sliderMinimumChanged(value);
	}

	public void fireSelectedThresholdChanged(int idx) {
		for (Cluster3DControllerListener listener : listeners)
			listener.selectedThresholdChanged(idx);
	}
	
	protected abstract void updateThresholds();
	
	@Override
	public void setThresholdValue(int idx, int value) {
		if (impThreshold == null)
			return;
		if (idx == -1)
			return;

		varazs.set(idx, value);
		updateThresholds();
	}

	@Override
	public void resetThresholdValue(int selectedIndex) {
		if (impThreshold == null)
			return;
		if (selectedIndex == -1)
			return;

		varazs.set(selectedIndex, (int)thrs[selectedIndex]);

		aktivE[selectedIndex] = true;
		updateThresholds();
	}
	

	@Override
	public void interpolThresholdValue(int[] idxs) {
		if (idxs.length == 0)
			return;
		for (int elem : idxs) {
			aktivE[elem] = false;
		}
		updateThresholds();
	}
	

	@Override
	public ListModel getListaModel() {
		return varazs.getModel();
	}

	public ListaCellRenderer getCellRenderer() {
		return cellRenderer;
	}
	

	@Override
	public void saveC3D() {
		if (floodfill == null || floodfill.getC3D() == null)
			return;
		SaveDialog sd = new SaveDialog("Save C3D file ...", floodfill.getC3D()
				.getFilename().replace(".zvi", ".c3d"), ".c3d");
		String directory = sd.getDirectory();
		String fileName = sd.getFileName();
		if (fileName == null)
			return;
		try {
			FileOutputStream fileOut = new FileOutputStream(directory + "/"
					+ fileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(floodfill.getC3D());
			out.close();
			fileOut.close();
			System.out.printf("Serialized data is saved in: " + directory + "/"
					+ fileName);
		} catch (IOException i) {
			i.printStackTrace();
		}

	}

	@Override
	public void openC3D() {
		OpenDialog sd = new OpenDialog("Open C3D file ...");
		String directory = sd.getDirectory();
		String fileName = sd.getFileName();
		if (fileName == null)
			return;

		try {
			FileInputStream fileIn = new FileInputStream(directory + "/"
					+ fileName);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			c3d = (C3D) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			return;
		} catch (ClassNotFoundException c) {
			System.out.println("C3D class not found");
			c.printStackTrace();
			return;
		}
		if (c3d != null)
			c3d.getColorImage().show();

	}
	

	@Override
	public void makeMovie() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exportTXT() {
		if (c3d == null)
			return;
		String title = c3d.getFilename().replace(".zvi", "-info.txt");
		SaveDialog dlg = new SaveDialog("Save cell log", title, ".txt");

		try {
			FileWriter outFile = new FileWriter(dlg.getDirectory()
					+ System.getProperty("file.separator") + dlg.getFileName());
			PrintWriter out = new PrintWriter(outFile);

			for (Cell cell : c3d.getCells())
				out.println(cell.getVolume());
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	@Override
	public void run(String arg) {
		Cluster3DView view = new Cluster3DView(this);
		view.setVisible(true);
	}
}
