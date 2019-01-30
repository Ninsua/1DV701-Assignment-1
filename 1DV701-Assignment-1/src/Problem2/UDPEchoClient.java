/*
  UDPEchoClient.java
  A simple echo client with no error handling
*/

/*
 * QUESTIONS:
 * What do they want us to do with the Client buffer size? Only adjust the buffer which holds the data of the receiving package?
 * Should we do anything more? If the buffer is too small, should we do anything about that?
 * Is the transmission rate OK?
 */

package Problem2;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;

public class UDPEchoClient {
	public static final boolean DEBUG_MODE = true;
    public static final int DEFAULT_BUFSIZE= 1024;
    public static final int MYPORT= 0;
    public static final String MSG= "An Echo Message!"+"An Echo Message!"+"An Echo Message!"+"An Echo Message!"+"An Echo Message!";
    public static final double A_SECOND = 1000.0;

    public static void main(String[] args) {
		byte[] buf = null;
		String destinationIP = "";
		int destinationPort = -1;
		int transmissionRate = 1; //Default transmission rate
		
		//Makes sure IP and port are provided as arguments.
		if (args.length < 2 || args.length > 4) {
		    System.err.printf("Usage: server_name port transfer_rate buffer_size transmission_rate\n");
		    System.exit(1);
		}
		
		//Checks to make sure IP is valid
		destinationIP = args[0];
		if (!validIP(destinationIP)) {
		    System.err.printf("Invalid IP");
		    System.exit(1);
		}
		
		//Checks to make sure port is valid
		if (!validPort(args[1])) {
		    System.err.printf("Invalid port");
		    System.exit(1);
		} else {
			destinationPort = Integer.parseInt(args[1]);	//If port is valid, assign it to port variable
		}	
		
		//If transmission rate is provided, validate it and set it to transmissionRate variable
		if (args.length >= 3) {
			if (!validTransmissionRate(args[2])) {
			    System.err.printf("Invalid transmission rate");
			    System.exit(1);
			} else
				transmissionRate = Integer.parseInt(args[2]);
			if (transmissionRate == 0)	//Makes sure that message is sent once if rate is 0
				transmissionRate = 1;
		}
		
		//If buffer size is provided, validate it and set it to the byte array size
		if (args.length == 4) {
			if (!validBufferSize(args[3])) {
			    System.err.printf("Invalid buffer size");
			    System.exit(1);
			} else
				buf = new byte[Integer.parseInt(args[3])];
		} else {	//If the buffer was not provided, default bufsize will be set
			buf = new byte[DEFAULT_BUFSIZE];
		}
		
		
		try {
			/* Create socket */
			DatagramSocket socket;
			socket = new DatagramSocket(null);
			
			/* Create local endpoint using bind() */
			SocketAddress localBindPoint = new InetSocketAddress(MYPORT);
			socket.bind(localBindPoint);
			socket.setSoTimeout((int)A_SECOND*10);	//Sets timeout of receiving packages to 10 seconds
			
			/* Create remote endpoint */
			SocketAddress remoteBindPoint = new InetSocketAddress(destinationIP,destinationPort);
			
			/* Create datagram packet for sending message */
			DatagramPacket sendPacket =
			    new DatagramPacket(MSG.getBytes(),
					       MSG.length(),
					       remoteBindPoint);
			
			/* Create datagram packet for receiving echoed message */
			DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
			
			/* Send and receive message*/
			//Stores the received packets
			DatagramPacket[] receivedPackets = new DatagramPacket[transmissionRate];
		
			long timerEnd = 0;
			int packetCount = 0;
			long timerStart = System.currentTimeMillis();
			try {
				//Attemps to send out as many packages as possible during a second, within the transmission rate
				while (timerEnd-timerStart < A_SECOND && packetCount < transmissionRate) {
					socket.send(sendPacket);
					socket.receive(receivePacket);

					receivedPackets[packetCount] = receivePacket;
					packetCount++;
					timerEnd = System.currentTimeMillis();
				}
				} catch (IOException e) {
					System.err.printf("Cannot reach %s on port %d \n",destinationIP,destinationPort);
				}
				
			/* Compare sent and received message */
	
			//If all packages was sent in a second or less
			double timeToSend = (timerEnd-timerStart)/A_SECOND;
			
			if (packetCount == transmissionRate) {
				comparePackets(receivedPackets, packetCount);
				System.out.printf("Sent %d packages in %f seconds on %s port %d \n",packetCount,timeToSend,destinationIP,destinationPort);
				
			} else if (packetCount == 0) {	//If no packages were sent/received
				System.err.println("No packages were sent/received");
			} else {	//If all packages did not get sent within a second
				comparePackets(receivedPackets, packetCount);
				
				int remainingMessages = transmissionRate-packetCount;
				
				System.out.printf(
						"Not all messages were sent. Remaining messages: %d"
						+ "\n"
						+ "Sent %d packages in %f seconds",remainingMessages,packetCount,timeToSend);
			}

			socket.close();
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
			
				if (receivedString.compareTo(MSG) == 0 && DEBUG_MODE)
				    System.out.printf("Package #%d: %d bytes sent and received\n", i, packetCollection[i].getLength());
				else if (DEBUG_MODE)
				    System.out.printf("Package #%d: Sent and received msg not equal!\n", i);
		}
    }
    
    //Makes a basic check to see if provided IP is valid or not
    private static boolean validIP(String IP) {
    	String[] IPSplit = IP.split("\\.");
    	
    	if (IPSplit.length != 4)
    		return false;
    
    	for (int i = 0; i < IPSplit.length; i++) {
    		try {
    			int dottedDecimal = Integer.parseInt(IPSplit[i]);
    			if (dottedDecimal < 0 || dottedDecimal > 255)
    				return false;
    		} catch (NumberFormatException e) {
    			return false;	//If the IP is not parsable to int, valid = false.
    		}
    	}
    	return true;
    }
    
    //Makes a basic check to see if provided port is valid or not
    private static boolean validPort(String port) {
    	try {
    		int portAsInteger = Integer.parseInt(port);
    		if (portAsInteger < 0 || portAsInteger > 65535) //Port cannot be less than 0 or more than 65535
    			return false;
    	} catch (NumberFormatException e) {
    		return false;	//If the port is not parsable to int, valid = false.
    	}
    	return true;
    }
    
    //Makes a basic check to see if provided transmission rate is valid or not
    private static boolean validTransmissionRate(String rate) {
    	try {
    		int properRate = Integer.parseInt(rate);
    		if (properRate < 0) {
    			return false;
    		}
    	} catch (NumberFormatException e) {
    		return false;
    	}
    	return true;
    }
    
    //Makes a basic check to see if provided buffer size is valid or not
    private static boolean validBufferSize(String bufferSize) {
    	try {
    		int properBufSize = Integer.parseInt(bufferSize);
    		
    		//Maximum size for IPv4 data gram (16 bits - 20 bytes IPv4 header and 8 bits UDP header)
    		if (properBufSize <= 0 || properBufSize > 65515) {
    			return false;
    		}
    	} catch (NumberFormatException e) {
    		return false;
    	}
    	return true;
    }
    

}