import java.util.*;
import java.io.*;
import java.net.*;

public class Host {
    public static boolean useEmojis;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        Display.toHostln("waiting for opponent to connect");
        Socket socket = serverSocket.accept();
        Display.toHostln("connected");


        BufferedReader clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter clientOut = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Display.setClientOut(clientOut); // consider 2 display objects

        useEmojis = emojiCheck(clientIn);
        String role = chooseRole();
        Player attacker;
        Player defender;

        Display.toBothln(instructions());
        if (role.equals("a")) {
            attacker = new Attacker(Player.PlayerType.HOST, input);
            defender = new Defender(Player.PlayerType.CLIENT, clientIn);
            Display.toHostln("You are the ATTACKER.");
            Display.toClient("You are the DEFENDER.");
        } else {
            attacker = new Attacker(Player.PlayerType.CLIENT, clientIn);
            defender = new Defender(Player.PlayerType.HOST, input);
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

            Display.toBothln(board.toString());
            displayHands(attacker, defender);
            if (displayWinner(board)) {
                break;
            }
            defender.takeTurn();
            defender.draw();
            board.declareControl();
        }

        serverSocket.close();
        socket.close();
    }

    private static String chooseRole() {
        Scanner scan = new Scanner(System.in);
        Display.toHost("Which role (a/d)? ");
        Display.toClient("Host is choosing role");
        String role = scan.nextLine();
        if (role.equalsIgnoreCase("a")) {
            return "a";
        } else if (role.equalsIgnoreCase("d")) {
            return "d";
        } else {
            Display.toHostln("Let's try that again.");
            return chooseRole();
        }
    }

    private static boolean emojiCheck(BufferedReader clientIn) throws IOException {
        return hostEmojiCheck() && clientEmojiCheck(clientIn);
    }

    private static boolean hostEmojiCheck() {
        Scanner scan = new Scanner(System.in);
        Display.toHostln("Here are the emojis used in the game:");
        Display.toHostln(Color.listOf(Color.ColorType.EMOJI));
        Display.toHost("Can you see them (y/n)? ");
        String input = scan.nextLine();
        if (input.equalsIgnoreCase("y")) {
            return true;
        } else if (input.equalsIgnoreCase("n")) {
            return false;
        } else {
            Display.toHostln("Let's try that again.");
            return hostEmojiCheck();
        }
    }

    private static boolean clientEmojiCheck(BufferedReader input) throws IOException {
        Display.toClient("Here are the emojis used in the game:");
        Display.toClient(Color.listOf(Color.ColorType.EMOJI));
        Display.toClient("Can you see them (y/n)? ", "GET_INPUT");
        String str = input.readLine();
        if (str.equalsIgnoreCase("y")) {
            return true;
        } else if (str.equalsIgnoreCase("n")) {
            return false;
        } else {
            Display.toClient("Let's try that again.");
            return clientEmojiCheck(input);
        }
    }

    private static String instructions() {
        StringBuilder str = new StringBuilder();
        if (useEmojis) {
            str.append("\nWhen playing a card, type either the name, color, or copy paste emoji of the suit followed by the number (no space).");
            str.append("\nNames: ").append(Color.listOf(Color.ColorType.NAME));
            str.append("\nColors: ").append(Color.listOf(Color.ColorType.COLOR));
            str.append("\nEmojis: ").append(Color.listOf(Color.ColorType.EMOJI));
        } else {
            str.append("\nWhen playing a card, type it as it is displayed by the system");
            str.append("\nSuits: ").append(Color.listOf(Color.ColorType.FRUIT));
        }
        str.append("\nSingle digit numbers must have a 0 in front of them.\n");
        return str.toString();
    }

    private static boolean displayWinner(Board board) {
        return switch(board.won()) {
            case Winner.ATTACKER -> {
                Display.toBoth("Attacker wins", "GAME_OVER");
                yield true;
            }
            case Winner.DEFENDER -> {
                Display.toBoth("Defender wins", "GAME_OVER");
                yield true;
            }
            default -> false;
        };
    }

    private static void displayHands(Player attacker, Player defender) {
        attacker.displayHand();
        defender.displayHand();
    }
}
