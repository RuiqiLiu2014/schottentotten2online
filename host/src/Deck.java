import java.util.*;

public class Deck {
    private static Deck instance;
    private Stack<Card> deck;

    private Deck() {
        deck = new Stack<>();
        deck.addAll(Constants.ALL_CARDS);
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

    public void reset() {
        deck = new Stack<>();
        deck.addAll(Constants.ALL_CARDS);
        shuffle();
    }
}
