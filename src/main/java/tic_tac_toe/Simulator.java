package tic_tac_toe;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Simulator {

    public static class SimulationResult{
        private int gamesWonByX;
        private int gamesWonByO;
        private int gamesDrawn;

        public int getGamesWonByX() {
            return gamesWonByX;
        }

        public int getGamesWonByO() {
            return gamesWonByO;
        }

        public int getGamesDrawn() {
            return gamesDrawn;
        }

        public int getGamesCount() {
            return gamesCount;
        }

        private int gamesCount;

        public void addGameOutcome(Game.Outcome o){
            gamesCount ++;
            if (o == Game.Outcome.DRAW){
                gamesDrawn++;
            }
            if (o == Game.Outcome.O_WON){
                gamesWonByO++;
            }
            if (o == Game.Outcome.X_WON){
                gamesWonByX++;
            }

        }

        public float getPercentageWon(Game.Side s){
            if (s == Game.Side.X){
                return gamesWonByX * 100 / gamesCount;
            }
            if (s == Game.Side.O){
                return gamesWonByO * 100 / gamesCount;
            }
            return 0;
        }

        public float getPercentageDrawn(){
            return gamesDrawn * 100 / gamesCount;
        }
    }

    public Player getPlayerX() {
        return playerX;
    }

    @Inject
    public void setPlayerX(@Named("player_x") Player playerX) {
        this.playerX = playerX;
        this.playerX.setSide(Game.Side.X);
    }

    public Player getPlayerO() {
        return playerO;
    }

    @Inject
    public void setPlayerO(@Named("player_o") Player playerO) {
        this.playerO = playerO;
        this.playerO.setSide(Game.Side.O);
    }


    private Player playerX;
    private Player playerO;

    public SimulationResult simulate(int gameCount){
        SimulationResult result = new SimulationResult();
        for (int i=0; i < gameCount; i++){
            Game game = new Game();
            game.draw();
            while(game.getGameOutcome() == Game.Outcome.UNDETERMINED){
                Player currentPlayer = game.getCurrentSide() == Game.Side.X ? playerX : playerO;
                Point p = currentPlayer.next(game.getBoard());
                game.playTurn(p);
                game.draw();
            }

            result.addGameOutcome(game.getGameOutcome());
        }
        return result;
    }


}
