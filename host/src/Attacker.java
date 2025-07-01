import java.util.*;
import java.io.*;
import java.net.*;

public class Attacker extends Player {
    public Attacker(PlayerType type, BufferedReader input) {
        super(type, input);
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

        if (Board.getInstance().playCard(card, wall, true) == Played.SUCCEEDED) {
            hand.remove(card);
            return true;
        }
        return false;
    }

    private Card chooseCard() throws IOException {
        clearInput();
        toOpponent("Opponent is thinking...");
        display("Which card (r for retreat)? ", "GET_INPUT");
        String c = input.readLine();
        if (c.equalsIgnoreCase("r")) {
            retreat();
            return null;
        }

        if (!Card.isValid(c)) {
            displayln("invalid move");
            displayln("your opponent smacks you");
            toOpponent("your opponent attempted an invalid move");
            toOpponent("you smack them");
            return null;
        }

        Card card = new Card(c);
        if (!hand.contains(card)) {
            displayln("you don't have that card");
            displayln("you clearly need glasses");
            toOpponent("your opponent tries to play a card they don't have");
            toOpponent("take them to get glasses after the game");
            return null;
        }

        return card;
    }

    private void retreat() throws IOException {
        int wall = chooseWall();
        if (wall != 0) {
            Board.getInstance().retreat(wall);
        }
    }
}
