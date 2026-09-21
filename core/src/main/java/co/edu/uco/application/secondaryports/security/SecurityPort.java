package co.edu.uco.application.secondaryports.security;

import java.time.LocalDateTime;

public interface SecurityPort {

    String generateAccessToken(String applicationId, String environmentId, LocalDateTime expirationDate);

    boolean validateAccessToken(String token);
}