package gui;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionsOther extends Frame {
	private static final long serialVersionUID = 1L;
	Label lSearchDepth;
	Choice cSearchDepth;
	Button ok;
	OptionsOther m_par;

	public OptionsOther() {
		super("Other Params");
		lSearchDepth = new Label("Max. Search-Depth");
		cSearchDepth = new Choice();
		for (int i = 4; i < 43; i++)
			cSearchDepth.add(i + "");
		cSearchDepth.select(42 + "");
		ok = new Button("OK");
		m_par = this;

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_par.setVisible(false);
			}
		});

		setLayout(new GridLayout(2, 0, 10, 10));

		add(lSearchDepth);
		add(new Canvas());
		add(cSearchDepth);

		add(ok);

		pack();
		setVisible(false);
	}

	public int getSearchDepth() {
		if (cSearchDepth.getSelectedIndex() > 37)
			return 100;
		return cSearchDepth.getSelectedIndex() + 4;
	}
}
