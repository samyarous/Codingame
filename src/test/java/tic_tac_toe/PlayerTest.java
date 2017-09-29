package tic_tac_toe;

import java.util.HashMap;
import org.junit.Test;
import tic_tac_toe.Game.Side;
import tic_tac_toe.players.AlphaBetaPlayer;
import tic_tac_toe.players.AlphaBetaPlayer.GameState;
import tic_tac_toe.players.MiniMaxPlayer;
import tic_tac_toe.players.NegaMaxPlayer;
import tic_tac_toe.players.NegaMaxAlphaBetaPlayer;

public class PlayerTest {


  @Test
  public void playersTest(){
    findWinningMoveTest(new AlphaBetaPlayer());
    findWinningMoveTest(new MiniMaxPlayer());
    findWinningMoveTest(new NegaMaxAlphaBetaPlayer());
    findWinningMoveTest(new AlphaBetaPlayer());
    findWinningMoveTest(new NegaMaxPlayer());
  }

  @Test
  public void hashCodeTest(){
    HashMap<GameState, Boolean> before = new HashMap<>();
    for (Side i00 : new Side[]{Side.X, Side.O, Side.NEUTRAL}){
      for (Side i01 : new Side[]{Side.X, Side.O, Side.NEUTRAL}) {
        for (Side i02 : new Side[]{Side.X, Side.O, Side.NEUTRAL}) {
          for (Side i10 : new Side[]{Side.X, Side.O, Side.NEUTRAL}) {
            for (Side i11 : new Side[]{Side.X, Side.O, Side.NEUTRAL}) {
              for (Side i12 : new Side[]{Side.X, Side.O, Side.NEUTRAL}) {
                for (Side i20 : new Side[]{Side.X, Side.O, Side.NEUTRAL}) {
                  for (Side i21 : new Side[]{Side.X, Side.O, Side.NEUTRAL}) {
                    for (Side i22 : new Side[]{Side.X, Side.O, Side.NEUTRAL}) {
                      Game g = new Game();
                      g.setCell(0, 0, i00);
                      g.setCell(0, 1, i01);
                      g.setCell(0, 2, i02);
                      g.setCell(1, 0, i10);
                      g.setCell(1, 1, i11);
                      g.setCell(1, 2, i12);
                      g.setCell(2, 0, i20);
                      g.setCell(2, 1, i21);
                      g.setCell(2, 2, i22);
                      GameState gs = new GameState(g, g.getCurrentSide());
                      assert !before.containsKey(gs);

                      before.put(gs, false);
                      Game g1 = new Game();
                      g1.setCell(0, 0, i00);
                      g1.setCell(0, 1, i01);
                      g1.setCell(0, 2, i02);
                      g1.setCell(1, 0, i10);
                      g1.setCell(1, 1, i11);
                      g1.setCell(1, 2, i12);
                      g1.setCell(2, 0, i20);
                      g1.setCell(2, 1, i21);
                      g1.setCell(2, 2, i22);
                      GameState gs1 = new GameState(g1, g1.getCurrentSide());
                      assert before.containsKey(gs1);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private void findWinningMoveTest(Player currentPlayer){
    Game game;
    Point point;
    // One winning move
    game = Game.fromString("O.O|X.X|OX.");
    currentPlayer.setSide(Side.X);
    point = currentPlayer.next(game);
    game.playTurn(point);
    assert point.equals(new Point(1, 1));
    game = Game.fromString("O.O|X..|X.X");
    currentPlayer.setSide(Side.O);
    point = currentPlayer.next(game);
    game.playTurn(point);
    assert point.equals(new Point(1, 0));
    // Forced move
    game = Game.fromString("X.X|OOX|XOO");
    currentPlayer.setSide(Side.O);
    point = currentPlayer.next(game);
    game.playTurn(point);
    assert point.equals(new Point(1, 0));
    game = Game.fromString("O.O|XXO|OXX");
    currentPlayer.setSide(Side.X);
    point = currentPlayer.next(game);
    game.playTurn(point);
    assert point.equals(new Point(1, 0));
    game = Game.fromString("X.O|.X.|..O");
    currentPlayer.setSide(Side.X);
    point = currentPlayer.next(game);
    game.playTurn(point);
    assert point.equals(new Point(2, 1));
    game = Game.fromString("..X|.X.|O.O");
    currentPlayer.setSide(Side.X);
    point = currentPlayer.next(game);
    game.playTurn(point);
    assert point.equals(new Point(1, 2));
    // Forced Draw
    game = Game.fromString("XXO|.O.|X..");
    currentPlayer.setSide(Side.O);
    point = currentPlayer.next(game);
    game.playTurn(point);
    assert point.equals(new Point(0, 1));
    game = Game.fromString("..X|.X.|O.O");
    currentPlayer.setSide(Side.X);
    point = currentPlayer.next(game);
    game.playTurn(point);
    assert point.equals(new Point(1, 2));
  }


}
