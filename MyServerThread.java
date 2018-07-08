import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MyServerThread extends Thread{

	ArrayList<MyServerThread> clientList;
	PrintWriter out;
	BufferedReader in;
	int id;

	public MyServerThread(Socket s, int id, ArrayList<MyServerThread> clientList){
		this.clientList = clientList;
		try{
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
		}catch(Exception e){
			e.printStackTrace();
		}
		this.id = id;
	}

	public void sendMessage(String msg){
		try{
	        out.println(msg);
	        out.flush();
		}catch(Exception e) {
	        System.out.println("S: Something bad happened :(");
	        e.printStackTrace();
     	}
	}

	public String getMessage(){
		try{
			return in.readLine();
		} catch (Exception e) {
	        e.printStackTrace();
			return "S: Something bad happened :(";
      	}
	}

	public void sendMessageToAll(String msg){
		try{
	        for(int i = 0;i < clientList.size();i++){
	        	clientList.get(i).sendMessage(msg);
	        }
		}catch(Exception e) {
	        e.printStackTrace();
     	}
	}

	public void run(){
		while(true){
			String msg = getMessage();
			sendMessageToAll(msg);
		}
	}
}