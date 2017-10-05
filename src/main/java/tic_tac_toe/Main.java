package tic_tac_toe;

import algorithms.AlphaBetaAlgorithm;
import algorithms.IterativeDeepeningAlgorithm;
import algorithms.MiniMaxAlgorithm;
import algorithms.NegaMaxAlgorithm;
import algorithms.NegaMaxAlphaBetaAlgorithm;
import algorithms.MTDAlgorithm;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tic_tac_toe.Simulator.SimulationResult;
import tic_tac_toe.players.AlphaBetaPlayer;
import tic_tac_toe.players.IterativeDeepeningPlayer;
import tic_tac_toe.players.MiniMaxPlayer;
import tic_tac_toe.players.NegaMaxPlayer;
import tic_tac_toe.players.NegaMaxAlphaBetaPlayer;
import tic_tac_toe.players.MTDPlayer;
import utils.MetricRegistry;

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
        List<Class<? extends Player>> allPlayers = new ArrayList<>();
        allPlayers.add(AlphaBetaPlayer.class);
        allPlayers.add(MiniMaxPlayer.class);
        allPlayers.add(NegaMaxAlphaBetaPlayer.class);
        allPlayers.add(NegaMaxPlayer.class);
        allPlayers.add(IterativeDeepeningPlayer.class);
        allPlayers.add(MTDPlayer.class);
        Map<Class<? extends Player>, Double> lost_rates = new HashMap<>();
        Map<Class<? extends Player>, Double> win_rates = new HashMap<>();
        Map<Class<? extends Player>, Double> draw_rates = new HashMap<>();
        int wonByO = 0;
        int wonByX = 0;
        for (Class<? extends Player> p1: allPlayers){
            for (Class<? extends Player> p2: allPlayers){
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
        for (Class<? extends Player> p: allPlayers){
            System.out.println(
              "\n" + p.getSimpleName() + ": \n"
                + " Won: " + win_rates.getOrDefault(p, 0.0) + "\n"
                + " Lost: " + lost_rates.getOrDefault(p, 0.0) + "\n"
                + " Draw: " + draw_rates.getOrDefault(p, 0.0)
            );
        }


        List<Class> allAlgorithms = new ArrayList<>();
        allAlgorithms.add(AlphaBetaAlgorithm.class);
        allAlgorithms.add(MiniMaxAlgorithm.class);
        allAlgorithms.add(NegaMaxAlphaBetaAlgorithm.class);
        allAlgorithms.add(NegaMaxAlgorithm.class);
        allAlgorithms.add(IterativeDeepeningAlgorithm.class);
        allAlgorithms.add(MTDAlgorithm.class);
        MetricRegistry metricRegistry = MetricRegistry.getInstance();

        for (String key: Arrays.asList("CacheHit", "CacheMiss", "computeBestAction", "computeBestActionPerNode")){
            System.out.println("\n" + key + ":");

            Class bestAlgorithm = null;
            Class worstAlgorithm = null;
            double leastValue = Double.POSITIVE_INFINITY;
            double mostValue = Double.NEGATIVE_INFINITY;
            double totalValue = 0;
            for (Class c: (allAlgorithms)){
                double value = 0;
                String prefix = c.getName();
                switch (key) {
                    case "CacheHit":
                        value = metricRegistry.getCounter(prefix + key).getCount();
                        break;
                    case "CacheMiss":
                        value = metricRegistry.getCounter(prefix + key).getCount();
                        break;
                    case "computeBestAction":
                        value = metricRegistry.getTimer(prefix + key).getAvgTime() * 100;
                        break;
                    case "computeBestActionPerNode":
                        value = metricRegistry.getTimer(prefix + key).getAvgTime() * 100;
                        break;
                }
                totalValue += value;
                if(value > mostValue){
                    mostValue = value;
                    worstAlgorithm = c;
                }
                if(value < leastValue){
                    leastValue = value;
                    bestAlgorithm = c;
                }
                System.out.println(c.getSimpleName() + ": " + value);
            }
            System.out.println("Least: " + bestAlgorithm.getSimpleName() + " At " + leastValue );
            System.out.println("Most: " + worstAlgorithm.getSimpleName() + " At " + mostValue );
            System.out.println("Average: " + (totalValue / allAlgorithms.size()));
        }
    }
}
