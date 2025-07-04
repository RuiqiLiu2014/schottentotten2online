import java.util.*;
import java.io.*;

public abstract class Player {
    protected Set<Card> hand;
    protected PlayerType playerType;
    protected BufferedReader input;

    public enum PlayerType {
        HOST, CLIENT
    }

    public Player(PlayerType type, BufferedReader input) {
        hand = new TreeSet<>();
        this.playerType = type;
        this.input = input;
    }

    public void takeTurn() throws IOException {
        toOpponent("Opponent is thinking...");
        Played played = playCard();
        while (played != Played.SUCCEEDED) {
            if (played == Played.USED_ACTION) {
                toOpponent("Opponent is thinking...");
                Host.displayHands();
            } else {
                displayHand();
            }
            played = playCard();
        }
    }

    protected int chooseWall() throws IOException {
        clearInput();
        display("Which wall (0 to cancel)? ", "GET_INPUT");
        String w = input.readLine();
        if (!isInteger(w)) {
            displayln("Invalid wall");
            return 0;
        }

        int wall = Integer.parseInt(w);
        if (wall == 0) {
            return 0;
        } else if (wall < 0 || wall > Constants.NUM_WALLS) {
            displayln("Invalid wall");
            return 0;
        }

        return wall;
    }

    private boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void draw() {
        Card card = Deck.getInstance().pop();
        if (card != null) {
            hand.add(card);
        }
    }

    public abstract Played playCard() throws IOException;

    public void displayHand() {
        StringBuilder h = new StringBuilder();
        for (Card card : hand) {
            h.append(card).append(" ");
        }
        displayln(h.toString());
    }

    public void display(String message) {
        display(message, "");
    }

    public void displayln(String message) {
        displayln(message, "");
    }

    public void display(String message, String prefix) {
        if (playerType == PlayerType.HOST) {
            Display.toHost(message);
        } else {
            Display.toClient(prefix + message.replace("\n", "\\n"));
        }
    }

    public void displayln(String message, String prefix) {
        if (playerType == PlayerType.HOST) {
            Display.toHostln(message);
        } else {
            Display.toClient(prefix + message.replace("\n", "\\n"));
        }
    }

    public void toOpponent(String message, String prefix) {
        if (playerType == PlayerType.HOST) {
            Display.toClient(message, prefix);
        } else {
            Display.toHostln(message);
        }
    }

    public void toOpponent(String message) {
        toOpponent(message, "");
    }

    protected void clearInput() throws IOException {
        while (input.ready()) {
            input.readLine();
        }
    }
}