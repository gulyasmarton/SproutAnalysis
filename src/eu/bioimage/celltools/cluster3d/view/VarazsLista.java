package eu.bioimage.celltools.cluster3d.view;

import javax.swing.DefaultListModel;

public class VarazsLista {
	private DefaultListModel model;		
	public VarazsLista(){			
		model = new DefaultListModel();		
	}
	
	public int get(int idx){
		return (Integer)model.get(idx);
	}
	
	public void set(int idx, int value){
		model.set(idx, value);
	}
	
	public void set(int value){
		model.addElement(value);
	}

	public DefaultListModel getModel() {
		return model;
	}
}
