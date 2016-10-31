package exercises.lottery.service;

import exercises.lottery.domain.Line;
import exercises.lottery.domain.LotteryRules;
import exercises.lottery.domain.Ticket;
import exercises.lottery.domain.TicketBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Class which provides the essential
 * functionality for Ticket objects.
 *
 * Created by guisil on 25/07/2016.
 */
@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    private final LotteryRules rules;
    private final LineService lineService;

    @Autowired
    TicketService(LotteryRules rules, LineService lineService) {
        this.rules = rules;
        this.lineService = lineService;
    }

    /**
     * Generates a new ticket, given a number of lines.
     * @param numberOfLines Amount of lines to generate in the ticket
     * @return Ticket object with the given amount of lines
     */
    public Ticket generateNewTicket(int numberOfLines) {
        logger.info("Generating new ticket with {} lines", numberOfLines);
        if (numberOfLines > rules.getMaxNumberOfLines()) {
            logger.error("Invalid number of lines ({}).", numberOfLines);
            throw new IllegalArgumentException("Invalid number of lines.");
        }

        Set<Line> lines = new LinkedHashSet<>();
        IntStream.range(0, numberOfLines)
                .forEach(i -> lines.add(lineService.generateNewLine(lines)));
        return new Ticket(lines);
    }

    /**
     * Amends the given ticket by adding it lines.
     * @param ticket Ticket object to amend
     * @param newLines Set of Line objects to add to the Ticket
     * @return Ticket object containing all the intended lines
     */
    public Ticket amendTicket(Ticket ticket, Set<Line> newLines) {
        logger.info("Amending ticket {} with {} new lines", ticket.getId(), newLines.size());
        if (ticket.isChecked()) {
            logger.error("Checked tickets cannot be amended.");
            throw new IllegalArgumentException("Checked tickets cannot be amended.");
        }

        if (ticket.getLines().size() + newLines.size() > rules.getMaxNumberOfLines()) {
            logger.error("Invalid number of lines ({}).", ticket.getLines().size() + newLines.size());
            throw new IllegalArgumentException("Invalid number of lines.");
        }

        if (!newLines.stream().allMatch(line -> rules.isLineValid(line))) {
            logger.error("Invalid line(s).");
            throw new IllegalArgumentException(("Invalid line(s)."));
        }

        Ticket toReturn = new TicketBuilder().id(ticket.getId()).lines(ticket.getLines()).build();
        toReturn.addLines(newLines);
        return toReturn;
    }

    /**
     * Checks the given ticket for the outcomes of its lines.
     * @param ticket Ticket object to check
     * @return Ticket object with all the outcomes of its lines included
     */
    public Ticket checkTicket(Ticket ticket) {
        logger.info("Checking outcomes of ticket {}", ticket.getId());
        if (ticket.isChecked()) {
            logger.info("Ticket {} is already checked. Nothing will be done.", ticket.getId());
            return ticket;
        }

        Set<Line> checkedLines = new LinkedHashSet<>();
        ticket.getLines().forEach(line -> checkedLines.add(lineService.determineLineOutcome(line)));

        return new TicketBuilder().id(ticket.getId()).checked().lines(checkedLines).build();
    }
}
