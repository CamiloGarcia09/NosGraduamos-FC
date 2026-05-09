package co.edu.uco.core.domain.port.out.broker;

import co.edu.uco.core.domain.domains.MessageCodeDomain;
import co.edu.uco.core.domain.port.GenericPort;
import jakarta.servlet.http.HttpServletResponse;

public interface SendMessage extends GenericPort<MessageCodeDomain, HttpServletResponse> {
}