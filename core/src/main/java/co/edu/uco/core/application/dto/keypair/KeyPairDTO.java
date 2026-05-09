package co.edu.uco.core.application.dto.keypair;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.security.PrivateKey;
import java.security.PublicKey;

@Getter
@Setter
@AllArgsConstructor
@Builder
public final class KeyPairDTO {
    private PublicKey publicKey;
    private PrivateKey privateKey;
}