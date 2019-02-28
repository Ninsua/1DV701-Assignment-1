/*
 *	READ ME:
 * 		The debug mode is highly recommended due to the infinite loop (specified by Morgan) that will loop the client forever.
 * 		If the debug mode is activated the thread will sleep for 5 seconds before trying to echo again, prints exception stack traces
 * 		and compare send and received packages.
 *		A buffer size smaller than the message will send the entire message
 *		but will only receive as much as the buffer allows (as instructed by Morgan).
 */

package Problem2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import NetworkLayer.NetworkLayer;

public class UDPEchoClient extends NetworkLayer {
	private static final String MSG = "An Echo Message!";

    public static void main(String[] args) {
    	DatagramPacket sendPacket = null;
    	DatagramPacket receivePacket = null;
    	DatagramSocket socket = null;
    	
    	//Checks and sets arguments
    	validArguments(args);
    	
		try {
			/* Set up socket and bind socket */
			SocketAddress localBindPoint = new InetSocketAddress(MYPORT);
			socket = new DatagramSocket(null);	
			socket.bind(localBindPoint);	
			
			socket.setSoTimeout((int)A_SECOND*6);	//Sets timeout of receiving packages to 6 seconds
			
			/* Create datagram packet for receiving echoed message */
			receivePacket = new DatagramPacket(buf, buf.length);
			
			SocketAddress remoteBindPoint = new InetSocketAddress(destinationIP,destinationPort);
			
			sendPacket = new DatagramPacket(MSG.getBytes(),MSG.getBytes().length,remoteBindPoint);
			
			/* Send and receive message*/
			//Stores the received packets
			DatagramPacket[] receivedPackets = new DatagramPacket[transmissionRate];
		
			try {
				while (true) {
					long timerEnd = 0;
					int packetCount = 0;
					long timerStart = System.currentTimeMillis();
					
					//Attempts to send out as many packages as possible during a second, within the transmission rate
					while (timerEnd-timerStart < A_SECOND && packetCount < transmissionRate) {
						socket.send(sendPacket);
						socket.receive(receivePacket);

						receivedPackets[packetCount] = receivePacket;
						packetCount++;
						timerEnd = System.currentTimeMillis();
					}

					/* Compare sent and received message */
						
					//If all packages was sent in a second or less
					double timeToSend = (timerEnd-timerStart)/A_SECOND;
					
					if (packetCount == transmissionRate) {
						if (DEBUG_MODE)
							comparePackets(receivedPackets, packetCount);
						System.out.printf("Sent %d packages in %f seconds on %s port %d \n",packetCount,timeToSend,destinationIP,destinationPort);
						System.out.printf("Echoed message: %s \n", new String(
								receivedPackets[0].getData(),
								receivedPackets[0].getOffset(),
								receivedPackets[0].getLength()));
					} else if (packetCount == 0) {	//If no packages were sent/received
						System.err.println("No packages were sent/received");
					} else {	//If all packages did not get sent within a second
						comparePackets(receivedPackets, packetCount);
						
						int remainingMessages = transmissionRate-packetCount;
						
						System.out.printf(
								"Not all messages were sent. Remaining messages: %d"
								+ "\n"
								+ "Sent %d packages in %f seconds \n",remainingMessages,packetCount,timeToSend);
					}
					
					//If debug mode, sleep for 2 seconds
					if (DEBUG_MODE)
						sleep(5000);
				}
				} catch (IOException e) {
					System.err.printf("Cannot reach %s on port %d \n",destinationIP,destinationPort);
				}
		} catch (SocketException e) {
			System.err.println("Cannot bind to port. Port may already be in use.");
		}
    }
    
    //Compares sent and received message array
    private static void comparePackets(DatagramPacket[] packetCollection, int packetsSent) {
    	for (int i = 0; i < packetsSent; i++) {
			String receivedString =
				    new String(packetCollection[i].getData(),
				    		packetCollection[i].getOffset(),
				    		packetCollection[i].getLength());
			
				if (receivedString.compareTo(MSG) == 0)
				    System.out.printf("Package #%d: %d bytes sent and received\n", i, packetCollection[i].getLength());
				else
				    System.out.printf("Package #%d: Sent and received msg not equal!\n", i);
		}
    }
}