import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;
import java.net.*;
import javazoom.jl.player.Player;
import sun.audio.*;

public class GamePanel extends JPanel implements Runnable
{
	private static final int WINDOW_WIDTH = 800;
	private static final int WINDOW_HEIGHT = 700;
	
	private Thread animator;
	private BufferedReader in;
	private PrintWriter out;
	private volatile boolean running = false;
	private volatile boolean gameOver = false; 
	private volatile boolean isPaused = false;
	private volatile boolean isTwo = false;
	private int state = 5;
	private int textState = -1;
	
	private Graphics mainGraphics;
	private Image mainImage = null;
	private Image background = null;
	private Image turretForce = null;

	private boolean fired = false;

	public ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	public static ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private Turret player1;
	private Turret player2;
	
	private long FPS = 60;
	private double averageFPS;

	private String name;

	public GamePanel(String ip)
	{
		setBackground(Color.white);
		setPreferredSize(new Dimension(WINDOW_WIDTH,WINDOW_HEIGHT));
		
		setFocusable(true);
		requestFocus();
		keyboardEvents();
		mouseEvents();

		Thread thread1 = new Thread(new Runnable(){

			@Override
			public void run()
			{
				while(!isTwo)
				{
					textState++;
					textState= textState%6;

					try 
					{
						Thread.sleep(5000);
					}
					catch(Exception ex){}

					gameRender();
					paintScreen();
				}
			}
		});

		thread1.start();
		
		try{
			Socket s = new Socket(ip, 8881);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
			background = ImageIO.read(new File("images/background.png"));
			turretForce = ImageIO.read(new File("images/gametitle.png"));

			Thread thread = new Thread(new Runnable(){
				@Override
				public void run(){
					while(true){
						try{
							String msg = in.readLine();
							//System.out.println(msg);
							String[] parts = msg.split(" ");
							if(parts[0].equals("LEFT")){
								if(parts[1].equals("True")){
									if(parts[2].equals(name))
										player1.setLeft(true);
									else
										player2.setLeft(true);
								}
								else if(parts[1].equals("False")){
									if(parts[2].equals(name))
										player1.setLeft(false);
									else
										player2.setLeft(false);
								}
							}
							else if(parts[0].equals("RIGHT")){
								if(parts[1].equals("True")){
									if(parts[2].equals(name))
										player1.setRight(true);
									else
										player2.setRight(true);
								}
								else if(parts[1].equals("False")){
									if(parts[2].equals(name))
										player1.setRight(false);
									else
										player2.setRight(false);
								}
							}
							else if(parts[0].equals("PEW")){
								if(parts[1].equals("True")){
									if(parts[2].equals(name))
										player1.setFiring(true);
									else
										player2.setFiring(true);
									if(fired)
										fired = false;
									fx("Ray Gun.mp3");
								}
								else if(parts[1].equals("False")){
									if(parts[2].equals(name))
										player1.setFiring(false);
									else
										player2.setFiring(false);
								}
					
							}
							else if(parts[0].equals("ENEMY")){
								enemies.add(new Enemy(Integer.parseInt(parts[1]), 10,Integer.parseInt(parts[2])));
							}
							else if(parts[0].equals("HIT")){
								enemies.get(Integer.parseInt(parts[2])).damaged();
								bullets.get(Integer.parseInt(parts[1])).hit();
							}
							else if(parts[0].equals("TWO")){
								fx("Sandstorm.mp3");
								two();
							}
							else if(parts[0].equals("NAME")){
								if(parts[1].equals("1"))
								{
									player1 = new Turret(200,570,out,"1");
									player2 = new Turret(500,570,out,"1");
									player1.setPlayer2(player2);
									name = "1";
								}
								else
								{
									player1 = new Turret(500,570,out,"1");
									player2 = new Turret(200,570,out,"2");
									player1.setPlayer2(player2);
									name = "2";
								}
							}
							else if(parts[0].equals("MOVE")){
								if(parts[2].equals("1"))
									player1.setX(Integer.parseInt(parts[1]));
								else
									player2.setX(Integer.parseInt(parts[1]));
							}
							else if(parts[0].equals("COUNT")){
								state = Integer.parseInt(parts[1]);
							}
							else if(parts[0].equals("GameOver")){
								gameOver = true;
								fx("GameoverOne.mp3");
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}	
				}
			});
			thread.start();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private void mouseEvents()
	{
		addMouseListener( new MouseAdapter( ) {
			public void mousePressed(MouseEvent e)
			{ 
				testPress(e.getX( ), e.getY( )); 
			}
		});
	}
	
	private void keyboardEvents( )
	{
		addKeyListener( new KeyAdapter( ) {

			public void keyPressed(KeyEvent e)
			{ 
				int keyCode = e.getKeyCode( );
				
				if(!isPaused)
				{
					if (keyCode == KeyEvent.VK_ESCAPE) 
					{
						running = false;
					}

					if (keyCode == KeyEvent.VK_A) 
					{
						out.println("LEFT True " + name);
						out.flush();
					}
					else if (keyCode == KeyEvent.VK_D) 
					{
						out.println("RIGHT True " + name);
						out.flush();
					}

					if (keyCode == KeyEvent.VK_L)
					{
						out.println("PEW True " + name);
						out.flush();
					}
				}
				if(keyCode == KeyEvent.VK_P){
						if(isPaused) resume();
						else pause();
				}
			}

			public void keyReleased(KeyEvent e)
			{ 
				int keyCode = e.getKeyCode( );
				
				if (keyCode == KeyEvent.VK_A) 
				{
					out.println("LEFT False " + name);
					out.flush();
				}
				else if (keyCode == KeyEvent.VK_D) 
				{
					out.println("RIGHT False " + name);
					out.flush();
				}
				if (keyCode == KeyEvent.VK_L )
				{
					out.println("PEW False " + name);
					out.flush();
				}
			}
		});
	}
	
	public void testPress(int x, int y)
	{
		System.out.println("PRESSED: " + x + " " + y );
	}
	
	public void addNotify()
	{
		super.addNotify();
		startGame();
	}
	
	private void startGame()
	{
		if(animator == null || !running )
		{
			animator = new Thread(this);
			animator.start();
		}
	}
	
	public void stopGame()
	{
		running = false;
	}
	
	public void pause()
	{
		isPaused = true;
	}
	
	public void resume()
	{
		isPaused = false;
	}
	
	public void run()
	{
		running = true;

		gameRender();
		paintScreen();

		
		while(running)
		{	
			if(!isPaused && isTwo)
			{
				gameUpdate();
				gameRender();
				paintScreen();
			}
			
			try{ Thread.sleep(1);}
			catch(InterruptedException ex){}
		}
		
		System.exit(0);
	}
	
	private void gameUpdate()
	{
		if(!gameOver)
		{
			for(int i = 0;i < enemies.size();i++)
				enemies.get(i).update();	

			player1.update();
			player2.update();

			for(int i = 0;i < bullets.size();i++)
			{
				bullets.get(i).update();

				for(int j = 0; j<enemies.size(); j++)
				{
					Rectangle a = bullets.get(i).returnRect();
					Rectangle b = enemies.get(j).returnRect();

					if(a.intersects(b))
					{
						out.println("HIT " + i + " " + j);
						out.flush();
					}
				}
			}
		}
	}
	
	private void gameRender()
	{
		if(mainImage == null )
		{
			mainImage = createImage(WINDOW_WIDTH,WINDOW_HEIGHT);
			if( mainImage == null )
			{
				System.out.println("Main image is null");
			}
			else
			{
				mainGraphics = mainImage.getGraphics();
			}
		}

		mainGraphics.setColor(Color.white);
		mainGraphics.drawImage(background,0,0,null);
		
		for(int i = 0;i < enemies.size();i++)
		{	
			if( enemies.get(i).draw(mainGraphics))
			{
				if(enemies.get(i).wentIn())
				{
					out.println("GameOver");
				}
				else
				{
					enemies.remove(i);
					i--;
					fx("explosion.mp3");
				}
			}
		}
		
		if(isTwo)
		{
			player1.draw(mainGraphics);
			player2.draw(mainGraphics);
		}

		for(int i = 0; i<bullets.size(); i++)
		{
			if(bullets.get(i).draw(mainGraphics))
			{
				bullets.remove(i);
				i--;
			}
		}
		
		if(gameOver)
			gameOverMessage(mainGraphics);

		if(!isTwo)
		{
			mainGraphics.setFont(new Font("Century Gothic", Font.PLAIN, 30)); 
			mainGraphics.setColor(Color.white);
			mainGraphics.drawImage(turretForce,110,10,null);
			if(textState == 0)
				mainGraphics.drawString("Find a partner please :P", 223,550);
			else if(textState == 1)
				mainGraphics.drawString("Forever Alone much? Just one friend. :(", 120,550);
			else if(textState == 2)
				mainGraphics.drawString("Oh come on. Try omegle! XD", 200,550);
			else if(textState == 3)
				mainGraphics.drawString("You lonely bastard, want tea? :(", 180,550);
			else if(textState == 4)
				mainGraphics.drawString("You need to be two to play :D", 195,550);
			else if(textState == 5)
				mainGraphics.drawString("Dafuq. Just find one on the street.", 165,550);
		}
		else if(state > 0)
		{
			mainGraphics.setFont(new Font("Century Gothic", Font.PLAIN, 20)); 
			mainGraphics.setColor(Color.white);
			mainGraphics.drawString("THE GAME STARTS IN " + state,300,306);
			mainGraphics.drawString("A -> left , D -> right , Hold L to fire", 233,326);
		}
	}
	
	private void gameOverMessage(Graphics g)
	{
		g.setFont(new Font("Century Gothic", Font.PLAIN, 30)); 
		g.setColor(Color.white);
		g.drawString("GAME OVER",320,313);
	}
	
	private void paintScreen()
	{
		Graphics g;
		
		try 
		{
			g = this.getGraphics( ); 
			if ( (g != null) && (mainImage != null) )
				g.drawImage(mainImage, 0, 0, null);
			
			Toolkit.getDefaultToolkit( ).sync( ); 
			g.dispose( );
		}
		catch (Exception e)
		{ 
			System.out.println("Graphics context error: " + e); 
		}
	}

	public void two()
	{
		isTwo = true;
		gameRender();
		paintScreen();
	}

	public void fx(String title) 
	{
	
		try {
			
			final Player player;
			FileInputStream fis = new FileInputStream("sounds"+File.separatorChar + title);
			BufferedInputStream bis = new BufferedInputStream(fis);
			player = new Player(bis);
		  
			new Thread() 
			{
				@Override
				public void run() 
				{
					try 
					{
						if(title.contains("Ray"))
						{
							if(!fired)
							{
								player.play();
								fired = true;
							}
						}
						else
							player.play();
					} 
					catch (Exception e) 
					{
						System.err.printf("%s\n", e.getMessage());
					}
				}
			}.start();
		
		} 
		catch (Exception e) 
		{
			System.err.printf("%s\n", e.getMessage());
		}

	}
}