import java.util.*;
import java.io.*;
import java.net.*;

public class Card implements Comparable<Card> {
    private final int value;
    private final Color color;

    public Card(Color color, int value) {
        this.value = value;
        this.color = color;
    }

    public Card(String name) {
        this.color = Color.convert(name.substring(0, name.length() - 2));
        this.value = Integer.parseInt(name.substring(name.length() - 2));
    }

    public String toString() {
        return value <= 9 ? color.getSymbol() + "0" + value : color.getSymbol() + value;
    }

    public int getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }

    public int compareTo(Card other) {
        if (this.color == other.color) {
            return this.value - other.value;
        }
        return this.color.compareTo(other.color);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof Card) {
            return compareTo((Card)o) == 0;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(value, color);
    }

    public static boolean isValid(String name) {
        if (name.length() <= 2) {
            return false;
        }

        Color color = Color.convert(name.substring(0, name.length() - 2));
        if (color == null) {
            return false;
        }

        int value = Integer.parseInt(name.substring(name.length() - 2));
        return value >= 0 && value <= 11;
    }
}