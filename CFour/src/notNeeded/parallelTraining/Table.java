package parallelTraining;

class Table {
	private final TableLine[] table;

	public Table(int numLines, int nrParallelThreads) {
		table = new TableLine[numLines];
		for (int i = 0; i < table.length; i++) {
			table[i] = new TableLine();
			table[i].draw50Games = new double[nrParallelThreads];
			table[i].score50Games = new double[nrParallelThreads];
		}
	}

	public long getGameNum(int i) {
		return table[i].getGameNum();
	}

	public double getAlpha(int i) {
		return table[i].getAlpha();
	}

	public double getEpsilon(int i) {
		return table[i].getEpsilon();
	}

	public double getScore50Games(int i, int threadNr) {
		return table[i].getScore50Games(threadNr);
	}

	public double getDraw50Games(int i, int threadNr) {
		return table[i].getDraw50Games(threadNr);
	}

	public void setGameNum(int i, long gameNum) {
		table[i].setGameNum(gameNum);
	}

	public void setAlpha(int i, double alpha) {
		table[i].setAlpha(alpha);
	}

	public void setEpsilon(int i, double epsilon) {
		table[i].setEpsilon(epsilon);
	}

	public void setScore50Games(int i, int threadNr, double score50Games) {
		table[i].setScore50Games(threadNr, score50Games);
	}

	public void setDraw50Games(int i, int threadNr, double draw50Games) {
		table[i].setDraw50Games(threadNr, draw50Games);
	}

	public void incCounterAndWrite(int i) {
		table[i].incCounterAndWrite();
	}
}