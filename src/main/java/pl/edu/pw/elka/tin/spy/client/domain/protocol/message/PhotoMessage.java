package pl.edu.pw.elka.tin.spy.client.domain.protocol.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.Header;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Data
@AllArgsConstructor
public class PhotoMessage implements Message, SendMessage {
	@Getter
	private Header header = Header.PHOTO;
	private byte[] photo;

	public PhotoMessage(byte[] photo) {
		this.photo = photo;
	}

	@Override
	public Header header() {
		return header;
	}

	@Override
	public byte[] toByteArray() {
		byte[] header = this.header.getValue().getBytes(StandardCharsets.UTF_8);
		int photoSize = photo.length;
		//messageSize = header(3) + photoSize
		int messageSize = 3 + photoSize;

		ByteBuffer bb = ByteBuffer.allocate(messageSizeFieldInBytes + messageSize);
		bb.putInt(messageSize);
		bb.put(header);
		bb.put(this.photo);

		return bb.array();
	}
}
