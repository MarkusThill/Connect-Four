package parallelTraining;

import java.text.DecimalFormat;
import java.util.concurrent.Semaphore;

class TableLine {
	private int numScoreEntries = 0;
	private long gameNum = -1;
	private double alpha = -1.0;
	private double epsilon = -1.0;
	double[] score50Games = null;
	double[] draw50Games = null;
	private double avgScore50Games = -1.0;
	
	private Semaphore mutex = new Semaphore(1);

	public long getGameNum() {
		return gameNum;
	}

	public double getAlpha() {
		return alpha;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public double getScore50Games(int threadNr) {
		return score50Games[threadNr];
	}

	public double getDraw50Games(int threadNr) {
		return draw50Games[threadNr];
	}

	public void setGameNum(long gameNum) {
		this.gameNum = gameNum;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}

	public void setScore50Games(int threadNr, double score50Games) {
		this.score50Games[threadNr] = score50Games;
	}

	public void setDraw50Games(int threadNr, double draw50Games) {
		this.draw50Games[threadNr] = draw50Games;
	}

	public void incCounterAndWrite() {
		try {
			mutex.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		numScoreEntries++;
		if (numScoreEntries == score50Games.length) {
			DecimalFormat df = new DecimalFormat("+0,00000;-0,00000");
			// Write line
			// TODO: Choose Seperator
			System.out.print(gameNum + ";" + df.format(alpha) + ";"
					+ df.format(epsilon) + ";");

			// First print all Draws
			for (int i = 0; i < draw50Games.length; i++)
				System.out.print(df.format(draw50Games[i]) + ";");

			// Then print all Scores and calculate avg
			double sum = 0.0;
			for (int i = 0; i < score50Games.length; i++) {
				System.out.print(df.format(score50Games[i]) + ";");
				sum += score50Games[i];
			}

			avgScore50Games = sum / score50Games.length;

			// Print avg. score
			System.out.print(df.format(avgScore50Games) + "");
			System.out.println();
		}

		if (numScoreEntries > score50Games.length) {
			throw new UnsupportedOperationException();
		}
		mutex.release();
	}
}