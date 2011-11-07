package com.sourcesense.woot.model;

public class WootId {

    private int siteId;
    private long clock;

    public WootId(int siteId, long clock) {
        this.siteId = siteId;
        this.clock = clock;
    }

    public int getSiteId() {
        return siteId;
    }

    public long getClock() {
        return clock;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof WootId)) return false;

        WootId charId = (WootId) o;
        return (siteId == charId.getSiteId()) && (clock == charId.getClock());
    }

    @Override
    public String toString() {
        return siteId + ":" + clock;
    }
}
