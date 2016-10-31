package exercises.lottery.domain;

import com.fasterxml.jackson.annotation.JsonView;
import exercises.lottery.views.CheckedTicketView;
import exercises.lottery.views.UncheckedTicketView;

import java.util.Arrays;

/**
 * Class which represents a line in a lottery ticket.
 *
 * Created by guisil on 24/07/2016.
 */
public class Line {

    @JsonView({UncheckedTicketView.class, CheckedTicketView.class})
    private int[] numbers;

    @JsonView(CheckedTicketView.class)
    private int outcome = -1;

    public Line() {
    }

    public Line(int[] numbers) {
        this.numbers = numbers;
    }

    public Line(int[] numbers, int outcome) {
        this.numbers = numbers;
        this.outcome = outcome;
    }

    public int[] getNumbers() {
        return numbers;
    }

    public int getOutcome() {
        return outcome;
    }

    public void setOutcome(int outcome) {
        this.outcome = outcome;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Line)) {
            return false;
        }
        Line other = (Line) obj;
        return Arrays.equals(other.getNumbers(), numbers)
                && other.getOutcome() == outcome;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Arrays.hashCode(numbers);
        result = 31 * result + outcome;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder().append("Numbers: [ ");
        for (int num : numbers) {
            representation.append(num).append(" ");
        }
        representation.append("], Outcome: ").append(outcome);
        return representation.toString();
    }
}
