package parallelTraining;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import competition.Evaluate;
import openingBook.BookSum;
import c4.Agent;
import c4.AlphaBetaAgent;
import c4.TDParams;

import c4.TDSAgent;

//TODO: Not completly developed yet!!!!!!
public class ParallelTraining {

	private static final int DEFAULT_NR_PARALLEL_TRAINING = 10;

	// Opening books
	private final BookSum books;

	// Referee
	private AlphaBetaAgent alphaBetaStd = null;

	// Evaluation for TD-Agents
	private Evaluate eval;

	// Thread-Nr. for parallel Training
	private final int threadNr;
	private final Table table;

	public ParallelTraining(int threadNr, Table table) {
		this.threadNr = threadNr;
		this.table = table;

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

	protected Agent initTDSAgent(String tdParPath) {
		TDParams tdPar;

		tdPar = loadTDParams(tdParPath);

		AlphaBetaAgent ab = null;
		if (tdPar.stopAfterMoves < 40)
			ab = alphaBetaStd;

		TDSAgent tds = new TDSAgent(tdPar, ab);
		return tds;
	}

	public void trainTDSAgent(TDSAgent tds) {

		TDParams tdPar = tds.getTDParams();

		// boolean evaluate = tdPar.evaluate;
		int interval = tdPar.infoInterval;

		// Start training
		int gameNum = tds.getTDParams().trainGameNum;
		for (int i = 0; i <= gameNum; i++) {
			tds.trainNet(1);
			if (i != 0 && i % interval == 0) {
				int index = i / interval;
				if (threadNr == 0) {
					table.setGameNum(index, i);
					table.setAlpha(index, tds.getAlpha());
					table.setEpsilon(index, tds.getEpsilon());
				}

				// Get score
				double[] score = eval.getScore(tds, alphaBetaStd, tdPar);
				if (!tdPar.evaluate012)
					score[0] = Double.NaN;
				if (!tdPar.evaluate50Games)
					score[1] = score[2] = Double.NaN;
				// Score50Games
				table.setScore50Games(index, threadNr, score[1]);

				// Draw50Games
				table.setDraw50Games(index, threadNr, score[2]);
				table.incCounterAndWrite(index);

			}
		}
	}

	// TODO: new (18.10.2013)
	void saveTDParams(TDParams td, String path, boolean activateRandCreation) {
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
			System.out.println("[ERROR: Could not open file!]");
		}

		try {
			oos.writeObject(td);
		} catch (IOException e) {
			System.out.println("[ERROR: Could not write file!]");
		}
	}

	private static TDParams loadTDParams(String path) {
		String filePath;

		ObjectInputStream ois = null;
		FileInputStream fis = null;
		try {
			File file = new File(path);
			filePath = file.getAbsolutePath();
			fis = new FileInputStream(filePath);
			ois = new ObjectInputStream(fis);
			Object obj = ois.readObject();
			if (obj instanceof TDParams) {
				TDParams so = (TDParams) obj;
				return so;
			} else
				System.out.println("[ERROR: file is contains no TD-Params!]");
		} catch (IOException e) {
			System.out.println("[ERROR: Could not open file!]");
		} catch (ClassNotFoundException e) {
			System.out.println("[ERROR: Could not open file!]");
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
		return null;
	}

	public static void main(final String[] args) {
		long zstStart = System.currentTimeMillis();
		if (args.length < 1) {
			System.out.println("usage: java -jar xxx.jar tdParPath");
			System.out
					.println("usage: java -jar xxx.jar tdParPath [tdParSaveNum]");
			System.out
					.println("usage: java -jar xxx.jar tdParPath [tdParSaveNum] [NrParallelTraining]");
			System.exit(1);
		}

		int nrParallelThreads = DEFAULT_NR_PARALLEL_TRAINING; // Default = 10
		if (args.length >= 3) {

			try {
				nrParallelThreads = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				System.out.println("Nr of parallel trainings is wrong!!!");
				System.exit(1);
			}
		}

		// initialize Table for Results
		// Calculate Nr. of Lines
		TDParams tdPar = loadTDParams(args[0]);
		int numLines = tdPar.trainGameNum / tdPar.infoInterval + 1;
		Table table = new Table(numLines, nrParallelThreads);

		// Print Column List
		System.out.print("gameNr;alpha;epsilon;");
		for (int i = 0; i < nrParallelThreads; i++)
			System.out.print(i + "_Draw50Games;");
		for (int i = 0; i < nrParallelThreads; i++)
			System.out.print(i + "_Score50Games;");
		System.out.print("AvgScore");
		System.out.println();

		Thread threads[] = new Thread[nrParallelThreads];
		// Create all Threads
		for (int i = 0; i < nrParallelThreads; i++) {
			threads[i] = new Thread(new TrainingThread(i, args, table));
			threads[i].start();
		}

		// wait for all threads
		for (int i = 0; i < threads.length; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
}
