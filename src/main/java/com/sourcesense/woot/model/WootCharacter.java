package com.sourcesense.woot.model;

public class WootCharacter {

    public static final int SPECIAL = -1;
    public static final long START = -1L;
    public static final long END = -2L;

    private WootId id;
    private char value;
    private int degree;
    private boolean visible;

    public static WootCharacter createBeginning() {
        WootId beginnerId = new WootId(SPECIAL, START);
        return new WootCharacter(beginnerId, '[', 0, false);
    }

    public static WootCharacter createEnd() {
        WootId endId = new WootId(SPECIAL, END);
        return new WootCharacter(endId, ']', 0, false);
    }

    public static WootCharacter createCharacter(int siteId, long clock,
                                                char value, int degree,
                                                boolean visible) {
        return new WootCharacter(new WootId(siteId, clock), value, degree, visible);
    }

    public WootCharacter(WootId id, char value, int degree, boolean visible) {
        this.id = id;
        this.value = value;
        this.degree = degree;
        this.visible = visible;
    }

    public WootId getId() {
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

    public int getDegree() {
        return degree;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null || !(o instanceof WootCharacter)) return false;

        WootCharacter c = (WootCharacter) o;

        return id.equals(c.getId()) && (value == c.getValue());
    }
}
