package exercises.lottery.data;

import exercises.lottery.domain.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DAO class for MongoDB.
 *
 * Created by guisil on 25/07/2016.
 */
@Component
public class LotteryDao {

    private static final Logger logger = LoggerFactory.getLogger(LotteryDao.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    LotteryDao(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * @return All tickets in the database.
     */
    public List<Ticket> getAllTickets() {
        logger.info("Retrieving all tickets in the database");
        return mongoTemplate.findAll(Ticket.class);
    }

    /**
     * @param ticketId ID of the ticket to retrieve
     * @return Ticket with the given ID
     */
    public Ticket getTicketById(String ticketId) {
        logger.info("Retrieving ticket with ID {} from the database", ticketId);
        Query findTicketQuery = new Query(Criteria.where("_id").is(ticketId));
        return mongoTemplate.findOne(findTicketQuery, Ticket.class);
    }

    /**
     * @param checked Checked parameter of the tickets to retrieve
     * @return Tickets with the given checked value
     */
    public List<Ticket> getTicketsByChecked(boolean checked) {
        logger.info("Retrieving all {} tickets from the database", checked ? "checked" : "unchecked");
        Query findTicketQuery = new Query(Criteria.where("checked").is(checked));
        return mongoTemplate.find(findTicketQuery, Ticket.class);
    }

    /**
     * @param ticket Ticket to insert in the database
     * @return Inserted Ticket
     */
    public Ticket createTicket(Ticket ticket) {
        logger.info("Inserting ticket in the database: {}", ticket);
        mongoTemplate.insert(ticket);
        return ticket;
    }

    /**
     * @param ticket Ticket to amend
     * @return Amended ticket
     */
    public Ticket amendTicket(Ticket ticket) {
        logger.info("Amending ticket in the database: ", ticket);
        Query query = new Query(Criteria.where("_id").is(ticket.getId()));
        Update update = new Update();
        update.set("lines", ticket.getLines());
        if (mongoTemplate.updateFirst(query, update, Ticket.class).getN() != 1) {
            logger.error("Error amending ticket {}", ticket);
            throw new EmptyResultDataAccessException("Error amending ticket.", 1);
        }
        return ticket;
    }

    /**
     * @param ticket Ticket to check
     * @return Checked ticket
     */
    public Ticket checkTicket(Ticket ticket) {
        logger.info("Checking ticket in the database: {}", ticket);
        Query query = new Query(Criteria.where("_id").is(ticket.getId()));
        Update updateChecked = new Update();
        updateChecked.set("checked", ticket.isChecked());
        if (mongoTemplate.updateFirst(query, updateChecked, Ticket.class).getN() != 1) {
            logger.error("Error checking ticket {}", ticket);
            throw new EmptyResultDataAccessException("Error checking ticket.", 1);
        }
        Update updateLines = new Update();
        updateLines.set("lines", ticket.getLines());
        if (mongoTemplate.updateFirst(query, updateLines, Ticket.class).getN() != 1) {
            logger.error("Error updating checked lines in ticket {}", ticket);
            throw new EmptyResultDataAccessException("Error updating checked lines in ticket.", 1);
        }
        return ticket;
    }
}
