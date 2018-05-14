package pl.edu.pw.elka.tin.spy.client.domain.protocol.message;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.Header;

@Data
@RequiredArgsConstructor
public class SuccessfulAuthMessage implements Message {
	private final byte[] secret;
	private Header header = Header.SUCCESSFUL_AUTH;

	@Override
	public Header header() {
		return header;
	}

}
