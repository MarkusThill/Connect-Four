package guiOptions;

import gui.GridLayout2;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Font;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JFrame;


public class OptionsMinimax extends JFrame 
{
	private static final long serialVersionUID = 1L;
	Label lTransPosSize;
	Choice cTransPosSize;
	Button ok;
	OptionsMinimax m_par;
	
	//Database
	Label ldatabases;
	Checkbox normalBook;
	Checkbox deepBook;
	Checkbox deepBookDist;
	Checkbox randomizeEqualMoves;
	final Checkbox cbUsePresetting;
	
	//Other
	Label lSearchDepth;
	Choice cSearchDepth;
	
	
	public OptionsMinimax(int[] sizeTable) {
		super("Minimax-Parameters");			
		
		cbUsePresetting = new Checkbox("Use Presetting");
		cbUsePresetting.setState(true);
		
		lTransPosSize = new Label("Size of Transposition-table (in MB)");
		lTransPosSize.setFont(new Font("Times New Roman", Font.BOLD, 14));
		cTransPosSize = new Choice();
		for (int s : sizeTable) cTransPosSize.add((double)s / (1024*1024)+"");
		cTransPosSize.select((double)sizeTable[3] / (1024*1024)+"");
		
		//Database
		ldatabases = new Label("Usage of Databases");
		ldatabases.setFont(new Font("Times New Roman", Font.BOLD, 14));
		normalBook = new Checkbox();
		normalBook.setLabel("Use normal Book (8-ply)");
		
		deepBook = new Checkbox();
		deepBook.setLabel("Use deep Book (12-ply)");
		
		deepBookDist = new Checkbox();
		deepBookDist.setLabel("Use deep Book (12-ply Exact Distance)");
		
		normalBook.setState(true);
		deepBook.setState(false);
		deepBookDist.setState(true);
		
		//Other
		lSearchDepth = new Label("Max. Search-Depth");
		lSearchDepth.setFont(new Font("Times New Roman", Font.BOLD, 14));
		cSearchDepth = new Choice();
		for (int i = 2;i<43;i++) 
			cSearchDepth.add(i+"");
		cSearchDepth.select(42+"");
		
		randomizeEqualMoves = new Checkbox("Randomize, if Equal Moves");
		randomizeEqualMoves.setState(true);
		
		ok = new Button("OK");
		m_par = this;
		//
		
		ok.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						m_par.setVisible(false);
					}
				}					
		);
		
		cbUsePresetting.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				handlePresetting(!cbUsePresetting.getState());
			}
		});

		setLayout(new GridLayout2(13,1,10,10));
		
		add (cbUsePresetting);
		//Transposition-Table
		add(lTransPosSize);
		add(cTransPosSize);
		add(new Canvas());
		
		//Database
		add(ldatabases);
		add(normalBook);
		add(deepBook);
		add(deepBookDist);
		
		//Other
		add(lSearchDepth);
		add(cSearchDepth);
		add(new Canvas());
		
		add(randomizeEqualMoves);
		
		add(ok);
		
		 handlePresetting(!cbUsePresetting.getState());
		
		pack();
		setVisible(false);
	} 	
	
	private void handlePresetting(boolean state) {
		cTransPosSize.setEnabled(state);
		normalBook.setEnabled(state);
		deepBook.setEnabled(state);
		deepBookDist.setEnabled(state);
		randomizeEqualMoves.setEnabled(state);
		cSearchDepth.setEnabled(state);
	}
	
	public boolean usePresetting() {
		return cbUsePresetting.getState();
	}
	
	public int getTableIndex() {
		return cTransPosSize.getSelectedIndex(); 
	}
	
	public boolean useNormalBook() {
		return normalBook.getState();
	}
	
	public boolean useDeepBook() {
		return deepBook.getState();
	}
	
	public boolean useDeepBookDist() {
		return deepBookDist.getState();
	}
	
	public boolean randomizeEqualMoves() {
		return randomizeEqualMoves.getState();
	}
	
	public int getSearchDepth() {
		if(cSearchDepth.getSelectedIndex()>37)
			return 100;
		return cSearchDepth.getSelectedIndex(); 
	}
} 
