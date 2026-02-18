package io.github.some_example_name.lwjgl3.engine.entity;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Entity implements IEntity {
    private static final AtomicLong NEXT_ID = new AtomicLong(1);

    private final long id = NEXT_ID.getAndIncrement();
    private boolean active = true;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
