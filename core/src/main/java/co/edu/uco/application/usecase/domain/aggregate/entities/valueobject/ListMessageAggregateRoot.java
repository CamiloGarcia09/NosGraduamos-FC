package co.edu.uco.application.usecase.domain.aggregate.entities.valueobject;

import co.edu.uco.application.usecase.domain.aggregate.AggregateRoot;
import co.edu.uco.application.usecase.domain.aggregate.entities.MessageTypeEntity;

import java.util.UUID;

public final class ListMessageAggregateRoot extends AggregateRoot<MessageTypeEntity, UUID> {
}