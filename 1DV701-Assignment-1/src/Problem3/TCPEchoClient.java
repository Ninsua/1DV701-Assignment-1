package Problem3;

/*	TODO
 *	TCP client buffer fills before server can send all.
 *	Build string from buffer so whole message can be read from server.
 * 
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import NetworkLayer.NetworkLayer;


public class TCPEchoClient extends NetworkLayer {
	private static String MSG = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
			+ "Sed viverra, justo eget ultrices dignissim, "
			+ "leo nunc egestas erat, eget aliquet urna mauris nec nisi.";
	
	@SuppressWarnings("resource")	//Because client is supposed to loop infinitely (according to Morgan)
	public static void main(String[] args) {
		Socket socket = null;
		
		//Checks and sets arguments
		validArguments(args);
		
		if (buf.length < MSG.getBytes().length) {
			System.err.println("Buffer size cannot be smaller than the message");
			System.exit(1);
		}
		
		//Concat line break to string for nicer output
		if (transmissionRate > 1)
			MSG = MSG+"\n";

		try {
			while (true) {
				/* Set up socket*/
				socket = new Socket();
				
				//Three way handshake
				socket.connect(new InetSocketAddress(destinationIP,destinationPort),(int)A_SECOND*6);
				
				//Save streams
				InputStream readStream = socket.getInputStream();
				OutputStream writeStream = socket.getOutputStream();
				
				byte[] messageBytes = MSG.getBytes();
				
				//Attempts to send out as many packages as possible during a second, within the transmission rate
				long timerEnd = 0;
				int rateCount = 0;
				long timerStart = System.currentTimeMillis();
				int bytesSent = 0;
				while (timerEnd-timerStart < A_SECOND && rateCount < transmissionRate) {
					//Write whole message to stream
					writeStream.write(messageBytes);
					rateCount++;
				}
				
				bytesSent = rateCount*messageBytes.length;
				
				//Read bytes from stream until the whole message has been read or the buffer is full
				int readBytes = 1;
				int bufferOffset = 0;
				int totalReadBytes = 0;
				while (totalReadBytes < messageBytes.length && bufferOffset < buf.length) {
					System.out.println("Trying to read "+(buf.length-bufferOffset)+" bytes to offset "+bufferOffset);
					readBytes = readStream.read(buf,bufferOffset,buf.length-bufferOffset);
					System.out.println("Client bytes read: "+readBytes);
					totalReadBytes += readBytes;
					bufferOffset = bufferOffset+readBytes;
				}

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
    		Thread.sleep(5000);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    }

}
