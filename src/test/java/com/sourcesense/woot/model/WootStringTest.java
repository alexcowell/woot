package com.sourcesense.woot.model;

import org.junit.Before;
import org.junit.Test;

import static com.sourcesense.woot.model.WootCharacter.*;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class WootStringTest {

    WootString string;

    @Before
    public void setUp() throws Exception {
        string = new WootString();
    }

    @Test
    public void anEmptyStringContainsOnlyTheStartAndEndCharacters() throws Exception {
        assertTrue(string.get(0) != null);
        assertEquals(SPECIAL, string.get(0).getId().getSiteId());
        assertEquals(START, string.get(0).getId().getClock());

        assertTrue(string.get(1) != null);
        assertEquals(SPECIAL, string.get(1).getId().getSiteId());
        assertEquals(END, string.get(1).getId().getClock());
    }

    @Test
    public void theStartAndEndCharactersShouldBeHidden() throws Exception {
        assertFalse(string.get(0).isVisible());
        assertFalse(string.get(1).isVisible());
    }

    @Test
    public void lengthOfAPseudoEmptyStringShouldBeTwo() throws Exception {
        assertEquals(2, string.length());
    }

    @Test
    public void insertingACharacterShouldIncreaseLengthByOne() throws Exception {
        WootCharacter c = createCharacter(101, 10L, 'a', 1, true);
        string.insert(c, 1);
        assertEquals(3, string.length());
    }

    @Test
    public void insertingACharacterShouldBeInCorrectPosition() throws Exception {
        WootCharacter c = createCharacter(101, 10L, 'a', 1, true);
        string.insert(c, 1);
        assertEquals(c, string.get(1));
    }

    @Test
    public void getThePositionOfACharacterInAString() throws Exception {
        WootCharacter c = createCharacter(101, 10L, 'a', 1, true);
        string.insert(c, 1);
        assertEquals(1, string.getPos(c));
    }

    @Test
    public void gettingPositionOfNonExistentCharacterReturnsMinusOne() throws Exception {
        WootCharacter c = createCharacter(101, 10L, 'a', 1, true);
        assertEquals(-1, string.getPos(c));
    }

    @Test
    public void valueOfStringShouldOnlyOutputVisibleCharacters() throws Exception {
        string.insert(createCharacter(101, 10L, 'a', 1, true), 1);
        string.insert(createCharacter(101, 11L, 'b', 1, true), 2);
        string.insert(createCharacter(101, 12L, '1', 1, false), 3);
        string.insert(createCharacter(101, 13L, 'c', 1, true), 4);

        assertEquals("abc", string.value());
    }

    @Test
    public void valueOfEmptyStringShouldBeEmpty() throws Exception {
        assertEquals("", string.value());
    }

    @Test
    public void callingCharIsVisibleForAVisibleCharShouldBeTrue() throws Exception {
        string.insert(createCharacter(101, 10L, 'a', 1, true), 1);
        assertTrue(string.charIsVisible(1));
    }

    @Test
    public void callingCharIsVisibleForHiddenCharShouldBeFalse() throws Exception {
        assertFalse(string.charIsVisible(0));
    }

    @Test
    public void callingCharIsVisibleForNonExistentCharShouldBeFalse() throws Exception {
        assertFalse(string.charIsVisible(14));
    }

    @Test
    public void callingContainsForExistingCharReturnsTrue() throws Exception {
        assertTrue(string.contains(createEnd()));
    }

    @Test
    public void callingContainsForNonExistentCharReturnsFalse() throws Exception {
        assertFalse(string.contains(createCharacter(101, 10L, 'a', 1, true)));
    }

    @Test
    public void callingContainsForExistingIdReturnsTrue() throws Exception {
        assertTrue(string.contains(createBeginning().getId()));
    }

    @Test
    public void callingContainsForNonExistentIdReturnsFalse() throws Exception {
        assertFalse(string.contains(new WootId(41, 4L)));
    }

    @Test
    public void getVisibleCharAtPosition() throws Exception {
        WootCharacter c = createCharacter(101, 10L, 'a', 1, true);

        string.insert(c, 1);

        assertEquals(c, string.getVisibleCharAt(0));
    }

    @Test
    public void getCharacterWithRespectToMultipleVisibleCharacters() throws Exception {
        WootCharacter c = createCharacter(101, 10L, 'a', 1, true);

        string.insert(createCharacter(110, 10L, 'h', 1, false), 1);
        string.insert(createCharacter(100, 10L, '1', 1, true), 2);
        string.insert(createCharacter(101, 10L, 'a', 1, true), 3);
        string.insert(createCharacter(106, 10L, '2', 1, true), 4);

        assertEquals(c, string.getVisibleCharAt(1));
    }

    @Test
    public void getVisibleCharAtPositionWithInvalidIndex() throws Exception {
        assertEquals(null, string.getVisibleCharAt(-1));
    }

    @Test
    public void getPresentCharacterById() throws Exception {
        WootCharacter c = createCharacter(1, 1L, 'a', 1, true);
        string.insert(c, 1);
        assertEquals(c, string.get(new WootId(1, 1L)));
    }

    @Test
    public void getNonExistentCharByIdReturnsNull() throws Exception {
        assertEquals(null, string.get(new WootId(10, 23L)));
    }

    @Test
    public void getPositionByIdOfExistingCharacter() throws Exception {
        assertEquals(0, string.getPos(new WootId(SPECIAL, START)));
    }

    @Test
    public void toStringContainsHiddenCharacters() throws Exception {
        string.insert(createCharacter(110, 10L, 'h', 1, false), 1);
        string.insert(createCharacter(100, 10L, '1', 1, true), 2);
        string.insert(createCharacter(101, 10L, 'a', 1, true), 3);
        string.insert(createCharacter(106, 10L, '2', 1, true), 4);
        string.insert(createCharacter(107, 10L, 'c', 1, true), 5);
        assertEquals("1a2c", string.value());
        assertEquals("[(h)1a2c]", string.toString());
    }
}
