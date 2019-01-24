package eu.bioimage.celltools.cluster3d.view;

import ij.ImagePlus;
import ij.gui.StackWindow;

public class StackWindowWithScrollEvent extends StackWindow {	
	public StackWindowWithScrollEvent(ImagePlus imp) {
		super(imp);		
	}
	public java.awt.Scrollbar getScrollbar() {
		return this.sliceSelector;
	}
	public void setScrollbar(java.awt.Scrollbar scrollbar) {
		this.sliceSelector = scrollbar;
	}
	
}
