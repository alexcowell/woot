package com.sourcesense.woot.operation;

import com.sourcesense.woot.model.WootCharacter;
import com.sourcesense.woot.model.WootString;
import com.sourcesense.woot.validation.PreconditionValidator;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.sourcesense.woot.model.WootCharacter.END;
import static com.sourcesense.woot.model.WootCharacter.SPECIAL;
import static com.sourcesense.woot.model.WootCharacter.START;
import static com.sourcesense.woot.model.WootCharacter.createCharacter;
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
        op = new WootInsert(createCharacter(1, 1L, true, 'a'));

        WootCharacter c = op.getChar();
        assertEquals(1, c.getId().getSiteId());
        assertEquals(1L, c.getId().getClock());
        assertTrue(c.isVisible());
        assertEquals('a', c.getValue());
    }

    @Test
    public void getOperationType() throws Exception {
        op = new WootInsert(createCharacter(1, 1L, true, 'a'));

        assertEquals(WootOpType.INSERT, op.getType());
    }

    @Test
    public void executeSimpleInsertIntoEmptyString() throws Exception {
        assertEquals("", target.value());

        op = createOp(createCharacter(1, 1L, true, 'a', SPECIAL, START, SPECIAL, END));
        op.execute(target);
        assertEquals("a", target.value());
    }

    @Test
    public void executeSimpleInsertIntoLongString() throws Exception {
        target.insert(createCharacter(2, 1L, true, 'a'), 1);
        target.insert(createCharacter(2, 2L, true, 'b'), 2);
        target.insert(createCharacter(2, 3L, true, 'c'), 3);
        target.insert(createCharacter(2, 4L, false, 'z'), 4);
        target.insert(createCharacter(2, 5L, true, 'd'), 5);
        target.insert(createCharacter(2, 6L, true, 'e'), 6);
        assertEquals("abcde", target.value());

        op = createOp(createCharacter(1, 1L, true, '1', 2, 1L, 2, 2L));
        op.execute(target);
        assertEquals("a1bcde", target.value());

        op = createOp(createCharacter(1, 2L, true, '2', 2, 4L, 2, 5L));
        op.execute(target);
        assertEquals("a1bc2de", target.value());
    }

    @Test
    public void executeOrderedInsertIntoShortStringAtSite1() throws Exception {
        // From Example 1 in Section 3.5 of WOOT spec.
        assertEquals("", target.value());

        // Operation 1
        op = createOp(createCharacter(1, 0L, true, '1', SPECIAL, START, SPECIAL, END));
        op.execute(target);
        assertEquals("1", target.value());

        // Operation 2
        op = createOp(createCharacter(2, 0L, true, '2', SPECIAL, START, SPECIAL, END));
        op.execute(target);
        assertEquals("12", target.value());

        // Operation 3
        op = createOp(createCharacter(3, 0L, true, '3', SPECIAL, START, 1, 0L));
        op.execute(target);
        assertEquals("312", target.value());

        // Operation 4
        op = createOp(createCharacter(3, 1L, true, '4', 1, 0L, SPECIAL, END));
        op.execute(target);
        assertEquals("3124", target.value());
    }

    @Test
    public void executeOrderedInsertIntoShortStringAtSite2() throws Exception {
        // From Example 1 in Section 3.5 of WOOT spec.
        assertEquals("", target.value());

        // Operation 2
        op = createOp(createCharacter(2, 0L, true, '2', SPECIAL, START, SPECIAL, END));
        op.execute(target);
        assertEquals("2", target.value());

        // Operation 1
        op = createOp(createCharacter(1, 0L, true, '1', SPECIAL, START, SPECIAL, END));
        op.execute(target);
        assertEquals("12", target.value());

        // Operation 3
        op = createOp(createCharacter(3, 0L, true, '3', SPECIAL, START, 1, 0L));
        op.execute(target);
        assertEquals("312", target.value());

        // Operation 4
        op = createOp(createCharacter(3, 1L, true, '4', 1, 0L, SPECIAL, END));
        op.execute(target);
        assertEquals("3124", target.value());
    }

    @Test
    public void executeOrderedInsertIntoShortStringAtSite3() throws Exception {
        // From Example 1 in Section 3.5 of WOOT spec.
        assertEquals("", target.value());

        // Operation 1
        op = createOp(createCharacter(1, 0L, true, '1', SPECIAL, START, SPECIAL, END));
        op.execute(target);
        assertEquals("1", target.value());

        // Operation 3
        op = createOp(createCharacter(3, 0L, true, '3', SPECIAL, START, 1, 0L));
        op.execute(target);
        assertEquals("31", target.value());

        // Operation 4
        op = createOp(createCharacter(3, 1L, true, '4', 1, 0L, SPECIAL, END));
        op.execute(target);
        assertEquals("314", target.value());

        // Operation 2
        op = createOp(createCharacter(2, 0L, true, '2', SPECIAL, START, SPECIAL, END));
        op.execute(target);
        assertEquals("3124", target.value());
    }

    @Test
    public void executeOrderedInsertIntoComplexStringAtSite1() throws Exception {
        // From Example 2 in Section 3.5 of WOOT spec.
        // Seven sites each inserting a character containing its identifier.
        assertEquals("", target.value());

        // First 5 characters have already been received.
        List<WootInsert> ops = Arrays.asList(
                new WootInsert(createCharacter(0, 0L, true, '0', SPECIAL, START, SPECIAL, END)),
                new WootInsert(createCharacter(1, 0L, true, '1', SPECIAL, START, 0, 0L)),
                new WootInsert(createCharacter(2, 0L, true, '2', SPECIAL, START, 0, 0L)),
                new WootInsert(createCharacter(3, 0L, true, '3', 0, 0L, SPECIAL, END)),
                new WootInsert(createCharacter(4, 0L, true, '4', 0, 0L, SPECIAL, END))
        );

        for (WootOp wootOp : ops) {
            assertTrue(validator.isExecutable(wootOp, target));
            wootOp.execute(target);
        }

        assertEquals("12034", target.value());

        // Receive 6 then 5.
        op = createOp(createCharacter(6, 0L, true, '6', 1, 0L, 3, 0L));
        op.execute(target);
        assertEquals("120634", target.value());

        op = createOp(createCharacter(5, 0L, true, '5', 2, 0L, 4, 0L));
        op.execute(target);
        assertEquals("1206354", target.value());
    }

    @Test
    public void executeOrderedInsertIntoComplexStringAtSite2() throws Exception {
        // From Example 2 in Section 3.5 of WOOT spec.
        // Seven sites each inserting a character containing its identifier.
        assertEquals("", target.value());

        // First 5 characters have already been received.
        List<WootInsert> ops = Arrays.asList(
                new WootInsert(createCharacter(0, 0L, true, '0', SPECIAL, START, SPECIAL, END)),
                new WootInsert(createCharacter(1, 0L, true, '1', SPECIAL, START, 0, 0L)),
                new WootInsert(createCharacter(2, 0L, true, '2', SPECIAL, START, 0, 0L)),
                new WootInsert(createCharacter(3, 0L, true, '3', 0, 0L, SPECIAL, END)),
                new WootInsert(createCharacter(4, 0L, true, '4', 0, 0L, SPECIAL, END))
        );

        for (WootOp wootOp : ops) {
            assertTrue(validator.isExecutable(wootOp, target));
            wootOp.execute(target);
        }

        assertEquals("12034", target.value());

        // Receive 5 then 6.
        op = createOp(createCharacter(5, 0L, true, '5', 2, 0L, 4, 0L));
        op.execute(target);
        assertEquals("120354", target.value());

        op = createOp(createCharacter(6, 0L, true, '6', 1, 0L, 3, 0L));
        op.execute(target);
        assertEquals("1206354", target.value());
    }

    private WootOp createOp(WootCharacter c) throws Exception {
        WootOp operation = new WootInsert(c);
        assertTrue(validator.isExecutable(operation, target));
        return operation;
    }
}
