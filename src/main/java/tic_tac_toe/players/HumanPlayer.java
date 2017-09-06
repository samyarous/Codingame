package tic_tac_toe.players;

import tic_tac_toe.Game;
import tic_tac_toe.Player;
import tic_tac_toe.Point;

import java.util.Scanner;

public class HumanPlayer extends Player {
    public Point next(Game game) {
        System.out.print(String.format("Player %s Move: ", this.getSide()));
        Scanner scanner = new Scanner(System. in);
        String input = scanner. nextLine();
        String[] point = input.split(" ");
        return new Point(Integer.parseInt(point[0]), Integer.parseInt(point[1]));
    }

    @Override
    public String report() {
        return "";
    }
}