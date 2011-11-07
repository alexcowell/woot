package com.sourcesense.woot.operation;

import com.sourcesense.woot.model.WootCharacter;
import com.sourcesense.woot.model.WootString;

/**
 *
 */
public class WootDelete implements WootOp {

    private WootCharacter character;
    private WootOpType type;

    public WootDelete(WootCharacter character) {
        this.character = character;
        this.type = WootOpType.DELETE;
    }

    /**
     * Executes a delete operation on the target WootString. The character
     * itself isn't actually delete but rather left as a tombstone. This is so
     * that future or concurrent operations using this character as a reference
     * will still be able to be executed.
     *
     * Runs in O(n).
     *
     * @param target The target string affected by this operation.
     */
    @Override
    public void execute(WootString target) {
        target.get(character.getId()).setVisible(false);
    }

    /**
     * @return The character manipulated by this operation.
     */
    @Override
    public WootCharacter getChar() {
        return character;
    }

    /**
     * @return The type of this operation.
     */
    @Override
    public WootOpType getType() {
        return type;
    }
}
