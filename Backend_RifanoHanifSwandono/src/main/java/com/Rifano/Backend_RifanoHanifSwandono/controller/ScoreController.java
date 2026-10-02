package com.Rifano.Backend_RifanoHanifSwandono.controller;

import com.Rifano.Backend_RifanoHanifSwandono.model.Score;
import com.Rifano.Backend_RifanoHanifSwandono.service.ScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*")
public class ScoreController {

    @Autowired
    private ScoreService scoreService;

    @GetMapping("/{scoreId}")
    public ResponseEntity<?> getScoreById(@PathVariable UUID scoreId) {
        Optional<Score> score = scoreService.getScoreByID(scoreId);

        if (score.isPresent()) {
            return ResponseEntity.ok(score.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Score not found");
        }
    }

    @PostMapping
    public ResponseEntity<?> createScore(@RequestBody Score score) {
        try {
            Score newScore = scoreService.createScore(score);
            return ResponseEntity.status(HttpStatus.CREATED).body(newScore);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing request: " + e.getMessage());
        }
    }
    // TODO:
// 1. Add the appropriate annotation for a GET endpoint along with the appropriate endpoint
    public ResponseEntity<List<Score>> getAllScores() {
        List<Score> scoreList = scoreService.getAllScores();
        // 2. Use scoreService to call getAllScores() and store those scores in a variable using List
        scoreService.getAllScores();
        // 3. Return the variable containing those scores
        return ResponseEntity.ok(scoreList);
    }

    // TODO:
// 1. Add the appropriate annotation for a GET endpoint along with the appropriate endpoint
    public  ResponseEntity<List<Score>> getLeaderboardByPoint(@RequestParam (defaultValue = "10") Integer limit ){
        // 4. Use scoreService to call getLeaderboard() with the appropriate parameter
        List<Score> scoreList = scoreService.getLeaderboard(limit);
        //    and store those scores in a variable using List
        // 5. Return the variable containing those scores
        return ResponseEntity.ok(scoreList);
    }
    // TODO:
// 1. Add the appropriate annotation for a GET endpoint along with the appropriate endpoint
    public ResponseEntity<List<Score>> getScoresAboveValue(@PathVariable Integer minValue){
        // 3. Use scoreService to call getScoreAboveValue() with the appropriate parameter
        List<Score> scoreList = scoreService.getScoreAboveValue(minValue);
        //    and store those scores in a variable using List
        // 4. Return the variable containing those scores
        return ResponseEntity.ok(scoreList);
    }
    // TODO:
// 1. Add the appropriate annotation for a GET endpoint along with the appropriate endpoint
    public ResponseEntity<List<Score>> getRecentScores(){
        // 2. Use scoreService to call getRecentScores() with the appropriate parameter
        List<Score> scoreList = scoreService.getRecentScores();
        //    and store those scores in a variable using List
        // 3. Return the variable containing those scores
        return ResponseEntity.ok(scoreList);
    }
    // TODO:
// 1. Add the appropriate annotation for a DELETE endpoint along with the appropriate endpoint
    public  ResponseEntity<?> deleteScore(@PathVariable UUID scoreId){
        // 3. create a try-catch block
        try {
            scoreService.deleteScore(scoreId);
            return ResponseEntity.ok("{\"message\": \"Creature deleted successfully\"}");
        } catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)  .body("{\"error\": \"" + e.getMessage() + "\"}");
        }

    }

}
