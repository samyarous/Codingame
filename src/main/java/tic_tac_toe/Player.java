package tic_tac_toe;

import java.awt.*;

public abstract class Player {
    public abstract  Point next(Game.Board board);
    private Game.Side side;


    public Game.Side getSide() {
        return side;
    }

    public void setSide(Game.Side side) {
        this.side = side;
    }
}
