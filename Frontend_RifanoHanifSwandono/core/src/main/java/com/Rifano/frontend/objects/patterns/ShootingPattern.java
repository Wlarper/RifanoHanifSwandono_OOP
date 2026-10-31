package com.Rifano.frontend.objects.patterns;
import com.Rifano.frontend.systems.BulletManager;

public interface ShootingPattern {
    void execute(float originX, float originY, BulletManager bulletManager, boolean isPlayer);
}
