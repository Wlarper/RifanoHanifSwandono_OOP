package com.Rifano.frontend.objects.patterns.bulletStrategy;

import com.Rifano.frontend.objects.bullets.Bullet;

public interface BulletMovementPattern {
    void move(Bullet bullet, float delta);
}
