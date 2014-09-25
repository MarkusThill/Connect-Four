package gui;

import gui.C4Game.State;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import nTupleTD.NTupleC4;
import nTupleTD.TDSAgent;


/**
 * Main Window, for analyzing single LUTs of the N-Tuple-System
 * 
 * @author Markus Thill
 * 
 */
public class LUTShow extends Dialog implements ListOperation {

	private static final long serialVersionUID = 1L;

	private C4Game m_game;
	private TDSAgent tds;
	private NTupleC4 nTupleList[];
	private int iTuple = 0;
	private int iLUT = 0;

	// Board
	private ImgShowComponent playingBoard[][];
	private int boardVal[][];
	private JPanel board;

	// Other Windows
	protected ShowTupleList winTupleList;
	protected ShowValueList winValueList;

	// Mode
	protected enum Mode {
		LUT, POSITION
	};

	Mode mode = Mode.LUT;

	// Labels
	private JLabel lTitle;
	private JLabel lInLUT;
	private JLabel lInNTuple;
	private JLabel lLUTGotoEntry;
	private JLabel lNGotoEntry;
	private JLabel lSortWeights;
	// private JLabel lResults;
	private JLabel lCurPlayer;
	private JLabel lLUTNr;
	private JLabel lPosVals;
	private JLabel lScore;
	private JLabel lMode;

	// Buttons
	private JButton bOK;
	private JButton bLUTFirst;
	private JButton bLUTLast;
	private JButton bLUTprev;
	private JButton bLUTnext;

	private JButton bNTupleFirst;
	private JButton bNTupleLast;
	private JButton bNTuplePrev;
	private JButton bNTupleNext;

	private JButton bLUTGotoEntry;
	private JButton bNGotoEntry;

	private JButton bResetBoard;

	// TextFields
	private JTextField tLUTGotoEntry;
	private JTextField tNGotoEntry;

	// ComboBox
	JComboBox<String> cbSortWeights;

	// RadioButtons
	private ButtonGroup cbgMode;
	private JRadioButton cbModeLUT;
	private JRadioButton cbModePos;

	// Reference to own Object
	LUTShow m_par = this;

	public LUTShow(C4Game game) {
		super(game.c4Frame, "N-Tuple LUT");
		m_game = game;
		boardVal = new int[7][6];

		for (int i = 0; i < boardVal.length; i++)
			for (int j = 0; j < boardVal[i].length; j++)
				boardVal[i][j] = -99;

		setModal(true);

		// -------------------------------------------------------------
		// Init Elements
		// -------------------------------------------------------------
		lTitle = new JLabel("Test", JLabel.CENTER);
		lTitle.setFont(new Font("Times New Roman", 1, 20));

		lMode = new JLabel("Mode");
		lMode.setFont(new Font("Times New Roman", 1, 18));
		cbgMode = new ButtonGroup();
		cbModeLUT = new JRadioButton("LUT-Show");
		cbModeLUT.setSelected(true);
		cbModePos = new JRadioButton("Eval Position");
		cbgMode.add(cbModeLUT);
		cbgMode.add(cbModePos);

		lInLUT = new JLabel("In LUT");
		lInLUT.setFont(new Font("Times New Roman", 1, 18));

		lInNTuple = new JLabel("In N-Tuple-List");
		lInNTuple.setFont(new Font("Times New Roman", 1, 18));

		bLUTFirst = new JButton("<<");
		bLUTLast = new JButton(">>");
		bLUTprev = new JButton("<");
		bLUTnext = new JButton(">");

		lLUTGotoEntry = new JLabel("Goto LUT-Entry", JLabel.RIGHT);
		tLUTGotoEntry = new JTextField("");
		bLUTGotoEntry = new JButton("Go");

		lNGotoEntry = new JLabel("Goto Tuple", JLabel.RIGHT);
		tNGotoEntry = new JTextField("");
		bNGotoEntry = new JButton("Go");

		bNTupleFirst = new JButton("<<");
		bNTupleLast = new JButton(">>");
		bNTuplePrev = new JButton("<");
		bNTupleNext = new JButton(">");

		lSortWeights = new JLabel("Sort Weights", JLabel.RIGHT);
		cbSortWeights = new JComboBox<String>(new String[] { "none", "increasing",
				"decreasing" });

		// Not implemented yet
		cbSortWeights.setEnabled(false);

		lCurPlayer = new JLabel("Cur. Player:");
		lCurPlayer.setFont(new Font("Times New Roman", 1, 18));

		lLUTNr = new JLabel("");
		lLUTNr.setForeground(Color.gray);

		lPosVals = new JLabel("Pos. Values:");
		lPosVals.setForeground(Color.gray);

		lScore = new JLabel("");
		lScore.setForeground(Color.blue);
		lScore.setFont(new Font("Times New Roman", 1, 16));

		bResetBoard = new JButton("Reset Board");

		bOK = new JButton("Close");

		// -------------------------------------------------------------
		// add Action Listener
		// -------------------------------------------------------------
		bOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWindow();
			}
		});

		bNTupleFirst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iTuple = 0;
				nTupleIndexChanged();
				updateWindow();
				// always refresh board
				updateNTuplePosition(true);
			}
		});

		bNTupleLast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iTuple = nTupleList.length - 1;
				nTupleIndexChanged();
				updateWindow();
				// always refresh board
				updateNTuplePosition(true);
			}
		});

		bNTuplePrev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iTuple--;
				nTupleIndexChanged();
				updateWindow();
				updateNTuplePosition(iTuple % 2 == 1);
			}
		});

		bNTupleNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iTuple++;
				nTupleIndexChanged();
				updateWindow();
				updateNTuplePosition(iTuple % 2 == 0);
			}
		});

		bNGotoEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Integer.parseInt(tNGotoEntry.getText());
					iTuple = Integer.valueOf(tNGotoEntry.getText()).intValue() * 2;
				} catch (NumberFormatException e1) {
					tNGotoEntry.setText(iTuple + "");
				}
				nTupleIndexChanged();
				updateWindow();
			}
		});

		bLUTFirst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iLUT = 0;
				updateWindow();
			}
		});

		bLUTLast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iLUT = nTupleList[iTuple].getLUTSize(false) - 1;
				updateWindow();
			}
		});

		bLUTprev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iLUT--;
				updateWindow();
			}
		});

		bLUTnext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				iLUT++;
				updateWindow();
			}
		});

		bLUTGotoEntry.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Integer.parseInt(tLUTGotoEntry.getText());
					iLUT = Integer.valueOf(tLUTGotoEntry.getText()).intValue();
				} catch (NumberFormatException e1) {
					tLUTGotoEntry.setText(iLUT + "");
				}
				updateWindow();
			}
		});

		cbModeLUT.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (cbModeLUT.isSelected()) {
					mode = Mode.LUT;
					enableLUTElements(true);

					winTupleList.setVisible(true);
					winValueList.dialog.setVisible(false);
					winTupleList.toFront();
				}
			}
		});

		cbModePos.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (cbModePos.isSelected()) {
					mode = Mode.POSITION;
					enableLUTElements(false);
					resetBoardVals(0);

					// Set Bottomrow
					if (nTupleList != null && nTupleList[0].getPosVals() == 4)
						setBoardValsBottom(3);
					iLUT = nTupleList[iTuple].getFeature(boardVal);
					updateWindow();
					updateNTuplePosition(true);

					winTupleList.setVisible(false);
					winValueList.dialog.setVisible(true);
					winValueList.dialog.toFront();
				}
			}
		});

		bResetBoard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int initValue = (mode == Mode.LUT ? -1 : 0);
				resetBoardVals(initValue);
				setReachableFields(0, 0);
				iLUT = 0;
				updateWindow();
				updateNTuplePosition(true);
			}
		});

		// -------------------------------------------------------------
		// Add Elements to Panel
		// -------------------------------------------------------------
		setLayout(new BorderLayout());
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0, 5, 10, 10));

		//
		p.add(new Canvas());
		p.add(lMode);
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		//
		p.add(new Canvas());
		p.add(cbModeLUT);
		p.add(cbModePos);
		p.add(new Canvas());
		p.add(new Canvas());

		//
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		//
		p.add(new Canvas());
		p.add(lInNTuple);
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		//
		p.add(new Canvas());
		p.add(bNTupleFirst);
		p.add(bNTuplePrev);
		p.add(bNTupleNext);
		p.add(bNTupleLast);

		//
		p.add(lNGotoEntry);
		p.add(tNGotoEntry);
		p.add(bNGotoEntry);
		p.add(new Canvas());
		p.add(new Canvas());

		//
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		//
		p.add(new Canvas());
		p.add(lInLUT);
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		//
		p.add(new Canvas());
		p.add(bLUTFirst);
		p.add(bLUTprev);
		p.add(bLUTnext);
		p.add(bLUTLast);

		//
		p.add(lLUTGotoEntry);
		p.add(tLUTGotoEntry);
		p.add(bLUTGotoEntry);
		p.add(new Canvas());
		p.add(new Canvas());

		//
		p.add(new Canvas());
		p.add(lCurPlayer);
		p.add(lLUTNr);
		p.add(lPosVals);
		p.add(new Canvas());

		//
		p.add(new Canvas());
		p.add(lScore);
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		//
		p.add(lSortWeights);
		p.add(cbSortWeights);
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		//
		p.add(bOK);
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		// -------------------------------------------------------------
		// Last
		// -------------------------------------------------------------
		setLayout(new BorderLayout(10, 10));
		setBackground(Color.white);

		board = initPlayingBoard();
		JPanel boardPanel = new JPanel();
		boardPanel.add(board);
		boardPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		boardPanel.add(new Canvas(), c);

		GridBagConstraints d = new GridBagConstraints();
		d.fill = GridBagConstraints.VERTICAL;
		d.gridx = 0;
		d.gridy = 20;
		boardPanel.add(bResetBoard, d);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(p);
		buttonPanel.setLayout(new GridBagLayout());
		buttonPanel.add(new Canvas(), c);

		add(boardPanel, BorderLayout.CENTER);
		add(lTitle, BorderLayout.NORTH);
		add(buttonPanel, BorderLayout.EAST);

		setSize(1100, 610);
		setVisible(false);
		setResizable(false);

		addWindowListener(new WindowClosingAdapter());

		// -------------------------------------------------------------
		// Create new Window with List of N-Tuples
		// -------------------------------------------------------------
		winTupleList = new ShowTupleList(null, this, this, "N-Tuple List");
		winTupleList.toFront();
		int x = getLocation().x + getWidth() + 8;
		int y = getLocation().y;
		winTupleList.setLocation(x, y);
		winTupleList.setVisible(false);

		winValueList = new ShowValueList(this, this);
		winValueList.dialog.toFront();
		x = getLocation().x + getWidth() + 8;
		y = getLocation().y;
		winValueList.dialog.setLocation(x, y);
		winValueList.dialog.setVisible(false);

		// Set initial Window
		updateWindow();
	}

	private JPanel initPlayingBoard() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout2(6, 7, 2, 2));
		panel.setBackground(Color.BLACK);

		for (int i = 0; i < 42; i++)
			panel.add(new Canvas());

		// Add Board
		playingBoard = new ImgShowComponent[7][6];
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 6; j++) {
				int imgIndex = ImgShowComponent.EMPTY0;
				ImgShowComponent newImg = replaceImage(playingBoard[i][j],
						imgIndex, i, j);
				if (newImg != playingBoard[i][j]) {
					playingBoard[i][j] = newImg;
					panel.remove((5 - j) * 7 + i);
					panel.add(playingBoard[i][j], (5 - j) * 7 + i);
				}
			}
		}
		return panel;
	}

	private void resetBoardVals(int initVal) {
		for (int i = 0; i < boardVal.length; i++)
			for (int j = 0; j < boardVal[i].length; j++)
				boardVal[i][j] = initVal;
	}

	private void setBoardValsBottom(int initVal) {
		for (int i = 0; i < boardVal.length; i++)
			boardVal[i][0] = initVal;
	}

	private ImgShowComponent replaceImage(ImgShowComponent oldImg,
			int imgIndex, int num1, int num2) {
		ImgShowComponent imgShComp = ImgShowComponent.replaceImg(oldImg,
				imgIndex);
		if (oldImg != imgShComp)
			imgShComp.addMouseListener(new MouseHandler(num1, num2) {
				public void mouseClicked(MouseEvent e) {
					handleMouseClick(x, y);
				}
			});
		return imgShComp;
	}

	private void handleMouseClick(int x, int y) {
		if (nTupleList != null) {
			switch (mode) {
			case LUT:
				int pow = nTupleList[iTuple].getPowSamplingPoint(x, y);
				if (pow > -1) {
					int posVals = nTupleList[iTuple].getPosVals();
					if (boardVal[x][y] == posVals - 1)
						iLUT -= pow * (posVals - 1);
					else
						iLUT += pow;
					updateWindow();
				}

				break;
			case POSITION:
				boardVal[x][y] = (boardVal[x][y] + 1) % 3;
				boolean isPartofNTuple = nTupleList[iTuple]
						.isPartOfNTuple(x, y);
				int imgIndex;
				if (!isPartofNTuple)
					// Without frame in corners
					imgIndex = boardVal[x][y] + 5;
				else
					// With frame in corners
					imgIndex = boardVal[x][y] + 1;
				putPiece(x, y, imgIndex);

				setReachableFields(x, y);

				iLUT = nTupleList[iTuple].getFeature(boardVal);
				updateWindow();
				break;
			}
		}
	}

	private void setReachableFields() {
		if (nTupleList != null)
			for (int i = 0; i < boardVal.length; i++)
				for (int j = 0; j < boardVal[i].length; j++)
					setReachableFields(i, j);
	}

	private void setReachableFields(int x, int y) {
		// Set reachable fields (=3) if needed
		if (nTupleList[iTuple].getPosVals() == 4) {
			if (y < 5) {
				if (boardVal[x][y] != 0 && boardVal[x][y] != 3
						&& boardVal[x][y + 1] == 0)
					boardVal[x][y + 1] = 3; // reachable field
				else if (boardVal[x][y] == 0 && boardVal[x][y + 1] == 3)
					boardVal[x][y + 1] = 0;
			}

			// Bottom-row is always reachable
			for (int i = 0; i < boardVal.length; i++)
				if (boardVal[i][0] == 0)
					boardVal[i][0] = 3;
		}
	}

	private void putPiece(int x, int y, int imgIndex) {
		ImgShowComponent newImg = replaceImage(playingBoard[x][y], imgIndex, x,
				y);
		if (newImg != playingBoard[x][y]) {
			board.remove((5 - y) * 7 + x);
			playingBoard[x][y] = newImg;
			board.add(playingBoard[x][y], (5 - y) * 7 + x);
		}
	}

	private void closeWindow() {
		m_par.setVisible(false);
		winTupleList.setVisible(false);
		winValueList.dialog.setVisible(false);
		m_game.changeState(State.IDLE);
	}

	public void setTDSAgent(TDSAgent agent) {
		tds = agent;
		nTupleList = tds.m_Net.getNTuples();

		iTuple = 0;
		iLUT = 0;

		// Set Tuple List in other Window
		winTupleList.setNTuples(tds.m_Net.getNTuples(1));

		updateWindow();
	}

	private void updateWindow() {
		checkNTupleIndex();
		checkLUTIndex();
		if (mode == Mode.LUT)
			enableLUTElements();
		updateTitle();
		updateNTupleGo();
		updateNTupleList();
		setReachableFields();
		updateValueList();
		updateLUTGo();
		updateLUTNr();
		updateScore();
		updatePosVals();
		updateCurPlayer();
		if (mode == Mode.LUT)
			updateLUTBoard();

		// Moved here from putPiece()
		board.invalidate();
		board.validate();
	}

	private void updateTitle() {
		String player = (iTuple % 2 == 0 ? "X." : "O:");
		String listLen = (nTupleList == null ? "0" : (nTupleList.length - 2)
				/ 2 + "");
		String title = "Lookup-Table for N-Tuple " + player + iTuple / 2 + "/"
				+ listLen;
		lTitle.setText(title);
	}

	private void updateNTupleGo() {
		tNGotoEntry.setText(iTuple / 2 + "");
	}

	private void updateNTupleList() {
		winTupleList.setSelectedIndex(iTuple / 2);
	}

	private void updateValueList() {
		if (nTupleList != null && mode == Mode.POSITION) {
			ValueList vl = new ValueList(nTupleList, boardVal);

			// if (vl.indexes != null && vl.values != null)
			winValueList.setValueList(vl);
			winValueList.setSelectedIndex(iTuple / 2);
		}
	}

	private void updateLUTGo() {
		tLUTGotoEntry.setText(iLUT + "");
	}

	private void updateLUTNr() {
		String curIndex = iLUT + "";
		String lastIndex = (nTupleList == null ? "0" : nTupleList[iTuple]
				.getLUTSize(false) - 1 + "");
		lLUTNr.setText("LUT: " + curIndex + " / " + lastIndex);
	}

	private void updateScore() {
		double score = (nTupleList == null ? Double.NaN : nTupleList[iTuple]
				.getWeight(iLUT));
		String str = new DecimalFormat("0.0000").format(score);
		lScore.setText("Score: " + str);
	}

	private void updatePosVals() {
		String posVals = "Pos. Values: ";
		if (nTupleList != null)
			posVals += nTupleList[iTuple].getPosVals();
		lPosVals.setText(posVals);
	}

	private void updateCurPlayer() {
		String player = (iTuple % 2 == 0 ? "X" : "O");
		lCurPlayer.setText("Player " + player);
	}

	private void updateLUTBoard() {
		if (nTupleList != null) {
			int[][] newBoard = nTupleList[iTuple].getBoard(iLUT);
			for (int i = 0; i < newBoard.length; i++)
				for (int j = 0; j < newBoard[i].length; j++) {
					if (boardVal[i][j] != newBoard[i][j]) {
						boardVal[i][j] = newBoard[i][j];
						int imgIndex = newBoard[i][j] + 1;
						putPiece(i, j, imgIndex);
					}
				}
		}
	}

	private void updateNTuplePosition(boolean refreshBoard) {
		// Only if mode = POSITION
		if (nTupleList != null && mode == Mode.POSITION) {
			int imgIndex = 0, i, j;

			if (refreshBoard) {
				// Reset Board
				for (i = 0; i < boardVal.length; i++)
					for (j = 0; j < boardVal[i].length; j++) {
						imgIndex = (boardVal[i][j] % 3) + 5;
						putPiece(i, j, imgIndex);
					}
				Integer[][] nTuple = nTupleList[iTuple].getNTuple2dim();

				// Move N-Tuple
				int value;
				for (i = 0; i < nTuple.length; i++) {
					value = boardVal[nTuple[i][0]][nTuple[i][1]];

					imgIndex = (value % 3) + 1;
					putPiece(nTuple[i][0], nTuple[i][1], imgIndex);
				}
			}

			// Index in LUT must be changed
			iLUT = nTupleList[iTuple].getFeature(boardVal);
			updateWindow();
		}
	}

	private void nTupleIndexChanged() {
		checkNTupleIndex();
		// Update selected Player
		int curPlayer = iTuple % 2;
		Player cPlayer = (curPlayer == 0 ? Player.X : Player.O);
		winValueList.setPlayer(cPlayer);
	}

	private void checkNTupleIndex() {
		if (nTupleList != null) {
			if (iTuple < 0)
				iTuple = 0;
			if (iTuple >= nTupleList.length)
				iTuple = nTupleList.length - 1;

			if (iTuple == 0) {
				bNTuplePrev.setEnabled(false);
				bNTupleFirst.setEnabled(false);
			} else {
				bNTuplePrev.setEnabled(true);
				bNTupleFirst.setEnabled(true);
			}

			if (iTuple == nTupleList.length - 1) {
				bNTupleNext.setEnabled(false);
				bNTupleLast.setEnabled(false);
			} else {
				bNTupleNext.setEnabled(true);
				bNTupleLast.setEnabled(true);
			}
		}
	}

	private void checkLUTIndex() {
		if (nTupleList != null) {
			int lutSize = nTupleList[iTuple].getLUTSize(false);
			if (iLUT < 0)
				iLUT = 0;
			if (iLUT >= lutSize)
				iLUT = lutSize - 1;

		}
	}

	private void enableLUTElements() {
		if (nTupleList != null) {
			int lutSize = nTupleList[iTuple].getLUTSize(false);
			if (iLUT == 0) {
				bLUTprev.setEnabled(false);
				bLUTFirst.setEnabled(false);
			} else {
				bLUTprev.setEnabled(true);
				bLUTFirst.setEnabled(true);
			}

			if (iLUT == lutSize - 1) {
				bLUTnext.setEnabled(false);
				bLUTLast.setEnabled(false);
			} else {
				bLUTnext.setEnabled(true);
				bLUTLast.setEnabled(true);
			}
		}
	}

	private void enableLUTElements(boolean state) {
		bLUTFirst.setEnabled(state);
		bLUTprev.setEnabled(state);
		bLUTnext.setEnabled(state);
		bLUTLast.setEnabled(state);
		lLUTGotoEntry.setEnabled(state);
		tLUTGotoEntry.setEnabled(state);
		bLUTGotoEntry.setEnabled(state);
		lSortWeights.setEnabled(state);

		// not implemented yet
		cbSortWeights.setEnabled(false);
	}

	@Override
	public void indexChanged(int newIndex) {
		iTuple = newIndex * 2;
		updateWindow();
		updateNTuplePosition(true);

	}

	@Override
	public void playerChanged(Player player) {
		int iTuplePlayer = iTuple % 2;

		// Don't change iTuple-Value, if it already shows on the nTuple of
		// Player O
		if (player == Player.O && iTuplePlayer == 0) {
			iTuple++;
			updateWindow();
			updateNTuplePosition(iTuple % 2 == 0);
		}
		// Don't change iTuple-Value, if it already shows on the nTuple of
		// Player X
		else if (player == Player.X && iTuplePlayer == 1) {
			iTuple--;
			updateWindow();
			updateNTuplePosition(iTuple % 2 == 1);
		}
	}

	private class MouseHandler implements MouseListener {
		int x, y;

		MouseHandler(int num1, int num2) {
			x = num1;
			y = num2;
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}
	}

	private class WindowClosingAdapter extends WindowAdapter {
		public WindowClosingAdapter() {
		}

		public void windowClosing(WindowEvent event) {
			closeWindow();
		}
	}

}
