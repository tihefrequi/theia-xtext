package {batch.namespace}.utils;

public enum ReturnCode {
    INITIAL(-1), OK(0), WARNING(10), ERROR(90);

    private int level;

    ReturnCode(int level) {
        this.level = level;
    }

    /**
     * @return the level
     */
    public int getLevel() {
        return level;
    }

    boolean isHigherLevel(ReturnCode rc) {
        return rc.getLevel() > this.level;
    }
}
