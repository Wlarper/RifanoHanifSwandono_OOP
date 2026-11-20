package com.Rifano.frontend.states;

import com.Rifano.frontend.objects.GameObject;
import com.Rifano.frontend.objects.enemies.Boss;
import com.Rifano.frontend.systems.LevelWaveManager;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public abstract class SpellCardState implements WaveState {
    private static final float INTRO_SECONDS = 2f;
    private static final float TEST_TIMEOUT_SECONDS = 4f;

    private final Random random = new Random();
    private List<Supplier<Attack>> blocks;
    private Attack currentBlock;
    private int blockIndex = -1;
    private float phaseTimer = 0f;

    protected abstract String getSpellName();
    protected abstract int getStartingLife();
    protected abstract float getTimeoutSeconds();
    protected abstract List<Supplier<Attack>> createAttackBlocks(GameObject player);
    protected abstract WaveState createNextState();

    protected float getGlideDistance() { return 90f; }
    protected float getGlideSeconds() { return 1.5f; }
    protected boolean isFinalPhase() { return false; }
    protected void onSpellStart(Boss boss) {}
    protected void onSpellEnd(LevelWaveManager manager, Boss boss) {}

    // ====================================================
    // Core Methods
    // ====================================================

    // TODO: Implement the methods inherited from WaveState, namely:

    @Override
    public void onEnter (LevelWaveManager manager) {
        Boss boss = manager.getBoss();
        if (boss == null || boss.isDestroyed()) {
            manager.spawnBoss(200, 420, "Rumia", getSpellName());
        }


        // When entering the State:
        // 1. Use LevelWaveManager to *get* the `Boss`!
        // 2. If there is no Boss, or it's destroyed, *spawn* one at coordinates (200, 420) named "Rumia", with HP matching the current spell state's starting life.
        // 3. Set the boss's current HP to the spell state's starting life. (Unlike Non-Spell: we set the HP, not the *life*!).
        // 4. Decide whether the boss survives at zero HP:
        //    if this phase is the *final phase*, then the boss MUST NOT survive at 0 HP!
        // 5. Make the boss unable to take *damage*, keep it *collidable*, and set its *movementPattern* so the Boss glides to `home` with the duration of `INTRO_SECONDS`. (HINT: Check how you did the movement pattern in NonSpellState!)
        // 6. Call `onSpellStart(...)`!
        // 7. Populate the `blocks` variable by creating the *attack blocks* using the player from `manager`.

        // When updating:
        // 1. Increment `phaseTimer` by `delta`.
        // 2. Fetch the `Boss` from `manager` (just *get*, no need to spawn again).
        // 3. If the Boss exists (not `null`), is not destroyed, and `phaseTimer >= INTRO_SECONDS`:
        //    3.1. If `currentBlock` is still `null`, make the boss able to take *damage* again.
        //    3.2. If `currentBlock` is still `null` OR `currentBlock` is finished, start the next attack block via the provided helper!
        //    3.3. Update `currentBlock` with `boss`, `bulletManager` from `manager`, and `delta`.
        // 4. Compute:
        //    - timeout = (manager.getPlayer() == null) ? TEST_TIMEOUT_SECONDS : getTimeoutSeconds(),
        //    - bossDefeated = (boss != null && boss.getHp() <= 0).
        // 5. If `bossDefeated || phaseTimer >= timeout`:
        //    5.1. If `bulletManager` from `manager` is not `null`, clear enemy bullets.
        //    5.2. Run `onSpellEnd(...)` with `manager` and `boss`.
        //    5.3. Create the next state, and switch to it via `manager`.@Override

        public void update (LevelWaveManager manager,float delta){
            phaseTimer += delta;
            Boss boss = manager.getBoss();
            if(boss!= null && !boss.isDestroyed() && phaseTimer >= INTRO_SECONDS()){
                if(currentBlock == null){
                    boss.setDamageable(true);
                }
                if(currentBlock == null) || currentBlock.isFinished(){
                    startNextBlock(boss);
                    manager.getBoss();


                }
                timeout = (manager.getPlayer() == null) ? TEST_TIMEOUT_SECONDS : getTimeoutSeconds();
                bossDefeated = (boss != null && boss.getHp() <= 0);

            }
        }
        // ====================================================
        // Helper
        // ====================================================
        private void startNextBlock (Boss boss){
            boolean firstBlock = (blockIndex < 0);
            blockIndex = (blockIndex + 1) % blocks.size();
            currentBlock = blocks.get(blockIndex).get();
            if (!firstBlock) {
                boss.setMovementPattern(BossGlide.random(boss, getGlideDistance(), getGlideSeconds(), random));
            }
            System.out.println("[" + getClass().getSimpleName() + "] Rumia block: " + currentBlock.getName());
        }
    }
}
