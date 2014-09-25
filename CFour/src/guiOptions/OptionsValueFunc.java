package guiOptions;

import gui.GridLayout2;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class OptionsValueFunc extends Frame {
	private static final long serialVersionUID = 1L;
	private Label lTitle;
	private Label lAgent;
	private Label lNumPieces;
	private Label lNumBoards;
	private Label ldeltaCorrect;
	private Label ldeltaCorrectRange;
	private Label lNotice;

	private JButton ok;
	private OptionsValueFunc m_par;

	private Checkbox cbUseDatabase;

	private TextField tNumPieces;
	private TextField tNumBoards;
	private TextField tdeltaCorrect;
	private TextField tdeltaCorrectRange;

	CheckboxGroup cbgAgent;
	Checkbox cbAgentX;
	Checkbox cbAgentO;
	Checkbox cbAgentEval;

	public OptionsValueFunc() {
		super("Parameters for Value-Function Test");
		setSize(120, 700);
		setBounds(0, 0, 120, 700);
		setLayout(new BorderLayout(10, 10));
		add(new Label(" "), BorderLayout.SOUTH);

		lTitle = new Label("Value-Function Test");
		lTitle.setFont(new Font("Times New Roman", Font.BOLD, 14));

		lAgent = new Label("Test Agent: ");

		cbgAgent = new CheckboxGroup();
		cbAgentX = new Checkbox("Agent X", cbgAgent, true);
		cbAgentX.setEnabled(true);
		cbAgentO = new Checkbox("Agent O", cbgAgent, true);
		cbAgentO.setEnabled(true);
		cbAgentEval = new Checkbox("Agent Eval", cbgAgent, true);
		cbAgentEval.setEnabled(true);

		lNumBoards = new Label("Number of Boards to test");
		tNumBoards = new TextField("10", 3);

		lNumPieces = new Label("How many Pieces? (-1: random) ");
		tNumPieces = new TextField("12", 3);

		ldeltaCorrect = new Label("Delta (Correct)");
		tdeltaCorrect = new TextField("0.2", 4);

		ldeltaCorrectRange = new Label("Delta (Correct-Range)");
		tdeltaCorrectRange = new TextField("0.5", 4);

		cbUseDatabase = new Checkbox("Take Positions out of Database (12-Ply)");
		cbUseDatabase.setState(false);
		
		lNotice = new Label("The Number of Pieces will always be 12!!!");

		ok = new JButton("OK");
		m_par = this;

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_par.setVisible(false);
			}
		});

		Panel p = new Panel();
		p.setLayout(new GridLayout2(9, 3, 5, 5));

		p.add(lTitle);
		p.add(new Canvas());
		p.add(new Canvas());
		
		p.add(lAgent);
		p.add(new Canvas());
		p.add(new Canvas());
		
		p.add(cbAgentX);
		p.add(cbAgentO);
		p.add(cbAgentEval);

		p.add(lNumBoards);
		p.add(tNumBoards);
		p.add(new Canvas());

		p.add(lNumPieces);
		p.add(tNumPieces);
		p.add(new Canvas());

		p.add(ldeltaCorrect);
		p.add(tdeltaCorrect);
		p.add(new Canvas());

		p.add(ldeltaCorrectRange);
		p.add(tdeltaCorrectRange);
		p.add(new Canvas());

		p.add(cbUseDatabase);
		p.add(lNotice);
		p.add(new Canvas());

		p.add(ok);

		add(p);

		pack();
		setVisible(false);
	}

	public boolean useDatabase() {
		return cbUseDatabase.getState();
	}

	public int getNumPieces() {
		return Integer.valueOf(tNumPieces.getText()).intValue();
	}

	public int getNumBoards() {
		return Integer.valueOf(tNumBoards.getText()).intValue();
	}
	
	public int getAgent() {
		if(cbAgentX.getState())
			return 0;
		if(cbAgentO.getState())
			return 1;
		return 2;
	}
	
	public double getDeltaCorrect() {
		return Double.valueOf(tdeltaCorrect.getText()).doubleValue();
	}
	
	public double getDeltaCorrectRange() {
		return Double.valueOf(tdeltaCorrectRange.getText()).doubleValue();
	}
}
