package exercises.lottery.service;

import exercises.lottery.domain.Line;
import exercises.lottery.domain.LotteryRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.Set;

/**
 * Class which provides essential
 * functionality for Line objects.
 *
 * Created by guisil on 25/07/2016.
 */
@Service
class LineService {

    private static final Logger logger = LoggerFactory.getLogger(LineService.class);

    private final LotteryRules rules;

    @Autowired
    LineService(LotteryRules rules) {
        this.rules = rules;
    }

    /**
     * Generates a new line.
     * @param existingLines Set containing the Ticket's existing lines
     * @return Line object which is different from the existing ones
     */
    Line generateNewLine(Set<Line> existingLines) {
        logger.info("Generating new line. There are {} existing lines in the ticket.", existingLines.size());
        if (existingLines.size() >= rules.getMaxNumberOfLines()) {
            logger.error("All possible lines ({}) were already generated", existingLines.size());
            throw new IllegalArgumentException("All possible lines were already generated.");
        }

        int[] numbers = new int[rules.getLineSize()];
        int minLineNumber = rules.getMinLineValue();
        int maxLineNumber = rules.getMaxLineValue();
        Random random = new Random();

        Line newLine;
        do {
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = random.nextInt((maxLineNumber - minLineNumber) + 1) + minLineNumber;
            }
            newLine = new Line(numbers);
        } while (existingLines.contains(newLine));

        return newLine;
    }

    /**
     * Determines the outcome of the given Line.
     * @param line Line to check
     * @return Line with the outcome
     */
    Line determineLineOutcome(Line line) {
        logger.info("Determining outcome of line {}", line);
        if (!rules.isLineValid(line)) {
            logger.error("Invalid line: {}", line);
            throw new IllegalArgumentException("Invalid line.");
        }

        int outcome = rules.getLineOutcome(line);
        Line lineWithOutcome = new Line(line.getNumbers());
        lineWithOutcome.setOutcome(outcome);
        return lineWithOutcome;
    }
}
