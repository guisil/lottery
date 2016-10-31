package exercises.lottery.domain.rules;

import exercises.lottery.domain.Line;
import exercises.lottery.domain.LotteryRules;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.util.Sets.*;

/**
 * Test class for SimpleLotteryRules.
 *
 * Created by guisil on 25/07/2016.
 */
public class SimpleLotteryRulesTest {

    private static final int LINE_SIZE = 3;
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 2;

    private static final int MAX_NUMBER_OF_LINES = 27;

    private static final int OUTCOME_TEN = 10;
    private static final int OUTCOME_FIVE = 5;
    private static final int OUTCOME_ONE = 1;
    private static final int OUTCOME_ZERO = 0;

    private LotteryRules rules;

    @Before
    public void setUp() throws Exception {
        rules = new SimpleLotteryRules();
    }


    @Test
    public void shouldReturnLineSize() throws Exception {
        assertThat(rules.getLineSize()).isEqualTo(LINE_SIZE);
    }

    @Test
    public void shouldReturnMinLineValue() throws Exception {
        assertThat(rules.getMinLineValue()).isEqualTo(MIN_VALUE);
    }

    @Test
    public void shouldReturnMaxLineValue() throws Exception {
        assertThat(rules.getMaxLineValue()).isEqualTo(MAX_VALUE);
    }

    @Test
    public void shouldReturnMaximumNumberOfLines() throws Exception {
        assertThat(rules.getMaxNumberOfLines()).isEqualTo(MAX_NUMBER_OF_LINES);
    }

    @Test
    public void lineShouldBeValid() throws Exception {
        Set<Line> validLines = newLinkedHashSet(
                new Line(new int[] { 0, 2, 1 }),
                new Line(new int[] { 0, 0, 0 }),
                new Line(new int[] { 2, 2, 1 }));

        assertThat(
                validLines.stream()
                        .allMatch(line -> rules.isLineValid(line)))
                .isTrue();
    }

    @Test
    public void lineShouldNotBeValid() throws Exception {
        Set<Line> invalidLines = newLinkedHashSet(
                new Line(new int[] { 0, 0, -1}),
                new Line(new int[] { 3, 0, 0 }),
                new Line(new int[] { 0, 34, 0 }),
                new Line(new int[] { 0 }),
                new Line(null));

        assertThat(
                invalidLines.stream()
                        .noneMatch(line -> rules.isLineValid(line)))
                .isTrue();
    }

    @Test
    public void lineOutcomeShouldBeTen() throws Exception {
        Set<Line> linesWithOutcomeTen = newLinkedHashSet(
                new Line(new int[] { 0, 2, 0 }),
                new Line(new int[] { 1, 1, 0 }),
                new Line(new int[] { 1, 0, 1 }));

        assertThat(
                linesWithOutcomeTen.stream()
                        .allMatch(line -> rules.getLineOutcome(line) == OUTCOME_TEN))
                .isTrue();
    }

    @Test
    public void lineOutcomeShouldBeFive() throws Exception {
        Set<Line> linesWithOutcomeFive = newLinkedHashSet(
                new Line(new int[] { 0, 0, 0 }),
                new Line(new int[] { 1, 1, 1 }),
                new Line(new int[] { 2, 2, 2 }));

        assertThat(
                linesWithOutcomeFive.stream()
                        .allMatch(line -> rules.getLineOutcome(line) == OUTCOME_FIVE))
                .isTrue();
    }

    @Test
    public void lineOutcomeShouldBeOne() throws Exception {
        Set<Line> linesWithOutcomeOne = newLinkedHashSet(
                new Line(new int[] { 0, 2, 2 }),
                new Line(new int[] { 1, 0, 2 }),
                new Line(new int[] { 2, 1, 0 }));

        assertThat(
                linesWithOutcomeOne.stream()
                        .allMatch(line -> rules.getLineOutcome(line) == OUTCOME_ONE))
                .isTrue();
    }

    @Test
    public void lineOutcomeShouldBeZero() throws Exception {
        Set<Line> linesWithOutcomeZero = newLinkedHashSet(
                new Line(new int[] { 0, 1, 0 }),
                new Line(new int[] { 1, 1, 2 }),
                new Line(new int[] { 2, 2, 0 }));

        assertThat(
                linesWithOutcomeZero.stream()
                        .allMatch(line -> rules.getLineOutcome(line) == OUTCOME_ZERO))
                .isTrue();
    }

    @Test
    public void shouldThrowException() throws Exception {
        Line invalidLine1 = new Line(new int[] { 0, 0, -1});
        Throwable thrown = catchThrowable(() -> rules.getLineOutcome(invalidLine1));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Line contains invalid numbers.");
    }
}