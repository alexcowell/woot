package com.sourcesense.woot.model;

public class WootCharacter {

    public static final int SPECIAL = -1;
    public static final long START = -1L;
    public static final long END = -2L;

    private CharacterId id;
    private boolean visible;
    private char value;
    private CharacterId previous;
    private CharacterId next;

    public static WootCharacter createBeginning() {
        CharacterId beginnerId = new CharacterId(SPECIAL, START);
        return new WootCharacter(beginnerId, false, '.', null, null);
    }

    public static WootCharacter createEnd() {
        CharacterId endId = new CharacterId(SPECIAL, END);
        return new WootCharacter(endId, false, '.', null, null);
    }

    public static WootCharacter createCharacter(int siteId, long clock,
                                                boolean visible, char value) {
        return new WootCharacter(new CharacterId(siteId, clock),
                visible, value, null, null);
    }

    public static WootCharacter createCharacter(int siteId, long clock,
                                                boolean visible, char value,
                                                int prevSiteId, long prevClock,
                                                int nextSiteId, long nextClock) {
        return new WootCharacter(new CharacterId(siteId, clock),
                visible, value, new CharacterId(prevSiteId, prevClock),
                new CharacterId(nextSiteId, nextClock));
    }

    public WootCharacter(CharacterId id, boolean visible, char value,
                         CharacterId previous, CharacterId next) {
        this.id = id;
        this.visible = visible;
        this.value = value;
        this.previous = previous;
        this.next = next;
    }

    public CharacterId getId() {
        return id;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public char getValue() {
        return value;
    }

    public CharacterId getPrevious() {
        return previous;
    }

    public CharacterId getNext() {
        return next;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof WootCharacter)) return false;

        WootCharacter c = (WootCharacter) o;

        return id.equals(c.getId()) && (value == c.getValue())
                && (visible == c.isVisible());
    }
}
