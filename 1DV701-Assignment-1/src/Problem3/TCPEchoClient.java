/*	READ ME:
 * 		The program takes the following arguments: server_name port transmission_rate buffer_size -debug
 * 		
 * 		The debug mode is highly recommended due to the infinite loop (specified by Morgan) that will loop the client forever.
 * 		If the debug mode is activated the thread will sleep for 5 seconds before trying to echo again, print exception stack traces
 * 		and other things that might be interesting to know
 * 		regarding stream writing/read and buffer information.
 */

package Problem3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import NetworkLayer.NetworkLayer;


public class TCPEchoClient extends NetworkLayer {
	private static String MSG = "This is an echo message!"+"\n";/*"Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
			+ "Sed viverra, justo eget ultrices dignissim, "
			+ "leo nunc egestas erat, eget aliquet urna mauris nec nisi."+"\n";*/
	
	private static String recievedMSG = "";
	
	@SuppressWarnings("resource")	//Because client is supposed to loop infinitely (according to Morgan)
	public static void main(String[] args) {
		Socket socket = null;
		
		//Checks and sets arguments
		validArguments(args);
		
		try {
			while (true) {
				/* Set up socket*/
				socket = new Socket();
				
				//Bind to ephemeral local port
				socket.bind(null);
				
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
					writeStream.write(messageBytes,0,messageBytes.length);
					bytesSent += messageBytes.length;
					rateCount++;
					
					timerEnd = System.currentTimeMillis();
				}
				
				//Calculate time to send
				double timeToSend = (timerEnd-timerStart)/A_SECOND;
				
				//Check if all of the transmission rate could be sent within one second, if not, print stats
				if (!(bytesSent == transmissionRate*messageBytes.length)) {
					System.out.printf("Not all messages were sent. Remaining bytes: %d \n",transmissionRate*messageBytes.length-bytesSent);
				}
				
				//Print stats
				System.out.printf("Sent %d bytes in %f seconds on %s port %d \n",bytesSent,timeToSend,destinationIP,destinationPort);
				
				//Read bytes from stream until the whole message has been read
				recievedMSG = "";
				int readBytes = 1;
				int bufferOffset = 0;
				while (recievedMSG.getBytes().length < bytesSent && readBytes != -1) {
					if (DEBUG_MODE)
						System.out.println("Trying to read "+(buf.length-bufferOffset)+" bytes to offset "+bufferOffset);
					readBytes = readStream.read(buf,bufferOffset,buf.length-bufferOffset);
					if (DEBUG_MODE)
						System.out.println("Client bytes read: "+readBytes);
					
					if (readBytes > 0) {
						//Reconstruct string from buffer
						recievedMSG += new String(buf,bufferOffset,readBytes);
						
						//Set proper buffer offset. Makes sure that no reading of buffer 
						bufferOffset = bufferOffset+readBytes;
						if (bufferOffset >= buf.length || bufferOffset+readStream.available() >= buf.length)
							bufferOffset = 0;
					}
				}

				//Prints echoed message
				System.out.println(recievedMSG);
				
				//Closes socket and streams.
				writeStream.close();
				readStream.close();
				socket.close();
				
				//If debug mode, sleep
				if (DEBUG_MODE)
					sleep();
			}
		} catch (IOException e)  {
			if (DEBUG_MODE)
				e.printStackTrace();
			System.err.printf("Cannot reach %s on port %d \n",destinationIP,destinationPort);
		} finally {
			//Makes sure the socket is closed if an error has occurred or if the client is shutting down.
			try {
				socket.close();
			} catch (IOException e) {
				if (DEBUG_MODE)
					e.printStackTrace();
			}
		}

	}

}
