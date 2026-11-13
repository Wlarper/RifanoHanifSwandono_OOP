package com.Rifano.frontend.objects.patterns.entityStrategy;

import com.Rifano.frontend.objects.GameObject;

public interface EntityMovementPattern {
    void move(GameObject entity, float delta);
}
