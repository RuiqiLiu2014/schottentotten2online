import java.util.*;
import java.io.*;
import java.net.*;

public class Defender extends Player {
    private boolean usedCauldron;

    public Defender(PlayerType type, BufferedReader input) {
        super(type, input);
        usedCauldron = false;
    }

    public boolean playCard() throws IOException {
        Card card = chooseCard();
        if (card == null) {
            return false;
        }

        int wall = chooseWall();
        if (wall == 0) {
            return false;
        }

        if (Board.getInstance().playCard(card, wall, false)) {
            hand.remove(card);
            return true;
        }
        return false;
    }

    private Card chooseCard() throws IOException {
        if (Board.getInstance().getCauldronCount() > 0 && !usedCauldron) {
            display("Which card (c for cauldron)? ", "GET_INPUT");
        } else {
            display("Which card? ", "GET_INPUT");
        }
        String c = input.readLine();
        if (c.equalsIgnoreCase("c") && !usedCauldron) {
            cauldron();
            return null;
        } else if (c.equalsIgnoreCase("c")) {
            displayln("you already tried that this turn");
            displayln("you cheater");
            return null;
        }

        if (!Card.isValid(c)) {
            displayln("invalid move");
            displayln("your opponent smacks you");
            return null;
        }

        Card card = new Card(c);
        if (!hand.contains(card)) {
            displayln("you don't have that card");
            displayln("you clearly need glasses");
            return null;
        }

        usedCauldron = false;
        return card;
    }

    private void cauldron() throws IOException {
        if (Board.getInstance().getCauldronCount() > 0) {
            int wall = chooseWall();
            if (wall != 0) {
                if (Board.getInstance().cauldron(wall)) {
                    usedCauldron = true;
                } else {
                    displayln("nothing to cauldron");
                    displayln("thanks for watering the plants with hot oil i guess");
                    displayln("jk have your cauldron back");
                }
            }
        } else {
            displayln("you have no more cauldrons");
            displayln("cry about it");
        }
    }
}
