package Problem3;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import NetworkLayer.NetworkLayer;

public class TCPEchoServer extends NetworkLayer {
	//Maximum threads in the thread pool is number of CPUs + 1.
	private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors()+1;
	
	public static void main(String[] args) {
		ExecutorService threadPool;
    	MYPORT = DEFAULT_SERVER_PORT;
    	int bufferSize = DEFAULT_BUFSIZE;
    	
    	try {
        	if (validPort(args[0]))
            	MYPORT = stringToInt(args[0]);
        	
        	if (validBufferSize(args[1]))
        		bufferSize = stringToInt(args[1]);
    	} catch (IndexOutOfBoundsException e) {
    		//If an invalid input was give, the default will be used
    	}
    	
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
				threadPool.execute(new TCPEchoServerThread(clientSocket,bufferSize));
			}
			
			//Kill running threads
			//threadPool.shutdownNow();
			
		} catch (IOException e) {
			System.err.printf("Cannot bind to port %d", MYPORT);
		}
		
		
	}

}