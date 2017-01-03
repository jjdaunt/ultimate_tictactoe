package ultimatettt.base;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Square extends JPanel implements MouseListener {
	
	private int owner;
	private Color[] colours = {Color.RED, Color.BLUE, new Color(255,192,183), new Color(135,206,250), Color.PINK, Color.CYAN};
	private Board board;
	private UTTT_Game game;
	private int x;
	private int y;
	
	// Initialize square.
	Square(Board b, UTTT_Game g, int x, int y){
		this.board = b;
		this.game = g;
		this.x = x;
		this.y = y;
		setBackground(Color.LIGHT_GRAY);		
		addMouseListener(this);
		owner = 0;
	}
	
   public void mouseClicked(MouseEvent e) {
		   
   }

   // Highlight playable squares.
   public void mouseEntered(MouseEvent e) {
	   if (this.board.playable && this.getOwner() == 0){
		   if (this.game.getPlayer() == 1) setBackground(colours[2]);
		   else setBackground(colours[3]);
	   }
   }

   public void mouseExited(MouseEvent e) {
	   if (this.getBackground() == colours[2] || this.getBackground() == colours[3]) setBackground(Color.LIGHT_GRAY);
   }

   public void mousePressed(MouseEvent e) {
	   // If the play is valid, prepare next player's turn.
	   if (this.board.playable && this.board.play(this.game.getPlayer(), this.x, this.y)){
		   if (this.game.getPlayer() > 0) this.game.setPlayables(this.x, this.y);
		   this.game.changePlayer();
	   };
   }

   public void mouseReleased(MouseEvent e) {

   }
	
	public int getOwner(){
		return this.owner;
	}
	
	public void setOwner(int owner){
		this.owner = owner;
		this.setBackground(colours[owner+3]);
		if (this.game.lastmove != null) this.game.lastmove.setBackground(colours[this.game.lastmove.getOwner()-1]);
		this.game.lastmove = this;
	}
	
	void reset(){
		this.owner = 0;
		this.setBackground(Color.LIGHT_GRAY);
	}
}
