import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Enemy{
	private int x, y;
	private int dx = 0;
	private int dy = 5;
	private int state = 1;
	private int type;
	private Image small1;
	private Image small2;
	private Image small3;
	private Image big1;
	private Image big2;
	private Image big3;

	private int life;
	private Rectangle r;
	private boolean wentIn = false;

	public Enemy(int x, int y, int type){
		this.x = x;
		this.y = y;
		this.type = type;

		if(type == 1)
			r = new Rectangle(x,y,60,60);
		else
			r = new Rectangle(x,y,90,90);

		if(type == 1)
			life = 3;
		else if(type == 2)
			life = 8;

		try
		{
			small1 = ImageIO.read(new File("images/rocket1.png"));
			small2 = ImageIO.read(new File("images/rocket2.png"));
			small3 = ImageIO.read(new File("images/rocket3.png"));
			big1 = ImageIO.read(new File("images/bigrocket1.png"));
			big2 = ImageIO.read(new File("images/bigrocket2.png"));
			big3 = ImageIO.read(new File("images/bigrocket3.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void update(){
		x+=dx;
		y+=dy;
		state+=1;
		state = state%3 + 1;

		if(type == 1)
			r.setRect(x,y,60,60);
		else
			r.setRect(x,y,90,90);
	}
	
	public boolean draw(Graphics g)
	{
		if(life <= 0)
			return true;
		
		if(y>=700)
		{
			wentIn = true;
			return true;
		}

		if(state == 1)
		{
			if(type == 1)
				g.drawImage(small1,x,y,null);
			else
				g.drawImage(big1,x,y,null);
		}
		else if(state == 2)
		{
			if(type == 1)
				g.drawImage(small2,x,y,null);
			else
				g.drawImage(big2,x,y,null);
		}
		else if(state == 3)
		{
			if(type == 1)
				g.drawImage(small3,x,y,null);
			else
				g.drawImage(big3,x,y,null);
		}

		return false;
	}

	public void damaged()
	{
		this.life-=1;
	}

	public Rectangle returnRect()
	{
		return this.r;
	}

	public boolean wentIn()
	{
		return wentIn;
	}
	
}
