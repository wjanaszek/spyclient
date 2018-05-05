package pl.edu.pw.elka.tin.spy.client.domain.protocol.message;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.Header;

@Data
@RequiredArgsConstructor
public class SuccessfulRegistrationMessage implements Message {
    private Header header = Header.SUCCESSFUL_REGISTRATION;
    private final int clientID;

    @Override
    public Header header() {
        return header;
    }

}
