package guiOptions;

import gui.GridLayout2;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class OptionsMultiTrain extends Frame {
	private static final long serialVersionUID = 1L;
	private Label lsingleComp;
	private Label lMeasureInterval;
	private Label lNumTraining;
	private Label lNotUsed;
	private Label lTDSAgent;
	private Label lOpponent;
	private Label lNumMeasures;
	private Label lMeasureMethod;

	private JButton ok;
	private OptionsMultiTrain m_par;

	private Checkbox cbUseCurBoard;
	private Checkbox cbLogValues;
	private Checkbox cbswapPlayers;

	private TextField tMeasureInterval;
	private TextField tNumTraining;
	private TextField tNumMatches;

	private Choice cTDSPlayer;
	private Choice cOpponent;
	private Choice cMeasureMethod;

	public OptionsMultiTrain() {
		super("Multi-Train-Parameters");
		setSize(120, 700);
		setBounds(0, 0, 120, 700);
		setLayout(new BorderLayout(10, 10));
		add(new Label(" "), BorderLayout.SOUTH);

		lsingleComp = new Label("Multi-Train");
		lsingleComp.setFont(new Font("Times New Roman", Font.BOLD, 14));

		lNumTraining = new Label("Training-Number");
		tNumTraining = new TextField("10", 3);

		ok = new JButton("OK");
		m_par = this;

		cbUseCurBoard = new Checkbox("Use current Board");
		cbUseCurBoard.setState(true);

		cbLogValues = new Checkbox("Log Value-Tables of Opponents");
		cbLogValues.setState(false);
		cbLogValues.setEnabled(false);

		cbswapPlayers = new Checkbox(
				"Swap Opponents after each Match");
		cbswapPlayers.setState(false);

		lMeasureInterval = new Label("Measuring Interval:");
		tMeasureInterval = new TextField("10000", 3);

		lNumMeasures = new Label("Num. of Matches / Board");
		tNumMatches = new TextField("2", 3);

		lNotUsed = new Label("");
		lTDSAgent = new Label("TD-Agent");
		lOpponent = new Label("Opponent");
		
		cTDSPlayer = new Choice();
		cOpponent = new Choice();
		
		cTDSPlayer.add("Agent X");
		cTDSPlayer.add("Agent O");
		cTDSPlayer.add("Agent Eval");
		cOpponent.add("Agent X");
		cOpponent.add("Agent O");
		cOpponent.add("Agent Eval");

		cTDSPlayer.select(0);
		cOpponent.select(1);
		
		lMeasureMethod = new Label("Select Measuring-Method");
		cMeasureMethod = new Choice();
		cMeasureMethod.add("Simple Match");
		cMeasureMethod.add("Different initial Positions");

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_par.setVisible(false);
			}
		});

		Panel p = new Panel();
		p.setLayout(new GridLayout2(0, 2, 5, 5));

		p.add(lsingleComp);
		p.add(new Canvas());

		p.add(lNotUsed);
		p.add(new Canvas());

		p.add(lTDSAgent);
		p.add(lOpponent);

		p.add(cTDSPlayer);
		p.add(cOpponent);

		p.add(lNumTraining);
		p.add(tNumTraining);

		p.add(lMeasureInterval);
		p.add(tMeasureInterval);
		
		p.add(lNumMeasures);
		p.add(tNumMatches);
		
		p.add(lMeasureMethod);
		p.add(cMeasureMethod);
		
		p.add(cbUseCurBoard);
		p.add(new Canvas());
				
		p.add(cbLogValues);
		p.add(new Canvas());

		p.add(cbswapPlayers);
		p.add(new Canvas());

		p.add(ok);
		p.add(new Canvas());

		add(p);

		pack();
		setVisible(false);
	}

	public boolean swapPlayers() {
		return cbswapPlayers.getState();
	}

	public boolean useCurBoard() {
		return cbUseCurBoard.getState();
	}

	public boolean logValues() {
		return cbLogValues.getState();
	}

	public int getMeasureInterval() {
		return Integer.valueOf(tMeasureInterval.getText()).intValue();
	}

	public int getNumTrainingGames() {
		return Integer.valueOf(tNumTraining.getText()).intValue();
	}
	
	public int getNumMatches() {
		return Integer.valueOf(tNumMatches.getText()).intValue();
	}
	
	public int getMeasureMethod() {
		return cMeasureMethod.getSelectedIndex();
	}

	public int getTDSPlayer() {
		return cTDSPlayer.getSelectedIndex();
	}

	public int getOpponent() {
		return cOpponent.getSelectedIndex();
	}
}
