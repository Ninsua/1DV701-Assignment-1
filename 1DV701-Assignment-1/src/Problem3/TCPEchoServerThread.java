package Problem3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import NetworkLayer.NetworkLayer;

public class TCPEchoServerThread extends NetworkLayer implements Runnable  {
	
	private Socket client;
	
	public TCPEchoServerThread(Socket newClient, int bufferSize, boolean debug) {
		client = newClient;
		buf = new byte[bufferSize];
		DEBUG_MODE = debug;
	}

	@Override
	public void run() {
			try {
				int readBytes = 0;

				/*
				 * Read until all message has been read.
				 * If buffer size is too small, fill buffer,
				 * write to output stream then write over old data in buffer
				 */
				
				InputStream readStream = client.getInputStream();
				OutputStream writeStream = client.getOutputStream();
				
				while (readBytes != -1) {
					readBytes = readStream.read(buf);
					
					if (DEBUG_MODE)
						System.out.println("Read "+readBytes+" into buffer.");
					
					if (readBytes > 0) {
						writeStream.write(buf,0,readBytes);
						
						if (DEBUG_MODE)
							System.out.println("Trying to write "+readBytes+" bytes");
					}
				}
				
				//Wait for client to close connection. Wait for a maximum of 6 seconds.
				boolean openConnection = true;
				int waitedTime = 0;
				while(openConnection && waitedTime<A_SECOND*6) {
					try {
						Thread.sleep(1);
						if (readStream.read() == -1)
							openConnection = false;
						waitedTime++;
					} catch (InterruptedException | IOException e) {
						break;
					}
				}
				
				System.out.printf("Sucessfully delivered the message to %s port %d on Thread %s \n"
						,client.getInetAddress().getHostAddress()
						,client.getPort()
						,Thread.currentThread().getName());
			} catch (IOException e) {
				if (DEBUG_MODE)
					e.printStackTrace();
				System.err.printf("Could not send data to %s on port %d \n",client.getInetAddress().getHostAddress(),client.getPort());
			} finally {
				try {
					client.close();
					if (DEBUG_MODE)
						System.out.println("Client socket successfully closed.");
				} catch (IOException e) {
					if (DEBUG_MODE) {
						e.printStackTrace();
						System.out.println("Client socket could not be closed properly.");
					}
				}
			}
	}
}