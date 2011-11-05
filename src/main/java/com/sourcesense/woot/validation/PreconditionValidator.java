package com.sourcesense.woot.validation;

import com.sourcesense.woot.model.WootCharacter;
import com.sourcesense.woot.model.WootString;
import com.sourcesense.woot.operation.WootOp;

import static com.sourcesense.woot.model.WootCharacter.SPECIAL;

/**
 * Contains checks to validate the preconditions of an operation.
 */
public class PreconditionValidator {

    /**
     * Checks that the given operation is executable on the specified
     * WootString.
     *
     * The preconditions for a delete operation are that the target character
     * exists in the specified string and it is not a special character.
     *
     * The precondition for an insert operation is that both the previous and
     * next characters of the character to insert exist in the specified string.
     *
     * @param op The operation to validate.
     * @param string The WootString to validate the operation against.
     * @return True if the operation satisfies the preconditions. False otherwise.
     */
    public boolean isExecutable(WootOp op, WootString string) {
        WootCharacter c = op.getChar();

        switch (op.getType()) {
            case DELETE:
                return (c.getId().getSiteId() != SPECIAL) && string.contains(c);
            case INSERT:
                return string.contains(c.getPrevious())
                        && string.contains(c.getNext());
        }

        return false;
    }
}
