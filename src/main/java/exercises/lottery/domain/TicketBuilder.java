package exercises.lottery.domain;

import java.util.Set;

/**
 * Builder class for Ticket objects.
 *
 * Created by guisil on 26/07/2016.
 */
public class TicketBuilder {

    private Ticket ticket = new Ticket();

    public TicketBuilder id(String id) {
        ticket.setId(id);
        return this;
    }

    public TicketBuilder checked() {
        ticket.setChecked(true);
        return this;
    }

    public TicketBuilder lines(Set<Line> lines) {
        ticket.addLines(lines);
        return this;
    }

    public Ticket build() {
        return ticket;
    }
}
