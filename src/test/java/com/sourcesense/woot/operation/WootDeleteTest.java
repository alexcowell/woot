package com.sourcesense.woot.operation;

import com.sourcesense.woot.model.WootCharacter;
import com.sourcesense.woot.model.WootString;

import org.junit.Before;
import org.junit.Test;

import static com.sourcesense.woot.model.WootCharacter.createCharacter;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class WootDeleteTest {

    private WootOp op;

    @Before
    public void setUp() throws Exception {
        op = new WootDelete(createCharacter(1, 1L, true, 'a'));
    }

    @Test
    public void getOperationCharacter() throws Exception {
        WootCharacter c = op.getChar();
        assertEquals(1, c.getId().getSiteId());
        assertEquals(1L, c.getId().getClock());
        assertTrue(c.isVisible());
        assertEquals('a', c.getValue());
    }

    @Test
    public void getOperationType() throws Exception {
        assertEquals(WootOpType.DELETE, op.getType());
    }

    @Test
    public void executeDeleteOperation() throws Exception {
        WootString target = new WootString();
        target.insert(createCharacter(1, 1L, true, 'a'), 1);
        assertEquals("a", target.value());

        op.execute(target);

        assertFalse(target.charIsVisible(1));
        assertEquals("", target.value());
    }

    @Test
    public void executingDeleteOnHiddenCharHasNoEffect() throws Exception {
        WootString target = new WootString();
        target.insert(createCharacter(1, 0L, true, 'x'), 1);
        target.insert(createCharacter(1, 1L, false, 'a'), 2);
        target.insert(createCharacter(1, 2L, true, 'y'), 3);
        assertEquals("xy", target.value());

        op.execute(target);

        assertFalse(target.charIsVisible(2));
        assertEquals("xy", target.value());
    }
}
