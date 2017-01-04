package ultimatettt.base;

import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Random;

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
	private int difficulty = 1;
	public Square lastmove = null;
	
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
		// If all boards are full or owned with no winner, the game ends in a tie.
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
		lastmove = null;
		setOptions();
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
	
	/// BEGIN AI ///
	
	private void AImove(){
		if (difficulty == 1) moveEasy();
		else if (difficulty == 2) moveMedium();
		else moveHard();
		// return move to human player
		changePlayer();
	}
	
	private void moveEasy(){
		// Difficulty 1, literally moves randomly until success. Not great.
		for (;;) {
			int i = randInt(0, 2);
			int j = randInt(0, 2);
			if (boards[i][j].playable) {
				int x = randInt(0, 2);
				int y = randInt(0, 2);
				// if move is valid, perform move and break to player change
				if (this.boards[i][j].play(player, x, y)) {
					if (getPlayer() > 0) setPlayables(x, y);
					break;
				}
				
			}
		}
	}
	
	private void moveMedium(){
		// Difficulty 2, actually tries. Doesn't have a great grasp of macro strategy.
		// Priority: win game, prevent loss on playable boards, win board, block board, fallback to moveEasy
		move: for (;;) {
			// if there is a board that can be played on and won, do that
			for (int i = 0; i < 9; i++) {
				if (boards[i/3][i%3].playable && gameWinnable(i, 2)){
					int target = boardWinnable(i, 2);
					if (target > -1){
						if (this.boards[i/3][i%3].play(player, target/3, target%3)) {
							if (getPlayer() > 0) setPlayables(target/3, target%3);
							break move;
						}
					}
				}
			}
			// block opponent's win
			for (int i = 0; i < 9; i++) {
				if (boards[i/3][i%3].playable && gameWinnable(i, 1)){
					int target = boardWinnable(i, 1);
					if (target > -1){
						if (this.boards[i/3][i%3].play(player, target/3, target%3)) {
							if (getPlayer() > 0) setPlayables(target/3, target%3);
							break move;
						}
					}
				}
			}
			// simply try to win a board
			for (int i = 0; i < 9; i++){
				if (!boards[i/3][i%3].playable) continue;
				int target = boardWinnable(i, 2);
				if (target > -1){
					if (this.boards[i/3][i%3].play(player, target/3, target%3)) {
						if (getPlayer() > 0) setPlayables(target/3, target%3);
						break move;
					}
				}
			}
			// try to block a won board for opponent
			for (int i = 0; i < 9; i++){
				if (!boards[i/3][i%3].playable) continue;
				int target = boardWinnable(i, 1);
				if (target > -1){
					if (this.boards[i/3][i%3].play(player, target/3, target%3)) {
						if (getPlayer() > 0) setPlayables(target/3, target%3);
						break move;
					}
				}
			}
			// failing any of these, play randomly.
			moveEasy();
			break move;
		}
	}
	
	private void moveHard(){
		// Difficulty 3, has some semblance of a plan to win the game.
		// Priority system largely inherited from medium, with emphasis on avoiding moves that
		// target certain boards in a hierarchy
		// if no move is found that doesn't violate loss constraints, builds a set of playable squares
		// and progressively eliminates options until it chooses randomly from a minimal set

		move: for (;;) {
			// if there is a board that can be played on and won, do that
			for (int i = 0; i < 9; i++) {
				if (boards[i/3][i%3].playable && gameWinnable(i, 2)){
					int target = boardWinnable(i, 2);
					if (target > -1){
						if (this.boards[i/3][i%3].play(player, target/3, target%3)) {
							if (getPlayer() > 0) setPlayables(target/3, target%3);
							break move;
						}
					}
				}
			}
			// find boards to avoid since they're game-winnable
			ArrayList<Integer> ogwBoards = new ArrayList<Integer>(9); // player
			ArrayList<Integer> gwBoards = new ArrayList<Integer>(9); // AI
			for (int i = 0; i < 9; i++) {
				if (gameWinnable(i, 1)) ogwBoards.add(i);
				if (gameWinnable(i, 2)) gwBoards.add(i);
			}
			// find boards that can be won in one move
			ArrayList<Integer> owBoards = new ArrayList<Integer>(9);
			ArrayList<Integer> wBoards = new ArrayList<Integer>(9);
			for (int i = 0; i < 9; i++) {
				if (boardWinnable(i, 1) > -1) owBoards.add(i);
				if (boardWinnable(i, 2) > -1) wBoards.add(i);
			}
			// the intersection of these should be absolutely avoided as they lead directly to a loss (or blocked win)
			ArrayList<Integer> lossBoards = new ArrayList<Integer>(9);
			ArrayList<Integer> winBoards = new ArrayList<Integer>(9);
			for (int i = 0; i < 9; i++) {
				if (ogwBoards.contains(i) && owBoards.contains(i)) lossBoards.add(i);
				if (gwBoards.contains(i) && wBoards.contains(i)) winBoards.add(i);
			}
			// avoidance priority: loss, win, w, ow, gw, ogw
			
			// win a board over low-priority moves
			for (int i = 0; i < 9; i++){
				if (!boards[i/3][i%3].playable) continue;
				int target = boardWinnable(i, 2);
				if (target > -1 && !lossBoards.contains(target) && !winBoards.contains(target)){
					// also avoid allowing opponent to play openly if that leads directly to a loss
					if ((boards[target/3][target%3].getOwner() > 0 || boards[target/3][target%3].full == 9) && !lossBoards.isEmpty()) continue;
					if (this.boards[i/3][i%3].play(player, target/3, target%3)) {
						if (getPlayer() > 0) setPlayables(target/3, target%3);
						break move;
					}
				}
			}
			
			// block a won board unless that loses the game
			for (int i = 0; i < 9; i++){
				if (!boards[i/3][i%3].playable) continue;
				int target = boardWinnable(i, 1);
				if (target > -1){
					// allow the play if it's blocking the current winnable board
					// TODO: ignores fork possibilities, fix under D4
					if (lossBoards.contains(target) && target != i) continue;
					// also avoid allowing opponent to play openly if that leads directly to a loss
					if ((boards[target/3][target%3].getOwner() > 0 || boards[target/3][target%3].full == 9) && !lossBoards.isEmpty()) continue;
					if (this.boards[i/3][i%3].play(player, target/3, target%3)) {
						if (getPlayer() > 0) setPlayables(target/3, target%3);
						break move;
					}
				}
			}
			
			// track playable squares
			ArrayList<Square> playables = new ArrayList<Square>(81);
			for (int i = 0; i < 9; i++){
				if (!boards[i/3][i%3].playable) continue;
				for (int j = 0; j < 9; j++){
					Square square = boards[i/3][i%3].squares[j/3][j%3];
					if (square.getOwner() == 0) playables.add(square);
				}
			}
			// elimination set
			ArrayList<Square> elims = new ArrayList<Square>(81);
			// begin eliminating with avoidance hierarchy: loss, win, w, ow, gw, ogw
			// TODO: Condense this after testing it, you monkey. The play on 1-size should be a function.
			for (int i = 0; i < playables.size(); i++){
				Square square = playables.get(i);
				if (lossBoards.contains(square.getXC() * 3 + square.getYC())) elims.add(square);
			}
			playables = pickSquare(playables, elims);
			if (playables.size() == 1){
				Square square = playables.get(0);
				Board board = square.getBoard();
				if (board.play(player, square.getXC(), square.getYC())) {
					if (getPlayer() > 0) setPlayables(square.getXC(), square.getYC());
					break move;
				}
				else JOptionPane.showMessageDialog(this, "OOPS", "The programmer is a monkey.", JOptionPane.WARNING_MESSAGE);
			}
			elims.clear();
			// avoid game win boards
			for (int i = 0; i < playables.size(); i++){
				Square square = playables.get(i);
				if (winBoards.contains(square.getXC() * 3 + square.getYC())) elims.add(square);
			}			
			playables = pickSquare(playables, elims);
			if (playables.size() == 1){
				Square square = playables.get(0);
				Board board = square.getBoard();
				if (board.play(player, square.getXC(), square.getYC())) {
					if (getPlayer() > 0) setPlayables(square.getXC(), square.getYC());
					break move;
				}
				else JOptionPane.showMessageDialog(this, "OOPS", "The programmer is a monkey.", JOptionPane.WARNING_MESSAGE);
			}
			elims.clear();
			// avoid full/owned boards
			for (int i = 0; i < playables.size(); i++){
				Square square = playables.get(i);
				if (boards[square.getXC()][square.getYC()].getOwner() > 0 || boards[square.getXC()][square.getYC()].full == 9) elims.add(square);
			}			
			playables = pickSquare(playables, elims);
			if (playables.size() == 1){
				Square square = playables.get(0);
				Board board = square.getBoard();
				if (board.play(player, square.getXC(), square.getYC())) {
					if (getPlayer() > 0) setPlayables(square.getXC(), square.getYC());
					break move;
				}
				else JOptionPane.showMessageDialog(this, "OOPS", "The programmer is a monkey.", JOptionPane.WARNING_MESSAGE);
			}
			elims.clear();
			// avoid w boards
			for (int i = 0; i < playables.size(); i++){
				Square square = playables.get(i);
				if (wBoards.contains(square.getXC() * 3 + square.getYC())) elims.add(square);
			}
			playables = pickSquare(playables, elims);
			if (playables.size() == 1){
				Square square = playables.get(0);
				Board board = square.getBoard();
				if (board.play(player, square.getXC(), square.getYC())) {
					if (getPlayer() > 0) setPlayables(square.getXC(), square.getYC());
					break move;
				}
				else JOptionPane.showMessageDialog(this, "OOPS", "The programmer is a monkey.", JOptionPane.WARNING_MESSAGE);
			}
			elims.clear();
			// avoid ow boards
			for (int i = 0; i < playables.size(); i++){
				Square square = playables.get(i);
				if (owBoards.contains(square.getXC() * 3 + square.getYC())) elims.add(square);
			}
			playables = pickSquare(playables, elims);
			if (playables.size() == 1){
				Square square = playables.get(0);
				Board board = square.getBoard();
				if (board.play(player, square.getXC(), square.getYC())) {
					if (getPlayer() > 0) setPlayables(square.getXC(), square.getYC());
					break move;
				}
				else JOptionPane.showMessageDialog(this, "OOPS", "The programmer is a monkey.", JOptionPane.WARNING_MESSAGE);
			}
			elims.clear();
			// avoid gw boards
			for (int i = 0; i < playables.size(); i++){
				Square square = playables.get(i);
				if (gwBoards.contains(square.getXC() * 3 + square.getYC())) elims.add(square);
			}
			playables = pickSquare(playables, elims);
			if (playables.size() == 1){
				Square square = playables.get(0);
				Board board = square.getBoard();
				if (board.play(player, square.getXC(), square.getYC())) {
					if (getPlayer() > 0) setPlayables(square.getXC(), square.getYC());
					break move;
				}
				else JOptionPane.showMessageDialog(this, "OOPS", "The programmer is a monkey.", JOptionPane.WARNING_MESSAGE);
			}
			elims.clear();
			// avoid ogw boards
			for (int i = 0; i < playables.size(); i++){
				Square square = playables.get(i);
				if (ogwBoards.contains(square.getXC() * 3 + square.getYC())) elims.add(square);
			}
			playables = pickSquare(playables, elims);
			if (playables.size() == 1){
				Square square = playables.get(0);
				Board board = square.getBoard();
				if (board.play(player, square.getXC(), square.getYC())) {
					if (getPlayer() > 0) setPlayables(square.getXC(), square.getYC());
					break move;
				}
				else JOptionPane.showMessageDialog(this, "OOPS", "The programmer is a monkey.", JOptionPane.WARNING_MESSAGE);
			}
			elims.clear();
			// then send to fewest opponent squares taken
			int redmax = 0;
			for (int i = 0; i < playables.size(); i++){
				Square square = playables.get(i);
				Board board = boards[square.getXC()][square.getYC()];
				if (board.reds > redmax){
					redmax = board.reds;
					elims.clear();
					elims.add(square);
				}
				else if (board.reds == redmax) elims.add(square);
			}
			// if this would eliminate all, skip it as a tiebreaker
			if (!(playables.size() == elims.size())) {
				playables = pickSquare(playables, elims);
				if (playables.size() == 1){
					Square square = playables.get(0);
					Board board = square.getBoard();
					if (board.play(player, square.getXC(), square.getYC())) {
						if (getPlayer() > 0) setPlayables(square.getXC(), square.getYC());
						break move;
					}
					else JOptionPane.showMessageDialog(this, "OOPS", "The programmer is a monkey.", JOptionPane.WARNING_MESSAGE);
				}
			}
			elims.clear();
			// send to fewest AI squares taken
			int bluemax = 0;
			for (int i = 0; i < playables.size(); i++){
				Square square = playables.get(i);
				Board board = boards[square.getXC()][square.getYC()];
				if (board.blues > bluemax){
					bluemax = board.blues;
					elims.clear();
					elims.add(square);
				}
				else if (board.blues == bluemax) elims.add(square);
			}
			// if this would eliminate all, skip it as a tiebreaker
			if (!(playables.size() == elims.size())) {
				playables = pickSquare(playables, elims);
				if (playables.size() == 1){
					Square square = playables.get(0);
					Board board = square.getBoard();
					if (board.play(player, square.getXC(), square.getYC())) {
						if (getPlayer() > 0) setPlayables(square.getXC(), square.getYC());
						break move;
					}
					else JOptionPane.showMessageDialog(this, "OOPS", "The programmer is a monkey.", JOptionPane.WARNING_MESSAGE);
				}
			}
			elims.clear();
			// if all elimination criteria are exhausted, select randomly from remaining squares
			elims = playables;
			playables = pickSquare(playables, elims);
			if (playables.size() == 1){
				Square square = playables.get(0);
				Board board = square.getBoard();
				if (board.play(player, square.getXC(), square.getYC())) {
					if (getPlayer() > 0) setPlayables(square.getXC(), square.getYC());
					break move;
				}
				else JOptionPane.showMessageDialog(this, "OOPS", "The programmer is a monkey.", JOptionPane.WARNING_MESSAGE);
			}
			// fallback that should never be hit: move randomly
			moveEasy();
			JOptionPane.showMessageDialog(this, "Hard AI Fallback", "moveEasy() triggered somehow", JOptionPane.WARNING_MESSAGE);
			break move;
		}
	}
	
	// TODO: Difficulty 4? understanding forks, micro board strategy (modifies elim criteria)
	
	/// BEGIN AI HELPERS ///
	
	// removes eliminated squares from playable list
	// if that would remove all, pick one randomly and return it
	private ArrayList<Square> pickSquare(ArrayList<Square> p, ArrayList<Square> e){
		ArrayList<Square> ret = new ArrayList<Square>(81);
		// if the two lists are identical, play at random
		if (p.size() == e.size()){
			ret.add(p.get(randInt(0,p.size()-1)));
			return ret;
		}
		// otherwise, return all options and continue
		for (int i = 0; i < p.size(); i++){
			if (!e.contains(p.get(i))) ret.add(p.get(i));
		}
		return ret;
	}
	
	// takes board from 0 to 8 to see if winning it wins the game
	private boolean gameWinnable(int board, int player){
		switch (board) {
			case 0:
				if (boards[0][1].getOwner() == player && boards[0][2].getOwner() == player) return true;
				if (boards[1][0].getOwner() == player && boards[2][0].getOwner() == player) return true;
				if (boards[1][1].getOwner() == player && boards[2][2].getOwner() == player) return true;
				break;
			case 1:
				if (boards[0][0].getOwner() == player && boards[0][2].getOwner() == player) return true;
				if (boards[1][1].getOwner() == player && boards[2][1].getOwner() == player) return true;
				break;
			case 2:
				if (boards[0][1].getOwner() == player && boards[0][0].getOwner() == player) return true;
				if (boards[1][2].getOwner() == player && boards[2][2].getOwner() == player) return true;
				if (boards[1][1].getOwner() == player && boards[2][0].getOwner() == player) return true;
				break;
			case 3:
				if (boards[0][0].getOwner() == player && boards[2][0].getOwner() == player) return true;
				if (boards[1][1].getOwner() == player && boards[1][2].getOwner() == player) return true;
				break;
			case 4:
				if (boards[0][0].getOwner() == player && boards[2][2].getOwner() == player) return true;
				if (boards[2][0].getOwner() == player && boards[0][2].getOwner() == player) return true;
				if (boards[0][1].getOwner() == player && boards[2][1].getOwner() == player) return true;
				if (boards[1][0].getOwner() == player && boards[1][2].getOwner() == player) return true;
				break;
			case 5:
				if (boards[0][2].getOwner() == player && boards[2][2].getOwner() == player) return true;
				if (boards[1][1].getOwner() == player && boards[1][0].getOwner() == player) return true;
				break;
			case 6:
				if (boards[0][0].getOwner() == player && boards[1][0].getOwner() == player) return true;
				if (boards[2][1].getOwner() == player && boards[2][2].getOwner() == player) return true;
				if (boards[1][1].getOwner() == player && boards[0][2].getOwner() == player) return true;
				break;
			case 7:
				if (boards[0][1].getOwner() == player && boards[1][1].getOwner() == player) return true;
				if (boards[2][0].getOwner() == player && boards[2][2].getOwner() == player) return true;
				break;
			case 8:
				if (boards[1][1].getOwner() == player && boards[0][0].getOwner() == player) return true;
				if (boards[1][2].getOwner() == player && boards[0][2].getOwner() == player) return true;
				if (boards[2][1].getOwner() == player && boards[2][0].getOwner() == player) return true;
				break;
			default:
				break;
		}
		return false;
	}

	// AI determining if a board is winnable. return -1 for false, 0-8 for target square
	private int boardWinnable(int board, int player){
		Board b = boards[board/3][board%3];
		int target = -1;
		for (int i = 0; i < 9; i++){
			switch (i) {
				case 0:
					if (b.squares[0][1].getOwner() == player && b.squares[0][2].getOwner() == player) target = i;
					if (b.squares[1][0].getOwner() == player && b.squares[2][0].getOwner() == player) target = i;
					if (b.squares[1][1].getOwner() == player && b.squares[2][2].getOwner() == player) target = i;
					break;
				case 1:
					if (b.squares[0][0].getOwner() == player && b.squares[0][2].getOwner() == player) target = i;
					if (b.squares[1][1].getOwner() == player && b.squares[2][1].getOwner() == player) target = i;
					break;
				case 2:
					if (b.squares[0][1].getOwner() == player && b.squares[0][0].getOwner() == player) target = i;
					if (b.squares[1][2].getOwner() == player && b.squares[2][2].getOwner() == player) target = i;
					if (b.squares[1][1].getOwner() == player && b.squares[2][0].getOwner() == player) target = i;
					break;
				case 3:
					if (b.squares[0][0].getOwner() == player && b.squares[2][0].getOwner() == player) target = i;
					if (b.squares[1][1].getOwner() == player && b.squares[1][2].getOwner() == player) target = i;
					break;
				case 4:
					if (b.squares[0][0].getOwner() == player && b.squares[2][2].getOwner() == player) target = i;
					if (b.squares[2][0].getOwner() == player && b.squares[0][2].getOwner() == player) target = i;
					if (b.squares[0][1].getOwner() == player && b.squares[2][1].getOwner() == player) target = i;
					if (b.squares[1][0].getOwner() == player && b.squares[1][2].getOwner() == player) target = i;
					break;
				case 5:
					if (b.squares[0][2].getOwner() == player && b.squares[2][2].getOwner() == player) target = i;
					if (b.squares[1][1].getOwner() == player && b.squares[1][0].getOwner() == player) target = i;
					break;
				case 6:
					if (b.squares[0][0].getOwner() == player && b.squares[1][0].getOwner() == player) target = i;
					if (b.squares[2][1].getOwner() == player && b.squares[2][2].getOwner() == player) target = i;
					if (b.squares[1][1].getOwner() == player && b.squares[0][2].getOwner() == player) target = i;
					break;
				case 7:
					if (b.squares[0][1].getOwner() == player && b.squares[1][1].getOwner() == player) target = i;
					if (b.squares[2][0].getOwner() == player && b.squares[2][2].getOwner() == player) target = i;
					break;
				case 8:
					if (b.squares[1][1].getOwner() == player && b.squares[0][0].getOwner() == player) target = i;
					if (b.squares[1][2].getOwner() == player && b.squares[0][2].getOwner() == player) target = i;
					if (b.squares[2][1].getOwner() == player && b.squares[2][0].getOwner() == player) target = i;
					break;
				default:
					break;
			}
		}
		return target;
	}
	
	public static int randInt(int min, int max){
		Random rand = new Random();
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}
	
	/// END AI, AI HELPERS ///

	private void setOptions(){
		String[] options = new String[] {"1", "2"};
		players = JOptionPane.showOptionDialog(this, "Select Player Count:", "vs AI or 2 Player?", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]) + 1;
		if (players == 1){
			String[] diff = new String[] {"Easy", "Medium", "Hard"};
			difficulty = JOptionPane.showOptionDialog(this, "Select AI Difficulty:", "Fear the AI", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, diff, diff[0]) + 1;
		}
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
		setOptions();
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
