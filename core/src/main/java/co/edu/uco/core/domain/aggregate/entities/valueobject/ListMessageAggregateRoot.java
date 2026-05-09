package co.edu.uco.core.domain.aggregate.entities.valueobject;

import co.edu.uco.core.domain.aggregate.AggregateRoot;
import co.edu.uco.core.domain.aggregate.entities.MessageTypeEntity;

import java.util.UUID;

public final class ListMessageAggregateRoot extends AggregateRoot<MessageTypeEntity, UUID> {
}