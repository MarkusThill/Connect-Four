package gui;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class OptionsDatabase extends Frame 
{
	private static final long serialVersionUID = 1L;
	Label ldatabases;
	
	Checkbox normalBook;
	Checkbox deepBook;
	Checkbox deepBookDist;
	Button ok;
	OptionsDatabase m_par;
	
	public OptionsDatabase() {
		super("Database");			
		ldatabases = new Label("Usage of Databases");
		
		normalBook = new Checkbox();
		normalBook.setLabel("Use normal Book (8-ply)");
		
		deepBook = new Checkbox();
		deepBook.setLabel("Use deep Book (12-ply)");
	
		deepBookDist = new Checkbox();
		deepBookDist.setLabel("Use deep Book (12-ply Exact-Distance)");
		
		normalBook.setState(true);
		deepBook.setState(true);
		deepBookDist.setState(false);
		
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

		setLayout(new GridLayout(5,1,10,10));
		
		add(ldatabases);
		add(normalBook);
		
		add(deepBook);
		add(deepBookDist);
		
		add(ok);
		
		pack();
		setVisible(false);
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
}
