package io.github.some_example_name.lingoman;

import io.github.some_example_name.collision.Collider;
import io.github.some_example_name.collision.ICollisionFilter;
import io.github.some_example_name.lingoman.entity.GhostEntity;
import io.github.some_example_name.lingoman.entity.LetterEntity;
import io.github.some_example_name.lingoman.entity.PlayerEntity;
import io.github.some_example_name.lingoman.entity.WallEntity;

public class LingoCollisionFilter implements ICollisionFilter {

    @Override
    public boolean canCollide(Collider a, Collider b) {
        if (a == null || b == null) {
            return false;
        }
        Object oa = a.getOwner();
        Object ob = b.getOwner();
        if (oa == null || ob == null) {
            return false;
        }

        if (oa instanceof WallEntity && ob instanceof WallEntity) {
            return false;
        }
        if (oa instanceof GhostEntity && ob instanceof GhostEntity) {
            return false;
        }
        if (oa instanceof LetterEntity && ob instanceof LetterEntity) {
            return false;
        }

        if (oa instanceof LetterEntity && !(ob instanceof PlayerEntity)) {
            return false;
        }
        if (ob instanceof LetterEntity && !(oa instanceof PlayerEntity)) {
            return false;
        }

        return true;
    }
}