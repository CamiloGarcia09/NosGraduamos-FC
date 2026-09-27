package co.edu.uco.application.usecase.security;

import co.edu.uco.application.usecase.domain.security.MessageAccessContext;
import co.edu.uco.application.usecase.domain.security.PermissionCode;

public interface MessageEnvironmentResolver {

    String resolve(MessageAccessContext context, PermissionCode permission);
}
