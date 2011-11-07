package com.sourcesense.woot.operation;

import com.sourcesense.woot.model.WootId;
import com.sourcesense.woot.model.WootCharacter;
import com.sourcesense.woot.model.WootString;

import java.util.ArrayList;
import java.util.List;

import static com.sourcesense.woot.model.WootCharacter.SPECIAL;
import static com.sourcesense.woot.model.WootCharacter.START;

/**
 *
 */
public class WootInsert implements WootOp {

    private WootCharacter character;
    private WootOpType type;
    private WootId previous;
    private WootId next;

    public WootInsert(WootCharacter character, WootId previous, WootId next) {
        this.character = character;
        this.type = WootOpType.INSERT;
        this.previous = previous;
        this.next = next;
    }

    /**
     * Inserts the character into the target WootString.
     *
     * @param target The target string affected by this operation.
     */
    @Override
    public void execute(WootString target) {
        // TODO: This can still be improved.
        // This uses the optimized WOOT spec insert algorithm. Improves worst
        // case runtime from O(n^3) to O(n^2).
        integrateInsert(target, previous, next);
    }

    private void integrateInsert(WootString target, WootId prev, WootId next) {
        int prevPos = target.getPos(prev);
        int nextPos = target.getPos(next);

        if ((nextPos - (prevPos + 1)) == 0) {
            // There are no characters between this character's previous and
            // next characters, so just insert it between them.
            target.insert(character, nextPos);
        } else {
            // There are characters in the way.
            int minDegree = findMinDegree(target, prevPos, nextPos);
            List<WootCharacter> filtered = filter(target, prevPos, nextPos, minDegree);

            int i = 1;
            while ((i < filtered.size() - 1) && compare(filtered.get(i), character) < 0) {
                i++;
            }

            integrateInsert(target, filtered.get(i-1).getId(), filtered.get(i).getId());
        }
    }

    private int findMinDegree(WootString target, int prevPos, int nextPos) {
        int minDegree = target.get(prevPos + 1).getDegree();

        for (int i = prevPos + 2; i < nextPos; i++) {
            int d = target.get(i).getDegree();
            if (d < minDegree) minDegree = d;
        }

        return minDegree;
    }

    private List<WootCharacter> filter(WootString target, int prevPos, int nextPos, int minDegree) {
        List<WootCharacter> result = new ArrayList<WootCharacter>();
        result.add(target.get(prevPos));

        for (int i = prevPos + 1; i < nextPos; i++) {
            WootCharacter c = target.get(i);

            if (c.getDegree() == minDegree) {
                result.add(c);
            }
        }

        result.add(target.get(nextPos));
        return result;
    }

    /**
     * @return The type of this operation.
     */
    @Override
    public WootOpType getType() {
        return type;
    }

    /**
     * @return The character manipulated by this operation.
     */
    @Override
    public WootCharacter getChar() {
        return character;
    }

    public WootId getPreviousChar() {
        return previous;
    }

    public WootId getNextChar() {
        return next;
    }

    private int compare(WootCharacter c1, WootCharacter c2) {
        WootId cid1 = c1.getId();
        WootId cid2 = c2.getId();

        if (cid1.getSiteId() == SPECIAL) {
            if (cid1.getClock() == START) return -1;
            else return 1;
        }

        if (cid2.getSiteId() == SPECIAL) {
            if (cid2.getClock() == START) return 1;
            else return -1;
        }

        if (cid1.getSiteId() == cid2.getSiteId()) {
            // TODO: hmm....
            return (int) (cid1.getClock() - cid2.getClock());
        }

        return cid1.getSiteId() - cid2.getSiteId();
    }
}
