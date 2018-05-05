package pl.edu.pw.elka.tin.spy.client.domain.protocol;

import pl.edu.pw.elka.tin.spy.client.domain.protocol.message.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RawMessageParser {
    public static Message parse(byte[] message) {
        ByteBuffer bb = ByteBuffer.wrap(message);

        Header messageHeader = readHeader(bb);

        switch (messageHeader) {
            case PHOTO_REQUEST:
                return new PhotoRequestMessage();
            case SUCCESSFUL_REGISTRATION:
                int clientId = readInt(bb);
                return new SuccessfulRegistrationMessage(clientId);
            case SUCCESSFUL_AUTH:
                String secret = readString(bb);
                return new SuccessfulAuthMessage(secret);
            default:
                return SimpleMessage.UnrecognisedHeader;

        }
    }

    private static int readInt(ByteBuffer bb) {
        byte[] rawInt = new byte[4];
        bb.get(rawInt,0,4);
        return ByteBuffer.wrap(rawInt).getInt();
    }

    private static String readString(ByteBuffer bb) {
        int nameSize = bb.getInt();
        byte[] rawName = new byte[nameSize];
        bb.get(rawName, 0, nameSize);

        return new String(rawName);
    }

    private static Header readHeader(ByteBuffer bb) {
        byte[] rawHeader = new byte[3];
        bb.get(rawHeader, 0, 3);
        return Header.fromString(new String(rawHeader, StandardCharsets.UTF_8));
    }
}
