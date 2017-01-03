package ultimatettt.base;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class Board extends JPanel {

	private UTTT_Game game;
	private int owner;
	public boolean playable = true;
	private Color[] colours = {new Color(191,0,0),  new Color(0,127,255)};
	public Square[][] squares = new Square[3][3];
	public int full = 0; // full squares

	public Board(UTTT_Game game){
		this.game = game;
		// Build squares
		setLayout(new GridLayout(3,3,2,2));
		setBackground(Color.BLACK);
		Border border = new EmptyBorder(5,5,5,5);
		setBorder(border);
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				Square square = new Square(this, game, i, j);
				squares[i][j] = square;
				add(square);
			}
		}
	}
	
	public int getOwner(){
		return this.owner;
	}
	
	// Make a play on a square.
	public boolean play(int owner, int x, int y){
		if (this.squares[x][y].getOwner() > 0 || this.getOwner() > 0) return false;
		this.squares[x][y].setOwner(owner);
		checkSquares();
		this.full++;
		return true;
	}
	
	// Check if a board has been won by a player.
	private void checkSquares(){
		int owner = this.squares[0][0].getOwner();
		if (owner > 0 && owner == this.squares[0][1].getOwner() && owner == this.squares[0][2].getOwner()) this.setOwner(owner);
		else if (owner > 0 && owner == this.squares[1][0].getOwner() && owner == this.squares[2][0].getOwner()) this.setOwner(owner);
		else if (owner > 0 && owner == this.squares[1][1].getOwner() && owner == this.squares[2][2].getOwner()) this.setOwner(owner);
		else{
			owner = this.squares[1][0].getOwner();
			if (owner > 0 && owner == this.squares[1][1].getOwner() && owner == this.squares[1][2].getOwner()) this.setOwner(owner);
			else{
				owner = this.squares[2][0].getOwner();
				if (owner > 0 && owner == this.squares[2][1].getOwner() && owner == this.squares[2][2].getOwner()) this.setOwner(owner);
				else if (owner > 0 && owner == this.squares[1][1].getOwner() && owner == this.squares[0][2].getOwner()) this.setOwner(owner);
				else{
					owner = this.squares[0][1].getOwner();
					if (owner > 0 && owner == this.squares[1][1].getOwner() && owner == this.squares[2][1].getOwner()) this.setOwner(owner);
					else {
						owner = this.squares[0][2].getOwner();
						if (owner > 0 && owner == this.squares[1][2].getOwner() && owner == this.squares[2][2].getOwner()) this.setOwner(owner);
					}
				}
			}
		}
		if (this.getOwner() > 0) this.game.checkWin();
	}
	
	private void setOwner(int owner){
		this.owner = owner;
		this.setBackground(colours[owner-1]);
	}
	
	void resetBoard(){
		this.full = 0;
		this.playable = true;
		this.owner = 0;
		this.setBackground(Color.BLACK);
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				this.squares[i][j].reset();
			}
		}
	}
}
