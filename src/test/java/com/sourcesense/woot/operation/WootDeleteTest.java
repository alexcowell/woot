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
        op = new WootDelete(createCharacter(1, 1L, 'a', 1, true));
    }

    @Test
    public void getOperationCharacter() throws Exception {
        WootCharacter c = op.getChar();
        assertEquals(1, c.getId().getSiteId());
        assertEquals(1L, c.getId().getClock());
        assertTrue(c.isVisible());
        assertEquals('a', c.getValue());
        assertEquals(1, c.getDegree());
    }

    @Test
    public void getOperationType() throws Exception {
        assertEquals(WootOpType.DELETE, op.getType());
    }

    @Test
    public void executeDeleteOperation() throws Exception {
        WootString target = new WootString();
        target.insert(createCharacter(1, 1L, 'a', 1, true), 1);
        assertEquals("a", target.value());

        op.execute(target);

        assertFalse(target.charIsVisible(1));
        assertEquals("", target.value());
        assertEquals("[(a)]", target.toString());
    }

    @Test
    public void executingDeleteOnHiddenCharHasNoEffect() throws Exception {
        WootString target = new WootString();
        target.insert(createCharacter(1, 0L, 'x', 1, true), 1);
        target.insert(createCharacter(1, 1L, 'a', 1, false), 2);
        target.insert(createCharacter(1, 2L, 'y', 1, true), 3);
        assertEquals("xy", target.value());
        assertEquals("[x(a)y]", target.toString());

        op.execute(target);

        assertFalse(target.charIsVisible(2));
        assertEquals("xy", target.value());
    }
}
