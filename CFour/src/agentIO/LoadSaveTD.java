package agentIO;

import gui.C4Buttons;
import gui.C4Frame_v2_14;
import gui.C4Game;
import gui.MessageBox;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import nTupleTD.TDSAgent;


import competition.Progress;

public class LoadSaveTD {

	private final JFileChooserApprove fc;
	private final C4Game c4Game;
	private final C4Buttons c4Buttons;
	private final C4Frame_v2_14 c4Frame;
	


	public LoadSaveTD(C4Game c4Game, C4Buttons c4Buttons, C4Frame_v2_14 c4Frame) {
		this.c4Game = c4Game;
		this.c4Buttons = c4Buttons;
		this.c4Frame = c4Frame;
		
		fc = new JFileChooserApprove();
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
				if(i>100)
					i=100;
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
		
		fc.setFileFilter((new AgentFilter()));
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
			long bytes = td.m_Net.getLUTSize(true) * 3 * 2; // 2 LUTs per player, and 3 tables (for learning rates)
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
			fileSize = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getInt();
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

		fc.setFileFilter((new AgentFilter()));
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
				final ProgressTackingInputStream ptis = new ProgressTackingInputStream(
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
}
