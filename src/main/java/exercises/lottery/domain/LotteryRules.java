package exercises.lottery.domain;

/**
 * Definition of the generic rules for a lottery game.
 *
 * Created by guisil on 25/07/2016.
 */
public interface LotteryRules {

    /**
     * @return Size of the lines in a ticket.
     */
    int getLineSize();

    /**
     * @return Minimum value that a line can have.
     */
    int getMinLineValue();

    /**
     * @return Maximum value that a line can have.
     */
    int getMaxLineValue();

    /**
     * @return Maximum number of lines that a ticket can have.
     */
    int getMaxNumberOfLines();

    /**
     * @param line Line to be validated
     * @return true if the given line is valid
     */
    boolean isLineValid(Line line);

    /**
     * @param line Line to be checked
     * @return outcome of the given line
     */
    int getLineOutcome(Line line);
}
