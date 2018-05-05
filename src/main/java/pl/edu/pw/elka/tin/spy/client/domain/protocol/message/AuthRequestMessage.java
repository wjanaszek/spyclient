package pl.edu.pw.elka.tin.spy.client.domain.protocol.message;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.Header;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Data
@RequiredArgsConstructor
public class AuthRequestMessage implements Message, SendMessage{
    @Getter private Header header = Header.AUTHENTICATION_REQUEST;
    private final int userID;
    private final String password;

    @Override
    public Header header() {
        return header;
    }

    @Override
    public byte[] toByteArray() {
        byte[] header = this.header.getValue().getBytes(StandardCharsets.UTF_8);
        byte[] password = this.password.getBytes(StandardCharsets.UTF_8);
        int passwordSize = password.length;
        //messageSize = header(3) + int for clientId + int for password length + password
        int messageSize = 3 + 4 + 4 + passwordSize;

        ByteBuffer bb = ByteBuffer.allocate(messageSizeFieldInBytes + messageSize);
        bb.putInt(messageSize);
        bb.put(header);
        bb.putInt(userID);
        bb.putInt(passwordSize);
        bb.put(password);

        return bb.array();
    }
}
