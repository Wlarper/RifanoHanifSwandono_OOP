package com.Rifano.frontend;

import com.badlogic.gdx.graphics.Color;

public class Boss extends Enemy {

    public Boss(String name, int hp) {
        super(380, 400, 48, 48, Color.BLUE, name, hp, 5000L);
    }
    public Boss(float x, float y, String name, int hp) {
        super(x, y, 48, 48, Color.BLUE, name, hp, 5000L);
    }
}

//Multilevel Inheritance because Boss inherits from Enemy,
//which in turn inherits from GameObject.Combined with multiple subclasses Fairy n Boss from Enemy.



