/*
  UDPEchoServer.java
  A simple echo server with basic error handling
*/

package Problem2;
import java.io.IOException;
import java.net.SocketException;

public class UDPEchoServer extends NetworkLayer {
	
    public static void main(String[] args) {
    	buf= new byte[DEFAULT_BUFSIZE];
    	MYPORT = 4950;
		
		try {
			/* Create socket and bind to port */
			setUpSocket();

			while (true) {
			    /* Create datagram packet for receiving message */
			   receivePacket = setUpPackage(buf, buf.length);
		
			    /* Send message*/
			    try {
				    /* Receiving message */
					socket.receive(receivePacket);
			
				    /* Create datagram packet for sending message */
					
					sendPacket = setUpPackage(receivePacket.getData(),
							receivePacket.getLength(),
							receivePacket.getAddress().toString(),
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