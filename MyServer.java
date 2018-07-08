import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MyServer {

	public static void main(String args[]) {
		try {
		
			System.out.println("Starting server...");
			ServerSocket ssocket = new ServerSocket(8881);
			System.out.println("Server has started");
			ArrayList<MyServerThread> clientList = new ArrayList<MyServerThread>();
			int threadID = 0;

			Thread thread = new Thread(new Runnable(){
				@Override
				public void run(){
					
					int waitTime = 5000;
					int counter = 0;

					while(true){
						int x = ((int)(Math.random()*700) + 1);
						int type = ((int)(Math.random()*2)+1);

						for(int i = 0;i < clientList.size();i++){
							clientList.get(i).sendMessage("ENEMY " + x + " " + type);
						}
						counter++;
						if(counter%5 == 0 && waitTime >= 1500){
							waitTime-=500;
						}
						if(waitTime == 1500 && counter%10 == 0)
						{
							waitTime-=300;
						}
						try{ Thread.sleep(waitTime);}
						catch(InterruptedException ex){}
					}	
				}
			});

			while(true){
				if(clientList.size() <  2){
					Socket s = ssocket.accept();
					MyServerThread t = new MyServerThread(s, threadID, clientList);
					threadID++;
					clientList.add(t);
					t.sendMessage("NAME " + clientList.size());
					if(clientList.size() == 2)
					{
						
						t.sendMessageToAll("TWO");
						int state = 5;
						while(state>=0)
						{
							try{Thread.sleep(1000);}
							catch(InterruptedException ex){}
							t.sendMessageToAll("COUNT " + state);
							state-=1;
						}
						thread.start();
					}	
					t.start();
				}
			}

		  
		} catch (Exception e) {
			System.exit(0);
		}
	}
}