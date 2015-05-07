package gui;

import gui.C4Game.State;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * Main Menu
 * 
 * @author Markus Thill
 * 
 */
public class C4Menu extends JMenuBar {

	private static final String TIPEVALUATE = "";

	private static final String TIPINSPECTLUT = "Inspect the lookup-tables (LUT) for the selected "
			+ "N-Tuple-Agent. The selected agent also must be trained for this to work";

	private static final String TIPSETNTUPLES = "<html><body>Show and change the N-Tuples or a N-Tuple-TD-Agent "
			+ "This button doesn't work for other agent-types. The selected agent also must <br>"
			+ "be initialized for this to work.  If  the used N-Tuples change, "
			+ "then the agent also must be initialized again!</body></html>";

	@SuppressWarnings("unused")
	private static final String TIPSHOWNTUPLE = "<html><body>Show the N-Tuples for a N-Tuple-TD-Agent. This button "
			+ "doesn't work for other agent-types. The selected agent also must <br>"
			+ "be trained for this to work</body></html>";

	private static final String TIPSAVE = "<html><body>Save a COMPLETE TD-Agent. All lookup-tables and configurations of the agent "
			+ "will be saved to HDD. Make sure, that enough <br>hdd-memory is available (all lookup-tables "
			+ "can get huge sometimes)</body></html>";

	private static final String TIPLOAD = "Open a COMPLETE TD-Agent. All lookup-tables and configurations of the agent "
			+ "will be loaded and assigned to the selected agent";

	private static final long serialVersionUID = -7247378225730684090L;

	private C4Game c4Game;

	// private JRadioButtonMenuItem rbMenuItem;
	// private JCheckBoxMenuItem cbMenuItem;
	private JFrame m_frame;
	private int selectedAgent = 0;

	// private final String agentList[] = { "Human", "Minimax", "TDS", "Random"
	// };
	private final String agentTypes[] = { "Agent X", "Agent O", "Agent Eval" };

	C4Menu(C4Game game, JFrame frame) {
		c4Game = game;
		m_frame = frame;

		generateFileMenu();
		generateTDAgentMenu();
		generateCompetitionMenu();
		generateOptionsMenu();
		generateHelpMenu();

	}

	private void generateFileMenu() {
		JMenu menu;
		JMenuItem menuItem;

		// Build the first menu.
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("File Options");

		// ==============================================================
		// Quit Program
		// ==============================================================
		menuItem = new JMenuItem("Quit Program", KeyEvent.VK_T);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
				ActionEvent.ALT_MASK));
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_frame.setVisible(false);
				m_frame.dispose();
				System.exit(0);
			}
		});
		menuItem.setToolTipText(TIPEVALUATE);
		menu.add(menuItem);

		add(menu);
	}

	private void generateTDAgentMenu() {
		JMenu menu, submenu;
		JMenuItem menuItem;

		menu = new JMenu("TD-Agents");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"Options for TD-Agent");
		for (int i = 0; i < 3; i++) {
			final int j = i;
			// ==============================================================
			// Agent X Submenu
			// ==============================================================
			submenu = new JMenu(agentTypes[i]);
			// submenu.setMnemonic(KeyEvent.VK_S);

			// ==============================================================
			// Load Agent X
			// ==============================================================
			menuItem = new JMenuItem("Load Agent");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					loadAgent(j);
				}
			});
			menuItem.setToolTipText(TIPLOAD);
			submenu.add(menuItem);

			// ==============================================================
			// Save Agent X
			// ==============================================================
			menuItem = new JMenuItem("Save Agent");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveAgent(j);
				}
			});
			menuItem.setToolTipText(TIPSAVE);
			submenu.add(menuItem);
			submenu.addSeparator();

			// ==============================================================
			// Load Weights into Agent
			// ==============================================================
			menuItem = new JMenuItem("Import Agent Weights");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					loadAgentWeights(j);
				}
			});
			// menuItem.setToolTipText(TIPSAVE);
			submenu.add(menuItem);
			//submenu.addSeparator();

			// ==============================================================
			// Save Weights of an Agent
			// ==============================================================
			menuItem = new JMenuItem("Export Agent Weights");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveAgentWeights(j);
				}
			});
			//menuItem.setToolTipText(TIPSAVE);
			submenu.add(menuItem);
			submenu.addSeparator();

			// ==============================================================
			// Show N-Tuples Agent X
			// ==============================================================
			// menuItem = new JMenuItem("Show N-Tuples");
			// menuItem.addActionListener(new ActionListener() {
			// public void actionPerformed(ActionEvent e) {
			// showNTuples(j);
			// }
			// });
			// menuItem.setToolTipText(TIPSHOWNTUPLE);
			// submenu.add(menuItem);

			// ==============================================================
			// Set N-Tuples of Agent X
			// ==============================================================
			menuItem = new JMenuItem("Show / Change N-Tuples");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setNTuples(j);
				}
			});
			menuItem.setToolTipText(TIPSETNTUPLES);
			submenu.add(menuItem);

			// ==============================================================
			// Show LUT of Agent X
			// ==============================================================
			menuItem = new JMenuItem("Inspect LUT");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					inspectLUT(j);
				}
			});
			menuItem.setToolTipText(TIPINSPECTLUT);
			submenu.add(menuItem);

			// ==============================================================
			// Evaluate TD-Agent Agent X
			// ==============================================================
			menuItem = new JMenuItem("Quick Evaluation");
			menuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					evaluate(j);
				}
			});

			menuItem.setToolTipText(TIPEVALUATE);
			submenu.add(menuItem);
			menu.add(submenu);
		}

		menu.addSeparator();
		submenu = new JMenu("Multi-Training");
		// ==============================================================
		// Options for Multi-Training
		// ==============================================================
		menuItem = new JMenuItem("Options");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c4Game.c4Buttons.setWindowPos(c4Game.winMultiTrainOptions);
			}
		});
		menuItem.setToolTipText("Set Options for the multi-training. The agent that shall "
				+ "be trained (TD) and the opponent must be selected here.");
		submenu.add(menuItem);

		// ==============================================================
		// Start Multi-Training
		// ==============================================================
		menuItem = new JMenuItem("Start Multi-Training");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c4Game.changeState(State.MULTITRAIN);
				String str = "[Start Multi-Training of TD-Agent]";
				printStatus(str);
			}
		});
		menuItem.setToolTipText("Start the multi-training of the TD-Agent. Be sure thtat the options "
				+ "where choosen correctly and that both agents (TD and opponent) are initialized");
		submenu.add(menuItem);

		menu.add(submenu);
		add(menu);
	}

	private void generateCompetitionMenu() {
		JMenu menu, submenu;
		JMenuItem menuItem;

		menu = new JMenu("Competition");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"Options for Competition");

		submenu = new JMenu("Competition");

		// ==============================================================
		// Competition-Options
		// ==============================================================
		menuItem = new JMenuItem("Competition-Options");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c4Game.c4Buttons.setWindowPos(c4Game.winCompOptions);
			}
		});
		menuItem.setToolTipText("Choose the options for single- and multi-Competitions");
		submenu.add(menuItem);

		// ==============================================================
		// Single-Competition
		// ==============================================================
		menuItem = new JMenuItem("Single-Competition");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c4Game.state = State.COMPETE;
				c4Game.changeState(State.COMPETE);
				String str = "[Start Single Competition]";
				printStatus(str);
			}
		});
		menuItem.setToolTipText("<html><body>Start a single competition between the selected Agents "
				+ "(must be selected in the Competion-Options. The results are printed to <br>"
				+ "a new window</body></html>");
		submenu.add(menuItem);

		// ==============================================================
		// Multi-Competition
		// ==============================================================
		menuItem = new JMenuItem("Multi-Competition");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c4Game.state = State.MULTICOMPETE;
				c4Game.changeState(State.MULTICOMPETE);
				String str = "[Start multi-Competition]";
				printStatus(str);
			}
		});
		menuItem.setToolTipText("<html><body>start a multi-competition. between the selected "
				+ "Agents (must be selected in the Competion-Options. The results <br>"
				+ "(Overview and single-Results are) printed to new window</body></html>");
		submenu.add(menuItem);
		menu.add(submenu);

		submenu = new JMenu("Test Value-Function");

		// ==============================================================
		// Test Value-Function Options
		// ==============================================================
		menuItem = new JMenuItem("Options");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c4Game.c4Buttons.setWindowPos(c4Game.winValueFuncOptions);
			}
		});
		menuItem.setToolTipText("Choose the options for the test of the value-function of an agent.");
		submenu.add(menuItem);

		// ==============================================================
		// Test Value-Function
		// ==============================================================
		menuItem = new JMenuItem("Test Value-Function");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c4Game.changeState(State.TESTVALUEFUNC);
				String str = "[Start test of the value-function]";
				printStatus(str);
			}
		});
		menuItem.setToolTipText("Start a Test of the Value-Function. "
				+ "The results will be put to a new window.");
		submenu.add(menuItem);

		// ==============================================================
		// Test finding best moves
		// ==============================================================
		menuItem = new JMenuItem("Test finding best moves");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c4Game.changeState(State.TESTBESTMOVE);
				String str = "[Start test for finding best-moves]";
				printStatus(str);
			}
		});
		menuItem.setToolTipText("<html><body>Check whether the selected agentis able to find "
				+ "the best move in special situations (positions where there<br> "
				+ "is only ONE best move</body></html>");
		submenu.add(menuItem);
		menu.add(submenu);

		add(menu);
	}

	private void generateOptionsMenu() {
		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"Options for Competition");

		// ==============================================================
		// Options for Game-Theoretic Values
		// ==============================================================
		menuItem = new JMenuItem("Game-Theoretic Values");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				c4Game.c4Buttons.setWindowPos(c4Game.winOptionsGTV);
			}
		});
		menuItem.setToolTipText("<html><body>1. Set the Size of the Hash-Table "
				+ "for the Agent that determines the game-theoretic <br>"
				+ "values in a lot of situations. If the Hash-Size is made "
				+ "smaller more time is needed to calculate the score <br>"
				+ "for a position. <br> "
				+ "2.Choose which databases shall be used "
				+ "for the Agent that determines the game-theoretic values in a lot of <br>"
				+ "Situations. The usage of the databases needes more memory but"
				+ " fastens the agent a lot <br>"
				+ "3. Set the Search Depth for the Agent "
				+ "that determines the game-theoretic values. The Search-depth <br>"
				+ "should be predefined value</body></html>");
		menu.add(menuItem);

		add(menu);
	}

	private void generateHelpMenu() {
		JMenu menu;
		JMenuItem menuItem;

		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("Help");

		// ==============================================================
		// Options for Game-Theoretic Values
		// ==============================================================
		menuItem = new JMenuItem("Show Help-File");
		menuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				java.net.URL url = getClass().getResource("/doc/index.htm");
				ShowBrowser.openURL(url);
			}
		});
		menuItem.setToolTipText("<html><body>Show Help-File</body></html>");
		menu.add(menuItem);

		add(menu);
	}

	void loadAgent(int index) {
		switch (index) {
		case 0:
			c4Game.changeState(State.LOAD_X);
			break;
		case 1:
			c4Game.changeState(State.LOAD_O);
			break;
		case 2:
			c4Game.changeState(State.LOAD_EVAL);
			break;
		}
	}

	void saveAgent(int index) {
		switch (index) {
		case 0:
			c4Game.changeState(State.SAVE_X);
			break;
		case 1:
			c4Game.changeState(State.SAVE_O);
			break;
		case 2:
			c4Game.changeState(State.SAVE_EVAL);
			break;
		}
	}

	void loadAgentWeights(int index) {
		switch (index) {
		case 0:
			c4Game.changeState(State.LOAD_WEIGHTS_X);
			break;
		case 1:
			c4Game.changeState(State.LOAD_WEIGHTS_O);
			break;
		case 2:
			c4Game.changeState(State.LOAD_WEIGHTS_EVAL);
			break;
		}
	}

	void saveAgentWeights(int index) {
		switch (index) {
		case 0:
			c4Game.changeState(State.SAVE_WEIGHTS_X);
			break;
		case 1:
			c4Game.changeState(State.SAVE_WEIGHTS_O);
			break;
		case 2:
			c4Game.changeState(State.SAVE_WEIGHTS_EVAL);
			break;
		}
	}

	@SuppressWarnings("unused")
	private void showNTuples(int index) {
		selectedAgent = index;
		String str = "[Stop Show-Ntuples]";
		if (c4Game.state != State.SHOWNTUPLE) {
			c4Game.changeState(State.SHOWNTUPLE);
			str = "[Show N-Tuples]";
		} else
			c4Game.changeState(State.IDLE);
		printStatus(str);
	}

	private void setNTuples(int index) {
		selectedAgent = index;
		if (c4Game.state != State.SETNTUPLE) {
			c4Game.changeState(State.SETNTUPLE);
			String str = "[Set N-Tuples]";
			printStatus(str);
		} else {
			c4Game.winConfigTuples.setVisible(false);
			c4Game.changeState(State.IDLE);
		}
	}

	private void inspectLUT(int index) {
		selectedAgent = index;
		if (c4Game.state != State.INSPNTUPLE) {
			c4Game.changeState(State.INSPNTUPLE);
			String str = "[Inspect N-Tuple LUTs]";
			printStatus(str);
		} else {
			c4Game.winLUTShow.setVisible(false);
			c4Game.changeState(State.IDLE);
		}
	}

	private void evaluate(int index) {
		selectedAgent = index;
		c4Game.changeState(State.EVALUATE);
		String str = "[Start Evaluation of TD-Agent]";
		printStatus(str);
	}

	public int getSelectedAgent() {
		return selectedAgent;
	}

	private void printStatus(String str) {
		c4Game.c4Buttons.printStatus(str);
	}

	public void setEnabledMenus(int[] menuList, boolean enable) {
		for (int i : menuList) {
			JMenu men = getMenu(i);
			men.setEnabled(enable);
		}
	}

}
