package com.sourcesense.woot.model;

public class CharacterId {

    private int siteId;
    private long clock;

    public CharacterId(int siteId, long clock) {
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
        if (o == null || !(o instanceof CharacterId)) return false;

        CharacterId charId = (CharacterId) o;
        return (siteId == charId.getSiteId()) && (clock == charId.getClock());
    }
}
