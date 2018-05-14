package pl.edu.pw.elka.tin.spy.client.application;

import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@NoArgsConstructor
public class SocketIO {
	private Socket socket;

	private InputStream input;

	private OutputStream output;

	public void connect(String ip, int port) throws IOException {
		close();
		socket = new Socket(ip, port);
		input = socket.getInputStream();
		output = socket.getOutputStream();
	}

	public boolean isConnected() {
		if (socket == null) {
			return false;
		} else if (!socket.isClosed()) {
			return true;
		} else {
			socket = null;
			return false;
		}
	}

	public void close() throws IOException {
		if (socket != null) {
			socket.close();
		}
		if (input != null) {
			input.close();
		}
		if (output != null) {
			output.close();
		}
	}


	public void read(byte[] bytes) throws IOException {
		if (!isConnected()) {
			throw new IOException("Socket not connected");
		}
		input.read(bytes);

	}

	public int read(byte[] bytes, int i, int remaining) throws IOException {
		if (!isConnected()) {
			throw new IOException("Socket not connected");
		}
		return input.read(bytes, i, remaining);
	}

	public void write(byte[] bytes) throws IOException {
		if (!isConnected()) {
			throw new IOException("Socket not connected");
		}
		output.write(bytes);
	}

	public void flush() throws IOException {
		if (!isConnected()) {
			throw new IOException("Socket not connected");
		}
		output.flush();
	}
}
