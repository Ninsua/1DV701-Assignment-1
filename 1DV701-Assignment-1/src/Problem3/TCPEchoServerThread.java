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
				int readBytes = 1;
				int bufferOffset = 0;
				
				//Set socket timeout to 10 seconds
				client.setSoTimeout((int)A_SECOND*10);
				
				/*
				 * Read until all message has been read.
				 * If buffer size is too small, fill buffer,
				 * write to output stream then write over old data in buffer
				 */
				
				InputStream readStream = client.getInputStream();
				OutputStream writeStream = client.getOutputStream();
				
				while (readStream.available() != 0 && readBytes != -1) {
					if (DEBUG_MODE)
						System.out.println("Trying to read "+(buf.length-bufferOffset)+" bytes to offset "+bufferOffset);
					readBytes = readStream.read(buf,bufferOffset,buf.length-bufferOffset);
					
					if (readBytes > 0) {
						if (DEBUG_MODE)
							System.out.println("Trying to write "+readBytes+" bytes starting at offset "+bufferOffset);
						writeStream.write(buf,bufferOffset,readBytes);
						bufferOffset = bufferOffset+readBytes;
						
						if (bufferOffset >= buf.length || bufferOffset+readStream.available() >= buf.length)
							bufferOffset = 0;
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
					if (DEBUG_MODE)
						e.printStackTrace();
				}
			}
	}
}