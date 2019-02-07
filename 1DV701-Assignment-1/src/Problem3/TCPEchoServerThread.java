package Problem3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import NetworkLayer.NetworkLayer;

public class TCPEchoServerThread extends NetworkLayer implements Runnable  {
	
	private Socket client;
	
	public TCPEchoServerThread(Socket newClient, int bufferSize) {
		client = newClient;
		buf = new byte[bufferSize];
	}

	@Override
	public void run() {
			try {
				int readBytes = 1;
				int bufferOffset = 0;
				
				/*
				 * Read until all message has been read.
				 * If buffer size is too small, fill buffer,
				 * write to output stream then write over old data in buffer
				 */
				
				InputStream readStream = client.getInputStream();
				OutputStream writeStream = client.getOutputStream();;
				
				while (readStream.available() != 0 && readBytes > 0) {
					System.out.println("Trying to read "+(buf.length-bufferOffset)+" bytes to offset "+bufferOffset);
					readBytes = readStream.read(buf,bufferOffset,buf.length-bufferOffset);
					System.out.println("Bytes read: "+readBytes);
					
					System.out.println("Trying to write "+readBytes+" starting at offset "+bufferOffset);
					writeStream.write(buf,bufferOffset,readBytes);
					bufferOffset = bufferOffset+readBytes;
					
					if (bufferOffset >= buf.length || bufferOffset+readStream.available() >= buf.length)
						bufferOffset = 0;
					System.out.println("bufferOffset: "+bufferOffset);
				}
				
				
				
				client.close();
				System.out.printf("Sucessfully delivered the message to %s port %d on Thread %s \n"
						,client.getInetAddress().getHostAddress()
						,client.getPort()
						,Thread.currentThread().getName());
			} catch (IOException e) {
				System.err.printf("Could not send data to %s on port %d \n",client.getInetAddress().getHostAddress(),client.getPort());
			}
	}
}