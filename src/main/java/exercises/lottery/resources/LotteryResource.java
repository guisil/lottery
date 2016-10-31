package exercises.lottery.resources;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import exercises.lottery.data.LotteryDao;
import exercises.lottery.domain.Line;
import exercises.lottery.domain.Ticket;
import exercises.lottery.service.TicketService;
import exercises.lottery.views.CheckedTicketView;
import exercises.lottery.views.UncheckedTicketView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * Class providing the REST interface.
 *
 * Created by guisil on 24/07/2016.
 */
@Component
@Path("/tickets")
public class LotteryResource {

    private static final Logger logger = LoggerFactory.getLogger(LotteryResource.class);

    private final LotteryDao lotteryDao;
    private final TicketService ticketService;

    @Autowired
    @Qualifier("defaultNumberOfLines")
    private int defaultNumberOfLines;

    @Autowired
    public LotteryResource(LotteryDao lotteryDao, TicketService ticketService) {
        this.lotteryDao = lotteryDao;
        this.ticketService = ticketService;
    }


    /**
     * GET method that retrieves all tickets.
     * @return all tickets in the database
     */
    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Ticket> getAllTickets() {
        logger.info("Received GET request to retrieve all tickets");
        return lotteryDao.getAllTickets();
    }

    /**
     * GET method that retrieves the ticket with the given ID
     * @param id path parameter with the ID of the ticket to retrieve
     * @return ticket with the given ID
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTicket(@PathParam("id") String id) {
        logger.info("Received GET request to retrieve ticket with 'id' {}", id);
        try {
            Ticket retrieved = lotteryDao.getTicketById(id);
            if (retrieved == null) {
                logger.error("Ticket not found for 'id' {}", id);
                JsonObject errorObject = createJsonErrorObject(null, "Ticket not found for 'id': " + id);
                return Response.status(Response.Status.NOT_FOUND).entity(errorObject).build();
            }
            String result = getJsonObjectWithAppropriateView(retrieved);
            return Response.ok().entity(result).build();
        } catch (Exception ex) {
            logger.error("Error processing request", ex);
            JsonObject errorObject = createJsonErrorObject(ex, null);
            return Response.serverError().entity(errorObject).build();
        }
    }

    /**
     * GET method that retrieves all the unchecked tickets
     * @return all the unchecked tickets in the database
     */
    @JsonView(UncheckedTicketView.class)
    @GET
    @Path("unchecked")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Ticket> getUncheckedTickets() {
        logger.info("Received GET request to retrieve all unchecked tickets");
        return lotteryDao.getTicketsByChecked(false);
    }

    /**
     * GET method that retrieves all the checked tickets
     * @return all the checked tickets in the database
     */
    @JsonView(CheckedTicketView.class)
    @GET
    @Path("checked")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Ticket> getCheckedTickets() {
        logger.info("Received GET request to retrieve all checked tickets");
        return lotteryDao.getTicketsByChecked(true);
    }

    /**
     * POST method that receives a number and generates a ticket
     * with that amount of lines.
     * @param numberOfLines number of lines to generate
     * @return generated ticket
     */
    @POST
    @Path("{numberOfLines}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateTicket(@PathParam("numberOfLines") int numberOfLines, @Context UriInfo uriInfo) {
        logger.info("Received POST request to generate a ticket with {} lines", numberOfLines);
        try {
            Ticket generated = ticketService.generateNewTicket(numberOfLines <= 0 ? defaultNumberOfLines : numberOfLines);
            Ticket stored = lotteryDao.createTicket(generated);
            String ticketsPathSegment = uriInfo.getPathSegments().get(0).toString();
            URI uri = uriInfo.getBaseUriBuilder().path(ticketsPathSegment).path(stored.getId()).build();
            String result = getJsonObjectWithAppropriateView(stored);
            return Response.created(uri).entity(result).build();
        } catch (IllegalArgumentException ex) {
            logger.error("Error generating the ticket", ex);
            JsonObject errorObject = createJsonErrorObject(ex, null);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorObject).build();
        } catch (Exception ex) {
            logger.error("Error generating the ticket", ex);
            JsonObject errorObject = createJsonErrorObject(ex, null);
            return Response.serverError().entity(errorObject).build();
        }
    }

    /**
     * PUT method that receives an ID of a ticket and a JSON representation
     * of a set of lines, and adds those lines to the ticket.
     * @param id ID of the ticket to amend
     * @param lines Set of lines to add to the ticket
     * @return ticket with the added lines
     */
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response amendTicket(@PathParam("id") String id, Set<Line> lines) {
        logger.info("Received PUT request to amend ticket with 'id' {}", id);
        try {
            Ticket existing = lotteryDao.getTicketById(id);
            if (existing == null) {
                throw new IllegalStateException("Ticket not found");
            }
            Ticket amended = ticketService.amendTicket(existing, lines);
            Ticket stored = lotteryDao.amendTicket(amended);
            String result = getJsonObjectWithAppropriateView(stored);
            return Response.ok().entity(result).build();
        } catch (IllegalArgumentException ex) {
            logger.error("Error amending ticket", ex);
            JsonObject errorObject = createJsonErrorObject(ex, null);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorObject).build();
        } catch (Exception ex) {
            logger.error("Error amending ticket", ex);
            JsonObject errorObject = createJsonErrorObject(ex, null);
            return Response.serverError().entity(errorObject).build();
        }
    }

    /**
     * PUT method that receives an ID of a ticket
     * and checks the ticket, including the outcome of each line.
     * @param id ID of the ticket to check
     * @return checked ticket
     */
    @PUT
    @Path("{id}/check")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkTicket(@PathParam("id") String id) {
        logger.info("Received PUT request to check ticket with 'id' {}", id);
        try {
            Ticket existing = lotteryDao.getTicketById(id);
            if (existing == null) {
                throw new IllegalStateException("Ticket not found");
            }
            Ticket checked = ticketService.checkTicket(existing);
            Ticket stored = lotteryDao.checkTicket(checked);
            String result = getJsonObjectWithAppropriateView(stored);
            return Response.ok().entity(result).build();
        } catch (IllegalArgumentException ex) {
            logger.error("Error checking ticket", ex);
            JsonObject errorObject = createJsonErrorObject(ex, null);
            return Response.status(Response.Status.BAD_REQUEST).entity(errorObject).build();
        } catch (Exception ex) {
            logger.error("Error checking ticket", ex);
            JsonObject errorObject = createJsonErrorObject(ex, null);
            return Response.serverError().entity(errorObject).build();
        }
    }


    private JsonObject createJsonErrorObject(Exception ex, String message) {
        String messageToUse = message;
        if (ex != null) {
            if (ex.getMessage() != null) {
                messageToUse = ex.getMessage();
            } else {
                messageToUse = ex.getClass().toString();
            }
        }
        return Json.createObjectBuilder().add("error",messageToUse).build();
    }

    private String getJsonObjectWithAppropriateView(Ticket object) throws JsonProcessingException {
        Class viewToUse = object.isChecked() ? CheckedTicketView.class : UncheckedTicketView.class;
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithView(viewToUse).writeValueAsString(object);
    }
}
