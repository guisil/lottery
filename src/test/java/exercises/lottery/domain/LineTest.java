package exercises.lottery.domain;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Test class for Line.
 *
 * Created by guisil on 25/07/2016.
 */
public class LineTest {

    private static final Line line1 = new Line(new int[] { 0, 1, 0});
    private static final Line line2 = new Line(new int[] { 0, 1, 0});
    private static final Line line3 = new Line(new int[] { 0, 1, 0});
    private static final Line line4 = new Line(new int[] { 0, 1, 2});


    @Test
    public void testEquals() throws Exception {
        assertThat(line1).as("Checking if equals is reflexive").isEqualTo(line1);
        assertThat(line1.equals(line2) && line2.equals(line1)).as("Checking if equals is symmetric").isTrue();
        assertThat(line1.equals(line2) && line2.equals(line3) && line1.equals(line3)).as("Checking if equals is transitive").isTrue();
        assertThat(line1.equals(line2) && line1.equals(line2) && line1.equals(line2)).as("Checking if equals is consistent").isTrue();
        assertThat(line1).as("Checking equals with null parameter").isNotEqualTo(null);
        assertThat(line1).as("Checking equals for different objects").isNotEqualTo(line4);
    }

    @Test
    public void testHashCode() throws Exception {
        assertThat(line1.hashCode()).as("Checking hashCode for the same object").isEqualTo(line1.hashCode());
        assertThat(line1.hashCode()).as("Checking hashCode for equal objects").isEqualTo(line2.hashCode());
    }
}