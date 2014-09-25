package gui;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class OptionsTransPosTable extends Frame 
{
	private static final long serialVersionUID = 1L;
	Label lTransPosSize;
	Choice cTransPosSize;
	Button ok;
	OptionsTransPosTable m_par;
	
	public OptionsTransPosTable(int[] sizeTable) {
		super("Transposition-Table");			
		lTransPosSize = new Label("Size of Transposition-table (in MB)");
		cTransPosSize = new Choice();
		for (int s : sizeTable) cTransPosSize.add((double)s / (1024*1024)+"");
		cTransPosSize.select((double)sizeTable[3] / (1024*1024)+"");
		ok = new Button("OK");
		m_par = this;
		
		ok.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						m_par.setVisible(false);
					}
				}					
		);

		setLayout(new GridLayout(2,0,10,10));
		
		add(lTransPosSize);
		add(new Canvas());
		add(cTransPosSize);
		
		add(ok);
		
		pack();
		setVisible(false);
	} 	
	
	public int getTableIndex() {
		return cTransPosSize.getSelectedIndex(); 
	}
} 
