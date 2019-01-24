package eu.bioimage.celltools.cluster3d.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ListaCellRenderer extends DefaultListCellRenderer {
	protected boolean[] aktivE;

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		if (aktivE == null)
			return this;
		if (!aktivE[index]) {
			Color bg = Color.red;
			setBackground(bg);
			setOpaque(true); // otherwise, it's transparent
		}
		return this; // DefaultListCellRenderer derived from JLabel,
						// DefaultListCellRenderer.getListCellRendererComponent
						// returns this as well.
	}

	public void setAktivE(boolean[] aktivE) {
		this.aktivE = aktivE;
	}

}
