package com.epam.spring.model;

import java.util.UUID;

public class EntityId {

    private UUID uuid = UUID.randomUUID();

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String toString() {
        return "EntityId{" +
                "uuid=" + uuid +
                '}';
    }
}
