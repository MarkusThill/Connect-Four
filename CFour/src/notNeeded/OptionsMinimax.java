package gui;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class OptionsMinimax extends Frame 
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
	
	//Other
	Label lSearchDepth;
	Choice cSearchDepth;
	
	
	public OptionsMinimax(int[] sizeTable) {
		super("Minimax-Parameters");			
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
		for (int i = 4;i<43;i++) 
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

		setLayout(new GridLayout(13,1,10,10));
		
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
		
		pack();
		setVisible(false);
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
		return cSearchDepth.getSelectedIndex()+4; 
	}
} 
