package pl.st.donuts;

public enum Direction {
    NORTH(0,  0, -1),
    EAST(1,   1, 0),
    SOUTH(2,  0, 1),
    WEST(3,  -1, 0);

    private int index;
    private int dx;
    private int dy;

    Direction(int index, int dx, int dy) {
        this.index = index;
        this.dx = dx;
        this.dy = dy;
    }

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }

    public int index() {
        return index;
    }

    public static Direction ofIndex(int index) {
        return switch(index) {
            case 0 -> Direction.SOUTH;
            case 1 -> Direction.EAST;
            case 2 -> Direction.NORTH;
            case 3 -> Direction.WEST;
            default -> throw new IllegalStateException("Unexpected value: " + index);
        };
    }

    public static Direction ofMovement(int dx, int dy) {
        if (dy < 0) return NORTH;
        if (dx > 0) return EAST;
        if (dy > 0) return SOUTH;
        if (dx < 0) return WEST;
        return null;
    }

    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case SOUTH -> NORTH;
            case WEST -> EAST;
            case EAST -> WEST;
            default -> null;
        };
    }

    public boolean isOpposite(Direction direction) {
        return direction == opposite();
    }

    public Direction clockwise() {
        return switch (this) {
            case NORTH -> EAST;
            case SOUTH -> WEST;
            case WEST -> NORTH;
            case EAST -> SOUTH;
            default -> null;
        };
    }

    public Direction counterclockwise() {
        return clockwise().opposite();
    }
}
