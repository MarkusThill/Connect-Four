package gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import competition.Result;

/**
 * Allows to display different types of Results (implementing the Interface
 * Result), e.g. for singile/multi-competitions
 * 
 * @author Markus Thill
 * 
 */
public class ResultOverview extends Frame {
	private static final long serialVersionUID = 1L;

	private JButton ok;
	private JButton nextPage;
	private JButton prevPage;
	private ResultOverview m_par;
	private JTextArea txt;
	private Checkbox cbPrintBoards;

	Result mcr = null;
	int mcrIndex = 0;

	public ResultOverview() {
		super("Competition Results");
		setSize(120, 700);
		setBounds(0, 0, 120, 700);
		setLayout(new BorderLayout(10, 10));
		add(new Label(" "), BorderLayout.SOUTH);

		txt = new JTextArea();
		txt.setFont(new Font("Times New Roman", 0, 14));
		JScrollPane scrollPane = new JScrollPane(txt);

		cbPrintBoards = new Checkbox("Print Boards");

		nextPage = new JButton("Next Page");
		prevPage = new JButton("Prev Page");
		ok = new JButton("OK");
		m_par = this;

		nextPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mcr != null) {
					if (mcrIndex < mcr.getNum())
						mcrIndex++;
					txt.setText(mcr.getSingleResult(mcrIndex - 1,
							cbPrintBoards.getState()));
					txt.setCaretPosition(0);
				}
			}
		});

		prevPage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mcr != null) {
					if (mcrIndex > 0)
						mcrIndex--;
					if (mcrIndex == 0)
						txt.setText(mcr.getOverViewResult());
					else
						txt.setText(mcr.getSingleResult(mcrIndex - 1,
								cbPrintBoards.getState()));
					txt.setCaretPosition(0);
				}
			}
		});

		cbPrintBoards.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (mcr != null) {
					if (mcrIndex == 0)
						txt.setText(mcr.getOverViewResult());
					else
						txt.setText(mcr.getSingleResult(mcrIndex - 1,
								cbPrintBoards.getState()));
				}
				txt.setCaretPosition(0);
			}
		});

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_par.setVisible(false);
			}
		});

		Panel p = new Panel();
		p.setLayout(new GridLayout(2, 3, 5, 5));
		p.add(prevPage);
		p.add(new Canvas());
		p.add(nextPage);
		p.add(cbPrintBoards);
		p.add(new Canvas());
		p.add(new Canvas());

		add(p, BorderLayout.NORTH);

		add(scrollPane);
		add(ok, BorderLayout.SOUTH);

		setPreferredSize(new Dimension(500, 700));
		pack();
		setVisible(false);
	}

	public void setResult(Result mcr) {
		this.mcr = mcr;
		txt.setText(mcr.getOverViewResult());
		txt.setCaretPosition(0);
		mcrIndex = 0;
		txt.setFont(new Font("Courier New", 0, 14));
	}
}
