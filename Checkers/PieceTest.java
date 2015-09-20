import static org.junit.Assert.*;
import org.junit.Test;

public class PieceTest{
	@Test
	public void testPiece(){
		Board board= new Board(true);
		Piece a = new Piece (true, board, 3, 8, "pawn");
		assertEquals(3, a.pos_x);
		assertEquals(8, a.pos_y);
	}

	@Test
	public void testisFire(){
		Board board= new Board(true);
		Piece a = new Piece (true, board, 2, 5, "pawn");
		assertEquals(true, a.isFire());
	}

	@Test
	public void testisFire1(){
		Board board= new Board(true);
		Piece a = new Piece (false, board, 2, 5, "pawn");
		assertEquals(false, a.isFire());
	}

	@Test
	public void tesstisKing(){
		Board board= new Board(true);
		Piece a = new Piece (true, board, 1, 3, "king");
		assertEquals(true, a.isKing());

	}

	@Test
	public void testisBomb(){
		Board board= new Board(true);
		Piece a = new Piece (true, board, 1, 2, "bomb");
		assertEquals(true, a.isBomb());
	}

	@Test
	public void testShield(){
		Board board= new Board(true);
		Piece a = new Piece (true, board, 3, 1, "shield");
		assertEquals(true, a.isShield());
	}

	@Test
	public void testHasCaptured(){
		Board board= new Board(true);
		Piece a = new Piece (true, board, 3, 1, "pawn");
		assertEquals(false, a.hasCaptured());

	}

	@Test
	public void testDoneCapturing(){
		Board board= new Board(true);
		Piece a = new Piece(true, board, 3, 1, "pawn");
		a.doneCapturing();
		assertEquals(false, a.hasCaptured());
	}

	@Test
	public void testSide(){
		Board board= new Board(true);
		Piece a = new Piece(true, board, 3, 1, "pawn");
		assertEquals(0, a.side());
	}



	
	public static void main(String... args) {
        jh61b.junit.textui.runClasses(PieceTest.class);
    }
}