package openingBook;

import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Test;

public class BookTest {

	Book bk;
	public void setUp() throws Exception {
		
	}

	@Test
	public void testGetValue() throws IOException {
		bk = new Book(Book.NORMALBOOK);
		bk.openBook();
		bk.readBook();
		bk.closeBook();
		
		byte expected1 = 1;
		int result1 = bk.getValue(1226729 & 0xFFFFFC, 1226729 & 0xFFFFFC);
		assertEquals(expected1, result1);
		
		byte expected2 = 2;
		int result2 = bk.getValue(1403134 & 0xFFFFFC, 1403134 & 0xFFFFFC);
		assertEquals(expected2, result2);
		
		byte expected3 = 1;
		int result3 = bk.getValue(1726441 & 0xFFFFFC, 1726441 & 0xFFFFFC);
		assertEquals(expected3, result3);
		
		byte expected4 = 2;
		int result4 = bk.getValue(2481586 & 0xFFFFFC, 2481586 & 0xFFFFFC);
		assertEquals(expected4, result4);
		
		byte expected5 = 2;
		int result5 = bk.getValue(0x2EF15A & 0xFFFFFC, 0x2EF15A & 0xFFFFFC);
		assertEquals(expected5, result5);
		
		byte expected6 = 2;
		int result6 = bk.getValue(0xB6BA32 & 0xFFFFFC, 0xB6BA32 & 0xFFFFFC);
		assertEquals(expected6, result6);
		
	}
	
	@Test
	public void testGetBoard() throws IOException {
		bk = new Book(0);
		bk.openBook();
		bk.readBook();
		bk.closeBook();
		
		//0 0 0 0 0 10 10 10 11 10 0 11 11 11 00
		//value: 0
		//0 0 0 0 0 0 0
		//0 0 0 0 0 1 0
		//0 0 0 0 0 2 0
		//0 0 0 0 0 1 2
		//0 0 0 0 0 1 2
		//0 0 0 0 0 1 2 
		int board[][] = new int[7][6];
		int val = bk.getBoard(0, board);
		assertEquals(0, val);
		assertEquals(board[5][0],1);
		assertEquals(board[5][1],1);
		assertEquals(board[5][2],1);
		assertEquals(board[5][3],2);
		assertEquals(board[5][4],1);
		assertEquals(board[6][0],2);
		assertEquals(board[6][1],2);
		assertEquals(board[6][2],2);
		
		/*
		//0 0 0 0 0 10 10 10 11 10 0 11 11 11 00
		//value: 0
		//0 0 0 0 0 0 0
		//0 0 0 0 0 0 0
		//0 0 0 0 0 0 0
		//0 0 0 0 0 0 0
		//0 0 0 0 0 0 0
		//0 0 0 0 0 0 0 
		board = new int[7][6];
		val = bk.getBoard(1000, board);
		assertEquals(0, val);
		assertEquals(board[][],);
		assertEquals(board[][],);
		assertEquals(board[][],);
		assertEquals(board[][],);
		assertEquals(board[][],);
		assertEquals(board[][],);
		assertEquals(board[][],);
		assertEquals(board[][],);
		*/
		
		//0 0 10 0 11 11 10 11 0 11 0 10 0 10 00
		//value: 0
		//0 0 0 0 0 0 0
		//0 0 0 0 0 0 0
		//0 0 0 2 0 0 0
		//0 0 0 1 0 0 0
		//0 0 0 2 0 0 0
		//0 0 1 2 2 1 1 
		board = new int[7][6];
		val = bk.getBoard(1000, board);
		assertEquals(0, val);
		assertEquals(board[2][0], 1);
		assertEquals(board[3][0],2);
		assertEquals(board[3][1],2);
		assertEquals(board[3][2],1);
		assertEquals(board[3][3],2);
		assertEquals(board[4][0],2);
		assertEquals(board[5][0],1);
		assertEquals(board[6][0],1);
		
		//0 0 11 10 0 10 10 11 0 0 10 11 0 11 01
		//value: 0
		//0 0 0 0 0 0 0
		//0 0 0 0 0 0 0
		//0 0 0 0 0 0 0
		//0 0 0 2 0 0 0
		//0 0 1 1 0 2 0
		//0 0 2 1 0 1 2 
		board = new int[7][6];
		val = bk.getBoard(3000, board);
		assertEquals(1, val);
		assertEquals(board[2][0],2);
		assertEquals(board[2][1],1);
		assertEquals(board[3][0],1);
		assertEquals(board[3][1],1);
		assertEquals(board[3][2],2);
		assertEquals(board[5][0],1);
		assertEquals(board[5][1],2);
		assertEquals(board[6][0],2);
		
		//0 0 10 0 11 11 10 11 0 11 0 10 0 10 00
		//value: 0
		//0 0 0 0 0 0 0
		//0 0 0 0 0 0 0
		//0 0 0 2 0 0 0
		//0 0 0 1 0 0 0
		//0 0 0 2 0 0 0
		//0 0 1 2 2 1 1 
		board = new int[7][6];
		val = bk.getBoard(1000, board);
		assertEquals(0, val);
		assertEquals(board[2][0],1);
		assertEquals(board[3][0],2);
		assertEquals(board[3][1],2);
		assertEquals(board[3][2],1);
		assertEquals(board[3][3],2);
		assertEquals(board[4][0],2);
		assertEquals(board[5][0],1);
		assertEquals(board[6][0],1);
	}
	
	public void testBookDeep() {
		
	}
}
