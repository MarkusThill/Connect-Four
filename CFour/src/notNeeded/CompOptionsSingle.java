package notNeeded;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompOptionsSingle extends Frame {
	private static final long serialVersionUID = 1L;
	private Label lsingleComp;
	private Label lPlayUntil;

	private Button ok;
	private CompOptionsSingle m_par;

	private Checkbox cbUseCurBoard;
	private Checkbox cbLogValues;

	private TextField tNumPieces;

	public CompOptionsSingle() {
		super("Competition-Parameters");
		setSize(120, 700);
		setBounds(0, 0, 120, 700);
		setLayout(new BorderLayout(10, 10));
		add(new Label(" "), BorderLayout.SOUTH); // just a little space at the
													// bottom

		lsingleComp = new Label("Single-Competition");
		lsingleComp.setFont(new Font("Times New Roman", Font.BOLD, 14));

		lPlayUntil = new Label("Stop Game after x Moves: ");

		ok = new Button("OK");
		m_par = this;

		cbUseCurBoard = new Checkbox("Use current Board");
		cbUseCurBoard.setState(true);

		cbLogValues = new Checkbox("Log Value-Tables of Opponents");
		cbLogValues.setState(true);

		tNumPieces = new TextField("42", 3);

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_par.setVisible(false);
			}
		});

		Panel p = new Panel();
		p.setLayout(new GridLayout(5, 2, 5, 5));

		p.add(lsingleComp);
		p.add(new Canvas());

		p.add(cbUseCurBoard);
		p.add(new Canvas());

		p.add(lPlayUntil);
		p.add(tNumPieces);

		p.add(cbLogValues);
		p.add(new Canvas());

		p.add(ok);

		add(p);

		pack();
		setVisible(false);
	}

	public boolean useCurBoard() {
		return cbUseCurBoard.getState();
	}

	public boolean logValues() {
		return cbLogValues.getState();
	}

	public int getNumPieces() {
		return Integer.valueOf(tNumPieces.getText()).intValue();
	}
}
