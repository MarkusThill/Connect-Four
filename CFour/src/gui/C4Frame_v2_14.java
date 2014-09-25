package gui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.UIManager;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;



/**
 * Main-Frame
 * 
 * @author Markus Thill
 * 
 */
public class C4Frame_v2_14 extends JFrame {

	private static final long serialVersionUID = 1L;
	public static final String TITLE = "TD Learning for Connect Four";
	public static final String VERSION = "v2.14";
	
	private C4Game t_Game;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
		      UIManager.setLookAndFeel(new WindowsLookAndFeel());
		   } catch (Exception e) {}
		
//		Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
//		// Determine Font-Size-factor based on screen-Height
//		// Assume as basis 1080
//		float fontFactor = (float) (screenSize.getHeight() / 1080.0f);
//		System.out.println("ScreenSize: " + screenSize);
//		
//		for (Iterator i = UIManager.getLookAndFeelDefaults().keySet().iterator(); i.hasNext();) {
//		    String key = new String(i.next().toString());
//		    if(key.endsWith(".font")) {
//		    	Font font = UIManager.getFont(key);
//		    	Font biggerFont = font.deriveFont(fontFactor*font.getSize2D());
//		    	// change ui default to bigger font
//		    	UIManager.put(key,biggerFont);
//		    }
//		}
		
		final C4Frame_v2_14 t_Frame = new C4Frame_v2_14(TITLE + " " + VERSION);

		 t_Frame.init();

	}

	/**
	 * Initialize the frame and {@link #t_Game}.
	 */
	private void init() {
		addWindowListener(new WindowClosingAdapter());
		t_Game.init();
		setSize(950, 610);
		setBounds(5, 5, 950, 610);
		setVisible(true);
		setResizable(false);
	}

	private C4Frame_v2_14(String title) {
		super(title);
		t_Game = new C4Game(this);
		setLayout(new BorderLayout(10, 10));
		setJMenuBar(t_Game.c4Menu);
		add(t_Game, BorderLayout.CENTER);
		add(new Label(" "), BorderLayout.SOUTH);

		pack();
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
}