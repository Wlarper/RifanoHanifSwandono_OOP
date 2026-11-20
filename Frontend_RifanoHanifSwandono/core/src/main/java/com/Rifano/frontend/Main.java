package com.Rifano.frontend;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.Rifano.frontend.systems.AssetManager;
import com.Rifano.frontend.systems.StageFacade;
import com.Rifano.frontend.ui.GameHUD;
import com.Rifano.frontend.systems.AudioSystem;

public class Main extends ApplicationAdapter {
    // TODO (2 points):
    // Complete the additional constants and fields of `Main` referring to the UML above
    // (player name constant to be saved to the leaderboard using your own username,
    // leaderboard size limit = 5, ScoreboardFacade instance, and a flag so the score
    // is only submitted once per run).

    private SpriteBatch batch;
    private GameHUD gameHUD;
    private StageFacade stage;

    @Override
    public void create() {
        batch = new SpriteBatch();
        gameHUD = new GameHUD();
        AssetManager.getInstance().init();
        AudioSystem.getInstance().init();


        stage = new StageFacade(gameHUD);
        // TODO (1 point): Initialize the stage facade component according to the UML, passing in gameHUD.
        StageFacade stage;

        // TODO (1 point): Initialize the network facade component (the one you just pasted) according to the UML.
        Scoreboard
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input != null && Gdx.input.isKeyJustPressed(Input.Keys.R)
            && (stage.isGameOver() || stage.isStageClear())) {
            // TODO (1 point): Reset the stage.

            // TODO (1 point): Reset the flag indicating whether the score has been submitted. (Logically, if we restart the game, we haven't submitted any score yet, right?)
            gameHUD.clearScoreboard();
        }

        // TODO (2 point): Update the stage.

        gameHUD.setStageClear(stage.isStageClear());

        // TODO (2 point): Call the helper method to check if the run ended and submit the score (see `Main` UML).

        ScreenUtils.clear(0.1f, 0.1f, 0.15f, 1f);
        batch.begin();
        stage.render(batch);
        gameHUD.render(batch);
        batch.end();

        gameHUD.renderBackground();
        stage.renderOverlay(gameHUD.getShapeRenderer());
    }

    private void submitScoreOnRunEnd() {
        if ((!stage.isGameOver() && !stage.isStageClear()) || scoreSubmitted) {
            return;
        }
        scoreSubmitted = true;
        gameHUD.setScoreboardStatus("Saving score...");
        scoreboard.submitScore(PLAYER_NAME, stage.getScore(),
            saved -> {
                gameHUD.setScoreboardStatus("Saved as " + saved.getPlayerName());
                scoreboard.fetchLeaderboard(LEADERBOARD_SIZE, gameHUD::showLeaderboard, gameHUD::setScoreboardStatus);
            },
            gameHUD::setScoreboardStatus);
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (gameHUD != null) gameHUD.dispose();
        AssetManager.getInstance().dispose();
    }
}



