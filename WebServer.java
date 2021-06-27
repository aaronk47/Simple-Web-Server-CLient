package Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;


public class WebServer implements Runnable {
	
	public static void main(String[] args) {						
		int port = 8080;											
		if(args.length == 1)										
		{	
			try {													
				port = Integer.parseInt(args[0]); 					
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[SERVER]> No Port number provided. Server will start at default port.");
			}
		}

		System.out.println("[SERVER]> Using Server Port : " + port);
		WebServer ws = new WebServer(port); 						
		new Thread(ws).start();										
	}
	
	private ServerSocket sSocket; 									
	private String shost; 											
	private int sPort; 											
	private final String DEF_HOST = "localhost";					
	private final int DEF_PORT = 8080;
	
	//Default constructor if no port is passed
	public WebServer ()													
	{
		this.shost = DEF_HOST; 								
		this.sPort = DEF_PORT; 								
	}
	
	//Parameterized constructor if a port and shost are passed
	public WebServer (String sHost, int port)							
	{
		this.shost = sHost; 										
		this.sPort = port; 										
	}
	
	//Parameterized constructor if a port is passed
	public WebServer (int port)											
	{
		this.shost = DEF_HOST; 								
		this.sPort = port; 										
	}
	
	@Override
	public void run() {
		
		try {
			InetAddress serverInet = InetAddress.getByName(shost);	
																		
			sSocket = new ServerSocket(sPort, 0, serverInet);

			System.out.println("SERVER started at host: " + sSocket.getInetAddress() + " port: " + sSocket.getLocalPort() + "\n");
			
																		
			int clientID=0;
			
																		
			while(true){
				Socket clientSocket = sSocket.accept();			
																		
				System.out.println("[SERVER - CLIENT"+clientID+"]> Connection established with the client at " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
				
																		
				RequestHandler rh = new RequestHandler(clientSocket, clientID);
				new Thread(rh).start();									
				clientID++;												
			}
			
		} catch (UnknownHostException e) {
			System.err.println("[SERVER]> UnknownHostException for the hostname: " + shost); 	
		} catch (IllegalArgumentException iae) {
			System.err.println("[SERVER]> EXCEPTION in starting the SERVER: " + iae.getMessage());	
		}
		catch (IOException ioe) {
			System.err.println("[SERVER]> EXCEPTION in starting the SERVER: " + ioe.getMessage());	
		}
		finally {
				try {
					if(sSocket != null){
						sSocket.close();
					}
				} catch (IOException e) {
					System.err.println("[SERVER]> EXCEPTION in closing the server socket." + e);	
				}
		}
	}
}