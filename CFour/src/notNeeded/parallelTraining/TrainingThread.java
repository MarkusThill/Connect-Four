package parallelTraining;

import c4.TDParams;
import c4.TDSAgent;

class TrainingThread implements Runnable {
	final int threadNr;
	final String args[];
	final Table table;

	TrainingThread(int threadNr, String[] args, Table table) {
		this.threadNr = threadNr;
		this.args = args;
		this.table = table;
	}

	@Override
	public void run() {
		ParallelTraining pt = new ParallelTraining(threadNr, table);
		TDSAgent tds = (TDSAgent) pt.initTDSAgent(args[0]);

		// Save param-file (e.g. after random n-tuple-creation)
		// deactivate Random n-tuple-creation in this case
		if (args.length >= 2) {
			// add extension to path to save TDParams under other filename
			String path = new String(args[0]);
			// TODO: Global constant for .tdPar because extension can change....
			path = path.replaceFirst(".tdPar", args[1] + ".tdPar");
			TDParams td = tds.getTDParams();
			pt.saveTDParams(td, path, false);
		}

		// Train Agent
		pt.trainTDSAgent(tds);

	}

}