package gui;

import gui.ListOperation.Player;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import javax.swing.*;

/**
 * Seperate Window for the LUTShow-class. It displays all important values for
 * single board-positions
 * 
 * @author Markus Thill
 * 
 */
public class ShowValueList {

	protected JDialog dialog;
	private JScrollPane scroll;
	private JList<String[]> list;
	private DefaultListModel<String[]> listModel;
	private String[][] columnData;
	ListOperation listOp;

	// RadioButtons
	private ButtonGroup cbgPlayers;
	private JRadioButton cbPlayerX;
	private JRadioButton cbPlayerO;

	// Values and Indexes
	int indexes[][];
	double[][][] values;
	double[][] sumValues;
	double score[];
	double scoreTanh[];

	public ShowValueList(Window parent, ListOperation lo) {
		listOp = lo;

		// create the Dialog and JList JPanel
		dialog = new VDialog(parent);

		// create Renderer and dislpay
		list.setCellRenderer(new MyCellRenderer());
	}

	public int getSelectedIndex() {
		return list.getSelectedIndex();
	}

	public void setSelectedIndex(int index) {
		list.setSelectedIndex(index);
	}

	public void setValueList(ValueList vl) {
		indexes = vl.indexes;
		values = vl.values;
		sumValues = vl.sumValues;
		score = vl.score;
		scoreTanh = vl.scoreTanh;
		refreshList();
	}

	public void refreshList() {
		if (indexes != null && values != null) {
			// Two Lines more for Sum of the Scores
			int extraLines = 4;
			int lines = indexes.length + extraLines;
			int i;
			columnData = new String[lines][MyCellRenderer.COLCOUNT];

			int player = (cbPlayerX.isSelected() ? 0 : 1);
			String playerStr = (player == 0 ? "X." : "O.");
			String val0, val1;

			DecimalFormat df = new DecimalFormat("+0.00000;-0.00000");
			for (i = 0; i < lines - extraLines; i++) {
				val0 = df.format(values[player][i][0]);
				val1 = df.format(values[player][i][1]);
				columnData[i][0] = playerStr + i + "/" + (indexes.length - 1);
				columnData[i][1] = "" + indexes[i][0] + ":";
				columnData[i][2] = val0 + "";
				columnData[i][3] = "" + indexes[i][1] + ":";
				columnData[i][4] = val1 + "";
			}

			// Line
			columnData[i][0] = "-------------------";
			columnData[i][1] = "-------------------";
			columnData[i][2] = "-------------------";
			columnData[i][3] = "-------------------";
			columnData[i][4] = "-------------------";

			// Sum
			columnData[i + 1][0] = "SUM";
			columnData[i + 1][1] = "    ";
			columnData[i + 1][2] = df.format(sumValues[player][0]);
			columnData[i + 1][3] = "    ";
			columnData[i + 1][4] = df.format(sumValues[player][1]);

			// Score
			columnData[i + 2][0] = "SCORE";
			columnData[i + 2][1] = df.format(score[player]);
			columnData[i + 2][2] = "    ";
			columnData[i + 2][3] = "    ";
			columnData[i + 2][4] = "    ";

			// Score Tanh
			columnData[i + 3][0] = "SCORE (Tanh)";
			columnData[i + 3][1] = df.format(scoreTanh[player]);
			columnData[i + 3][2] = "    ";
			columnData[i + 3][3] = "    ";
			columnData[i + 3][4] = "    ";

			// columnData[indexes.length][0]
			list.setListData(columnData);
			list.setSelectedIndex(-1);
			list.ensureIndexIsVisible(-1);
			list.setFont(new Font("Arial", 1, 12));
		}
	}

	public void setPlayer(Player player) {
		if (player == Player.O)
			cbPlayerO.setSelected(true);
		else
			cbPlayerX.setSelected(true);
	}

	private class VDialog extends JDialog {
		private static final long serialVersionUID = 1L;

		// Labels
		private JLabel lTitle;
		private JLabel lTupleNr;
		private JLabel lScore1;
		private JLabel lScore2;
		private JLabel lIndex1;
		private JLabel lIndex2;

		public VDialog(Window parent) {
			super(parent, "Values for all N-Tuples");
			setLayout(new BorderLayout(10, 10));

			// -------------------------------------------------------------
			// Init Elements
			// -------------------------------------------------------------
			lTitle = new JLabel("Values for all N-Tuples (for current Board)",
					JLabel.CENTER);
			lTitle.setFont(new Font("Times New Roman", 1, 20));

			cbgPlayers = new ButtonGroup();
			cbPlayerX = new JRadioButton("Player X");
			cbPlayerO = new JRadioButton("Player O");
			cbPlayerX.setSelected(true);
			cbgPlayers.add(cbPlayerX);
			cbgPlayers.add(cbPlayerO);

			lTupleNr = new JLabel("Tuple-Nr.", JLabel.CENTER);
			lScore1 = new JLabel("Score", JLabel.CENTER);
			lScore2 = new JLabel("Score (mir.)", JLabel.CENTER);
			lIndex1 = new JLabel("Index", JLabel.CENTER);
			lIndex2 = new JLabel("Index (mir.)", JLabel.CENTER);

			// -------------------------------------------------------------
			// Add Action-Listeners
			// -------------------------------------------------------------

			cbPlayerX.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					if (cbPlayerX.isSelected()) {
						refreshList();
						listOp.playerChanged(Player.X);
					}
				}
			});

			cbPlayerO.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent arg0) {
					if (cbPlayerO.isSelected()) {
						refreshList();
						listOp.playerChanged(Player.O);
					}
				}
			});

			// -------------------------------------------------------------
			// Add Elements to Table-Title-Panel
			// -------------------------------------------------------------
			JPanel pTableTitle = new JPanel(new GridLayout2(0, 11, 5, 5));
			pTableTitle.add(lTupleNr);
			pTableTitle.add(new Label(""));
			pTableTitle.add(lIndex1);
			pTableTitle.add(new Label(""));
			pTableTitle.add(new Label(""));
			pTableTitle.add(lScore1);
			pTableTitle.add(new Label(""));
			pTableTitle.add(new Label(""));
			pTableTitle.add(lIndex2);
			pTableTitle.add(new Label(""));
			pTableTitle.add(lScore2);

			// -------------------------------------------------------------
			// Add Elements for Top of the Window
			// -------------------------------------------------------------
			JPanel pRadio = new JPanel(new GridLayout2(0, 5, 5, 5));
			pRadio.add(cbPlayerX);
			pRadio.add(cbPlayerO);
			pRadio.add(new Canvas());
			pRadio.add(new Canvas());
			pRadio.add(new Canvas());
			JPanel pWindowNorth = new JPanel(new GridLayout2(0, 1, 5, 5));
			pWindowNorth.add(lTitle);
			pWindowNorth.add(new JLabel(" "));
			pWindowNorth.add(new JLabel(" "));
			pWindowNorth.add(pRadio);
			pWindowNorth.add(pTableTitle);

			// -------------------------------------------------------------
			// Add Elements to Window
			// -------------------------------------------------------------
			add(pWindowNorth, BorderLayout.NORTH);
			add(new PanelBuilder(), BorderLayout.CENTER);

			// -------------------------------------------------------------
			// Display rules
			// -------------------------------------------------------------
			setResizable(true);
			setLocation(250, 50);
			setBackground(Color.lightGray);
			setDefaultCloseOperation(HIDE_ON_CLOSE);
			setSize(400, 600);
			setVisible(true);
		}

		private class PanelBuilder extends JPanel {
			private static final long serialVersionUID = 6317304873766041971L;

			public PanelBuilder() {
				GridBagLayout layout = new GridBagLayout();
				GridBagConstraints layoutConstraints = new GridBagConstraints();
				setLayout(layout);

				scroll = new JScrollPane();
				listModel = new DefaultListModel<String[]>();
				list = new JList<String[]>(listModel);
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				list.setLayoutOrientation(JList.VERTICAL);
				list.setVisibleRowCount(5);
				list.setSelectedIndex(-1);
				list.ensureIndexIsVisible(-1);

				layoutConstraints.gridx = 0;
				layoutConstraints.gridy = 0;
				layoutConstraints.gridwidth = 1;
				layoutConstraints.gridheight = 1;
				layoutConstraints.fill = GridBagConstraints.BOTH;
				layoutConstraints.insets = new Insets(1, 1, 1, 1);
				layoutConstraints.anchor = GridBagConstraints.CENTER;
				layoutConstraints.weightx = 1.0;
				layoutConstraints.weighty = 1.0;
				scroll = new JScrollPane(list,
						JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				layout.setConstraints(scroll, layoutConstraints);
				add(scroll);

				list.addMouseListener(new MouseListener() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						listOp.indexChanged(list.getSelectedIndex());
						Player player = (cbPlayerX.isSelected() ? Player.X
								: Player.O);
						listOp.playerChanged(player);
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
					}

					@Override
					public void mousePressed(MouseEvent arg0) {
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
					}

				});
			}
		}

	}

	private static class MyCellRenderer extends JPanel implements
			ListCellRenderer<Object> {
		private static final long serialVersionUID = 1L;
		private static final int COLCOUNT = 5;
		JLabel[] cols = new JLabel[COLCOUNT];

		MyCellRenderer() {
			setLayout(new GridLayout(1, COLCOUNT));
			for (int i = 0; i < cols.length; i++) {
				cols[i] = new JLabel("", JLabel.LEFT);
				cols[i].setOpaque(true);
				add(cols[i]);
			}
		}

		public Component getListCellRendererComponent(JList<?> list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			for (int i = 0; i < cols.length; i++) {
				String data = ((String[]) value)[i];
				cols[i].setText(data);
				if (isSelected) {
					cols[i].setBackground(list.getSelectionBackground());
					cols[i].setForeground(list.getSelectionForeground());
				} else {
					cols[i].setBackground(list.getBackground());
					cols[i].setForeground(list.getForeground());
				}
			}
			setEnabled(list.isEnabled());
			setFont(list.getFont());
			return this;
		}
	}
}
