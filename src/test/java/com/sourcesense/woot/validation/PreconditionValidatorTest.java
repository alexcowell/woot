package com.sourcesense.woot.validation;

import com.sourcesense.woot.model.WootCharacter;
import com.sourcesense.woot.model.WootId;
import com.sourcesense.woot.model.WootString;
import com.sourcesense.woot.operation.WootDelete;
import com.sourcesense.woot.operation.WootInsert;
import com.sourcesense.woot.operation.WootOp;

import org.junit.Before;
import org.junit.Test;

import static com.sourcesense.woot.model.WootCharacter.*;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class PreconditionValidatorTest {

    private PreconditionValidator validator;
    private WootString target;
    private WootOp op;

    @Before
    public void setUp() throws Exception {
        validator = new PreconditionValidator();
        target = new WootString();
    }

    @Test
    public void validDeleteOperationIsExecutable() throws Exception {
        target.insert(createCharacter(1, 0L, 'a', 1, true), 1);
        target.insert(createCharacter(1, 1L, 'x', 1, true), 2);
        target.insert(createCharacter(1, 2L, 'b', 1, true), 3);

        op = new WootDelete(createCharacter(1, 1L, 'x', 1, true));

        assertTrue(validator.isExecutable(op, target));
    }

    @Test
    public void invalidDeleteOperationIsNotExecutable() throws Exception {
        target.insert(createCharacter(1, 0L, 'a', 1, true), 1);
        target.insert(createCharacter(1, 1L, 'b', 1, true), 2);
        target.insert(createCharacter(1, 2L, 'c', 1, true), 3);

        op = new WootDelete(createCharacter(1, 1L, 'x', 1, true));
        assertFalse(validator.isExecutable(op, target));

        op = new WootDelete(createCharacter(1, 5L, 'a', 1, true));
        assertFalse(validator.isExecutable(op, target));

        op = new WootDelete(createCharacter(1, 1L, 'a', 1, false));
        assertFalse(validator.isExecutable(op, target));
    }

    @Test
    public void deleteOperationIsInvalidIfItTargetsASpecialCharacter() throws Exception {
        op = new WootDelete(WootCharacter.createBeginning());
        assertFalse(validator.isExecutable(op, target));

        op = new WootDelete(WootCharacter.createEnd());
        assertFalse(validator.isExecutable(op, target));
    }

    @Test
    public void validInsertOperationIsExecutable() throws Exception {
        target.insert(createCharacter(2, 2L, 'x', 1, true), 1);
        target.insert(createCharacter(3, 3L, 'y', 1, true), 2);

        op = createInsert(createCharacter(1, 1L, 'a', 1, true), 2, 2L, 3, 3L);
        assertTrue(validator.isExecutable(op, target));
    }

    @Test
    public void validInsertOperationOnEmptyStringIsExecutable() throws Exception {
        op = createInsert(createCharacter(1, 1L, 'a', 1, true), SPECIAL, START, SPECIAL, END);
        assertTrue(validator.isExecutable(op, target));
    }

    @Test
    public void invalidInsertOperationIsNotExecutable() throws Exception {
        op = createInsert(createCharacter(1, 1L, 'a', 1, true), SPECIAL, START, 2, 1L);
        assertFalse(validator.isExecutable(op, target));

        op = createInsert(createCharacter(1, 1L, 'a', 1, true), 2, 2L, SPECIAL, END);
        assertFalse(validator.isExecutable(op, target));

        op = createInsert(createCharacter(1, 1L, 'a', 1, true), 2, 2L, 3, 3L);
        assertFalse(validator.isExecutable(op, target));
    }

    private WootOp createInsert(WootCharacter c, int pi, long pc, int ni, long nc) {
        return new WootInsert(c, new WootId(pi, pc), new WootId(ni, nc));
    }
}
