package tic_tac_toe;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import tic_tac_toe.Game.Side;
import utils.MetricRegistry;

public class Simulator {

    private static MetricRegistry metricRegistry = MetricRegistry.getInstance();

    private Player player1;
    private Player player2;
    private boolean player_1_first = false;

    public Player getPlayerX() {
        return player_1_first ? player1 : player2;
    }

    @Inject
    public void setPlayer1(@Named("player_1") Player player1) {
        this.player1 = player1;
    }

    public Player getPlayerO() {
        return player_1_first ? player2 : player1;
    }

    @Inject
    public void setPlayer2(@Named("player_2") Player player2) {
        this.player2 = player2;
    }

    public SimulationResult simulate(int gameCount, boolean drawFirst){
        SimulationResult result = new SimulationResult();
        for (int i=0; i < gameCount; i++){
            if( (i+1) % Math.ceil(gameCount / 10) == 0) {
                System.out.print(Math.round((i+1) * 100 / gameCount));
                System.out.print(",");
            }
            getPlayerX().setSide(Side.X);
            getPlayerO().setSide(Side.O);
            Game game = new Game();
            if (drawFirst)
                game.draw();
            while(game.getGameOutcome() == Game.Outcome.UNDETERMINED){
                Player currentPlayer = game.getCurrentSide() == Game.Side.X ? getPlayerX() : getPlayerO();
                Point p = currentPlayer.next(game);
                game.playTurn(p);
                if (drawFirst) game.draw();
            }
            result.addGameOutcome(game.getGameOutcome(), player_1_first);
            player_1_first = !player_1_first;
        }

        result.addMetricReport(1, player1.report());
        result.addMetricReport(2, player2.report());
        return result;
    }

    public static class SimulationResult{
        private int gamesWonByPlayer1;
        private int gamesWonByPlayer2;
        private int gamesDrawn;
        private int gamesCount;
        private String player1Report;
        private String player2Report;


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


        public int getGamesWonByPlayer1() {
            return gamesWonByPlayer1;
        }

        public int getGamesWonByPlayer2() {
            return gamesWonByPlayer2;
        }

        public int getGamesDrawn() {
            return gamesDrawn;
        }

        public int getGamesCount() {
            return gamesCount;
        }

        public void addGameOutcome(Game.Outcome o, boolean player1first ){
            gamesCount ++;
            if (o == Game.Outcome.DRAW){
                gamesDrawn++;
            }
            if (o == Game.Outcome.O_WON){
                if(player1first){
                    gamesWonByPlayer2++;
                } else {
                    gamesWonByPlayer1++;
                }

            }
            if (o == Game.Outcome.X_WON){
                if(player1first){
                    gamesWonByPlayer1++;
                } else {
                    gamesWonByPlayer2++;
                }
            }

        }

        public float getPercentageWon(int player){
            if (player == 1){
                return gamesWonByPlayer1 * 100 / gamesCount;
            }
            if (player == 2){
                return gamesWonByPlayer2 * 100 / gamesCount;
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
                    this.getGamesWonByPlayer1(),
                    this.getPercentageWon(1),
                    this.getGamesWonByPlayer2(),
                    this.getPercentageWon(2),
                    this.getGamesDrawn(),
                    this.getPercentageDrawn(),
                    this.player1Report,
                    this.player2Report
            );
        }

        public void addMetricReport(int player, String report) {
            if(player == 1){
                player1Report = report;
            }
            if(player == 2){
                player2Report = report;
            }
        }
    }


}
