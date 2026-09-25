package com.Rifano.frontend.objects.bullets;

import com.Rifano.frontend.objects.Collidable;
import com.Rifano.frontend.objects.GameObject;
import com.Rifano.frontend.objects.BulletType;
import com.Rifano.frontend.objects.enemies.Enemy;
import com.badlogic.gdx.graphics.Color;

public class Bullet extends GameObject {
    private BulletType bulletType;
    private int damage;

    // Constructor with default speed (400f)
    public Bullet(float x, float y, BulletType bulletType, int damage) {
        super(x, y, 8, 16, 400f, Color.YELLOW);
        this.bulletType = bulletType;
        this.damage = damage;
    }

    // Overloaded constructor with custom speed
    public Bullet(float x, float y, float speed, BulletType bulletType, int damage) {
        super(x, y, 8, 16, speed, Color.YELLOW);
        this.bulletType = bulletType;
        this.damage = damage;
    }

    @Override
    public void update(float delta) {
        this.y += this.speed * delta;
    }

    public BulletType getBulletType() {
        return bulletType;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Enemy enemy) {
            System.out.println("Bullet hit" + enemy.getName() + "Bullet hit" + damage );
            enemy.takeDamage(damage);
            destroy();

        }
    }


}
