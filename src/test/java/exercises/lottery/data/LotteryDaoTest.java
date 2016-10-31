package exercises.lottery.data;

import exercises.lottery.config.TestMongoConfig;
import exercises.lottery.domain.Line;
import exercises.lottery.domain.Ticket;
import exercises.lottery.domain.TicketBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.util.Sets.*;

/**
 * Test class for LotteryDao.
 *
 * Created by guisil on 25/07/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestMongoConfig.class })
@ActiveProfiles("testing")
public class LotteryDaoTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    private LotteryDao lotteryDao;


    @Before
    public void setUp() throws Exception {
        lotteryDao = new LotteryDao(mongoTemplate);
    }

    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Ticket.class);
    }


    @Test
    public void shouldFindNoTickets() throws Exception {
        assertThat(lotteryDao.getAllTickets()).isEmpty();
    }

    @Test
    public void shouldFindAllTickets() throws Exception {
        Ticket firstTicket = getExampleTicket();
        mongoTemplate.insert(firstTicket);
        Ticket secondTicket = getExampleTicket();
        mongoTemplate.insert(secondTicket);
        List<Ticket> finalTickets = lotteryDao.getAllTickets();
        assertThat(finalTickets).containsOnly(firstTicket, secondTicket);
    }

    @Test
    public void shouldNotFindOneTicket() throws Exception {
        assertThat(lotteryDao.getTicketById("something")).isNull();
    }

    @Test
    public void shouldFindOneTicket() throws Exception {
        Ticket expected = getExampleTicket();
        mongoTemplate.insert(expected);
        assertThat(lotteryDao.getTicketById(expected.getId())).isEqualTo(expected);
    }

    @Test
    public void shouldFindUncheckedTickets() throws Exception {
        Ticket uncheckedTicket = getExampleTicket();
        mongoTemplate.insert(uncheckedTicket);
        Ticket checkedTicket = getExampleTicket();
        checkedTicket.setChecked(true);
        mongoTemplate.insert(checkedTicket);

        assertThat(lotteryDao.getTicketsByChecked(false)).containsOnly(uncheckedTicket);
    }

    @Test
    public void shouldFindCheckedTickets() throws Exception {
        Ticket uncheckedTicket = getExampleTicket();
        mongoTemplate.insert(uncheckedTicket);
        Ticket checkedTicket = getExampleTicket();
        checkedTicket.setChecked(true);
        mongoTemplate.insert(checkedTicket);
        assertThat(lotteryDao.getTicketsByChecked(true)).containsOnly(checkedTicket);
    }

    @Test
    public void shouldCreateTicket() throws Exception {
        Ticket expected = getExampleTicket();
        assertThat(expected.getId()).isNullOrEmpty();
        lotteryDao.createTicket(expected);
        assertThat(expected.getId()).isNotEmpty();
        Ticket retrieved = mongoTemplate.findOne(new Query(Criteria.where("_id").is(expected.getId())), Ticket.class);
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    public void shouldThrowExceptionWhenInsertingWithExistingId() throws Exception {
        Ticket inserted = insertTicketInDb();
        // Try to insert it again
        Throwable thrown = catchThrowable(() -> lotteryDao.createTicket(inserted));
        assertThat(thrown).isInstanceOf(DuplicateKeyException.class);
    }

    @Test
    public void shouldUpdateLines() throws Exception {
        Ticket initial = getExampleTicket();
        assertThat(initial.getId()).isNullOrEmpty();
        mongoTemplate.insert(initial);
        assertThat(initial.getId()).isNotEmpty();
        Ticket updated = addLinesToTicket(initial);
        lotteryDao.amendTicket(updated);
        Ticket retrieved = mongoTemplate.findOne(new Query(Criteria.where("_id").is(initial.getId())), Ticket.class);
        assertThat(retrieved).isEqualTo(updated);
    }

    @Test
    public void shouldThrowExceptionWhenAmendingNonExistingTicket() throws Exception {
        Ticket nonExistingTicket = getExampleTicket();
        nonExistingTicket.setId("something");
        Throwable thrown = catchThrowable(() -> lotteryDao.amendTicket(nonExistingTicket));
        assertThat(thrown).isInstanceOf(EmptyResultDataAccessException.class).hasMessage("Error amending ticket.");
    }

    @Test
    public void shouldCheckTicketAndAddOutcomes() {
        Ticket initial = getExampleTicket();
        assertThat(initial.getId()).isNullOrEmpty();
        mongoTemplate.insert(initial);
        assertThat(initial.getId()).isNotEmpty();
        Ticket checked = checkTicket(initial);
        lotteryDao.checkTicket(checked);
        Ticket retrieved = mongoTemplate.findOne(new Query(Criteria.where("_id").is(initial.getId())), Ticket.class);
        assertThat(retrieved).isEqualTo(checked);
    }

    @Test
    public void shouldThrowExceptionWhenCheckingNonExistingTicket() throws Exception {
        Ticket nonExistingTicket = getExampleTicket();
        nonExistingTicket.setId("something");
        Throwable thrown = catchThrowable(() -> lotteryDao.checkTicket(nonExistingTicket));
        assertThat(thrown).isInstanceOf(EmptyResultDataAccessException.class).hasMessage("Error checking ticket.");
    }

    private Ticket getExampleTicket() {
        Set<Line> lines = newLinkedHashSet(
                new Line(new int[] { 0, 1, 2}),
                new Line(new int[] { 1, 0, 1}));
        return new TicketBuilder().lines(lines).build();
    }

    private Ticket addLinesToTicket(Ticket ticket) {
        Set<Line> lines = ticket.getLines();
        lines.add(new Line(new int[] { 1, 1, 0 }));
        lines.add(new Line(new int[] { 2, 2, 2 }));
        ticket.setLines(lines);
        return ticket;
    }

    private Ticket checkTicket(Ticket ticket) {
        Set<Line> lines = ticket.getLines();
        for (Line line : lines) {
            line.setOutcome(0);
        }
        return new TicketBuilder().id(ticket.getId()).checked().lines(lines).build();
    }

    private Ticket insertTicketInDb() {
        Ticket inserted = getExampleTicket();
        mongoTemplate.insert(inserted);
        assertThat(inserted.getId()).isNotEmpty();
        return inserted;
    }
}