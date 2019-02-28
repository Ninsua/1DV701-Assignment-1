/*	READ ME:
 * 		The program takes the following arguments: port buffersize -debug
 * 		If no arguments are provided, the default values will be used. PORT: 4950 Buffer size: 1024
 * 		
 * 		The debug mostly prints exception stack traces and things that might be interesting to know
 * 		regarding stream writing/read and buffer information.
 */

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
    	
    	//Check and set arguments
    	try {
        	if (validPort(args[0]))
            	MYPORT = stringToInt(args[0]);
        	
        	if (validBufferSize(args[1]))
        		bufferSize = stringToInt(args[1]);
        	
        	if (validDebugArgument(args[2])) {
        		DEBUG_MODE = true;
        	}
    	} catch (IndexOutOfBoundsException | NumberFormatException e) {
    		if (DEBUG_MODE)
    			e.printStackTrace();
    		//If an invalid input was given, the default values will be used
    	}
    	
		threadPool = Executors.newFixedThreadPool(MAX_THREADS);
		
		ServerSocket socket;
		
		try {
			socket = new ServerSocket();
			
			socket.bind(new InetSocketAddress(MYPORT));
			
			if (DEBUG_MODE)
				System.out.println("Server is running...");
				
			//Runs new task in thread from the thread pool whenever a connection is established
			while (true) {
				Socket clientSocket = null;
				try {
					clientSocket = socket.accept();
					if (DEBUG_MODE)
						System.out.println("Incoming connection accepted");
					threadPool.execute(new TCPEchoServerThread(clientSocket,bufferSize,DEBUG_MODE));
				} catch (IOException e) {
					if (DEBUG_MODE)
						System.err.println("Could not accept connection");
				}
			}
			
			//Kill running threads
			//threadPool.shutdownNow();
			
		} catch (IOException e) {
			System.err.printf("Cannot bind to port %d", MYPORT);
		}
	}
}