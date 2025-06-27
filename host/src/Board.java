import java.util.*;
import java.io.*;
import java.net.*;

public class Board {
    private static Board instance;
    private final Wall[] board;
    private Deck deck;
    private Discard discard;
    private int cauldronCount;

    private Board() {
        board = new Wall[Constants.numWalls];
        for (int i = 0; i < Constants.numWalls; i++) {
            board[i] = new Wall(Constants.wallLengths[i], Constants.damagedWallLengths[i], Constants.wallPatterns[i], Constants.damagedWallPatterns[i], i + 1);
        }
        deck = Deck.getInstance();
        discard = Discard.getInstance();
        cauldronCount = Constants.numCauldrons;
    }

    public static Board getInstance() {
        if (instance == null) {
            instance = new Board();
        }
        return instance;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("\n");
        str.append((Constants.cardSpace() + " ").repeat(Constants.longestWall()).substring(8));
        str.append("ATTACKER").append(" ".repeat(Constants.leftWalls[0].length()));
        str.append(" ".repeat(Constants.longestWall() * 2)).append("DECK:");
        if (deck.size() < 10) {
            str.append("0");
        }
        str.append(deck.size()).append(" ".repeat(Constants.longestWall() * 2));
        str.append(" ".repeat(Constants.rightWalls[0].length())).append("DEFENDER ");
        str.append(Constants.CAULDRON.repeat(cauldronCount));
        str.append("\n");

        for (Wall wall : board) {
            str.append(wall.toString()).append("\n");
        }

        str.append("-".repeat((Constants.cardSpace().length() + 1) * Constants.longestWall() + Constants.leftWalls[0].length() + Constants.longestWall() * 2));
        str.append("DISCARD");
        str.append("-".repeat((Constants.cardSpace().length() + 1) * Constants.longestWall() + Constants.rightWalls[0].length() + Constants.longestWall() * 2));
        str.append("\n");
        if (discard.isEmpty()) {
            str.append("\n");
        } else {
            str.append(discard.toString());
        }
        str.append("-".repeat(2 * (Constants.cardSpace().length() + 1) * Constants.longestWall()));
        str.append("-".repeat(Constants.leftWalls[0].length() + Constants.rightWalls[0].length() + Constants.longestWall() * 4));
        str.append("-------");
        return str.toString();
    }

    public void setup(Player attacker, Player defender) {
        deck.shuffle();
        for (int i = 0; i < Constants.handSize; i++) {
            attacker.draw();
            defender.draw();
        }
    }

    public boolean playCard(Card card, int wall, boolean attacker) {
        int i = board[wall - 1].playCard(card, attacker);
        if (i == -1) {
            return false;
        } else if (i != 0) {
            discard.add(new Card(Constants.colors.get(i - 1), 0));
            discard.add(new Card(Constants.colors.get(i - 1), 11));
        }
        return true;
    }

    public void retreat(int wall) {
        List<Card> cards = board[wall - 1].retreat();
        if (!cards.isEmpty()) {
            discard.addAll(cards);
            Display.toBothln(toString());
            Display.toBothln("Attacker retreated from wall " + wall + ". What a coward.");
        }
    }

    public boolean cauldron(int wall) {
        Card card = board[wall - 1].cauldron();
        if (card != null) {
            discard.add(card);
            cauldronCount--;
            Display.toBothln(toString());
            String str = "Defender used cauldron on wall " + (wall - 1) + ". " + cauldronCount + " cauldron";
            if (cauldronCount != 1) {
                str += "s";
            }
            str += " remaining.";
            Display.toBothln(str);
            return true;
        }
        return false;
    }

    public void declareControl() {
        List<Card> remainingCards = new ArrayList<>();
        for (Card card : Constants.allCards()) {
            if (!discard.contains(card) && !onBoard(card)) {
                remainingCards.add(card);
            }
        }

        for (Wall wall : board) {
            if (wall.declareControl(remainingCards)) {
                discard.addAll(wall.damage());
            }
        }
    }

    public boolean onBoard(Card card) {
        for (Wall wall : board) {
            if (wall.contains(card)) {
                return true;
            }
        }
        return false;
    }

    public int won() {
        int numDamaged = 0;
        for (Wall wall : board) {
            if (wall.isBroken()) {
                return Constants.attackerWins;
            } else if (wall.isDamaged()) {
                numDamaged++;
            }
        }
        if (numDamaged >= 4) {
            return Constants.attackerWins;
        }
        if (deck.isEmpty()) {
            return Constants.defenderWins;
        }
        if (defenderSideFull()) {
            return Constants.defenderWins;
        }
        return Constants.noWinner;
    }

    private boolean defenderSideFull() {
        for (Wall wall : board) {
            if (wall.defenderHasSpace()) {
                return false;
            }
        }
        return true;
    }

    public int getCauldronCount() {
        return cauldronCount;
    }
}