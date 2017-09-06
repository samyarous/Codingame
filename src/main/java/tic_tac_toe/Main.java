package tic_tac_toe;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import tic_tac_toe.players.AlphaBetaPlayer;
import tic_tac_toe.players.HumanPlayer;
import tic_tac_toe.players.MinMaxPlayer;

public class Main {

    private static class AiVsAiGame extends AbstractModule {

        protected void configure() {
            bind(Player.class).annotatedWith(Names.named("player_1")).to(AlphaBetaPlayer.class);
            bind(Player.class).annotatedWith(Names.named("player_2")).to(MinMaxPlayer.class);
        }
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AiVsAiGame());
        Simulator simulator = injector.getInstance(Simulator.class);
        Simulator.SimulationResult result = simulator.simulate(1000);
        System.out.println(result);
    }
}
