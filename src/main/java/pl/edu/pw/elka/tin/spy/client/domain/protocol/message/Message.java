package pl.edu.pw.elka.tin.spy.client.domain.protocol.message;

import pl.edu.pw.elka.tin.spy.client.domain.protocol.Header;

public interface Message {
	int intFieldInBytes = 4;

	Header header();
}
