package Problem2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPEchoServer extends NetworkLayer {
	//Maximum threads in the thread pool is number of CPUs + 1.
	private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors()+1;
	
	public static void main(String[] args) {
		ExecutorService threadPool;
    	MYPORT = 4950;
    	
		threadPool = Executors.newFixedThreadPool(MAX_THREADS);
		
		ServerSocket socket;
		
		try {
			socket = new ServerSocket();
			
			socket.bind(new InetSocketAddress(MYPORT));
			
			if (DEBUG_MODE)
				System.out.println("Server is running...");
				
			//Runs new task in the thread pool whenever a connection is established
			while (true) {
				Socket clientSocket = socket.accept();
				threadPool.execute(new TCPEchoServerThread(clientSocket));
			}
			
			//Kill running threads
			//threadPool.shutdownNow();
			
		} catch (IOException e) {
			System.err.printf("Cannot bind to port %d", MYPORT);
		}
		
		
	}

}