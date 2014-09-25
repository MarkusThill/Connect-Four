package gui;

// MessageBox - source from Dr. Kirsh site

import java.awt.*;
import java.awt.event.*;

/**
 * @author Dr. Kirsh
 *
 */
public class MessageBox extends Frame {
	private static final long serialVersionUID = 1L;

	public MessageBox(String message, String title) {
		addWindowListener(new ClosingHandler());
		addKeyListener(new EscHandler());
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.lightGray);

		Label label = new Label(message, Label.CENTER);
		label.setFont(new Font("SansSerif", Font.BOLD, 12));
		label.addKeyListener(new EscHandler());
		add(label, BorderLayout.CENTER);
		Panel buttonsPanel = new Panel();
		buttonsPanel.addKeyListener(new EscHandler());
		Button ok = new Button("OK");
		buttonsPanel.add(ok);
		ok.addActionListener(new OkHandler());
		ok.addKeyListener(new EscHandler());
		add(buttonsPanel, BorderLayout.SOUTH);
		this.setTitle(title);

		pack(); // take minimum necessary size.
		this.setLocation(400, 300);
		this.setVisible(true);
	}

	class ClosingHandler extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dispose();
		}
	}

	class EscHandler extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				dispose();
		}
	}

	class OkHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}
}
