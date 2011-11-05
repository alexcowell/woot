package com.sourcesense.woot.operation;

import com.sourcesense.woot.model.CharacterId;
import com.sourcesense.woot.model.WootCharacter;
import com.sourcesense.woot.model.WootString;

import java.util.List;

import static com.sourcesense.woot.model.WootCharacter.END;
import static com.sourcesense.woot.model.WootCharacter.SPECIAL;
import static com.sourcesense.woot.model.WootCharacter.START;

/**
 *
 */
public class WootInsert implements WootOp {

    private WootCharacter character;
    private WootOpType type;

    public WootInsert(WootCharacter character) {
        this.character = character;
        this.type = WootOpType.INSERT;
    }

    /**
     * Executes this operation to the target WootString.
     *
     * @param target The target string affected by this operation.
     */
    @Override
    public void execute(WootString target) {
        // TODO: This is really inefficient - just following spec.
        /*
        WootCharacter prev = target.get(character.getPrevious());
        WootCharacter next = target.get(character.getNext());

        int prevPos = target.getPos(prev);
        int nextPos = target.getPos(next);

        if ((nextPos - (prevPos + 1)) == 0) {
            // There are no characters between this character's previous and
            // next characters, so just insert it between them.
            target.insert(character, target.getPos(next));
        } else {
            // TODO: There are characters in the way.
            List<WootCharacter> chars = target.filterRange(prev, next);

            int i = 1;
            while ((i < chars.size() - 1) && compare(chars.get(i), character) < 0) {
                i++;
            }
        }
        */

        WootCharacter prev = target.get(character.getPrevious());
        WootCharacter next = target.get(character.getNext());
        integrateInsert(target, prev, next);
    }

    private void integrateInsert(WootString target, WootCharacter prev, WootCharacter next) {
        int prevPos = target.getPos(prev);
        int nextPos = target.getPos(next);

        if ((nextPos - (prevPos + 1)) == 0) {
            // There are no characters between this character's previous and
            // next characters, so just insert it between them.
            target.insert(character, target.getPos(next));
        } else {
            // TODO: There are characters in the way.
            List<WootCharacter> chars = target.filterRange(prev, next);

            int i = 1;
            while ((i < chars.size() - 1) && compare(chars.get(i), character) < 0) {
                i++;
            }

            integrateInsert(target, chars.get(i - 1), chars.get(i));
        }
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

    private int compare(WootCharacter c1, WootCharacter c2) {
        CharacterId cid1 = c1.getId();
        CharacterId cid2 = c2.getId();

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