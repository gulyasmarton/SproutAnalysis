package eu.bioimage.celltools.cluster3d.controller.listeners;

public interface Cluster3DControllerListener {

	void sliderValueChanged(int value);

	void sliderMaximumChanged(int value);

	void sliderMinimumChanged(int value);

	void selectedThresholdChanged(int idx);

}
