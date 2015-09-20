import static org.junit.Assert.*;
import org.junit.Test;

public class BoardTest{

	@Test
	public void testPieceAt(){
		Board board = new Board(true);
		Piece a = new Piece(true, board, 3, 4, "pawn");
		board.place(a, 3, 4);
		assertEquals(a, board.pieceAt(3, 4));
		assertEquals(null, board.pieceAt(7, 7));
	}

	
	
	@Test
	public void testPlaceReplace(){
		Board board = new Board(true);
		Piece a = new Piece(true, board, 3, 4, "pawn");
		Piece b = new Piece(false, board, 3, 4, "pawn");
		board.place(a, 3, 4);
		board.place (b, 3, 4);
		assertEquals (b, board.pieceAt(3, 4));
	}

	@Test
	public void testRemove(){
		Board board = new Board(true);
		Piece a = new Piece(true, board, 3, 4, "pawn");
		board.place(a, 3, 4);		
		Piece b = board.remove(3, 4);
		assertEquals (null, board.pieceAt(3, 4));
		assertEquals(b, a);
	}

	@Test
	public void testRemoveOutOfBounds(){
		Board board = new Board(true);

		Piece a = new Piece(true, board, 4, 5, "pawn");
		board.place(a, 4, 5);		
		board.remove(8, 8);
		assertEquals (a, board.pieceAt(4, 5));
	}

	@Test
	public void testRemoveNull(){
		Board board = new Board(true);

		Piece a = new Piece(true, board, 2, 1, "pawn");
		board.place(a, 2, 1);		
		board.remove(0, 0);
		assertEquals (a, board.pieceAt(2, 1));
	}

	@Test
	public void testNewSelect(){
		Board board = new Board(true);
		Piece a = new Piece(true, board, 2, 1, "pawn");
		board.place (a, 2, 1);
		assertEquals(true, board.canSelect(2, 1));
		board.select (2, 1);

	}

	@Test
	public void testCanSelectNextTurn(){
		Board board = new Board(true);
		Piece a = new Piece(true, board, 2, 1, "pawn");
		Piece b = new Piece(false, board, 1, 1, "bomb");
		board.place (a, 2, 1);
		board.select (2, 1);
		board.select (3, 2);
		board.endTurn();
		board.place(b, 1, 1);
		assertEquals(true, board.canSelect(1, 1));

	}
	@Test
	public void testCanSelectEmtpyCell(){
		Board board = new Board(true);
		Piece a = new Piece(true, board, 2, 1, "pawn");
		board.place (a, 2, 1);
		assertEquals(true, board.canSelect(2, 1));
		board.select (2, 1);
		assertEquals(true, board.canSelect(3,2));
		
	}
	@Test
	public void testSelectPlace(){
		Board board = new Board(true);
		Piece a = new Piece(true, board, 2, 1, "pawn");
		board.place(a, 2, 1);
		board.select(2, 1);
		assertEquals(true, board.canSelect(3, 2));
		board.select(3, 2);
		assertEquals(a, board.pieceAt(3, 2));
	}
	@Test
	public void testRoyalty(){
		Board board = new Board(true);
		Piece a = new Piece (true, board, 2, 7, "shield");
		board.place(a, 2, 7);
		assertEquals(true, a.isKing());
	}

	@Test
	public void testCapture(){
		Board board = new Board(true);
		Piece a = new Piece (true, board, 0, 0, "pawn");
		Piece b = new Piece (false, board, 1, 1, "pawn");
		board.place(a, 0, 0);
		board.place(b, 1, 1);
		board.select(0,0);
		board.select(2, 2);
		assertEquals(null, board.pieceAt(1, 1));
		assertEquals(a, board.pieceAt(2, 2));
	}
	
	@Test
	public void testWinnerFire(){
		Board board = new Board(true);
		Piece a = new Piece (true, board, 0, 0, "pawn");
		board.place(a, 0, 0);
		assertEquals("Fire", board.winner());

	}

	@Test
	public void testWinnerNoone(){
		Board board = new Board(true);
		assertEquals("No one", board.winner());
	}

	@Test
	public void testWinnerWater(){
		Board board = new Board(true);
		Piece a = new Piece (false, board, 0, 0, "pawn");
		board.place(a, 0, 0);
		assertEquals("Water", board.winner());
	}

	@Test
	public void testKingMovement(){
		Board board = new Board(true);
		Piece a = new Piece (true, board, 2, 7, "shield");
		board.place(a, 2, 7);
		assertEquals(true, a.isKing());
		board.select(2, 7);
		assertEquals(true, board.canSelect(3, 6));
		assertEquals(true, board.canSelect(1, 6));
		assertEquals(false, board.canSelect(1, 8));

	}

	@Test
	public void testKingCapture(){
		Board board = new Board(true);
		Piece a = new Piece (true, board, 2, 7, "shield");
		Piece b = new Piece (false, board, 1, 6, "pawn");
		board.place(a, 2, 7);
		board.place(b, 1, 6);
		board.select(2, 7);
		assertEquals(true, board.canSelect(0, 5));
		board.select(0, 5);
		assertEquals(null, board.pieceAt(1, 6));
		assertEquals(a, board.pieceAt(0, 5));
	}

	@Test
	public void testChainMove(){
		Board board = new Board(true);
		Piece a = new Piece (true, board, 0, 0, "shield");
		Piece b = new Piece (false, board, 1, 1, "pawn");
		Piece c = new Piece (false, board, 3, 3, "pawn");
		board.place(a, 0, 0);
		board.place(b, 1, 1);
		board.place(c, 3, 3);
		board.select(0,0);
		assertEquals(true, board.canSelect(2, 2));
		board.select(2, 2);
		assertEquals(true, board.canSelect(4, 4));
		assertEquals(false, board.canSelect(1, 3));
		board.select(4, 4);
		for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
            	assertEquals(false, board.canSelect(i,j));     			
            }
        }
		assertEquals(a, board.pieceAt(4,4));
	}

	@Test
	public void testBombCapture(){
		Board board = new Board(true);
		Piece a = new Piece (true, board, 6, 6, "bomb");
		Piece b = new Piece (false, board, 7, 7, "bomb");
		board.place(a, 6, 6);
		board.place(b, 7, 7);
		board.select(6, 6);
		assertEquals(false, board.canSelect(8, 8));
	}
	@Test
	public void testBombCaptureAfter(){
		Board board = new Board(true);
		Piece a = new Piece (true, board, 0, 0, "bomb");
		Piece b = new Piece (false, board, 1, 1, "bomb");
		Piece c = new Piece (true, board, 2,1, "pawn");
		board.place(a, 0, 0);
		board.place(b, 1, 1);
		board.place(c, 2, 1);
		board.place(new Piece(false, board, 3, 1, "pawn"), 3, 1);
		board.place(new Piece(false, board, 1, 2, "bomb"), 1, 2);
		board.place(new Piece(true, board, 3, 2, "bomb" ), 3, 2);
		board.place(new Piece(true, board, 1, 3, "pawn"), 1, 3);
		board.place(new Piece(false, board, 2, 3, "pawn"), 2, 3);
		board.place(new Piece(true, board, 3, 3, "bomb"), 3, 3);
		board.select(0, 0);
		assertEquals(false, board.canSelect(0,1));
		board.select(2, 2);
		board.endTurn();
		for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
              	assertEquals(null, board.pieceAt(i, j));
            	assertEquals(false, board.canSelect(i,j));     			
            }
        }
	}

	@Test
	public void testMovement(){
		Board board = new Board(true);
		Piece a = new Piece (true, board, 3, 3, "shield");
		board.place(a, 3, 3);

		board.select(3, 3);
		assertEquals(false, board.canSelect(5, 5));
		assertEquals(false, board.canSelect(1, 5));
		assertEquals(false, board.canSelect(5, 1));
		assertEquals(false, board.canSelect(1, 1));
	}

	@Test
	public void testEndTurn(){
		Board board = new Board(true);
		Piece a = new Piece (true, board, 0, 0, "shield");
		Piece c = new Piece (false, board, 3, 3, "pawn");
		board.place(a, 0, 0);
		board.place(c, 3, 3);
		board.select(0, 0);
		board.select(1, 1);
		board.endTurn();
		assertEquals(true, board.canSelect(3, 3));


	}
    @Test
    public void testValidMoveKing() {
        Board board = new Board(true);
        int x = 0, y = 6;
        Piece piece = new Piece(true, board, x, y, "Pawn");
        board.place(piece, x, y);
        board.select(x, y);
        piece.move(x + 1, y + 1);
        board.endTurn();
        board.endTurn();
        board.select(x + 1, y + 1);
        assertEquals(true, board.canSelect(x, y));
    }

    @Test
    public void testAfterCapture(){
    	Board board = new Board(true);
    	Piece a = new Piece(true, board, 1, 1, "Shield");
    	Piece b = new Piece (false, board, 2, 2, "Pawn");
    	board.place(a, 1, 1);
    	board.place(b, 2, 2);
    	board.select(1, 1);
    	board.select(3, 3);
    	for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
            	assertEquals(false, board.canSelect(i,j));     			
            }
        }
    }

    @Test
    public void endTurnHasCaptured(){
    	Board board = new Board(true);
    	Piece a = new Piece(true, board, 1, 1, "Shield");
    	Piece b = new Piece (false, board, 2, 2, "Pawn");
    	board.place(a, 1, 1);
    	board.place(b, 2, 2);
    	board.select(1, 1);
    	board.select(3, 3);
    	board.endTurn();
    	assertEquals(false, a.hasCaptured());

    }
	public static void main(String... args) {
        jh61b.junit.textui.runClasses(BoardTest.class);
    }
}