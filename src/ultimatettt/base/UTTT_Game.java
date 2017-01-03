package ultimatettt.base;

import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.*;;

@SuppressWarnings("serial")
public class UTTT_Game extends JFrame {
	
	public UTTT_Game(){
		initUI();
	}
	
	private Board[][] boards = new Board[3][3];
	
	// current player
	private int player = 1;
	public int getPlayer(){
		return this.player;
	}
	
	public void changePlayer(){
		if (player == 1) {
			player++;
			if (players == 1) AImove();
		}
		else player = 1;
	}
	
	// 1 vs AI or 2P
	public int players;
	
	// Detect if a player won the game (or the game ended in a tie) after completing a board.
	public void checkWin(){
		int owner = this.boards[0][0].getOwner();
		if (owner > 0 && owner == this.boards[0][1].getOwner() && owner == this.boards[0][2].getOwner()) this.setWinner(owner);
		else if (owner > 0 && owner == this.boards[1][0].getOwner() && owner == this.boards[2][0].getOwner()) this.setWinner(owner);
		else if (owner > 0 && owner == this.boards[1][1].getOwner() && owner == this.boards[2][2].getOwner()) this.setWinner(owner);
		else{
			owner = this.boards[1][0].getOwner();
			if (owner > 0 && owner == this.boards[1][1].getOwner() && owner == this.boards[1][2].getOwner()) this.setWinner(owner);
			else{
				owner = this.boards[2][0].getOwner();
				if (owner > 0 && owner == this.boards[2][1].getOwner() && owner == this.boards[2][2].getOwner()) this.setWinner(owner);
				else if (owner > 0 && owner == this.boards[1][1].getOwner() && owner == this.boards[0][2].getOwner()) this.setWinner(owner);
				else{
					owner = this.boards[0][1].getOwner();
					if (owner > 0 && owner == this.boards[1][1].getOwner() && owner == this.boards[2][1].getOwner()) this.setWinner(owner);
					else {
						owner = this.boards[0][2].getOwner();
						if (owner > 0 && owner == this.boards[1][2].getOwner() && owner == this.boards[2][2].getOwner()) this.setWinner(owner);
					}
				}
			}
		}
		// If all boards are full with no winner, the game ends in a tie.
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				if (this.boards[i][j].full < 9 && this.boards[i][j].getOwner() == 0) return;
			}
		}
		this.setWinner(0);
	}
	
	// Ask for restart.
	public void setWinner(int owner){
		int result = -1;
		if (owner == 0) result = JOptionPane.showConfirmDialog(this, "Tie game! Would you like to play again?", "Play Again?", JOptionPane.YES_NO_OPTION);
		else if (players == 1){
			if (owner == 1) result = JOptionPane.showConfirmDialog(this, "You win! Would you like to play again?", "Play Again?", JOptionPane.YES_NO_OPTION);
			else result = JOptionPane.showConfirmDialog(this, "The AI wins. Would you like to play again?", "Play Again?", JOptionPane.YES_NO_OPTION);
		}
		else result = JOptionPane.showConfirmDialog(this, "Player " + owner + " wins! Would you like to play again?", "Play Again?", JOptionPane.YES_NO_OPTION);
		if (result == 0) resetBoards();
		else System.exit(0);
	}

	// Reset game state.
	public void resetBoards(){
		this.player = 0;
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				boards[i][j].resetBoard();
			}
		}
		// TODO: Allow switch between AI and PvP here?
	}
	
	// Set which board(s) are playable for the next player.
	public void setPlayables(int x, int y){
		// All non-owned, non-full boards are playable if sent to an owned or full board.
		if (boards[x][y].getOwner() > 0 || boards[x][y].full == 9){	
			for (int i = 0; i < 3; i++){
				for (int j = 0; j < 3; j++){
					if (boards[i][j].getOwner() > 0 || boards[i][j].full == 9) boards[i][j].playable = false;
					else{
						boards[i][j].playable = true;
						boards[i][j].setBackground(new Color(0,150,0));
					}
				}
			}
		}
		// Otherwise, target board is only playable board.
		else {
			for (int i = 0; i < 3; i++){
				for (int j = 0; j < 3; j++){
					if (i == x && j == y){
						boards[i][j].playable = true;
						boards[i][j].setBackground(new Color(0,150,0));
					}
					else{
						boards[i][j].playable = false;
						if (boards[i][j].getOwner() == 0) boards[i][j].setBackground(Color.BLACK);
					}
				}
			}
		}
	}
	
	// The AI lives here.
	public void AImove(){
		move: for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++){
				if (boards[i][j].playable) {
					for (int x = 0; x < 3; x++){
						for (int y = 0; y < 3; y++){
							if (this.boards[i][j].play(player, x, y)){
								// if move is valid, perform move and break to player change
								setPlayables(x, y);
								break move; // not great
							}
						}
					}
				}
			}
		}
		// return move to human player
		changePlayer();
	}
	
	private void initUI(){
		// Initialize frame, layout
		setTitle("Ultimate Tic Tac Toe");
		Container contentPane = getContentPane();
		setSize(750,750);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		GridLayout layout = new GridLayout(3,3,5,5);
		contentPane.setLayout(layout);
		// Build boards
		for (int i = 0; i < 3; i++){
			for (int j = 0; j < 3; j++) {
				Board board = new Board(this);
				boards[i][j] = board;
				contentPane.add(board);
			}
		}
		String[] options = new String[] {"1", "2"};
		players = JOptionPane.showOptionDialog(this, "Select Player Count:", "Fear the AI", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]) + 1;
		// TODO: AI difficulty options
	}
	
	public static void main(String args[]){
        EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                UTTT_Game frame = new UTTT_Game();
                frame.setVisible(true);
            }
        });
	}
}
