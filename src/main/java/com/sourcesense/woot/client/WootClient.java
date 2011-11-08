package com.sourcesense.woot.client;

import com.sourcesense.woot.model.WootCharacter;
import com.sourcesense.woot.model.WootString;
import com.sourcesense.woot.operation.WootDelete;
import com.sourcesense.woot.operation.WootInsert;
import com.sourcesense.woot.operation.WootOp;
import com.sourcesense.woot.validation.PreconditionValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
    private List<WootClient> peers;
    private UpdateHandler updateHandler;

    public WootClient(int id) {
        this.id = id;
        this.clock = 0L;
        this.string = new WootString();
        this.queue = new ConcurrentLinkedQueue<WootOp>();
        this.validator = new PreconditionValidator();
        this.peers = new ArrayList<WootClient>();
        this.updateHandler = new UpdateHandler();
        this.updateHandler.start();
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

    public void ins(int pos, char c) {
        WootOp op = generateInsert(pos, c);

        // TODO: Broadcast operation.
        broadcast(op);
    }

    protected WootOp generateInsert(int pos, char c) {
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
        op.execute(string);

        return op;
    }

    public void del(int pos) {
        WootOp op =  generateDelete(pos);

        // TODO: Broadcast operation.

    }

    protected WootOp generateDelete(int pos) {
        // TODO: Increment clock if error occurs during generation?
        clock++;

        WootCharacter toDelete = string.getVisibleCharAt(pos);

        if (toDelete == null) {
            // TODO: Throw exception or return null?
            // This will fail validation - but will never be valid!!! So a
            // waste of space in the operation queue.
            toDelete = string.get(0);
        }

        WootOp op = new WootDelete(toDelete);
        op.execute(string);

        return op;
    }

    // These methods are just to get clients communicating without networking.
    // For spike purposes only.
    public void setPeers(List<WootClient> peers) {
        this.peers = peers;
    }

    private void broadcast(WootOp op) {
        for (WootClient client : peers) {
            client.receive(op);
        }
    }

    private void receive(WootOp op) {
        queue.add(op);
    }

    private class UpdateHandler extends Thread {

        private boolean running;

        public UpdateHandler() {
            this.running = true;
        }

        public void run() {
            while (running) {
                WootOp op = queue.poll();

                if (op != null) {
                    if (validator.isExecutable(op, string)) {
                        op.execute(string);
                    } else {
                        queue.add(op);
                    }
                }

                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    running = false;
                }
            }
        }
    }
}
