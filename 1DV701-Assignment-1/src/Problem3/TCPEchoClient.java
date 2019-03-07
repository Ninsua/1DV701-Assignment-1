/*	READ ME:
 * 		The program takes the following arguments: server_name port transmission_rate buffer_size -debug
 * 		
 */

package Problem3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import NetworkLayer.NetworkLayer;

public class TCPEchoClient extends NetworkLayer {
	private static String MSG = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. "
			+ "Sed viverra, justo eget ultrices dignissim, "
			+ "leo nunc egestas erat, eget aliquet urna mauris nec nisi."+"\n";
	
	private static String recievedMSG = "";
	
	@SuppressWarnings("resource")	//Because client is supposed to loop infinitely (according to Morgan)
	public static void main(String[] args) {
		Socket socket = null;
		
		//Checks and sets arguments
		validArguments(args);
		
		try {
			int rateCount = 0;
			
			while (transmissionRate > 0 || rateCount < 1) {
				/* Set up socket*/
				socket = new Socket();
				
				//Bind to ephemeral local port
				socket.bind(null);
				
				//Three way handshake, set timeout to 6 seconds
				socket.connect(new InetSocketAddress(destinationIP,destinationPort),(int)A_SECOND*6);
				
				//Save streams
				InputStream readStream = socket.getInputStream();
				OutputStream writeStream = socket.getOutputStream();
				
				byte[] messageBytes = MSG.getBytes();
				
				//Attempts to send out as many packages as possible during a second, within the transmission rate
				long timerEnd = 0;
				long timerStart = System.currentTimeMillis();
				
				rateCount = 0;
				int bytesSent = 0;
				while ((timerEnd-timerStart < A_SECOND && rateCount < transmissionRate) || (rateCount < 1)) {
					//Write whole message to stream
					writeStream.write(messageBytes,0,messageBytes.length);
					bytesSent = messageBytes.length;
					
					//Read bytes from stream until the whole message has been read
					recievedMSG = "";
					int readBytes = 0;
					
					while (readBytes != -1 && recievedMSG.getBytes().length < bytesSent) {
						readBytes = readStream.read(buf);
						
						if (DEBUG_MODE)
							System.out.println("Read "+readBytes+" into buffer.");
						
						if (readBytes > 0) {
							//Reconstruct string from buffer
							recievedMSG += new String(buf,0,readBytes);
						}
					}

					//Prints echoed message
					System.out.println(recievedMSG);
					
					timerEnd = System.currentTimeMillis();
					
					rateCount++;
				}
				
				//Calculate time to send
				double timeToSend = (timerEnd-timerStart)/A_SECOND;
				
				//Check if all of the transmission rate could be sent within one second, if not, print stats
				if (!(rateCount*messageBytes.length == (transmissionRate == 0 ? 1 : transmissionRate)*messageBytes.length)) {
					System.out.printf("Not all messages were sent/received. Remaining bytes: %d \n",(transmissionRate == 0 ? 1 : transmissionRate)*messageBytes.length-rateCount*messageBytes.length);
				}
				
				//Print stats
				System.out.printf("Sent %d bytes in %f seconds on %s port %d \n",bytesSent,timeToSend,destinationIP,destinationPort);
				

				//Closes socket and streams.
				writeStream.close();
				readStream.close();
				socket.close();
				
				//Sleep for the remaining time of the second
				sleep((int) Math.round(A_SECOND-timeToSend));
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
				System.err.println("Error closing the connection.");
			}
		}

	}

}
