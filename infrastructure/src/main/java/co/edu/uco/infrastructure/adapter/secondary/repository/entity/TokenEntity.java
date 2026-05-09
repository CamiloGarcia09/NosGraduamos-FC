package co.edu.uco.infrastructure.adapter.secondary.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static co.edu.uco.infrastructure.configuration.InfrastructureConstant.*;
import static co.edu.uco.utils.helper.UtilUUID.getStringToUUID;

@Entity
@Table(name = TOKEN_ENTITY)
@Data
@NoArgsConstructor
@AllArgsConstructor
public final class TokenEntity {
    @Id
    private String id;
    @Column(nullable = false, name = FIELD_CREATION_DATE)
    private LocalDateTime creationDate;
    @Column(nullable = false, name = FIELD_SECRET_NAME)
    private String secretName;
    @Column(name = FIELD_EXPIRATION_DATE)
    private LocalDateTime expirationDate;
    @Column(nullable = false,name = FIELD_ENVIRONMENT_ID)
    private UUID environmentId;
    @Column(name = FIELD_STATE_ID)
    private UUID tokenStateDataId = getStringToUUID(TOKEN_STATE_ACTIVE_ID);
}