import java.util.*;
import java.io.*;
import java.net.*;

public class Discard {
    private static Discard instance;
    private Set<Card> discard;

    private Discard() {
        discard = new TreeSet<>();
    }

    public static Discard getInstance() {
        if (instance == null) {
            instance = new Discard();
        }
        return instance;
    }

    public void add(Card card) {
        discard.add(card);
    }

    public void addAll(Collection<Card> cards) {
        discard.addAll(cards);
    }

    public boolean contains(Card card) {
        return discard.contains(card);
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        List<Card> list = List.copyOf(discard);
        for (int i = 0; i < list.size() - 1; i++) {
            str.append(list.get(i).toString()).append(" ");
            if (!list.get(i).getColor().equals(list.get(i + 1).getColor())) {
                str.append("\n");
            }
        }
        if (!list.isEmpty()) {
            str.append(list.getLast());
        }
        str.append("\n");
        return str.toString();
    }

    public boolean isEmpty() {
        return discard.isEmpty();
    }
}
