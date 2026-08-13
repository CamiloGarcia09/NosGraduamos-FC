package co.edu.uco.application.secondaryports.broker;

import co.edu.uco.application.usecase.domain.MessageCodeDomain;
import co.edu.uco.application.secondaryports.GenericPort;
import jakarta.servlet.http.HttpServletResponse;

public interface SendMessage extends GenericPort<MessageCodeDomain, HttpServletResponse> {
}