import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

public class Turret{

	private Image turret = null;
	private int x, y;
	private int dx = 0;
	private int dy = 0;
	private int speed = 15;
	private int friction = 3;
	private boolean left = false;
	private boolean right = false;
	private boolean isFiring = false;
	private int firingDelay = 3; 
	private int counter = 5;
	private int state = 0; // 0-idle, 1-went right, 2-went left
	private PrintWriter pw;
	private String name;
	private Turret player2;
	private Rectangle r;

	public Turret(int x, int y, PrintWriter pw, String name){
		this.x = x;
		this.y = y;
		this.pw = pw;
		this.name = name;
		//this.r = new Rectangle(x,y,120,120);

		try
		{
			turret = ImageIO.read(new File("images/turret.png"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void update(){

		if(right)
		{
			dx+=speed;
		}
		else if(left)
		{
			dx-=speed;
		}	

		if((x+dx)<=0){
			x = 0;
			state = 0;
			dx = 0;
			right = false;
			left = false;
		} 
		else if((x+dx)>=680){
			x = 680;
			state = 0;
			dx = 0;
			right = false;
			left = false;
		} 
		else
			x+=dx;

		/*r.setRect(x,y,120,120);
		if( r.intersects(player2.getRect()))
		{
			x = (int)player2.getRect().getX()+120;
			r.setRect(x,y,120,120);
		}*/

		dx = 0;

		if(isFiring)
		{
			counter++;

			if(counter>firingDelay){
				GamePanel.bullets.add(new Bullet(x+60,y-10));
			}

			if(counter > firingDelay )
				counter = 0;
		}
		else 
			counter = 0;
	}

	public void draw(Graphics g)
	{
		g.drawImage(turret,x,y,null);
	}

	public void setLeft(boolean value)
	{
		this.left = value;
	}

	public void setRight(boolean value)
	{
		this.right = value;
	}

	public void setFiring(boolean value)
	{
		this.isFiring = value;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public void setPlayer2(Turret p2)
	{
		this.player2 = p2;
	}

	public Rectangle getRect()
	{
		return this.r;
	}
}
