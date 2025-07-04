import java.io.*;

public class Attacker extends Player {
    public Attacker(PlayerType type, BufferedReader input) {
        super(type, input);
    }

    public Played playCard() throws IOException {
        Card card = chooseCard();
        if (card == null) {
            return Played.FAILED;
        } else if (card.isAction()) {
            return Played.USED_ACTION;
        }

        int wall = chooseWall();
        if (wall == 0) {
            return Played.FAILED;
        }

        Played played = Table.getInstance().playCard(card, wall, true);

        if (played == Played.SUCCEEDED) {
            hand.remove(card);
            return Played.SUCCEEDED;
        } else if (played == Played.NO_SPACE) {
            displayln("No more space");
        }
        return Played.FAILED;
    }

    private Card chooseCard() throws IOException {
        clearInput();
        display("Which card (r for retreat)? ", "GET_INPUT");
        String c = input.readLine();
        if (c.equalsIgnoreCase("r")) {
            if (retreat()) {
                return Card.ACTION;
            }
            return null;
        }

        if (!Card.isValid(c)) {
            displayln("Invalid card");
            return null;
        }

        Card card = new Card(c);
        if (!hand.contains(card)) {
            displayln("You don't have that card");
            return null;
        }

        return card;
    }

    private boolean retreat() throws IOException {
        int wall = chooseWall();
        if (wall != 0) {
            boolean retreated = Table.getInstance().retreat(wall);
            if (!retreated) {
                displayln("Nothing to retreat");
            }
            return retreated;
        }
        return false;
    }
}
