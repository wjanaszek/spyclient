package pl.edu.pw.elka.tin.spy.client.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@AllArgsConstructor
public class ReaderThread implements Runnable {
	private Socket socket;
	private final ConcurrentLinkedQueue<byte[]> rawMessageQueue;
	private DataInputStream inputStream;

	public ReaderThread(Socket inSocket, ConcurrentLinkedQueue<byte[]> inQueue) {
		this.socket = inSocket;
		this.rawMessageQueue = inQueue;

		try {
			this.inputStream = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			log.info("Starting Reader Thread");
			try {
				int messageLength = inputStream.readInt();
				if(messageLength > 0) {
					byte[] message = new byte[messageLength];
					inputStream.readFully(message, 0, message.length);
					rawMessageQueue.add(message);

				}
			} catch (IOException e) {
				throw new RuntimeException("Failed to get input stream");
			}
		}
	}
}
