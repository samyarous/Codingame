package tic_tac_toe;

import java.awt.*;

public abstract class Player {
    public abstract  Point next(Game board);
    public abstract String report();

    private Game.Side side;


    public Game.Side getSide() {
        return side;
    }

    public Game.Side getOtherSide() {
        return (side == Game.Side.X) ? Game.Side.O : Game.Side.X;
    }

    public void setSide(Game.Side side) {
        this.side = side;
    }

}
