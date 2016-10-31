package exercises.lottery.domain.rules;

import exercises.lottery.domain.Line;
import exercises.lottery.domain.LotteryRules;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

/**
 * Implementation of LotteryRules.
 *
 * Created by guisil on 25/07/2016.
 */
@Component
public class SimpleLotteryRules implements LotteryRules {

    private final int lineSize = 3;
    private final int minLineValue = 0;
    private final int maxLineValue = 2;

    private final int maximumScoreSum = 2;

    private final int OUTCOME_TEN = 10;
    private final int OUTCOME_FIVE = 5;
    private final int OUTCOME_ONE = 1;
    private final int OUTCOME_ZERO = 0;


    /**
     *{@inheritDoc}
     */
    @Override
    public int getLineSize() {
        return lineSize;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int getMinLineValue() {
        return minLineValue;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int getMaxLineValue() {
        return maxLineValue;
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int getMaxNumberOfLines() {
        int numberOfTypes = maxLineValue + 1 - minLineValue;
        int numberOfTimes = lineSize;
        return (int) Math.pow(numberOfTypes, numberOfTimes);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public boolean isLineValid(Line line) {
        if (line.getNumbers() == null || line.getNumbers().length != lineSize) {
            return false;
        }
        return IntStream.of(line.getNumbers())
                .allMatch(n -> n >= minLineValue && n <= maxLineValue);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public int getLineOutcome(Line line) {
        if (!isLineValid(line)) {
            throw new IllegalArgumentException("Line contains invalid numbers.");
        }
        if (IntStream.of(line.getNumbers()).sum() == maximumScoreSum) {
            return OUTCOME_TEN;
        }
        int first = line.getNumbers()[0];
        if (IntStream.of(line.getNumbers()).allMatch(n -> n == first)) {
            return OUTCOME_FIVE;
        }
        if (IntStream.range(1, lineSize).noneMatch(i -> line.getNumbers()[i] == first)) {
            return OUTCOME_ONE;
        }
        return OUTCOME_ZERO;
    }
}
