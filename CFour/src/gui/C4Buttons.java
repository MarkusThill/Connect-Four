package gui;

import gui.C4Game.Action;
import gui.C4Game.State;
import guiOptions.OptionsMCTS;
import guiOptions.OptionsMinimax;
import guiOptions.OptionsTD;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import nTupleTD.TDParams;
import mcts.MCTSParams;
import c4.Agent.AgentState;
import c4.AlphaBetaAgent;
import c4.Agent;
import c4.RandomAgent;

/**
 * Class containing all the Buttons on the right panel of the main-window
 * 
 * @author Markus Thill
 * 
 */
public class C4Buttons extends JPanel {

	private static final long serialVersionUID = 1L;

	StatusBar statusBar = new StatusBar();

	C4Game m_game;
	C4Buttons buttons;
	private final String agentList[] = { "Human", "Minimax", "TDS", "Random",
			"Minimax-TDS", "MCTS" };

	// ==================================================================
	// Elements
	// ==================================================================
	private JLabel lPlayerX;
	private JLabel lPlayerO;
	private JLabel lEvaluation;
	private JLabel lChooseOpponents;
	private JLabel lGame;
	private JLabel lTraining;
	private JLabel lprintVals;
	private JLabel lEval;
	private JLabel lSetInitialBoard;
	private JLabel lCurrentAgents;
	private JLabel lAgent0;
	private JLabel lAgent1;
	private JLabel lAgent2;

	private JButton bPlay;
	private JButton bStopGame;
	private JButton bStep;
	private JButton bMoveBack;
	private JButton bNextMove;
	private JButton bParamsX;
	private JButton bParamsO;
	private JButton bParamsEval;
	private JButton bInitX;
	private JButton bInitO;
	private JButton bInitEval;
	private JButton bTrainX;
	private JButton bTrainO;
	private JButton bTrainEval;
	private JButton bMakeNextMoveEval;
	private JButton bSetInitialBoard;
	private JButton bResetBoard;

	protected JComboBox<String> cChooseX;
	protected JComboBox<String> cChooseO;
	protected JComboBox<String> cChooseEval;

	private JCheckBox cbShowGTV;
	private JCheckBox cbShowAgentV;
	private JCheckBox cbShowEvalV;
	protected JCheckBox cbAutostep;

	JProgressBar progress;

	JScrollPane listScrollPane;

	protected C4Buttons(C4Game game) {
		// ==============================================================
		// Inits
		// ==============================================================
		m_game = game;
		buttons = this;

		// ==============================================================
		// Init Elements
		// ==============================================================

		lPlayerX = new JLabel("Player X", JLabel.CENTER);
		lPlayerX.setForeground(Color.yellow);
		lPlayerX.setFont(new Font("Times New Roman", Font.BOLD, 18));

		lPlayerO = new JLabel("Player O", JLabel.CENTER);
		lPlayerO.setForeground(Color.red);
		lPlayerO.setFont(new Font("Times New Roman", Font.BOLD, 18));

		lEvaluation = new JLabel("Evaluation", JLabel.CENTER);
		lEvaluation.setForeground(Color.black);
		lEvaluation.setFont(new Font("Times New Roman", Font.BOLD, 18));

		lChooseOpponents = new JLabel("choose opponents:");

		cChooseX = new JComboBox<String>(agentList);
		cChooseX.setSelectedIndex(0);

		cChooseO = new JComboBox<String>(agentList);
		cChooseO.setSelectedIndex(0);

		cChooseEval = new JComboBox<String>(Arrays.copyOfRange(agentList, 0, 4));
		cChooseEval.setSelectedIndex(0);

		bPlay = new JButton("Start Game");
		bStopGame = new JButton("Stop Game");
		bStep = new JButton("Step");

		lGame = new JLabel("Game: ");
		bMoveBack = new JButton("Move Back");
		bNextMove = new JButton("Next Move");

		lTraining = new JLabel("Training:");
		bParamsX = new JButton("Params X");
		bParamsO = new JButton("Params O");
		bParamsEval = new JButton("Params Eval");

		bInitX = new JButton("Init X");
		bInitO = new JButton("Init O");
		bInitEval = new JButton("Init Eval");

		bTrainX = new JButton("Train X");
		bTrainO = new JButton("Train O");
		bTrainEval = new JButton("Train Eval");

		lCurrentAgents = new JLabel("Current Agents:");
		lAgent0 = new JLabel("None.");
		lAgent0.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lAgent1 = new JLabel("None.");
		lAgent1.setFont(new Font("Times New Roman", Font.BOLD, 14));
		lAgent2 = new JLabel("None.");
		lAgent2.setFont(new Font("Times New Roman", Font.BOLD, 14));

		lEval = new JLabel("Evaluation: ");
		bMakeNextMoveEval = new JButton("Make Move");

		lprintVals = new JLabel("Print Value-Bar:");
		cbShowGTV = new JCheckBox("Theoretic Vals");

		cbShowGTV.setBorderPainted(false);
		// cbShowGTV.b

		cbShowAgentV = new JCheckBox("Agent Vals");
		cbShowEvalV = new JCheckBox("Eval Vals");

		cbAutostep = new JCheckBox("Autostep");

		lSetInitialBoard = new JLabel("Initial Board:");
		bSetInitialBoard = new JButton("Set");
		bResetBoard = new JButton("Reset");
		progress = new JProgressBar(0, 100);

		// ==============================================================
		// Add Action Listeners
		// ==============================================================
		bPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (m_game.state != C4Game.State.PLAY) {
					m_game.changeState(C4Game.State.PLAY);
					String str = "[Start Game...]";
					printStatus(str);
				}
			}
		});

		bStopGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_game.changeState(C4Game.State.IDLE);
				m_game.resetBoard();
				String str = "[Game Stopped by User]";
				printStatus(str);
			}
		});

		bStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean autoMode = cbAutostep.isSelected();
				if (m_game.state == State.PLAY && !autoMode)
					m_game.setPlayStep(true);
			}
		});

		cbShowGTV.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				initValueBar();
			}
		});

		cbShowAgentV.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				initValueBar();
			}
		});

		cbShowEvalV.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				initValueBar();
			}
		});

		cbAutostep.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				// boolean isChecked = cbAutostep.isSelected();
				// bStep.setEnabled(!isChecked);
			}
		});

		bMoveBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_game.action = Action.MOVEBACK;
				String str = "[Take move back]";
				printStatus(str);
			}
		});

		bNextMove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_game.action = Action.NEXTMOVE;
				String str = "[Make a move forward]";
				printStatus(str);
			}
		});

		bParamsX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int x = cChooseX.getSelectedIndex();
				openParams(x, 0);

			}
		});

		bParamsO.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int x = cChooseO.getSelectedIndex();
				openParams(x, 1);

			}
		});

		bParamsEval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int x = cChooseEval.getSelectedIndex();
				openParams(x, 2);

			}
		});

		bInitX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = "[Agent succesfully initialized]";
				if (isToBeInitialized(0))
					switch (cChooseX.getSelectedIndex()) {
					case 0:
						m_game.players[0] = null;
						str = "[No Action made]";
						break;
					case 1:
						m_game.players[0] = m_game.initAlphaBetaAgent(0);
						break;
					case 2:
						m_game.players[0] = m_game.initTDSAgent(0);
						break;
					case 3:
						m_game.players[0] = new RandomAgent();
						break;
					case 4:
						m_game.players[0] = m_game.initAlphaBetaTDSAgent(0);
						break;
					case 5:
						m_game.players[0] = m_game.initMCTSAgent(0);
						break;
					default:
						throw new UnsupportedOperationException(
								"Agent not supported yet!");
					}
				else
					str = "[Agent was not changed!]";
				printStatus(str);
			}
		});

		bInitO.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = "[Agent succesfully initialized]";
				if (isToBeInitialized(1))
					switch (cChooseO.getSelectedIndex()) {
					case 0:
						m_game.players[1] = null;
						str = "[No Action made]";
						break;
					case 1:
						m_game.players[1] = m_game.initAlphaBetaAgent(1);
						break;
					case 2:
						m_game.players[1] = m_game.initTDSAgent(1);
						break;
					case 3:
						m_game.players[1] = new RandomAgent();
						break;
					case 4:
						m_game.players[1] = m_game.initAlphaBetaTDSAgent(1);
						break;
					case 5:
						m_game.players[1] = m_game.initMCTSAgent(1);
						break;
					default:
						throw new UnsupportedOperationException(
								"Agent not supported yet!");
					}
				else
					str = "[Agent was not changed!]";
				printStatus(str);
			}
		});

		bInitEval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = "[Agent succesfully initialized]";
				if (isToBeInitialized(2))
					switch (cChooseEval.getSelectedIndex()) {
					case 0:
						m_game.players[2] = null;
						str = "[No Action made]";
						break;
					case 1:
						m_game.players[2] = m_game.initAlphaBetaAgent(2);
						break;
					case 2:
						m_game.players[2] = m_game.initTDSAgent(2);
						break;
					case 3:
						m_game.players[2] = new RandomAgent();
						break;
					case 4:
						m_game.players[2] = m_game.initAlphaBetaTDSAgent(2);
						break;
					case 5:
						m_game.players[2] = m_game.initMCTSAgent(2);
						break;
					default:
						throw new UnsupportedOperationException(
								"Agent not supported yet!");
					}
				else
					str = "[Agent was not changed!]";
				printStatus(str);
			}
		});

		bTrainX.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = "";
				if (isToBeTrained(0))
					switch (cChooseX.getSelectedIndex()) {
					case 0:
						m_game.players[0] = null;
						str = "[No Action!]";
						break;
					case 1:
						m_game.players[0] = m_game.initAlphaBetaAgent(0);
						str = "[Init with AB-Agent]";
						break;
					case 2:
						// m_game.players[0] = m_game.initTDSAgent(0);
						m_game.changeState(State.TRAIN_X);
						str = "[Train TD-Agent]";
						break;
					case 3:
						m_game.players[0] = new RandomAgent();
						str = "[Init with Random-Agent]";
						break;
					case 4:
						m_game.players[0] = m_game.initAlphaBetaTDSAgent(0);
						str = "[Init with AB-TDS-Agent]";
						break;
					}
				printStatus(str);
			}
		});

		bTrainO.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = "";
				if (isToBeTrained(1))
					switch (cChooseO.getSelectedIndex()) {
					case 0:
						m_game.players[1] = null;
						str = "[No Action!]";
						break;
					case 1:
						m_game.players[1] = m_game.initAlphaBetaAgent(1);
						str = "[Init with AB-Agent]";
						break;
					case 2:
						m_game.changeState(State.TRAIN_O);
						str = "[Train TD-Agent]";
						break;
					case 3:
						m_game.players[1] = new RandomAgent();
						str = "[Init with Random-Agent]";
						break;
					case 4:
						m_game.players[1] = m_game.initAlphaBetaTDSAgent(1);
						str = "[Init with AB-TDS-Agent]";
						break;
					}
				printStatus(str);
			}
		});

		bTrainEval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = "";
				if (isToBeTrained(2))
					switch (cChooseEval.getSelectedIndex()) {
					case 0:
						m_game.players[2] = null;
						str = "[No Action!]";
						break;
					case 1:
						m_game.players[2] = m_game.initAlphaBetaAgent(2);
						str = "[Init with AB-Agent]";
						break;
					case 2:
						m_game.changeState(State.TRAIN_EVAL);
						str = "[Train TD-Agent]";
						break;
					case 3:
						m_game.players[2] = new RandomAgent();
						str = "[Init with Random-Agent]";
						break;
					}
				printStatus(str);
			}
		});

		bMakeNextMoveEval.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int[][] board = m_game.c4.getBoard();
				Agent pa = m_game.players[2];
				String str = "[Evalution is not initialized. Can't make a move!]";
				if (pa != null) {
					int bestMove = pa.getBestMove(board);
					m_game.makeCompleteMove(bestMove, "Evaluation");
					setEnabledPlayStep(false);
					str = "[Move made by Evaluation]";
				}
				printStatus(str);
			}
		});

		bSetInitialBoard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String str = "";
				if (m_game.state == State.SETBOARD) {
					bSetInitialBoard.setText("Set");
					m_game.changeState(State.IDLE);
					str = "[Stop Setting Initial-Board]";
				} else {
					m_game.setInitialBoard();
					m_game.changeState(State.SETBOARD);
					bSetInitialBoard.setText("Stop");
					str = "[Start Setting Initial-Board]";
				}
				printStatus(str);
			}
		});

		bResetBoard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_game.resetBoard();
				String str = "[Reset Board]";
				printStatus(str);
			}
		});

		// ==============================================================
		// Add Tool-Tips
		// ==============================================================

		bPlay.setToolTipText("<html><body>Start a new Math between the two players. "
				+ "If no Agent is selected, the moves are made by a Human.<br> "
				+ "The Agents must be trained (Button Train) to work, even "
				+ "if it is a Minimax-agent and the like</body></html>");

		bStopGame
				.setToolTipText("<html><body>Stop the current game. The board is "
						+ "going to be reseted an a new match can be started</body></html>");
		bMoveBack
				.setToolTipText("<html><body>Take a move back during a game. If"
						+ "the opponents are two humans, then always ONE piece <br>"
						+ "will be taken from the Board. If one opponent is an "
						+ "agent, then always two pieces will be taken from the <br>"
						+ "board, so that it's the human players move. If two \n"
						+ "Agents are playing against each other, then this <br>"
						+ "Button has no function</body></html>");

		bNextMove.setToolTipText("<html><body>Opposite of \"Move Back\". If"
				+ "the opponents are two humans, then always ONE piece <br>"
				+ "will be put from the Board. If one opponent is an "
				+ "agent, then always two pieces will be put on the <br>"
				+ "board. If two \nAgents are playing against each other, "
				+ "then this <br>Button has no function</body></html>");

		bParamsX.setToolTipText("<html><body>Set the parameters for the selected agent. "
				+ "Changes will only then be updated if the Init-Button is <br>"
				+ "used to re-init the agent.</body></html>");

		bParamsO.setToolTipText("<html><body>Set the parameters for the selected agent. "
				+ "Changes will only then be updated if the Init-Button is <br>"
				+ "used to re-init the agent.</body></html>");

		bParamsEval
				.setToolTipText("<html><body>Set the parameters for the selected agent. "
						+ "Changes will only then be updated if the Init-Button is <br>"
						+ "used to re-init the agent.</body></html>");

		bInitX.setToolTipText("<html><body>Init the selected Agent. This has to "
				+ "be done for all agents. If Parameters like the used N-Tuples change, <br>"
				+ "then the agent also must be initialized again! </body></html>");

		bInitO.setToolTipText("<html><body>Init the selected Agent. This has to "
				+ "be done for all agents.  If Parameters like the used N-Tuples change, <br>"
				+ "then the agent also must be initialized again!</body></html>");

		bInitEval
				.setToolTipText("<html><body>Init the selected Agent. This has to "
						+ "be done for all agents.  If Parameters like the used N-Tuples change, <br>"
						+ "then the agent also must be initialized again!</body></html>");

		bMakeNextMoveEval
				.setToolTipText("Let the Evaluation make a move during a game. ");

		bSetInitialBoard
				.setToolTipText("Set a initial Board for Games and competitions.");

		bResetBoard
				.setToolTipText("Reset the initial Board for Games and competitions.");

		bTrainX.setToolTipText("<html><body>Train the selected Agent. This has to "
				+ "be done for all TD-agents with a training-routine <br>. If no training routine is "
				+ "found, then the agent will be re-initialized</body></html>");

		bTrainO.setToolTipText("<html><body>Train the selected Agent. This has to "
				+ "be done for all TD-agents with a training-routine <br>. If no training routine is "
				+ "found, then the agent will be re-initialized</body></html>");

		bTrainEval
				.setToolTipText("<html><body>Train the selected Agent. This has to "
						+ "be done for all TD-agents with a training-routine <br>. If no training routine is "
						+ "found, then the agent will be re-initialized</body></html>");

		bStep.setToolTipText("<html><body>Makes a move during a game for the current agent.</body></html>");

		// ==============================================================
		// Place Elements on the Panel
		// ==============================================================
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(0, 4, 10, 10));
		p.setPreferredSize(new Dimension(480, 300));

		p.add(new JLabel(""));
		p.add(lPlayerX);
		p.add(lPlayerO);
		p.add(lEvaluation);

		p.add(lChooseOpponents);
		p.add(cChooseX);
		p.add(cChooseO);
		p.add(cChooseEval);

		p.add(lTraining);
		p.add(bParamsX);
		p.add(bParamsO);
		p.add(bParamsEval);

		p.add(new JLabel(""));
		p.add(bInitX);
		p.add(bInitO);
		p.add(bInitEval);

		p.add(new JLabel(""));
		p.add(bTrainX);
		p.add(bTrainO);
		p.add(bTrainEval);

		p.add(lCurrentAgents);
		p.add(lAgent0);
		p.add(lAgent1);
		p.add(lAgent2);

		p.add(lprintVals);
		p.add(cbShowGTV);
		p.add(cbShowAgentV);
		p.add(cbShowEvalV);

		p.add(lSetInitialBoard);
		p.add(bSetInitialBoard);
		p.add(bResetBoard);
		p.add(new JLabel(""));

		p.add(lGame);
		p.add(bPlay);
		p.add(bStopGame);
		p.add(cbAutostep);

		p.add(new JLabel(""));
		p.add(bMoveBack);
		p.add(bNextMove);
		p.add(bStep);

		p.add(lEval);
		p.add(bMakeNextMoveEval);
		p.add(new JLabel(""));
		p.add(new JLabel(""));

		// ==============================================================
		// Add Panel to Scroll-Pane
		// ==============================================================
		listScrollPane = new JScrollPane(p,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		// =====================================

		// ==============================================================
		// Status-Bar
		// ==============================================================
		JPanel s = new JPanel();
		s.setLayout(new GridLayout2(0, 1, 10, 10));
		s.add(progress);
		s.add(statusBar);
		statusBar.setMessage("Init done.");

		setLayout(new BorderLayout(10, 10));

		add(listScrollPane, BorderLayout.CENTER);
		add(s, java.awt.BorderLayout.SOUTH);
	}

	protected boolean isToBeInitialized(int index) {
		Agent p = m_game.players[index];
		if (p != null) {
			if (p.getAgentState() == AgentState.INITIALIZED
					|| p.getAgentState() == AgentState.TRAINED) {
				int sel = JOptionPane
						.showConfirmDialog(
								null,
								"Agent is already initialzed/trained. Are you sure that you want to initialize again? This will reset the whole agent.",
								"Warning!", JOptionPane.YES_NO_OPTION);
				return (sel == JOptionPane.YES_OPTION);
			}
		}
		return true;
	}

	protected boolean isToBeTrained(int index) {
		Agent p = m_game.players[index];
		if (p != null) {
			if (p.getAgentState() == AgentState.TRAINED) {
				int sel = JOptionPane
						.showConfirmDialog(
								null,
								"Agent is already trained. Are you sure that you want to train again? This will not reset the agent, but the training will be continued based on the current parameter-settings.",
								"Warning!", JOptionPane.YES_NO_OPTION);
				return (sel == JOptionPane.YES_OPTION);
			}
		}
		return true;
	}

	protected StatusBar getStatusBar() {
		return statusBar;
	}

	protected void setWindowPos(Window obj) {
		obj.setVisible(!obj.isVisible());
		int x = m_game.getLocation().x + m_game.getWidth() + 8;
		int y = m_game.getLocation().y;
		if (m_game.c4Frame != null) {
			x = m_game.c4Frame.getLocation().x + m_game.c4Frame.getWidth() + 1;
			y = m_game.c4Frame.getLocation().y;
			Dimension screenSize = java.awt.Toolkit.getDefaultToolkit()
					.getScreenSize();
			if (x + obj.getWidth() > screenSize.width)
				x = m_game.c4Frame.getLocation().x;
			if (y + obj.getHeight() > screenSize.height)
				y = 1;
			// If window-height is still too large, then reduce it to screensize
			if (obj.getHeight() + 5 > screenSize.height)
				obj.setSize(obj.getWidth(), (int) (screenSize.height * 0.95));
		}
		obj.setLocation(x, y);
	}

	protected void initValueBar() {
		m_game.showAgentV = cbShowAgentV.isSelected();
		m_game.showAgentEvalV = cbShowEvalV.isSelected();
		m_game.showGTV = cbShowGTV.isSelected();
		m_game.printValueBar();
	}

	protected void openParams(int choiceIndex, int agent) {
		for (int i = 0; i < 3; i++)
			if (m_game.params[i] != null && i != agent)
				m_game.params[i].setVisible(false);

		switch (choiceIndex) {
		case 1:
		case 4:
			if (m_game.params[agent] == null
					|| !m_game.params[agent].getClass().equals(
							OptionsMinimax.class))
				m_game.params[agent] = new OptionsMinimax(
						AlphaBetaAgent.TRANSPOSBYTES);
			break;
		case 2:
			if (m_game.params[agent] == null
					|| !m_game.params[agent].getClass().equals(OptionsTD.class))
				m_game.params[agent] = new OptionsTD(new TDParams());
			break;
		case 5:
			if (m_game.params[agent] == null
					|| !m_game.params[agent].getClass().equals(
							OptionsMCTS.class))
				m_game.params[agent] = new OptionsMCTS(new MCTSParams());
			break;
		case 0:
		default:
			m_game.params[agent] = null;
			break;

		}
		if (m_game.params[agent] != null) {
			setWindowPos(m_game.params[agent]);
		}
	}

	protected void enableItems(State st) {
		switch (st) {
		case IDLE:
			bPlay.setEnabled(true);
			bStopGame.setEnabled(false);
			bMoveBack.setEnabled(false);
			bNextMove.setEnabled(false);
			bParamsX.setEnabled(true);
			bParamsO.setEnabled(true);
			bInitX.setEnabled(true);
			bInitO.setEnabled(true);
			cChooseX.setEnabled(true);
			cChooseO.setEnabled(true);
			bSetInitialBoard.setEnabled(true);
			bResetBoard.setEnabled(true);
			cbShowGTV.setEnabled(true);
			cbShowAgentV.setEnabled(true);
			bMakeNextMoveEval.setEnabled(true);
			bMakeNextMoveEval.setEnabled(false);
			cbShowAgentV.setEnabled(true);
			cbShowEvalV.setEnabled(true);
			cbShowGTV.setEnabled(true);
			cChooseEval.setEnabled(true);
			bParamsEval.setEnabled(true);
			bInitEval.setEnabled(true);
			bTrainX.setEnabled(true);
			bTrainO.setEnabled(true);
			bTrainEval.setEnabled(true);
			cbAutostep.setEnabled(false);
			setEnabledPlayStep(false);
			initValueBar();
			break;
		case PLAY:
			bPlay.setEnabled(false);
			bStopGame.setEnabled(true);
			bMoveBack.setEnabled(true);
			bNextMove.setEnabled(true);
			bMakeNextMoveEval.setEnabled(true);
			bParamsX.setEnabled(false);
			bParamsO.setEnabled(false);
			bInitX.setEnabled(false);
			bInitO.setEnabled(false);
			bSetInitialBoard.setEnabled(false);
			bResetBoard.setEnabled(false);
			cChooseX.setEnabled(false);
			cChooseO.setEnabled(false);
			bTrainX.setEnabled(false);
			bTrainO.setEnabled(false);
			cbAutostep.setEnabled(true);
			break;
		case SETBOARD:
			bPlay.setEnabled(false);
			bMakeNextMoveEval.setEnabled(false);
			bMoveBack.setEnabled(true);
			bNextMove.setEnabled(true);
			bTrainX.setEnabled(false);
			bTrainO.setEnabled(false);
			break;
		case TRAIN_EVAL:
		case TRAIN_O:
		case TRAIN_X:
		case EVALUATE:
			cChooseEval.setEnabled(false);
			bParamsEval.setEnabled(false);
			bInitEval.setEnabled(false);
			bTrainEval.setEnabled(false);
		case TESTVALUEFUNC:
		case COMPETE:
		case MULTICOMPETE:
		case TESTBESTMOVE:
		case MULTITRAIN:
			cbAutostep.setEnabled(false);
			bPlay.setEnabled(false);
			bParamsX.setEnabled(false);
			bParamsO.setEnabled(false);
			bInitX.setEnabled(false);
			bInitO.setEnabled(false);
			bSetInitialBoard.setEnabled(false);
			bResetBoard.setEnabled(false);
			cChooseX.setEnabled(false);
			cChooseO.setEnabled(false);
			bMakeNextMoveEval.setEnabled(false);
			cbShowAgentV.setEnabled(false);
			cbShowEvalV.setEnabled(false);
			cbShowGTV.setEnabled(false);
			bTrainX.setEnabled(false);
			bTrainO.setEnabled(false);
			break;
		case SHOWNTUPLE:
			deactivateAll();
			break;
		case SETNTUPLE:
			deactivateAll();
			break;
		case INSPNTUPLE:
		case LOAD_EVAL:
		case LOAD_O:
		case LOAD_X:
		case SAVE_EVAL:
		case SAVE_O:
		case SAVE_X:
			deactivateAll();
			break;
		default:
			break;
		}
	}

	protected void setEnabledPlayStep(boolean enabled) {
		if (!cbAutostep.isSelected())
			bStep.setEnabled(enabled);
		else
			bStep.setEnabled(false);
	}

	private void deactivateAll() {
		cbAutostep.setEnabled(false);
		bPlay.setEnabled(false);
		bParamsX.setEnabled(false);
		bParamsO.setEnabled(false);
		bInitX.setEnabled(false);
		bInitO.setEnabled(false);
		bSetInitialBoard.setEnabled(false);
		bResetBoard.setEnabled(false);
		cChooseX.setEnabled(false);
		cChooseO.setEnabled(false);
		bMakeNextMoveEval.setEnabled(false);
		cbShowAgentV.setEnabled(false);
		cbShowEvalV.setEnabled(false);
		cbShowGTV.setEnabled(false);
		cChooseEval.setEnabled(false);
		bParamsEval.setEnabled(false);
		bInitEval.setEnabled(false);
		bTrainX.setEnabled(false);
		bTrainO.setEnabled(false);
		bTrainEval.setEnabled(false);
	}

	protected void printCurAgents(Agent pa[]) {
		String none = "None / Human.";
		if (pa == null) {
			lAgent0.setText(none);
			lAgent1.setText(none);
			lAgent2.setText(none);
		} else {
			if (pa[0] != null)
				lAgent0.setText(pa[0].getName());
			else
				lAgent0.setText(none);
			if (pa[1] != null)
				lAgent1.setText(pa[1].getName());
			else
				lAgent1.setText(none);
			if (pa[2] != null)
				lAgent2.setText(pa[2].getName());
			else
				lAgent2.setText(none);
		}
	}

	protected void setProgressBar(int value) {
		progress.setValue(value);
	}

	public class StatusBar extends JTextArea {
		private static final long serialVersionUID = 1L;

		/** Creates a new instance of StatusBar */
		public StatusBar() {
			super();
			super.setPreferredSize(new Dimension(200, 32));
			setMessage("Ready");
		}

		public void setMessage(String message) {
			setText(" " + message);
		}
	}

	public void printStatus(String str) {
		m_game.syncStatusBar = true;
		System.out.println(str);
		statusBar.setMessage(str);
	}

}
