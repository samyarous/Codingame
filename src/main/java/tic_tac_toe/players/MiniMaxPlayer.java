package tic_tac_toe.players;

import java.util.Random;
import tic_tac_toe.Constants;
import tic_tac_toe.Game;
import tic_tac_toe.Game.Side;
import tic_tac_toe.Player;
import tic_tac_toe.Point;
import algorithms.MiniMaxAlgorithm;

import java.util.ArrayList;
import java.util.List;
import tic_tac_toe.players.AlphaBetaPlayer.PossibleAction;

import static tic_tac_toe.Game.Side.O;
import static tic_tac_toe.Game.Side.X;

public class MiniMaxPlayer extends Player {

    public static class PossibleAction implements MiniMaxAlgorithm.IAction<GameState> {

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

    public class GameState implements MiniMaxAlgorithm.INode<PossibleAction> {

        Game game;
        Game.Side playerSide;
        private final Random random = new Random();

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
            //result.sort((MiniMaxPlayer.PossibleAction a, MiniMaxPlayer.PossibleAction b) -> random.nextInt());
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
                    return playerSide == X ? 150 : -150;
                case O_WON:
                    return playerSide == O ? 150 : -150;
                case DRAW:
                    return 0;
            }

            int[] columnsScore = new int[game.BOARD_SIZE];
            int[] rowsScore    = new int[game.BOARD_SIZE];
            int[] diagonalsScore   = new int[2];

            for (int x=0; x < game.BOARD_SIZE; x++){
                for (int y=0; y < game.BOARD_SIZE; y++){
                    int cellScore = 0;
                    if(game.getCell(x, y) == playerSide){
                        cellScore = 1;
                    } else if (game.getCell(x, y) == playerSide.getOther()){
                        cellScore = -1;
                    }
                    // compute each columns score
                    columnsScore[x] += cellScore;
                    rowsScore[y] += cellScore;
                    if(x == y) {
                        diagonalsScore[0] += cellScore;
                    }
                    if(x + y == game.BOARD_SIZE - 1) {
                        diagonalsScore[1] += cellScore;
                    }
                }
            }

            double bestCellScore = 0;

            for (int x=0; x < game.BOARD_SIZE; x++) {
                for (int y = 0; y < game.BOARD_SIZE; y++) {

                    if(game.getCell(x, y) == Side.NEUTRAL){
                        double cellScore = 0;
                        cellScore += Math.pow(5, Math.abs(columnsScore[x])) * columnsScore[x];
                        cellScore += Math.pow(5, Math.abs(rowsScore[y])) * rowsScore[y];
                        if(x == y) {
                            cellScore += Math.pow(5, Math.abs(diagonalsScore[0])) * diagonalsScore[0];
                        }
                        if(x + y == game.BOARD_SIZE - 1) {
                            cellScore += Math.pow(5, Math.abs(diagonalsScore[1])) * diagonalsScore[1];
                        }

                        bestCellScore = Math.max(bestCellScore, cellScore);
                    }
                }
            }

            return bestCellScore;
        }

        @Override
        public String toString() {
            return game.toString();
        }

        @Override
        public int hashCode() {
            return game.hashCode() +  (this.playerSide == X ? 1 << 20 : 0);
        }

        @Override
        public boolean equals(Object o) {
            return o.hashCode() == this.hashCode();
        }
    }

    MiniMaxAlgorithm<GameState, PossibleAction> algorithm = new MiniMaxAlgorithm<>(Constants.USE_CACHING, Constants.MAX_DEPTH);

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
