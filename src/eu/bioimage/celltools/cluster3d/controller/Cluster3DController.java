package eu.bioimage.celltools.cluster3d.controller;

import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import eu.bioimage.celltools.cluster3d.controller.listeners.Cluster3DControllerListener;

public interface Cluster3DController {

	String getTitle();

	void setActiveImage();

	void setThresholdFilter();

	void clusterDectecting();

	void addCluster3DControllerListener(Cluster3DControllerListener listener);

	void removeCluster3DControllerListener(Cluster3DControllerListener listener);

	void setThresholdValue(int idx, int value);

	void resetThresholdValue(int selectedIndex);

	void interpolThresholdValue(int[] idxs);

	ListModel getListaModel();

	ListCellRenderer getCellRenderer();

	void listaSelectionChanged(int idx);

	void openC3D();

	void saveC3D();

	void makeMovie();

	void exportTXT();

	void backgroundIsBlack(boolean isBlack);

}
