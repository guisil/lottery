package exercises.lottery.service;

import exercises.lottery.domain.Line;
import exercises.lottery.domain.LotteryRules;
import exercises.lottery.domain.Ticket;
import exercises.lottery.domain.TicketBuilder;
import exercises.lottery.domain.rules.SimpleLotteryRules;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.util.Sets.*;

/**
 * Test class for TicketService.
 *
 * Created by guisil on 25/07/2016.
 */
public class TicketServiceTest {

    private static Line[] testLines;
    private static Line[] testLinesWithOutcomes;
    private static final String firstTicketId = "1";
    private static Ticket firstTicketWithId;
    private static Ticket firstTicketWithId_Checked;
    private static Set<Line> extraLines;
    private static Ticket firstTicketWithExtraLines;

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    private LineService lineService;

    private LotteryRules rules;

    private TicketService ticketService;

    @BeforeClass
    public static void init() throws Exception {

        testLines = new Line[]{
                new Line(new int[]{2, 1, 0}),
                new Line(new int[]{0, 2, 0}),
                new Line(new int[]{1, 1, 1}),
                new Line(new int[]{0, 0, 2}),
                new Line(new int[]{1, 1, 2})
        };

        testLinesWithOutcomes = new Line[]{
                new Line(new int[]{2, 1, 0}, 1),
                new Line(new int[]{0, 2, 0}, 10),
                new Line(new int[]{1, 1, 1}, 5),
                new Line(new int[]{0, 0, 2}, 0),
                new Line(new int[]{1, 1, 2}, 0)
        };

        firstTicketWithId = createTicket(false, firstTicketId, testLines[0], testLines[1]);
        firstTicketWithId_Checked = createTicket(true, firstTicketId, testLinesWithOutcomes[0], testLinesWithOutcomes[1]);

        extraLines = newLinkedHashSet(testLines[2], testLines[3]);

        firstTicketWithExtraLines =
                createTicketWithExtraLines(firstTicketWithId, testLines[2], testLines[3]);
    }

    @Before
    public void setUp() throws Exception {
        rules = new SimpleLotteryRules();
        ticketService = new TicketService(rules, lineService);
    }


    @Test
    public void shouldGenerateNewTicket() throws Exception {
        int numberOfLines = 5;
        Set<Line> expectedLines = new LinkedHashSet<>();
        expectedLines.addAll(Arrays.asList(testLines));
        Ticket expectedTicket = new Ticket(expectedLines);
        given(lineService.generateNewLine(any(Set.class)))
                .willReturn(testLines[0], testLines[1], testLines[2], testLines[3], testLines[4]);
        assertThat(ticketService.generateNewTicket(numberOfLines)).isEqualTo(expectedTicket);
    }

    @Test
    public void shouldThrowExceptionWithInvalidNumberOfLines() throws Exception {
        int invalidNumberOfLines = rules.getMaxNumberOfLines() + 1;
        Throwable thrown = catchThrowable(() -> ticketService.generateNewTicket(invalidNumberOfLines));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Invalid number of lines.");
    }

    @Test
    public void shouldAmendUncheckedTicket() throws Exception {
        assertThat(ticketService.amendTicket(firstTicketWithId, extraLines)).isEqualTo(firstTicketWithExtraLines);
    }

    @Test
    public void shouldThrowExceptionForCheckedTicket() throws Exception {
        Throwable thrown = catchThrowable(() -> ticketService.amendTicket(firstTicketWithId_Checked, extraLines));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Checked tickets cannot be amended.");
    }

    @Test
    public void shouldThrowExceptionWhenAddingTooManyLines() throws Exception {
        Line excluded = new Line(new int[] { 1, 2, 0 });
        Set<Line> linesToAdd = newLinkedHashSet(excluded, new Line(new int[] { 1, 1, 1, }));
        Set<Line> allPossibleLinesButOne = getAllPossibleLines();
        allPossibleLinesButOne.remove(excluded);
        Ticket ticketToAmend = new TicketBuilder().id("10").lines(allPossibleLinesButOne).build();

        Throwable thrown = catchThrowable(() -> ticketService.amendTicket(ticketToAmend, linesToAdd));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Invalid number of lines.");
    }

    @Test
    public void shouldThrowExceptionWhenThereAreInvalidLines() throws Exception {
        Set<Line> invalidLines = newLinkedHashSet(
                new Line(new int[] { 1 }),
                new Line(new int[] { 2, 1, 4 }));
        Throwable thrown = catchThrowable(() -> ticketService.amendTicket(firstTicketWithId, invalidLines));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Invalid line(s).");
    }

    @Test
    public void shouldCheckTicket() throws Exception {
        given(lineService.determineLineOutcome(any(Line.class)))
                .willReturn(testLinesWithOutcomes[0], testLinesWithOutcomes[1]);
        assertThat(ticketService.checkTicket(firstTicketWithId)).isEqualTo(firstTicketWithId_Checked);
    }

    @Test
    public void shouldDoNothingIfTicketIsAlreadyChecked() throws Exception {
        assertThat(ticketService.checkTicket(firstTicketWithId_Checked)).isEqualTo(firstTicketWithId_Checked);
        verifyZeroInteractions(lineService);
    }


    private static Ticket createTicket(boolean checked, String id, Line... lines) {
        if (checked) {
            return new TicketBuilder().id(id).lines(new LinkedHashSet<>(Arrays.asList(lines))).checked().build();
        } else {
            return new TicketBuilder().id(id).lines(new LinkedHashSet<>(Arrays.asList(lines))).build();
        }
    }

    private static Ticket createTicketWithExtraLines(Ticket ticket, Line... extraLines) {
        Ticket newTicket = new TicketBuilder().id(ticket.getId()).lines(ticket.getLines()).build();
        newTicket.addLines(new LinkedHashSet<>(Arrays.asList(extraLines)));
        return newTicket;
    }

    private Set<Line> getAllPossibleLines() {
        Set<Line> allLines = new LinkedHashSet<>();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    allLines.add(new Line(new int[] { i, j, k }));
                }
            }
        }
        return allLines;
    }
}