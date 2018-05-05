package pl.edu.pw.elka.tin.spy.client.domain.protocol.message;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.Header;

@Data
@RequiredArgsConstructor
public class SuccessfulAuthMessage implements Message {
    private Header header = Header.SUCCESSFUL_AUTH;
    private final String secret;

    @Override
    public Header header() {
        return header;
    }

}
