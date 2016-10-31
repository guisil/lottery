package exercises.lottery.service;

import exercises.lottery.domain.Line;
import exercises.lottery.domain.LotteryRules;
import exercises.lottery.domain.rules.SimpleLotteryRules;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.util.Sets.newLinkedHashSet;

/**
 * Test class for LineService.
 *
 * Created by guisil on 25/07/2016.
 */
public class LineServiceTest {

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    private static Set<Line> allPossibleLines;
    private static Set<Line> testLines;

    private LotteryRules rules;

    private LineService lineService;

    @BeforeClass
    public static void init() throws Exception {

        allPossibleLines = getAllPossibleLines();
        testLines = newLinkedHashSet(
                new Line(new int[]{2, 1, 0}),
                new Line(new int[]{0, 2, 0}),
                new Line(new int[]{1, 1, 1}),
                new Line(new int[]{0, 0, 2}),
                new Line(new int[]{1, 1, 2})
        );
    }

    @Before
    public void setUp() throws Exception {
        rules = new SimpleLotteryRules();
        lineService = new LineService(rules);
    }


    @Test
    public void newLineShouldHaveCorrectSize() throws Exception {
        Line newLine = lineService.generateNewLine(new LinkedHashSet<>());
        assertThat(newLine.getNumbers()).hasSize(rules.getLineSize());
    }

    @Test
    public void newLineShouldHaveCorrectValues() throws Exception {
        Line newLine = lineService.generateNewLine(new LinkedHashSet<>());
        assertThat(IntStream.of(newLine.getNumbers())
                .allMatch(n -> n >= rules.getMinLineValue() && n <= rules.getMaxLineValue()))
                .isTrue();
    }

    @Test
    public void newLinesAreDifferent() throws Exception {
        Line newLine = lineService.generateNewLine(testLines);
        assertThat(testLines).doesNotContain(newLine);
    }

    @Test
    public void newLineHasOnlyOnePossibleValue() throws Exception {
        Line excludedLine = new Line(new int[] { 1, 2, 1});
        Set<Line> allLinesButOne = getAllPossibleLines();
        allLinesButOne.remove(excludedLine);
        Line newLine = lineService.generateNewLine(allLinesButOne);
        assertThat(newLine).isEqualTo(excludedLine);
    }

    @Test
    public void shouldThrowExceptionIfAllHaveAlreadyBeenGenerated() throws Exception {
        Throwable thrown = catchThrowable(() -> lineService.generateNewLine(allPossibleLines));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("All possible lines were already generated.");
    }

    @Test
    public void shouldRetrieveLineWithOutcome() throws Exception {
        int[] lineNumbers = new int[] { 0, 1, 2 };
        Line existingLine = new Line(lineNumbers);
        int expectedOutcome = 1;
        Line expectedLine = new Line(lineNumbers);
        expectedLine.setOutcome(expectedOutcome);
        assertThat(lineService.determineLineOutcome(existingLine)).isEqualTo(expectedLine);
    }

    @Test
    public void shouldThrowExceptionForInvalidLine() throws Exception {
        Line invalidLine = new Line(new int[] { 2, 3, 5 });
        Throwable thrown = catchThrowable(() -> lineService.determineLineOutcome(invalidLine));
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class).hasMessage("Invalid line.");
    }

    private static Set<Line> getAllPossibleLines() {
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