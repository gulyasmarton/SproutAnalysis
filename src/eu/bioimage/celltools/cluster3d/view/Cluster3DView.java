package eu.bioimage.celltools.cluster3d.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import eu.bioimage.celltools.cluster3d.controller.Cluster3DController;
import eu.bioimage.celltools.cluster3d.controller.listeners.Cluster3DControllerListener;

public class Cluster3DView extends JFrame {	
	private static final long serialVersionUID = -8635768365545527833L;
	
	private JPanel contentPane;
	private JList lista;
	private JButton btnSetActiveImage;
	private JButton btnThresholding;
	private JButton btnClusterAnalysing;
	private JButton btnSave;
	private JButton btnOpen;
	private JScrollBar scrollBar;
	private JLabel label;
	private JButton btnAuto;
	private JButton btnReset;
	private JButton btnExportTxt;
	private JButton btnMovie;
	private JCheckBox isBlack;

	public Cluster3DView(final Cluster3DController controller) {
		setTitle(controller.getTitle());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 231, 521);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		controller
				.addCluster3DControllerListener(new Cluster3DControllerListener() {

					@Override
					public void sliderValueChanged(int value) {
						scrollBar.setValue(value);
						label = new JLabel(value+"");
					}

					@Override
					public void sliderMinimumChanged(int value) {
						scrollBar.setMinimum(value);
					}

					@Override
					public void sliderMaximumChanged(int value) {
						scrollBar.setMaximum(value);
					}

					@Override
					public void selectedThresholdChanged(int idx) {
						lista.setSelectedIndex(idx);
					}
				});

		JPanel gombokPane = new JPanel();
		gombokPane.setBorder(BorderFactory.createTitledBorder("Workflow"));
		contentPane.add(gombokPane, BorderLayout.PAGE_START);
		GridBagLayout gbl_gombokPane = new GridBagLayout();

		gombokPane.setLayout(gbl_gombokPane);

		btnSetActiveImage = new JButton("Set Active Image");
		btnSetActiveImage.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				controller.setActiveImage();
				setTitle(controller.getTitle());

			}
		});

		GridBagConstraints gbc_btnSetActiveImage = new GridBagConstraints();
		gbc_btnSetActiveImage.fill = GridBagConstraints.BOTH;
		gbc_btnSetActiveImage.insets = new Insets(5, 15, 5, 15);
		gbc_btnSetActiveImage.gridx = 0;
		gbc_btnSetActiveImage.gridy = 0;
		gombokPane.add(btnSetActiveImage, gbc_btnSetActiveImage);

		
		isBlack = new JCheckBox("black background");
		isBlack.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.backgroundIsBlack(isBlack.isSelected());
				
			}
		});
		GridBagConstraints gbc_btnisBlack = new GridBagConstraints();
		gbc_btnisBlack.fill = GridBagConstraints.BOTH;
		gbc_btnisBlack.insets = new Insets(5, 15, 5, 15);
		gbc_btnisBlack.gridx = 0;
		gbc_btnisBlack.gridy = 1;
		gombokPane.add(isBlack, gbc_btnisBlack);
		
		btnThresholding = new JButton("Thresholding");
		btnThresholding.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.setThresholdFilter();
			}
		});

		GridBagConstraints gbc_btnThresholding = new GridBagConstraints();
		gbc_btnThresholding.fill = GridBagConstraints.BOTH;
		gbc_btnThresholding.insets = new Insets(5, 15, 5, 15);
		gbc_btnThresholding.gridx = 0;
		gbc_btnThresholding.gridy = 2;
		gombokPane.add(btnThresholding, gbc_btnThresholding);

		btnClusterAnalysing = new JButton("Cluster Analysing");
		btnClusterAnalysing.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.clusterDectecting();
			}
		});

		GridBagConstraints gbc_btnClusterAnalysing = new GridBagConstraints();
		gbc_btnClusterAnalysing.fill = GridBagConstraints.BOTH;
		gbc_btnClusterAnalysing.gridx = 0;
		gbc_btnClusterAnalysing.gridy = 3;
		gbc_btnClusterAnalysing.insets = new Insets(5, 15, 5, 15);
		gombokPane.add(btnClusterAnalysing, gbc_btnClusterAnalysing);

		JPanel thresholdPane = new JPanel();
		thresholdPane.setBorder(BorderFactory
				.createTitledBorder("Thresholding"));
		contentPane.add(thresholdPane, BorderLayout.CENTER);

		thresholdPane.setLayout(new BorderLayout(0, 0));

		JPanel thresholdFelsoPane = new JPanel();
		thresholdPane.add(thresholdFelsoPane, BorderLayout.PAGE_START);
		thresholdFelsoPane.setLayout(new BoxLayout(thresholdFelsoPane,
				BoxLayout.X_AXIS));
		scrollBar = new JScrollBar();
		thresholdFelsoPane.add(scrollBar);
		scrollBar.setMaximum(255);
		scrollBar.setOrientation(JScrollBar.HORIZONTAL);
		scrollBar.addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {	
				controller.setThresholdValue(lista.getSelectedIndex(),scrollBar.getValue());
				label.setText(scrollBar.getValue()+"");
			}
		});

		label = new JLabel("0");
		thresholdFelsoPane.add(label);

		JPanel thresholdAlsoPane = new JPanel();
		thresholdPane.add(thresholdAlsoPane, BorderLayout.PAGE_END);

		btnAuto = new JButton("Reset");
		btnAuto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.resetThresholdValue(lista.getSelectedIndex());
			}
		});
		thresholdAlsoPane.add(btnAuto);

		btnReset = new JButton("Interpolation");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.interpolThresholdValue(lista.getSelectedIndices());				
			}
		});
		thresholdAlsoPane.add(btnReset);

		lista = new JList(controller.getListaModel());
		lista.setCellRenderer(controller.getCellRenderer());
		lista.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // SINGLE_SELECTION
		lista.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				int idx = lista.getSelectedIndex();
				if (idx == -1)
					return;
				controller.listaSelectionChanged(idx);
				label.setText(lista.getSelectedValue() + "");
				scrollBar.setValue((Integer) lista.getSelectedValue());
			}
		});

		JScrollPane scrollPane = new JScrollPane(lista);
		thresholdPane.add(scrollPane, BorderLayout.CENTER);

		JPanel FilePane = new JPanel();
		FilePane.setBorder(BorderFactory.createTitledBorder("C3D files"));
		contentPane.add(FilePane, BorderLayout.PAGE_END);
		GridBagLayout gbl_FilePane = new GridBagLayout();
		FilePane.setLayout(gbl_FilePane);

		btnOpen = new JButton("Open");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.openC3D();
			}
		});
		GridBagConstraints gbc_btnOpen = new GridBagConstraints();
		gbc_btnOpen.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnOpen.insets = new Insets(0, 0, 5, 5);
		gbc_btnOpen.gridx = 0;
		gbc_btnOpen.gridy = 0;
		FilePane.add(btnOpen, gbc_btnOpen);

		btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.saveC3D();
			}
		});
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.anchor = GridBagConstraints.NORTHWEST;
		gbc_btnSave.insets = new Insets(0, 0, 5, 5);
		gbc_btnSave.gridx = 1;
		gbc_btnSave.gridy = 0;
		FilePane.add(btnSave, gbc_btnSave);

		btnMovie = new JButton("Movie");
		btnMovie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.makeMovie();
			}
		});
		GridBagConstraints gbc_btnMovie = new GridBagConstraints();
		gbc_btnMovie.insets = new Insets(0, 0, 0, 5);
		gbc_btnMovie.gridx = 1;
		gbc_btnMovie.gridy = 1;
		FilePane.add(btnMovie, gbc_btnMovie);

		btnExportTxt = new JButton("Export...");
		btnExportTxt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.exportTXT();
			}

		});
		GridBagConstraints gbc_btnExportTxt = new GridBagConstraints();
		gbc_btnExportTxt.anchor = GridBagConstraints.NORTH;
		gbc_btnExportTxt.gridx = 0;
		gbc_btnExportTxt.gridy = 1;
		FilePane.add(btnExportTxt, gbc_btnExportTxt);

	}

}
