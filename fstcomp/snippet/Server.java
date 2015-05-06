package snippet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	private static final int PORT = 24250;
	private ServerSocket mServerSocket;
	
	public Server(String data) {
		try {
			// Create new Server Socket
			this.mServerSocket = new ServerSocket(PORT);
			System.out.println("ServerSocket created and waiting for Client");
			// Wait for incoming connections
			Socket incoming = mServerSocket.accept();
			System.out.println("Socket connected!");
			// Send the data to the client
			PrintWriter out = new PrintWriter(incoming.getOutputStream(), true);
			out.print(data);
			out.close();
			System.out.println("Data was sent successfully!");
			incoming.close();
			mServerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
			
}
