package com.sourcesense.woot.client;

import com.sourcesense.woot.model.WootString;
import com.sourcesense.woot.operation.WootOp;

import org.junit.Before;
import org.junit.Test;

import static com.sourcesense.woot.model.WootCharacter.createCharacter;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class WootClientTest {

    private WootClient client;

    @Before
    public void setUp() throws Exception {
        client = new WootClient(123);
    }

    @Test
    public void clientHasUniqueId() throws Exception {
        assertEquals(123, client.getId());
    }

    @Test
    public void clientHasLocalLogicalClock() throws Exception {
        assertEquals(0, client.getClock());
    }

    @Test
    public void clientHasSequenceString() throws Exception {
        assertNotNull(client.getString());
        assertEquals(2, client.getString().length());
    }

    @Test
    public void clientHasAQueueOfPendingOperations() throws Exception {
        assertNotNull(client.getQueue());
    }

    @Test
    public void clientHasAValidator() throws Exception {
        assertNotNull(client.getValidator());
    }

    @Test
    public void generatingAnInsertIncrementsTheClockByOne() throws Exception {
        client.generateInsert(1, 'a');
        assertEquals(1L, client.getClock());
    }

    @Test
    public void clientGeneratesAValidInsertOperationForEmptyString() throws Exception {
        WootOp op = client.generateInsert(0, 'a');
        assertTrue(client.getValidator().isExecutable(op, client.getString()));
        assertEquals("a", client.getString().value());
        assertEquals("[a]", client.getString().toString());
    }

    @Test
    public void clientGeneratesAValidInsertOpForShortString() throws Exception {
        WootString s = client.getString();
        s.insert(createCharacter(1, 0L, 'a', 1, true), 1);
        s.insert(createCharacter(1, 1L, 'b', 1, true), 2);
        s.insert(createCharacter(1, 2L, 'c', 1, true), 3);
        s.insert(createCharacter(1, 3L, 'd', 1, true), 4);
        s.insert(createCharacter(1, 4L, 'e', 1, true), 5);
        assertEquals("abcde", s.value());
        assertEquals("[abcde]", s.toString());

        WootOp op = client.generateInsert(2, 'x');
        assertTrue(client.getValidator().isExecutable(op, client.getString()));
        assertEquals("abcxde", client.getString().value());

        op = client.generateInsert(1, 'y');
        assertTrue(client.getValidator().isExecutable(op, client.getString()));
        assertEquals("abycxde", client.getString().value());
    }

    @Test
    public void generatingADeleteIncrementsTheClockByOne() throws Exception {
        client.generateDelete(1);
        assertEquals(1L, client.getClock());
    }

    @Test
    public void clientGeneratesInvalidDeleteOpForSpecialChars() throws Exception {
        WootOp op = client.generateDelete(0);
        assertFalse(client.getValidator().isExecutable(op, client.getString()));

        op = client.generateDelete(1);
        assertFalse(client.getValidator().isExecutable(op, client.getString()));
    }

    @Test
    public void clientGeneratesValidDeleteOpForShortString() throws Exception {
        WootString s = client.getString();
        s.insert(createCharacter(1, 0L, 'a', 1, true), 1);
        s.insert(createCharacter(1, 1L, 'b', 1, true), 2);
        s.insert(createCharacter(1, 2L, 'c', 1, true), 3);
        s.insert(createCharacter(1, 3L, 'd', 1, true), 4);
        s.insert(createCharacter(1, 4L, 'e', 1, true), 5);
        assertEquals("abcde", s.value());
        assertEquals("[abcde]", s.toString());

        WootOp op = client.generateDelete(2);
        assertTrue(client.getValidator().isExecutable(op, client.getString()));
        assertEquals("abde", client.getString().value());
        assertEquals("[ab(c)de]", client.getString().toString());

        op = client.generateDelete(2);
        assertTrue(client.getValidator().isExecutable(op, client.getString()));
        assertEquals("abe", client.getString().value());
        assertEquals("[ab(c)(d)e]", client.getString().toString());

        op = client.generateDelete(0);
        assertTrue(client.getValidator().isExecutable(op, client.getString()));
        assertEquals("be", client.getString().value());
        assertEquals("[(a)b(c)(d)e]", client.getString().toString());
    }
}
