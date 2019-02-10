package NetworkLayer;

public abstract class NetworkLayer {
	protected static boolean DEBUG_MODE = false;	//Shows packet comparison
	protected static final int DEFAULT_BUFSIZE = 1024;
	protected static final int DEFAULT_SERVER_PORT = 4950;
	protected static int MYPORT = 0;
	protected static String destinationIP;
	protected static byte[] buf = null;
	protected static int destinationPort = -1;
	protected static int transmissionRate = 1; //Default transmission rate
	protected static final double A_SECOND = 1000.0;
	
	protected static void validArguments(String[] arguments) {
		//Makes sure IP and port are provided as arguments.
		if (arguments.length < 2 || arguments.length > 5) {
		    System.err.printf("Usage: server_name port transfer_rate buffer_size -debug\n");
		    System.exit(1);
		}
		
		//Checks to make sure IP is valid
		destinationIP = arguments[0];
		if (!validIP(destinationIP)) {
		    System.err.printf("Invalid IP");
		    System.exit(1);
		}
		
		//Checks to make sure port is valid
		if (!validPort(arguments[1])) {
		    System.err.printf("Invalid port");
		    System.exit(1);
		} else {
			destinationPort = Integer.parseInt(arguments[1]);	//If port is valid, assign it to port variable
		}	
		
		//If transmission rate is provided, validate it and set it to transmissionRate variable
		if (arguments.length >= 3) {
			if (!validTransmissionRate(arguments[2])) {
			    System.err.printf("Invalid transmission rate");
			    System.exit(1);
			} else
				transmissionRate = Integer.parseInt(arguments[2]);
			
			if (transmissionRate == 0)	//Makes sure that message is sent once if rate is 0
				transmissionRate = 1;
		}
		
		//If buffer size is provided, validate it and set it to the byte array size
		if (arguments.length >= 4) {
			if (!validBufferSize(arguments[3])) {
			    System.err.printf("Invalid buffer size");
			    System.exit(1);
			} else
				buf = new byte[Integer.parseInt(arguments[3])];
			
		} else {	//If the buffer was not provided, default bufsize will be set
			buf = new byte[DEFAULT_BUFSIZE];
		}
		
		//Checks debug mode
		if (arguments.length >= 5) {
			if (validDebugArgument(arguments[4])) {
				DEBUG_MODE = true;
			}
		}
	}
	
    //Makes a basic check to see if provided IP is valid or not
    protected static boolean validIP(String IP) {
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
    protected static boolean validPort(String port) {
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
    protected static boolean validTransmissionRate(String rate) {
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
    protected static boolean validBufferSize(String bufferSize) {
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
    
    protected static boolean validDebugArgument(String debugString) {
		if (!("-debug".compareTo(debugString) == 0)) {
			return false;
		}
		return true;
    }
    
    protected static int stringToInt(String input) {
    	try {
    		return Integer.parseInt(input);
    	} catch (NumberFormatException e) {
    		return -1;
    	}
    }
    
    @SuppressWarnings("unused")	//Only unused on the abstract class
	protected static void sleep()  {
    	try {
    		Thread.sleep(5000);
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}
    }
}
