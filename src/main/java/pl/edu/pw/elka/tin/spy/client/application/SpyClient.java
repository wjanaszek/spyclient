package pl.edu.pw.elka.tin.spy.client.application;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpyClient {
	static String SERVER_URL = "localhost";
	static int SERVER_PORT = 9999;

	static ServerConnection listener;


	public static void main(String... args) {

		if (args.length == 2) {
			SERVER_URL = args[0];
			SERVER_PORT = Integer.valueOf(args[1]);
		}

		listener = new ServerConnection(SERVER_URL, SERVER_PORT);

		start();

	}

	private static void start() {
		listener.run();
	}


}
