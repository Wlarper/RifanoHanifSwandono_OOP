package com.Rifano.frontend;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.ArrayList;
import java.util.List;

import com.Rifano.frontend.objects.GameObject;
import com.Rifano.frontend.objects.Player;
import com.Rifano.frontend.objects.enemies.Enemy;
import com.Rifano.frontend.objects.enemies.Fairy;
import com.Rifano.frontend.objects.enemies.Boss;
import com.Rifano.frontend.objects.items.Item;

public class Main extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;

    private Player player;
    private Fairy fairy;
    private Boss boss;
    private List<Item> items;
    private List<GameObject> gameObjects;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        gameObjects = new ArrayList<>();

        player = new Player("Reimu Hakurei", 100, 15, 3);

        fairy = new Fairy("Stage 1 Fairy", 20);

        boss = new Boss("Cirno (Stage 2 Boss)", 150);

        powerItem = new Item(200, 450, 16, 16, 80f, ItemType.POWER, 500L);
        pointItem = new Item(320, 480, 12, 12, 120f, ItemType.POINT, 1000L);


        items = new ArrayList<>();
        items.add(new Item(100, 700, 12, 12, 100f, "Point Item", 100L));
        items.add(new Item(250, 750, 12, 12, 130f, "Power Item", 50L));
        items.add(new Item(400, 680, 12, 12, 90f, "Point Item", 100L));

        gameObjects.add(player);
        gameObjects.add(fairy);
        gameObjects.add(boss);
        gameObjects.addAll(items);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // 1. Polymorphic Update Loop: Items move downward automatically via Item.update(delta)
        for (GameObject obj : gameObjects) {
            obj.update(delta);
        }

        // AABB Collision detection between every unique entity pair
        for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
                GameObject a = entities.get(i);
                GameObject b = entities.get(j);
                if(a.getCoreHitbox().overlaps(b.getCoreHitbox())){
                   a.onCollision(a);
                   b.onCollision(b);
                }

                // TODO: Check whether getCoreHitbox() of a and b overlap (use the .overlaps() method of Rectangle)
                // TODO: Call a.onCollision(b) and b.onCollision(a)
            }
        }


        // 2. Clear Screen
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);

        // 3. Polymorphic Render Loop: Draw hitboxes with ShapeRenderer
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (GameObject obj : gameObjects) {
            obj.render(shapeRenderer);
        }
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
