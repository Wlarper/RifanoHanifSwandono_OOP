package com.Rifano.frontend.objects;

import com.Rifano.frontend.objects.items.ItemType;
import com.Rifano.frontend.objects.enemies.Enemy;
import com.Rifano.frontend.objects.items.Item;
import com.Rifano.frontend.objects.bullets.Bullet;
import com.Rifano.frontend.objects.bullets.BulletType;
import com.Rifano.frontend.objects.patterns.bulletStrategy.HomingBulletMovement;
import com.Rifano.frontend.objects.patterns.entityStrategy.FixedMovement;
import com.Rifano.frontend.objects.patterns.shootingStrategy.CompositeShootingPattern;
import com.Rifano.frontend.objects.patterns.shootingStrategy.FrontShot;
import com.Rifano.frontend.objects.patterns.shootingStrategy.SideShot;
import com.Rifano.frontend.objects.patterns.shootingStrategy.SpreadShot;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.Rifano.frontend.systems.AssetManager;
import com.Rifano.frontend.systems.EntityFactory;
import com.Rifano.frontend.systems.AudioSystem;
import com.badlogic.gdx.math.Rectangle;
import com.Rifano.frontend.objects.patterns.ShootingPattern;
import com.Rifano.frontend.systems.BulletManager;
import com.Rifano.frontend.objects.GameObject;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.Rifano.frontend.observers.GameObserver;
import com.badlogic.gdx.utils.Null;

import java.util.ArrayList;
import java.util.List;

public class Player extends EntityShooter {
    private String name;
    private int hp;
    private int lives = 3;
    private int power;
    private int spellCards;
    private long score;
    private int currentDir = 0;
    private int grazeCount = 0;
    private boolean focused = false;
    private float shootCooldown = 5f / 60f; // 5 frames at 60 FPS (12 shots/sec)
    private float shootTimer = shootCooldown;
    private float homingCooldown = 30f / 60f; // 30 frames (0.5s) or 15 frames (0.25s)
    private float homingShootTimer = homingCooldown;
    private float invulnerableTimer = 0f;
    private List<GameObserver> observers = new ArrayList<>();
    private ShootingPattern mainShootingPattern;
    private ShootingPattern sideShootingPattern;

    public Player(String name, int hp, int power, int spellCards) {
        super(280, 40, 32, 48, 240f, Color.RED);
        setMovementPattern(new FixedMovement());
        updatePowerStrategy();
        this.name = name;
        this.hp = hp;
        this.power = power;
        this.spellCards = spellCards;
        this.score = 0;
    }

    public Player(float x, float y, String name, int hp, int power, int spellCards) {
        super(x, y, 32, 48, 240f, Color.RED);
        setMovementPattern(new FixedMovement());
        updatePowerStrategy();
        this.name = name;
        this.hp = hp;
        this.power = power;
        this.spellCards = spellCards;
        this.score = 0;
    }

    public void takeDamage(int damage) {
        takeDamage(damage, null, null);
    }
    public void takeDamage(int damage, BulletManager bulletManager, List<GameObject> entities) {
        if (isInvulnerable() || isDestroyed()) {
            if (isInvulnerable()) {
                System.out.println(name + " is INVULNERABLE! (0 damage taken)");
            }
            return;
        }
        this.hp -= damage;
        notifyHpChanged();
        if (this.hp < 0) {
            this.hp = 0;
        }
        System.out.println(name + " took " + damage + " damage! Remaining HP: " + this.hp);
        if (this.hp == 0) {
            System.out.println(name + " was defeated (Pichuun~)! ");
            loseLife(bulletManager, entities);
        }
    }
    public void loseLife(BulletManager bulletManager, List<GameObject> entities) {
        this.lives--;
        notifyLivesChanged();
        if (bulletManager != null) {
            bulletManager.clearEnemyBullets();
        }
        if (this.lives > 0) {
            if (bulletManager != null) {
                System.out.println("[Player] " + name + " lost a life! Remaining Lives: " + this.lives + ". Respawning at (200, 50)...");
            }
            respawn();
        } else {
            if (bulletManager != null) {
                System.out.println("[Player] GAME OVER! " + name + " has run out of lives!");
            }
            destroy();
        }
    }
    public void respawn() {
        this.active = true;
        this.x = 200;
        this.y = 50;
        this.hp = 100;
        notifyHpChanged();
        this.invulnerableTimer = 3.0f;
        this.power = Math.max(10, this.power - 16); // Touhou death penalty
        updatePowerStrategy();
        notifyPowerChanged();
        this.spellCards = 3; // Reset bombs to 3 upon respawn
        notifySpellCardsChanged();
    }
    public void resetGame() {
        this.lives = 3;
        notifyLivesChanged();
        this.hp = 100;
        notifyHpChanged();
        this.power = 15;
        updatePowerStrategy();
        notifyPowerChanged();
        this.spellCards = 3;
        notifySpellCardsChanged();
        this.score = 0;
        notifyScoreChanged();
        this.grazeCount = 0;
        notifyGrazeChanged();
        this.active = true;
        this.x = 200;
        this.y = 50;
        this.invulnerableTimer = 3.0f;
    }

    public void shoot(Enemy target) {
        int damage = 10 + getPower();
        System.out.println(getName() + " shoots " + target.getName() + " dealing " + damage + " DMG!");
        target.takeDamage(damage);
    }

    @Override
    public void shootBullet(BulletManager bulletManager) {
        if (bulletManager == null) return;
        if (shootTimer >= shootCooldown) {
            if (mainShootingPattern != null) {
                mainShootingPattern.execute(x + width / 2f - 8f, y + height, bulletManager, true);
            } else if (shootingPattern != null) {
                shootingPattern.execute(x + width / 2f - 8f, y + height, bulletManager, true);
            } else {
                bulletManager.spawnPlayerBullet(x + width / 2f - 8f, y + height, 0, 400f, 10 + power);
            }
            shootTimer = 0f;
        }

        if (sideShootingPattern != null && homingShootTimer >= homingCooldown) {
            sideShootingPattern.execute(x + width / 2f - 8f, y + height, bulletManager, true);
            homingShootTimer = 0f;
        }
    }


    public void addScore(long points) {
        if (points > 0) {
            this.score += points;
            notifyScoreChanged();
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
                    updatePowerStrategy();
                    notifyPowerChanged();
                    addScore(item.getScoreValue());
                    System.out.println(name + " collected POWER item! Power increased to " + power);
                }

                case POINT -> {
                    addScore(item.getScoreValue());
                    System.out.println(name + " collected POINT item!");
                }
                case BOMB -> {
                    this.spellCards++;
                    notifySpellCardsChanged();
                    addScore(item.getScoreValue());
                    System.out.println(name + " collected BOMB item! SpellCards: " + spellCards);
                }
                case LIFE -> {
                    this.lives++;
                    this.hp = 100;
                    notifyLivesChanged();
                    addScore(item.getScoreValue());
                    System.out.println(name + " collected LIFE item! Remaining Lives: " + lives);
                }
            }
        } else {
            addScore(item.getScoreValue());
            System.out.println(name + " collected " + item.getItemType() + "!");
        }
        AudioSystem.getInstance().playSound("item_get");
        item.destroy();
    }


    public boolean isAlive() {
        return this.hp > 0 && this.lives > 0 && !isDestroyed();
    }
    public boolean isGameOver() {
        return this.lives <= 0;
    }
    public boolean isInvulnerable() {
        return invulnerableTimer > 0f;
    }
    public float getInvulnerableTimer() {
        return invulnerableTimer;
    }
    public int getLives() { return lives; }
    public void setLives(int lives) {
        this.lives = lives;
        notifyLivesChanged();
    }
    public void move(float dx, float dy) {
        this.x += dx * speed;
        this.y += dy * speed;
        clampToPlayfield();
    }
    private void clampToPlayfield() {
        float minX = 32;
        float maxX = 416 - width;
        float minY = 16;
        float maxY = 560 - height;
        if (x < minX) x = minX;
        if (x > maxX) x = maxX;
        if (y < minY) y = minY;
        if (y > maxY) y = maxY;
    }
    public void useBomb(BulletManager bulletManager, List<GameObject> entities) {
        if (spellCards > 0) {
            spellCards--;
            invulnerableTimer = 5.0f; // 5 seconds invulnerability during Fantasy Seal!
            notifySpellCardsChanged();
            if (bulletManager != null) {
                bulletManager.clearEnemyBullets(entities);

                // Search for an active enemy target to chase (Boss or Fairy)
                GameObject target = null;
                if (entities != null) {
                    for (GameObject obj : entities) {
                        if (obj instanceof Enemy enemy && !enemy.isDestroyed()) {
                            target = enemy;
                            break;
                        }
                    }
                }

                bulletManager.spawnBombOrbs(x + width / 2f, y + height / 2f, target, entities);
            }
            System.out.println(name + " unleashes SPELL CARD: Spirit Sign Fantasy Seal (霊符「夢想封印」)! All enemy bullets cleared & 8 homing orbs launched!");
        } else {
            System.out.println("No Spell Cards (Bombs) remaining!");
        }
    }

    public void useBomb(BulletManager bulletManager) {
        useBomb(bulletManager, null);
    }
    public void addGraze() {
        this.grazeCount++;
        addScore(50);
        notifyGrazeChanged();
    }
    @Override
    public void update(float delta) {
        super.update(delta); // Advances stateTime and movementPattern
        shootTimer += delta;
        homingShootTimer += delta;

        if (shootTimer > 0.15f) {
            shootTimer = shootCooldown;
            homingShootTimer = homingCooldown;
        }

        if (invulnerableTimer > 0f) {
            invulnerableTimer -= delta;
            if (invulnerableTimer < 0f) invulnerableTimer = 0f;
        }
    }
    public void updateAnimationState(float dx) {
        AssetManager assets = AssetManager.getInstance();
        if (dx < 0) {
            if (currentDir != -1) {
                currentDir = -1;
                Animation<TextureRegion> anim = assets.getAnimation("player_left");
                if (anim != null) {
                    setAnimation(anim);
                }
            }
        } else if (dx > 0) {
            if (currentDir != 1) {
                currentDir = 1;
                Animation<TextureRegion> anim = assets.getAnimation("player_right");
                if (anim != null) {
                    setAnimation(anim);
                }
            }
        } else {
            if (currentDir != 0) {
                currentDir = 0;
                Animation<TextureRegion> anim = assets.getAnimation("player_idle");
                if (anim != null) {
                    setAnimation(anim);
                }
            }
        }
    }
    @Override
    public void render(SpriteBatch batch) {
        if (batch != null && active && !isDestroyed()) {
            if (invulnerableTimer > 0f) {
                boolean visible = ((int) (invulnerableTimer * 16)) % 2 == 0;
                if (!visible) return;
            }
            if (focused) {
                batch.setColor(1f, 1f, 1f, 0.5f);
            }
            super.render(batch);
            if (focused) {
                batch.setColor(1f, 1f, 1f, 1.0f);
            }
        }
    }
    public void renderFocusIndicator(ShapeRenderer shapeRenderer) {
        if (focused && shapeRenderer != null && active) {
            Rectangle core = getCoreHitbox();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.circle(core.x + core.width / 2f, core.y + core.height / 2f, 4f);
            shapeRenderer.setColor(Color.WHITE);
            shapeRenderer.circle(core.x + core.width / 2f, core.y + core.height / 2f, 2f);
            shapeRenderer.end();
        }
    }
    public boolean isFocused() { return focused; }
    public void setFocused(boolean focused) {
        if (this.focused != focused) {
            this.focused = focused;
            updatePowerStrategy();
        }
    }
    public int getGrazeCount() { return grazeCount; }
    public int getHp() {
        return hp;
    }
    public void setHp(int hp) {
        this.hp = Math.max(0, hp);
        notifyHpChanged();
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
        updatePowerStrategy();
        notifyPowerChanged();
    }

    public int getSpellCards() {
        return spellCards;
    }

    public void setSpellCards(int spellCards) {
        this.spellCards = spellCards;
        notifySpellCardsChanged();
    }

    public long getScore() {
        return score;
    }

    @Override
    public Rectangle getCoreHitbox() {
        return new Rectangle(x + (width / 2f) - 4f, y + (height / 2f) - 4f, 8f, 8f);
    }

    @Override
    public Rectangle getGrazeHitbox() {
        return new Rectangle(x, y, width, height);
    }

    public void registerObserver(GameObserver observer) {
            if (observer != null && !observers.contains(observer)) {
                observers.add(observer);
                observer.onScoreChanged(score);
                observer.onHpChanged(hp);
                observer.onLivesChanged(lives);
                observer.onSpellCardsChanged(spellCards);
                observer.onPowerChanged(power);
                observer.onGrazeChanged(grazeCount);
            }
        }
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }
    private void notifyScoreChanged() {
        for (GameObserver obs : observers) {
            obs.onScoreChanged(this.score);
        }
    }
    private void notifyHpChanged() {
        for (GameObserver obs : observers) {
            obs.onHpChanged(this.hp);
        }
    }
    private void notifyLivesChanged() {
        for (GameObserver obs : observers) {
            obs.onLivesChanged(lives);
        }
    }
    private void  notifySpellCardsChanged() {
        for (GameObserver obs : observers) {
            obs.onSpellCardsChanged(spellCards);
        }
    }
    private void  notifyPowerChanged() {
        for (GameObserver obs : observers) {
            obs.onPowerChanged(power);
        }
    }
    private void  notifyGrazeChanged() {
        for (GameObserver obs : observers) {
            obs.onGrazeChanged(grazeCount);
        }
    }
    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Item){
            collectItem((Item) other);
            System.out.println("Player Touches Items");}

    }
    public void updatePowerStrategy() {
        float spreadAngle = focused ? 4f : 18f;
        float dualSpreadAngle = focused ? 3f : 12f;
        float sideHOffset = focused ? 8f : 24f;
        float sideVOffset = focused ? 16f : -16f;
        float leftAngle = focused ? 100f : 120f;
        float rightAngle = focused ? 80f : 60f;

        float normalSpeed = 720f;
        float homingSpeed = 500f;

        // 2. Evaluate player power ranks (Authentic Touhou 6 EoSD Power Ranks):
        if (power < 8) {
            // Rank 1 (Power 0 - 7): 1 Center Straight Shot
            mainShootingPattern = new FrontShot(normalSpeed, 48);
            sideShootingPattern = null;
            homingCooldown = 30f / 60f;
        } else if (power < 16) {
            // Rank 2 (Power 8 - 15): 1 Center Straight Shot + 2 Homing Flank Amulets (Homing SideShot)
            mainShootingPattern = new FrontShot(normalSpeed, 48);
            sideShootingPattern = new SideShot(homingSpeed, 14, sideHOffset, sideVOffset, leftAngle, rightAngle, true, new HomingBulletMovement(360f), "bullet_amulet_homing");
            homingCooldown = 30f / 60f;
        } else if (power < 32) {
            // Rank 3 (Power 16 - 31): 2 Tightly Spaced Center Straight Shots + 2 Homing Flank Amulets
            mainShootingPattern = new SpreadShot(normalSpeed, 2, dualSpreadAngle, 30, 8f, null, null, null);
            sideShootingPattern = new SideShot(homingSpeed, 14, sideHOffset, sideVOffset, leftAngle, rightAngle, true, new HomingBulletMovement(360f), "bullet_amulet_homing");
            homingCooldown = 30f / 60f;
        } else if (power < 48) {
            // Rank 4 (Power 32 - 47): 3 Center Fan Spread Shots + 2 Homing Flank Amulets
            mainShootingPattern = new SpreadShot(normalSpeed, 3, spreadAngle, 26, 0f, null, null, null);
            sideShootingPattern = new SideShot(homingSpeed, 14, sideHOffset, sideVOffset, leftAngle, rightAngle, true, new HomingBulletMovement(360f), "bullet_amulet_homing");
            homingCooldown = 30f / 60f;
        } else if (power < 64) {
            // Rank 5 (Power 48 - 63): 3 Center Fan Spread Shots + Homing Amulets (Cadence accelerated to 15 frames / 0.25s!)
            mainShootingPattern = new SpreadShot(normalSpeed, 3, spreadAngle, 26, 0f, null, null, null);
            sideShootingPattern = new SideShot(homingSpeed, 12, sideHOffset, sideVOffset, leftAngle, rightAngle, true, new HomingBulletMovement(360f), "bullet_amulet_homing");
            homingCooldown = 15f / 60f;
        } else if (power < 80) {
            // Rank 6 (Power 64 - 79): 3 Center Fan Spread Shots + 4 Homing Amulets (Composite Pattern)
            mainShootingPattern = new SpreadShot(normalSpeed, 3, spreadAngle, 26, 0f, null, null, null);
            SideShot side1 = new SideShot(homingSpeed, 9, sideHOffset, sideVOffset, leftAngle, rightAngle, true, new HomingBulletMovement(360f), "bullet_amulet_homing");
            SideShot side2 = new SideShot(homingSpeed, 12, sideHOffset + 4f, sideVOffset, leftAngle + 30f, rightAngle - 30f, true, new HomingBulletMovement(360f), "bullet_amulet_homing");
            sideShootingPattern = new CompositeShootingPattern(side1, side2);
            homingCooldown = 15f / 60f;
        } else if (power < 128) {
            // Rank 7 & 8 (Power 80 - 127): 3 Center Fan Spread Shots + 4 Tightly Angled Homing Amulets
            mainShootingPattern = new SpreadShot(normalSpeed, 3, spreadAngle, 26, 0f, null, null, null);
            SideShot side1 = new SideShot(homingSpeed, 8, sideHOffset, sideVOffset, leftAngle - 10f, rightAngle + 10f, true, new HomingBulletMovement(360f), "bullet_amulet_homing");
            SideShot side2 = new SideShot(homingSpeed, 8, sideHOffset + 4f, sideVOffset, leftAngle + 10f, rightAngle - 10f, true, new HomingBulletMovement(360f), "bullet_amulet_homing");
            sideShootingPattern = new CompositeShootingPattern(side1, side2);
            homingCooldown = 15f / 60f;
        } else {
            // Rank 9 (Power 128 MAX): 4 Spaced Center Fan Spread Shots + 6 Maximum Homing Amulets
            mainShootingPattern = new SpreadShot(normalSpeed, 4, spreadAngle * 1.2f, 24, 16f, null, null, null);
            SideShot side1 = new SideShot(homingSpeed, 8, sideHOffset, sideVOffset, leftAngle - 10f, rightAngle + 10f, true, new HomingBulletMovement(360f), "bullet_amulet_homing");
            SideShot side2 = new SideShot(homingSpeed, 8, sideHOffset + 4f, sideVOffset, leftAngle + 10f, rightAngle - 10f, true, new HomingBulletMovement(360f), "bullet_amulet_homing");
            SideShot side3 = new SideShot(homingSpeed, 8, sideHOffset + 8f, sideVOffset, leftAngle + 30f, rightAngle - 30f, true, new HomingBulletMovement(360f), "bullet_amulet_homing");
            sideShootingPattern = new CompositeShootingPattern(side1, side2, side3);
            homingCooldown = 15f / 60f;
        }

        // 3. Attach composite strategy to superclass EntityShooter attribute
        if (sideShootingPattern != null) {
            setShootingPattern(new CompositeShootingPattern(mainShootingPattern, sideShootingPattern));
        } else {
            setShootingPattern(mainShootingPattern);
        }
    }
    public ShootingPattern getMainShootingPattern() { return mainShootingPattern; }
    public ShootingPattern getSideShootingPattern() { return sideShootingPattern; }
    public float getShootCooldown() { return shootCooldown; }
    public float getHomingCooldown() { return homingCooldown; }


}

