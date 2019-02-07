package Problem3;

import java.io.IOException;
import java.net.Socket;

import Problem2.NetworkLayer;

public class TCPEchoServerThread extends NetworkLayer implements Runnable  {
	
	private Socket client;
	
	public TCPEchoServerThread(Socket newClient) {
		client = newClient;
		buf = new byte[DEFAULT_BUFSIZE];
	}

	@Override
	public void run() {
			try {
				System.out.println("Bytes read: " + client.getInputStream().read(buf));
				System.out.println("Buffer size: "+buf.length);
				client.getOutputStream().write(buf);

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