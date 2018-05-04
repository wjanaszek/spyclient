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
        ByteBuffer bb = ByteBuffer.allocate(7);
        bb.putInt(3);
        bb.put(header.getValue().getBytes(StandardCharsets.UTF_8));
        return bb.array();
    }
}
