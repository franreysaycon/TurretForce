import java.awt.*;

public class Bullet
{
	private int x;
	private int y;
	private int dy;
	private int speed = 30;
	private Rectangle r;
	private boolean hit = false;

	public Bullet(int x, int y)
	{
		this.x = x;
		this.y = y;

		this.r = new Rectangle(x,y,10,10);
	}

	public void update()
	{
		y-=speed;

		r.setRect(x,y,10,10);
	}

	public boolean draw(Graphics g)
	{
		if(hit)
			return true;
		if(y<=0)
			return true;

		g.setColor(Color.yellow);
		g.fillOval(x-15,y,10,10);

		
		return false;
	}

	public Rectangle returnRect()
	{
		return this.r;
	}

	public void hit()
	{
		this.hit = true;
	}
}