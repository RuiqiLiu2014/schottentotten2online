import java.util.*;
import java.io.*;
import java.net.*;

public class Deck {
    private static Deck instance;
    private final Stack<Card> deck;

    private Deck() {
        deck = new Stack<>();
        deck.addAll(Constants.allCards);
    }

    public static synchronized Deck getInstance() {
        if (instance == null) {
            instance = new Deck();
        }
        return instance;
    }

    public int size() {
        return deck.size();
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public Card pop() {
        if (deck.isEmpty()) {
            return null;
        }
        return deck.pop();
    }
}
