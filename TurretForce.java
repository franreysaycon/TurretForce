import java.awt.*;
import javax.swing.*;
import java.io.*;

public class TurretForce extends JFrame{

	GamePanel gp;
	public TurretForce(){
		String ip = JOptionPane.showInputDialog(null,"Input IP Address.");
		try{
			gp = new GamePanel(ip);
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		this.setSize(800, 700);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.getContentPane().add(gp);
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
	    this.setLocation(x, y);
	    setVisible(true);
	}
	public static void main(String[] args){
		new TurretForce();
	}

}