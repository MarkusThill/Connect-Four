package c4;

import java.io.IOException;
import openingBook.Book;
import openingBook.BookSum;

import junit.framework.TestCase;

/**
 * Some Tests for critical methods of the Alpha-Beta-Agent
 * 
 * @author Markus Thill 
 * 
 */
public class AlphaBetaAgentTest extends TestCase {

	int field[][] = new int[][] { { 1, 2, 1, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 },
			{ 1, 1, 2, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 },
			{ 2, 2, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0 } };

	private static BookSum books = new BookSum();
	AlphaBetaAgent c4 = new AlphaBetaAgent(field, books);

	public void testAlphaBetaAgentIntArrayArray() {
		c4 = new AlphaBetaAgent(field, books);

		int[] expected2 = { 3, 0, 3, 0, 0, 2, 0 };
		int[] result2 = c4.getColHeight();
		for (int i = 0; i < 7; i++)
			assertEquals(expected2[i], result2[i]);
	}

	public void testAlphaBetaAgent() {
		c4 = new AlphaBetaAgent(books);
		int[] expected2 = { 0, 0, 0, 0, 0, 0, 0 };
		int[] result2 = c4.getColHeight();
		for (int i = 0; i < 7; i++)
			assertEquals(expected2[i], result2[i]);
	}

	public void testPrintBoard() {
		c4.printBoard();
	}

	public void testResetBoard() {
		c4.resetBoard();

		long expected1 = 0L;
		long result1 = c4.getField((int) 1) | c4.getField((int) 2);
		assertEquals(expected1, result1);

		int[] expected2 = { 0, 0, 0, 0, 0, 0, 0 };
		int[] result2 = c4.getColHeight();
		for (int i = 0; i < 7; i++)
			assertEquals(expected2[i], result2[i]);

	}

	public void testGetMask() {
		long expected1 = 1L;
		long result1 = c4.getMask((int) 6, (int) 5);
		assertEquals(expected1, result1);

		long expected2 = 0x20000000000L;
		long result2 = c4.getMask((int) 0, (int) 0);
		assertEquals(expected2, result2);

		long expected3 = 0x100000000L;
		long result3 = c4.getMask((int) 1, (int) 3);
		assertEquals(expected3, result3);
	}

	public void testComputeFieldMasks() {
		long expected1 = 0x20000000000L;
		long result1 = c4.getFieldMask((int) 0, (int) 0);
		assertEquals(expected1, result1);

		long expected2 = 0x400000000L;
		long result2 = c4.getFieldMask((int) 1, (int) 1);
		assertEquals(expected2, result2);

		long expected3 = 0x1L;
		long result3 = c4.getFieldMask((int) 6, (int) 5);
		assertEquals(expected3, result3);
	}

	public void testIsLegalBoard() {
		assertTrue(c4.isLegalBoard(field));

		field[0][4] = 1;
		assertFalse(c4.isLegalBoard(field));

		field[0][4] = 0;
		field[0][2] = 3;
		assertFalse(c4.isLegalBoard(field));
	}

	public void testIsLegalMove() {
		assertTrue(c4.isLegalMove((int) 0));

		c4.putPiece((int) 1, (int) 0);
		c4.putPiece((int) 1, (int) 0);
		c4.putPiece((int) 1, (int) 0);
		assertFalse(c4.isLegalMove((int) 0));
	}

	public void testPutPiece() {
		c4.putPiece((int) 1, (int) 6);
		c4.putPiece((int) 2, (int) 0);

		long expected1 = 0x28030000020L;
		long result1 = c4.getField((int) 1);
		assertEquals(expected1, result1);

		long expected2 = 0x14008000C00L;
		long result2 = c4.getField((int) 2);
		assertEquals(expected2, result2);

		int[] expected3 = { 4, 0, 3, 0, 0, 2, 1 };
		int[] result3 = c4.getColHeight();
		for (int i = 0; i < 7; i++)
			assertEquals(expected3[i], result3[i]);

	}

	public void testRemovePiece() {
		c4.removePiece((int) 1, (int) 0);
		c4.removePiece((int) 2, (int) 5);

		long expected1 = 0x20030000000L;
		long result1 = c4.getField((int) 1);
		assertEquals(expected1, result1);

		long expected2 = 0x10008000800L;
		long result2 = c4.getField((int) 2);
		assertEquals(expected2, result2);

		int[] expected3 = { 2, 0, 3, 0, 0, 1, 0 };
		int[] result3 = c4.getColHeight();
		for (int i = 0; i < 7; i++)
			assertEquals(expected3[i], result3[i]);

	}

	public void testComputeColHeight() {
		c4.computeColHeight();
		int expected1 = 8;
		int result1 = c4.countPieces();
		assertEquals(expected1, result1);

		c4.resetBoard();
		c4.computeColHeight();
		int expected2 = 0;
		int result2 = c4.countPieces();
		assertEquals(expected2, result2);

	}

	public void testCountPieces() {

		int expected1 = 8;
		int result1 = c4.countPieces();
		assertEquals(expected1, result1);

		c4.resetBoard();
		int expected2 = 0;
		int result2 = c4.countPieces();
		assertEquals(expected2, result2);
	}

	public void testCanWin() {
		assertFalse(c4.canWin((int) 1, (int) 1, (int) 1));
		assertFalse(c4.canWin((int) 2, (int) 1, (int) 1));
		assertFalse(c4.canWin((int) 1, (int) 3, (int) 4));

		c4.putPiece((int) 1, (int) 0);
		c4.putPiece((int) 1, (int) 0);
		assertFalse(c4.canWin((int) 2, (int) 0, (int) 5));

		c4.putPiece((int) 2, (int) 6);
		c4.putPiece((int) 2, (int) 4);
		assertTrue(c4.canWin((int) 2, (int) 3, (int) 0));
		assertFalse(c4.canWin((int) 1, (int) 3, (int) 0));
	}

	public void testHasWin() {
		assertFalse(c4.hasWin((int) 1));
		assertFalse(c4.hasWin((int) 2));

		c4.putPiece((int) 1, (int) 0);
		c4.putPiece((int) 1, (int) 0);
		assertFalse(c4.hasWin((int) 2));

		c4.putPiece((int) 2, (int) 6);
		c4.putPiece((int) 2, (int) 4);
		assertTrue(c4.hasWin((int) 1));
		assertTrue(c4.hasWin((int) 2));
	}

	public void testFindThreat() {
		int expected1 = -1;
		int result1 = c4.findThreat((int) 1, (int) 0);
		assertEquals(expected1, result1);

		int expected2 = -1;
		int result2 = c4.findThreat((int) 2, (int) 0);
		assertEquals(expected2, result2);

		c4.putPiece((int) 2, (int) 3);
		c4.putPiece((int) 1, (int) 3);
		c4.putPiece((int) 2, (int) 1);

		int expected3 = 1;
		int result3 = c4.findThreat((int) 1, (int) 0);
		assertEquals(expected3, result3);

	}

	public void testFindOddThreatP1() {
		int expected1 = -1;
		int result1 = c4.findOddThreatP1((int) 0);
		assertEquals(expected1, result1);

		c4.putPiece((int) 1, (int) 3);
		int expected2 = 0;
		int result2 = c4.findOddThreatP1((int) 0);
		assertEquals(expected2, result2);

	}

	public void testGenerateMoves() {
		int[] expected1 = { 2, 5, 3, 1, 4, 0, 6, -1 };
		int[] result1 = c4.generateMoves((int) 1, true);
		for (int i = 0; i < 8; i++)
			assertEquals(expected1[i], result1[i]);

		c4.putPiece((int) 1, (int) 2);
		c4.putPiece((int) 2, (int) 2);
		int[] expected2 = { 5, 3, 2, 1, 4, 0, 6, -1 };
		int[] result2 = c4.generateMoves((int) 1, true);
		for (int i = 0; i < 8; i++)
			assertEquals(expected2[i], result2[i]);
	}

	public void testGetMirrored() {
		long expected1 = 0x30028L;
		long result1 = c4.getMirroredField(1);
		assertEquals(expected1, result1);

		c4.putPiece((int) 1, (int) 1);
		c4.putPiece((int) 1, (int) 3);
		long expected2 = 0x830828L;
		long result2 = c4.getMirroredField(1);
		assertEquals(expected2, result2);
	}

	public void testGetMirroredFieldP2() {
		long expected1 = 0xC00008010L;
		long result1 = c4.getMirroredField(2);
		assertEquals(expected1, result1);

		c4.putPiece((int) 2, (int) 1);
		c4.putPiece((int) 2, (int) 3);
		long expected2 = 0xC00808810L;
		long result2 = c4.getMirroredField(2);
		assertEquals(expected2, result2);
	}

	public void testfieldToHuffman() {
		int expected1 = 0xECF850;
		int result1 = c4.fieldToHuffman(c4.getField((int) 1),
				c4.getField((int) 2), false);
		assertEquals(expected1, result1);

		c4 = new AlphaBetaAgent(0xC00280000L, 0xD40000L, books);
		int expected2 = 0x795DC0;
		int result2 = c4.fieldToHuffman(0xC00280000L, 0xD40000L, false);
		assertEquals(expected2, result2);

		c4 = new AlphaBetaAgent(0x8002A0000L, 0xD40000L, books);
		int expected3 = 0x657730;
		int result3 = c4.fieldToHuffman(0x8002A0000L, 0xD40000L, false);
		assertEquals(expected3, result3);

		c4 = new AlphaBetaAgent(0x28280000L, 0x10D00000L, books);
		int expected4 = 0x3B5760;
		int result4 = c4.fieldToHuffman(0x28280000L, 0x10D00000L, false);
		assertEquals(expected4, result4);

		c4 = new AlphaBetaAgent(0x202C0000L, 0x10D00000L, books);
		int expected5 = 0x395DE0;
		int result5 = c4.fieldToHuffman(0x202C0000L, 0x10D00000L, false);
		assertEquals(expected5, result5);

		c4 = new AlphaBetaAgent(0x20020280000L, 0x10D00000L, books);
		int expected6 = 0xCE5760;
		int result6 = c4.fieldToHuffman(0x20020280000L, 0x10D00000L, false);
		assertEquals(expected6, result6);

		int expected7 = 0x15DB8C;
		int result7 = c4.fieldToHuffman(c4.getMirroredField(1),
				c4.getMirroredField(2), true);
		assertEquals(expected7, result7);

		c4 = new AlphaBetaAgent(0x30280000L, 0xD00020L, books);
		int expected8 = 0x8576F0;
		int result8 = c4.fieldToHuffman(c4.getMirroredField(1),
				c4.getMirroredField(2), true);
		assertEquals(expected8, result8);

		c4 = new AlphaBetaAgent(0x20030200000L, 0x8D00000L, books);
		int expected9 = 0x15CF8C;
		int result9 = c4.fieldToHuffman(c4.getMirroredField(1),
				c4.getMirroredField(2), true);
		assertEquals(expected9, result9);
	}

	public void testAlphaBetaStart() {

		c4 = new AlphaBetaAgent(books);

		int result0 = c4.rootNode(false);
		assertTrue(result0 == 3);

		c4.putPiece((int) 1, (int) 2);
		int result1 = c4.rootNode(false);
		assertTrue(result1 == 2);

		c4 = new AlphaBetaAgent(0x20800000L, 0x10000000L, books);
		int result2 = c4.rootNode(false);
		assertTrue(result2 == 1 || result2 == 4);

		c4 = new AlphaBetaAgent(0x28800000L, 0x810000000L, books);
		int result3 = c4.rootNode(false);
		assertTrue(result3 == 2);

		c4 = new AlphaBetaAgent(0x428800000L, 0x814000000L, books);
		int result4 = c4.rootNode(false);
		assertTrue(result4 == 1);

		c4 = new AlphaBetaAgent(0x800000000L, 0x0L, books);
		int result5 = c4.rootNode(false);
		assertTrue(result5 == 2);

		c4 = new AlphaBetaAgent(0x810000000L, 0x20000000L, books);
		int result6 = c4.rootNode(false);
		assertTrue(result6 == 2);

		c4 = new AlphaBetaAgent(0x810020000L, 0x28000000L, books);
		int result7 = c4.rootNode(false);
		assertTrue(result7 == 2);

		c4 = new AlphaBetaAgent(0x810030000L, 0x2C000000L, books);
		int result8 = c4.rootNode(false);
		assertTrue(result8 == 2);

		c4 = new AlphaBetaAgent(0x800000L, 0x800L, books);
		int result9 = c4.rootNode(false);
		assertTrue(result9 == 5);

		c4 = new AlphaBetaAgent(0x800000L, 0x800000000L, books);
		int result10 = c4.rootNode(false);
		assertEquals(result10, 1);

		c4 = new AlphaBetaAgent(0x800000L, 0x400000L, books);
		int result11 = c4.rootNode(false);
		assertEquals(result11, 3);

		c4 = new AlphaBetaAgent(0xA00000L, 0x500000L, books);
		int result12 = c4.rootNode(false);
		assertEquals(result12, 3);

		c4 = new AlphaBetaAgent(0x400800000L, 0x820000000L, books);
		int result13 = c4.rootNode(false);
		assertEquals(result13, 1);

		c4 = new AlphaBetaAgent(0x800400L, 0x20800L, books);
		int result14 = c4.rootNode(false);
		assertEquals(result14, 5);

		c4 = new AlphaBetaAgent(0x800000L, 0x20000000000L, books);
		int result15 = c4.rootNode(false);
		assertEquals(result15, 3);

		c4 = new AlphaBetaAgent(0x800800000L, 0x20000000000L, books);
		int result16 = c4.rootNode(false);
		assertEquals(result16, 3);

		c4 = new AlphaBetaAgent(0x10800000L, 0x20000000L, books);
		int result17 = c4.rootNode(false);
		assertEquals(result17, 2);

		c4 = new AlphaBetaAgent(0x30000000L, 0x800L, books);
		int result18 = c4.rootNode(false);
		assertEquals(result18, 2);

		c4 = new AlphaBetaAgent(0x20000800000L, 0x800000000L, books);
		int result19 = c4.rootNode(false);
		//assertEquals(result19, 2);
		assertTrue(result19 == 1 || result19 == 3);

		c4 = new AlphaBetaAgent(0x800800000L, 0x20000000000L, books);
		int result20 = c4.rootNode(false);
		assertEquals(result20, 3);

		c4 = new AlphaBetaAgent(0x20000800000L, 0x800L, books);
		int result21 = c4.rootNode(false);
		assertEquals(result21, 3);

		c4 = new AlphaBetaAgent(0x810000000L, 0x20000000L, books);
		int result22 = c4.rootNode(false);
		assertEquals(result22, 2);

		c4 = new AlphaBetaAgent(0x20000010000L, 0x20000L, books);
		int result23 = c4.rootNode(false);
		//assertEquals(result22, 2);
		assertTrue(result23 == 3 || result23 == 4);

		c4 = new AlphaBetaAgent(0x20020000L, 0x10000000L, books);
		int result24 = c4.rootNode(false);
		assertEquals(result24, 3);

		c4 = new AlphaBetaAgent(0x20000000400L, 0x800L, books);
		int result25 = c4.rootNode(false);
		assertEquals(result25, 5);

	}

	public void testAlphaBeta() throws IOException {
		int i = 2;

		c4 = new AlphaBetaAgent(0x804C10800L, 0x20038020000L, books);
		int result2 = c4.rootNode(false);
		assertTrue(result2 == 3);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x820118800L, 0x10E20000L, books);
		int result3 = c4.rootNode(false);
		assertTrue(result3 == 5);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x20A10220800L, 0x10428C00000L, books);
		int result4 = c4.rootNode(false);
		assertTrue(result4 == 3);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x10820020400L, 0x20410000800L, books);
		int result5 = c4.rootNode(false);
		assertTrue(result5 == 3);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0xD10800000L, 0x20220400000L, books);
		int result6 = c4.rootNode(false);
		assertTrue(result6 == 3);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x30000820800L, 0x20018400L, books);
		int result7 = c4.rootNode(false);
		assertTrue(result7 == 6);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x30A08000L, 0x434000L, books);
		int result8 = c4.rootNode(false);
		assertTrue(result8 == 2);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x30020220000L, 0x800034000L, books);
		int result9 = c4.rootNode(false);
		assertTrue(result9 != 6);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x18800820000L, 0x20600000800L, books);
		int result10 = c4.rootNode(false);
		assertTrue(result10 == 2);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x420830000L, 0x20800400800L, books);
		int result11 = c4.rootNode(false);
		assertTrue(result11 == 3);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x20020018800L, 0x18820000L, books);
		int result12 = c4.rootNode(false);
		assertTrue(result12 == 2);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x2002C010000L, 0x10020C00L, books);
		int result13 = c4.rootNode(false);
		assertTrue(result13 == 3);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0xA20020800L, 0x20400010400L, books);
		int result14 = c4.rootNode(false);
		assertTrue(result14 == 3);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x830210800L, 0x400C28000L, books);
		int result15 = c4.rootNode(false);
		assertTrue(result15 == 4);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x20C00800400L, 0x20030800L, books);
		int result16 = c4.rootNode(false);
		assertTrue(result16 == 4);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0xC20600000L, 0x300800800L, books);
		int result17 = c4.rootNode(false);
		assertTrue(result17 == 1);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0xA00020600L, 0x20400800800L, books);
		int result18 = c4.rootNode(false);
		assertTrue(result18 == 3);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x800220C00L, 0x20000C10000L, books);
		int result19 = c4.rootNode(false);
		assertTrue(result19 == 4);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x18000A00800L, 0x24000420000L, books);
		int result20 = c4.rootNode(false);
		assertTrue(result20 == 4);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0xC00A04C00L, 0x30000438000L, books);
		int result21 = c4.rootNode(false);
		assertTrue(result21 == 0);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x430020800L, 0x20800810000L, books);
		int result22 = c4.rootNode(false);
		assertTrue(result22 == 4);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x20010C08000L, 0x10020030000L, books);
		int result23 = c4.rootNode(false);
		assertTrue(result23 == 3);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x800830200L, 0x20000D00L, books);
		int result24 = c4.rootNode(false);
		assertTrue(result24 == 4);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x20030A00000L, 0x800420800L, books);
		int result25 = c4.rootNode(false);
		assertTrue(result25 == 2);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x30C00800L, 0x30000020400L, books);
		int result26 = c4.rootNode(false);
		assertTrue(result26 == 3);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x4800810C00L, 0x38020020000L, books);
		int result27 = c4.rootNode(false);
		assertTrue(result27 == 2);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x20420020400L, 0x800800A00L, books);
		int result28 = c4.rootNode(false);
		assertTrue(result28 == 3);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0xC20810000L, 0x20010020800L, books);
		int result29 = c4.rootNode(false);
		assertTrue(result29 == 4);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x10820010800L, 0x20000820400L, books);
		int result30 = c4.rootNode(false);
		assertTrue(result30 == 4);
		System.out.print(i++ + "\n");

		long zstVorher;
		long zstNachher;

		zstVorher = System.currentTimeMillis();

		zstNachher = System.currentTimeMillis();
		System.out.println("Zeit benötigt: " + ((zstNachher - zstVorher))
				+ " msec\n");

		c4 = new AlphaBetaAgent(0x20800810000L, 0x430020000L, books);
		int result31 = c4.rootNode(false);
		assertTrue(result31 == 0);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x400A80000L, 0xA00500000L, books);
		int result32 = c4.rootNode(false);
		assertTrue(result32 == 1);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0xA80400L, 0x500A00L, books);
		int result33 = c4.rootNode(false);
		assertTrue(result33 == 5);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0xAA0000L, 0x20540000L, books);
		int result34 = c4.rootNode(false);
		assertTrue(result34 == 2);
		System.out.print(i++ + "\n");

		c4 = new AlphaBetaAgent(0x0L, 0x0L, books);
		int result35 = c4.rootNode(false);
		assertTrue(result35 == 3);
		System.out.print(i++ + "\n");

		//Random rnd = new Random();
		int NUM = /*Math.abs(rnd.nextInt()) %*/ 1000;
		for (int z = 0; z < 2; z++) {
			Book bk;
			if (z == 0)
				bk = new Book(Book.NORMALBOOK);
			else
				bk = new Book(Book.DEEPBOOK);
			bk.openBook();
			bk.readBook();
			bk.closeBook();

			int board[][];
			int j, realVal, val;
			long f1 = 0, f2 = 0;
			for (j = 0; j < bk.getBookSize(); j += (bk.getBookSize() / NUM)) {
				for (int k = 0; k < 2; k++) {
					board = new int[7][6];
					realVal = bk.getBoard(j, board);
					if (k == 0)
						c4 = new AlphaBetaAgent(board, null);
					else
						c4 = new AlphaBetaAgent(f1, f2, null);
					val = c4.rootNode(true);
					if (val < 0)
						val = 1;

					f1 = c4.getMirroredField(ConnectFour.PLAYER1);
					f2 = c4.getMirroredField(ConnectFour.PLAYER2);
					System.out.println(val + " " + realVal);
					System.out.print(j + "\n");
					assertEquals(realVal, val);
					System.gc();
				}
			}
		}
	}

}
