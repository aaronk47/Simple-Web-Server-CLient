# Simple-Web-Server-Client
This is a Simple Web Server-CLient program. This project I completed in my first semester in the Computer Networks course at The University of Texas at Arlington.

Steps for the Compilation of the Code: 
---------------------------------------

1) Save the client and server codes in 2 different directories on same disk.
2) Compile the server side on command prompt by following commands:
	-> cd WebServerCN\src\Server
	-> javac *.java
3) Compile the client side on another command prompt by following commands:
	-> cd WebClient\src\Client
	-> javac *.java
4) On the server side prompt put following command:
	-> java WebServer (port number)
		Example: java WebServer 8080
5)On client side prompt:
	-> java Webclient (ip address) (port number) (file name)
		Example: java WebClient 127.0.0.1 8080 index.html
	    or	Example: java WebClient localhost 8080 index.html
	 the ip address used is 127.0.0.1 port number 8080 and file name is index.html
