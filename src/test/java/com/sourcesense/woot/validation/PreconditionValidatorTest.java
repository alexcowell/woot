package com.sourcesense.woot.validation;

import com.sourcesense.woot.model.WootCharacter;
import com.sourcesense.woot.model.WootString;
import com.sourcesense.woot.operation.WootDelete;
import com.sourcesense.woot.operation.WootInsert;
import com.sourcesense.woot.operation.WootOp;

import org.junit.Before;
import org.junit.Test;

import static com.sourcesense.woot.model.WootCharacter.END;
import static com.sourcesense.woot.model.WootCharacter.SPECIAL;
import static com.sourcesense.woot.model.WootCharacter.START;
import static com.sourcesense.woot.model.WootCharacter.createCharacter;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class PreconditionValidatorTest {

    private PreconditionValidator validator;
    private WootString target;

    @Before
    public void setUp() throws Exception {
        validator = new PreconditionValidator();
        target = new WootString();
    }

    @Test
    public void validDeleteOperationIsExecutable() throws Exception {
        target.insert(createCharacter(1, 0L, true, 'a'), 1);
        target.insert(createCharacter(1, 1L, true, 'x'), 2);
        target.insert(createCharacter(1, 2L, true, 'b'), 3);

        WootOp op = new WootDelete(createCharacter(1, 1L, true, 'x'));

        assertTrue(validator.isExecutable(op, target));
    }

    @Test
    public void invalidDeleteOperationIsNotExecutable() throws Exception {
        target.insert(createCharacter(1, 0L, true, 'a'), 1);
        target.insert(createCharacter(1, 1L, true, 'b'), 2);
        target.insert(createCharacter(1, 2L, true, 'c'), 3);

        WootOp op = new WootDelete(createCharacter(1, 1L, true, 'x'));
        assertFalse(validator.isExecutable(op, target));

        op = new WootDelete(createCharacter(1, 5L, true, 'a'));
        assertFalse(validator.isExecutable(op, target));

        op = new WootDelete(createCharacter(1, 1L, false, 'a'));
        assertFalse(validator.isExecutable(op, target));
    }

    @Test
    public void deleteOperationIsInvalidIfItTargetsASpecialCharacter() throws Exception {
        WootOp op = new WootDelete(WootCharacter.createBeginning());
        assertFalse(validator.isExecutable(op, target));

        op = new WootDelete(WootCharacter.createEnd());
        assertFalse(validator.isExecutable(op, target));
    }

    @Test
    public void validInsertOperationIsExecutable() throws Exception {
        target.insert(createCharacter(2, 2L, true, 'x'), 1);
        target.insert(createCharacter(3, 3L, true, 'y'), 2);

        WootOp op = new WootInsert(createCharacter(1, 1L, true, 'a', 2, 2L, 3, 3L));
        assertTrue(validator.isExecutable(op, target));
    }

    @Test
    public void validInsertOperationOnEmptyStringIsExecutable() throws Exception {
        WootOp op = new WootInsert(createCharacter(1, 1L, true, 'a', SPECIAL, START, SPECIAL, END));
        assertTrue(validator.isExecutable(op, target));
    }

    @Test
    public void invalidInsertOperationIsNotExecutable() throws Exception {
        WootOp op = new WootInsert(createCharacter(1, 1L, true, 'a'));
        assertFalse(validator.isExecutable(op, target));
    }
}
