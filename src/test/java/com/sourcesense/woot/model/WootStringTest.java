package com.sourcesense.woot.model;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

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
        WootCharacter c = createCharacter(101, 10L, true, 'a');
        string.insert(c, 1);
        assertEquals(3, string.length());
    }

    @Test
    public void insertingACharacterShouldBeInCorrectPosition() throws Exception {
        WootCharacter c = createCharacter(101, 10L, true, 'a');
        string.insert(c, 1);
        assertEquals(c, string.get(1));
    }

    @Test
    public void getThePositionOfACharacterInAString() throws Exception {
        WootCharacter c = createCharacter(101, 10L, true, 'a');
        string.insert(c, 1);
        assertEquals(1, string.getPos(c));
    }

    @Test
    public void gettingPositionOfNonExistentCharacterReturnsMinusOne() throws Exception {
        WootCharacter c = createCharacter(101, 10L, true, 'a');
        assertEquals(-1, string.getPos(c));
    }

    @Test
    public void valueOfStringShouldOnlyOutputVisibleCharacters() throws Exception {
        string.insert(createCharacter(101, 10L, true, 'a'), 1);
        string.insert(createCharacter(101, 11L, true, 'b'), 2);
        string.insert(createCharacter(101, 12L, false, '1'), 3);
        string.insert(createCharacter(101, 13L, true, 'c'), 4);

        assertEquals("abc", string.value());
    }

    @Test
    public void valueOfEmptyStringShouldBeEmpty() throws Exception {
        assertEquals("", string.value());
    }

    @Test
    public void callingCharIsVisibleForAVisibleCharShouldBeTrue() throws Exception {
        string.insert(createCharacter(101, 10L, true, 'a'), 1);
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
        assertFalse(string.contains(createCharacter(101, 10L, true, 'a')));
    }

    @Test
    public void callingContainsForExistingIdReturnsTrue() throws Exception {
        assertTrue(string.contains(createBeginning().getId()));
    }

    @Test
    public void callingContainsForNonExistentIdReturnsFalse() throws Exception {
        assertFalse(string.contains(new CharacterId(41, 4L)));
    }

    @Test
    public void getVisibleCharAtPosition() throws Exception {
        WootCharacter c = createCharacter(101, 10L, true, 'a');

        string.insert(c, 1);

        assertEquals(c, string.getVisibleCharAt(0));
    }

    @Test
    public void getCharacterWithRespectToMultipleVisibleCharacters() throws Exception {
        WootCharacter c = createCharacter(101, 10L, true, 'a');

        string.insert(createCharacter(110, 10L, false, 'h'), 1);
        string.insert(createCharacter(100, 10L, true, '1'), 2);
        string.insert(createCharacter(101, 10L, true, 'a'), 3);
        string.insert(createCharacter(106, 10L, true, '2'), 4);

        assertEquals(c, string.getVisibleCharAt(1));
    }

    @Test
    public void getVisibleCharAtPositionWithInvalidIndex() throws Exception {
        assertEquals(null, string.getVisibleCharAt(-1));
    }

    @Test
    public void getPresentCharacterById() throws Exception {
        WootCharacter c = createCharacter(1, 1L, true, 'a');
        string.insert(c, 1);
        assertEquals(c, string.get(new CharacterId(1, 1L)));
    }

    @Test
    public void getNonExistentCharByIdReturnsNull() throws Exception {
        assertEquals(null, string.get(new CharacterId(10, 23L)));
    }

    @Test
    public void filterCharacters() throws Exception {
        List<WootCharacter> chars = Arrays.asList(
            createCharacter(100, 1L, true, 'a', SPECIAL, START, SPECIAL, END),
            createCharacter(100, 3L, true, 'b', 100, 1L, 100, 2L),
            createCharacter(100, 4L, true, 'c', 100, 3L, 100, 2L),
            createCharacter(100, 2L, true, 'd', 100, 1L, SPECIAL, END)
        );

        string.insert(chars.get(0), 1);
        string.insert(chars.get(1), 2);
        string.insert(chars.get(2), 3);
        string.insert(chars.get(3), 4);
        assertEquals("abcd", string.value());

        List<WootCharacter> filtered = string.filterRange(chars.get(0), chars.get(3));
        assertEquals(3, filtered.size());
        assertTrue(filtered.contains(chars.get(0)));
        assertTrue(filtered.contains(chars.get(1)));
        assertTrue(!filtered.contains(chars.get(2)));
        assertTrue(filtered.contains(chars.get(3)));
    }
}
