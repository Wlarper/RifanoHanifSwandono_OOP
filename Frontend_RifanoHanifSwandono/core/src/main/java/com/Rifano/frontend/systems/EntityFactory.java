package com.Rifano.frontend.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.Rifano.frontend.objects.Player;
import com.Rifano.frontend.objects.bullets.Bullet;
import com.Rifano.frontend.objects.BulletType;
import com.Rifano.frontend.objects.enemies.Boss;
import com.Rifano.frontend.objects.enemies.Fairy;
import com.Rifano.frontend.objects.items.Item;
import com.Rifano.frontend.objects.items.ItemType;

public class EntityFactory {

    // Create a Player and assign the 'player_idle' animation from AssetManager
    public static Player createPlayer(float x, float y, String name, int hp, int power, int spellCards) {
        Player player = new Player(x, y, name, hp, power, spellCards);
        Animation<TextureRegion> anim = AssetManager.getInstance().getAnimation("player_idle");
        player.setAnimation(anim);
        return player;
    }


    // Create an Item and assign its sprite from AssetManager based on ItemType
    public static Item createItem(float x, float y, ItemType itemType) {
        Item item = new Item(x, y, itemType);
        String key = switch (itemType) {
            case POWER -> "item_power";
            case POINT -> "item_point";
            case BOMB  -> "item_bomb";
            case LIFE  -> "item_life";
        };
        TextureRegion sprite = AssetManager.getInstance().getTextureRegion(key);
        item.setSprite(sprite);
        return item;
    }
    // Create an enemy bullet (type DANMAKU, speed 0f, sprite bullet_danmaku)
    public static Bullet createEnemyBullet(float x, float y, int damage) {
        Bullet bullet = new Bullet(x, y, 0f, BulletType.DANMAKU, damage);
        TextureRegion sprite = AssetManager.getInstance().getTextureRegion("bullet_danmaku");
        bullet.setSprite(sprite);
        return bullet;
    }
    public static Boss createBoss(float x, float y, String name, int hp) {
        // TODO:.
        Boss boss= new Boss( x,  y,  name,  hp);
        Animation<TextureRegion> idleAnim = AssetManager.getInstance().getAnimation(idleAnim);
        boss.setAnimation(idleAnim);
        return boss;
    }

    public static Fairy createFairy(float x, float y, String name, int hp) {
        // TODO:
        Fairy fairy= new Fairy(x,y,name,hp);
        Animation<TextureRegion> idleAnim = AssetManager.getInstance().getAnimation(idleAnim);
        fairy.setAnimation(idleAnim);
        boss.setAnimation(idleAnim);
        return boss;
    }

    public static Fairy createFairy(float x, float y, String name, int hp, String keyString) {
        // TODO:
        Fairy fairy= new Fairy(x,y,name,hp);
        Animation<TextureRegion> idleAnim = AssetManager.getInstance().getAnimation(idleAnim);
        fairy.setAnimation(idleAnim);
        boss.setAnimation(idleAnim);
        return boss;
    }
}
