public class Piece{
	
	private boolean isFire;
	private Board playing_board;
	private int pos_x;
	private int pos_y;
	private String variant;
	private boolean captured;
	private boolean isKing;
	/*
	* Creates a constructor for a piecce
	*/
	public Piece(boolean isFire, Board b, int x, int y, String type){
		this.isFire = isFire;
		playing_board = b;
		pos_x = x;
		pos_y = y;
		variant = type;
		captured = false;
		isKing = false;

	}

	/*
	*  Returns true if the piece is of sid_type fire
	*/
	public boolean isFire(){
		if (isFire == true){
			return true;
		}
		return false;
	}

	/*
	* Retruns which side's turn it is
	*/
	public int side(){
		if(isFire()){
			return 0;
		}
		return 1;
	}
	/*
	*  Returns true if the piece is of King variant
	*/
	public boolean isKing(){
		if(isFire && pos_y== 7){
			isKing= true;
		}
		else if(!isFire && pos_y == 0){
			isKing = true;
		}
		if (isKing){
			return true;
		}
		return false;
	}

	/*
	* Returns true if the piece is of Bomb variant
	*/
	public boolean isBomb(){
		if (variant.equals("bomb")){
			return true;
		}
		return false;
	}

	/*
	* Returns true if the piece is of Shield variant
	*/
	public boolean isShield(){
		if (variant.equals("shield")){
			return true;
		}
		return false;
	}
	/*
	* Returns true if the piece has captured any other pieces
	*/
	public boolean hasCaptured(){
		if (captured == true){
			return true;
		}
		return false;
	}

	/*
	* Makes the hasCaptured value go to false at the end of the turn
	*/
	public void doneCapturing(){
		captured = false;
	}

	private void explode(int x, int y){
		for (int i = x-1; i <= x + 1 ; i++) {
            for (int j = y-1; j <= y +1; j++) {
           		if(playing_board.pieceAt(i, j)!=null && !playing_board.pieceAt(i,j).isShield()){
           			playing_board.remove(i, j);
           		}
            }
        }


	}
	public void move(int x, int y){
		if (Math.abs(x -pos_x)==2 && Math.abs(y- pos_y)==2){
			playing_board.remove((pos_x+ x)/2, (pos_y + y)/ 2);
			if (this.isBomb()){
				this.explode(x, y);
			}
			captured = true;		
		}
		pos_x=x;
		pos_y=y;
	}


}