import java.io.*;
import java.util.*;
import java.net.Socket;
import java.net.ServerSocket;

public class serverProgram implements Runnable
{
	//Varibles
	static int port = 6789;
	static Socket dataSocket;

    public static void main(String[] args) throws Exception
    {
    	ServerSocket serverSocket = new ServerSocket(port);

   		while(true){
    		serverProgram httpserver = new serverProgram();
    		Thread thread = new Thread(httpserver);
    		thread.start();
    		dataSocket = serverSocket.accept();
    	}
    }
    @Override
    public void run()
    {
    	try {
    		processRun();
    	} catch (Exception e){
    		System.out.println(e);
    	} finally {
    		try {
    			dataSocket.close();
    		}
    		catch (Exception e){
    			System.out.println(e);
    		}
    	}
    }

    public void processRun() throws Exception{
    	BufferedReader inRequest = new BufferedReader(new InputStreamReader(dataSocket.getInputStream())); //Server accepting files
    	DataOutputStream outputStream = new DataOutputStream(dataSocket.getOutputStream());
    	String requestFromClient = inRequest.readLine(); //Reading the clientes request and putting it into a varible
    	System.out.println(requestFromClient);
    	String[] nameOfFile = requestFromClient.split(" ");
    	nameOfFile[1] = nameOfFile[1].substring(1);
    	System.out.println(nameOfFile[1]);

    	//Creating File Object in Java Reference https://www.geeksforgeeks.org/file-class-in-java/
    	File userFile = new File(new File(".") , nameOfFile[1]); 
    	String contentType = "";
    	String status = "";
    	String httpResponse = "";

    	//Status checking
    	if (userFile.exists() && !userFile.isDirectory()){
    		status = "200 OK";
    		contentType = returnType(nameOfFile[1]);
    		System.out.println("200 OK");
    	} else if (!userFile.exists()){
    		status = "404 Not Found\n";
    		userFile = new File(new File(".") , "404.html");
    	} else if (nameOfFile[1].equals("./index.html")&&nameOfFile[1].contains("index") && nameOfFile[1].endsWith(".html"))
    	{
    		contentType = ("301.html");
    		status = "301 Moved Permanently";
    		userFile = new File("." + "301.html");
    	}
    	//Convert to Byte
    	byte[] fileInBytes = fileToBytes(userFile);
    	httpResponse += "HTTP/1.1 " + status + "Date: " + new Date() + "\n";
    	httpResponse += "Server: Generic Web Server\nContent-type: " + contentType + "\n";
    	httpResponse += "Content-Length: " + (int) userFile.length() + "\n\n";
    	// Send it out
    	outputStream.writeBytes(httpResponse);
    	outputStream.write(fileInBytes, 0, (int) userFile.length());
    	// Close everything
    	System.out.println(httpResponse);
    	inRequest.close();
    	outputStream.close();
    }
    public static byte[] fileToBytes(File userFile)
    {
    	byte[] bytefiles = new byte[(int) userFile.length()];
		try (FileInputStream in = new FileInputStream(userFile))
		{
			in.read(bytefiles);
		}
		catch (Exception e) 
		{
			System.out.println(e);
		}
		return bytefiles;
    }

    public static String returnType (String userFile)
    {
    	String endOfFile = userFile.substring(userFile.lastIndexOf('.')+ 1);
    	switch(endOfFile){ //Checks end of file
    		case "pdf":
    		return "text/pdf";
    		case "html":
    		return "text/html";
    		case "gif":
    		return "text/gif";
    		default:
    		return "type not found";
    	}
    }
}