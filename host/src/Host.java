import java.util.*;
import java.io.*;
import java.net.*;

public class Host {
    public static boolean useEmojis = true; // change this to toggle emoji mode

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("waiting for opponent to connect");
        Socket socket = serverSocket.accept();
        System.out.println("connected");

        BufferedReader clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter clientOut = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Display.setClientOut(clientOut);

        String role = chooseRole();
        Player attacker;
        Player defender;
        Display.toBothln(instructions());
        if (role.equals("a")) {
            attacker = new Attacker(PlayerType.HOST, input);
            defender = new Defender(PlayerType.CLIENT, clientIn);
            Display.toHostln("You are the ATTACKER.");
            Display.toClient("You are the DEFENDER.");
        } else {
            attacker = new Attacker(PlayerType.CLIENT, clientIn);
            defender = new Defender(PlayerType.HOST, input);
            Display.toClient("You are the ATTACKER.");
            Display.toHostln("You are the DEFENDER.");
        }

        Board board = Board.getInstance();
        board.setup(attacker, defender);

        while (true) {
            Display.toBothln(board.toString());
            displayHands(attacker, defender);
            attacker.takeTurn();
            board.declareControl();
            attacker.draw();

            int won = board.won();
            if (won != Constants.noWinner) {
                Display.toBothln(board.toString());
                displayWinner(won);
                break;
            }

            Display.toBothln(board.toString());
            displayHands(attacker, defender);
            defender.takeTurn();
            defender.draw();
            board.declareControl();
        }
    }

    private static String chooseRole() {
        Scanner scan = new Scanner(System.in);
        System.out.print("Which role (a/d)? ");
        String role = scan.nextLine();
        if (role.equalsIgnoreCase("a")) {
            return "a";
        } else if (role.equalsIgnoreCase("d")) {
            return "d";
        } else {
            return chooseRole();
        }
    }

    private static String instructions() {
        String str = "\nWhen playing a card, type either the name, color, or copy paste emoji of the suit followed by the number (no space).\n";
        str += "Single digit numbers must have a 0 in front of them.\n";
        str += "Suits: heart, diamond, star, clover, flower\n";
        str += "Colors: red, blue, yellow, green, pink\n";
        return str;
    }

    private static void displayWinner(int won) {
        if (won == Constants.attackerWins) {
            Display.toBoth("Attacker wins", "GAME_OVER");
        } else if (won == Constants.defenderWins) {
            Display.toBoth("Defender wins", "GAME_OVER");
        } else {
            Display.toBoth("Game not over yet");
        }
    }

    private static void displayHands(Player attacker, Player defender) {
        attacker.displayHand();
        defender.displayHand();
    }
}
