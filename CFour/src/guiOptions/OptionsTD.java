package guiOptions;

import gui.GridLayout2;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import nTupleTD.TDParams;
import nTupleTD.TDParams.Activation;
import nTupleTD.TDParams.GameInterval;
import nTupleTD.TDParams.UpdateMethod;

import adaptableLearningRates.AlphaBound;
import adaptableLearningRates.AutoStep;
import adaptableLearningRates.ELK1;
import adaptableLearningRates.IDBD;
import adaptableLearningRates.IDBDwk;
import adaptableLearningRates.STDLearningRate;
import adaptableLearningRates.TemporalCoherence;
import agentIO.JFileChooserApprove;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

//import com.jgoodies.looks.windows.WindowsLookAndFeel;


public class OptionsTD extends JFrame {

	private static final String DEFAULT_TDPAR_DIR = "tdPar";

	private static final int TITLEFONTSIZE = 20;

	private static final long serialVersionUID = 1L;

	private static final double SIGFACREFERENCE = 70;

	private JLabel lGeneral;
	private JLabel lAlpha;
	private JLabel lAlphafin;

	private JLabel lEpsInit;
	private JLabel lEpsFinal;
	private JLabel lEpsChangeType;
	private JLabel lEpsExtraParam;
	private JLabel lEpsSlope;
	private JLabel lambdaL;
	private JLabel gammaL;
	private JLabel numGamesL;
	private JLabel stopAfterL;
	private JLabel randModeL;
	private JLabel lNTuples;
	private JLabel randTupleLenL;
	private JLabel randTupleNumL;
	private JLabel extraParamAlphaL;
	private JLabel posValsL;
	private JLabel nPlyLookaheadL;
	// private JLabel lInfoInterval;

	private JLabel lhashIndex;
	private JLabel lhashSize;
	private JLabel lIdbdTitle;
	private JLabel lIdbdBetaInit;
	private JLabel lIdbdTheta;
	private JLabel lIdbdWkOmega;
	private JLabel lIndexHashingTitle;
	private JLabel lWeightUpdateMethod;
	private JLabel lEligTraces;

	// TCL
	private JLabel lTclTitle;
	private JLabel lTclInitNA;
	private JLabel lTclDiscountInit;
	private JLabel lTclDiscountFinal;
	private JLabel lTclDiscountBiasInit;
	private JLabel lTclDiscountBiasFinal;
	private JLabel lTclEpsisodeLength;
	private JLabel lTclExpSchemeA;
	private JLabel lTclExpSchemeB;

	private JTextField tTclInitNA;
	private JTextField tTclDiscountInit;
	private JTextField tTclDiscountFinal;
	private JTextField tTclDiscountBiasInit;
	private JTextField tTclDiscountBiasFinal;
	private JTextField tTclEpsiodeLength;
	private JTextField tTclExpSchemeA;
	private JTextField tTclExpSchemeB;

	private JCheckBox cbTclUseEpisodes;
	private JCheckBox cbTclUseExpScheme;

	private JRadioButton rbTclUpdateWeightsNA;
	private JRadioButton rbTclUpdateNAWeights;
	private JRadioButton rbTclUpdateRecommendedWC;
	private JRadioButton rbTclUpdateErrorSignal;
	private ButtonGroup bgTclUpdateOrder;
	private ButtonGroup bgTclUpdateSignal;

	private JTextField tAlpha;
	private JTextField tAlphafin;
	private JTextField tEpsIn;
	private JTextField tEpsfin;
	private JTextField tEpsSlope;
	private JTextField tLambda;
	private JTextField gammaT;
	private JTextField gameNumT;
	private JTextField stopAfterT;
	private JTextField randTupleLenT;
	private JTextField randTupleNumT;
	private JTextField extraParamAlphaT;
	private JTextField tEpsExtraParam;
	private JTextField nPlyLookAheadT;
	// private JTextField tInfoInterval;
	private JTextField tSigOutputFac;
	private JTextField tIdbdBetaInit;
	private JTextField tIdbdTheta;
	private JTextField tIdbdWkOmega;
	private JTextField tNumEvaluationMatches;

	private JComboBox<String> comboAlphaChangeType;
	private JComboBox<String> comboEpsilonChangeType;
	private JComboBox<String> comboRandMode;
	private JComboBox<String> comboPosVals;
	private JComboBox<String> comboHashSize;
	private JComboBox<String> comboWeightUpdateMethod;
	private JComboBox<String> comboActivation;

	private JCheckBox cbEpsRandUpdate;
	private JCheckBox randNTuples;
	private JCheckBox useSym;
	private JCheckBox randInitWeights;
	private JCheckBox cbSingleMatch;
	private JCheckBox cbEvaluateGames;
	private JCheckBox cbEvaluate012;
	private JCheckBox cbHashIntern;
	private JCheckBox cbHashExtern;
	// private JCheckBox cbUseActivationFunction;
	private JCheckBox cbUseBiasWeight;
	private JCheckBox cbEligReplacingTraces;
	private JCheckBox cbEligResetOnRandomMove;

	private JButton ok;
	private JButton bSave, bOpen;
	private JButton bSigOutputFac;
	private JButton bLoadBest;
	private JButton bChangeIntervals;
	
	JFileChooserApprove fc;

	private OptionsIntervals intervalListOpts = null;

	private OptionsTD m_par;

	private Integer[][] nTuples = null;

	public OptionsTD(TDParams tdPar) {
		super("TD Parameter");
		
		fc = new JFileChooserApprove();
		fc.setCurrentDirectory(new File(DEFAULT_TDPAR_DIR));

		Font fontTitle = new Font("Serif", Font.BOLD, TITLEFONTSIZE);

		lGeneral = new JLabel("General");
		lGeneral.setFont(fontTitle);

		lWeightUpdateMethod = new JLabel("Learning Rate Adaption", JLabel.RIGHT);
		comboWeightUpdateMethod = new JComboBox<String>(
				TDParams.WEIGHTUPDATEMETHOD);
		comboWeightUpdateMethod.setMaximumRowCount(10);
		
		cbEpsRandUpdate = new JCheckBox("Update on random move");

		tAlpha = new JTextField();
		tAlphafin = new JTextField();
		tEpsIn = new JTextField();
		tEpsfin = new JTextField();
		tLambda = new JTextField();
		tLambda.setEnabled(true);
		gammaT = new JTextField();
		lAlpha = new JLabel("<html>\u03B1<sub>init</sub></html>", JLabel.RIGHT);
		lAlphafin = new JLabel("<html>\u03B1<sub>final</sub></html>",
				JLabel.RIGHT);
		lEpsInit = new JLabel("<html>\u03B5<sub>init</sub></html>",
				JLabel.RIGHT);
		lEpsFinal = new JLabel("<html>\u03B5<sub>final</sub></html>",
				JLabel.RIGHT);
		lEpsSlope = new JLabel("\u03B5 Slope (m)", JLabel.RIGHT);
		tEpsSlope = new JTextField();

		// cbUseActivationFunction = new JCheckBox("Use activation Func.");
		// //TODO: removed...

		cbUseBiasWeight = new JCheckBox("Use Bias-weight");

		lambdaL = new JLabel("\u03BB", JLabel.RIGHT);
		gammaL = new JLabel("\u03B3", JLabel.RIGHT);

		lEligTraces = new JLabel("Eligibilty Traces", JLabel.RIGHT);
		cbEligResetOnRandomMove = new JCheckBox("Reset on rand. move", true);
		cbEligReplacingTraces = new JCheckBox("Replacing Traces", true);
		tNumEvaluationMatches = new JTextField();

		numGamesL = new JLabel("Game Number (millions)", JLabel.RIGHT);
		gameNumT = new JTextField();

		stopAfterL = new JLabel("Reward after x Moves:", JLabel.RIGHT);
		stopAfterT = new JTextField();

		String chType[] = new String[] { "exponential", "linear", "tanh",
				"Step-Function" };
		new JLabel("\u03B1 Change", JLabel.RIGHT);
		comboAlphaChangeType = new JComboBox<String>(chType);
		comboAlphaChangeType.setEnabled(false);

		lEpsExtraParam = new JLabel("\u03B5 :Extra Param", JLabel.RIGHT);
		lEpsChangeType = new JLabel("\u03B5 Change", JLabel.RIGHT);
		comboEpsilonChangeType = new JComboBox<String>(chType);
		comboEpsilonChangeType.setSelectedIndex(2);

		new JLabel("Output Sig. Factor (k)", JLabel.RIGHT);
		tSigOutputFac = new JTextField();
		bSigOutputFac = new JButton("proposal");
		new JLabel("V(x)=tanh(f(x)*k)");

		lIdbdTitle = new JLabel("IDBD");
		lIdbdTitle.setFont(fontTitle);
		lIdbdBetaInit = new JLabel("<html>\u03D0<sub>init</sub></html>",
				JLabel.RIGHT);
		lIdbdTheta = new JLabel("\u03B8", JLabel.RIGHT);
		tIdbdBetaInit = new JTextField();
		tIdbdTheta = new JTextField();
		tIdbdWkOmega = new JTextField();
		lIdbdWkOmega = new JLabel("<html>\u03c9<sub>k</sub></html>",
				JLabel.RIGHT);

		lTclTitle = new JLabel("TCL");
		lTclTitle.setFont(fontTitle);
		lTclInitNA = new JLabel("Init Tables", JLabel.RIGHT);
		tTclInitNA = new JTextField();
		lTclInitNA.setEnabled(false);
		tTclInitNA.setEnabled(false);
		cbTclUseEpisodes = new JCheckBox("Use Update Epsiodes");
		lTclEpsisodeLength = new JLabel("Episode-length", JLabel.RIGHT);
		tTclEpsiodeLength = new JTextField();
		lTclDiscountInit = new JLabel("<html>\u03BC<sub>init</sub></html>",
				JLabel.RIGHT);
		tTclDiscountInit = new JTextField();
		lTclDiscountFinal = new JLabel("\u03BC final", JLabel.RIGHT);
		tTclDiscountFinal = new JTextField();
		lTclDiscountBiasInit = new JLabel("\u03BC init (bias)", JLabel.RIGHT);
		tTclDiscountBiasInit = new JTextField();
		lTclDiscountBiasFinal = new JLabel("\u03BC final (bias)", JLabel.RIGHT);
		tTclDiscountBiasFinal = new JTextField();
		rbTclUpdateNAWeights = new JRadioButton("Weight-Factors -- Weights");
		rbTclUpdateWeightsNA = new JRadioButton("Weights -- Weight-Factors");
		rbTclUpdateErrorSignal = new JRadioButton("Use Error Signal");
		rbTclUpdateRecommendedWC = new JRadioButton("Use rec. Weight Change");
		bgTclUpdateOrder = new ButtonGroup();
		bgTclUpdateOrder.add(rbTclUpdateNAWeights);
		bgTclUpdateOrder.add(rbTclUpdateWeightsNA);
		bgTclUpdateOrder.setSelected(rbTclUpdateNAWeights.getModel(), true);
		bgTclUpdateSignal = new ButtonGroup();
		bgTclUpdateSignal.add(rbTclUpdateErrorSignal);
		bgTclUpdateSignal.add(rbTclUpdateRecommendedWC);
		bgTclUpdateSignal
				.setSelected(rbTclUpdateRecommendedWC.getModel(), true);

		lTclExpSchemeA = new JLabel("<html>\u03D0</html>", JLabel.RIGHT);
		lTclExpSchemeB = new JLabel("Exp. Scheme Fac. B", JLabel.RIGHT);
		tTclExpSchemeA = new JTextField();
		tTclExpSchemeB = new JTextField();
		cbTclUseExpScheme = new JCheckBox("Use exp. Scheme");

		lNTuples = new JLabel("N-Tuples");
		lNTuples.setFont(fontTitle);

		randNTuples = new JCheckBox("Random N-Tuples");
		randNTuples.setToolTipText("otherwise use current N-Tuple-System");

		randModeL = new JLabel("N-Tuple Type", JLabel.RIGHT);
		String randType[] = new String[] { "Points(Equal length)", "Points",
				"Walk(Equal length)", "Walk" };
		comboRandMode = new JComboBox<String>(randType);
		comboRandMode.setEnabled(false);
		comboRandMode.setSelectedIndex(2);

		randTupleNumL = new JLabel("Tuple Num", JLabel.RIGHT);
		randTupleNumT = new JTextField();

		randTupleLenL = new JLabel("Tuple Length", JLabel.RIGHT);
		randTupleLenT = new JTextField();

		useSym = new JCheckBox("Use Symmetry");
		useSym.setSelected(true);

		randInitWeights = new JCheckBox("Random Weight Init");

		extraParamAlphaL = new JLabel("\u03B1 Extra Param", JLabel.RIGHT);
		extraParamAlphaT = new JTextField();
		tEpsExtraParam = new JTextField();

		String str = "Extra Param for Tanh (WP) or Step-Function (Jumping-Point).";
		extraParamAlphaL.setToolTipText(str);
		extraParamAlphaT.setToolTipText("Currently not available");
		extraParamAlphaT.setEnabled(false);
		tEpsExtraParam.setToolTipText(str);

		nPlyLookaheadL = new JLabel("n-Ply Look-ahead", JLabel.RIGHT);
		nPlyLookAheadT = new JTextField();

		posValsL = new JLabel("Pos. Values / field", JLabel.RIGHT);
		comboPosVals = new JComboBox<String>(new String[] { "3", "4" });
		comboPosVals.setSelectedIndex(1);

		bSave = new JButton("Save");
		bOpen = new JButton("Open");

		// lInfoInterval = new JLabel("Info-Interval", JLabel.RIGHT);

		// tInfoInterval = new JTextField();
		cbSingleMatch = new JCheckBox("Single-Match");
		cbSingleMatch.setSelected(true);

		cbEvaluateGames = new JCheckBox("Evaluation matches:");
		cbEvaluateGames.setSelected(true);
		cbEvaluate012 = new JCheckBox("boards: 0,1,2 stones");
		cbEvaluate012.setSelected(false);

		lIndexHashingTitle = new JLabel("Index-Hashing");
		lIndexHashingTitle.setFont(fontTitle);
		lhashIndex = new JLabel("Hash Index-Sets", JLabel.RIGHT);
		cbHashIntern = new JCheckBox("Training");
		cbHashIntern.setSelected(true);
		cbHashExtern = new JCheckBox("extern access");
		cbHashExtern.setSelected(true);
		lhashSize = new JLabel("Hash-size", JLabel.RIGHT);
		comboHashSize = new JComboBox<String>(new String[] {
				4194304 / 512 + "", 4194304 / 256 + "", 4194304 / 128 + "",
				4194304 / 64 + "" });
		comboHashSize.setSelectedIndex(2);

		comboActivation = new JComboBox<String>(TDParams.ACTIVATION);

		// -----------------------------------------------------------------
		// Create Window to chose the Evaluation Intervals
		// -----------------------------------------------------------------
		intervalListOpts = new OptionsIntervals(this, tdPar.infoInterval); // TODO

		bChangeIntervals = new JButton("Change Intervals");

		bLoadBest = new JButton("Load Best");

		ok = new JButton("OK");
		m_par = this;

		// =========================================================
		comboRandMode.setEnabled(false);
		randModeL.setEnabled(false);
		randModeL.setEnabled(false);
		randTupleNumL.setEnabled(false);
		randTupleNumT.setEnabled(false);
		randTupleLenL.setEnabled(false);
		randTupleLenT.setEnabled(false);

		comboWeightUpdateMethod.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// int index = cbxWeightUpdateMethod.getSelectedIndex();
				UpdateMethod meth = getUpdateMethod();
				enableGUIElements(meth);
				printUpdateMethTitle(meth);
			}

		});

		cbTclUseEpisodes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				enableEpisodes();
			}
		});

		cbTclUseExpScheme.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				enableExpScheme();
			}
		});

		randNTuples.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				setRandElementState(randNTuples.isSelected());
			}
		});

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_par.setVisible(false);
			}
		});

		bSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				m_par.saveTDParams(getTDParams());
			}
		});

		bOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TDParams td = m_par.loadTDParams();
				setTDParams(td);
			}
		});

		bSigOutputFac.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double fac = SIGFACREFERENCE / (double) getTupleNum();
				if (!getUseSymmetry())
					fac *= 2;
				tSigOutputFac.setText(fac + "");
			}
		});

		bLoadBest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadBestParams();
			}
		});

		tLambda.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent arg0) {
				enableEligElements();
			}
		});

		tEpsfin.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent arg0) {
				enableEpsElements();
			}
		});

		tEpsIn.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent arg0) {
				enableEpsElements();
			}

		});

		cbEvaluateGames.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				enableEvaluationMatches();
			}
		});

		bChangeIntervals.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				intervalListOpts.setVisible(true);
			}
		});

		// JPanel p = new JPanel(new GridLayout2(0, 4, 10, 4));
		// p.setBorder(BorderFactory.createTitledBorder("Test"));

		JPanel p = new JPanel(new GridLayout2(0, 4, 10, 4));
		// setLayout(new GridLayout2(0, 4, 10, 4));
		p.add(lGeneral);
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(lWeightUpdateMethod);
		p.add(comboWeightUpdateMethod);
		p.add(bLoadBest);
		p.add(new Canvas());

		p.add(lAlpha);
		p.add(tAlpha);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(lAlphafin);
		p.add(tAlphafin);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(lEpsInit);
		p.add(tEpsIn);
		p.add(lEpsChangeType);
		p.add(comboEpsilonChangeType);

		p.add(lEpsFinal);
		p.add(tEpsfin);
		p.add(lEpsExtraParam);
		p.add(tEpsExtraParam);

		p.add(new Canvas());
		p.add(cbEpsRandUpdate);
		p.add(lEpsSlope);
		p.add(tEpsSlope);

		p.add(lambdaL);
		p.add(tLambda);
		p.add(gammaL);
		p.add(gammaT);

		p.add(lEligTraces);
		p.add(cbEligResetOnRandomMove);
		p.add(cbEligReplacingTraces);
		p.add(new Canvas());

		JLabel lValueFunction = new JLabel("Activation", JLabel.RIGHT);
		p.add(lValueFunction);
		// p.add(cbUseActivationFunction); TODO: removed...
		p.add(comboActivation);
		p.add(cbUseBiasWeight);
		p.add(new Canvas());

		p.add(nPlyLookaheadL);
		p.add(nPlyLookAheadT);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(numGamesL);
		p.add(gameNumT);
		p.add(stopAfterL);
		p.add(stopAfterT);

		// p.add(lInfoInterval);
		// p.add(tInfoInterval);
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(cbEvaluateGames);
		p.add(tNumEvaluationMatches);
		p.add(bChangeIntervals);
		p.add(new Canvas());

		// p.add(cbEvaluate012);
		// p.add(cbInfoIntervalMeasure);

		// p.add(lSigOutputFac);
		// p.add(tSigOutputFac);
		// p.add(bSigOutputFac);
		// p.add(lSigOutputFacInfo);

		p.add(lIdbdTitle);
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(lIdbdBetaInit);
		p.add(tIdbdBetaInit);
		p.add(lIdbdTheta);
		p.add(tIdbdTheta);

		p.add(lIdbdWkOmega);
		p.add(tIdbdWkOmega);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(lTclTitle);
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		// p.add(lTclInitNA); // Not needed anymore
		// p.add(tTclInitNA);
		// p.add(new Canvas());
		// p.add(new Canvas());

		p.add(new Canvas());
		p.add(cbTclUseEpisodes);
		p.add(lTclEpsisodeLength);
		p.add(tTclEpsiodeLength);

		p.add(lTclDiscountInit);
		p.add(tTclDiscountInit);
		p.add(new Canvas());
		p.add(new Canvas());
		// p.add(lTclDiscountFinal);
		// p.add(tTclDiscountFinal);

		// p.add(lTclDiscountBiasInit);
		// p.add(tTclDiscountBiasInit);
		// p.add(lTclDiscountBiasFinal);
		// p.add(tTclDiscountBiasFinal);

		p.add(new Canvas());
		p.add(rbTclUpdateNAWeights);
		p.add(rbTclUpdateWeightsNA);
		p.add(new Canvas());

		p.add(new Canvas());
		p.add(rbTclUpdateErrorSignal);
		p.add(rbTclUpdateRecommendedWC);
		p.add(new Canvas());

		p.add(new Canvas());
		p.add(cbTclUseExpScheme);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(lTclExpSchemeA);
		p.add(tTclExpSchemeA);
		p.add(new Canvas());
		p.add(new Canvas());
		// p.add(lTclExpSchemeB);
		// p.add(tTclExpSchemeB);

		p.add(lNTuples);
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(new Canvas());
		p.add(randNTuples);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(randModeL);
		p.add(comboRandMode);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(randTupleNumL);
		p.add(randTupleNumT);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(randTupleLenL);
		p.add(randTupleLenT);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(useSym);
		p.add(randInitWeights);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(posValsL);
		p.add(comboPosVals);
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(lIndexHashingTitle);
		p.add(new Canvas());
		p.add(new Canvas());
		p.add(new Canvas());

		p.add(lhashIndex);
		p.add(cbHashIntern);
		p.add(cbHashExtern);
		p.add(new Canvas());

		p.add(new Canvas());
		p.add(lhashSize);
		p.add(comboHashSize);
		p.add(new Canvas());

		p.add(new Canvas());
		p.add(bSave);
		p.add(bOpen);
		p.add(ok);

		JScrollPane sp = new JScrollPane(p,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		add(sp);

		// set the Parameters given to all elements
		setTDParams(tdPar);

		pack();
		setVisible(false);
	}

	private void loadBestParams() {
		TDParams tdPar = getTDParams();
		TDParams bestTDPar;
		switch (tdPar.updateMethod) {
		case ALPHABOUND:
			bestTDPar = new AlphaBound(tdPar).getBestParams(tdPar);
			break;
		case AUTOSTEP:
			bestTDPar = new AutoStep(tdPar, 1).getBestParams(tdPar);
			break;
		case ELK1:
			bestTDPar = new ELK1(tdPar, 1).getBestParams(tdPar);
			break;
		case IDBDMSE:
			bestTDPar = new IDBD(tdPar, 1).getBestParams(tdPar);
			break;
		case IDBD_LINEAR:
			bestTDPar = new IDBD(tdPar, 1).getBestParams(tdPar);
			break;
		case IDBD_NONLINEAR:
			bestTDPar = new IDBD(tdPar, 1).getBestParams(tdPar);
			break;
		case IDBD_WK:
			bestTDPar = new IDBDwk(tdPar, 1, null).getBestParams(tdPar);
			break;
		case K1:
			bestTDPar = new ELK1(tdPar, 1).getBestParams(tdPar);
			break;
		case TCL:
			bestTDPar = new TemporalCoherence(tdPar, 1, null)
					.getBestParams(tdPar);
			break;
		case TDL:
			bestTDPar = new STDLearningRate(tdPar).getBestParams(tdPar);
			break;
		default:
			throw new UnsupportedOperationException("Method not supported yet!");
		}
		setTDParams(bestTDPar);
		UpdateMethod meth = bestTDPar.updateMethod;
		enableGUIElements(meth);
	}

	private void printUpdateMethTitle(UpdateMethod meth) {
		switch (meth) {
		case IDBD_LINEAR:
			lIdbdTitle.setText("lin. IDBD");
			break;
		case IDBD_NONLINEAR:
			lIdbdTitle.setText("non-lin. IDBD");
			break;
		case IDBD_WK:
			lIdbdTitle.setText("IDBD_WK");
			break;
		case K1:
			lIdbdTitle.setText("K1");
			break;
		case ELK1:
			lIdbdTitle.setText("ELK1");
			break;
		case AUTOSTEP:
			lIdbdTitle.setText("AUTOSTEP");
			break;
		case TCL:
		case TDL:
			lIdbdTitle.setText("IDBD");
			break;
		case ALPHABOUND:
			break;
		case IDBDMSE:
			lIdbdTitle.setText("IDBD (MSE)");
			break;
		case IRPROP_PLUS:
			lIdbdTitle.setText("IDBD");
			break;
		default:
			throw new UnsupportedOperationException("Method not supported yet!");
		}

	}

	private void enableEpisodes() {
		boolean value = cbTclUseEpisodes.isSelected();
		tTclEpsiodeLength.setEnabled(value);
		lTclEpsisodeLength.setEnabled(value);
		if (value) {
			rbTclUpdateErrorSignal.setSelected(!value);
			rbTclUpdateRecommendedWC.setSelected(value);
		}
		rbTclUpdateErrorSignal.setEnabled(!value);
		rbTclUpdateRecommendedWC.setEnabled(!value);
	}

	private void enableExpScheme() {
		boolean value = cbTclUseExpScheme.isSelected();
		lTclExpSchemeA.setEnabled(value);
		lTclExpSchemeB.setEnabled(false);
		tTclExpSchemeA.setEnabled(value);
		tTclExpSchemeB.setEnabled(false);
	}

	private void enableEvaluationMatches() {
		boolean value = cbEvaluateGames.isSelected();
		tNumEvaluationMatches.setEnabled(value);
	}

	private void enableGUIElements(UpdateMethod index) {

		// assume 0 -> TDL
		// assume 1 -> TCL
		// assume 2 -> IDBD
		// get Border of some Other Object
		Border stdBorder = tAlphafin.getBorder();
		tAlpha.setBorder(stdBorder);
		tIdbdTheta.setBorder(stdBorder);

		switch (index) {
		case IRPROP_PLUS:
			// deactivate TCL and IDBD
			lAlpha.setEnabled(true);
			lAlphafin.setEnabled(true);
			tAlpha.setEnabled(true);
			tAlphafin.setEnabled(true);
			lIdbdBetaInit.setEnabled(false);
			lIdbdTheta.setEnabled(false);
			lIdbdTitle.setEnabled(false);
			tIdbdBetaInit.setEnabled(false);
			tIdbdTheta.setEnabled(false);
			lTclTitle.setEnabled(false);
			lTclDiscountBiasFinal.setEnabled(false);
			lTclDiscountBiasInit.setEnabled(false);
			lTclDiscountFinal.setEnabled(false);
			lTclDiscountInit.setEnabled(false);
			lTclEpsisodeLength.setEnabled(false);
			lTclInitNA.setEnabled(false);
			lTclTitle.setEnabled(false);
			tTclDiscountBiasFinal.setEnabled(false);
			tTclDiscountBiasInit.setEnabled(false);
			tTclDiscountFinal.setEnabled(false);
			tTclDiscountInit.setEnabled(false);
			tTclInitNA.setEnabled(false);
			tTclEpsiodeLength.setEnabled(false);
			cbTclUseEpisodes.setEnabled(false);
			cbTclUseExpScheme.setEnabled(false);
			lTclExpSchemeA.setEnabled(false);
			lTclExpSchemeB.setEnabled(false);
			tTclExpSchemeA.setEnabled(false);
			tTclExpSchemeB.setEnabled(false);
			rbTclUpdateErrorSignal.setEnabled(false);
			rbTclUpdateNAWeights.setEnabled(false);
			rbTclUpdateRecommendedWC.setEnabled(false);
			rbTclUpdateWeightsNA.setEnabled(false);
			comboActivation.setSelectedIndex(Activation.TANH.getValue());
			comboActivation.setEnabled(true);
			tIdbdWkOmega.setEnabled(false);
			lIdbdWkOmega.setEnabled(false);
			break;
		case ALPHABOUND:
			tAlpha.setText("1.0");
			tAlphafin.setText("1.0");
			// deactivate TCL and IDBD
			lAlpha.setEnabled(true);
			lAlphafin.setEnabled(true);
			tAlpha.setEnabled(true);
			tAlphafin.setEnabled(false);
			lIdbdBetaInit.setEnabled(false);
			lIdbdTheta.setEnabled(false);
			lIdbdTitle.setEnabled(false);
			tIdbdBetaInit.setEnabled(false);
			tIdbdTheta.setEnabled(false);
			lTclTitle.setEnabled(false);
			lTclDiscountBiasFinal.setEnabled(false);
			lTclDiscountBiasInit.setEnabled(false);
			lTclDiscountFinal.setEnabled(false);
			lTclDiscountInit.setEnabled(false);
			lTclEpsisodeLength.setEnabled(false);
			lTclInitNA.setEnabled(false);
			lTclTitle.setEnabled(false);
			tTclDiscountBiasFinal.setEnabled(false);
			tTclDiscountBiasInit.setEnabled(false);
			tTclDiscountFinal.setEnabled(false);
			tTclDiscountInit.setEnabled(false);
			tTclInitNA.setEnabled(false);
			tTclEpsiodeLength.setEnabled(false);
			cbTclUseEpisodes.setEnabled(false);
			cbTclUseExpScheme.setEnabled(false);
			lTclExpSchemeA.setEnabled(false);
			lTclExpSchemeB.setEnabled(false);
			tTclExpSchemeA.setEnabled(false);
			tTclExpSchemeB.setEnabled(false);
			rbTclUpdateErrorSignal.setEnabled(false);
			rbTclUpdateNAWeights.setEnabled(false);
			rbTclUpdateRecommendedWC.setEnabled(false);
			rbTclUpdateWeightsNA.setEnabled(false);
			comboActivation.setSelectedIndex(Activation.NONE.getValue());
			comboActivation.setEnabled(true);
			tIdbdWkOmega.setEnabled(false);
			lIdbdWkOmega.setEnabled(false);
			break;
		case AUTOSTEP:
			tAlpha.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
					Color.red));
			// deactivate TCL and IDBD
			lAlpha.setEnabled(true);
			lAlphafin.setEnabled(false);
			tAlpha.setEnabled(true);
			tAlphafin.setEnabled(false);
			lIdbdBetaInit.setEnabled(false);
			lIdbdTheta.setEnabled(true);
			lIdbdTitle.setEnabled(true);
			tIdbdBetaInit.setEnabled(false);
			tIdbdTheta.setEnabled(true);
			tIdbdTheta.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
					Color.red));
			lTclTitle.setEnabled(false);
			lTclDiscountBiasFinal.setEnabled(false);
			lTclDiscountBiasInit.setEnabled(false);
			lTclDiscountFinal.setEnabled(false);
			lTclDiscountInit.setEnabled(false);
			lTclEpsisodeLength.setEnabled(false);
			lTclInitNA.setEnabled(false);
			tTclDiscountBiasFinal.setEnabled(false);
			tTclDiscountBiasInit.setEnabled(false);
			tTclDiscountFinal.setEnabled(false);
			tTclDiscountInit.setEnabled(false);
			tTclInitNA.setEnabled(false);
			tTclEpsiodeLength.setEnabled(false);
			cbTclUseEpisodes.setEnabled(false);
			cbTclUseExpScheme.setEnabled(false);
			lTclExpSchemeA.setEnabled(false);
			lTclExpSchemeB.setEnabled(false);
			tTclExpSchemeA.setEnabled(false);
			tTclExpSchemeB.setEnabled(false);
			rbTclUpdateErrorSignal.setEnabled(false);
			rbTclUpdateNAWeights.setEnabled(false);
			rbTclUpdateRecommendedWC.setEnabled(false);
			rbTclUpdateWeightsNA.setEnabled(false);
			comboActivation.setSelectedIndex(Activation.NONE.getValue());
			comboActivation.setEnabled(false);
			tIdbdWkOmega.setEnabled(false);
			lIdbdWkOmega.setEnabled(false);
			break;
		case TDL:
			// deactivate TCL and IDBD
			lAlpha.setEnabled(true);
			lAlphafin.setEnabled(true);
			tAlpha.setEnabled(true);
			tAlphafin.setEnabled(true);
			lIdbdBetaInit.setEnabled(false);
			lIdbdTheta.setEnabled(false);
			lIdbdTitle.setEnabled(false);
			tIdbdBetaInit.setEnabled(false);
			tIdbdTheta.setEnabled(false);
			lTclTitle.setEnabled(false);
			lTclDiscountBiasFinal.setEnabled(false);
			lTclDiscountBiasInit.setEnabled(false);
			lTclDiscountFinal.setEnabled(false);
			lTclDiscountInit.setEnabled(false);
			lTclEpsisodeLength.setEnabled(false);
			lTclInitNA.setEnabled(false);
			lTclTitle.setEnabled(false);
			tTclDiscountBiasFinal.setEnabled(false);
			tTclDiscountBiasInit.setEnabled(false);
			tTclDiscountFinal.setEnabled(false);
			tTclDiscountInit.setEnabled(false);
			tTclInitNA.setEnabled(false);
			tTclEpsiodeLength.setEnabled(false);
			cbTclUseEpisodes.setEnabled(false);
			cbTclUseExpScheme.setEnabled(false);
			lTclExpSchemeA.setEnabled(false);
			lTclExpSchemeB.setEnabled(false);
			tTclExpSchemeA.setEnabled(false);
			tTclExpSchemeB.setEnabled(false);
			rbTclUpdateErrorSignal.setEnabled(false);
			rbTclUpdateNAWeights.setEnabled(false);
			rbTclUpdateRecommendedWC.setEnabled(false);
			rbTclUpdateWeightsNA.setEnabled(false);
			comboActivation.setSelectedIndex(Activation.TANH.getValue());
			comboActivation.setEnabled(true);
			tIdbdWkOmega.setEnabled(false);
			lIdbdWkOmega.setEnabled(false);
			break;
		case TCL: // TCL
			// deactivate IDBD
			lAlpha.setEnabled(true);
			lAlphafin.setEnabled(true);
			tAlpha.setEnabled(true);
			tAlphafin.setEnabled(true);
			lIdbdBetaInit.setEnabled(false);
			lIdbdTheta.setEnabled(false);
			lIdbdTitle.setEnabled(false);
			tIdbdBetaInit.setEnabled(false);
			tIdbdTheta.setEnabled(false);
			lTclTitle.setEnabled(true);
			lTclDiscountBiasFinal.setEnabled(false);
			lTclDiscountBiasInit.setEnabled(false);
			lTclDiscountFinal.setEnabled(false);
			lTclDiscountInit.setEnabled(true);
			lTclEpsisodeLength.setEnabled(true);
			lTclInitNA.setEnabled(false); // deactivate
			lTclTitle.setEnabled(true);
			tTclDiscountBiasFinal.setEnabled(false);
			tTclDiscountBiasInit.setEnabled(false);
			tTclDiscountFinal.setEnabled(false);
			tTclDiscountInit.setEnabled(true);
			tTclInitNA.setEnabled(false); // deactivate
			tTclEpsiodeLength.setEnabled(true);
			cbTclUseEpisodes.setEnabled(true);
			cbTclUseExpScheme.setEnabled(true);
			lTclExpSchemeA.setEnabled(true);
			lTclExpSchemeB.setEnabled(false);
			tTclExpSchemeA.setEnabled(true);
			tTclExpSchemeB.setEnabled(false);
			rbTclUpdateErrorSignal.setEnabled(true);
			rbTclUpdateNAWeights.setEnabled(true);
			rbTclUpdateRecommendedWC.setEnabled(true);
			rbTclUpdateWeightsNA.setEnabled(true);
			comboActivation.setSelectedIndex(Activation.TANH.getValue());
			comboActivation.setEnabled(true);
			tIdbdWkOmega.setEnabled(false);
			lIdbdWkOmega.setEnabled(false);
			enableEpisodes();
			enableExpScheme();
			break;
		case IDBD_LINEAR:
		case K1:
			lAlpha.setEnabled(false);
			lAlphafin.setEnabled(false);
			tAlpha.setEnabled(false);
			tAlphafin.setEnabled(false);
			lIdbdBetaInit.setEnabled(true);
			lIdbdTheta.setEnabled(true);
			lIdbdTitle.setEnabled(true);
			tIdbdBetaInit.setEnabled(true);
			tIdbdTheta.setEnabled(true);
			lTclTitle.setEnabled(false);
			lTclDiscountBiasFinal.setEnabled(false);
			lTclDiscountBiasInit.setEnabled(false);
			lTclDiscountFinal.setEnabled(false);
			lTclDiscountInit.setEnabled(false);
			lTclEpsisodeLength.setEnabled(false);
			lTclInitNA.setEnabled(false);
			lTclTitle.setEnabled(false);
			tTclDiscountBiasFinal.setEnabled(false);
			tTclDiscountBiasInit.setEnabled(false);
			tTclDiscountFinal.setEnabled(false);
			tTclDiscountInit.setEnabled(false);
			tTclInitNA.setEnabled(false);
			tTclEpsiodeLength.setEnabled(false);
			cbTclUseEpisodes.setEnabled(false);
			cbTclUseExpScheme.setEnabled(false);
			lTclExpSchemeA.setEnabled(false);
			lTclExpSchemeB.setEnabled(false);
			tTclExpSchemeA.setEnabled(false);
			tTclExpSchemeB.setEnabled(false);
			rbTclUpdateErrorSignal.setEnabled(false);
			rbTclUpdateNAWeights.setEnabled(false);
			rbTclUpdateRecommendedWC.setEnabled(false);
			rbTclUpdateWeightsNA.setEnabled(false);
			comboActivation.setSelectedIndex(Activation.NONE.getValue());
			comboActivation.setEnabled(false);
			tIdbdWkOmega.setEnabled(false);
			lIdbdWkOmega.setEnabled(false);
			break;
		case IDBD_WK:
			lAlpha.setEnabled(false);
			lAlphafin.setEnabled(false);
			tAlpha.setEnabled(false);
			tAlphafin.setEnabled(false);
			lIdbdBetaInit.setEnabled(true);
			lIdbdTheta.setEnabled(true);
			lIdbdTitle.setEnabled(true);
			tIdbdBetaInit.setEnabled(true);
			tIdbdTheta.setEnabled(true);
			lTclTitle.setEnabled(false);
			lTclDiscountBiasFinal.setEnabled(false);
			lTclDiscountBiasInit.setEnabled(false);
			lTclDiscountFinal.setEnabled(false);
			lTclDiscountInit.setEnabled(false);
			lTclEpsisodeLength.setEnabled(false);
			lTclInitNA.setEnabled(false);
			lTclTitle.setEnabled(false);
			tTclDiscountBiasFinal.setEnabled(false);
			tTclDiscountBiasInit.setEnabled(false);
			tTclDiscountFinal.setEnabled(false);
			tTclDiscountInit.setEnabled(false);
			tTclInitNA.setEnabled(false);
			tTclEpsiodeLength.setEnabled(false);
			cbTclUseEpisodes.setEnabled(false);
			cbTclUseExpScheme.setEnabled(false);
			lTclExpSchemeA.setEnabled(false);
			lTclExpSchemeB.setEnabled(false);
			tTclExpSchemeA.setEnabled(false);
			tTclExpSchemeB.setEnabled(false);
			rbTclUpdateErrorSignal.setEnabled(false);
			rbTclUpdateNAWeights.setEnabled(false);
			rbTclUpdateRecommendedWC.setEnabled(false);
			rbTclUpdateWeightsNA.setEnabled(false);
			comboActivation.setSelectedIndex(Activation.TANH.getValue());
			comboActivation.setEnabled(false);
			tIdbdWkOmega.setEnabled(true);
			lIdbdWkOmega.setEnabled(true);
			break;
		case IDBD_NONLINEAR:
		case IDBDMSE:
		case ELK1:
			lAlpha.setEnabled(false);
			lAlphafin.setEnabled(false);
			tAlpha.setEnabled(false);
			tAlphafin.setEnabled(false);
			lIdbdBetaInit.setEnabled(true);
			lIdbdTheta.setEnabled(true);
			lIdbdTitle.setEnabled(true);
			tIdbdBetaInit.setEnabled(true);
			tIdbdTheta.setEnabled(true);
			lTclTitle.setEnabled(false);
			lTclDiscountBiasFinal.setEnabled(false);
			lTclDiscountBiasInit.setEnabled(false);
			lTclDiscountFinal.setEnabled(false);
			lTclDiscountInit.setEnabled(false);
			lTclEpsisodeLength.setEnabled(false);
			lTclInitNA.setEnabled(false);
			lTclTitle.setEnabled(false);
			tTclDiscountBiasFinal.setEnabled(false);
			tTclDiscountBiasInit.setEnabled(false);
			tTclDiscountFinal.setEnabled(false);
			tTclDiscountInit.setEnabled(false);
			tTclInitNA.setEnabled(false);
			tTclEpsiodeLength.setEnabled(false);
			cbTclUseEpisodes.setEnabled(false);
			cbTclUseExpScheme.setEnabled(false);
			lTclExpSchemeA.setEnabled(false);
			lTclExpSchemeB.setEnabled(false);
			tTclExpSchemeA.setEnabled(false);
			tTclExpSchemeB.setEnabled(false);
			rbTclUpdateErrorSignal.setEnabled(false);
			rbTclUpdateNAWeights.setEnabled(false);
			rbTclUpdateRecommendedWC.setEnabled(false);
			rbTclUpdateWeightsNA.setEnabled(false);
			comboActivation.setSelectedIndex(Activation.TANH.getValue());
			comboActivation.setEnabled(false);
			tIdbdWkOmega.setEnabled(false);
			lIdbdWkOmega.setEnabled(false);
			if (index == UpdateMethod.IDBD_NONLINEAR)
				comboActivation.setSelectedIndex(Activation.LOGSIG.getValue());
			break;
		default:
			throw new UnsupportedOperationException(
					"No behaviour for selected method defined: " + index
							+ "!!!");
		}
	}

	// public boolean evaluate() {
	// return cbInfoIntervalEvaluate.isSelected();
	// }

	public boolean getEvaluate012() {
		return cbEvaluate012.isSelected();
	}

	public boolean getEvaluate50Games() {
		return cbEvaluateGames.isSelected();
	}

	public boolean singleMatch() {
		return cbSingleMatch.isSelected();
	}

	public GameInterval[] getInfoInterval() {
		// return Integer.valueOf(tInfoInterval.getText()).intValue();
		return intervalListOpts.getIntervalList();
	}

	public double getAlpha() {
		return Double.valueOf(tAlpha.getText()).doubleValue();
	}

	public double getAlphaFinal() {
		return Double.valueOf(tAlphafin.getText()).doubleValue();
	}
	
	public boolean getEpsRandUpdate() {
		return cbEpsRandUpdate.isSelected();
	}

	public double getEpsilon() {
		return Double.valueOf(tEpsIn.getText()).doubleValue();
	}

	public double getEpsilonFinal() {
		return Double.valueOf(tEpsfin.getText()).doubleValue();
	}

	public double getLambda() {
		double lambda;
		try {
			lambda = Double.valueOf(tLambda.getText()).doubleValue();
		} catch (java.lang.NumberFormatException e) {
			lambda = 0.0;
		}
		return lambda;
	}

	public double getGamma() {
		return Double.valueOf(gammaT.getText()).doubleValue();
	}

	public int getNumGames() {
		Double val = Double.valueOf(gameNumT.getText()).doubleValue();
		return (int) (val * TDParams.SCALE_GAME_BY_MILLION);
	}

	public int getAlphaChangeType() {
		return comboAlphaChangeType.getSelectedIndex();
	}

	public int getNPlyLookAhead() {
		return Integer.valueOf(nPlyLookAheadT.getText()).intValue();
	}

	public int getEpsilonChange() {
		return comboEpsilonChangeType.getSelectedIndex();
	}

	public int getNumEvaluationMatches() {
		return Integer.valueOf(tNumEvaluationMatches.getText()).intValue();
	}

	public int getStopAfter() {
		return Integer.valueOf(stopAfterT.getText()).intValue();
	}

	public boolean getRandNTuples() {
		return randNTuples.isSelected();
	}

	public int getTupleNum() {
		return Integer.valueOf(randTupleNumT.getText()).intValue();
	}

	public int getTupleLen() {
		return Integer.valueOf(randTupleLenT.getText()).intValue();
	}

	public boolean getRandTuple() {
		return randNTuples.isSelected();
	}

	public int getRandMode() {
		return comboRandMode.getSelectedIndex();
	}

	public boolean getRandInitWeights() {
		return randInitWeights.isSelected();
	}

	public boolean getUseSymmetry() {
		return useSym.isSelected();
	}

	public double getAlphaChangeParam() {
		return Double.valueOf(extraParamAlphaT.getText()).doubleValue();
	}

	public double getEpsilonChangeParam() {
		return Double.valueOf(tEpsExtraParam.getText()).doubleValue();
	}

	public int getPosVals() {
		return comboPosVals.getSelectedIndex() + 3;
	}

	public double getSigOutputFac() {
		return Double.valueOf(tSigOutputFac.getText()).doubleValue();
	}

	public boolean getHashIntern() {
		return cbHashIntern.isSelected();
	}

	public boolean getHashExtern() {
		return cbHashExtern.isSelected();
	}

	public int getHashSize() {
		String sel = comboHashSize.getSelectedItem().toString();
		int value = Integer.valueOf(sel).intValue();
		return value;
	}

	public UpdateMethod getUpdateMethod() {
		UpdateMethod meth = null;
		switch (comboWeightUpdateMethod.getSelectedIndex()) {
		case 0:
			meth = UpdateMethod.TDL;
			break;
		case 1:
			meth = UpdateMethod.TCL;
			break;
		case 2:
			meth = UpdateMethod.IDBD_LINEAR;
			break;
		case 3:
			meth = UpdateMethod.IDBD_NONLINEAR;
			break;
		case 4:
			meth = UpdateMethod.AUTOSTEP;
			break;
		case 5:
			meth = UpdateMethod.K1;
			break;
		case 6:
			meth = UpdateMethod.ELK1;
			break;
		case 7:
			meth = UpdateMethod.IDBD_WK;
			break;
		case 8:
			meth = UpdateMethod.ALPHABOUND;
			break;
		case 9:
			meth = UpdateMethod.IDBDMSE;
			break;
		case 10:
			meth = UpdateMethod.IRPROP_PLUS;
			break;
		default:
			throw new UnsupportedOperationException("Method not supported yet");

		}
		return meth;
	}

	public double getEpsilonSlope() {
		return Double.valueOf(tEpsSlope.getText()).doubleValue();
	}

	public Activation getUseActivationFunc() {
		Activation[] act = Activation.values();
		return act[comboActivation.getSelectedIndex()];
	}

	public boolean getUseBiasWeight() {
		return cbUseBiasWeight.isSelected();
	}

	public double getIdbdBetaInit() {
		return Double.valueOf(tIdbdBetaInit.getText()).doubleValue();
	}

	public double getIdbdTheta() {
		return Double.valueOf(tIdbdTheta.getText()).doubleValue();
	}

	public double getIdbdWKOmegak() {
		return Double.valueOf(tIdbdWkOmega.getText()).doubleValue();
	}

	public double getTclTablesinitValue() {
		return Double.valueOf(tTclInitNA.getText()).doubleValue();
	}

	public boolean getTclUseUpdateEpisodes() {
		return cbTclUseEpisodes.isSelected();
	}

	public int getTclEpisodeLength() {
		return Integer.valueOf(tTclEpsiodeLength.getText()).intValue();
	}

	public double getTclMuInit() {
		return Double.valueOf(tTclDiscountInit.getText()).doubleValue();
	}

	public double getTclMuFinal() {
		return Double.valueOf(tTclDiscountFinal.getText()).doubleValue();
	}

	public double getTclMuInitBias() {
		return Double.valueOf(tTclDiscountBiasInit.getText()).doubleValue();
	}

	public double getTclMuFinalBias() {
		return Double.valueOf(tTclDiscountBiasFinal.getText()).doubleValue();
	}

	public boolean getTclUpdate1WeightFactors2Weights() {
		// true: Update First Weight-Factors(N-A) then the Weights
		// false: Update first the Weights and then the weight-factors (N-A)
		return rbTclUpdateNAWeights.isSelected();
	}

	public boolean getTclUseErrorSignal() {
		return rbTclUpdateErrorSignal.isSelected();
	}

	public boolean getTclUseExpScheme() {
		return cbTclUseExpScheme.isSelected();
	}

	public double getTclExpSchemeFacA() {
		return Double.valueOf(tTclExpSchemeA.getText()).doubleValue();
	}

	public double getTclExpSchemeFacB() {
		return Double.valueOf(tTclExpSchemeB.getText()).doubleValue();
	}

	public boolean getEligResetOnRandomMove() {
		return cbEligResetOnRandomMove.isSelected();
	}

	public boolean getEligReplacingTraces() {
		return cbEligReplacingTraces.isSelected();
	}

	public TDParams getTDParams() {
		TDParams tdPar = new TDParams();
		;

		tdPar.trainGameNum = getNumGames();
		tdPar.stopAfterMoves = getStopAfter();
		tdPar.alphaInit = getAlpha();
		tdPar.alphaFinal = getAlphaFinal();
		tdPar.alphaChangeMethod = getAlphaChangeType();
		tdPar.epsilonInit = getEpsilon();
		tdPar.epsRandUpdate = getEpsRandUpdate();
		tdPar.epsilonFinal = getEpsilonFinal();
		tdPar.epsilonMethod = getEpsilonChange();
		tdPar.gamma = getGamma();
		tdPar.lambda = getLambda();
		tdPar.resetEligOnRandomMove = getEligResetOnRandomMove();
		tdPar.replacingTraces = getEligReplacingTraces();
		tdPar.numEvaluationMatches = getNumEvaluationMatches();

		tdPar.randNTuples = getRandNTuples();
		tdPar.tupleNum = getTupleNum();
		tdPar.randTupleLen = getTupleLen();
		tdPar.randMode = getRandMode();
		tdPar.useSymmetry = getUseSymmetry();
		tdPar.randInitWeights = getRandInitWeights();
		tdPar.alphaChangeParam = getAlphaChangeParam();
		tdPar.epsilonChangeParam = getEpsilonChangeParam();
		tdPar.posVals = getPosVals();
		tdPar.nPlyLookAhead = getNPlyLookAhead();
		tdPar.singleMatch = singleMatch();
		tdPar.infoInterval = intervalListOpts.getIntervalList();

		if (nTuples != null) {
			tdPar.nTuples = nTuples;
		}

		tdPar.sigOutputFac = getSigOutputFac();

		tdPar.evaluate012 = getEvaluate012();
		tdPar.evaluateAgent = getEvaluate50Games();

		tdPar.hashSize = getHashSize();
		tdPar.useHashExtern = getHashExtern();
		tdPar.useHashIntern = getHashIntern();

		tdPar.updateMethod = getUpdateMethod();
		tdPar.epsilonSlope = getEpsilonSlope();
		// tdPar.useActivation = getUseActivationFunc(); TODO: removed
		tdPar.activation = getUseActivationFunc();
		tdPar.useBiasWeight = getUseBiasWeight();
		tdPar.idbdBetaInit = getIdbdBetaInit();
		tdPar.idbdTheta = getIdbdTheta();
		tdPar.idbdWKOmegak = getIdbdWKOmegak();

		// getTclTablesinitValue() -> Do not use anymore
		// tdPar.tclTablesInitValue = Double.NEGATIVE_INFINITY;

		tdPar.tclUseUpdateEpisodes = getTclUseUpdateEpisodes();
		tdPar.tclEpisodeLength = getTclEpisodeLength();
		tdPar.tclMuInit = getTclMuInit();
		tdPar.tclMuFinal = getTclMuFinal();
		tdPar.tclMuInitBias = getTclMuInitBias();
		tdPar.tclMuFinalBias = getTclMuFinalBias();
		tdPar.tclUpdate1WeightFactors2Weights = getTclUpdate1WeightFactors2Weights();
		tdPar.tclUseErrorSignal = getTclUseErrorSignal();
		tdPar.tclUseExpScheme = getTclUseExpScheme();
		tdPar.tclExpSchemeFacA = getTclExpSchemeFacA();
		tdPar.tclExpSchemeFacB = getTclExpSchemeFacB();

		return tdPar;
	}

	public void setTDParams(TDParams tdPar) {
		if (tdPar != null) {
			gameNumT.setText((double) tdPar.trainGameNum
					/ TDParams.SCALE_GAME_BY_MILLION + "");
			stopAfterT.setText(tdPar.stopAfterMoves + "");
			tAlpha.setText(tdPar.alphaInit + "");
			tAlphafin.setText(tdPar.alphaFinal + "");
			comboAlphaChangeType.setSelectedIndex(tdPar.alphaChangeMethod);

			cbEpsRandUpdate.setSelected(tdPar.epsRandUpdate);
			tEpsIn.setText(tdPar.epsilonInit + "");
			tEpsfin.setText(tdPar.epsilonFinal + "");
			comboEpsilonChangeType.setSelectedIndex(tdPar.epsilonMethod);

			tLambda.setText(tdPar.lambda + "");

			cbEligReplacingTraces.setSelected(tdPar.replacingTraces);
			cbEligResetOnRandomMove.setSelected(tdPar.resetEligOnRandomMove);

			gammaT.setText(tdPar.gamma + "");

			randNTuples.setSelected(tdPar.randNTuples);
			randTupleLenT.setText(tdPar.randTupleLen + "");
			randTupleNumT.setText(tdPar.tupleNum + "");
			comboRandMode.setSelectedIndex(tdPar.randMode);
			setRandElementState(randNTuples.isSelected());

			useSym.setSelected(tdPar.useSymmetry);

			extraParamAlphaT.setText(tdPar.alphaChangeParam + "");
			tEpsExtraParam.setText(tdPar.epsilonChangeParam + "");

			comboPosVals.setSelectedIndex(tdPar.posVals - 3);

			randInitWeights.setSelected(tdPar.randInitWeights);

			nPlyLookAheadT.setText(tdPar.nPlyLookAhead + "");

			cbSingleMatch.setSelected(tdPar.singleMatch);
			intervalListOpts = new OptionsIntervals(this, tdPar.infoInterval);
			// tInfoInterval.setText(tdPar.infoInterval + "");

			if (tdPar.nTuples != null)
				nTuples = tdPar.nTuples;

			// cbInfoIntervalEvaluate.setSelected(tdPar.evaluate);
			tSigOutputFac.setText(tdPar.sigOutputFac + "");

			cbEvaluate012.setSelected(tdPar.evaluate012);
			cbEvaluateGames.setSelected(tdPar.evaluateAgent);
			tNumEvaluationMatches.setText(tdPar.numEvaluationMatches + "");

			long index = Math.round(Math.log(tdPar.hashSize) / Math.log(2)) - 13L;
			comboHashSize.setSelectedIndex((int) index);
			cbHashExtern.setSelected(tdPar.useHashExtern);
			cbHashIntern.setSelected(tdPar.useHashIntern);

			int updateIndex = -1;
			switch (tdPar.updateMethod) {
			case TDL:
				updateIndex = 0;
				break;
			case TCL:
				updateIndex = 1;
				break;
			case IDBD_LINEAR:
				updateIndex = 2;
				break;
			case IDBD_NONLINEAR:
				updateIndex = 3;
				break;
			case AUTOSTEP:
				updateIndex = 4;
				break;
			case K1:
				updateIndex = 5;
				break;
			case ELK1:
				updateIndex = 6;
				break;
			case IDBD_WK:
				updateIndex = 7;
				break;
			case ALPHABOUND:
				updateIndex = 8;
				break;
			case IDBDMSE:
				updateIndex = 9;
				break;
			default:
				updateIndex = -1;
				throw new UnsupportedOperationException(
						"Method not supported yet!");
			}
			comboWeightUpdateMethod.setSelectedIndex(updateIndex);
			tEpsSlope.setText(tdPar.epsilonSlope + "");
			// cbUseActivationFunction.setSelected(tdPar.useActivation); TODO:
			// removed
			comboActivation.setSelectedIndex(tdPar.activation.getValue());
			cbUseBiasWeight.setSelected(tdPar.useBiasWeight);
			tIdbdBetaInit.setText(tdPar.idbdBetaInit + "");
			tIdbdTheta.setText(tdPar.idbdTheta + "");
			tIdbdWkOmega.setText(tdPar.idbdWKOmegak + "");

			tTclInitNA.setText(""); // Do not use anymore!!!
			cbTclUseEpisodes.setSelected(tdPar.tclUseUpdateEpisodes);
			tTclEpsiodeLength.setText(tdPar.tclEpisodeLength + "");
			tTclDiscountInit.setText(tdPar.tclMuInit + "");
			tTclDiscountFinal.setText(tdPar.tclMuFinal + "");
			tTclDiscountBiasInit.setText(tdPar.tclMuInitBias + "");
			tTclDiscountBiasFinal.setText(tdPar.tclMuFinalBias + "");
			rbTclUpdateNAWeights
					.setSelected(tdPar.tclUpdate1WeightFactors2Weights);
			rbTclUpdateWeightsNA
					.setSelected(!tdPar.tclUpdate1WeightFactors2Weights);
			rbTclUpdateErrorSignal.setSelected(tdPar.tclUseErrorSignal);
			rbTclUpdateRecommendedWC.setSelected(!tdPar.tclUseErrorSignal);
			cbTclUseExpScheme.setSelected(tdPar.tclUseExpScheme);
			tTclExpSchemeA.setText(tdPar.tclExpSchemeFacA + "");
			tTclExpSchemeB.setText(tdPar.tclExpSchemeFacB + "");

			System.out.println("[TDParams were successfully changed!]");
		} else
			System.out.println("[ERROR: TDParams were NOT changed!]");

		if (tdPar != null) enableGUIElements(tdPar.updateMethod);
		if (tdPar != null) printUpdateMethTitle(tdPar.updateMethod);
		enableExpScheme();
		enableEligElements();
		enableEvaluationMatches();
		enableEpisodes();
		enableEpsElements();
		if (tdPar != null) enableGUIElements(tdPar.updateMethod);
	}

	public void setNTuples(Integer[][] nTuples) {
		this.nTuples = nTuples;
	}

	private void setRandElementState(boolean state) {
		comboRandMode.setEnabled(state);
		randModeL.setEnabled(state);
		randModeL.setEnabled(state);
		randTupleNumL.setEnabled(state);
		randTupleNumT.setEnabled(state);
		randTupleLenL.setEnabled(state);
		randTupleLenT.setEnabled(state);
	}

	private void saveTDParams(TDParams td) {

		fc.setFileFilter(new ParamFilter());
		fc.setAcceptAllFileFilterUsed(false);

		int returnVal = fc.showSaveDialog(this);
		String filePath;

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			// System.out.println("Opening: " + file.getAbsoluteFile() + ".");
			filePath = file.getPath();

			if (!filePath.toLowerCase().endsWith(".tdPar")
					&& !filePath.toLowerCase().endsWith(".tdpar"))
				filePath = filePath + ".tdPar";

			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(new FileOutputStream(filePath));
			} catch (IOException e) {
				System.out.println("[ERROR in OptionsTD.java: Could not open file " + filePath + " !]");
			}

			try {
				oos.writeObject(td);
				System.out.println("[TD-Params successfully saved]");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("[ERROR in OptionsTD.java: Could not write file " + filePath + " !]");
			}
		} else
			System.out.println("[TD-Params were NOT saved!]");
	}

	private TDParams loadTDParams() {

		fc.setFileFilter(new ParamFilter());
		fc.setAcceptAllFileFilterUsed(false);

		int returnVal = fc.showOpenDialog(this);
		String filePath = "";

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			ObjectInputStream ois = null;
			FileInputStream fis = null;
			try {
				File file = fc.getSelectedFile();
				filePath = file.getPath();
				fis = new FileInputStream(filePath);
				ois = new ObjectInputStream(fis);
				Object obj = ois.readObject();
				if (obj instanceof TDParams) {
					TDParams so = (TDParams) obj;
					return so;
				} else
					System.out.println("[ERROR in OptionsTD.java: file " + filePath + " contains no TD-Params!]");
				
			} catch (IOException e) {
				// e.printStackTrace();
				System.out.println("[ERROR in OptionsTD.java: Could not open file " + filePath + " !]");
			} catch (ClassNotFoundException e) {
				// e.printStackTrace();
				System.out.println("[ERROR in OptionsTD.java: Could not open file " + filePath + " !]");
			} finally {
				if (ois != null)
					try {
						ois.close();
					} catch (IOException e) {
					}
				if (fis != null)
					try {
						fis.close();
					} catch (IOException e) {
					}
			}
		} else
			System.out.println("[TD-Params were NOT opened!]");
		return null;
	}

	private void enableEligElements() {
		if (tLambda.getText() != "") {
			double lambda = getLambda();
			boolean enableElig = (lambda != 0.0);
			lEligTraces.setEnabled(enableElig);
			cbEligReplacingTraces.setEnabled(enableElig);
			cbEligResetOnRandomMove.setEnabled(enableElig);
			if (getUpdateMethod() == UpdateMethod.TCL) {
				cbTclUseEpisodes.setEnabled(!enableElig);
				lTclEpsisodeLength.setEnabled(!enableElig);
				tTclEpsiodeLength.setEnabled(!enableElig);
				if (enableElig)
					cbTclUseEpisodes.setSelected(false);
			}
		}
	}

	private void enableEpsElements() {
		if (tEpsIn.getText() != "" && tEpsfin.getText() != "") {
			boolean epsNEqual = !tEpsIn.getText().equals(tEpsfin.getText());
			lEpsSlope.setEnabled(epsNEqual);
			tEpsSlope.setEnabled(epsNEqual);
			lEpsChangeType.setEnabled(epsNEqual);
			comboEpsilonChangeType.setEnabled(epsNEqual);
			lEpsExtraParam.setEnabled(epsNEqual);
			tEpsExtraParam.setEnabled(epsNEqual);
		}

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

	public static void main(String args[]) {
		try {
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		} catch (Exception e) {
		}

		OptionsTD op = new OptionsTD(new TDParams());
		op.addWindowListener(new WindowClosingAdapter());
		op.setVisible(true);
	}
}
