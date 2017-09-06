package tic_tac_toe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import tic_tac_toe.Game.Side;

public class GameTest {

    @Test
    public void testHashCode(){
        HashMap<Game, Boolean> before = new HashMap<>();
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
                                            assert !before.containsKey(g);

                                            before.put(g, false);
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

                                            assert before.containsKey(g1);
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

    @Test
    public void testFromString(){
        Game b;
        b = Game.fromString("...|...|...");
        assert(b.hashCode() == 0);
        assert(b.equals(Game.fromString(b.toString())));
        b = Game.fromString("...|.X.|...");
        assert(b.hashCode() == 0b000000000100000000);
        assert(b.equals(Game.fromString(b.toString())));
        b = Game.fromString("O..|.X.|...");
        assert(b.hashCode() == 0b000000000100000010);
        assert(b.equals(Game.fromString(b.toString())));
        b = Game.fromString("XOX|OXO|XOX");
        assert(b.hashCode() == 0b011001100110011001);
        assert(b.equals(Game.fromString(b.toString())));
        b = Game.fromString("XXX|O.O|..."); // one col
        assert(b.equals(Game.fromString(b.toString())));
    }

    @Test
    public void testOutcome(){
        Game b;
        b = new Game();
        assert (b.getGameOutcome() == Game.Outcome.UNDETERMINED);
        b = Game.fromString("XX.|O.O|..."); // 2 in one col
        assert (b.getGameOutcome() == Game.Outcome.UNDETERMINED);
        b = Game.fromString(".O.|OOX|..X"); // 2 in one row
        assert (b.getGameOutcome() == Game.Outcome.UNDETERMINED);
        b = Game.fromString("XO.|OX.|O.."); // 2 in first diag
        assert (b.getGameOutcome() == Game.Outcome.UNDETERMINED);
        b = Game.fromString(".XO|X..|O.."); // 2 in second diag
        assert (b.getGameOutcome() == Game.Outcome.UNDETERMINED);
        b = Game.fromString("XXX|O.O|..."); // one col
        assert (b.getGameOutcome() == Game.Outcome.X_WON);
        b = Game.fromString("XX.|OOO|X.."); // one col
        assert (b.getGameOutcome() == Game.Outcome.O_WON);
        b = Game.fromString(".OX|OOX|..X"); // one row
        assert (b.getGameOutcome() == Game.Outcome.X_WON);
        b = Game.fromString("OX.|OXX|O.."); // one row
        assert (b.getGameOutcome() == Game.Outcome.O_WON);
        b = Game.fromString("XO.|OX.|O.X"); // first diag
        assert (b.getGameOutcome() == Game.Outcome.X_WON);
        b = Game.fromString("OX.|XO.|X.O"); // first diag
        assert (b.getGameOutcome() == Game.Outcome.O_WON);
        b = Game.fromString(".OX|OX.|X.O"); // second diag
        assert (b.getGameOutcome() == Game.Outcome.X_WON);
        b = Game.fromString(".XO|XO.|O.X"); // second diag
        assert (b.getGameOutcome() == Game.Outcome.O_WON);

    }

    @Test
    public void testHash(){
        Game b;
        // testing the empty hash
        b = new Game();
        assert(b.hashCode() == 0);
        // filling the whole board as a checker
        b = Game.fromString("XOX|OXO|XOX");
        assert(b.hashCode() == 0b011001100110011001);
        // filling the whole board but leaving the middle square empty
        b.setCell(1, 1, Game.Side.NEUTRAL);
        assert(b.hashCode() == 0b011001100010011001);
        // filling the whole board but leaving one diagonal empty
        b.setCell(0, 0, Game.Side.NEUTRAL);
        b.setCell(2, 2, Game.Side.NEUTRAL);
        assert(b.hashCode() == 0b001001100010011000);
        // filling the whole board but leaving both diagonal empty
        b.setCell(0, 2, Game.Side.NEUTRAL);
        b.setCell(2, 0, Game.Side.NEUTRAL);
        assert(b.hashCode() == 0b001000100010001000);

        // chang current player and try again
        // testing the empty hash
        b = new Game();
        assert(b.hashCode() == 0);
        // filling the whole board as a checker
        b = Game.fromString("XOX|OXO|XOX");
        assert(b.hashCode() == 0b011001100110011001);
        // filling the whole board but leaving the middle square empty
        b.setCell(1, 1, Game.Side.NEUTRAL);
        assert(b.hashCode() == 0b011001100010011001);
        // filling the whole board but leaving one diagonal empty
        b.setCell(0, 0, Game.Side.NEUTRAL);
        b.setCell(2, 2, Game.Side.NEUTRAL);
        assert(b.hashCode() == 0b001001100010011000);
        // filling the whole board but leaving both diagonal empty
        b.setCell(0, 2, Game.Side.NEUTRAL);
        b.setCell(2, 0, Game.Side.NEUTRAL);
        assert(b.hashCode() == 0b001000100010001000);
    }

    @Test
    public void testEqual(){
        Game a;
        Game b;
        // testing the empty hash
        b = new Game();
        a = new Game();
        assert(b.equals(a));
        // filling the whole board as a checker
        b = Game.fromString("XOX|OXO|XOX");
        a = Game.fromString("XOX|OXO|XOX");

        assert(b.equals(a));
        // filling the whole board but leaving the middle square empty
        b.setCell(1, 1, Game.Side.NEUTRAL);
        a.setCell(1, 1, Game.Side.NEUTRAL);
        assert(b.equals(a));
        // filling the whole board but leaving one diagonal empty
        b.setCell(0, 0, Game.Side.NEUTRAL);
        b.setCell(2, 2, Game.Side.NEUTRAL);
        a.setCell(0, 0, Game.Side.NEUTRAL);
        a.setCell(2, 2, Game.Side.NEUTRAL);
        assert(b.equals(a));
        // filling the whole board but leaving both diagonal empty
        b.setCell(0, 2, Game.Side.NEUTRAL);
        b.setCell(2, 0, Game.Side.NEUTRAL);
        a.setCell(0, 2, Game.Side.NEUTRAL);
        a.setCell(2, 0, Game.Side.NEUTRAL);
        assert(b.equals(a));
        assert(b != a);
        a.setCell(2, 1, Game.Side.NEUTRAL);
        assert(b != a);
    }

    @Test
    public void testFromHash(){
        Game b;
        // testing the empty hash
        b = new Game();
        assert(b.equals(Game.fromHash(b.hashCode())));
        // filling the whole board as a checker
        b = Game.fromString("XOX|OXO|XOX");

        assert(b.equals(Game.fromHash(b.hashCode())));
        // filling the whole board but leaving the middle square empty
        b.setCell(1, 1, Game.Side.NEUTRAL);
        assert(b.equals(Game.fromHash(b.hashCode())));
        // filling the whole board but leaving one diagonal empty
        b.setCell(0, 0, Game.Side.NEUTRAL);
        b.setCell(2, 2, Game.Side.NEUTRAL);
        assert(b.equals(Game.fromHash(b.hashCode())));
        // filling the whole board but leaving both diagonal empty
        b.setCell(0, 2, Game.Side.NEUTRAL);
        b.setCell(2, 0, Game.Side.NEUTRAL);
        assert(b.equals(Game.fromHash(b.hashCode())));
    }
}
