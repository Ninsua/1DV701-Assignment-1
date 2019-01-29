/*
  UDPEchoClient.java
  A simple echo client with no error handling
*/

package Problem2;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UDPEchoClient {
    public static final int BUFSIZE= 1024;
    public static final int MYPORT= 0;
    public static final String MSG= "An Echo Message!";
    public static final double A_SECOND = 1000.0;

    public static void main(String[] args) throws IOException {
		byte[] buf= new byte[BUFSIZE];
		String destinationIP = "";
		int port = -1;
		int transmissionRate = 1; //Default transmission rate
		
		//Makes sure IP and port are provided as arguments
		if (args.length < 2 || args.length > 4) {
		    System.err.printf("Usage: server_name port transfer_rate\n");
		    System.exit(1);
		}
		
		//If transmission rate is provided, validate it and set it to transmissionRate variable
		if (args.length == 3) {
			if (!validTransmissionRate(args[2])) {
			    System.err.printf("Invalid transmission rate");
			    System.exit(1);
			} else
				transmissionRate = Integer.parseInt(args[2]);
			if (transmissionRate == 0)	//Makes sure that message is sent once if rate is 0
				transmissionRate = 1;
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
			port = Integer.parseInt(args[1]);	//If port is valid, assign it to port variable
		}
		
		
		/* Create socket */
		DatagramSocket socket= new DatagramSocket(null);
		
		/* Create local endpoint using bind() */
		SocketAddress localBindPoint = new InetSocketAddress(MYPORT);
		socket.bind(localBindPoint);
		
		/* Create remote endpoint */
		SocketAddress remoteBindPoint = new InetSocketAddress(destinationIP,port);
		
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
		//Attemps to send out as many packages as possible during a second, within the transmission rate
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
			comparePackets(receivedPackets, packetCount);
			System.out.printf("Sent %d packages in %f seconds",packetCount,timeToSend);
			
		} else {	//If all packages did not get sent within a second
			comparePackets(receivedPackets, packetCount);
			
			int remainingMessages = transmissionRate-packetCount;
			
			System.out.printf(
					"Not all messages were sent. Remaining messages: %d"
					+ "\n"
					+ "Sent %d packages in %f seconds",remainingMessages,packetCount,timeToSend);
		}

		socket.close();
    }
    
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
}