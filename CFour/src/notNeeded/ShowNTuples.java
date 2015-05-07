package gui;

import gui.C4Game.Action;
import gui.C4Game.State;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * Generates a summary of all N-Tuples. Single N-Tuples can be displayed on the
 * playing-board
 * 
 * @author Markus Thill
 * 
 */
public class ShowNTuples extends Frame {
	private static final long serialVersionUID = 1L;

	private JButton ok;
	private ShowNTuples m_par;
	private TextArea txt;
	private JButton bPrevNTuple;
	private JButton bNextNTuple;
	private C4Game m_game;

	int mcrIndex = 0;

	public ShowNTuples(C4Game game) {
		super("Show N-Tuples");
		m_game = game;
		setSize(120, 700);
		setBounds(0, 0, 120, 700);
		setLayout(new BorderLayout(10, 10));
		add(new Label(" "), BorderLayout.SOUTH);

		txt = new TextArea();
		txt.setFont(new Font("Lucida Console", 0, 14));

		ok = new JButton("OK");
		m_par = this;

		bPrevNTuple = new JButton("Prev N-Tuple");
		bNextNTuple = new JButton("Next N-Tuple");

		bPrevNTuple.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_game.action = Action.MOVEBACK;
				String str = "[Previous N-Tuple]";
				m_game.c4Buttons.printStatus(str);
			}
		});

		bNextNTuple.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_game.action = Action.NEXTMOVE;
				String str = "[Next N-Tuple]";
				m_game.c4Buttons.printStatus(str);
			}
		});

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_par.setVisible(false);
				m_game.changeState(State.IDLE);
			}
		});

		bPrevNTuple
				.setToolTipText("<html><body>Show the next N-Tuple for a N-Tuple-TD-Agent. This button "
						+ "doesn't work for other agent-types. The selected agent also must <br>"
						+ "be trained for this to work</body></html>");

		bNextNTuple
				.setToolTipText("<html><body>Show the previus N-Tuple for a N-Tuple-TD-Agent. This button "
						+ "doesn't work for other agent-types. The selected agent also must <br>"
						+ "be trained for this to work</body></html>");

		Panel p = new Panel();
		p.setLayout(new GridLayout2(2, 3, 5, 5));
		p.add(bPrevNTuple);
		p.add(bNextNTuple);
		p.add(new Canvas());

		add(p, BorderLayout.NORTH);

		add(txt);
		add(ok, BorderLayout.SOUTH);

		pack();
		setVisible(false);
	}

	public void setNTuples(String mcr) {
		txt.setText(mcr);
		txt.setCaretPosition(0);
		txt.setCaretPosition(25);
		txt.setSelectionStart(7);
		txt.setSelectionEnd(17);
		mcrIndex = 0;
	}
}
