package pl.edu.pw.elka.tin.spy.client.encryption;

import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class XOREncryptor {
	public byte[] encrypt(byte[] message, byte[] secret) {
		if (secret != null) {
			ByteBuffer buffer = ByteBuffer.allocate(message.length);

			int secretLength = secret.length;
			for (int i = 0; i < message.length; i++) {
				byte b = (byte) (message[i] ^ secret[8 % secretLength]);
				buffer.put(b);
			}
			return buffer.array();
		}
		return message;
	}
}
