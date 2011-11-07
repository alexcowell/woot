package com.sourcesense.woot.client;

import com.sourcesense.woot.model.WootCharacter;
import com.sourcesense.woot.model.WootString;
import com.sourcesense.woot.operation.WootInsert;
import com.sourcesense.woot.operation.WootOp;
import com.sourcesense.woot.validation.PreconditionValidator;

import java.util.LinkedList;
import java.util.Queue;

import static com.sourcesense.woot.model.WootCharacter.createCharacter;

/**
 *
 */
public class WootClient {

    private int id;
    private long clock;
    private WootString string;
    private Queue<WootOp> queue;
    private PreconditionValidator validator;

    public WootClient(int id) {
        this.id = id;
        this.clock = 0L;
        this.string = new WootString();
        this.queue = new LinkedList<WootOp>();
        this.validator = new PreconditionValidator();
    }

    public int getId() {
        return id;
    }

    public long getClock() {
        return clock;
    }

    public WootString getString() {
        return string;
    }

    public Queue<WootOp> getQueue() {
        return queue;
    }

    public PreconditionValidator getValidator() {
        return validator;
    }

    public WootOp ins(int pos, char c) {
        // TODO: Lots of checks!!!
        // TODO: Lots of optimization!!!
        clock++;

        WootCharacter prev = string.getVisibleCharAt(pos);

        if (prev == null) {
            prev = string.get(0);
        }

        WootCharacter next = string.getVisibleCharAt(pos + 1);

        if (next == null) {
            next = string.get(string.length() - 1);
        }

        int degree = Math.max(prev.getDegree(), next.getDegree()) + 1;
        WootCharacter newChar = createCharacter(id, clock, c, degree, true);

        WootOp op = new WootInsert(newChar, prev.getId(), next.getId());

        // Apply op locally.
        op.execute(string);

        return op;
    }
}
