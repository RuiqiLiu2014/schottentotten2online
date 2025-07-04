import java.util.*;
import java.io.*;
import java.net.*;

public class Host {
    public static boolean useEmojis;
    private static Player attacker;
    private static Player defender;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
        Display.toHostln("waiting for client to connect");
        Socket socket = serverSocket.accept();
        Display.toHostln("connected\n");

        BufferedReader clientIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter clientOut = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        Display.setClientOut(clientOut); // consider 2 display objects

        useEmojis = emojiCheck(clientIn);
        Display.toBothln(instructions());

        Display.toClient("Host is choosing role");
        Role hostRole = chooseRole();
        boolean playAgain = runGame(clientIn, input, hostRole);
        while (playAgain) {
            Display.toClient("Host is choosing role");
            hostRole = chooseRole(hostRole);
            playAgain = runGame(clientIn, input, hostRole);
        }

        Display.toBothln("GAME OVER");
        Display.toBothln("THANKS FOR PLAYING", "END_PROGRAM");
        serverSocket.close();
        socket.close();
    }

    private static boolean runGame(BufferedReader clientIn, BufferedReader input, Role hostRole) throws IOException {
        if (hostRole == Role.ATTACKER) {
            attacker = new Attacker(Player.PlayerType.HOST, input);
            defender = new Defender(Player.PlayerType.CLIENT, clientIn);
            Display.toHostln("\nYou are the ATTACKER.");
            Display.toClient("\nYou are the DEFENDER.");
        } else {
            attacker = new Attacker(Player.PlayerType.CLIENT, clientIn);
            defender = new Defender(Player.PlayerType.HOST, input);
            Display.toClient("\nYou are the ATTACKER.");
            Display.toHostln("\nYou are the DEFENDER.");
        }

        Table table = Table.getInstance();
        table.setup(attacker, defender);

        while (true) {
            Display.toBothln(table.toString());
            displayHands();
            if (displayWinner(table)) {
                break;
            }
            attacker.takeTurn();
            table.declareControl();
            attacker.draw();

            Display.toBothln(table.toString());
            displayHands();
            if (displayWinner(table)) {
                break;
            }
            defender.takeTurn();
            defender.draw();
            table.declareControl();
        }

        return playAgain();
    }

    private static boolean playAgain() throws IOException {
        Scanner scan = new Scanner(System.in);
        Display.toHost("Rematch (y/n)? ");
        clearHostInput();
        String input = scan.nextLine();
        if (input.equalsIgnoreCase("y")) {
            return true;
        } else if (input.equalsIgnoreCase("n")) {
            return false;
        } else {
            Display.toHostln("Let's try that again.");
            return playAgain();
        }
    }

    private static Role chooseRole() throws IOException {
        Scanner scan = new Scanner(System.in);
        Display.toHost("Which role (attacker/defender/random)? ");
        clearHostInput();
        String role = scan.nextLine();
        if (role.equalsIgnoreCase("a") || role.equalsIgnoreCase("attacker")) {
            return Role.ATTACKER;
        } else if (role.equalsIgnoreCase("d") || role.equalsIgnoreCase("defender")) {
            return Role.DEFENDER;
        } else if (role.equalsIgnoreCase("random") || role.equalsIgnoreCase("r")) {
            int i = (int)(2 * Math.random());
            return i == 0 ? Role.ATTACKER : Role.DEFENDER;
        } else {
            Display.toHostln("Let's try that again.");
            return chooseRole();
        }
    }

    private static Role chooseRole(Role prevHostRole) throws IOException {
        Scanner scan = new Scanner(System.in);
        Display.toHost("Which role (attacker/defender/random/swap)? ");
        clearHostInput();
        String role = scan.nextLine();
        if (role.equalsIgnoreCase("a") || role.equalsIgnoreCase("attacker")) {
            return Role.ATTACKER;
        } else if (role.equalsIgnoreCase("d") || role.equalsIgnoreCase("defender")) {
            return Role.DEFENDER;
        } else if (role.equalsIgnoreCase("random") || role.equalsIgnoreCase("r")) {
            int i = (int)(2 * Math.random());
            return i == 0 ? Role.ATTACKER : Role.DEFENDER;
        } else if (role.equalsIgnoreCase("swap") || role.equalsIgnoreCase("s")) {
            return prevHostRole.other();
        } else {
            Display.toHostln("Let's try that again.");
            return chooseRole(prevHostRole);
        }
    }

    private static boolean emojiCheck(BufferedReader clientIn) throws IOException {
        return hostEmojiCheck() && clientEmojiCheck(clientIn);
    }

    private static boolean hostEmojiCheck() throws IOException {
        Scanner scan = new Scanner(System.in);
        Display.toClient("Checking host emojis");
        Display.toHostln("Here are the emojis used in the game:");
        Display.toHostln(Color.listOf(Color.ColorType.EMOJI));
        Display.toHost("Can you see them (y/n)? ");
        clearHostInput();
        String input = scan.nextLine().trim();
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
        Display.toHostln("Checking client emojis");
        Display.toClient("Here are the emojis used in the game:");
        Display.toClient(Color.listOf(Color.ColorType.EMOJI));
        Display.toClient("Can you see them (y/n)? ", "GET_INPUT");
        String str = input.readLine().trim();
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

    private static boolean displayWinner(Table table) {
        return switch(table.won()) {
            case Winner.ATTACKER -> {
                attacker.displayln("\nYOU WIN\n");
                defender.displayln("\nYOU LOSE\n");
                yield true;
            }
            case Winner.DEFENDER -> {
                defender.displayln("\nYOU WIN\n");
                attacker.displayln("\nYOU LOSE\n");
                yield true;
            }
            default -> false;
        };
    }

    public static void displayHands() {
        attacker.displayHand();
        defender.displayHand();
    }

    private enum Role {
        ATTACKER, DEFENDER;

        public Role other() {
            return this == ATTACKER ? DEFENDER : ATTACKER;
        }
    }

    private static void clearHostInput() throws IOException {
        while (System.in.available() > 0) {
            System.in.read();
        }
    }
}
