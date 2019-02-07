/*
  UDPEchoServer.java
  A simple echo server with basic error handling
*/

package Problem2;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import NetworkLayer.NetworkLayer;

public class UDPEchoServer extends NetworkLayer {
	protected static DatagramPacket sendPacket = null;
	protected static DatagramPacket receivePacket = null;
	protected static DatagramSocket socket = null;
	
    public static void main(String[] args) {
    	buf= new byte[DEFAULT_BUFSIZE];
    	MYPORT = 4950;
    	DatagramPacket sendPacket = null;
    	DatagramPacket receivePacket = null;
    	DatagramSocket socket = null;
		
		try {
			/* Create socket and bind to port */
			SocketAddress localBindPoint = new InetSocketAddress(MYPORT);
			socket = new DatagramSocket(null);	
			socket.bind(localBindPoint);	
			
			while (true) {
			    /* Create datagram packet for receiving message */
			   receivePacket = new DatagramPacket(buf, buf.length);
		
			    /* Send message*/
			    try {
				    /* Receiving message */
					socket.receive(receivePacket);
			
				    /* Create datagram packet for sending message */
					
					sendPacket = new DatagramPacket(receivePacket.getData(),
							receivePacket.getLength(),
							receivePacket.getAddress(),
							receivePacket.getPort()
							);
			    	
					socket.send(sendPacket);

				    System.out.printf("UDP echo request from %s", receivePacket.getAddress().getHostAddress());
				    System.out.printf(" using port %d\n", receivePacket.getPort());
				} catch (IOException e) {
					System.err.printf("Cannot send or receive packages",receivePacket.getAddress(),receivePacket.getPort());
				}
			}
			
		} catch (SocketException e) {
			System.err.printf("Cannot bind to port %d", MYPORT);
		}

    } 
}