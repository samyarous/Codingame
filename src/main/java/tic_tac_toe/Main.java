package tic_tac_toe;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import tic_tac_toe.players.HumanPlayer;
import tic_tac_toe.players.MinMaxPlayer;

public class Main {

    private static class HumanVsHumanGame extends AbstractModule {

        protected void configure() {
            bind(Player.class).annotatedWith(Names.named("player_x")).to(MinMaxPlayer.class);
            bind(Player.class).annotatedWith(Names.named("player_o")).to(MinMaxPlayer.class);
        }
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new HumanVsHumanGame());
        Simulator simulator = injector.getInstance(Simulator.class);
        Simulator.SimulationResult result = simulator.simulate(10000);
        System.out.println(result);
    }
}
