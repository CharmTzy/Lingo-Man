package io.github.some_example_name.lwjgl3.engine.entity;

public interface IEntity {
  long getId();

  boolean isActive();
  void setActive(boolean active);

  default void onAdded() {}
  default void onRemoved() {}
}
