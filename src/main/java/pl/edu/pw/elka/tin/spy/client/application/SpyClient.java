package pl.edu.pw.elka.tin.spy.client.application;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpyClient {
	static final String SERVER_URL = "localhost";
	static final int SERVER_PORT = 9999;

	static ServerConnection listener;


	public static void main(String... args) {

		listener = new ServerConnection(SERVER_URL, SERVER_PORT);

		start();

	}

	private static void start() {
		listener.run();
	}


}
