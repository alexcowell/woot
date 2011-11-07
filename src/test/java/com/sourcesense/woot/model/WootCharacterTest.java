package com.sourcesense.woot.model;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class WootCharacterTest {

    private WootCharacter c;

    @Before
    public void setUp() throws Exception {
        c = new WootCharacter(new WootId(123, 21L), 'x', 1, true);
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
    public void characterHasDegree() throws Exception {
        assertEquals(1, c.getDegree());
    }
}
