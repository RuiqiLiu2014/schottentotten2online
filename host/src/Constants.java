import java.util.*;
import java.io.*;
import java.net.*;

public class Constants {
    // change all static finals to all caps
    public static final Color[] colors = Color.values();
    public static final List<Integer> values = List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);

    public static final int handSize = 6;
    public static final int numCauldrons = 3;

    public static final String CAULDRON = "\uD83E\uDDC9";
    public static final String[] leftWalls = {"||", "| ", "  "};
    public static final String[] rightWalls = {"||", " |", "  "};

    public static final int[] wallLengths = {3, 4, 3, 2, 3, 4, 3};
    public static final int[] damagedWallLengths = {3, 2, 3, 4, 3, 2, 3};
    public static final WallPattern[] wallPatterns = {WallPattern.PLUS, WallPattern.NONE, WallPattern.NONE, WallPattern.NONE, WallPattern.NONE, WallPattern.NONE, WallPattern.MINUS};
    public static final WallPattern[] damagedWallPatterns = {WallPattern.RUN, WallPattern.EQUALS, WallPattern.COLOR, WallPattern.MINUS, WallPattern.COLOR, WallPattern.EQUALS, WallPattern.RUN};

    public static final Set<Card> allCards;
    public static final int longestWall;
    public static final String cardSpace;
    public static final int numWalls;

    static {
        allCards = new TreeSet<>();
        for (Color color : colors) {
            for (int value : values) {
                allCards.add(new Card(color, value));
            }
        }

        int max = 0;
        for (int wall : wallLengths) {
            if (wall > max) {
                max = wall;
            }
        }
        for (int wall : damagedWallLengths) {
            if (wall > max) {
                max = wall;
            }
        }
        longestWall = max;

        cardSpace = Host.useEmojis ? "    " : "       ";
        numWalls = wallLengths.length;
    }
}