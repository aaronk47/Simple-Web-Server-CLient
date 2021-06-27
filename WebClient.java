package Client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.GregorianCalendar;

public class WebClient {

	public static void main(String[] args) {
		final String CRLF = "\r\n"; 										
		final String SP = " "; 												
		String serverHost = null;
		int serverPort = 8080;												
		String filePath = "/";												
		if(args.length == 1)										
		{
			
			serverHost = args[0];											
		}
		else if (args.length == 2){
			serverHost = args[0];											
			try {															
				serverPort = Integer.parseInt(args[1]); 					
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[CLIENT]> Integer Port is not provided. Default Server port will be used.");
				filePath = args[1];											
			}
		}
		else if (args.length == 3){
			serverHost = args[0];											
			try {															
				serverPort = Integer.parseInt(args[1]); 					
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[CLIENT]> Integer Port is not provided. Default Server port will be used.");
			}

			filePath = args[2];												
		}
		else
		{
			System.err.println("[CLIENT]> Not enough parameters provided. At least serverHost is required.");
			System.exit(-1);
		}
		System.out.println("[CLIENT]> Using Server Port: " + serverPort);
		System.out.println("[CLIENT]> Using FilePath: " + filePath);
		Socket socket = null;												
																			
		BufferedReader socketInStream = null; 								
		DataOutputStream socketOutStream = null; 							
		
		FileOutputStream fos = null; 										
		
		try {
			
																			
			InetAddress serverInet = InetAddress.getByName(serverHost);
			
																			
			socket = new Socket(serverInet, serverPort);
			System.out.println("[CLIENT]> Connected to the server at " + serverHost + ":" + serverPort);
			
																			
			socketInStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
																			
			socketOutStream = new DataOutputStream(socket.getOutputStream());

																			
			String requestLine = "GET" + SP + filePath + SP +"HTTP/1.0" + CRLF;
			System.out.println("[CLIENT]> Sending HTTP GET request: " + requestLine);

																			
			long start = new GregorianCalendar().getTimeInMillis(); 
			
																			
			socketOutStream.writeBytes(requestLine);
			
																			
			socketOutStream.writeBytes(CRLF);
			
																			
			socketOutStream.flush();
			
			System.out.println("[CLIENT]> Waiting for a response from the server");
																			
			String responseLine = socketInStream.readLine();
			System.out.println("[CLIENT]> Received HTTP Response with status line: " + responseLine);

																			
			String contentType = socketInStream.readLine();
			System.out.println("[CLIENT]> Received " + contentType);

																			
			socketInStream.readLine();

			System.out.println("[CLIENT]> Received Response Body:");
																			
			StringBuilder content = new StringBuilder();
			String res;
			while((res = socketInStream.readLine()) != null)
			{
																			
				content.append(res + "\n");
				
																			
				System.out.println(res);
			}
										
			String fileName = getFileName(content.toString());				
			long finish = new GregorianCalendar().getTimeInMillis();
			System.out.println("RTT: " + (finish-start+" ms"));
																			
																			
			fos = new FileOutputStream(fileName);
			fos.write(content.toString().getBytes());
			fos.flush();
			System.out.println("[CLIENT]> HTTP Response received. File Created: " + fileName);
		} catch (IllegalArgumentException iae) {
			System.err.println("[CLIENT]> EXCEPTION in connecting to the SERVER: " + iae.getMessage());
		} catch (IOException e) {
			System.err.println("[CLIENT]> ERROR " + e);
		}
		finally {
			try {
																			
				if (socketInStream != null) {
					socketInStream.close();
				}
				if (socketOutStream != null) {
					socketOutStream.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (socket != null) {
					socket.close();
					System.out.println("[CLIENT]> Closing the Connection.");
				}
			} catch (IOException e) {
				System.err.println("[CLIENT]> EXCEPTION in closing resource." + e);
			}
		}
	}
									
	private static String getFileName(String content)						
	{
		String fname = "";												
		fname = content.substring(content.indexOf("<title>")+("<title>").length(), content.indexOf("</title>"));
		if(fname.equals(""))
		{
			fname = "index";
		}	
		fname = fname+".html";
		return fname;
	}
}