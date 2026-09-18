package com.Rifano.frontend.objects.enemies; // CHANGED (Q1): was "com.Rifano.frontend", now moved into objects.enemies

import com.Rifano.frontend.objects.GameObject;
import com.badlogic.gdx.graphics.Color;

public class Boss extends Enemy {

    public Boss(String name, int hp) {
        super(380, 400, 48, 48, Color.BLUE, name, hp, 5000L);
    }
    public Boss(float x, float y, String name, int hp) {
        super(x, y, 48, 48, Color.BLUE, name, hp, 5000L);
    }

    @Override
    public void onCollision(Collidable other) {
        // TODO: Check whether the other received by this method is a Player
        if (other instanceof Player){
            // TODO: Print "Player touches Boss"
            System.out.println("Player Touches Boss");}
    }

}




