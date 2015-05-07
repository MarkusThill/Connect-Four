package gui;

import gui.C4Game.Action;
import gui.C4Game.State;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ConfigTuples extends Frame {
	private static final long serialVersionUID = 1L;

	private C4Game m_game;
	private DefaultListModel listModel;
	private JList lTupleList;
	//private JScrollPane listScroller;
	private JButton ok;
	private JButton bDelete;
	private JButton bNewTuple;
	//private JButton bSave;
	private JButton bDiscard;
	private ConfigTuples m_par;

	public ConfigTuples(C4Game game) {
		super("N-Tuple Configuration");
		
		m_game = game;

		bDelete = new JButton("Delete");
		bNewTuple = new JButton("New");
		bDiscard = new JButton("Discard");
		//bSave = new JButton("Save");
		
		// ==========================================================
		listModel = new DefaultListModel();
		//listModel.addElement("Jane Doe");
		//listModel.addElement("John Smith");
		//listModel.addElement("Kathy Green");

		// ====================================
		lTupleList = new JList(listModel);
		lTupleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lTupleList.setLayoutOrientation(JList.VERTICAL);
		lTupleList.setVisibleRowCount(5);
		//listScroller = new JScrollPane(lTupleList);
		//listScroller.setPreferredSize(new Dimension(250, 80));
		//=====================================
		JScrollPane listScrollPane = new JScrollPane(lTupleList);
		
		ok = new JButton("Save");
		m_par = this;

		// ==========================================================================
		lTupleList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {

					if (lTupleList.getSelectedIndex() == -1) {
						// No selection, disable fire button.
						// fireButton.setEnabled(false);

					} else {
						// Selection, enable the fire button.
						// fireButton.setEnabled(true);
					}
				}
			}
		});

		bDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = lTupleList.getSelectedIndex();
				if (index >= 0) {
					listModel.remove(index);
					m_game.nTupleList.remove(index);
					m_game.action = Action.DELETE;
				}

				int size = listModel.getSize();

				if (size == 0) { // Nobody's left, disable delete.
					 bDelete.setEnabled(false);

				} else if (index >= 0){ // Select an index.
					if (index == listModel.getSize()) {
						// removed item in last position
						index--;
					}

					lTupleList.setSelectedIndex(index);
					lTupleList.ensureIndexIsVisible(index);
				}
			}
		});
		
		bNewTuple.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    String name = "[]";

			    int index = lTupleList.getSelectedIndex(); //get selected index
			    if (index == -1) { //no selection, so insert at beginning
			        index = 0;
			    } else {           //add after the selected item
			        index++;
			    }
			    bDelete.setEnabled(true);
			    
			    m_game.nTupleList.add(index, new ArrayList<Integer>());

			    listModel.insertElementAt(name, index);

			    //Select the new item and make it visible.
			    lTupleList.setSelectedIndex(index);
			    lTupleList.ensureIndexIsVisible(index);
			}
		});

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[][] tp = m_game.getChangedNTuples();
//				int index = m_game.m_tgb.cChooseShow.getSelectedIndex();
//				OptionsTD cf = (OptionsTD) m_game.params[index];
//				cf.setNTuples(tp);
				m_par.setVisible(false);
				m_game.changeState(State.IDLE);
			}
		});
		
		bDiscard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_par.setVisible(false);
				m_game.changeState(State.IDLE);
			}
		});
		
		// ==========================================================================
		
		setLayout(new BorderLayout());
		Panel p = new Panel();
		p.setLayout(new GridLayout(2,2,5,5));
		p.add(bDelete);
		//p.add(bSave);
		p.add(bNewTuple);
		p.add(bDiscard);
		p.add(ok);

		add(listScrollPane, BorderLayout.CENTER);
        add(p, BorderLayout.PAGE_END);
        
        //setSize(getPreferredSize());
        setSize(300,500);
		setVisible(false);
	}
	
	public void setNTuples(String[] tuples) {
		listModel.removeAllElements();
		for(int i=0;i<tuples.length;i++)
			listModel.addElement(tuples[i]);
		lTupleList.setSelectedIndex(-1);
	    lTupleList.ensureIndexIsVisible(-1);
	}
	
	public void updateNTuple(String tuple, int index) {
		listModel.set(index, tuple);
	}
	
	public int getSelectedIndex() {
		return lTupleList.getSelectedIndex();
	}
}
