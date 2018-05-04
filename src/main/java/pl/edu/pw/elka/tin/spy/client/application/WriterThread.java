package pl.edu.pw.elka.tin.spy.client.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.Header;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.message.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Slf4j
@AllArgsConstructor
public class WriterThread implements Runnable{
	private Socket socket;
	private DataOutputStream outputStream;
	private final Queue<byte[]> rawMessageQueue;
	private Queue<Message> outputMessageQueue;

	public WriterThread(Socket outSocket, Queue<byte[]> outQueue) {
		this.socket = outSocket;
		this.rawMessageQueue = outQueue;

		try {
			this.outputStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		log.info("Starting Writer Thread");
		while(true){
			if (rawMessageQueue.size() > 0) {
				List<byte[]> rawMessages = new LinkedList<>();
				while (rawMessageQueue.size() > 0) {
					rawMessages.add(rawMessageQueue.poll());
				}
				rawMessages.stream()
						.map(RawMessageParser::parse)
						.forEach(outputMessageQueue::add);
			}

			Message newMessage = outputMessageQueue.poll();

			if (newMessage != null) {
				handleMessage(newMessage);
			}
		}
	}

	private void handleMessage(Message message) {
		if (message instanceof SimpleMessage
				&& ((SimpleMessage) message).getHeader().equals(Header.PHOTO_REQUEST)) {
			PhotoMessage photo = (PhotoMessage) message;
//			try {
//				takePhoto();
//			} catch (IOException e) {
//				e.printStackTrace();
//				log.error("Failed to save photo");
//			}
//		} else if (message instanceof SendMessage) {
//			sendMessage((SendMessage)message);
		}
	}

	private void takePhoto() {

	}

	private void sendMessage(SendMessage message) {
		try {
			outputStream.write(message.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Failed to send message");
		}
	}
}
