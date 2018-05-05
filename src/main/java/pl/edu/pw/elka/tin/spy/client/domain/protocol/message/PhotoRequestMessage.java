package pl.edu.pw.elka.tin.spy.client.domain.protocol.message;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pl.edu.pw.elka.tin.spy.client.domain.protocol.Header;

@Data
@RequiredArgsConstructor
public class PhotoRequestMessage implements Message{

	@Getter
	private Header header = Header.REGISTRATION_REQUEST;

	@Override
	public Header header() {
		return header;
	}
}
