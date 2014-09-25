package gui;

import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import c4.TDParams;

public class OptionsTD extends Frame {
	private static final long serialVersionUID = 1L;
	private Label alphaL;
	private Label alfinL;
	private Label epsilL;
	private Label epfinL;
	private Label lambdaL;
	private Label gammaL;
	private Label numGamesL;
	private Label stopAfterL;
	private Label alphaChangeTypeL;
	private Label epsilonChangeTypeL;
	private Label randModeL;
	private Label lNTuples;
	private Label randTupleLenL;
	private Label randTupleNumL;
	private JLabel extraParamL;
	private Label posValsL;

	private TextField alphaT;
	private TextField alfinT;
	private TextField epsilT;
	private TextField epfinT;
	private TextField lambdaT;
	private TextField gammaT;
	private TextField gameNumT;
	private TextField stopAfterT;
	private TextField randTupleLenT;
	private TextField randTupleNumT;
	private JTextField extraParamAlphaT;
	private JTextField extraParamEpsilonT;

	private Choice AlphaChangeTypeC;
	private Choice epsilonChangeTypeC;
	private Choice randMode;
	private Choice posValsC;

	private Checkbox randNTuples;
	private Checkbox useSym;
	private Checkbox randInitWeights;

	private JButton ok;
	private OptionsTD m_par;
	
	private int[][] nTuples = null;

	public OptionsTD() {
		super("TD Parameter");
		alphaT = new TextField("0.001");
		alfinT = new TextField("0.001");
		epsilT = new TextField("0.9");
		epfinT = new TextField("0.1");
		lambdaT = new TextField("0.0");
		gammaT = new TextField("1.0");
		alphaL = new Label("Alpha init");
		alfinL = new Label("Alpha final");
		epsilL = new Label("Epsilon init");
		epfinL = new Label("Epsilon final");
		lambdaL = new Label("Lambda");
		gammaL = new Label("Gamma");

		numGamesL = new Label("Game Number");
		gameNumT = new TextField("100000");

		stopAfterL = new Label("Reward after x Moves:");
		stopAfterT = new TextField("100");

		alphaChangeTypeL = new Label("Alpha Change");
		AlphaChangeTypeC = new Choice();
		AlphaChangeTypeC.add("exponetial");
		AlphaChangeTypeC.add("linear");
		AlphaChangeTypeC.add("tanh");
		AlphaChangeTypeC.add("Step-Function");
		AlphaChangeTypeC.setEnabled(false);

		epsilonChangeTypeL = new Label("Epsilon Change");
		epsilonChangeTypeC = new Choice();
		epsilonChangeTypeC.add("exponential");
		epsilonChangeTypeC.add("linear");
		epsilonChangeTypeC.add("tanh");
		epsilonChangeTypeC.add("Step-Function");

		lNTuples = new Label("N-Tuples");

		randNTuples = new Checkbox();
		randNTuples.setLabel("Random N-Tuples");

		randModeL = new Label("N-Tuple Type");
		randMode = new Choice();
		randMode.add("Points(Equal length)");
		randMode.add("Points");
		randMode.add("Walk(Equal length)");
		randMode.add("Walk");
		randMode.setEnabled(false);
		
		randTupleNumL= new Label("Tuple Num");
		randTupleNumT = new TextField("50");
		
		randTupleLenL = new Label("Tuple Length");
		randTupleLenT = new TextField("10");

		useSym = new Checkbox();
		useSym.setLabel("Use Symmetry");
		useSym.setState(true);

		randInitWeights = new Checkbox();
		randInitWeights.setLabel("Random Weight Init");
		
		extraParamL = new JLabel("Extra Param");
		extraParamAlphaT = new JTextField("20000");
		extraParamEpsilonT = new JTextField("20000");
		
		String str = "Extra Param for Tanh (WP) or Step-Function (Jumping-Point).";
		extraParamL.setToolTipText(str);
		extraParamAlphaT.setToolTipText("Currently not available");
		extraParamAlphaT.setEnabled(false);
		extraParamEpsilonT.setToolTipText(str);
		
		posValsL = new Label("Pos. Values / field");
		posValsC = new Choice();
		posValsC.add("3");
		posValsC.add("4");
		
		
		ok = new JButton("OK");
		m_par = this;
		
		// =========================================================
		randMode.setEnabled(false);
		randModeL.setEnabled(false);
		randModeL.setEnabled(false);
		randTupleNumL.setEnabled(false);
		randTupleNumT.setEnabled(false);
		randTupleLenL.setEnabled(false);
		randTupleLenT.setEnabled(false);

		randNTuples.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (randNTuples.getState() == true) {
					randMode.setEnabled(true);
					randModeL.setEnabled(true);
					randModeL.setEnabled(true);
					randTupleNumL.setEnabled(true);
					randTupleNumT.setEnabled(true);
					randTupleLenL.setEnabled(true);
					randTupleLenT.setEnabled(true);
				} else {
					randMode.setEnabled(false);
					randModeL.setEnabled(false);
					randModeL.setEnabled(false);
					randTupleNumL.setEnabled(false);
					randTupleNumT.setEnabled(false);
					randTupleLenL.setEnabled(false);
					randTupleLenT.setEnabled(false);
				}
			}
		});
		

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_par.setVisible(false);
			}
		});

		setLayout(new GridLayout(0, 4, 10, 10));

		add(alphaL);
		add(alphaT);
		add(epsilL);
		add(epsilT);

		add(alfinL);
		add(alfinT);
		add(epfinL);
		add(epfinT);

		add(lambdaL);
		add(lambdaT);
		add(gammaL);
		add(gammaT);

		add(numGamesL);
		add(gameNumT);
		add(stopAfterL);
		add(stopAfterT);
		
		add(alphaChangeTypeL);
		add(AlphaChangeTypeC);
		add(epsilonChangeTypeL);
		add(epsilonChangeTypeC);
		
		add(extraParamL);
		add(extraParamAlphaT);
		add(new Canvas());
		add(extraParamEpsilonT);

		add(lNTuples);
		add(randNTuples);
		add(new Canvas());
		add(new Canvas());
		
		add(new Canvas());
		add(randModeL);
		add(randMode);
		add(new Canvas());
		
		add(new Canvas());
		add(randTupleNumL);
		add(randTupleNumT);
		add(new Canvas());
		
		add(new Canvas());
		add(randTupleLenL);
		add(randTupleLenT);
		add(new Canvas());

		add(new Canvas());
		add(useSym);
		add(new Canvas());
		add(new Canvas());

		add(new Canvas());
		add(randInitWeights);
		add(new Canvas());
		add(new Canvas());
		
		add(new Canvas());
		add(posValsL);
		add(posValsC);
		add(new Canvas());

		add(new Canvas());
		add(ok);

		pack();
		setVisible(false);
	}

	public double getAlpha() {
		return Double.valueOf(alphaT.getText()).doubleValue();
	}

	public double getAlphaFinal() {
		return Double.valueOf(alfinT.getText()).doubleValue();
	}

	public double getEpsilon() {
		return Double.valueOf(epsilT.getText()).doubleValue();
	}

	public double getEpsilonFinal() {
		return Double.valueOf(epfinT.getText()).doubleValue();
	}

	public double getLambda() {
		return Double.valueOf(lambdaT.getText()).doubleValue();
	}

	public double getGamma() {
		return Double.valueOf(gammaT.getText()).doubleValue();
	}

	public int getNumGames() {
		return Integer.valueOf(gameNumT.getText()).intValue();
	}
	
	public int getAlphaChangeType() {
		return AlphaChangeTypeC.getSelectedIndex();
	}
	
	public int getEpsilonChange() {
		return epsilonChangeTypeC.getSelectedIndex();
	}
	
	public int getStopAfter() {
		return Integer.valueOf(stopAfterT.getText()).intValue();
	}
	
	public boolean getRandNTuples() {
		return randNTuples.getState();
	}
	
	public int getTupleNum() {
		return Integer.valueOf(randTupleNumT.getText()).intValue();
	}
	
	public int getTupleLen() {
		return Integer.valueOf(randTupleLenT.getText()).intValue();
	}
	
	public boolean getRandTuple() {
		return randNTuples.getState();
	}
	
	public int getRandMode() {
		return randMode.getSelectedIndex();
	}
	
	public boolean getRandInitWeights() {
		return randInitWeights.getState();
	}
	
	public boolean getUseSymmetry() {
		return useSym.getState();
	}
	
	public double getAlphaChangeParam() {
		return Double.valueOf(extraParamAlphaT.getText()).doubleValue();
	}
	
	public double getEpsilonChangeParam() {
		return Double.valueOf(extraParamEpsilonT.getText()).doubleValue();
	}
	
	public int getPosVals() {
		return posValsC.getSelectedIndex() + 3;
	}
	
	public TDParams getTDParams() {
		TDParams tdPar = new TDParams();
		
		tdPar.trainGameNum = getNumGames();
		tdPar.stopAfterMoves = getStopAfter();
		tdPar.alphaInit = getAlpha();
		tdPar.alphaFinal = getAlphaFinal();
		tdPar.alphaChangeMethod = getAlphaChangeType();
		tdPar.epsilonInit = getEpsilon();
		tdPar.epsilonFinal = getEpsilonFinal();
		tdPar.epsilonMethod = getEpsilonChange();
		tdPar.gamma = getGamma();
		tdPar.lambda = getLambda();
		tdPar.randNTuples = getRandNTuples();
		tdPar.randTupleNum = getTupleNum();
		tdPar.randTupleLen = getTupleLen();
		tdPar.randMode = getRandMode();
		tdPar.useSymmetry = getUseSymmetry();
		tdPar.randInitWeights = getRandInitWeights();
		tdPar.alphaChangeParam = getAlphaChangeParam();
		tdPar.epsilonChangeParam = getEpsilonChangeParam();
		tdPar.posVals = getPosVals();
		
		if(nTuples != null) {
			tdPar.nTuples = nTuples;
		}
		
		return tdPar;
	}
	
	public void setNTuples(int[][] nTuples) {
		this.nTuples = nTuples;
	}
}
