package guiOptions;

import gui.GridLayout2;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;
import mcts.*;
import mcts.MCTSParams.ActionSelection;
import mcts.MCTSParams.ConstraintType;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class OptionsMCTS extends JFrame {
	private static final int TITLEFONTSIZE = 20;
	private static final long serialVersionUID = 1L;

	// Contraint-Type: or time-limit or iteration-limit
	final JLabel lConstraintType = new JLabel("Constraint-Type");
	final ButtonGroup bgConstraint = new ButtonGroup();
	final JRadioButton rbConstraintTime = new JRadioButton("Time Constraint");
	final JRadioButton rbConstraintIteration = new JRadioButton(
			"Iteration Constraint");

	// Constraint-Value
	final JFormattedTextField tConstraintValue;

	// Select most visited action or best action?
	final JLabel lSelectAction = new JLabel("Select Action");
	final JRadioButton rbSelectMostVisited = new JRadioButton("Most Visited");
	final JRadioButton rbSelectBestValue = new JRadioButton("Best Value");
	final ButtonGroup bgSelectionAction = new ButtonGroup();

	// UCB-Constant
	final JFormattedTextField tUCBConstant;

	// Try to find sub-tree of previous MCTS-Search
	final JCheckBox cbUseOldSubtree = new JCheckBox("Use subtree of prev. search");
	
	// Temporary MCTSParams, in case the user cancels
	private MCTSParams mctsTempPar;

	public OptionsMCTS(MCTSParams par) {
		super("MCTS Parameters");

		// General Section
		final Font fontTitle = new Font("Serif", Font.BOLD, TITLEFONTSIZE);
		final JLabel lSectionGeneral = new JLabel("General");
		lSectionGeneral.setFont(fontTitle);

		// Contraint-Type: time-limit or iteration-limit
		bgConstraint.add(rbConstraintTime);
		bgConstraint.add(rbConstraintIteration);
		bgConstraint.setSelected(rbConstraintIteration.getModel(), true);

		// Text-field for the Constraint
		final JLabel lConstraintValue = new JLabel("Constraint-Value",
				JLabel.RIGHT);
		tConstraintValue = new JFormattedTextField();
		final JLabel lConstraintUnit = new JLabel("iterations");

		// Select most visited action or best action?
		bgSelectionAction.add(rbSelectMostVisited);
		bgSelectionAction.add(rbSelectBestValue);
		bgSelectionAction.setSelected(rbSelectMostVisited.getModel(), true);

		// Constant for UCB
		final JLabel lUCBConstant = new JLabel("C (UCB)", JLabel.RIGHT);

		tUCBConstant = new JFormattedTextField();

		// Button Load Params
		final JButton bLoad = new JButton("Load");

		// Button Save Params
		final JButton bSave = new JButton("Save");

		// Button Save Params

		// Button Apply
		final JButton bApply = new JButton("Apply");

		// Button Cancel
		final JButton bCancel = new JButton("Cancel");

		rbConstraintIteration.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Set Constraint-Unit to Iterations
				lConstraintUnit.setText("iterations");
			}
		});

		rbConstraintTime.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Set Constraint-Unit to Iterations
				lConstraintUnit.setText("seconds");
			}
		});
		
		bApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				mctsTempPar = null;
			}
		});
		
		bCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setMCTSParams(mctsTempPar);
				setVisible(false);
			}
		});
		
		addWindowListener(new WindowClosing(this));

		// Create panel
		// JPanel
		JPanel p = new JPanel(new GridLayout2(0, 4, 10, 4));
		p.add(lSectionGeneral);
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(lSelectAction);
		p.add(rbSelectMostVisited);
		p.add(rbSelectBestValue);
		p.add(new Canvas());

		p.add(lConstraintType);
		p.add(rbConstraintTime);
		p.add(rbConstraintIteration);
		p.add(new Canvas());

		p.add(lConstraintValue);
		p.add(tConstraintValue);
		p.add(lConstraintUnit);
		p.add(new Canvas());

		p.add(lUCBConstant);
		p.add(tUCBConstant);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(new Canvas());
		p.add(cbUseOldSubtree);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(bLoad);
		p.add(bSave);
		p.add(bApply);
		p.add(bCancel);

		JScrollPane sp = new JScrollPane(p,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(sp);

		// Set the predifined params
		setMCTSParams(par);

		setResizable(false);
		pack();
	}

	private ConstraintType getConstraintType() {
		if (rbConstraintIteration.isSelected())
			return ConstraintType.ITERATION;
		return ConstraintType.TIME;
	}

	private double getConstraintValue() {
		return Double.valueOf(tConstraintValue.getText()).doubleValue();
	}

	private ActionSelection getActionSelection() {
		return rbSelectBestValue.isSelected() ? ActionSelection.BESTVALUE
				: ActionSelection.MOSTVISITED;
	}

	private double getUCBConstant() {
		return Double.valueOf(tUCBConstant.getText()).doubleValue();
	}

	private boolean getUseOldSubtree() {
		return cbUseOldSubtree.isSelected();
	}

	public MCTSParams getMCTSParams() {
		MCTSParams mctsPar = new MCTSParams();
		mctsPar.constraintType = getConstraintType();
		mctsPar.constraintValue = getConstraintValue();
		mctsPar.actionSelection = getActionSelection();
		mctsPar.ucbConstant = getUCBConstant();
		mctsPar.useOldSubtree = getUseOldSubtree();
		return mctsPar;

	}
	
	@Override
	public void setVisible(boolean value) {
		if(value) {
			mctsTempPar = getMCTSParams();
		}
		super.setVisible(value);
	}

	public void setMCTSParams(MCTSParams par) {
		// Constraint-Type
		switch (par.constraintType) {
		case ITERATION:
			rbConstraintIteration.setSelected(true);
			break;
		case TIME:
			rbConstraintTime.setSelected(true);
			break;
		default:
			throw new UnsupportedOperationException("Not supported yet!");
		}

		// Constraint Value
		tConstraintValue.setText(par.constraintValue + "");

		// Action selection based on...
		switch (par.actionSelection) {
		case BESTVALUE:
			rbSelectBestValue.setSelected(true);
			break;
		case MOSTVISITED:
			rbSelectMostVisited.setSelected(true);
			break;
		default:
			throw new UnsupportedOperationException("Not supported yet!");
		}

		// UCB Constant
		tUCBConstant.setText(par.ucbConstant + "");
		
		// Use old subtree
		cbUseOldSubtree.setSelected(par.useOldSubtree);
	}
	
	private static class WindowClosing extends WindowAdapter {
		OptionsMCTS window;
		WindowClosing(OptionsMCTS window) {
			this.window = window;
		}
		
		public void windowClosing(WindowEvent event) {
			window.setMCTSParams(window.mctsTempPar); // Discard changes
			event.getWindow().setVisible(false);
		}
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

		OptionsMCTS op = null;

		op = new OptionsMCTS(new MCTSParams());
		op.addWindowListener(new WindowClosingAdapter());
		op.setVisible(true);
	}

}
