package gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;

import nTupleTD.TDParams;
import nTupleTD.TDSAgent;
import nTupleTD.TDParams.GameInterval;

import openingBook.BookSum;

import competition.Competition;
import competition.Evaluate;
import competition.ResultCompSingle;

import c4.Agent;
import c4.AlphaBetaAgent;

public class ConsoleTraining_v2_14 {

	// Opening books
	private final BookSum books;

	// Referee
	public AlphaBetaAgent alphaBetaStd = null;

	// Evaluation for TD-Agents
	Evaluate eval;

	public ConsoleTraining_v2_14() {
		// Init Opening-books
		books = new BookSum();

		// Init Referee
		alphaBetaStd = new AlphaBetaAgent(books);
		alphaBetaStd.resetBoard();
		alphaBetaStd.setTransPosSize(3); // Hash-Size
		alphaBetaStd.setBooks(true, false, true);
		alphaBetaStd.setDifficulty(42);
		alphaBetaStd.randomizeEqualMoves(true);

		// Init evaluation
		eval = new Evaluate(alphaBetaStd);
	}

	protected Agent initTDSAgent(TDParams tdPar) {

		AlphaBetaAgent ab = null;
		if (tdPar.stopAfterMoves < 40)
			ab = alphaBetaStd;

		TDSAgent tds = new TDSAgent(tdPar, ab);
		return tds;
	}

	public void trainTDSAgent(TDSAgent tds/* ,int gameNum, int player */,
			boolean useCSV) {
		long zstStart = System.currentTimeMillis();

		// OptionsTD opTD = (OptionsTD) params[player];
		// TDParams tdPar = opTD.getTDParams();
		// tds.setTDParams(tdPar);

		TDParams tdPar = tds.getTDParams();
		boolean measureStrength = tdPar.singleMatch;
		// boolean evaluate = tdPar.evaluate;
		// int interval = tdPar.infoInterval;

		// Init competition for Strength-measurement
		Competition comp = null;
		boolean getValues = false;
		int pieceNum = 0;

		// No single matches possible for CSV-Format; only evaluation
		if (measureStrength && !useCSV) {
			comp = new Competition(tds, alphaBetaStd, alphaBetaStd);
			pieceNum = tdPar.stopAfterMoves;
			getValues = false;
		}

		if (useCSV)
			System.out
					.println("gameNr;alpha;epsilon;Score012;Score50Games;Draw50Games;");

		// Start training
		int gameNum = tds.getTDParams().trainGameNum;
		DecimalFormat df = new DecimalFormat("+0.00000;-0.00000");
		for (int i = 0; i <= gameNum; i++) {
			tds.trainNet(1);
			if (GameInterval.evalNecessary(tdPar.infoInterval, i) && i != 0) {
				if (!useCSV) {
					System.out.println(i + " ; alpha:"
							+ df.format(tds.getAlpha()) + " ; epsilon: "
							+ df.format(tds.getEpsilon()));

					// Get score
					if (measureStrength) {
						comp.setInitialBoard(new int[7][6]);
						ResultCompSingle scr = comp.compete(pieceNum,
								getValues, false);
						String str = new String();
						if (scr.winner != -1)
							str = str.concat("Simple-Match-Winner: "
									+ scr.agents[scr.winner] + "!!");
						else
							str = str
									.concat("Simple-Match-Winner: No Winner: Draw!!");
						System.out.println(str);
					}
					if (tdPar.evaluate012 || tdPar.evaluateAgent) {
						System.out.println("Start Evaluation...");
						System.out.println(eval.getScoreStr(tds, alphaBetaStd,
								tdPar) + "");
					}
					System.out.println("\n");
				} else {
					System.out.print(i + ";" + df.format(tds.getAlpha()) + ";"
							+ df.format(tds.getEpsilon()) + ";");

					// Get score
					double[] score = eval.getScore(tds, alphaBetaStd, tdPar);
					if (!tdPar.evaluate012)
						score[0] = Double.NaN;
					if (!tdPar.evaluateAgent)
						score[1] = score[2] = Double.NaN;

					System.out.print(df.format(score[0]) + ";");
					System.out.print(df.format(score[1]) + ";");
					System.out.print(df.format(score[2]) + ";");
					System.out.println();
				}
			}

		}

		// Evaluate Strength
		// System.out.println(eval.getScoreStr(tds, alphaBetaStd));

		// Set N-Tuples in the Options
		// int nTuples[][] = tds.m_Net.getNTuples1Dim();
		// opTD.setNTuples(nTuples);

		// Stop time
		long zstStop = System.currentTimeMillis();
		long time = zstStop - zstStart;
		long seconds = time / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		seconds = seconds % 60;
		minutes = minutes % 60;
		System.out.println("Time needed: " + hours + "h:" + minutes + "min:"
				+ seconds + "s;;;;;;");

	}

	private void saveTDParams(TDParams td, String path,
			boolean activateRandCreation) {
		td.randNTuples = activateRandCreation; // activate/deactivate random
												// creation of n-tuples

		File file = new File(path);
		String filePath = file.getPath();

		if (!filePath.toLowerCase().endsWith(".tdPar")
				&& !filePath.toLowerCase().endsWith(".tdpar"))
			filePath = filePath + ".tdPar";

		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(filePath));
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("[ERROR: Could not open file!]");
		}

		try {
			oos.writeObject(td);
			System.out.println("[TD-Params successfully saved]");
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("[ERROR: Could not write file!]");
		}
	}

	private static TDParams loadTDParams(String path) {
		String filePath;

		// if (returnVal == JFileChooser.APPROVE_OPTION) {
		ObjectInputStream ois = null;
		FileInputStream fis = null;
		try {
			File file = new File(path);
			// filePath = file.getPath();
			filePath = file.getAbsolutePath();
			// System.out.println("Opening: " + filePath);
			fis = new FileInputStream(filePath);
			ois = new ObjectInputStream(fis);
			Object obj = ois.readObject();
			if (obj instanceof TDParams) {
				TDParams so = (TDParams) obj;
				return so;
			} else
				System.out.println("[ERROR: file is contains no TD-Params!]");
		} catch (IOException e) {
			// e.printStackTrace();
			System.out.println("[ERROR: Could not open TDParam-file!]");
		} catch (ClassNotFoundException e) {
			// e.printStackTrace();
			System.out.println("[ERROR: Could not open TDParam-file!]");
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
		// } else
		// System.out.println("[TD-Params were NOT opened!]");
		return null;
	}

	public static void main(String[] args) {
		ConsoleTraining_v2_14 ct = new ConsoleTraining_v2_14();
		if (args.length < 2) {
			System.out
					.println("usage: java -jar xxx.jar tdParPath resPath run-Nr");
			System.out
					.println("usage: java -jar xxx.jar tdParPath resPath run-Nr par1 valPar1 par2 valPar2 ...");
			System.exit(1);
		}

		TDParams tdPar;
		tdPar = loadTDParams(args[0]);

		for (int i = 3; i < args.length; i++) {
			String arg = args[i].toLowerCase();
			if (arg.equals("beta")) {
				float beta = Float.parseFloat(args[i + 1]);
				tdPar.idbdBetaInit = beta;
			} else if (arg.equals("theta")) {
				float theta = Float.parseFloat(args[i + 1]);
				tdPar.idbdTheta = theta;
			}
		}

		TDSAgent tds = (TDSAgent) ct.initTDSAgent(tdPar);

		// write tdPar in textFile
		try {
			// String pid = ManagementFactory.getRuntimeMXBean().getName();
			PrintWriter out = new PrintWriter(args[1] + "/tdParams" + args[2]
					+ ".txt");
			out.print(tds.getTDParams());
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// Save param-file (e.g. after random n-tuple-creation)
		// But only if n-tuple-generation was random
		// deactivate Random n-tuple-creation in this case
		if (tdPar.randNTuples) {
			// add extension to path to save TDParams under other filename
			String path = new String(args[1] + "/" + args[2] + ".tdPar");
			TDParams td = tds.getTDParams();
			td.randNTuples = false;
			ct.saveTDParams(td, path, false);
		}

		// Train Agent
		ct.trainTDSAgent(tds, true);
	}
}
