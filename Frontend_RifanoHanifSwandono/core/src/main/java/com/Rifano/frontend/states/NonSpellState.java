package com.Rifano.frontend.states;

import com.Rifano.frontend.objects.GameObject;
import com.Rifano.frontend.objects.enemies.Boss;
import com.Rifano.frontend.objects.patterns.entityStrategy.EntityMovementPattern;
import com.Rifano.frontend.objects.patterns.entityStrategy.FixedMovement;
import com.Rifano.frontend.states.WaveState;
import com.Rifano.frontend.systems.LevelWaveManager;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public abstract class NonSpellState implements WaveState {
    private static final float TEST_TIMEOUT_SECONDS = 4f;
    private static final float GLIDE_DISTANCE = 90f;
    private static final float GLIDE_SECONDS = 1f;

    private final Random random = new Random();
    private List<Supplier<Attack>> attacks;
    private Attack currentAttack;
    private int currentIndex;
    private float phaseTimer = 0f;

    protected abstract String getPhaseName();
    protected abstract int getPhaseLife();
    protected abstract int getLifeThreshold();
    protected abstract List<Supplier<Attack>> createAttacks(GameObject player);
    protected abstract float getTimeoutSeconds();
    protected abstract WaveState createNextState();

    protected float getIntroSeconds() { return 0f; }
    protected boolean isCollidableDuringIntro() { return true; }
    protected EntityMovementPattern createIntroMovement(Boss boss) { return new FixedMovement(); }

    // ====================================================
    // Core Methods
    // ====================================================

    // TODO: Implement the methods inherited from WaveState, namely:

    @Override
    public void onEnter (LevelWaveManager manager){
        Boss boss = manager.getBoss();
        if(boss==null|| boss.isDestroyed()){
            manager.spawnBoss(320, 464,"Rumia", getPhaseLife());
        }
        boss.setLife(getPhaseLife());
        boss.setSurvivesAtZeroHp(true);
        if(getIntroSeconds() > 0 ){
            boss.setDamageable(false);
            boss.setCollidable(false);
            boss.setMovementPattern(createIntroMovement);
        }
        if(getIntroSeconds() <= 0) {
            boss.setDamageable(true);
            boss.setCollidable(true);
            boss.setMovementPattern(BossGlide.random(boss, GLIDE_DISTANCE, GLIDE_SECONDS, random));
        }
        createAttacks();
        currentAttack=null;
    }

    // When entering the State:
    // 1. Use LevelWaveManager to *get* the `Boss`!
    // 2. If there is no Boss, or it's destroyed, *spawn* one at coordinates (320, 464) named "Rumia", with HP matching the current phase life.
    // 3. Set the boss's life to match the current phase's life.
    // 4. Configure the boss to survive at zero HP.
    // 5. Check whether *introSeconds* is greater than 0:
    //    - If YES: the Boss cannot take *damage*, its *collidable* flag follows whatever *state it is in intro collidable*, and set its *movementPattern* by creating the intro movement.
    //    - If NO: the Boss can take *damage*, is *collidable*, and its *movementPattern* is `BossGlide.random(boss, GLIDE_DISTANCE, GLIDE_SECONDS, random)`.
    // 6. Populate the `attacks` variable by creating the attacks.
    //    Get the player argument for creating attacks from `manager`.
    // 7. Set `currentAttack` to `null`.

    @Override
    public void update(LevelWaveManager manager, float delta){
        phaseTimer += delta;
        Boss boss =manager.getBoss();
        if(boss!= null && !boss.isDestroyed() && phaseTimer >= getIntroSeconds()){
            if(currentAttack != null){
                boss.setCollidable(true);
                boss.setDamageable(true);
                startAttack(boss, 0);
                currentAttack.update((boss,manager.getBulletManager()));
            }
            if(currentAttack == null)||currentAttack.isFinished(){
        }
        timeout = (manager.getPlayer() == null) ? TEST_TIMEOUT_SECONDS : getTimeoutSeconds();
        hpCutoffReached = (boss != null && boss.getHp() < getLifeThreshold());
        if(hpCutoffReached || phaseTimer >= timeout){
            if(boss==null){
                boss.setHp(boss.getLifeThreshold);
            }
            if(boss!=null){

            }
        }

    }

    // When updating:
    // 1. Increment `phaseTimer` by `delta`.
    // 2. Fetch the `Boss` from `manager` (just *get*, don't spawn).
    // 3. If the Boss exists (not `null`), is not destroyed, and `phaseTimer >= getIntroSeconds()`:
    //    3.1. If `currentAttack` is `null`, make the boss *collidable* and able to take *damage*, then start the *attack* at index 0!
    //    3.2. Update `currentAttack` with `boss`, `bulletManager` from `manager`, and `delta`.
    //    3.3. If `currentAttack` is finished, start another boss attack by calling the helper to *pick another attack*.
    // 4. Write the following variables:
    //    - timeout = (manager.getPlayer() == null) ? TEST_TIMEOUT_SECONDS : getTimeoutSeconds(),
    //    - hpCutoffReached = (boss != null && boss.getHp() < getLifeThreshold()).
    // 5. If `hpCutoffReached || phaseTimer >= timeout`:
    //    5.1. If boss is not `null`, set the boss's HP to the *life threshold*.
    //    5.2. If `bulletManager` from `manager` is not `null`, clear enemy bullets. (HINT: who's in charge of the bullets on screen?)
    //    5.3. Create the next state, and switch to it via `manager`.

    // ====================================================
    // Helper
    // ====================================================
    private void startAttack(Boss boss, int index) {
        currentIndex = index;
        currentAttack = attacks.get(index).get();
        boss.setMovementPattern(BossGlide.random(boss, GLIDE_DISTANCE, GLIDE_SECONDS, random));
    }

    private int pickAnotherAttack() {
        int next = random.nextInt(attacks.size() - 1);
        return (next >= currentIndex) ? next + 1 : next;
    }
}
