package com.sourcesense.woot.operation;

import com.sourcesense.woot.model.WootCharacter;
import com.sourcesense.woot.model.WootId;
import com.sourcesense.woot.model.WootString;
import com.sourcesense.woot.validation.PreconditionValidator;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.sourcesense.woot.model.WootCharacter.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class WootInsertTest {

    private WootOp op;
    private PreconditionValidator validator;
    private WootString target;

    @Before
    public void setUp() throws Exception {
        validator = new PreconditionValidator();
        target = new WootString();
    }

    @Test
    public void getOperationCharacter() throws Exception {
        op = new WootInsert(createCharacter(1, 1L, 'a', 1, true), null, null);

        WootCharacter c = op.getChar();
        assertEquals(1, c.getId().getSiteId());
        assertEquals(1L, c.getId().getClock());
        assertTrue(c.isVisible());
        assertEquals('a', c.getValue());
        assertEquals(1, c.getDegree());
    }

    @Test
    public void getOperationType() throws Exception {
        op = new WootInsert(createCharacter(1, 1L, 'a', 1, true), null, null);

        assertEquals(WootOpType.INSERT, op.getType());
    }

    @Test
    public void executeSimpleInsertIntoEmptyString() throws Exception {
        assertEquals("", target.value());

        op = createValidOp(createCharacter(1, 1L, 'a', 1, true), SPECIAL, START, SPECIAL, END);
        op.execute(target);
        assertEquals("a", target.value());
        assertEquals("[a]", target.toString());
    }

    @Test
    public void executeSimpleInsertIntoLongString() throws Exception {
        target.insert(createCharacter(2, 1L, 'a', 1, true), 1);
        target.insert(createCharacter(2, 2L, 'b', 1, true), 2);
        target.insert(createCharacter(2, 3L, 'c', 1, true), 3);
        target.insert(createCharacter(2, 4L, 'z', 1, false), 4);
        target.insert(createCharacter(2, 5L, 'd', 1, true), 5);
        target.insert(createCharacter(2, 6L, 'e', 1, true), 6);
        assertEquals("abcde", target.value());
        assertEquals("[abc(z)de]", target.toString());

        op = createValidOp(createCharacter(1, 1L, '1', 2, true), 2, 1L, 2, 2L);
        op.execute(target);
        assertEquals("a1bcde", target.value());
        assertEquals("[a1bc(z)de]", target.toString());

        op = createValidOp(createCharacter(1, 2L, '2', 2, true), 2, 4L, 2, 5L);
        op.execute(target);
        assertEquals("a1bc2de", target.value());
        assertEquals("[a1bc(z)2de]", target.toString());
    }

    @Test
    public void executeOrderedInsertIntoShortStringAtSite1() throws Exception {
        // From Example 1 in Section 3.5 of WOOT spec.
        assertEquals("", target.value());
        assertEquals("[]", target.toString());

        // Operation 1
        op = createValidOp(createCharacter(1, 0L, '1', 1, true), SPECIAL, START, SPECIAL, END);
        op.execute(target);
        assertEquals("1", target.value());
        assertEquals("[1]", target.toString());

        // Operation 2
        op = createValidOp(createCharacter(2, 0L, '2', 1, true), SPECIAL, START, SPECIAL, END);
        op.execute(target);
        assertEquals("12", target.value());
        assertEquals("[12]", target.toString());

        // Operation 3
        op = createValidOp(createCharacter(3, 0L, '3', 2, true), SPECIAL, START, 1, 0L);
        op.execute(target);
        assertEquals("312", target.value());
        assertEquals("[312]", target.toString());

        // Operation 4
        op = createValidOp(createCharacter(3, 1L, '4', 2, true), 1, 0L, SPECIAL, END);
        op.execute(target);
        assertEquals("3124", target.value());
        assertEquals("[3124]", target.toString());
    }

    @Test
    public void executeOrderedInsertIntoShortStringAtSite2() throws Exception {
        // From Example 1 in Section 3.5 of WOOT spec.
        assertEquals("", target.value());
        assertEquals("[]", target.toString());

        // Operation 2
        op = createValidOp(createCharacter(2, 0L, '2', 1, true), SPECIAL, START, SPECIAL, END);
        op.execute(target);
        assertEquals("2", target.value());
        assertEquals("[2]", target.toString());

        // Operation 1
        op = createValidOp(createCharacter(1, 0L, '1', 1, true), SPECIAL, START, SPECIAL, END);
        op.execute(target);
        assertEquals("12", target.value());
        assertEquals("[12]", target.toString());

        // Operation 3
        op = createValidOp(createCharacter(3, 0L, '3', 2, true), SPECIAL, START, 1, 0L);
        op.execute(target);
        assertEquals("312", target.value());
        assertEquals("[312]", target.toString());

        // Operation 4
        op = createValidOp(createCharacter(3, 1L, '4', 2, true), 1, 0L, SPECIAL, END);
        op.execute(target);
        assertEquals("3124", target.value());
        assertEquals("[3124]", target.toString());
    }

    @Test
    public void executeOrderedInsertIntoShortStringAtSite3() throws Exception {
        // From Example 1 in Section 3.5 of WOOT spec.
        assertEquals("", target.value());
        assertEquals("[]", target.toString());

        // Operation 1
        op = createValidOp(createCharacter(1, 0L, '1', 1, true), SPECIAL, START, SPECIAL, END);
        op.execute(target);
        assertEquals("1", target.value());
        assertEquals("[1]", target.toString());

        // Operation 3
        op = createValidOp(createCharacter(3, 0L, '3', 2, true), SPECIAL, START, 1, 0L);
        op.execute(target);
        assertEquals("31", target.value());
        assertEquals("[31]", target.toString());

        // Operation 4
        op = createValidOp(createCharacter(3, 1L, '4', 2, true), 1, 0L, SPECIAL, END);
        op.execute(target);
        assertEquals("314", target.value());
        assertEquals("[314]", target.toString());

        // Operation 2
        op = createValidOp(createCharacter(2, 0L, '2', 1, true), SPECIAL, START, SPECIAL, END);
        op.execute(target);
        assertEquals("3124", target.value());
        assertEquals("[3124]", target.toString());
    }

    @Test
    public void executeOrderedInsertIntoComplexStringAtSite1() throws Exception {
        // From Example 2 in Section 3.5 of WOOT spec.
        // Seven sites each inserting a character containing its identifier.
        assertEquals("", target.value());
        assertEquals("[]", target.toString());

        // First 5 characters have already been received.
        List<WootOp> ops = Arrays.asList(
                createOp(createCharacter(0, 0L, '0', 1, true), SPECIAL, START, SPECIAL, END),
                createOp(createCharacter(1, 0L, '1', 2, true), SPECIAL, START, 0, 0L),
                createOp(createCharacter(2, 0L, '2', 2, true), SPECIAL, START, 0, 0L),
                createOp(createCharacter(3, 0L, '3', 2, true), 0, 0L, SPECIAL, END),
                createOp(createCharacter(4, 0L, '4', 2, true), 0, 0L, SPECIAL, END)
        );

        for (WootOp wootOp : ops) {
            assertTrue(validator.isExecutable(wootOp, target));
            wootOp.execute(target);
        }

        assertEquals("12034", target.value());
        assertEquals("[12034]", target.toString());

        // Receive 6 then 5.
        op = createValidOp(createCharacter(6, 0L, '6', 3, true), 1, 0L, 3, 0L);
        op.execute(target);
        assertEquals("120634", target.value());
        assertEquals("[120634]", target.toString());

        op = createValidOp(createCharacter(5, 0L, '5', 3, true), 2, 0L, 4, 0L);
        op.execute(target);
        assertEquals("1206354", target.value());
        assertEquals("[1206354]", target.toString());
    }

    @Test
    public void executeOrderedInsertIntoComplexStringAtSite2() throws Exception {
        // From Example 2 in Section 3.5 of WOOT spec.
        // Seven sites each inserting a character containing its identifier.
        assertEquals("", target.value());
        assertEquals("[]", target.toString());

        // First 5 characters have already been received.
        List<WootOp> ops = Arrays.asList(
                createOp(createCharacter(0, 0L, '0', 1, true), SPECIAL, START, SPECIAL, END),
                createOp(createCharacter(1, 0L, '1', 2, true), SPECIAL, START, 0, 0L),
                createOp(createCharacter(2, 0L, '2', 2, true), SPECIAL, START, 0, 0L),
                createOp(createCharacter(3, 0L, '3', 2, true), 0, 0L, SPECIAL, END),
                createOp(createCharacter(4, 0L, '4', 2, true), 0, 0L, SPECIAL, END)
        );

        for (WootOp wootOp : ops) {
            assertTrue(validator.isExecutable(wootOp, target));
            wootOp.execute(target);
        }

        assertEquals("12034", target.value());
        assertEquals("[12034]", target.toString());

        // Receive 5 then 6.
        op = createValidOp(createCharacter(5, 0L, '5', 3, true), 2, 0L, 4, 0L);
        op.execute(target);
        assertEquals("120354", target.value());
        assertEquals("[120354]", target.toString());

        op = createValidOp(createCharacter(6, 0L, '6', 3, true), 1, 0L, 3, 0L);
        op.execute(target);
        assertEquals("1206354", target.value());
        assertEquals("[1206354]", target.toString());
    }

    @Test
    public void tempStressTest() throws Exception {
        assertEquals("", target.value());

        int max = 10000;

        for (int i = 0; i < max; i++) {
            op = createValidOp(createCharacter(0, (long) i, 'a', 1, true), SPECIAL, START, SPECIAL, END);
            op.execute(target);
        }

        assertEquals(max + 2, target.length());
    }

    @Test
    public void tempSequentialStressTest() throws Exception {
        assertEquals("", target.value());

        int max = 10000;

        op = createValidOp(createCharacter(0, 0L, 'a', 1, true), SPECIAL, START, SPECIAL, END);
        op.execute(target);

        for (int i = 1; i < max; i++) {
            op = createValidOp(createCharacter(0, (long) i, 'a', 1, true), 0, (long) i - 1, SPECIAL, END);
            op.execute(target);
        }

        assertEquals(max + 2, target.length());
    }

    private WootOp createOp(WootCharacter c, int pi, long pc, int ni, long nc) throws Exception {
        return new WootInsert(c, new WootId(pi, pc), new WootId(ni, nc));
    }

    private WootOp createValidOp(WootCharacter c, int pi, long pc, int ni, long nc) throws Exception {
        WootOp operation = new WootInsert(c, new WootId(pi, pc), new WootId(ni, nc));
        assertTrue(validator.isExecutable(operation, target));
        return operation;
    }
}
