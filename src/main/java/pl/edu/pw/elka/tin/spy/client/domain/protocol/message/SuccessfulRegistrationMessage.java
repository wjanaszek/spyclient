package pl.edu.pw.elka.tin.spy.client.domain.protocol.message;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.Header;

@Data
@RequiredArgsConstructor
public class SuccessfulRegistrationMessage implements Message {
	private final int clientID;
	private Header header = Header.SUCCESSFUL_REGISTRATION;

	@Override
	public Header header() {
		return header;
	}

}
