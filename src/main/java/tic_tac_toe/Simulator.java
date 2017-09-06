package tic_tac_toe;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tic_tac_toe.Game.Side;
import utils.MetricRegistry;

public class Simulator {

    private static MetricRegistry metricRegistry = MetricRegistry.getInstance();

    private Player playerX;
    private Player playerO;

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

    public SimulationResult simulate(int gameCount){
        SimulationResult result = new SimulationResult();
        for (int i=0; i < gameCount; i++){
            if( i % Math.ceil(gameCount / 100) == 0) {
                System.out.println(Math.round(i * 100 / gameCount));
            }
            Game game = new Game();
            if (i == 0)
                game.draw();
            while(game.getGameOutcome() == Game.Outcome.UNDETERMINED){
                Player currentPlayer = game.getCurrentSide() == Game.Side.X ? playerX : playerO;
                Point p = currentPlayer.next(game);
                game.playTurn(p);
                if (i == 0) game.draw();
            }

            result.addGameOutcome(game.getGameOutcome());
        }

        result.addMetricReport(Side.X, playerX.report());
        result.addMetricReport(Side.O, playerO.report());
        return result;
    }

    public static class SimulationResult{
        private int gamesWonByX;
        private int gamesWonByO;
        private int gamesDrawn;
        private int gamesCount;
        private String playerXReport;
        private String playerOReport;


        public double getAvgTime(){
            return metricRegistry.getTimer("computeBestAction").getAvgTime();
        }

        public double getMinTime(){
            return metricRegistry.getTimer("computeBestAction").getMinTime();
        }

        public double getMaxTime(){
            return metricRegistry.getTimer("computeBestAction").getMaxTime();
        }

        public double getOperationCount(){
            return metricRegistry.getCounter("computeBestAction").getCount();
        }


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

        @Override
        public String toString() {
            return String.format(
                    "Result: G: %d; X: %d (%.2f%%); O: %d (%.2f%%); D: %d (%.2f%%)\n"
                      + "PlayerX: %s\n"
                      + "PlayerO: %s",
                    this.getGamesCount(),
                    this.getGamesWonByX(),
                    this.getPercentageWon(Game.Side.X),
                    this.getGamesWonByO(),
                    this.getPercentageWon(Game.Side.O),
                    this.getGamesDrawn(),
                    this.getPercentageDrawn(),
                    this.playerXReport,
                    this.playerOReport
            );
        }

        public void addMetricReport(Side side, String report) {
            if(side == Side.X){
                playerXReport = report;
            }
            if(side == Side.O){
                playerOReport = report;
            }
        }
    }


}
