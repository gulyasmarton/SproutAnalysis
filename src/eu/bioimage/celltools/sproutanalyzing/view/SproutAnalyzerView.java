package eu.bioimage.celltools.sproutanalyzing.view;



import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.GridBagLayout;

import javax.swing.JButton;

import eu.bioimage.celltools.sproutanalyzing.controller.SproutAnalyzerController;
import eu.bioimage.celltools.sproutanalyzing.controller.SproutAnalyzerListener;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;

import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;

public class SproutAnalyzerView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -106251589092484609L;
	private JPanel contentPane;
	JSpinner spinner_from;
	JSpinner spinner_distance;

	/**
	 * Create the frame.
	 */
	public SproutAnalyzerView(final SproutAnalyzerController controller) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 273, 434);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWeights = new double[] { 1.0 };
		// gbl_contentPane.columnWidths = new int[]{0, 0};
		gbl_contentPane.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		// gbl_contentPane.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0, 1.0, 0.0, 0.0 };
		contentPane.setLayout(gbl_contentPane);

		JButton btnOpenCd = new JButton("Open C3D");
		btnOpenCd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				controller.LoadC3D();
			}
		});
		GridBagConstraints gbc_btnOpenCd = new GridBagConstraints();
		gbc_btnOpenCd.insets = new Insets(0, 0, 5, 0);
		gbc_btnOpenCd.gridx = 0;
		gbc_btnOpenCd.gridy = 0;
		contentPane.add(btnOpenCd, gbc_btnOpenCd);

		JButton btnSetCircles = new JButton("Set circles");
		btnSetCircles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.setCircles();
			}

		});
		GridBagConstraints gbc_btnSetCircles = new GridBagConstraints();
		gbc_btnSetCircles.insets = new Insets(0, 0, 5, 0);
		gbc_btnSetCircles.gridx = 0;
		gbc_btnSetCircles.gridy = 3;
		contentPane.add(btnSetCircles, gbc_btnSetCircles);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		contentPane.add(panel, gbc_panel);

		JLabel lblMaxDistance = new JLabel("From center (um):");
		panel.add(lblMaxDistance);

		spinner_from = new JSpinner();
		spinner_from.setModel(new SpinnerNumberModel(new Integer(700), null,
				null, new Integer(20)));
		spinner_from.setValue(700);
		spinner_from.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				controller.setFromCenter((Integer) spinner_from.getValue());
				controller.setCircles();
			}
		});
		panel.add(spinner_from);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		contentPane.add(panel_1, gbc_panel_1);

		JLabel lblDistanceum = new JLabel("Distance (um):");
		panel_1.add(lblDistanceum);

		spinner_distance = new JSpinner();
		spinner_distance.setValue(20);
		spinner_distance.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				controller.setDistance((Integer) spinner_distance.getValue());
			}
		});
		panel_1.add(spinner_distance);

		JButton btnShowCylinders = new JButton("Show cylinders");
		btnShowCylinders.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.showCylinders();
			}
		});

		JPanel panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.insets = new Insets(0, 0, 5, 0);
		gbc_panel_4.fill = GridBagConstraints.BOTH;
		gbc_panel_4.gridx = 0;
		gbc_panel_4.gridy = 4;
		contentPane.add(panel_4, gbc_panel_4);

		JButton btnSetMask = new JButton("set mask");
		btnSetMask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.setMask();
			}
		});
		panel_4.add(btnSetMask);

		JButton btnRemoveMask = new JButton("remove mask");
		btnRemoveMask.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.removeMask();
				controller.clearMask();
			}
		});
		panel_4.add(btnRemoveMask);
		GridBagConstraints gbc_btnShowCylinders = new GridBagConstraints();
		gbc_btnShowCylinders.insets = new Insets(0, 0, 5, 0);
		gbc_btnShowCylinders.gridx = 0;
		gbc_btnShowCylinders.gridy = 5;
		contentPane.add(btnShowCylinders, gbc_btnShowCylinders);

		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 6;
		contentPane.add(panel_2, gbc_panel_2);

		JButton btnOpenSpr = new JButton("Open SPR");
		btnOpenSpr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.OpenSPR();
			}
		});
		panel_2.add(btnOpenSpr);

		JButton btnSaveSpr = new JButton("Save SPR");
		btnSaveSpr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.saveSPRdialog();
			}
		});
		panel_2.add(btnSaveSpr);

		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 7;
		contentPane.add(panel_3, gbc_panel_3);

		JButton btnExportTxt = new JButton("Export txt");
		btnExportTxt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.SaveTextDiagol();
			}
		});
		panel_3.add(btnExportTxt);

		JButton btnSaveAll = new JButton("Save All");
		btnSaveAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.SaveAll();
			}
		});
		panel_3.add(btnSaveAll);

		JButton btnSaveAllAs = new JButton("Save All As");
		btnSaveAllAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.SaveAllAs();
			}
		});
		panel_3.add(btnSaveAllAs);

		JButton btnNewButton = new JButton("OpenZVI");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.openZVI();
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 8;
		contentPane.add(btnNewButton, gbc_btnNewButton);

		JButton btnCopyRoi = new JButton("Copy Roi");
		btnCopyRoi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.copyRoi();				
			}
		});
		GridBagConstraints gbc_btnCopyRoi = new GridBagConstraints();
		gbc_btnCopyRoi.gridx = 0;
		gbc_btnCopyRoi.gridy = 9;
		contentPane.add(btnCopyRoi, gbc_btnCopyRoi);

		controller.addSproutAnalyzerListener(new SproutAnalyzerListener() {

			@Override
			public void changedFromCenter(int value) {
				spinner_from.setValue(value);

			}

			@Override
			public void changedDistance(int value) {
				spinner_distance.setValue(value);
			}
		});
	}

}
