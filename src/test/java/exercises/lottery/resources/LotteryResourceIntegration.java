package exercises.lottery.resources;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import exercises.lottery.LotteryApplication;
import exercises.lottery.domain.Line;
import exercises.lottery.domain.Ticket;
import exercises.lottery.domain.TicketBuilder;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Iterator;
import java.util.Set;

import static org.assertj.core.util.Sets.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Class for integration tests on LotteryResource.
 *
 * Created by guisil on 26/07/2016.
 */
@ActiveProfiles("integration")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { LotteryApplication.class })
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class LotteryResourceIntegration {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static Ticket ticket1, ticket2;
    private static Set<Line> extraLines, invalidLines;

    @Value("${local.server.port}")
    private int port;

    @BeforeClass
    public static void init() throws Exception {
        extraLines = newLinkedHashSet(
                new Line(new int[] { 0, 1, 0}),
                new Line(new int[] { 2, 2, 1}));

        invalidLines = newLinkedHashSet(
                new Line(new int[] { 0, 5, 0}),
                new Line(new int[] { 2, 2, 9}));

        ticket1 = new TicketBuilder().lines(
                newLinkedHashSet(
                        new Line(new int[] { 2, 0, 1}),
                        new Line(new int[] { 0, 0, 1})))
                .build();

        Line checkedLine1 = new Line(new int[] { 0, 2, 0});
        checkedLine1.setOutcome(10);
        Line checkedLine2 = new Line(new int[] { 2, 2, 1});
        checkedLine2.setOutcome(0);
        ticket2 = new TicketBuilder().checked().lines(
                newLinkedHashSet(checkedLine1, checkedLine2))
                .build();
    }

    @Before
    public void setUp() throws Exception {
        mongoTemplate.dropCollection("ticket");
        RestAssured.port = port;
    }

    @Test
    public void shouldNotFindAnyTicket() throws Exception {
        when().get("/lottery/tickets")
                .then().statusCode(HttpStatus.SC_OK)
                .and().body("", hasSize(0));
    }

    @Test
    public void shouldFindAllTickets() throws Exception {
        mongoTemplate.save(ticket1);
        mongoTemplate.save(ticket2);
        when().get("/lottery/tickets")
        .then().statusCode(HttpStatus.SC_OK)
        .and().body("", hasSize(2))
        .and().body("id", contains(ticket1.getId(), ticket2.getId()));
    }

    @Test
    public void shouldOnlyFindCheckedTicket() throws Exception {
        mongoTemplate.save(ticket1);
        mongoTemplate.save(ticket2);
        when().get("/lottery/tickets/checked")
                .then().statusCode(HttpStatus.SC_OK)
                .and().body("", hasSize(1))
                .and().body("id[0]", is(ticket2.getId()));
    }

    @Test
    public void shouldOnlyFindUncheckedTicket() throws Exception {
        mongoTemplate.save(ticket1);
        mongoTemplate.save(ticket2);
        when().get("/lottery/tickets/unchecked")
                .then().statusCode(HttpStatus.SC_OK)
                .and().body("", hasSize(1))
                .and().body("id[0]", is(ticket1.getId()));
    }

    @Test
    public void shouldNotFindTicketWithId() throws Exception {
        when().get("/lottery/tickets{id}", "111111")
                .then().statusCode(HttpStatus.SC_NOT_FOUND);
    }

    @Test
    public void shouldFindUncheckedTicketWithId() throws Exception {
        mongoTemplate.save(ticket1);
        when().get("/lottery/tickets/{id}", ticket1.getId())
                .then().statusCode(HttpStatus.SC_OK)
                .body("id", equalTo(ticket1.getId()))
                .body("checked", equalTo(ticket1.isChecked()))
                .body("lines.numbers", hasSize(2))
                .body("lines.numbers[0]", hasItems(2, 0, 1))
                .body("lines.numbers[1]", hasItems(0, 0, 1))
                .body("lines.outcome[0]", isEmptyOrNullString())
                .body("lines.outcome[1]", isEmptyOrNullString());
    }

    @Test
    public void shouldFindCheckedTicketWithId() throws Exception {
        mongoTemplate.save(ticket2);
        when().get("/lottery/tickets/{id}", ticket2.getId())
                .then().statusCode(HttpStatus.SC_OK)
                .body("id", equalTo(ticket2.getId()))
                .body("checked", equalTo(ticket2.isChecked()))
                .body("lines.numbers", hasSize(2))
                .body("lines.numbers[0]", hasItems(0, 2, 0))
                .body("lines.numbers[1]", hasItems(2, 2, 1))
                .body("lines.outcome[0]", notNullValue())
                .body("lines.outcome[1]", notNullValue());
    }

    @Test
    public void shouldGenerateTicket() throws Exception {
        when().post("/lottery/tickets/{numberOfLines}", 2)
                .then().statusCode(HttpStatus.SC_CREATED)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("checked", equalTo(false))
                .body("lines", hasSize(2));
    }

    @Test
    public void shouldGenerateTicketWithOneLineIfZeroIsIndicated() throws Exception {
        when().post("/lottery/tickets/{numberOfLines}", 0)
                .then().statusCode(HttpStatus.SC_CREATED)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .body("checked", equalTo(false))
                .body("lines", hasSize(1));
    }

    @Test
    public void shouldNotGenerateTicketForInvalidNumberOfLines() throws Exception {
        when().post("/lottery/tickets/{numberOfLines}", 28)
                .then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("error", notNullValue());
    }

    @Test
    public void shouldAmendTicket() throws Exception {
        mongoTemplate.save(ticket1);
        given().contentType(ContentType.JSON)
                .body(extraLines)
        .when().put("/lottery/tickets/{id}", ticket1.getId())
                .then().statusCode(HttpStatus.SC_OK)
                .body("lines", hasSize(4));
    }

    @Test
    public void shouldThrowErrorWhenAmendingCheckedTicket() throws Exception {
        mongoTemplate.save(ticket2);
        given().contentType(ContentType.JSON)
                .body(extraLines)
                .when().put("/lottery/tickets/{id}", ticket2.getId())
                .then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("error", notNullValue());
    }

    @Test
    public void shouldThrowErrorForAmendingWithInvalidLines() throws Exception {
        mongoTemplate.save(ticket1);
        given().contentType(ContentType.JSON)
                .body(invalidLines)
                .when().put("/lottery/tickets/{id}", ticket1.getId())
                .then().statusCode(HttpStatus.SC_BAD_REQUEST)
                .body("error", notNullValue());
    }

    @Test
    public void shouldThrowErrorWhenTicketToAmendNotFound() throws Exception {
        given().contentType(ContentType.JSON)
                .body(extraLines)
                .when().put("/lottery/tickets/{id}", "11111")
                .then().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body("error", notNullValue());
    }

    @Test
    public void shouldCheckTicket() throws Exception {
        mongoTemplate.save(ticket1);
        when().put("/lottery/tickets/{id}/check", ticket1.getId())
                .then().statusCode(HttpStatus.SC_OK)
                .body("checked", equalTo(true))
                .body("lines.outcome[0]", greaterThan(-1));
    }

    @Test
    public void shouldNotChangeCheckedTicket() throws Exception {
        mongoTemplate.save(ticket2);
        Iterator<Line> lineIterator = ticket2.getLines().iterator();
        int outcomeLine1 = lineIterator.next().getOutcome();
        int outcomeLine2 = lineIterator.next().getOutcome();
        when().put("/lottery/tickets/{id}/check", ticket2.getId())
                .then().statusCode(HttpStatus.SC_OK)
                .body("checked", equalTo(true))
                .body("lines", hasSize(2))
                .body("lines.outcome[0]", is(outcomeLine1))
                .body("lines.outcome[1]", is(outcomeLine2));
    }

    @Test
    public void shouldThrowErrorWhenTicketToCheckNotFound() throws Exception {
        given().contentType(ContentType.JSON)
                .when().put("/lottery/tickets/{id}/check", "11111")
                .then().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body("error", notNullValue());
    }
}
