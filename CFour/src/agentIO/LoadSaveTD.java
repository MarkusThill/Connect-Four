package agentIO;

import gui.C4Buttons;
import gui.C4Frame_v2_14;
import gui.C4Game;
import gui.MessageBox;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.IOUtils;
import nTupleTD.TDParams;
import nTupleTD.TDParams.UpdateMethod;
import nTupleTD.TDSAgent;
import nTupleTD.WeightSubSet;

import competition.Progress;

public class LoadSaveTD {
	private static final String DEFAULT_DIR_AGENT = "agents";
	private final JFileChooserApprove fc;
	private final FileFilter tdAgentExt = new ExtensionFilter("agt.zip",
			"TD-Agents");
	private final FileFilter txtExt = new ExtensionFilter(".txt.zip",
			"Compressed Text-Files (.txt.zip)");
	private final C4Game c4Game;
	private final C4Buttons c4Buttons;
	private final C4Frame_v2_14 c4Frame;

	public LoadSaveTD(C4Game c4Game, C4Buttons c4Buttons, C4Frame_v2_14 c4Frame) {
		this.c4Game = c4Game;
		this.c4Buttons = c4Buttons;
		this.c4Frame = c4Frame;

		fc = new JFileChooserApprove();
		fc.setCurrentDirectory(new File(DEFAULT_DIR_AGENT));
	}

	public JDialog createProgressDialog(final IGetProgress streamProgress,
			final String msg) {
		// ------------------------------------------------------------
		// Setup Progressbar Dialog
		final JDialog dlg = new JDialog(c4Frame, msg, true);
		final JProgressBar dpb = new JProgressBar(0, 100);
		dlg.add(BorderLayout.CENTER, dpb);
		dlg.add(BorderLayout.NORTH, new JLabel("Progress..."));
		dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		dlg.setSize(300, 75);
		dlg.setLocationRelativeTo(c4Frame);

		Thread t = new Thread(new Runnable() {
			public void run() {
				dlg.setVisible(true);
			}
		});
		t.start();

		c4Game.setProgress(new Progress() {
			@Override
			public String getStatusMessage() {
				String str = new String(msg + getProgress() + "%");
				return str;
			}

			@Override
			public int getProgress() {
				int i = (int) (streamProgress.getProgess().get() * 100);
				if (i > 100)
					i = 100;
				dpb.setValue(i);
				return i;
			}
		});
		return dlg; // dialog has to be closed, if not needed anymore with
					// dlg.setVisible(false)
	}

	// ==============================================================
	// Menu: Save Agent
	// ==============================================================
	public void saveTDAgent(TDSAgent td) throws IOException {
		fc.removeChoosableFileFilter(txtExt);
		fc.setFileFilter(tdAgentExt);
		fc.setAcceptAllFileFilterUsed(false);

		int returnVal = fc.showSaveDialog(c4Game);
		String filePath = "";

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = null;
			String path = fc.getSelectedFile().getPath();

			if (!path.toLowerCase().endsWith(".agt.zip"))
				path = path + ".agt.zip";

			file = new File(path);
			filePath = file.getPath();

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(filePath);
			} catch (FileNotFoundException e2) {
				new MessageBox("ERROR: Could not load TDSAgent to " + filePath,
						"C4Game.saveTDAgent");
				c4Buttons.printStatus("[ERROR: Could not save to file "
						+ filePath + " !]");
			}

			GZIPOutputStream gz = null;
			try {
				gz = new GZIPOutputStream(fos) {
					{
						def.setLevel(Deflater.BEST_COMPRESSION);
					}
				};

			} catch (IOException e1) {
				c4Buttons
						.printStatus("[ERROR: Could not create ZIP-OutputStream for"
								+ filePath + " !]");
			}

			// estimate agent size
			long bytes = td.m_Net.getLUTSize(true) * 3 * 2; // 2 LUTs per
															// player, and 3
															// tables (for
															// learning rates)
			// new
			final agentIO.IOProgress p = new agentIO.IOProgress(bytes);
			final ProgressTrackingOutputStream ptos = new ProgressTrackingOutputStream(
					gz, p);

			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(ptos);
			} catch (IOException e) {
				c4Buttons
						.printStatus("[ERROR: Could not create Object-OutputStream for"
								+ filePath + " !]");
			}

			final JDialog dlg = createProgressDialog(ptos, "Saving...");

			try {
				oos.writeObject(td);
			} catch (IOException e) {
				c4Buttons.printStatus("[ERROR: Could not write to file "
						+ filePath + " !]");
			}

			try {
				oos.flush();
				oos.close();
				fos.close();
			} catch (IOException e) {
				c4Buttons
						.printStatus("[ERROR: Could not complete Save-Process]");
			}

			dlg.setVisible(false);
			c4Game.setProgress(null);
			c4Buttons.printStatus("Done.");

		} else
			c4Buttons.printStatus("[Save Agent: Aborted by User]");

		// Rescan current directory, hope it helps
		fc.rescanCurrentDirectory();
	}

	public int estimateGZIPLength(File f) {
		RandomAccessFile raf;
		int fileSize = 0;
		try {
			raf = new RandomAccessFile(f, "r");
			raf.seek(raf.length() - 4);
			byte[] bytes = new byte[4];
			raf.read(bytes);
			fileSize = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
					.getInt();
			if (fileSize < 0)
				fileSize += (1L << 32);
			raf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileSize;
	}

	public TDSAgent loadTDAgent() {
		TDSAgent so = null;
		fc.removeChoosableFileFilter(txtExt);
		fc.setFileFilter(tdAgentExt);
		fc.setAcceptAllFileFilterUsed(false);

		int returnVal = fc.showOpenDialog(c4Game);
		String filePath = "";

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			ObjectInputStream ois = null;
			FileInputStream fis = null;
			try {

				File file = fc.getSelectedFile();
				filePath = file.getPath();
				fis = new FileInputStream(filePath);

				GZIPInputStream gs = new GZIPInputStream(fis);

				long fileLength = (long) (estimateGZIPLength(file));
				final ProgressTrackingObjectInputStream ptis = new ProgressTrackingObjectInputStream(
						gs, new agentIO.IOProgress(fileLength));
				ois = new ObjectInputStream(ptis);

				final JDialog dlg = createProgressDialog(ptis, "Loading...");

				// ois = new ObjectInputStream(gs);
				Object obj = ois.readObject();
				if (obj instanceof TDSAgent) {
					so = (TDSAgent) obj;

					// Hier Änderungen für Konvertieren eintragen
					/*
					 * TDParams p = so.getTDParams(); p.sigOutputFac = 1.0;
					 * p.evaluate = true; so.setTDParams(p);
					 */
				} else {
					new MessageBox("ERROR: Could not load TDSAgent from "
							+ filePath, "C4Game.loadTDAgent");
					c4Buttons
							.printStatus("[ERROR: Could not load TDSAgent from "
									+ filePath + "!]");
				}
				dlg.setVisible(false);
				c4Game.setProgress(null);
				c4Buttons.printStatus("Done.");
			} catch (IOException e) {
				new MessageBox("ERROR: Could not open file " + filePath,
						"C4Game.loadTDAgent");
				c4Buttons.printStatus("[ERROR: Could not open file " + filePath
						+ " !]");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
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
			c4Buttons
					.printStatus("[ERROR: Something went wrong while loading file "
							+ filePath + " !]");
		return so;
	}

	public TDSAgent loadTDAgentWeights() {
		fc.removeChoosableFileFilter(tdAgentExt);
		fc.setFileFilter(txtExt);
		fc.setAcceptAllFileFilterUsed(false);
		TDSAgent tdAgent = null;
		int returnVal = fc.showOpenDialog(c4Game);
		String filePath = "";

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			filePath = file.getPath();
			ZipFile zipFile = null;
			try {
				zipFile = new ZipFile(filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}

			ZipArchiveEntry zipArchiveEntry1 = zipFile.getEntry("p0.txt");
			ZipArchiveEntry zipArchiveEntry2 = zipFile.getEntry("p1.txt");

			String s1 = getZipContentFiles(zipFile, zipArchiveEntry1);
			String s2 = getZipContentFiles(zipFile, zipArchiveEntry2);

			try {
				zipFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			//
			// Parse both files...
			//
			ParseResult pr1 = parseTxtFile(s1, "p0.txt");
			ParseResult pr2 = parseTxtFile(s2, "p1.txt");

			//
			// Compare the results of both files and find errors...
			//
			checkParseResults(pr1, pr2);

			TDParams tdPar = createTDParamsFromParse(pr1);
			
			final IOProgress prog = new IOProgress(pr1.t * 2); // since we have two sets of luts..
			IGetProgress progress = new IGetProgress() {
				@Override
				public IOProgress getProgess() {
					return prog;
				}
			};

			final JDialog dlg = createProgressDialog(progress, "Create Agent ... ");
			//
			// Create TD-Agent
			//
			tdAgent = new TDSAgent(tdPar);
			//
			// Update the LUTs of the agent
			//
			WeightSubSet[][] ws = tdAgent.getWeightSubset();
			for(int i=0;i<ws.length;i++) { //0,1
				ParseResult pr = (i==0 ? pr1 : pr2);
				for(int j=0;j<ws[i].length;j++) {
					float[] lut = ws[i][j].getLUT();
					ArrayList<Float> newLUT = pr.list.get(j).lut;
					if(lut.length != newLUT.size())
						throw new UnsupportedOperationException("Error. Problem with the LUT-sizes!");
					for(int k=0;k<lut.length;k++) {
						lut[k] = newLUT.get(k);
					}
					prog.update(1);
				}
			}
			dlg.setVisible(false);

			c4Game.setProgress(null);
			c4Buttons.printStatus("Done.");
		}
		return tdAgent;
	}

	private TDParams createTDParamsFromParse(ParseResult pr1) {
		//
		// Create a TDParams-file
		//
		TDParams tdPar = new TDParams();
		tdPar.tupleNum = pr1.t;
		tdPar.posVals = pr1.p;
		tdPar.updateMethod = UpdateMethod.TDL;
		//
		// Create n-tuples array. The samples of the first set are enough.
		// Symmetries are automatically considered later...
		//
		ArrayList<Integer[]> tupleList = new ArrayList<>(pr1.t);
		for (TupelAndLUT i : pr1.list) {
			ArrayList<Integer> set = i.samplePointSets.get(0);
			Integer[] tuple = set.toArray(new Integer[set.size()]);
			tupleList.add(tuple);
		}
		tdPar.nTuples = tupleList.toArray(new Integer[tupleList.size()][]);
		return tdPar;
	}

	private void checkParseResults(ParseResult pr1, ParseResult pr2) {
		if (pr1.t != pr2.t)
			throw new UnsupportedOperationException(
					"Both text-files contain differing numbers of n-tuples");
		if (pr1.p != pr2.p)
			throw new UnsupportedOperationException(
					"Both text-files contain differing values for the number of possible states per sample point (P).");

		if (pr1.list.size() != pr1.t) {
			throw new UnsupportedOperationException(
					"The number of n-tuples for the first file is wrong!");
		}
		if (pr2.list.size() != pr2.t) {
			throw new UnsupportedOperationException(
					"The number of n-tuples for the second file is wrong!");
		}

		//
		// Iterate through the list
		//
		for (int i = 0; i < pr1.list.size(); i++) {
			//
			// Compare both n
			//
			TupelAndLUT tal1 = pr1.list.get(i);
			TupelAndLUT tal2 = pr2.list.get(i);
			if (tal1.n != tal2.n)
				throw new UnsupportedOperationException("The lengths of tuple"
						+ i + " differ!");
			if (tal1.numSampleSets != tal2.numSampleSets)
				throw new UnsupportedOperationException(
						"The number of tuple sets for tuple" + i + " differ!");
			if (tal1.numSampleSets != 2)
				throw new UnsupportedOperationException(
						"The number of tuple sets for tuple" + i + " is not 2!");
			for (int j = 0; j < 2; j++) {
				ArrayList<Integer> set1 = tal1.samplePointSets.get(j);
				ArrayList<Integer> set2 = tal1.samplePointSets.get(j);
				//
				// Both sample sets have to be equal
				//
				if (set1.size() != set2.size())
					throw new UnsupportedOperationException(
							"The number of sample points in set " + j
									+ " of n-tuple " + i + "differ!!");
				for (int k = 0; k < set1.size(); k++) {
					int i1 = set1.get(k);
					int i2 = set2.get(k);
					if (i1 != i2)
						throw new UnsupportedOperationException(
								"The sample points in set " + j
										+ " of n-tuple " + i + "differ!!");
				}

			}

		}
	}

	public ParseResult parseTxtFile(String txt, String name) {
		final String BRACKET_O = "{";
		final String BRACKET_C = "}";
		Scanner s = new Scanner(txt);
		s.useLocale(Locale.ENGLISH);

		//
		// First symol should be "{"
		//
		String l = s.nextLine();
		assert (l.equals(BRACKET_O));

		//
		// The next line contains the number of n-tuples
		//
		final int t = s.nextInt();
		s.nextLine(); // because nextInt() does not complete the line

		//
		// The next line contains the number of possible values per sample point
		//
		int p = s.nextInt();
		s.nextLine(); // because nextInt() does not complete the line

		ArrayList<TupelAndLUT> list = new ArrayList<>();

		final IOProgress prog = new IOProgress(t);
		IGetProgress progress = new IGetProgress() {
			@Override
			public IOProgress getProgess() {
				return prog;
			}
		};

		final JDialog dlg = createProgressDialog(progress, "Parsing File "
				+ name + " ... ");

		// Now iterate through all n-tuples with their corresponding LUTS
		while (s.hasNext()) {
			prog.update(1);
			//
			// Next symbol should be "{" or "}", if the file is finished
			//
			l = s.nextLine();
			if (l.equals(BRACKET_C))
				break;
			assert (l.equals(BRACKET_O));

			//
			// Next token is the length of the n-tuple
			//
			int n = s.nextInt();
			s.nextLine(); // because nextInt() does not complete the line

			//
			// Next token is the number of tuple-sets (2, if symmetries are
			// used)
			//
			int numTupleSets = s.nextInt();
			s.nextLine(); // because nextInt() does not complete the line

			//
			// Now read in all sets of sample-points (there should be normally
			// one or two sets)....
			//
			ArrayList<ArrayList<Integer>> samplePointSets = new ArrayList<>(
					numTupleSets);
			for (int i = 0; i < numTupleSets; i++) {
				//
				// next token should be a set of n sample points, in the form:
				// "{ s1 s2 s3 ... sn }"
				//
				String sampleSet = s.nextLine();
				Scanner s1 = new Scanner(sampleSet);
				ArrayList<Integer> a = new ArrayList<>();
				while (s1.hasNext()) {
					if (!s1.hasNextInt()) {
						String d = s1.next();
						if (d.equals(BRACKET_O))
							continue;
						if (d.equals(BRACKET_C))
							break;
						s1.close();
						s.close();
						throw new UnsupportedOperationException(
								"Somethings wrong in the text-file");
					}
					int x = s1.nextInt();
					a.add(x);
				}
				s1.close();
				// The number of sample points should be equal to n
				assert (a.size() == n);
				samplePointSets.add(a);
			}
			// TODO: Check if the sample points are really symmetric....

			//
			// Now read in LUT
			//
			String startLut = s.next();
			assert (startLut.equals(BRACKET_O));
			ArrayList<Float> lut = new ArrayList<>((int) Math.pow(p, n));
			while (s.hasNext()) {
				if (!s.hasNextFloat()) {
					String d = s.next();
					if (!d.equals(BRACKET_C)) {
						s.close();
						throw new UnsupportedOperationException(
								"Somethings wrong in the text-file");
					}
					s.nextLine();
					break;
				}
				Float f = s.nextFloat();
				lut.add(f);
			}
			//
			// The Size should be p^n
			//
			if (lut.size() != (int) Math.pow(p, n)) {
				s.close();
				throw new UnsupportedOperationException(
						"The size of the LUT is wrong!!!");
			}

			//
			// Next symbol should be "}", since this n-tuple is finished
			//
			l = s.nextLine();
			assert (l.equals(BRACKET_C));

			TupelAndLUT tal = new TupelAndLUT();
			tal.lut = lut;
			tal.n = n;
			tal.numSampleSets = numTupleSets;
			tal.samplePointSets = samplePointSets;
			list.add(tal);
		}

		dlg.setVisible(false);

		//
		// Check, if we really read in the information of t n-tuples
		//
		if (list.size() != t) {
			s.close();
			throw new UnsupportedOperationException(
					"T is not equal to the number of provided n-tuples");
		}

		s.close();

		//
		// Create ParseResult
		//
		ParseResult pr = new ParseResult();
		pr.p = p;
		pr.t = t;
		pr.list = list;
		return pr;
	}

	public String getZipContentFiles(ZipFile zipFile,
			ZipArchiveEntry zipArchiveEntry) {
		String txtFileContent = null;
		InputStream is = null;
		try {
			is = zipFile.getInputStream(zipArchiveEntry);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ZipArchiveInputStream zis = new ZipArchiveInputStream(is);
		if (zis.canReadEntryData(zipArchiveEntry)) {
			long fileLength = (long) zipArchiveEntry.getSize();
			ProgressTrackingInputStream ptis = null;
			try {
				ptis = new ProgressTrackingInputStream(is,
						new agentIO.IOProgress(fileLength));
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			final JDialog dlg = createProgressDialog(ptis, "Loading "
					+ zipArchiveEntry.getName() + "...");

			InputStreamReader isr = new InputStreamReader(ptis);
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(isr);
			String read = null;
			try {
				read = br.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			while (read != null) {
				sb.append(read);
				sb.append(System.lineSeparator());
				try {
					read = br.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			dlg.setVisible(false);
			txtFileContent = sb.toString();
			try {
				br.close();
				isr.close();
				ptis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		try {
			zis.close();
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return txtFileContent;
	}

	public void saveTDAgentWeights2(final TDSAgent td) throws IOException {
		fc.removeChoosableFileFilter(tdAgentExt);
		fc.setFileFilter(txtExt);
		fc.setAcceptAllFileFilterUsed(false);

		int returnVal = fc.showSaveDialog(c4Game);
		String filePath = "";

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = null;
			String path = fc.getSelectedFile().getPath();

			if (!path.toLowerCase().endsWith(".txt.zip"))
				path = path + ".txt.zip";

			file = new File(path);
			filePath = file.getPath();

			FileOutputStream fos_zip = null;
			try {
				fos_zip = new FileOutputStream(filePath);
			} catch (FileNotFoundException e2) {
				new MessageBox("ERROR: Could not save Weights to " + filePath,
						"C4Game.saveTDAgent");
				c4Buttons.printStatus("[ERROR: Could not save to file "
						+ filePath + " !]");
			}

			// estimate agent size
			long bytes = td.m_Net.getLUTSize(false) * 3; // estimate 3 bytes per
															// weight

			//
			// Zip the files...
			//
			ArchiveOutputStream logical_zip = null;
			try {
				logical_zip = new ArchiveStreamFactory()
						.createArchiveOutputStream(ArchiveStreamFactory.ZIP,
								fos_zip);
			} catch (ArchiveException e1) {
				e1.printStackTrace();
			}

			for (int i = 0; i < 2; i++) {
				final int j = i;
				/* Create Archive entry - write header information */
				logical_zip.putArchiveEntry(new ZipArchiveEntry("p" + i
						+ ".txt"));

				/* Copy input file */
				PipedOutputStream outPipe = new PipedOutputStream();
				PipedInputStream inPipe = new PipedInputStream(outPipe);

				final agentIO.IOProgress p = new agentIO.IOProgress(bytes);
				final ProgressTrackingPrintStream ptos = new ProgressTrackingPrintStream(
						outPipe, p);

				final JDialog dlg = createProgressDialog(ptos, "Saving File "
						+ (i + 1) + "...");

				//
				// Print to file. THis has to be done in a thread, otherwise
				// ptos would block, if the buffer is full...
				//
				new Thread(new Runnable() {
					@Override
					public void run() {
						printWeights(ptos, td, j);
						ptos.flush();
						ptos.close();
					}
				}).start();

				IOUtils.copy(inPipe, logical_zip);
				logical_zip.closeArchiveEntry();

				dlg.setVisible(false);
			}

			/* Finish addition of entries to the file */
			logical_zip.finish();

			try {
				fos_zip.close();
			} catch (IOException e) {
				c4Buttons
						.printStatus("[ERROR: Could not complete Save-Process]");
			}

			c4Game.setProgress(null);
			c4Buttons.printStatus("Done.");

		} else
			c4Buttons.printStatus("[Save Agent: Aborted by User]");

		// Rescan current directory, hope it helps
		fc.rescanCurrentDirectory();
	}

	private void printWeights(PrintStream ps, TDSAgent td, int player) {
		WeightSubSet[][] ws = td.getWeightSubset();
		Integer[][] nTuples = td.m_Net.getNTuples1Dim();
		int sampleSetNum = td.getTDParams().useSymmetry ? 2 : 1;

		// Use this Board representation:
		// 5 11 17 23 29 35 41
		// 4 10 16 22 28 34 40
		// 3 9 15 21 27 33 39
		// 2 8 14 20 26 32 38
		// 1 7 13 19 25 31 37
		// 0 6 12 18 24 30 36

		//
		// Print opening bracket '{' and number of n-tuples
		//
		ps.println("{");
		ps.println(ws[player].length);
		// print P. P=4 is the typical value
		ps.println(td.getTDParams().posVals);

		for (int i = 0; i < ws[player].length; i++) {
			ps.println("{");
			//
			// print n (number of sample points of this n-tuple)
			//
			ps.println(nTuples[i].length);

			//
			// print m: How many sample-point sets do we have
			//
			ps.println(sampleSetNum);

			//
			// print n-Tuple
			//
			ps.print("{ ");
			for (int k : nTuples[i]) {
				ps.print(k + " ");
			}
			ps.println("}");

			//
			// If we use symmetries, also print the symmetric sample points
			//
			if (sampleSetNum == 2) {
				ps.print("{ ");
				for (int k : nTuples[i]) {
					// int f = 1-2*(k/24);
					int k1 = k + (36 - (k / 6) * 12);
					ps.print(k1 + " ");
				}
				ps.println("}");
			}

			ps.print("{ ");
			float lut[] = ws[player][i].getLUT();
			for (int j = 0; j < lut.length; j++) {
				if (lut[j] != 0.0f)
					ps.print(lut[j]);
				else
					ps.print("0");
				ps.print(" ");
				if ((j + 1) % 10 == 0)
					ps.println();
			}
			ps.println("} ");

			ps.println("}");
		}

		//
		// Print opening bracket '{'
		//
		ps.println("}");
	}

	private class TupelAndLUT {
		int n;
		int numSampleSets; // 1 or 2 (if symmetry is used..)
		ArrayList<ArrayList<Integer>> samplePointSets;
		ArrayList<Float> lut;

	}

	private class ParseResult {
		int t; // number of n-tuples
		int p; // number of possible values per sample point
		ArrayList<TupelAndLUT> list;
	}

}
