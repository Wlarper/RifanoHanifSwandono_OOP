package com.Rifano.frontend;

import com.Rifano.frontend.commands.Command;
import com.Rifano.frontend.commands.InputHandler;
import com.Rifano.frontend.objects.GameObject;
import com.Rifano.frontend.objects.Player;
import com.Rifano.frontend.objects.enemies.Fairy;
import com.Rifano.frontend.objects.enemies.Boss;
import com.Rifano.frontend.objects.items.Item;
import com.Rifano.frontend.objects.items.ItemType;
import com.Rifano.frontend.objects.patterns.entityStrategy.SCurveMovement;
import com.Rifano.frontend.objects.patterns.entityStrategy.TargetPointMovement;
import com.Rifano.frontend.objects.patterns.shootingStrategy.AlternatingRingShot;
import com.Rifano.frontend.objects.patterns.shootingStrategy.SpreadShot;
import com.Rifano.frontend.systems.AssetManager;
import com.Rifano.frontend.systems.AudioSystem;
import com.Rifano.frontend.systems.BulletManager;
import com.Rifano.frontend.systems.CollisionReferee;
import com.Rifano.frontend.systems.EntityFactory;
import com.Rifano.frontend.ui.GameHUD;
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
    private BulletManager bulletManager;
    private CollisionReferee collisionReferee;
    private InputHandler inputHandler;
    private GameHUD gameHUD;
    private float enemyShootTimer = 0f;
    private float phaseTimer = 0f;
    private int currentPhase = 0;
    private float waveTimer = 0f;
    private int wave1SpawnIndex = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        fairy = new ArrayList<>();
        entities = new ArrayList<>();
        AssetManager.getInstance().init();
        AudioSystem.getInstance().init();
        bulletManager = new BulletManager();
        collisionReferee = new CollisionReferee();
        inputHandler = new InputHandler();
        gameHUD = new GameHUD();

        player = EntityFactory.createPlayer(200, 50, "Reimu Hakurei", 100, 15, 3);
        boss = EntityFactory.createBoss(200, 420, "Rumia", 150);
        powerItem = EntityFactory.createItem(180, 450, ItemType.POWER);
        pointItem = EntityFactory.createItem(220, 480, ItemType.POINT);

        player.registerObserver(gameHUD);

        entities.add(player);
        entities.add(boss);
        entities.add(powerItem);
        entities.add(pointItem);
        inputHandler.setEntities(entities);

        bulletManager.setEntities(entities);
        boss.setMovementPattern(new TargetPointMovement(200f, 420f, 150f));
        boss.setShootingPattern(new SpreadShot(220f, 5, 25f, 10, player, "bullet_danmaku_red"));
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        if (Gdx.input != null && Gdx.input.isKeyJustPressed(Input.Keys.R) && player.isGameOver()) {
            restartGame();
        }
        List<Command> commands = inputHandler.handleInput(delta);
        for (Command command : commands) {
            command.execute(player, bulletManager);
        }
        updateEnemyScheduler(delta);
        updateAndClean(entities, delta, 416, 560);
        bulletManager.update(delta, 416, 560);
        collisionReferee.resolveCollisions(player, entities, bulletManager);
        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);
        batch.begin();
        for (GameObject entity : entities) {
            if (!entity.isDestroyed()) {
                entity.render(batch);
            }
        }
        bulletManager.render(batch);
        gameHUD.render(batch);
        batch.end();
        gameHUD.renderBackground();
        player.renderFocusIndicator(gameHUD.getShapeRenderer());
    }

    private void updateFairyWave(float delta) {
        float[] spawnX = {60f, 68f, 76f, 84f, 92f};
        waveTimer += delta;
        if (wave1SpawnIndex < spawnX.length && waveTimer >= 1.0f + wave1SpawnIndex * 0.267f) {
            float x = spawnX[wave1SpawnIndex++];
            Fairy f = EntityFactory.createFairy(x, 480f, "Left S-Curve Fairy", 8, "fairy_idle_blue");

            f.setMovementPattern(new SCurveMovement(130f, (float) Math.toRadians(270), false));
            f.setShootingPattern(null);

            fairy.add(f);
            entities.add(f);
        }
    }

    private void resetFairyWave() {
        waveTimer = 0f;
        wave1SpawnIndex = 0;
    }

    private void restartGame() {
        System.out.println("[Main] Restarting game...");
        entities.clear();
        bulletManager.clearEnemyBullets();
        bulletManager.clearPlayerBullets();
        entities.add(player);
        player.resetGame();
        fairy.clear();
        resetFairyWave();
        boss = EntityFactory.createBoss(200, 420, "Rumia", 150);
        boss.setMovementPattern(new TargetPointMovement(200f, 420f, 150f));
        boss.setShootingPattern(new SpreadShot(220f, 5, 25f, 10, player, "bullet_danmaku_red"));
        currentPhase = 0;
        phaseTimer = 0f;
        enemyShootTimer = 0f;
        powerItem = EntityFactory.createItem(180, 450, ItemType.POWER);
        pointItem = EntityFactory.createItem(220, 480, ItemType.POINT);
        entities.add(boss);
        entities.add(powerItem);
        entities.add(pointItem);
    }

    private void updateEnemyScheduler(float delta) {
        updateFairyWave(delta);
        fairy.removeIf(f -> f.isDestroyed() || f.isOffScreen(416, 560));

        enemyShootTimer += delta;
        if (enemyShootTimer >= 1.5f && boss != null && !boss.isDestroyed()) {
            boss.shootBullet(bulletManager);
            enemyShootTimer = 0f;
        }

        phaseTimer += delta;
        if (phaseTimer >= 4.0f && boss != null && !boss.isDestroyed()) {
            phaseTimer = 0f;
            currentPhase = (currentPhase + 1) % 2;

            if (currentPhase == 0) {
                boss.setMovementPattern(new TargetPointMovement(120f, 420f, 150f));
                boss.setShootingPattern(new SpreadShot(220f, 5, 25f, 10, player, "bullet_danmaku_red"));
                System.out.println("[Boss Strategy] Rumia shifted to Upper Left with Aimed SpreadShot!");
            } else {
                boss.setMovementPattern(new TargetPointMovement(280f, 420f, 150f));
                boss.setShootingPattern(new AlternatingRingShot(180f, 16, 10));
                System.out.println("[Boss Strategy] Rumia shifted to Upper Right with AlternatingRingShot!");
            }
        }
    }

    public <T extends GameObject> void updateAndClean(List<T> list, float delta, float screenWidth, float screenHeight) {
        Iterator<T> listiterator = list.iterator();
        while (listiterator.hasNext()) {
            T obj = listiterator.next();
            obj.update(delta);
            if (obj.isOffScreen(screenWidth, screenHeight) || obj.isDestroyed()) {
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
        if (gameHUD != null) {
            gameHUD.dispose();
        }
        AssetManager.getInstance().dispose();
        AudioSystem.getInstance().dispose();

    }
}
