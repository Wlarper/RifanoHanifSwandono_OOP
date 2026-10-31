package com.Rifano.frontend;

import com.Rifano.frontend.objects.GameObject;
import com.Rifano.frontend.objects.Player;
import com.Rifano.frontend.objects.enemies.Fairy;
import com.Rifano.frontend.objects.enemies.Boss;
import com.Rifano.frontend.objects.items.Item;
import com.Rifano.frontend.objects.items.ItemType;
import com.Rifano.frontend.systems.AssetManager;
import com.Rifano.frontend.systems.AudioSystem;
import com.Rifano.frontend.systems.EntityFactory;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Input;
import java.util.Iterator;


public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Player player;
    private List<Fairy> fairy;
    private Boss boss;
    private Item pointItem;
    private Item powerItem;
    private List<GameObject> entities;

    @Override
    public void create() {
        batch = new SpriteBatch();
        fairy = new ArrayList<>();
        entities = new ArrayList<>();
        AssetManager.getInstance().init();
        AudioSystem.getInstance().init();
        player = EntityFactory.createPlayer(280, 40, "Reimu Hakurei", 100, 15, 3);
        Fairy redFairy = EntityFactory.createFairy(150, 380, "Red Fairy", 20);
        Fairy blueFairy = EntityFactory.createFairy(250, 380, "Blue Fairy", 20, "fairy_idle_blue");
        fairy.add(redFairy);
        fairy.add(blueFairy);
        boss = EntityFactory.createBoss(380, 400, "Rumia", 150);
        powerItem = EntityFactory.createItem(200, 450, ItemType.POWER);
        pointItem = EntityFactory.createItem(320, 480, ItemType.POINT);
        entities.add(player);
        entities.addAll(fairy);
        entities.add(boss);
        entities.add(pointItem);
        entities.add(powerItem);

    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            entities.add(player.shootBullet());
        }
        updateAndClean(entities, delta, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                GameObject a = entities.get(i);
                GameObject b = entities.get(j);

                if (!a.isDestroyed() && !b.isDestroyed()) {
                    if (a.getCoreHitbox().overlaps(b.getCoreHitbox())) {
                        a.onCollision(b);
                        b.onCollision(a);
                    }
                }
            }
        }

        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);
        batch.begin();
        for (GameObject entity : entities) {
            if (!entity.isDestroyed()) {
                entity.render(batch);
            }
        }
        batch.end();
    }
    public <T extends GameObject> void updateAndClean(List<T> list, float delta, float screenWidth, float screenHeight) {
        Iterator<T> listiterator = list.iterator();
        while (listiterator.hasNext()) {
            T obj = listiterator.next();
            obj.update(delta);
            if (obj.isOffScreen(screenWidth, screenHeight) || obj.isDestroyed()){
                System.out.println("Removed via Generic Iterator: " + obj.getClass().getSimpleName());
                listiterator.remove();
            }
        }

    }
    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        AssetManager.getInstance().dispose();
        AudioSystem.getInstance().dispose();
    }
}
