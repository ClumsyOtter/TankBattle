package tankWar;

import java.awt.Color;
import java.awt.Graphics;

public class Explode {
	int x,y;	//±¬Õ¨µÄÎ»ÖÃ
	TankClient tc;
	int[] diameter = {1,1,1,1,1,1,1,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40,40};
	int step = 0;
	private boolean live = true;
	public Explode(int x, int y, TankClient tc) {
		this.x = x;
		this.y = y;
		this.tc = tc;
	}

	public void draw(Graphics g){
		if(!live){
			tc.explodes.remove(this);
			return;
		}
		if(step == diameter.length){
			live = false; 
			step = 0;
			return;
		}
		Color c = g.getColor();
		g.setColor(Color.MAGENTA);
		g.fillOval(x, y, diameter[step], diameter[step]);
		g.setColor(c);
		step++;
	}	
}
