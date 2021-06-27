package Server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public final class RequestHandler implements Runnable {

	private Socket cSocket; 									 
	private int clientID; 											

	private final String CRLF = "\r\n"; 							
	private final String SP = " "; 									
	public RequestHandler(Socket cs, int cID) {						
		this.cSocket = cs;
		this.clientID = cID;
	}
	@Override
	public void run() {
																	
		BufferedReader socketInStream = null; 						
		DataOutputStream socketOutStream = null; 					
		
		FileInputStream fis = null; 								
		
		try {
			socketInStream = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));		
			socketOutStream = new DataOutputStream(cSocket.getOutputStream());							
			String packet = socketInStream.readLine();							
			if(packet != null)													
			{
				System.out.println("[SERVER - CLIENT"+clientID+"]> Received a request: " + packet);
				String[] msgParts = packet.split(SP);							
				if (msgParts[0].equals("GET") && msgParts.length == 3) {		
					String filePath = msgParts[1];								
																				
					if(filePath.indexOf("/") != 0)
					{	
						filePath = "/" + filePath;
					}
					System.out.println("[SERVER - CLIENT"+clientID+"]> Requested filePath: " + filePath);

					if(filePath.equals("/"))									
					{
						System.out.println("[SERVER - CLIENT"+clientID+"]> Respond with default /index.html file");
						filePath = filePath + "index.html";						
					}
					
					filePath = "." + filePath;									

							
					File file = new File(filePath);								
					try {
																				
						if (file.isFile() && file.exists()) {
																				
							String responseLine = "HTTP/1.0" + SP + "200" + SP + "OK" + CRLF;
							socketOutStream.writeBytes(responseLine);
							socketOutStream.writeBytes("Content-type: " + getContentType(filePath) + CRLF); 		
							socketOutStream.writeBytes(CRLF);					
							fis = new FileInputStream(file);					
							byte[] buffer = new byte[1024];						
							int bytes = 0;
							while((bytes = fis.read(buffer)) != -1 ) {			
								socketOutStream.write(buffer, 0, bytes);
							}
							
							System.out.println("[SERVER - CLIENT"+clientID+"]> Sending Response with status line: " + responseLine);
							
							socketOutStream.flush();							
							System.out.println("[SERVER - CLIENT"+clientID+"]> HTTP Response sent");
							
						} else {
							
							System.out.println("[SERVER - CLIENT"+clientID+"]> ERROR: Requested filePath " + filePath + " does not exist"); 		
							String responseLine = "HTTP/1.0" + SP + "404" + SP + "Not Found" + CRLF;				
							socketOutStream.writeBytes(responseLine);

							
							socketOutStream.writeBytes("Content-type: text/html" + CRLF);							
							socketOutStream.writeBytes(CRLF);					
							socketOutStream.writeBytes(getErrorFile());			
							System.out.println("[SERVER - CLIENT"+clientID+"]> Sending Response with status line: " + responseLine);
							socketOutStream.flush();
							System.out.println("[SERVER - CLIENT"+clientID+"]> HTTP Response sent"); 
						}
					} catch (FileNotFoundException e) {
						System.err.println("[SERVER - CLIENT"+clientID+"]> EXCEPTION: Requested filePath " + filePath + " does not exist");
					} catch (IOException e) {
						System.err.println("[SERVER - CLIENT"+clientID+"]> EXCEPTION in processing request." + e.getMessage());
					}
				} else {
					System.err.println("[SERVER - CLIENT"+clientID+"]> Invalid HTTP GET Request. " + msgParts[0]);
				}
			}
			else
			{	
				System.err.println("[SERVER - CLIENT"+clientID+"]> Discarding a NULL/unknown HTTP request."); 
			}
		} catch (IOException e) 
		{
			System.err.println("[SERVER - CLIENT"+clientID+"]> EXCEPTION in processing request." + e.getMessage());
		} finally {
			try {															
				if (fis != null) {
					fis.close();
				}
				if (socketInStream != null) {
					socketInStream.close();
				}
				if (socketOutStream != null) {
					socketOutStream.close();
				}
				if (cSocket != null) {
					cSocket.close();
					System.out.println("[SERVER - CLIENT"+clientID+"]> Closing the connection.\n");
				}
			} catch (IOException e) {
				System.err.println("[SERVER - CLIENT"+clientID+"]> EXCEPTION in closing resource." + e);
			}
		}
	}
	
	private String getContentType(String filePath)								
	{
		if(filePath.endsWith(".html") || filePath.endsWith(".html"))			
		{
			return "text/html";
		}
		return "application/octet-stream";										
	}
	private String getErrorFile ()
	{
		String errorFileContent = 	"<!doctype html>" + "\n" +
									"<html lang=\"en\">" + "\n" +
									"<head>" + "\n" +
									"    <meta charset=\"UTF-8\">" + "\n" +
									"    <title>Error 404</title>" + "\n" +
									"</head>" + "\n" +
									"<body>" + "\n" +
									"    <b>ErrorCode:</b> 404" + "\n" +
									"    <br>" + "\n" +
									"    <b>Error Message:</b> The requested file does not exist on this server." + "\n" +
									"</body>" + "\n" +
									"</html>";
		return errorFileContent;
	}
}