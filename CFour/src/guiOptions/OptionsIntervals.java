package guiOptions;

import gui.GridLayout2;

import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import javax.swing.*;

import nTupleTD.TDParams;
import nTupleTD.TDParams.GameInterval;


import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class OptionsIntervals extends JDialog {
	private static final long serialVersionUID = -563934103210279835L;

	// Only add those components as class variables, which are also needed in
	// other classes
	// ----------------------------------------------------------------------
	// Labels

	// Buttons
	JButton bApply;
	JButton bDeleteEntry;

	// Textfields
	final JTextField tStartInterval;
	final JTextField tStepBy;
	final JTextField tEndInterval;

	// Combo-Boxes
	final JComboBox<TDParams.GameInterval> cboxIntervals;

	/**
	 * Constructor:
	 */
	OptionsIntervals(JFrame parent, TDParams.GameInterval[] iList) {
		// Set title of this frame
		super(parent, "Measurement-Intervals");

		// -----------------------------------------------------------------
		// Generate Combobox, that contains all intervals
		// -----------------------------------------------------------------
		cboxIntervals = new JComboBox<TDParams.GameInterval>(iList);

		// -----------------------------------------------------------------
		// Generate "Create new Interval" Button
		// -----------------------------------------------------------------
		JButton bCreateNew = new JButton("Create new");
		bCreateNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Add new Element to ComboBox
				cboxIntervals.addItem(new TDParams.GameInterval(0, 10000, 0));
				cboxIntervals.setSelectedIndex(cboxIntervals.getItemCount() - 1);
			}
		});

		// -----------------------------------------------------------------
		// Create "Start of Interval" Label and Textfield
		// -----------------------------------------------------------------
		JLabel lStartInterval = new JLabel("Start of Interval:", JLabel.RIGHT);
		tStartInterval = new JTextField("0");

		// -----------------------------------------------------------------
		// Create "Step-Size" Label and Textfield
		// -----------------------------------------------------------------
		JLabel lStepBy = new JLabel("Step by:", JLabel.RIGHT);
		tStepBy = new JTextField("10000");

		// -----------------------------------------------------------------
		// Create "End of Interval" Label and Textfield
		// -----------------------------------------------------------------
		JLabel lEndInterval = new JLabel("End of Interval:", JLabel.RIGHT);
		tEndInterval = new JTextField("0");

		// -----------------------------------------------------------------
		// Generate "Delete Interval" Button
		// -----------------------------------------------------------------
		bDeleteEntry = new JButton("Delete");
		bDeleteEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Remove selected Item from ComboBox
				if (cboxIntervals.getItemCount() > 0) {
					int index = cboxIntervals.getSelectedIndex();
					cboxIntervals.removeItemAt(index);
				}
			}
		});

		// -----------------------------------------------------------------
		// Generate "Apply" Button
		// -----------------------------------------------------------------
		bApply = new JButton("Apply");
		bApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Save the values of the Textfields in the item of the combobox
				TDParams.GameInterval gi = (TDParams.GameInterval) cboxIntervals
						.getSelectedItem();
				// Get values from the Textfields
				int startInterval = getStartInterval();
				int stepBy = getStepBy();
				int endInterval = getEndInterval();

				// Save values in the corresponding item
				gi.startInterval = startInterval;
				gi.stepBy = stepBy;
				gi.endInterval = endInterval;

				// Refresh Combobox
				cboxIntervals.repaint();
			}

			private int getEndInterval() {
				double end = Double.valueOf(tEndInterval.getText())
						.doubleValue();
				int endInterval = (int) (end * TDParams.SCALE_GAME_BY_MILLION);
				return endInterval;
			}

			private int getStepBy() {
				double stepBy = Double.valueOf(tStepBy.getText()).doubleValue();
				return (int) (stepBy * TDParams.SCALE_GAME_BY_THOUSEND);
			}

			private int getStartInterval() {
				double start = Double.valueOf(tStartInterval.getText())
						.doubleValue();
				int startInterval = (int) (start * TDParams.SCALE_GAME_BY_MILLION);
				return startInterval;
			}
		});

		// -----------------------------------------------------------------
		// Create OK-Button
		// -----------------------------------------------------------------
		JButton bOK = new JButton("OK");
		bOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		cboxIntervals.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});

		// -----------------------------------------------------------------
		// Create Panel and add components to it
		JPanel p = new JPanel(new GridLayout2(0, 4, 10, 4));
		// -----------------------------------------------------------------

		p.add(cboxIntervals);
		p.add(bCreateNew);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(lStartInterval);
		p.add(tStartInterval);
		p.add(new JLabel("Games (in millions)", JLabel.LEFT));
		p.add(new Canvas());

		p.add(lStepBy);
		p.add(tStepBy);
		p.add(new JLabel("Games (in thousends)", JLabel.LEFT));
		p.add(new Canvas());

		p.add(lEndInterval);
		p.add(tEndInterval);
		p.add(new JLabel("Games (in millions)", JLabel.LEFT));
		p.add(new Canvas());

		p.add(new Canvas());
		p.add(new Canvas());
		p.add(bApply);
		p.add(bDeleteEntry);

		p.add(bOK);

		// -----------------------------------------------------------------
		// Add
		JScrollPane sp = new JScrollPane(p,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(sp);

		refresh();
		
		Point pos = parent.getLocation();
		setLocation(pos.x + 40, pos.y +40);

		setModal(true);
		pack();
		setVisible(false);
		setResizable(false);
	}

	private void refresh() {
		// Write the values of the selected item into the textfields
		TDParams.GameInterval gi = (TDParams.GameInterval) cboxIntervals
				.getSelectedItem();
		if (gi != null) {
			enableComponents(true);
			tStartInterval.setText(gi.startInterval
					/ TDParams.SCALE_GAME_BY_MILLION + "");
			tStepBy.setText(gi.stepBy / TDParams.SCALE_GAME_BY_THOUSEND + "");
			tEndInterval.setText(gi.endInterval
					/ TDParams.SCALE_GAME_BY_MILLION + "");
		} else {
			// Deactivate all Textfields
			enableComponents(false);
		}
	}

	private void enableComponents(boolean enabled) {
		tStartInterval.setEnabled(enabled);
		tStepBy.setEnabled(enabled);
		tEndInterval.setEnabled(enabled);
		bApply.setEnabled(enabled);
		bDeleteEntry.setEnabled(enabled);
	}

	public GameInterval[] getIntervalList() {
		ComboBoxModel<GameInterval> model = cboxIntervals.getModel();
		int size = model.getSize();
		GameInterval[] gi = new GameInterval[size];
		for(int i=0;i<size;i++)
			gi[i] = model.getElementAt(i);
		return gi;
	}

	private static class WindowClosingAdapter extends WindowAdapter {
		public WindowClosingAdapter() {
		}

		public void windowClosing(WindowEvent event) {
			event.getWindow().setVisible(false);
			event.getWindow().dispose();
			System.exit(0);
		}
	}

	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		} catch (Exception e) {
		}
		TDParams.GameInterval[] iList = new TDParams.GameInterval[1];
		iList[0] = new TDParams.GameInterval(0, 10000, 2000000);

		OptionsIntervals op = new OptionsIntervals(null, iList);
		op.addWindowListener(new WindowClosingAdapter());
		op.setVisible(true);
	}
}
