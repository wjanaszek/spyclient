package pl.edu.pw.elka.tin.spy.client.domain.protocol.message;

public interface SendMessage {
	int messageSizeFieldInBytes = 4;

	byte[] toByteArray();
}
