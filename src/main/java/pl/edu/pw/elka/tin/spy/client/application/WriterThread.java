package pl.edu.pw.elka.tin.spy.client.application;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.*;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.Header;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.RawMessageParser;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.message.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@AllArgsConstructor
public class WriterThread implements Runnable{
	private Socket socket;
	private DataOutputStream outputStream;
	private final ConcurrentLinkedQueue<byte[]> rawMessageQueue;
	private Queue<Message> outputMessageQueue;

	public WriterThread(Socket outSocket, ConcurrentLinkedQueue<byte[]> outQueue) {
		this.socket = outSocket;
		this.rawMessageQueue = outQueue;
		this.outputMessageQueue = new LinkedList<>();

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
		if (!isRegistered())
			sendMessage(new RegistrationRequestMessage("test1","test"));
		else
			sendMessage(new AuthRequestMessage(clientId(),"test"));

		//register("test5", "test");

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

	private void createPropertiesFile() {
		String propertiesPath = getPropertiesDirectoryPath() + File.separator + "client.properties";
		File propertiesFile = new File(propertiesPath);
		try {
			propertiesFile.createNewFile();
		} catch (IOException e) {
			log.error("Problem accessing properties file");
		}
	}

	private void handleMessage(Message message) {
		switch (message.header()){
			case PHOTO_REQUEST: {
				log.info("Get PHOTO message, sending photo to server");
				byte[] photo = takePhoto();
				sendMessage(new PhotoMessage(Header.PHOTO,photo));
			}
			case SUCCESSFUL_REGISTRATION: {
				log.info("Get REGISTRATION_SUCCESSFUL message, saving registration info");
				SuccessfulRegistrationMessage successMessage = (SuccessfulRegistrationMessage)message;
				int clientId = successMessage.getClientID();
				saveAsRegistered(clientId);
			}
			case SUCCESSFUL_AUTH:{
				log.info("Get SUCCESSFUL_AUTH message");
				SuccessfulAuthMessage successMessage = (SuccessfulAuthMessage)message;
				String secret = successMessage.getSecret();
				saveAsActive(secret);
			}
		}
	}

	private void saveAsActive(String secret) {
		setProperty("secret",secret);
	}

	private byte[] takePhoto() {
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

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
		}

		return image;
	}

	private void saveAsRegistered(int clientId) {
		setProperty("clientId",Integer.toString(clientId));
		setProperty("registered", "true");
	}

	private void setProperty(String name, String value){
		String propertiesPath = getPropertiesDirectoryPath() + File.separator + "client.properties";
		Properties properties = new Properties();

		FileOutputStream output = null;
		try {
			properties.setProperty(name, value);
			File file = new File(propertiesPath);
			output = new FileOutputStream(propertiesPath,true);
			properties.store(output,null);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String getPropertiesDirectoryPath(){
		String homeDirectory = System.getProperty("user.home");
		String propertiesDirectory = homeDirectory + File.separator + "SpyClient";
		File newDirectory = new File(propertiesDirectory);
		newDirectory.mkdir();

		return propertiesDirectory;
	}

	private void sendMessage(SendMessage message) {
		try {
			outputStream.write(message.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			log.error("Failed to send message");
		}
	}

	private boolean isRegistered() {
		String property = getProperty("registered");
		if (property == null)
			return false;
		if (property.equals("true"))
			return true;
		return false;
	}

	private int clientId() {
		return Integer.parseInt(getProperty("clientId"));
	}

	private String getProperty(String propertyName){
		String propertiesPath = getPropertiesDirectoryPath() + File.separator + "client.properties";
		Properties properties = new Properties();
		String property = null;
		InputStream input = null;
		try {
			input = new FileInputStream(propertiesPath);
			properties.load(input);
			property = properties.getProperty(propertyName);
		} catch (FileNotFoundException e) {
			log.debug("Properties file doesn't exist");
		} catch (IOException e) {
			log.debug("Problem accessing properties file");
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return property;
	}

}
