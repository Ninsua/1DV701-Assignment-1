package Problem3;

import java.io.BufferedOutputStream;

/*
 * Questions:
 * Is the buffer size correct? It's currently filling the buffer size with the transmission 
 */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import Problem2.NetworkLayer;

public class TCPEchoClient extends NetworkLayer {
	private static final String MSG = "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
			+ "Sed viverra, justo eget ultrices dignissim,"
			+ "leo nunc egestas erat, eget aliquet urna mauris nec nisi.";
	
	@SuppressWarnings("resource")	//Because client is supposed to loop infinitely (according to Morgan)
	public static void main(String[] args) {
		Socket socket = null;
		
		//Checks and sets arguments
		validArguments(args);
		
		//Fill buffer with message
		/*int rateCount = 0;
		byte[] messageBytes = MSG.getBytes();
		for (int i = 0; i < buf.length && rateCount < transmissionRate; i++) {
			buf[i] = messageBytes[i%messageBytes.length];
			if (i%messageBytes.length == 0)
				rateCount++;
		}*/

		try {
			while (true) {
				/* Set up socket*/
				socket = new Socket();
				
				//Three way handshake
				socket.connect(new InetSocketAddress(destinationIP,destinationPort),(int)A_SECOND*6);
				
				
				long timerEnd = 0;
				int rateCount = 0;
				long timerStart = System.currentTimeMillis();
				while (timerEnd-timerStart < A_SECOND && rateCount < transmissionRate) {
					//Write message to stream
					socket.getOutputStream().write(MSG.getBytes());
					socket.getInputStream().read(buf);
					rateCount++;
				}

				//Read message from stream
				System.out.println(socket.getInputStream().read(buf));

				System.out.println(new String(buf));
				
				socket.close();
				
				//If debug mode, sleep for 2 seconds
				if (DEBUG_MODE)
					sleep();
				
			}
		} catch (IOException e)  {
			System.err.printf("Cannot reach %s on port %d \n",destinationIP,destinationPort);
		}

	}
	
    private static void sleep()  {
    	try {
    		Thread.sleep(10000);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    }

}
