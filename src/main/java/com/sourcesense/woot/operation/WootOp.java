package com.sourcesense.woot.operation;

import com.sourcesense.woot.model.WootCharacter;
import com.sourcesense.woot.model.WootString;

/**
 * A WOOT operation.
 */
public interface WootOp {

    /**
     * Executes this operation to the target WootString.
     *
     * @param target The target string affected by this operation.
     */
    // TODO: Throw exception if operation fails?
    public void execute(WootString target);

    /**
     * @return The character manipulated by this operation.
     */
    public WootCharacter getChar();

    /**
     * @return The type of this operation.
     */
    public WootOpType getType();
}
