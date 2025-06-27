import java.util.*;
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345); // Replace localhost with host IP
        System.out.println("connected");
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner scan = new Scanner(System.in);

        while (true) {
            String line = in.readLine();
            if (line == null) {
                break;
            }
            line = line.replace("\\n", "\n");
            if (line.startsWith("GET_INPUT")) {
                while (System.in.available() > 0) {
                    System.in.read();
                }
                System.out.print(line.substring("GET_INPUT".length()));
                out.println(scan.nextLine());
            } else if (line.startsWith("GAME_OVER")) {
                System.out.print(line.substring("GAME_OVER".length()));
                break;
            } else if (line.startsWith("NO_LN")){
                System.out.print(line.substring("NO_LN".length()));
            } else {
                System.out.println(line);
            }
        }
    }
}
