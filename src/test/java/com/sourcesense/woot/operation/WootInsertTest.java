package com.sourcesense.woot.operation;

import com.sourcesense.woot.model.WootCharacter;
import com.sourcesense.woot.model.WootString;
import com.sourcesense.woot.validation.PreconditionValidator;

import org.junit.Before;
import org.junit.Test;

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

        op = new WootInsert(createCharacter(1, 1L, true, 'a', SPECIAL, START, SPECIAL, END));
        assertTrue(validator.isExecutable(op, target));

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

        op = new WootInsert(createCharacter(1, 1L, true, '1', 2, 1L, 2, 2L));
        assertTrue(validator.isExecutable(op, target));

        op.execute(target);
        assertEquals("a1bcde", target.value());

        op = new WootInsert(createCharacter(1, 2L, true, '2', 2, 4L, 2, 5L));
        assertTrue(validator.isExecutable(op, target));

        op.execute(target);
        assertEquals("a1bc2de", target.value());
    }

    @Test
    public void executeOrderedInsertIntoShortString() throws Exception {
        // From Example 1 in Section 3.5 of WOOT spec.
        assertEquals("", target.value());

        op = new WootInsert(createCharacter(2, 0L, true, '2', SPECIAL, START, SPECIAL, END));
        assertTrue(validator.isExecutable(op, target));

        op.execute(target);
        assertEquals("2", target.value());

        op = new WootInsert(createCharacter(1, 0L, true, '1', SPECIAL, START, SPECIAL, END));
        assertTrue(validator.isExecutable(op, target));

        op.execute(target);
        assertEquals("12", target.value());
    }
}
