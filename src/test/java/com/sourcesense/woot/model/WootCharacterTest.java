package com.sourcesense.woot.model;

import com.sourcesense.woot.model.CharacterId;
import com.sourcesense.woot.model.WootCharacter;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class WootCharacterTest {

    private WootCharacter c;

    @Before
    public void setUp() throws Exception {
        c = new WootCharacter(new CharacterId(123, 21L), true, 'x',
                new CharacterId(100, 15L), new CharacterId(200, 345L));
    }

    @Test
    public void characterHasId() throws Exception {
        assertEquals(123, c.getId().getSiteId());
        assertEquals(21L, c.getId().getClock());
    }

    @Test
    public void characterIsVisible() throws Exception {
        assertTrue(c.isVisible());
    }

    @Test
    public void characterCanBeHidden() throws Exception {
        c.setVisible(false);
        assertFalse(c.isVisible());
    }

    @Test
    public void characterHasAnAlphanumericValue() throws Exception {
        assertEquals('x', c.getValue());
    }

    @Test
    public void characterKnowsPreviousCharacter() throws Exception {
        assertEquals(100, c.getPrevious().getSiteId());
        assertEquals(15L, c.getPrevious().getClock());
    }

    @Test
    public void characterKnowsNextCharacter() throws Exception {
        assertEquals(200, c.getNext().getSiteId());
        assertEquals(345L, c.getNext().getClock());
    }
}
