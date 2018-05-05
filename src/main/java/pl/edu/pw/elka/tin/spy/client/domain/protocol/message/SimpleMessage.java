package pl.edu.pw.elka.tin.spy.client.domain.protocol.message;

import lombok.Getter;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.Header;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class SimpleMessage implements Message, SendMessage {
    @Getter
    private Header header;

    public static SimpleMessage PhotoRequest = new SimpleMessage(Header.PHOTO_REQUEST);
    public static SimpleMessage UnrecognisedHeader = new SimpleMessage(Header.UNRECOGNISED);

    public SimpleMessage(Header header) {
        this.header = header;
    }


    @Override
	public byte[] toByteArray() {
		byte[] header = this.header.getValue().getBytes(StandardCharsets.UTF_8);
		ByteBuffer bb = ByteBuffer.allocate(messageSizeFieldInBytes + header.length);
		bb.putInt(header.length);
		bb.put(header);
		return bb.array();
	}

	@Override
	public Header header() {
		return header;
	}
}
