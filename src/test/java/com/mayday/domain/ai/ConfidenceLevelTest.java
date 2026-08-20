package com.mayday.domain.ai;

import com.mayday.domain.ai.model.ConfidenceLevel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfidenceLevelTest {

    @Test
    void highStartsFromNinetyFive() {
        assertThat(ConfidenceLevel.fromScore(95)).isEqualTo(ConfidenceLevel.HIGH);
        assertThat(ConfidenceLevel.fromScore(100)).isEqualTo(ConfidenceLevel.HIGH);
    }

    @Test
    void mediumCoversSeventyToNinetyFour() {
        assertThat(ConfidenceLevel.fromScore(70)).isEqualTo(ConfidenceLevel.MEDIUM);
        assertThat(ConfidenceLevel.fromScore(94)).isEqualTo(ConfidenceLevel.MEDIUM);
    }

    @Test
    void lowCoversScoresBelowSeventy() {
        assertThat(ConfidenceLevel.fromScore(0)).isEqualTo(ConfidenceLevel.LOW);
        assertThat(ConfidenceLevel.fromScore(69)).isEqualTo(ConfidenceLevel.LOW);
    }

    @Test
    void outOfRangeScoresAreClamped() {
        assertThat(ConfidenceLevel.fromScore(101)).isEqualTo(ConfidenceLevel.HIGH);
        assertThat(ConfidenceLevel.fromScore(-1)).isEqualTo(ConfidenceLevel.LOW);
    }
}
