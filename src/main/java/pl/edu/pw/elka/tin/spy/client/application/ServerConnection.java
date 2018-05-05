package pl.edu.pw.elka.tin.spy.client.application;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ServerConnection implements Runnable{

	private Socket taskSocket;

	private ThreadPoolExecutor poolExecutor;

	private final ConcurrentLinkedQueue<byte[]> queue = new ConcurrentLinkedQueue<>();

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

	}

}
