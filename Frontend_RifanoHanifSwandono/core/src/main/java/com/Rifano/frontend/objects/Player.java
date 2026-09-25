package com.Rifano.frontend.objects;

import com.Rifano.frontend.objects.items.ItemType;
import com.Rifano.frontend.objects.enemies.Enemy;
import com.Rifano.frontend.objects.items.Item;
import com.Rifano.frontend.objects.bullets.Bullet;
import com.Rifano.frontend.objects.BulletType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

public class Player extends GameObject {
    private String name;
    private int hp;
    private int power;
    private int spellCards;
    private long score;

    public Player(String name, int hp, int power, int spellCards) {
        super(280, 40, 32, 32, 200f, Color.RED);
        this.name = name;
        this.hp = hp;
        this.power = power;
        this.spellCards = spellCards;
        this.score = 0;
    }

    public Player(float x, float y, String name, int hp, int power, int spellCards) {
        super(x, y, 32, 32, 200f, Color.RED);
        this.name = name;
        this.hp = hp;
        this.power = power;
        this.spellCards = spellCards;
        this.score = 0;
    }

    public void takeDamage(int damage) {
        setHp(getHp() - damage);

        if (this.hp > 0) {
            System.out.println(this.name + " took " + damage + " damage!!!  Remaining HP:" + this.hp);
        } else {
            System.out.println(this.name + " DIEEDDDD BOOOOO ");
        }
    }

    public void shoot(Enemy target) {
        int damage = 10 + getPower();
        System.out.println(getName() + " shoots " + target.getName() + " dealing " + damage + " DMG!");
        target.takeDamage(damage);
    }

    public Bullet shootBullet() {
        int damage = 10 + power;
        System.out.println(name + " shoots bullet dealing " + damage + " DMG!");
        return new Bullet(x + (width / 2) - 4, y + height, BulletType.AMULET, damage);
    }

    public void addScore(long points) {
        if (points > 0) {
            this.score += points;
            System.out.println(getName() + " gained " + points + " pts! Total Score: " + this.score);
        }
    }

    public void collectItem(Item item) {
        if (item.isDestroyed()) return;
        ItemType type = item.getItemTypeEnum();
        if (type != null) {
            switch (type) {
                case POWER -> {
                    this.power += type.getPowerBonus();
                    addScore(item.getScoreValue());
                    System.out.println(name + "Collected POWER item! power increased to" + power);
                }
                case POINT -> {
                    addScore(item.getScoreValue());
                    System.out.println(name + "collected POINT item!");
                }
                case BOMB -> {
                    spellCards += 1;
                    addScore(item.getScoreValue());
                    System.out.println(name + "collected BOMB item! Spellcards: " + spellCards);
                }
                case LIFE -> {
                    hp += 20;
                    addScore(item.getScoreValue());
                    System.out.println(name + "collected LIFE item! HP:" + hp);
                }
            }
            item.destroy();
        } else {
            addScore(item.getScoreValue());
            System.out.println(name + " collected " + item.getItemType() + "!");
        }
    }


    public boolean isAlive() {
        return this.hp > 0;
    }
    public int getHp() {
        return hp;
    }
    public void setHp(int hp) {
        this.hp = Math.max(0, hp);
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getPower() {
        return power;
    }
    public void setPower(int power) {
        this.power = power;
    }

    public int getSpellCards() {
        return spellCards;
    }

    public void setSpellCards(int spellCards) {
        this.spellCards = spellCards;
    }

    public long getScore() {
        return score;
    }

    @Override
    public void update(float delta) {
        if (Gdx.input != null) {
            if(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP) )
            {
                y += speed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN) )
            {
                y -= speed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))
            {
                x -= speed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            {
                x += speed * delta;
            }
        }
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Item){
            collectItem((Item) other);
            System.out.println("Player Touches Items");}

    }

}

