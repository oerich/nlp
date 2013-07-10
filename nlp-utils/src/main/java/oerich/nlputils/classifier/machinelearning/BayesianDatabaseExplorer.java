package oerich.nlputils.classifier.machinelearning;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import oerich.nlputils.NLPProperties;
import oerich.nlputils.text.StopWordFilterFactory;

public class BayesianDatabaseExplorer extends JFrame {

	private static final long serialVersionUID = 1L;
	private JSplitPane splitPane;
	private JTextArea textArea;
	private NewBayesianClassifier classifier;
	private JTable table;
	private JLabel[] indicators;
	private double[] steps = { 0.999, 0.98, 0.95, 0.9, 0.6, 0.4, 0.2, 0.1,
			0.05, 0.02 };
	private JPanel indicatorPanel;

	public BayesianDatabaseExplorer() {
		super("Bayesian Database Explorer");

		setLayout(new BorderLayout());

		this.textArea = new JTextArea();
		this.textArea.setWrapStyleWord(true);
		this.textArea.setLineWrap(true);

		try {
			this.classifier = new NewBayesianClassifier();
			this.classifier.setStopWordFilter(StopWordFilterFactory
					.getInstance());
			// this.classifier.setStemmer(new Stemmer());
			this.classifier.setProClassBias(1);
			// this.classifier.setUnknownWordValue(0.3);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JButton openButton = new JButton("open");
		openButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser fc = new JFileChooser(".");
				if (JFileChooser.CANCEL_OPTION == fc
						.showOpenDialog(BayesianDatabaseExplorer.this))
					return;
				try {
					BayesianDatabaseExplorer.this.classifier.init(fc
							.getSelectedFile());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		JButton classifyButton = new JButton("classify");
		classifyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				classify();
			}
		});

		JButton explainButton = new JButton("explain");
		explainButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateTable(BayesianDatabaseExplorer.this.textArea.getText());
			}
		});
		JButton showDBButton = new JButton("show db");
		showDBButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateTable(null);
			}
		});

		JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
		buttonPanel.add(openButton);
		buttonPanel.add(classifyButton);
		buttonPanel.add(explainButton);
		buttonPanel.add(showDBButton);

		this.table = new JTable();

		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				new JScrollPane(textArea), new JScrollPane(this.table));

		add(this.splitPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.WEST);
		add(getIndicatorPanel(), BorderLayout.EAST);
		pack();
		setVisible(true);
	}

	private JPanel getIndicatorPanel() {
		if (this.indicatorPanel == null) {
			this.indicatorPanel = new JPanel(new BorderLayout());
			this.indicatorPanel.add(new JLabel("clarification"),
					BorderLayout.NORTH);
			this.indicatorPanel.add(new JLabel("other"), BorderLayout.SOUTH);

			JLabel[] inds = updateIndicators(0.5);

			JPanel thermometer = new JPanel(new GridLayout(inds.length, 1));
			for (JLabel l : inds) {
				thermometer.add(l);
			}
			this.indicatorPanel.add(thermometer, BorderLayout.CENTER);
		}

		return this.indicatorPanel;
	}

	private void updateTable(String text) {
		TableModel model = this.classifier.explainClassification(text);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				model);
		this.table.setRowSorter(sorter);

		this.table.setModel(model);
	}

	private void classify() {
		updateIndicators(this.classifier.classify(this.textArea.getText()));
	}

	private JLabel[] updateIndicators(double val) {
		if (this.indicators == null) {
			this.indicators = new JLabel[this.steps.length + 1];
			double prev = 1.00;
			for (int i = 0; i < this.indicators.length; i++) {
				this.indicators[i] = new JLabel(getLowerBound(i) + " - " + prev);
				prev = getLowerBound(i);
			}
		}

		for (JLabel l : this.indicators) {
			l.setBorder(BorderFactory.createEmptyBorder());
		}

		this.indicators[indexForVal(val)].setBorder(BorderFactory
				.createLineBorder(Color.RED));

		return this.indicators;
	}

	private int indexForVal(double val) {
		for (int i = 0; i < this.steps.length; i++)
			if (val > this.steps[i])
				return i;
		return this.steps.length;
	}

	private double getLowerBound(int index) {
		if (index >= this.steps.length)
			return 0;
		return this.steps[index];
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NLPProperties.getInstance().setResourcePath("/");
		BayesianDatabaseExplorer bde = new BayesianDatabaseExplorer();

		bde.pack();

		bde.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
