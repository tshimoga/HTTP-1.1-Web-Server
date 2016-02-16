package web_server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

public class NormalWebServer implements Runnable {

	private static ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private String serverText = "";

	public NormalWebServer(Socket clientSocket, String serverText) {
		this.clientSocket = clientSocket;
		this.serverText = serverText;
	}

	private static void handleShutdown() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					serverSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

	public static void main(String[] args) {
		handleShutdown();
		int portNumber = Integer.parseInt(args[0]);
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (;;) {
			try {
				Socket socket = serverSocket.accept();
				new Thread(new NormalWebServer(socket, "New Thread")).start();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@Override
	public void run() {
		try {
			String inputLine = "";
			Protocol http = new Protocol();
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			// ZipOutputStream out = new
			// ZipOutputStream(clientSocket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			String request = "";
			while ((inputLine = in.readLine()) != null && !inputLine.equals("")) {
				request += inputLine + "\n";
			}

			http.processInput(request);
			http.processResponse();
			String[] arr = http.getResponse().toString().split("break");
			boolean encode = false;
			for (String s : arr) {

				if (encode && !"".equals(s)) {
					PrintWriter writer = new PrintWriter("result.html", "UTF-8");
					writer.println(s);
					writer.close();
					File myFile = new File ("result.html.gz");
					GZIPOutputStream zipStream = new GZIPOutputStream(new FileOutputStream(myFile));
					FileInputStream inFile = 
				            new FileInputStream("result.html");
					
					byte[] buffer = new byte[1024];
					
					int len;
			        while ((len = inFile.read(buffer)) > 0) {
			        	zipStream.write(buffer, 0, len);
			        }
			 
			        zipStream.finish();
			        zipStream.close();
			        
			        BufferedInputStream bis =new BufferedInputStream(new FileInputStream(myFile));
			        byte [] mybytearray  = new byte [(int)myFile.length()];
			        
			        bis.read(mybytearray,0,mybytearray.length);
			        clientSocket.getOutputStream().write(mybytearray,0,mybytearray.length);
			        inFile.close();
			        bis.close();
			        clientSocket.getOutputStream().flush();
				} else {
					out.println(s);
				}

				if (s.contains("gzip")) {
					encode = true;
				}
			}
			out.flush();
			out.close();
			clientSocket.close();

		} catch (IOException e) {
			e.printStackTrace();

		}

	}
	
	

}
