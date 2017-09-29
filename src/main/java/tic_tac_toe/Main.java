package tic_tac_toe;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tic_tac_toe.Simulator.SimulationResult;
import tic_tac_toe.players.AlphaBetaPlayer;
import tic_tac_toe.players.MiniMaxPlayer;
import tic_tac_toe.players.NegaMaxPlayer;
import tic_tac_toe.players.NegaMaxAlphaBetaPlayer;

public class Main {

    private static class AIInjectorGame extends AbstractModule {

        Class<? extends Player> player_1;
        Class<? extends Player> player_2;

        public AIInjectorGame(Class<? extends Player> player_1, Class<? extends Player> player_2) {
            this.player_1 = player_1;
            this.player_2 = player_2;
        }

        protected void configure() {
            bind(Player.class).annotatedWith(Names.named("player_1")).to(player_1);
            bind(Player.class).annotatedWith(Names.named("player_2")).to(player_2);
        }
    }

    public static void main(String[] args) {
        List<Class<? extends Player>> all_players = new ArrayList<>();
        all_players.add(AlphaBetaPlayer.class);
        all_players.add(MiniMaxPlayer.class);
        all_players.add(NegaMaxAlphaBetaPlayer.class);
        all_players.add(NegaMaxPlayer.class);
        Map<Class<? extends Player>, Double> lost_rates = new HashMap<>();
        Map<Class<? extends Player>, Double> win_rates = new HashMap<>();
        Map<Class<? extends Player>, Double> draw_rates = new HashMap<>();
        int wonByO = 0;
        int wonByX = 0;
        for (Class<? extends Player> p1: all_players){
            for (Class<? extends Player> p2: all_players){
                /*if(!(p1 == NegaMaxAlphaBetaPlayer.class && p2 == NegaMaxPlayer.class)){
                    continue;
                }*/
                Injector injector = Guice.createInjector(new AIInjectorGame(p1, p2));
                Simulator simulator = injector.getInstance(Simulator.class);
                System.out.println("\n" + p1.getSimpleName() + " vs " + p2.getSimpleName());
                SimulationResult r = simulator.simulate(100, false);
                win_rates.put(p1, win_rates.getOrDefault(p1, 0.0) + r.getGamesWonByPlayer1());
                win_rates.put(p2, win_rates.getOrDefault(p2, 0.0) + r.getGamesWonByPlayer2());
                lost_rates.put(p1, lost_rates.getOrDefault(p1, 0.0) + r.getGamesWonByPlayer2());
                lost_rates.put(p2, lost_rates.getOrDefault(p2, 0.0) + r.getGamesWonByPlayer1());
                draw_rates.put(p1, draw_rates.getOrDefault(p1, 0.0) + r.getGamesDrawn());
                draw_rates.put(p2, draw_rates.getOrDefault(p2, 0.0) + r.getGamesDrawn());
            }
        }
        for (Class<? extends Player> p: all_players){
            System.out.println(
              "\n" + p.getSimpleName() + ": \n"
                + " Won: " + win_rates.getOrDefault(p, 0.0) + "\n"
                + " Lost: " + lost_rates.getOrDefault(p, 0.0) + "\n"
                + " Draw: " + draw_rates.getOrDefault(p, 0.0)
            );
        }



    }
}
