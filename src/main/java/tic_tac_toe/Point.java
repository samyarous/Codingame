package tic_tac_toe;

public class Point {
    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("Point[%d, %d]", x, y);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Point){
            Point other = (Point) o;
            return other.x == this.x && other.y == this.y;
        } else {
            return false;
        }
    }
}
