package com.sourcesense.woot.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A WootString is an ordered sequence of WootCharacters:
 * Cb, c1, c2, ..., c3, Ce
 * where Cb and Ce are special characters (with special identifiers) marking
 * the beginning and the end of a sequence, respectively.
 */
public class WootString {

    private List<WootCharacter> sequence;

    public WootString() {
        this.sequence = new ArrayList<WootCharacter>(2);

        // Create special start and end characters.
        this.sequence.add(WootCharacter.createBeginning());
        this.sequence.add(WootCharacter.createEnd());
    }

    /**
     * Get the character at the specified position in this sequence.
     *
     * @param index The position of the character to return.
     * @return The character at the specified position.
     */
    public WootCharacter get(int index) {
        return sequence.get(index);
    }

    /**
     * Get the character with the matching unique id in this sequence, if it
     * exists.
     *
     * @param id The id of the character to return.
     * @return The character with the specified id if found. Null otherwise.
     */
    public WootCharacter get(CharacterId id) {
        for (WootCharacter c : sequence) {
            if (c.getId().equals(id)) return c;
        }

        return null;
    }

    /**
     * Get the character at the specified position in this sequence, with
     * respect to visibility. So if there is the following string:
     *      12a34b
     * and the numbers are hidden, character 'a' will be at visible position 0
     * and character 'b' will be at visible position 1.
     *
     * @param index The visible index of the character to get.
     * @return The visible character at the index, if it exists. Null otherwise.
     */
    public WootCharacter getVisibleCharAt(int index) {
        int count = 0;

        for (WootCharacter c : sequence) {
            if (c.isVisible()) {
                if (count == index) return c;
                count++;
            }
        }

        return null;
    }

    /**
     * Get the length of this sequence (including special and hidden
     * characters).
     *
     * @return The length of this sequence.
     */
    public int length() {
        return sequence.size();
    }

    public void insert(WootCharacter c, int index) {
        // TODO: Check for insertion before or after special chars?
        sequence.add(index, c);
    }

    /**
     * Get the position of the specified character in this sequence.
     *
     * @param c The character to get the position of.
     * @return The index of the specified character in this sequence if it
     *         exists. -1 otherwise.
     */
    public int getPos(WootCharacter c) {
        return sequence.indexOf(c);
    }

    /**
     * Transforms this sequence into a human-readable String, only displaying
     * the visible, non-special characters.
     *
     * @return The String representation of this sequence.
     */
    public String value() {
        StringBuilder sb = new StringBuilder();

        for (WootCharacter c : sequence) {
            if (c.isVisible()) sb.append(c.getValue());
        }

        return sb.toString();
    }

    /**
     * Check if the character at the specified position in this sequence is
     * visible or not.
     *
     * @param index The index of the character to check.
     * @return True if the character is visible. False if the character is
     *         hidden or doesn't exist in this sequence.
     */
    public boolean charIsVisible(int index) {
        return index <= sequence.size() && sequence.get(index).isVisible();
    }

    /**
     * @param c The character to check the presence of.
     * @return True if the character can be found in this sequence.
     *         False otherwise.
     */
    public boolean contains(WootCharacter c) {
        return sequence.contains(c);
    }

    /**
     * @param id The id of the character to check the presence of.
     * @return True if a character with the specified id can be found in this
     *         sequence. False otherwise.
     */
    public boolean contains(CharacterId id) {
        for (WootCharacter c : sequence) {
            if (c.getId().equals(id)) return true;
        }

        return false;
    }

    /**
     * Filter the range of characters between prev and next based on the linear
     * extension of the precedence relation defined in the WOOT spec:
     *
     * Character a precedes character b in a string S if and only if S.getPos(a)
     * <= S.getPos(b).
     *
     * This method removes characters from the specified range that have a
     * previous or next character in the range. So the characters kept in the
     * range returned have:
     *
     *      c.prev <= prev && next <= c.next
     *
     * @param prev The initial character in the range.
     * @param next The final character in the range.
     * @return The filtered range of characters.
     */
    public List<WootCharacter> filterRange(WootCharacter prev, WootCharacter next) {
        // TODO: Check prev precedes next.
        List<WootCharacter> result = new ArrayList<WootCharacter>();
        result.add(prev);

        int prevIndex = getPos(prev);
        int nextIndex = getPos(next);

        for (int i = prevIndex + 1; i < nextIndex; i++) {
            WootCharacter c = get(i);
            WootCharacter pc = get(c.getPrevious());
            WootCharacter nc = get(c.getNext());

            if (getPos(pc) <= prevIndex && nextIndex <= getPos(nc)) {
                result.add(c);
            }
        }

        result.add(next);
        return result;
    }
}
