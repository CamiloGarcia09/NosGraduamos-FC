package co.edu.uco.core.domain.aggregate.entities;

import co.edu.uco.core.domain.aggregate.Entity;
import lombok.Getter;

import java.util.UUID;

@Getter
public final class MessageStatusEntity extends Entity<UUID> {
    private UUID id;
    private String name;
}