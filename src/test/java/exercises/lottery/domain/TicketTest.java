package exercises.lottery.domain;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.util.Sets.*;

/**
 * Test class for Ticket.
 *
 * Created by guisil on 25/07/2016.
 */
public class TicketTest {

    private static Ticket ticket1, ticket2, ticket3, ticket4, ticket5, ticket6;

    @BeforeClass
    public static void init() throws Exception {
        final Line line1 = new Line(new int[] { 0, 1, 0});
        final Line line2 = new Line(new int[] { 0, 1, 0});
        final Line line3 = new Line(new int[] { 0, 1, 0});
        ticket1 = new TicketBuilder().id("id1").lines(newLinkedHashSet(line1, line2)).build();
        ticket2 = new TicketBuilder().id("id1").lines(newLinkedHashSet(line1, line2)).build();
        ticket3 = new TicketBuilder().id("id1").lines(newLinkedHashSet(line1, line2)).build();
        ticket4 = new TicketBuilder().id("id1").lines(newLinkedHashSet(line2, line3)).build();
        ticket5 = new TicketBuilder().lines(newLinkedHashSet(line2, line3)).build();
        ticket6 = new TicketBuilder().id("id6").lines(newLinkedHashSet(new Line(new int[] { 2, 2, 2 }))).build();
    }

    @Test
    public void testEquals() throws Exception {
        assertThat(ticket1).as("Checking if equals is reflexive").isEqualTo(ticket1);
        assertThat(ticket1.equals(ticket2) && ticket2.equals(ticket1)).as("Checking if equals is symmetric").isTrue();
        assertThat(ticket1.equals(ticket2) && ticket2.equals(ticket3) && ticket1.equals(ticket3)).as("Checking if equals is transitive").isTrue();
        assertThat(ticket1.equals(ticket2) && ticket1.equals(ticket2) && ticket1.equals(ticket2)).as("Checking if equals is consistent").isTrue();
        assertThat(ticket1).as("Checking equals with null parameter").isNotEqualTo(null);
        assertThat(ticket4).as("Checking equals with null IDs (second object)").isNotEqualTo(ticket5);
        assertThat(ticket5).as("Checking equals with null IDs (first object)").isNotEqualTo(ticket4);
        assertThat(ticket1).as("Checking equals for different objects").isNotEqualTo(ticket6);
    }

    @Test
    public void testHashCode() throws Exception {
        assertThat(ticket1.hashCode()).as("Checking hashCode for the same object").isEqualTo(ticket1.hashCode());
        assertThat(ticket1.hashCode()).as("Checking hashCode for equal objects").isEqualTo(ticket2.hashCode());
    }

    @Test
    public void shouldNotAddRepeatedLines() throws Exception {
        Set<Line> lines = newLinkedHashSet(
                new Line(new int[] { 0, 1, 2 }),
                new Line(new int[] { 1, 1, 1 }));
        Ticket aTicket = new TicketBuilder().lines(lines).build();
        Line newLine1 = new Line(new int[] { 1, 1, 1 });
        Set<Line> newLines = newLinkedHashSet(newLine1);
        aTicket.addLines(newLines);
        assertThat(aTicket.getLines()).hasSize(lines.size());
    }

    @Test
    public void shouldAddLines() throws Exception {
        Set<Line> lines = newLinkedHashSet(
                new Line(new int[] { 0, 1, 2 }),
                new Line(new int[] { 1, 1, 1 }));
        Ticket aTicket = new TicketBuilder().lines(lines).build();
        Line newLine1 = new Line(new int[] { 0, 1, 1 });
        Set<Line> newLines = newLinkedHashSet(newLine1);
        aTicket.addLines(newLines);
        assertThat(aTicket.getLines()).contains(newLine1);
    }
}