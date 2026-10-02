package com.Rifano.Backend_RifanoHanifSwandono.service;

import com.Rifano.Backend_RifanoHanifSwandono.model.Score;
import com.Rifano.Backend_RifanoHanifSwandono.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    public Score createScore(Score score) {
        return scoreRepository.save(score);
    }
    public Optional<Score> getScoreByID(UUID scoreId) {
        return scoreRepository.findById(scoreId);
    }

    public List<Score> getAllScores(){
        // TODO: Use scoreRepository to find all scores in the database, then return the result
        // hint: Call the same method as the code you wrote in TP number 4
        return scoreRepository.findAll();
    }
    public List<Score> getRecentScores(){
        // TODO: Use scoreRepository to find all scores in the database ordered by newest creation, then return the result
        return scoreRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Score> getScoreAboveValue(int minValue){
        // TODO: Use scoreRepository to find all scores in the database whose points are above a certain value
        // use minValue as the lower bound of the point value
        return scoreRepository.findByPointGreaterThan(minValue);
    }

    public List<Score> getLeaderboard(int limit) {
        // TODO: Use scoreRepository to find the Top Scores and provide the appropriate parameter
        return scoreRepository.findTopScores();
    }

    public void deleteScore(UUID scoreId) {
        // TODO:
        Optional<Score> score = scoreRepository.findById(scoreId);
        score.orElseThrow(()-> new RuntimeException("Score with ID " + scoreId + " was not found"));
        // 1. Find the score you want to delete using scoreRepository, then store that score (hint: see how it's done in getScoreById())
        // 2. Check whether the score was found or not with `.orElseThrow(()-> new RuntimeException("Score with ID " + scoreId + " was not found"));`
        // 3. Call delete() from scoreRepository to delete the score stored earlier
        deleteScore(scoreId);
    }


}
