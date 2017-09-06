package tic_tac_toe.players;

import algorithms.AlphaBetaPruningAlgorithm;
import tic_tac_toe.Game;
import tic_tac_toe.Player;
import tic_tac_toe.Point;
import algorithms.MiniMaxAlgorithm;

import java.util.ArrayList;
import java.util.List;

import static tic_tac_toe.Game.Side.O;
import static tic_tac_toe.Game.Side.X;

public class AlphaBetaPlayer extends Player {

  public static class PossibleAction implements AlphaBetaPruningAlgorithm.IAction<GameState> {

    Game.Side side;
    Point p;

    public PossibleAction(Point p) {
      this.p = p;
    }

    @Override
    public GameState apply(GameState node) {
      node.game.setCell(p.x, p.y, node.game.getCurrentSide());
      return node;
    }

    @Override
    public void undo(GameState node) {
      node.game.setCell(p.x, p.y, Game.Side.NEUTRAL);
    }

    @Override
    public String toString() {
      return p.toString();
    }
  }

  public class GameState implements AlphaBetaPruningAlgorithm.INode<PossibleAction> {

    Game game;
    Game.Side playerSide;

    public GameState(Game game, Game.Side playerSide) {
      this.game = game;
      this.playerSide = playerSide;
    }

    @Override
    public List<PossibleAction> getPossibleActions() {
      List<PossibleAction> result = new ArrayList<>();
      for (int x=0; x < Game.BOARD_SIZE; x++) {
        for (int y = 0; y < Game.BOARD_SIZE; y++) {
          if (game.getCell(x, y) == Game.Side.NEUTRAL){
            result.add(new PossibleAction(new Point(x, y)));
          }
        }
      }
      return result;

    }

    @Override
    public boolean isTerminal() {
      return game.getGameOutcome() != Game.Outcome.UNDETERMINED;
    }

    @Override
    public double getUtility() {
      switch (game.getGameOutcome()){
        case X_WON:
          return playerSide == X ? 100 : -100;
        case O_WON:
          return playerSide == O ? 100 : -100;
        case DRAW:
          return 0;
        default:
          return 0;
      }
    }

    @Override
    public String toString() {
      return game.toString();
    }

    @Override
    public int hashCode() {
      return game.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      return o.hashCode() == this.hashCode();
    }
  }

  AlphaBetaPruningAlgorithm<GameState, PossibleAction> algorithm = new AlphaBetaPruningAlgorithm<>(true, Integer.MAX_VALUE);

  public Point next(Game game) {
    GameState gameState = new GameState(game, this.getSide());
    PossibleAction action = algorithm.computeBestAction(gameState);
    return action.p;
  }

  @Override
  public String report() {
    return algorithm.report();
  }

}
